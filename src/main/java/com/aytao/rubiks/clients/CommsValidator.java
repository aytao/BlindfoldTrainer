package com.aytao.rubiks.clients;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.UnhandledException;

import com.aytao.rubiks.comm.Comm;
import com.aytao.rubiks.comm.CommValidity;
import com.aytao.rubiks.comm.Comm.UnbalancedBracketsException;
import com.aytao.rubiks.cube.Cube;
import com.aytao.rubiks.cube.SpeffzUtils;
import com.aytao.rubiks.cube.Move.IllegalMoveException;
import com.aytao.rubiks.threeStyle.CommParser;
import com.aytao.rubiks.threeStyle.ThreeStyle;
import com.aytao.rubiks.utils.Defines;
import com.aytao.rubiks.utils.ResourceHandler;

import au.com.bytecode.opencsv.CSVReader;
import javafx.util.Pair;

public class CommsValidator {

  private static class CommDescription {
    private final char target1;
    private final char target2;

    public CommDescription(char target1, char target2) {
      this.target1 = target1;
      this.target2 = target2;
    }
  }

  private static final char EDGE_BUFFER = ThreeStyle.EDGE_BUFFER;
  private static final char CORNER_BUFFER = ThreeStyle.CORNER_BUFFER;

  public static CommValidity[][] checkEdgeValidity(String fileName) {
    Set<Character> bufferPieceSet = SpeffzUtils.getRelatedEdgeStickersSet(EDGE_BUFFER);
    CommValidity[][] validities = new CommValidity[Defines.NUM_SPEFFZ_LETTERS][Defines.NUM_SPEFFZ_LETTERS];

    try (CSVReader reader = new CSVReader(new FileReader(ResourceHandler.getFile(fileName)))) {
      List<String[]> commStrings = reader.readAll();
      int rowIdx = 1;
      for (int i = 0; i < Defines.NUM_SPEFFZ_LETTERS; i++) {
        if (bufferPieceSet.contains((char) (i + 'a'))) {
          continue;
        }
        int colIdx = 1;
        for (int j = 0; j < Defines.NUM_SPEFFZ_LETTERS; j++) {
          if (bufferPieceSet.contains((char) (j + 'a'))) {
            continue;
          }
          validities[i][j] = getEdgeCommStringValidity(commStrings.get(colIdx)[rowIdx], (char) (i + 'a'),
              (char) (j + 'a'));
          // try {
          // Comm comm = new Comm(commStrings.get(colIdx)[rowIdx]);
          // validities[i][j] = checkEdgeComm(comm, EDGE_BUFFER, (char) (i + 'a'), (char)
          // (j + 'a'));
          // } catch (IllegalMoveException e) {
          // System.out.println("Illegal Move");
          // validities[i][j] = CommValidity.ILLEGAL_MOVE;
          // } catch (UnbalancedBracketsException e) {
          // System.out.println("Unbalanced Brackets");
          // validities[i][j] = CommValidity.UNBALANCED;
          // } catch (Exception e) {
          // System.out.printf("Error with %s comm BUFFER->%c->%c: ", "edge", (char)
          // (colIdx
          // + 'a'), (char) (rowIdx + 'a'));
          // System.out.println(commStrings.get(colIdx)[rowIdx]);
          // System.out.println(e.getMessage());
          // e.printStackTrace();
          // System.out.println("_____________________________________________");
          // }
          colIdx++;
        }
        rowIdx++;
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Error opening file '" + fileName + "'");
    }
    // return comms;
    return validities;
  }

  // TODO: Refactor into private method
  private static CommValidity getEdgeCommStringValidity(String commString, char target1, char target2) {
    if (commString.matches("(\\s)*")) {
      return null;
    }

    try {
      Comm comm = new Comm(commString);
      return checkEdgeComm(comm, EDGE_BUFFER, target1, target2);
    } catch (IllegalMoveException e) {
      return CommValidity.ILLEGAL_MOVE;
    } catch (UnbalancedBracketsException e) {
      return CommValidity.UNBALANCED;
    } catch (Exception e) {

      String errorStr = String.format("Unhandled error with %s comm BUFFER->%c->%c: %s", "edge", target1, target2,
          commString);
      throw new UnhandledException(errorStr, e);
    }
  }

  public static CommValidity checkEdgeComm(Comm comm, char buffer, char target1, char target2) {
    Cube cube = new Cube();
    cube.execute(comm.toSequence());

    char[] corners = SpeffzUtils.cornerReport(cube);
    if (!isAscendingOrder(corners)) {
      return CommValidity.DISRUPTS_OTHER_TYPE;
    }

    char[] edges = SpeffzUtils.edgeReport(cube);

    if (!otherStickersUndisturbed(edges, buffer, target1, target2)) {
      return CommValidity.DISRUPTS_SAME_TYPE;
    }

    if (!isCycle(edges, buffer, target1, target2)) {
      return CommValidity.INCORRECT_CYCLE;
    }

    return CommValidity.VALID;
  }

  public static CommValidity checkCornerComm(Comm comm, char buffer, char target1, char target2) {
    Cube cube = new Cube();
    cube.execute(comm.toSequence());

    char[] edges = SpeffzUtils.edgeReport(cube);
    if (!isAscendingOrder(edges)) {
      return CommValidity.DISRUPTS_OTHER_TYPE;
    }

    char[] corners = SpeffzUtils.cornerReport(cube);
    // TODO: DOESN'T WORK!!!! Fix Edge dependency in shared messages
    if (!otherStickersUndisturbed(corners, buffer, target1, target2)) {
      return CommValidity.DISRUPTS_SAME_TYPE;
    }

    if (!isCycle(corners, buffer, target1, target2)) {
      return CommValidity.INCORRECT_CYCLE;
    }

    return CommValidity.VALID;
  }

  private static boolean otherStickersUndisturbed(char[] report, char buffer, char target1, char target2) {
    Set<Character> ignore = SpeffzUtils.getRelatedEdgeStickersSet(buffer);
    ignore.addAll(SpeffzUtils.getRelatedEdgeStickersSet(target1));
    ignore.addAll(SpeffzUtils.getRelatedEdgeStickersSet(target2));

    for (int i = 0; i < Defines.NUM_SPEFFZ_LETTERS; i++) {
      if (ignore.contains((char) ('a' + i))) {
        continue;
      }

      if (report[i] != i + 'a') {
        return false;
      }
    }

    return true;
  }

  private static boolean isReplaced(char[] report, char source, char target) {
    return report[target - 'a'] == source;
  }

  private static boolean isCycle(char[] report, char b, char t1, char t2) {
    return isReplaced(report, b, t1) &&
        isReplaced(report, t1, t2) &&
        isReplaced(report, t2, b);
  }

  private static boolean isAscendingOrder(char[] report) {
    for (int i = 0; i < report.length; i++) {
      if (report[i] != 'a' + i) {
        return false;
      }
    }

    return true;
  }

  private static void printErrorGroup(CommValidity cv, List<CommDescription> list) {
    System.out.println(cv.name());
    System.out.println("_______________________");

    for (CommDescription commDescription : list) {
      System.out.printf("%c%c\n", Character.toUpperCase(commDescription.target1),
          Character.toUpperCase(commDescription.target2));
    }
  }

  public static void main(String[] args) {
    // char t1 = 'm';
    // char t2 = 'a';
    // Comm comm = CommParser.getEdgeComm(t1, t2);
    // System.out.println(comm);
    // System.out.println(checkEdgeComm(comm, ThreeStyle.EDGE_BUFFER, t1, t2));
    CommValidity[][] commValidities = checkEdgeValidity("Comms/UFComms.csv");

    Map<CommValidity, List<CommDescription>> map = new HashMap<>();
    for (int i = 0; i < commValidities.length; i++) {
      for (int j = 0; j < commValidities[i].length; j++) {
        if (commValidities[i][j] == null) {
          continue;
        }
        List<CommDescription> list = map.getOrDefault(commValidities[i][j], new ArrayList<>());
        list.add(new CommDescription((char) (i + 'a'), (char) (j + 'a')));
        map.put(commValidities[i][j], list);
      }
    }

    for (CommValidity cv : map.keySet()) {
      if (CommValidity.isValid(cv)) {
        continue;
      }
      printErrorGroup(cv, map.get(cv));
      System.out.println("\n\n");
    }
  }
}

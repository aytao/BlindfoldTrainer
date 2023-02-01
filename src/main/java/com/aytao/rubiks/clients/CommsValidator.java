package com.aytao.rubiks.clients;

import java.io.FileReader;
import java.util.List;
import java.util.Set;

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

public class CommsValidator {

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
          try {
            Comm comm = new Comm(commStrings.get(colIdx)[rowIdx]);
            validities[i][j] = checkEdgeComm(comm, EDGE_BUFFER, (char) (i + 'a'), (char) (j + 'a'));
          } catch (IllegalMoveException e) {
            System.out.println("Illegal Move");
            validities[i][j] = CommValidity.ILLEGAL_MOVE;
          } catch (UnbalancedBracketsException e) {
            System.out.println("Unbalanced Brackets");
            validities[i][j] = CommValidity.UNBALANCED;
          } catch (Exception e) {
            System.out.printf("Error with %s comm BUFFER->%c->%c: ", "edge", (char) (colIdx
                + 'a'), (char) (rowIdx + 'a'));
            System.out.println(commStrings.get(colIdx)[rowIdx]);
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.out.println("_____________________________________________");
          }
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

  public static void main(String[] args) {
    // char t1 = 'm';
    // char t2 = 'a';
    // Comm comm = CommParser.getEdgeComm(t1, t2);
    // System.out.println(comm);
    // System.out.println(checkEdgeComm(comm, ThreeStyle.EDGE_BUFFER, t1, t2));
    checkEdgeValidity("Comms/UFComms.csv");
  }
}

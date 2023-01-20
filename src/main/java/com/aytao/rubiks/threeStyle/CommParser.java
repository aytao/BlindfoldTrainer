package com.aytao.rubiks.threeStyle;

import java.io.FileReader;
import java.util.List;
import java.util.Set;

import com.aytao.rubiks.ResourceHandler;
import com.aytao.rubiks.comm.Comm;
import com.aytao.rubiks.cube.Sequence;
import com.aytao.rubiks.cube.SpeffzUtils;
import com.aytao.rubiks.utils.Defines;

import au.com.bytecode.opencsv.CSVReader;

public class CommParser {
  private final static Comm[][] EDGE_COMMS;
  private final static Comm[][] CORNER_COMMS;

  static {
    EDGE_COMMS = parseEdgeComms();
    CORNER_COMMS = parseCornerComms();
  }

  private static Comm[][] parseEdgeComms() {
    return parseFile("Comms/UFComms.csv", SpeffzUtils.getRelatedEdgeStickersSet(ThreeStyle.EDGE_BUFFER), "edge");
  }

  private static Comm[][] parseCornerComms() {
    return parseFile("Comms/UFRComms.csv", SpeffzUtils.getRelatedCornerStickersSet(ThreeStyle.CORNER_BUFFER), "corner");
  }

  private static Comm[][] parseFile(String fileName, Set<Character> bufferPieceSet, String type) {
    Comm[][] comms = new Comm[Defines.NUM_SPEFFZ_LETTERS][Defines.NUM_SPEFFZ_LETTERS];

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
            comms[i][j] = comm;
          } catch (IllegalArgumentException e) {
            System.out.printf("Error with %s comm BUFFER->%c->%c: ", type, (char) (colIdx + 'a'),
                (char) (rowIdx + 'a'));
            System.out.println(commStrings.get(colIdx)[rowIdx]);
            System.out.println(e.getMessage());
            System.out.println("_____________________________________________");
          } catch (Exception e) {
            e.printStackTrace();
          }
          colIdx++;
        }
        rowIdx++;
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Error opening file '" + fileName + "'");
    }
    return comms;
  }

  public static void validateTarget(char target) {
    if (!SpeffzUtils.isValidSpeffzLetter(target)) {
      throw new IllegalArgumentException("Character " + target + " is not a valid Speffz letter");
    }
  }

  public static Comm getComm(Comm[][] comms, char target1, char target2) {
    validateTarget(target1);
    validateTarget(target2);

    return comms[target1 - 'a'][target2 - 'a'];
  }

  public static Comm getEdgeComm(char target1, char target2) {
    return getComm(EDGE_COMMS, target1, target2);
  }

  public static Comm getCornerComm(char target1, char target2) {
    return getComm(CORNER_COMMS, target1, target2);
  }

  public static void main(String args[]) {
    System.out.println(Sequence.toString(getEdgeComm('l', 'e').toSequence()));
    System.out.println(Sequence.toString(getCornerComm('a', 'g').toSequence()));
  }
}

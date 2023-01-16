package com.aytao.rubiks.threeStyle;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.aytao.rubiks.ResourceHandler;
import com.aytao.rubiks.comm.Comm;
import com.aytao.rubiks.cube.Sequence;
import com.aytao.rubiks.utils.Defines;

import au.com.bytecode.opencsv.CSVReader;

public class CommHandler {
  private final static Comm[][] EDGE_COMMS;

  static {
    EDGE_COMMS = parseFile("Comms/UFComms.csv");
  }

  private static Comm[][] parseFile(String fileName) {
    Comm[][] comms = new Comm[Defines.NUM_SPEFFZ_LETTERS][Defines.NUM_SPEFFZ_LETTERS];
    try (CSVReader reader = new CSVReader(new FileReader(ResourceHandler.getFile("Comms/UFComms.csv")))) {
      List<String[]> commStrings = reader.readAll();
      int rowIdx = 1;
      for (int i = 0; i < Defines.NUM_SPEFFZ_LETTERS - 2; i++) {
        if (i + 'a' == 'c' || i + 'a' == 'i') {
          continue;
        }
        int colIdx = 1;
        for (int j = 0; j < Defines.NUM_SPEFFZ_LETTERS - 2; j++) {
          if (j + 'a' == 'c' || j + 'a' == 'i') {
            // TODO: Related sticker from buffer
            continue;
          }
          try {
            Comm comm = new Comm(commStrings.get(colIdx)[rowIdx]);
            comms[i][j] = comm;
          } catch (IllegalArgumentException e) {
            System.out.println("Error with comm " + rowIdx + " " + colIdx);
            System.out.println(commStrings.get(colIdx)[rowIdx]);
            System.out.println(e);
          } catch (Exception e) {
            e.printStackTrace();
          }
          colIdx++;
        }
        rowIdx++;
      }
    } catch (Exception e) {
      e.printStackTrace();
      // throw new RuntimeException("Error opening file '" + fileName + "'");
    }
    return comms;
  }

  public static void main(String args[]) {
    System.out.println(Sequence.toString(EDGE_COMMS[0][3].toSequence()));
    System.out.println(Sequence.toString(EDGE_COMMS[0][5].toSequence()));
  }
}

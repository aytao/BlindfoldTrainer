/* *****************************************************************************
 *  Author:    Andrew Tao
 *
 *  Description:  Has methods that take a Cube object as input and returns a
 *                character array representing the state and position of
 *                each sticker on the Cube.
 *
 **************************************************************************** */

package com.aytao.rubiks.trace.reporting;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import com.aytao.rubiks.ResourceHandler;
import com.aytao.rubiks.cube.Cube;
import com.aytao.rubiks.cube.CubeColor;
import com.aytao.rubiks.cube.Move;

public class StickerReport {

  private static final int NUM_EDGE_STICKERS = 24;
  private static final int NUM_CORNER_STICKERS = 24;
  private static final int[][] edgeCoords;
  private static final HashSet<HashSet<Character>> edgePieces;

  private static final int[][] cornerCoords;
  private static final HashSet<HashSet<Character>> cornerPieces;

  static {
    edgeCoords = getCoords("Labels/EdgeLabels.txt", NUM_EDGE_STICKERS);
    cornerCoords = getCoords("Labels/CornerLabels.txt", NUM_CORNER_STICKERS);
    edgePieces = getPieces("Connections/EdgeConnections.txt");
    cornerPieces = getPieces("Connections/CornerConnections.txt");
  }

  /*
   * Opens the csv file labelsFile which should have numStickers number of lines,
   * and uses the information to return a 2d mapping of sticker names to
   * coordinates
   */
  private static int[][] getCoords(String labelsFileName, int numStickers) {
    int[][] coords = new int[numStickers][];
    try (Scanner in = new Scanner(ResourceHandler.getFile(labelsFileName), "utf-8")) {
      while (in.hasNext()) {
        String line = in.nextLine();
        String[] args = line.split(",");

        assert (args.length == 4);

        char c = args[0].charAt(0);
        int i = Integer.parseInt(args[1]);
        int j = Integer.parseInt(args[2]);
        int k = Integer.parseInt(args[3]);
        coords[c - 'a'] = new int[] { i, j, k };
      }
    } catch (Exception e) {
      throw new RuntimeException("Error opening file '" + labelsFileName + "'");
    }

    return coords;
  }

  /*
   * Opens the csv file connections. Uses the information in the file to create
   * a HashSet of HashSets, where the inner most HashSet is a set of characters
   * that represents stickers of the same piece, and the outer HashSet is a set
   * of all pieces
   */
  private static HashSet<HashSet<Character>> getPieces(String connectionsFileName) {
    HashSet<HashSet<Character>> piecesSet = new HashSet<>();
    try (Scanner in = new Scanner(ResourceHandler.getFile(connectionsFileName), "utf-8")) {
      while (in.hasNext()) {
        String line = in.nextLine();
        String[] stickers = line.split(",");
        HashSet<Character> piece = new HashSet<>();
        for (String s : stickers) {
          char sticker = s.charAt(0);
          piece.add(sticker);
        }
        piecesSet.add(piece);
      }
    } catch (Exception e) {
      throw new RuntimeException("Error opening file '" + connectionsFileName + "'");
    }

    return piecesSet;
  }

  /*
   * Given a Cube object cube, returns a char[] that represents the current state
   * of
   * each edge-piece sticker on the cube. The array is indexed with 'a' at
   * position 0,
   * and the characters in the array represent the current sticker that is in that
   * position.
   */
  public static char[] edgeReport(Cube cube) {
    CubeColor[] colors = new CubeColor[NUM_EDGE_STICKERS];

    for (int i = 0; i < NUM_EDGE_STICKERS; i++) {
      int[] coord = edgeCoords[i];
      colors[i] = cube.getStickerAt(coord[0], coord[1], coord[2]);
    }

    char[] report = new char[NUM_EDGE_STICKERS];

    for (HashSet<Character> pieceStickers : edgePieces) {
      HashSet<CubeColor> piece = new HashSet<>();

      for (char sticker : pieceStickers) {
        piece.add(colors[sticker - 'a']);
      }

      HashMap<CubeColor, Character> map = PieceToStickers.getEdge(piece);

      if (map == null) {
        throw new IllegalArgumentException("Cube has invalid edge piece");
      }

      for (char sticker : pieceStickers) {
        report[sticker - 'a'] = map.get(colors[sticker - 'a']);
      }
    }
    return report;
  }

  /*
   * Given a Cube object cube, returns a char[] that represents the current state
   * of
   * each corner-piece sticker on the cube. The array is indexed with 'a' at
   * position 0,
   * and the characters in the array represent the current sticker that is in that
   * position.
   */
  public static char[] cornerReport(Cube cube) {
    CubeColor[] colors = new CubeColor[NUM_CORNER_STICKERS];

    for (int i = 0; i < NUM_CORNER_STICKERS; i++) {
      int[] coord = cornerCoords[i];
      colors[i] = cube.getStickerAt(coord[0], coord[1], coord[2]);
    }

    char[] report = new char[NUM_CORNER_STICKERS];

    for (HashSet<Character> pieceStickers : cornerPieces) {
      HashSet<CubeColor> piece = new HashSet<>();

      for (char sticker : pieceStickers) {
        piece.add(colors[sticker - 'a']);
      }

      HashMap<CubeColor, Character> map = PieceToStickers.getCorner(piece);

      if (map == null) {
        throw new IllegalArgumentException("Cube has invalid edge piece");
      }

      for (char sticker : pieceStickers) {
        report[sticker - 'a'] = map.get(colors[sticker - 'a']);
      }
    }
    return report;
  }

  /* Performs some very simple unit testing */
  public static void main(String[] args) {
    Cube cube = new Cube();
    cube.scrambleOrientation();

    cube.execute(Move.R);

    System.out.println(cube);

    System.out.println(Arrays.toString(edgeReport(cube)));
    System.out.println(Arrays.toString(cornerReport(cube)));
  }
}

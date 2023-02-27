/* *****************************************************************************
 *  Author:    Andrew Tao
 *
 *  Description:  Has methods that take a Cube object as input and returns a
 *                character array representing the state and position of
 *                each sticker on the Cube.
 *
 **************************************************************************** */

package com.aytao.rubiks.cube;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.aytao.rubiks.utils.Defines;
import com.aytao.rubiks.utils.ResourceHandler;

public class SpeffzUtils {

  private static final int[][] edgeCoords;
  private static final Set<Set<Character>> edgePieceSets;

  private static final int[][] cornerCoords;
  private static final Set<Set<Character>> cornerPieceSets;

  private final static Map<Set<CubeColor>, Map<CubeColor, Character>> edgePieceMap;
  private final static Map<Set<CubeColor>, Map<CubeColor, Character>> cornerPieceMap;

  private static final Map<Character, Set<Character>> relatedEdgeStickers;
  private final static Map<Character, Set<Character>> relatedCornerStickers;

  /*****************************************************************************
   * Initializers
   ****************************************************************************/

  static {
    Cube cube = new Cube();
    cube.scrambleOrientation();
    CubeColor[][][] allStickers = cube.getStickers();

    edgeCoords = getCoords("Labels/EdgeLabels.txt");
    cornerCoords = getCoords("Labels/CornerLabels.txt");

    edgePieceSets = getPieces("Connections/EdgeConnections.txt");
    cornerPieceSets = getPieces("Connections/CornerConnections.txt");

    edgePieceMap = getPieceMap(edgeCoords, edgePieceSets, allStickers);
    cornerPieceMap = getPieceMap(cornerCoords, cornerPieceSets, allStickers);

    relatedEdgeStickers = getRelatedStickersMap(edgePieceSets);
    relatedCornerStickers = getRelatedStickersMap(cornerPieceSets);
  }

  /*
   * Opens the csv file labelsFile which should have Defines.NUM_SPEFFZ_LETTERS
   * number of lines, and uses the information to return a 2d mapping of sticker
   * names to coordinates.
   */
  private static int[][] getCoords(String labelsFileName) {
    int[][] coords = new int[Defines.NUM_SPEFFZ_LETTERS][];
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
      throw new RuntimeException("Error opening file '" + labelsFileName + "'", e);
    }

    return coords;
  }

  /*
   * Opens the csv file connections. Uses the information in the file to create
   * a HashSet of HashSets, where the inner most HashSet is a set of characters
   * that represents stickers of the same piece, and the outer HashSet is a set
   * of all pieces
   */
  private static Set<Set<Character>> getPieces(String connectionsFileName) {
    Set<Set<Character>> piecesSet = new HashSet<>();
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

  private static Map<Set<CubeColor>, Map<CubeColor, Character>> getPieceMap(int[][] coords,
      Set<Set<Character>> pieceSets, CubeColor[][][] allStickers) {
    Map<Set<CubeColor>, Map<CubeColor, Character>> pieceToStickersMap = new HashMap<>();

    CubeColor[] stickerColors = new CubeColor[Defines.NUM_SPEFFZ_LETTERS];

    for (int i = 0; i < Defines.NUM_SPEFFZ_LETTERS; i++) {
      int[] coord = coords[i];
      stickerColors[i] = allStickers[coord[0]][coord[1]][coord[2]];
    }

    for (Set<Character> pieceSet : pieceSets) {
      HashMap<CubeColor, Character> map = new HashMap<>();
      for (char sticker : pieceSet) {
        map.put(stickerColors[sticker - 'a'], sticker);
      }
      pieceToStickersMap.put(map.keySet(), map);
    }

    return pieceToStickersMap;
  }

  private static Map<Character, Set<Character>> getRelatedStickersMap(Set<Set<Character>> pieceSets) {
    Map<Character, Set<Character>> map = new HashMap<>();

    for (Set<Character> pieceSet : pieceSets) {
      for (char sticker : pieceSet) {
        map.put(sticker, pieceSet);
      }
    }

    return map;
  }

  /*****************************************************************************
   * Sticker Colors
   ****************************************************************************/
  public static CubeColor getEdgeStickerColor(char c, Cube cube) {
    c = Character.toLowerCase(c);
    if (!isValidSpeffzLetter(c)) {
      throw new IllegalArgumentException("Letter " + c + " is not a valid Speffz letter");
    }

    int[] coord = edgeCoords[c - 'a'];

    return cube.getStickerAt(coord[0], coord[1], coord[2]);
  }

  public static CubeColor getCornerStickerColor(char c, Cube cube) {
    c = Character.toLowerCase(c);
    if (!isValidSpeffzLetter(c)) {
      throw new IllegalArgumentException("Letter " + c + " is not a valid Speffz letter");
    }

    int[] coord = cornerCoords[c - 'a'];

    return cube.getStickerAt(coord[0], coord[1], coord[2]);
  }

  public static CubeColor getCenterStickerColor(Face face, Cube cube) {
    return cube.getStickerAt(face.faceNum, 1, 1);
  }

  public static CubeColor[][] getWholeFace(Face face, Cube cube) {
    CubeColor[][] ret = new CubeColor[Cube.N][Cube.N];
    CubeColor[][] faceColors = cube.getStickers()[face.faceNum];

    for (int i = 0; i < ret.length; i++) {
      for (int j = 0; j < ret[i].length; j++) {
        ret[i][j] = faceColors[i][j];
      }
    }

    return ret;
  }

  /*****************************************************************************
   * Related Sticker Sets
   ****************************************************************************/

  private static Set<Character> getRelatedSticker(char c, Map<Character, Set<Character>> relatedStickers) {
    c = Character.toLowerCase(c);
    if (!isValidSpeffzLetter(c)) {
      throw new IllegalArgumentException("Letter " + c + " is not a valid Speffz letter");
    }

    return new HashSet<>(relatedStickers.get(c));
  }

  public static Set<Character> getRelatedEdgeStickersSet(char c) {
    return getRelatedSticker(c, relatedEdgeStickers);
  }

  public static Set<Character> getRelatedCornerStickersSet(char c) {
    return getRelatedSticker(c, relatedCornerStickers);
  }

  /*****************************************************************************
   * Report Methods
   ****************************************************************************/

  /*
   * Given a Cube object cube, returns a char[] that represents the current state
   * of each edge-piece sticker on the cube. The array is indexed with 'a' at
   * position 0, and the characters in the array represent the current sticker
   * that is in that position.
   */
  public static char[] edgeReport(Cube cube) {
    CubeColor[] colors = new CubeColor[Defines.NUM_SPEFFZ_LETTERS];

    for (int i = 0; i < Defines.NUM_SPEFFZ_LETTERS; i++) {
      int[] coord = edgeCoords[i];
      colors[i] = cube.getStickerAt(coord[0], coord[1], coord[2]);
    }

    char[] report = new char[Defines.NUM_SPEFFZ_LETTERS];

    for (Set<Character> pieceStickers : edgePieceSets) {
      HashSet<CubeColor> piece = new HashSet<>();

      for (char sticker : pieceStickers) {
        piece.add(colors[sticker - 'a']);
      }

      Map<CubeColor, Character> map = edgePieceMap.get(piece);

      if (map == null) {
        throw new IllegalArgumentException("Cube has invalid edge piece");
      }

      for (char sticker : pieceStickers) {
        report[sticker - 'a'] = map.get(colors[sticker - 'a']);
      }
    }
    assert (isValidReport(report));
    return report;
  }

  /*
   * Given a Cube object cube, returns a char[] that represents the current state
   * of each corner-piece sticker on the cube. The array is indexed with 'a' at
   * position 0, and the characters in the array represent the current sticker
   * that is in that position.
   */
  public static char[] cornerReport(Cube cube) {
    CubeColor[] colors = new CubeColor[Defines.NUM_SPEFFZ_LETTERS];

    for (int i = 0; i < Defines.NUM_SPEFFZ_LETTERS; i++) {
      int[] coord = cornerCoords[i];
      colors[i] = cube.getStickerAt(coord[0], coord[1], coord[2]);
    }

    char[] report = new char[Defines.NUM_SPEFFZ_LETTERS];

    for (Set<Character> pieceStickers : cornerPieceSets) {
      HashSet<CubeColor> piece = new HashSet<>();

      for (char sticker : pieceStickers) {
        piece.add(colors[sticker - 'a']);
      }

      Map<CubeColor, Character> map = cornerPieceMap.get(piece);

      if (map == null) {
        throw new IllegalArgumentException("Cube has invalid edge piece");
      }

      for (char sticker : pieceStickers) {
        report[sticker - 'a'] = map.get(colors[sticker - 'a']);
      }
    }
    assert (isValidReport(report));
    return report;
  }

  /*****************************************************************************
   * Validation helpers
   ****************************************************************************/

  /* Letter must be between 'a' and 'x', inclusive */
  public static boolean isValidSpeffzLetter(char c) {
    return c >= 'a' && c <= 'x';
  }

  /* Report should contain exactly one of each letter */
  private static boolean isValidReport(char[] report) {
    boolean[] seen = new boolean[report.length];

    for (char c : report) {
      if (!isValidSpeffzLetter(c)) {
        return false;
      }
      if (seen[c - 'a']) {
        return false;
      } else {
        seen[c - 'a'] = true;
      }
    }

    return true;
  }

  /* Performs some very simple unit testing */
  public static void main(String[] args) {
    /* TODO: Better testing */
    Cube cube = new Cube();
    cube.scrambleOrientation();

    cube.execute(Move.R);

    System.out.println(cube);

    System.out.println(Arrays.toString(edgeReport(cube)));
    System.out.println(Arrays.toString(cornerReport(cube)));
  }
}

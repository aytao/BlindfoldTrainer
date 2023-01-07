/* *****************************************************************************
 *  Author:    Andrew Tao
 *
 *  Description:  Provides methods that allow for solving a Cube object
 *                using the Old Pochmann (blindfold) method.
 *
 **************************************************************************** */

package com.aytao.rubiks.pochmann;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import com.aytao.rubiks.ResourceHandler;
import com.aytao.rubiks.Solution;
import com.aytao.rubiks.cube.Cube;
import com.aytao.rubiks.cube.Move;
import com.aytao.rubiks.cube.Sequence;
import com.aytao.rubiks.trace.CycleTracer;
import com.aytao.rubiks.trace.reporting.StickerReport;

public class Pochmann {
  public static class PochmannSolution implements Solution {

    /* Sequences of moves used in the Old Pochmann Method */
    private static final ArrayList<Move> EDGE_SWAP;
    private static final ArrayList<Move> PARITY;
    private static final ArrayList<Move> CORNER_SWAP;

    static {
      EDGE_SWAP = Sequence.getSequence("R U R' U' R' F R2 U' R' U' R U R' F'");
      PARITY = Sequence.getSequence("R U R' F' R U2 R' U2 R' F R U R U2 R' U'");
      CORNER_SWAP = Sequence.getSequence("R U' R' U' R U R' F' R U R' U' R' F R");
    }

    ArrayList<Character> edgeOrder;
    ArrayList<Character> cornerOrder;
    boolean parity;

    public PochmannSolution(ArrayList<Character> edgeOrder,
        ArrayList<Character> cornerOrder, boolean parity) throws IllegalArgumentException {
      checkOrderValidity(edgeOrder, cornerOrder);
      this.edgeOrder = new ArrayList<>();
      for (char c : edgeOrder) {
        this.edgeOrder.add(Character.toLowerCase(c));
      }
      this.cornerOrder = new ArrayList<>();
      for (char c : cornerOrder) {
        this.cornerOrder.add(Character.toLowerCase(c));
      }
      this.parity = parity;
    }

    private static void checkOrderValidity(ArrayList<Character> edgeOrder,
        ArrayList<Character> cornerOrder) {
      if (edgeOrder == null || cornerOrder == null)
        throw new IllegalArgumentException("An order is null");
      for (Character character : edgeOrder) {
        if (character == null) {
          throw new IllegalArgumentException("A sticker provided in edgeOrder is null");
        }
        char c = Character.toLowerCase(character);
        if (Character.toLowerCase(c) < 'a' || Character.toLowerCase(c) > 'x') {
          throw new IllegalArgumentException("Sticker " + c + "is not a valid sticker");
        }
        if (relatedEdgeStickers.get(EDGE_BUFFER).contains(c)) {
          throw new IllegalArgumentException("Cannot swap sticker " + c + " with buffer " + EDGE_BUFFER);
        }
      }
      for (Character character : cornerOrder) {
        if (character == null) {
          throw new IllegalArgumentException("A sticker provided in cornerOrder is null");
        }
        char c = Character.toLowerCase(character);
        if (c < 'a' || c > 'x') {
          throw new IllegalArgumentException("Sticker " + character + "is not a valid sticker");
        }
        if (relatedCornerStickers.get(CORNER_BUFFER).contains(c)) {
          throw new IllegalArgumentException("Cannot swap sticker " + character + " with buffer " + CORNER_BUFFER);
        }
      }
    }

    public ArrayList<Move> toMoves() {
      ArrayList<Move> solution = new ArrayList<>();
      swapAllEdges(solution, edgeOrder);
      if (parity) {
        solution.addAll(PARITY);
      }
      swapAllCorners(solution, cornerOrder);
      return solution;
    }

    /*******************************************************
     * Swapping methods
     *******************************************************/

    /* Swap all edges with buffer in given order */
    private static void swapAllEdges(ArrayList<Move> solution, Iterable<Character> edges) {
      for (Character c : edges) {
        swapBufferWithEdge(solution, c);
      }
    }

    /* Swap given edge sticker with buffer */
    private static void swapBufferWithEdge(ArrayList<Move> solution, char c) {
      assert (c >= 'a' && c <= 'x');
      assert (!relatedEdgeStickers.get(EDGE_BUFFER).contains(c));
      assert (EDGE_SETUPS[c - 'a'] != null);

      ArrayList<Move> setup = Sequence.getSequence(EDGE_SETUPS[c - 'a']);

      // Move the intended edge sticker to the target, swap the buffer with the
      // target,
      // and move the newly swapped edge back
      solution.addAll(setup);
      solution.addAll(EDGE_SWAP);
      solution.addAll(Sequence.getInverse(setup));
    }

    /* Swap all corners with buffer in given order */
    private static void swapAllCorners(ArrayList<Move> solution, Iterable<Character> corners) {
      for (Character c : corners) {
        swapBufferWithCorner(solution, c);
      }
    }

    /* Swap given edge sticker with buffer */
    private static void swapBufferWithCorner(ArrayList<Move> solution, char c) {
      assert (c >= 'a' && c <= 'x');
      assert (!relatedCornerStickers.get(CORNER_BUFFER).contains(c));
      assert (CORNER_SETUPS[c - 'a'] != null);

      ArrayList<Move> setup = Sequence.getSequence(CORNER_SETUPS[c - 'a']);

      // Move the intended corner sticker to the target, swap the buffer with the
      // target,
      // and move the newly swapped corner back
      solution.addAll(setup);
      solution.addAll(CORNER_SWAP);
      solution.addAll(Sequence.getInverse(setup));
    }
  }

  /* the number of edge and corner stickers */
  private static final int NUM_EDGE_STICKERS = 24;
  private static final int NUM_CORNER_STICKERS = 24;

  /*
   * Sets of set up moves needed to get any specific sticker in the target
   * position
   */
  private static final String[] EDGE_SETUPS;
  private static final String[] CORNER_SETUPS;

  /* Maps a specific sticker to a set of all stickers on the same piece */
  /* TODO: Move to utils */
  private static final HashMap<Character, HashSet<Character>> relatedEdgeStickers;
  private static final HashMap<Character, HashSet<Character>> relatedCornerStickers;

  /*
   * The buffers used for the edge swap (T perm) and corner swap (modified Y perm)
   * sequences
   */
  private static final char EDGE_BUFFER = 'b';
  private static final char CORNER_BUFFER = 'e';

  static {
    EDGE_SETUPS = getSetUpMoves("Setup Moves/EdgeSetUpMoves.txt", NUM_EDGE_STICKERS);
    CORNER_SETUPS = getSetUpMoves("Setup Moves/CornerSetUpMoves.txt", NUM_CORNER_STICKERS);

    relatedEdgeStickers = getRelatedStickers("Connections/EdgeConnections.txt");
    relatedCornerStickers = getRelatedStickers("Connections/CornerConnections.txt");
  }

  /*
   * Opens the file setupFile and retrieves all setup moves for each sticker.
   * Returns a String array where the String at each index (where index 0
   * represents 'a')
   * contains a sequence of moves that when executed, move the specified sticker
   * to the
   * target position to be swapped with the buffer.
   */
  private static String[] getSetUpMoves(String setupFileName, int numStickers) {
    String[] arr = new String[numStickers];

    try (Scanner in = new Scanner(ResourceHandler.getFile(setupFileName), "utf-8")) {
      while (in.hasNext()) {
        String line = in.nextLine();
        String[] stickerSetup = line.split(":");

        assert (stickerSetup[0].length() == 1);

        char sticker = stickerSetup[0].charAt(0);
        if (stickerSetup.length == 2) {
          String setup = stickerSetup[1];
          arr[sticker - 'a'] = setup;
        } else {
          arr[sticker - 'a'] = "";
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("Error opening file: '" + setupFileName + "'");
    }

    return arr;
  }

  /*
   * Opens the file connectionsFile and retrieves all related stickers for each
   * piece. Returns
   * a HashMap where each sticker maps to a set of all stickers on the same piece
   */
  private static HashMap<Character, HashSet<Character>> getRelatedStickers(String connectionsFileName) {
    HashMap<Character, HashSet<Character>> map = new HashMap<>();

    try (Scanner in = new Scanner(ResourceHandler.getFile(connectionsFileName), "utf-8")) {
      while (in.hasNext()) {
        String line = in.nextLine();
        String[] stickers = line.split(",");

        HashSet<Character> set = new HashSet<>();
        for (String sticker : stickers) {
          map.put(sticker.charAt(0), set);
          set.add(sticker.charAt(0));
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("Error opening file: '" + connectionsFileName + "'");
    }

    return map;
  }

  /*******************************************************
   * Solution methods
   *******************************************************/
  /*
   * Checks if a given edge order, parity, and corner order would validly solve
   * the cube
   */
  public static boolean checkValidity(ArrayList<Move> scramble, ArrayList<Character> edgeOrder,
      ArrayList<Character> cornerOrder, boolean parity) {
    if (cornerOrder.size() % 2 != edgeOrder.size() % 2)
      return false;
    if ((cornerOrder.size() % 2 == 1) != parity)
      return false;
    Cube cube = new Cube();
    cube.execute(scramble);
    PochmannSolution solution = new PochmannSolution(edgeOrder, cornerOrder, parity);
    if (!cube.validSolution(solution))
      return false;

    return true;
  }

  /* Solves a cube using the Pochmann method and returns the solution used */
  public static PochmannSolution getSolution(Cube cube) {
    ArrayList<Character> edges = CycleTracer.edgeOrder(cube, EDGE_BUFFER);
    ArrayList<Character> corners = CycleTracer.cornerOrder(cube, CORNER_BUFFER);
    PochmannSolution solution = new PochmannSolution(edges, corners, edges.size() % 2 == 1);

    assert (cube.validSolution(solution));

    return solution;
  }

  /* Handle a solution failure (in the main method) */
  private static void handleFailure(ArrayList<Move> scramble, ArrayList<Move> solution) {
    Cube cube = new Cube();
    cube.scrambleOrientation();

    System.out.print("Scramble:");
    for (Move move : scramble) {
      System.out.print(" " + move);
      cube.execute(move);
    }
    System.out.println("\nScrambled State:\n" + cube);

    System.out.print("Solution:");
    for (Move move : solution) {
      System.out.print(" " + move);
      cube.execute(move);
    }
    System.out.println("\nResulting State:\n" + cube);
  }

  /*
   * Takes a command line argument with a default value of 20, and attempts to
   * solve the specified number of random scrambles. Prints to stdout the result
   * of the attempts.
   */
  public static void main(String[] args) {

    int numTests = 20;
    if (args.length == 1) {
      numTests = Integer.parseInt(args[0]);
    }

    for (int i = 0; i < numTests; i++) {
      Cube cube = new Cube();
      cube.scrambleOrientation();

      ArrayList<Move> scramble = Sequence.getScramble();
      cube.execute(scramble);

      PochmannSolution solution = getSolution(cube);
      ArrayList<Move> solutionMoves = solution.toMoves();
      boolean valid = checkValidity(scramble, solution.edgeOrder,
          solution.cornerOrder, solution.parity);
      if (!cube.validSolution(solutionMoves) || !valid) {
        handleFailure(scramble, solutionMoves);
        return;
      }
    }

    System.out.println("All " + numTests + " random scrambles solved!");
  }
}

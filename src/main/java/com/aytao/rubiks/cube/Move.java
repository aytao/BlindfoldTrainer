/* *****************************************************************************
 *  Author:    Andrew Tao
 *
 *  Description:  An enum representing all supported moves by the Cube class.
 *                Also provides methods for working with String representations
 *                and generating random moves.
 *
 **************************************************************************** */

package com.aytao.rubiks.cube;

public enum Move {
  // WCA legal moves
  U, Up, U2,
  D, Dp, D2,
  F, Fp, F2,
  B, Bp, B2,
  R, Rp, R2,
  L, Lp, L2,

  // Wide moves
  Uw, Uwp, Uw2,
  Dw, Dwp, Dw2,
  Fw, Fwp, Fw2,
  Bw, Bwp, Bw2,
  Rw, Rwp, Rw2,
  Lw, Lwp, Lw2,

  // Slice moves and cube rotations
  M, Mp, M2,
  S, Sp, S2,
  E, Ep, E2,
  X, Xp, X2,
  Y, Yp, Y2,
  Z, Zp, Z2;

  public static class IllegalMoveException extends IllegalArgumentException {

    private String moveStr;

    public IllegalMoveException(String moveStr) {
      super();
      this.moveStr = moveStr;
    }

    @Override
    public String toString() {
      return "Move '" + this.moveStr + "' is not supported";
    }
  }

  // all moves
  private static final Move[] allMoves = Move.values();

  // The number of WCA moves supported
  private static final int NUM_WCA_LEGAL_MOVES = 18;

  /* Prints a specified move to stdout, using standard cube notation */
  public String toString() {
    if (this.name().length() == 1)
      return this.name();

    String s = this.name().replace('p', '\'');

    if (s.charAt(1) == 'w') {
      return Character.toLowerCase(s.charAt(0)) + s.substring(2);
    } else if ('x' <= s.charAt(0) && s.charAt(0) <= 'z') {
      return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    return s;
  }

  /*
   * Parses a provided String into a Move.
   * Throws an IllegalMoveException for unsupported moves.
   */
  public static Move move(String moveStr) {
    String s = moveStr.replace('\'', 'p');
    Move move;
    try {
      if (Character.isLowerCase(s.charAt(0))) {
        // Rotations
        if ('x' <= s.charAt(0) && s.charAt(0) <= 'z') {
          s = Character.toUpperCase(s.charAt(0)) + s.substring(1);
        }
        // Wide moves
        else {
          s = Character.toUpperCase(s.charAt(0)) + "w" + s.substring(1);
        }
      }

      // Ignore ' on double moves (M2', R2', etc.);
      if (s.length() >= 3 && s.charAt(s.length() - 1) == 'p' && s.charAt(s.length() - 2) == '2') {
        s = s.substring(0, s.length() - 1);
      }
      move = valueOf(s);
    } catch (IllegalArgumentException e) {
      throw new IllegalMoveException(moveStr);
    }
    return move;
  }

  /* Returns the inverse of a specified move */
  public static Move getInverse(Move move) {
    if (move.name().contains("2")) {
      return move;
    }

    if (move.name().contains("p")) {
      return move(move.name().replace("p", ""));
    }

    return move(move.name() + "p");
  }

  /* Returns a random move */
  public static Move randomMove() {
    return allMoves[(int) (Math.random() * allMoves.length)];
  }

  /* Returns a random WCA-legal move */
  public static Move randomWCAMove() {
    return allMoves[(int) (Math.random() * NUM_WCA_LEGAL_MOVES)];
  }
}

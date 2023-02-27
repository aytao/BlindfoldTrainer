/* *****************************************************************************
 *  Author:    Andrew Tao
 *
 *  Description:  Implements a Cube data type that represents a 3x3 Rubik’s
 *                Cube and allows for execution of all 18 WCA-legal face moves,
 *                as well as slice moves, wide moves, and cube rotations.
 *
 **************************************************************************** */

package com.aytao.rubiks.cube;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Cube {
  // Dimension of the cube
  static final int N = 3;

  // Number of faces on the cube
  static final int NUM_FACES = 6;

  // Face names
  static final int U = 0;
  static final int L = 1;
  static final int F = 2;
  static final int R = 3;
  static final int B = 4;
  static final int D = 5;

  // A Cube in the solved state
  private static final Cube SOLVED_STATE;

  static {
    SOLVED_STATE = new Cube();
    SOLVED_STATE.scrambleOrientation();
  }

  // A 3D-array representing the color of each sticker
  CubeColor[][][] stickers;

  // Makes a new instance of the Cube class. Move recording is set to off
  public Cube() {
    stickers = solvedState();
  }

  // Makes a new Cube with the same sticker positions as the provided Cube. Does
  // not copy move recording information
  public Cube(Cube cube) {
    stickers = cube.getStickers();
  }

  public Cube(List<Move> scramble) {
    stickers = solvedState();
    this.scrambleOrientation();
    this.execute(scramble);
  }

  /*
   * Returns true if and only if the provided object is a Cube and every sticker
   * in the provided object
   * is the same as the corresponding sticker in this Cube. Does not account for
   * orientation; two cubes
   * that differ only by cube rotations are still considered different.
   */
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Cube cube = (Cube) o;
    return Arrays.deepEquals(stickers, cube.stickers);
  }

  /*
   * Uses stickers array to return a hashcode for this Cube object
   */
  public int hashCode() {
    return Arrays.deepHashCode(stickers);
  }

  /* Returns true if and only if the provided cube is solved */
  public boolean isSolved() {
    Cube clone = new Cube(this);
    clone.scrambleOrientation();

    return clone.equals(SOLVED_STATE);
  }

  /* Returns true if and only if the provided moves solve the cube */
  public boolean validSolution(ArrayList<Move> solution) {
    Cube copy = new Cube(this);
    copy.execute(solution);
    return copy.isSolved();
  }

  /*
   * Returns true if and only if the moves of the provided solution solve the cube
   */
  public boolean validSolution(Solution solution) {
    return validSolution(solution.toSequence());
  }

  /*
   * Puts the cube in the scrambling orientation (White top, green front)
   * Returns the sequence of the moves used
   */
  public ArrayList<Move> scrambleOrientation() {
    ArrayList<Move> sequence = new ArrayList<>();

    if (stickers[U][1][1] != CubeColor.WHITE) {
      if (stickers[U][1][1] == CubeColor.YELLOW) {
        X2();
        sequence.add(Move.X2);
      } else {
        while (stickers[F][1][1] != CubeColor.WHITE) {
          Y();
          sequence.add(Move.Y);
        }
        X();
        sequence.add(Move.X);
      }
    }

    while (stickers[F][1][1] != CubeColor.GREEN) {
      Y();
      sequence.add(Move.Y);
    }

    assert (stickers[F][1][1] == CubeColor.GREEN);
    assert (stickers[U][1][1] == CubeColor.WHITE);

    return sequence;
  }

  /* Returns a copy of the stickers array */
  CubeColor[][][] getStickers() {
    CubeColor[][][] copy = new CubeColor[NUM_FACES][N][N];

    for (int face = 0; face < NUM_FACES; face++) {
      for (int row = 0; row < N; row++) {
        System.arraycopy(stickers[face][row], 0, copy[face][row], 0, N);
      }
    }

    return copy;
  }

  /* Returns the color of the sticker at the requested coordinate */
  CubeColor getStickerAt(int face, int row, int col) {
    return stickers[face][row][col];
  }

  /*****************************************************************************
   * Move execution methods
   ****************************************************************************/

  /* Executes the move given as a parameter */
  public void execute(Move move) {
    switch (move) {
      case U:
        U();
        break;
      case Up:
        Up();
        break;
      case U2:
        U2();
        break;
      case Uw:
        U();
        Ep();
        break;
      case Uwp:
        Up();
        E();
        break;
      case Uw2:
        U2();
        E2();
        break;
      case D:
        D();
        break;
      case Dp:
        Dp();
        break;
      case D2:
        D2();
        break;
      case Dw:
        D();
        E();
        break;
      case Dwp:
        Dp();
        Ep();
        break;
      case Dw2:
        D2();
        E2();
        break;
      case F:
        F();
        break;
      case Fp:
        Fp();
        break;
      case F2:
        F2();
        break;
      case Fw:
        F();
        S();
        break;
      case Fwp:
        Fp();
        Sp();
        break;
      case Fw2:
        F2();
        S2();
        break;
      case B:
        B();
        break;
      case Bp:
        Bp();
        break;
      case B2:
        B2();
        break;
      case Bw:
        B();
        Sp();
        break;
      case Bwp:
        Bp();
        S();
        break;
      case Bw2:
        B2();
        S2();
        break;
      case R:
        R();
        break;
      case Rp:
        Rp();
        break;
      case R2:
        R2();
        break;
      case Rw:
        R();
        Mp();
        break;
      case Rwp:
        Rp();
        M();
        break;
      case Rw2:
        R2();
        M2();
        break;
      case L:
        L();
        break;
      case Lp:
        Lp();
        break;
      case L2:
        L2();
        break;
      case Lw:
        L();
        M();
        break;
      case Lwp:
        Lp();
        Mp();
        break;
      case Lw2:
        L2();
        M2();
        break;
      case M:
        M();
        break;
      case Mp:
        Mp();
        break;
      case M2:
        M2();
        break;
      case S:
        S();
        break;
      case Sp:
        Sp();
        break;
      case S2:
        S2();
        break;
      case E:
        E();
        break;
      case Ep:
        Ep();
        break;
      case E2:
        E2();
        break;
      case X:
        X();
        break;
      case Xp:
        Xp();
        break;
      case X2:
        X2();
        break;
      case Y:
        Y();
        break;
      case Yp:
        Yp();
        break;
      case Y2:
        Y2();
        break;
      case Z:
        Z();
        break;
      case Zp:
        Zp();
        break;
      case Z2:
        Z2();
        break;
      default:
        throw new IllegalArgumentException("Move is not yet supported!");
    }
  }

  /* Executes a sequence of moves */
  public void execute(List<Move> moves) {
    for (Move move : moves) {
      execute(move);
    }
  }

  /*****************************************************************************
   * Helper methods
   ****************************************************************************/

  /* Returns a stickers array of a solved cube */
  private static CubeColor[][][] solvedState() {
    CubeColor[][][] stickers = new CubeColor[6][N][N];

    for (int i = 0; i < NUM_FACES; i++) {
      CubeColor color;
      switch (i) {
        case U:
          color = CubeColor.WHITE;
          break;
        case F:
          color = CubeColor.GREEN;
          break;
        case R:
          color = CubeColor.RED;
          break;
        case B:
          color = CubeColor.BLUE;
          break;
        case L:
          color = CubeColor.ORANGE;
          break;
        default: // D
          color = CubeColor.YELLOW;
      }

      for (int j = 0; j < N; j++) {
        for (int k = 0; k < N; k++) {
          stickers[i][j][k] = color;
        }
      }
    }

    return stickers;
  }

  /* Copies parameter copy into parameter face */
  private static void copy(CubeColor[][] face, CubeColor[][] copy) {
    assert (face.length == copy.length);
    assert (face[0].length == copy[0].length);
    assert (face.length == N && face[0].length == N);

    for (int i = 0; i < N; i++) {
      System.arraycopy(copy[i], 0, face[i], 0, N);
    }
  }

  /* Rotates a provided face 90 degrees clockwise */
  private static void rotateCW(CubeColor[][] face) {
    CubeColor[][] copy = new CubeColor[face.length][face[0].length];

    int n = face.length - 1;

    for (int i = 0; i < face.length; i++) {
      for (int j = 0; j < face[0].length; j++) {
        copy[j][n - i] = face[i][j];
      }
    }

    copy(face, copy);
  }

  /* Rotates a provided face 90 degrees clockwise */
  private static void rotateCCW(CubeColor[][] face) {
    CubeColor[][] copy = new CubeColor[face.length][face[0].length];

    int n = face.length - 1;

    for (int i = 0; i < face.length; i++) {
      for (int j = 0; j < face[0].length; j++) {
        copy[n - j][i] = face[i][j];
      }
    }

    copy(face, copy);
  }

  /*****************************************************************************
   * Debugging methods
   ****************************************************************************/

  /* Returns a String representation of this object */
  public String toString() {
    StringBuilder str = new StringBuilder();

    // add U face
    for (int i = 0; i < N; i++) {
      str.append("\t");
      addRow(stickers[U][i], str);
      str.append('\n');
    }

    str.append('\n');

    // add each row
    for (int i = 0; i < N; i++) {
      for (int face = 1; face < NUM_FACES - 1; face++) {
        addRow(stickers[face][i], str);
        str.append('\t');
      }
      str.append('\n');
    }

    str.append('\n');

    // add D face
    for (int i = 0; i < N; i++) {
      str.append("\t");
      addRow(stickers[D][i], str);
      str.append('\n');
    }

    return str.toString();
  }

  /* Adds a row to the provided StringBuilder */
  private void addRow(CubeColor[] row, StringBuilder str) {
    for (CubeColor cubeColor : row) {
      str.append(cubeColor).append(" ");
    }
  }

  /* Check that the current state is legal (that there are N^2 of each color) */
  private boolean isLegal() {
    int yellow = 0;
    int red = 0;
    int green = 0;
    int orange = 0;
    int blue = 0;
    int white = 0;

    for (CubeColor[][] face : stickers) {
      for (int j = 0; j < N; j++) {
        for (int k = 0; k < N; k++) {
          switch (face[j][k]) {
            case YELLOW:
              yellow++;
              break;
            case RED:
              red++;
              break;
            case GREEN:
              green++;
              break;
            case ORANGE:
              orange++;
              break;
            case BLUE:
              blue++;
              break;
            case WHITE:
              white++;
              break;
          }
        }
      }
    }

    int n = N * N;

    return (yellow == n && red == n && green == n && orange == n && blue == n && white == n);
  }

  /*****************************************************************************
   * U moves
   ****************************************************************************/

  /* Turns the U face 1 quarter-turn clockwise */
  private void U() {
    rotateCW(stickers[U]);

    CubeColor[] temp = stickers[1][0];

    for (int i = 1; i < 4; i++) {
      stickers[i][0] = stickers[i + 1][0];
    }

    stickers[4][0] = temp;

    assert (isLegal());
  }

  /* Turns the U face 1 quarter-turn counterclockwise */
  private void Up() {

    rotateCCW(stickers[U]);

    CubeColor[] temp = stickers[4][0];

    for (int i = 4; i > 1; i--) {
      stickers[i][0] = stickers[i - 1][0];
    }

    stickers[1][0] = temp;

    assert (isLegal());
  }

  /* Turns the U face 1 half-turn */
  private void U2() {
    U();
    U();

    assert (isLegal());
  }

  /*****************************************************************************
   * D moves
   ****************************************************************************/

  /* Turns the D face 1 quarter-turn clockwise */
  private void D() {
    rotateCW(stickers[D]);

    CubeColor[] temp = stickers[4][N - 1];

    for (int i = 4; i > 1; i--) {
      stickers[i][N - 1] = stickers[i - 1][N - 1];
    }

    stickers[1][N - 1] = temp;

    assert (isLegal());
  }

  /* Turns the D face 1 quarter-turn counterclockwise */
  private void Dp() {
    rotateCCW(stickers[D]);

    CubeColor[] temp = stickers[1][N - 1];

    for (int i = 1; i < 4; i++) {
      stickers[i][N - 1] = stickers[i + 1][N - 1];
    }

    stickers[4][N - 1] = temp;

    assert (isLegal());
  }

  /* Turns the D face 1 half-turn */
  private void D2() {
    D();
    D();

    assert (isLegal());
  }

  /*****************************************************************************
   * F moves
   ****************************************************************************/

  /* Turns the F face 1 quarter-turn clockwise */
  private void F() {
    // rotate stickers on face
    rotateCW(stickers[F]);

    // rotate edges
    for (int i = 0; i < N; i++) {
      CubeColor temp = stickers[L][N - 1 - i][N - 1];
      stickers[L][N - 1 - i][N - 1] = stickers[D][0][N - 1 - i];
      stickers[D][0][N - 1 - i] = stickers[R][i][0];
      stickers[R][i][0] = stickers[U][N - 1][i];
      stickers[U][N - 1][i] = temp;
    }

    assert (isLegal());
  }

  /* Turns the F face 1 quarter-turn counterclockwise */
  private void Fp() {
    // rotate stickers on face
    rotateCCW(stickers[F]);

    // rotate edges
    for (int i = 0; i < N; i++) {
      CubeColor temp = stickers[U][N - 1][i];
      stickers[U][N - 1][i] = stickers[R][i][0];
      stickers[R][i][0] = stickers[D][0][N - 1 - i];
      stickers[D][0][N - 1 - i] = stickers[L][N - 1 - i][N - 1];
      stickers[L][N - 1 - i][N - 1] = temp;
    }

    assert (isLegal());
  }

  /* Turns the F face 1 half-turn */
  private void F2() {
    F();
    F();

    assert (isLegal());
  }

  /*****************************************************************************
   * B moves
   ****************************************************************************/

  /* Turns the B face 1 quarter-turn clockwise */
  private void B() {
    // rotate stickers on face
    rotateCW(stickers[B]);

    // rotate edges
    for (int i = 0; i < N; i++) {
      CubeColor temp = stickers[R][i][N - 1];
      stickers[R][i][N - 1] = stickers[D][N - 1][N - 1 - i];
      stickers[D][N - 1][N - 1 - i] = stickers[L][N - 1 - i][0];
      stickers[L][N - 1 - i][0] = stickers[U][0][i];
      stickers[U][0][i] = temp;
    }

    assert (isLegal());
  }

  /* Turns the B face 1 quarter-turn counterclockwise */
  private void Bp() {
    // rotate stickers on face
    rotateCCW(stickers[B]);

    // rotate edges
    for (int i = 0; i < N; i++) {
      CubeColor temp = stickers[U][0][i];
      stickers[U][0][i] = stickers[L][N - 1 - i][0];
      stickers[L][N - 1 - i][0] = stickers[D][N - 1][N - 1 - i];
      stickers[D][N - 1][N - 1 - i] = stickers[R][i][N - 1];
      stickers[R][i][N - 1] = temp;
    }

    assert (isLegal());
  }

  /* Turns the B face 1 half-turn */
  private void B2() {
    B();
    B();

    assert (isLegal());
  }

  /*****************************************************************************
   * R moves
   ****************************************************************************/

  /* Turns the R face 1 quarter-turn clockwise */
  private void R() {
    // rotate stickers on face
    rotateCW(stickers[R]);

    // rotate edges
    for (int i = 0; i < N; i++) {
      CubeColor temp = stickers[F][i][N - 1];
      stickers[F][i][N - 1] = stickers[D][i][N - 1];
      stickers[D][i][N - 1] = stickers[B][N - 1 - i][0];
      stickers[B][N - 1 - i][0] = stickers[U][i][N - 1];
      stickers[U][i][N - 1] = temp;
    }

    assert (isLegal());
  }

  /* Turns the B face 1 quarter-turn counterclockwise */
  private void Rp() {
    // rotate stickers on face
    rotateCCW(stickers[R]);

    // rotate edges
    for (int i = 0; i < N; i++) {
      CubeColor temp = stickers[U][i][N - 1];
      stickers[U][i][N - 1] = stickers[B][N - 1 - i][0];
      stickers[B][N - 1 - i][0] = stickers[D][i][N - 1];
      stickers[D][i][N - 1] = stickers[F][i][N - 1];
      stickers[F][i][N - 1] = temp;
    }

    assert (isLegal());
  }

  /* Turns the L face 1 half-turn */
  private void R2() {
    R();
    R();

    assert (isLegal());
  }

  /*****************************************************************************
   * L moves
   ****************************************************************************/

  /* Turns the L face 1 quarter-turn clockwise */
  private void L() {
    // rotate stickers on face
    rotateCW(stickers[L]);

    // rotate edges
    for (int i = 0; i < N; i++) {
      CubeColor temp = stickers[B][N - 1 - i][N - 1];
      stickers[B][N - 1 - i][N - 1] = stickers[D][i][0];
      stickers[D][i][0] = stickers[F][i][0];
      stickers[F][i][0] = stickers[U][i][0];
      stickers[U][i][0] = temp;
    }

    assert (isLegal());
  }

  /* Turns the L face 1 quarter-turn counterclockwise */
  private void Lp() {
    // rotate stickers on face
    rotateCCW(stickers[L]);

    // rotate edges
    for (int i = 0; i < N; i++) {
      CubeColor temp = stickers[U][i][0];
      stickers[U][i][0] = stickers[F][i][0];
      stickers[F][i][0] = stickers[D][i][0];
      stickers[D][i][0] = stickers[B][N - 1 - i][N - 1];
      stickers[B][N - 1 - i][N - 1] = temp;
    }

    assert (isLegal());
  }

  /* Turns the L face 1 half-turn */
  private void L2() {
    L();
    L();

    assert (isLegal());
  }

  /*****************************************************************************
   * M moves (M has same axis as L)
   ****************************************************************************/

  /* Turns the M slice 1 quarter-turn clockwise */
  private void M() {

    for (int i = 0; i < N; i++) {
      CubeColor temp = stickers[B][N - 1 - i][N / 2];
      stickers[B][N - 1 - i][N / 2] = stickers[D][i][N / 2];
      stickers[D][i][N / 2] = stickers[F][i][N / 2];
      stickers[F][i][N / 2] = stickers[U][i][N / 2];
      stickers[U][i][N / 2] = temp;
    }

    assert (isLegal());
  }

  /* Turns the M slice 1 quarter-turn counterclockwise */
  private void Mp() {

    for (int i = 0; i < N; i++) {
      CubeColor temp = stickers[U][i][N / 2];
      stickers[U][i][N / 2] = stickers[F][i][N / 2];
      stickers[F][i][N / 2] = stickers[D][i][N / 2];
      stickers[D][i][N / 2] = stickers[B][N - 1 - i][N / 2];
      stickers[B][N - 1 - i][N / 2] = temp;
    }

    assert (isLegal());
  }

  /* Turns the M slice 1 half-turn */
  private void M2() {
    M();
    M();

    assert (isLegal());
  }

  /*****************************************************************************
   * S moves (S has same axis as F)
   ****************************************************************************/

  /* Turns the S slice 1 quarter-turn clockwise */
  private void S() {

    for (int i = 0; i < N; i++) {
      CubeColor temp = stickers[L][N - 1 - i][N / 2];
      stickers[L][N - 1 - i][N / 2] = stickers[D][N / 2][N - 1 - i];
      stickers[D][N / 2][N - 1 - i] = stickers[R][i][N / 2];
      stickers[R][i][N / 2] = stickers[U][N / 2][i];
      stickers[U][N / 2][i] = temp;
    }

    assert (isLegal());
  }

  /* Turns the S slice 1 quarter-turn counterclockwise */
  private void Sp() {

    for (int i = 0; i < N; i++) {
      CubeColor temp = stickers[U][N / 2][i];
      stickers[U][N / 2][i] = stickers[R][i][N / 2];
      stickers[R][i][N / 2] = stickers[D][N / 2][N - 1 - i];
      stickers[D][N / 2][N - 1 - i] = stickers[L][N - 1 - i][N / 2];
      stickers[L][N - 1 - i][N / 2] = temp;
    }

    assert (isLegal());
  }

  /* Turns the S slice 1 half-turn */
  private void S2() {
    S();
    S();

    assert (isLegal());
  }

  /*****************************************************************************
   * E moves (E has same axis as D)
   ****************************************************************************/

  /* Turns the E slice 1 quarter-turn clockwise */
  private void E() {
    CubeColor[] temp = stickers[4][N / 2];

    for (int i = 4; i > 1; i--) {
      stickers[i][N / 2] = stickers[i - 1][N / 2];
    }

    stickers[1][N / 2] = temp;

    assert (isLegal());
  }

  /* Turns the E face 1 quarter-turn counterclockwise */
  private void Ep() {
    CubeColor[] temp = stickers[1][N / 2];

    for (int i = 1; i < 4; i++) {
      stickers[i][N / 2] = stickers[i + 1][N / 2];
    }

    stickers[4][N / 2] = temp;

    assert (isLegal());
  }

  /* Turns the E slice 1 half-turn */
  private void E2() {
    E();
    E();

    assert (isLegal());
  }

  /*****************************************************************************
   * X Rotations
   ****************************************************************************/

  /* Rotate the entire cube one quarter turn clockwise on the R axis */
  private void X() {
    rotateCW(stickers[R]);
    rotateCCW(stickers[L]);

    CubeColor[][] temp = stickers[B];
    stickers[B] = stickers[U];
    stickers[U] = stickers[F];
    stickers[F] = stickers[D];
    stickers[D] = temp;

    // rotate D and B 180 degrees, due to representation of B
    rotateCCW(stickers[D]);
    rotateCCW(stickers[D]);
    rotateCCW(stickers[B]);
    rotateCCW(stickers[B]);

    assert (isLegal());
  }

  /* Rotate the entire cube one quarter turn clockwise on the L axis */
  private void Xp() {
    rotateCW(stickers[L]);
    rotateCCW(stickers[R]);

    CubeColor[][] temp = stickers[D];
    stickers[D] = stickers[F];
    stickers[F] = stickers[U];
    stickers[U] = stickers[B];
    stickers[B] = temp;

    // rotate B and U 180 degrees, due to representation of B
    rotateCCW(stickers[B]);
    rotateCCW(stickers[B]);
    rotateCCW(stickers[U]);
    rotateCCW(stickers[U]);

    assert (isLegal());
  }

  /* Rotate the entire cube one half turn on the R axis */
  private void X2() {
    X();
    X();

    assert (isLegal());
  }

  /*****************************************************************************
   * Y Rotations
   ****************************************************************************/

  /* Rotate the entire cube one quarter turn clockwise on the U axis */
  private void Y() {
    rotateCW(stickers[U]);
    rotateCCW(stickers[D]);

    CubeColor[][] temp = stickers[F];
    stickers[F] = stickers[R];
    stickers[R] = stickers[B];
    stickers[B] = stickers[L];
    stickers[L] = temp;

    assert (isLegal());
  }

  /* Rotate the entire cube one quarter turn clockwise on the D axis */
  private void Yp() {
    rotateCW(stickers[D]);
    rotateCCW(stickers[U]);

    CubeColor[][] temp = stickers[L];
    stickers[L] = stickers[B];
    stickers[B] = stickers[R];
    stickers[R] = stickers[F];
    stickers[F] = temp;

    assert (isLegal());
  }

  /* Rotate the entire cube one half turn on the U axis */
  private void Y2() {
    Y();
    Y();

    assert (isLegal());
  }

  /*****************************************************************************
   * Z Rotations
   ****************************************************************************/

  /* Rotate the entire cube one quarter turn clockwise on the F axis */
  private void Z() {
    rotateCW(stickers[F]);
    rotateCCW(stickers[B]);

    CubeColor[][] temp = stickers[L];
    stickers[L] = stickers[D];
    stickers[D] = stickers[R];
    stickers[R] = stickers[U];
    stickers[U] = temp;

    // rotate faces 90 degrees, due to array representation
    rotateCW(stickers[U]);
    rotateCW(stickers[R]);
    rotateCW(stickers[D]);
    rotateCW(stickers[L]);

    assert (isLegal());
  }

  /* Rotate the entire cube one quarter turn counterclockwise on the F axis */
  private void Zp() {
    rotateCW(stickers[B]);
    rotateCCW(stickers[F]);

    CubeColor[][] temp = stickers[L];
    stickers[L] = stickers[U];
    stickers[U] = stickers[R];
    stickers[R] = stickers[D];
    stickers[D] = temp;

    // rotate faces 90 degrees, due to array representation
    rotateCCW(stickers[U]);
    rotateCCW(stickers[R]);
    rotateCCW(stickers[D]);
    rotateCCW(stickers[L]);

    assert (isLegal());
  }

  /* Rotate the entire cube one half turn on the F axis */
  private void Z2() {
    Z();
    Z();

    assert (isLegal());
  }

  /*****************************************************************************
   * Unit testing
   ****************************************************************************/

  /* Executes n random moves and prints the resulting state */
  private static void randomTurns(Cube cube, int n) {
    System.out.println("Moves executed: ");
    for (int i = 0; i < n; i++) {
      Move move = Move.randomMove();
      cube.execute(move);
      System.out.print(move + " ");
    }
    System.out.println();
  }

  /*
   * If a command line integer is provided, executes the provided number of
   * random moves, and prints the moves and the resulting state of the cube to
   * stdout. Otherwise, reads a scramble from stdin and prints out the resulting
   * state of the cube.
   */
  public static void main(String[] args) {
    Cube cube = new Cube();

    if (args.length == 1) {
      randomTurns(cube, Integer.parseInt(args[0]));
      System.out.println(cube);
      return;
    }

    Scanner scanner;
    try {
      scanner = new Scanner(System.in, "utf-8");
    } catch (Exception e) {
      System.err.println("IO issue; executing " + 20 + "random moves");
      for (int i = 0; i < 20; i++) {
        cube.execute(Move.randomMove());
      }
      System.out.println(cube);
      return;
    }

    while (scanner.hasNext()) {
      Move move;
      try {
        String s = scanner.next();
        move = Move.move(s);
      } catch (Exception e) {
        System.out.println(e.getMessage());
        continue;
      }
      cube.execute(move);
    }

    System.out.println(cube);
    scanner.close();

  }
}

package com.aytao.rubiks.miscramble;

import com.aytao.rubiks.cube.Cube;
import com.aytao.rubiks.cube.Face;

public class UFaceCheck extends ScrambleChecker {

  @Override
  public boolean checkMisscramble(Cube misscrambledCube, Cube correctlyScrambledCube) {
    return checkFace(Face.U, misscrambledCube, correctlyScrambledCube);
  }

  @Override
  public String toString() {
    return "U Face Check: " + getMissRate();
  }
}

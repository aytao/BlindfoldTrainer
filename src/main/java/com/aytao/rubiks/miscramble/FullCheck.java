package com.aytao.rubiks.miscramble;

import com.aytao.rubiks.cube.Cube;

public class FullCheck extends ScrambleChecker {

  @Override
  public boolean checkMisscramble(Cube misscrambledCube, Cube correctlyScrambledCube) {
    return misscrambledCube.equals(correctlyScrambledCube);
  }

  @Override
  public String toString() {
    return "Full Check: " + getMissRate();
  }
}

package com.aytao.rubiks.miscramble;

import com.aytao.rubiks.cube.Cube;

public class NoCheck extends ScrambleChecker {

  @Override
  boolean checkMisscramble(Cube misscrambledCube, Cube correctlyScrambledCube) {
    return true;
  }
}

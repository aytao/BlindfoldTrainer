package com.aytao.rubiks.miscramble;

import com.aytao.rubiks.cube.Cube;

public class NoCheck extends ScrambleChecker {

  @Override
  boolean checkMisscramble(Cube misscramble, Cube correctScramble) {
    return true;
  }
}

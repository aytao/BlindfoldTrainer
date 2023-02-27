package com.aytao.rubiks.miscramble;

import java.util.List;

import com.aytao.rubiks.cube.Cube;
import com.aytao.rubiks.cube.Move;

public abstract class ScrambleChecker {

  private int totalCaught = 0;
  private int totalChecked = 0;

  public static class Stats {
    public final int totalCaught;
    public final int totalChecked;

    public Stats(int totalCaught, int totalChecked) {
      this.totalCaught = totalCaught;
      this.totalChecked = totalChecked;
    }
  }

  abstract boolean checkMisscramble(Cube misscramble, Cube correctScramble);

  public void checkMisscramble(List<Move> misscramble, List<Move> correctScramble) {
    Cube misscrambledCube = new Cube(misscramble);
    Cube correctlyScrambledCube = new Cube(correctScramble);

    if (checkMisscramble(misscrambledCube, correctlyScrambledCube)) {
      this.totalCaught++;
    }

    this.totalChecked++;
  }

  public Stats getStats() {
    return new Stats(this.totalCaught, this.totalChecked);
  }
}

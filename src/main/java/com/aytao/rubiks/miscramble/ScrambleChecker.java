package com.aytao.rubiks.miscramble;

import java.util.Arrays;
import java.util.List;

import com.aytao.rubiks.cube.Cube;
import com.aytao.rubiks.cube.CubeColor;
import com.aytao.rubiks.cube.Face;
import com.aytao.rubiks.cube.Move;
import com.aytao.rubiks.cube.SpeffzUtils;

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

  boolean checkFace(Face face, Cube misscrambledCube, Cube correctlyScrambledCube) {
    CubeColor[][] misscrambledUFace = SpeffzUtils.getWholeFace(face, misscrambledCube);
    CubeColor[][] correctlyScrambledUFace = SpeffzUtils.getWholeFace(face, correctlyScrambledCube);

    return Arrays.deepEquals(misscrambledUFace, correctlyScrambledUFace);
  }

  abstract boolean checkMisscramble(Cube misscrambledCube, Cube correctlyScrambledCube);

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

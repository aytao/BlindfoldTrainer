package com.aytao.rubiks.miscramble;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.aytao.rubiks.cube.Cube;
import com.aytao.rubiks.cube.CubeColor;
import com.aytao.rubiks.cube.Face;
import com.aytao.rubiks.cube.Move;
import com.aytao.rubiks.cube.SpeffzUtils;

public abstract class ScrambleChecker {

  int totalMissed = 0;
  int totalChecked = 0;

  public static class Stats {
    public final int totalMissed;
    public final int totalChecked;

    public Stats(int totalMissed, int totalChecked) {
      this.totalMissed = totalMissed;
      this.totalChecked = totalChecked;
    }
  }

  boolean checkFace(Face face, Cube misscrambledCube, Cube correctlyScrambledCube) {
    CubeColor[][] misscrambledUFace = SpeffzUtils.getWholeFace(face, misscrambledCube);
    CubeColor[][] correctlyScrambledUFace = SpeffzUtils.getWholeFace(face, correctlyScrambledCube);

    return Arrays.deepEquals(misscrambledUFace, correctlyScrambledUFace);
  }

  boolean checkEdgeSticker(char c, Cube misscrambledCube, Cube correctlyScrambledCube) {
    CubeColor misscrambledStickerColor = SpeffzUtils.getEdgeStickerColor(c, misscrambledCube);
    CubeColor correctStickerColor = SpeffzUtils.getEdgeStickerColor(c, correctlyScrambledCube);

    return misscrambledStickerColor == correctStickerColor;
  }

  boolean checkEdgeStickers(Set<Character> stickers, Cube misscrambledCube, Cube correctlyScrambledCube) {
    for (Character sticker : stickers) {
      if (!checkEdgeSticker(sticker, misscrambledCube, correctlyScrambledCube)) {
        return false;
      }
    }
    return true;
  }

  boolean checkCornerSticker(char c, Cube misscrambledCube, Cube correctlyScrambledCube) {
    CubeColor misscrambledStickerColor = SpeffzUtils.getCornerStickerColor(c, misscrambledCube);
    CubeColor correctStickerColor = SpeffzUtils.getCornerStickerColor(c, correctlyScrambledCube);

    return misscrambledStickerColor == correctStickerColor;
  }

  boolean checkCornerStickers(Set<Character> stickers, Cube misscrambledCube, Cube correctlyScrambledCube) {
    for (Character sticker : stickers) {
      if (!checkCornerSticker(sticker, misscrambledCube, correctlyScrambledCube)) {
        return false;
      }
    }
    return true;
  }

  public abstract boolean checkMisscramble(Cube misscrambledCube, Cube correctlyScrambledCube);

  public void checkMisscramble(List<Move> misscramble, List<Move> correctScramble) {
    Cube misscrambledCube = new Cube(misscramble);
    Cube correctlyScrambledCube = new Cube(correctScramble);

    if (misscrambledCube.equals(correctlyScrambledCube))
      return;

    if (checkMisscramble(misscrambledCube, correctlyScrambledCube)) {
      this.totalMissed++;
    }

    this.totalChecked++;
  }

  public Stats getStats() {
    return new Stats(this.totalMissed, this.totalChecked);
  }

  public double getMissRate() {
    return (double) this.totalMissed / this.totalChecked;
  }
}

package com.aytao.rubiks.miscramble;

import java.util.HashSet;
import java.util.Set;

import com.aytao.rubiks.cube.Cube;
import com.aytao.rubiks.cube.SpeffzUtils;

public class UDCorners extends ScrambleChecker {

  private static final Set<Character> cornerStickers;

  static {
    char[] cornerStickersArray = new char[] { 'a', 'b', 'c', 'd', 'u', 'v', 'w', 'x' };

    cornerStickers = new HashSet<>();
    for (char c : cornerStickersArray) {
      cornerStickers.add(c);
    }
  }

  @Override
  public boolean checkMisscramble(Cube misscrambledCube, Cube correctlyScrambledCube) {
    SpeffzUtils.getCornerStickerColor('a', correctlyScrambledCube);

    return checkCornerStickers(cornerStickers, misscrambledCube, correctlyScrambledCube);
  }

  @Override
  public String toString() {
    return "UD Corners Check: " + getMissRate();
  }
}

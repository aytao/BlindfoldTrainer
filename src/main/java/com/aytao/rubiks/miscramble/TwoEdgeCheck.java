package com.aytao.rubiks.miscramble;

import java.util.HashSet;
import java.util.Set;

import com.aytao.rubiks.cube.Cube;
import com.aytao.rubiks.cube.SpeffzUtils;

public class TwoEdgeCheck extends ScrambleChecker {

  private static final Set<Character> edgeStickers;

  static {
    char[] edgeStickersArray = new char[] { 'b', 'c', };

    edgeStickers = new HashSet<>();
    for (char c : edgeStickersArray) {
      edgeStickers.add(c);
    }
  }

  @Override
  public boolean checkMisscramble(Cube misscrambledCube, Cube correctlyScrambledCube) {
    SpeffzUtils.getEdgeStickerColor('a', correctlyScrambledCube);

    return checkEdgeStickers(edgeStickers, misscrambledCube, correctlyScrambledCube);
  }

  @Override
  public String toString() {
    return "Two Edges Check: " + getMissRate();
  }
}

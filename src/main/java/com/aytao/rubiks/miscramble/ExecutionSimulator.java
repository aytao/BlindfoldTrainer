package com.aytao.rubiks.miscramble;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aytao.rubiks.cube.Move;
import com.aytao.rubiks.cube.Sequence;

public class ExecutionSimulator {

  private static final Map<Move, Move[]> relatedFaceMoves;
  public static final double DEFAULT_ERROR_RATE = 0.05;

  static {
    Move[][] faceMoves = {
        { Move.U, Move.U2, Move.Up },
        { Move.L, Move.L2, Move.Lp },
        { Move.F, Move.F2, Move.Fp },
        { Move.R, Move.R2, Move.Rp },
        { Move.B, Move.B2, Move.Bp },
        { Move.D, Move.D2, Move.Dp }
    };

    relatedFaceMoves = new HashMap<>();

    for (Move[] face : faceMoves) {
      for (int i = 0; i < face.length; i++) {
        relatedFaceMoves.put(face[i], copyWithout(face, i));
      }
    }
  }

  private static Move[] copyWithout(Move[] moves, int idx) {
    Move[] otherMoves = new Move[moves.length - 1];
    for (int i = 0; i < otherMoves.length; i++) {
      if (i >= idx) {
        otherMoves[i] = moves[i + 1];
      } else {
        otherMoves[i] = moves[i];
      }
    }
    return otherMoves;
  }

  private static Move simulateExec(Move move, double errorRate) {
    if (Math.random() > errorRate) {
      return move;
    }

    Move[] otherMoves = relatedFaceMoves.get(move);

    int idx = (int) (Math.random() * otherMoves.length);

    return otherMoves[idx];
  }

  public static List<Move> simulateExec(List<Move> scramble, double errorRate) {
    List<Move> misscramble = new ArrayList<>();

    for (Move move : scramble) {
      misscramble.add(simulateExec(move, errorRate));
    }

    return misscramble;
  }

  public static List<Move> simulateExec(List<Move> scramble) {
    return simulateExec(scramble, DEFAULT_ERROR_RATE);
  }

  public static void main(String[] args) {
    List<Move> scramble = Sequence.getScramble();
    List<Move> misscramble = simulateExec(scramble, DEFAULT_ERROR_RATE);

    System.out.println(scramble);
    System.out.println(misscramble);
  }
}

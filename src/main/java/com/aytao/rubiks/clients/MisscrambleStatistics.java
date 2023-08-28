package com.aytao.rubiks.clients;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.aytao.rubiks.cube.Move;
import com.aytao.rubiks.cube.Sequence;
import com.aytao.rubiks.miscramble.ExecutionSimulator;
import com.aytao.rubiks.miscramble.FullCheck;
import com.aytao.rubiks.miscramble.NoCheck;
import com.aytao.rubiks.miscramble.ScrambleChecker;
import com.aytao.rubiks.miscramble.TwoEdgeCheck;
import com.aytao.rubiks.miscramble.UDCorners;
import com.aytao.rubiks.miscramble.UDFaceCheck;
import com.aytao.rubiks.miscramble.UFFaceCheck;
import com.aytao.rubiks.miscramble.UFaceCheck;

public class MisscrambleStatistics {

  public static void evalScramble(Set<ScrambleChecker> checkers,
      List<Move> misscramble, List<Move> scramble) {
    for (ScrambleChecker checker : checkers) {
      checker.checkMisscramble(misscramble, scramble);
    }
  }

  public static void main(String[] args) {
    int n = args.length > 0 ? Integer.parseInt(args[0]) : 100;
    Set<ScrambleChecker> checkers = new HashSet<>();

    checkers.add(new FullCheck());
    checkers.add(new NoCheck());
    checkers.add(new TwoEdgeCheck());
    checkers.add(new UDCorners());
    checkers.add(new UDFaceCheck());
    checkers.add(new UFaceCheck());
    checkers.add(new UFFaceCheck());

    for (int i = 0; i < n; i++) {
      List<Move> scramble = Sequence.getScramble();
      List<Move> misscramble = ExecutionSimulator.simulateExec(scramble);

      evalScramble(checkers, misscramble, scramble);
    }

    for (ScrambleChecker checker : checkers) {
      System.out.println(checker);
    }
  }
}

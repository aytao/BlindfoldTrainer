package com.aytao.rubiks;

import java.util.ArrayList;
import java.util.Arrays;

import com.aytao.rubiks.cube.Cube;
import com.aytao.rubiks.cube.Move;
import com.aytao.rubiks.cube.Sequence;
import com.aytao.rubiks.pochmann.Pochmann;
import com.aytao.rubiks.pochmann.Pochmann.PochmannSolution;

public class PochmannSolutionTester {
  public static void main(String args[]) {

    ArrayList<Character> edges = new ArrayList<Character>();
    edges.addAll(Arrays.asList('R', 'E', 'A', 'P', 'V', 'T', 'K', 'W', 'C', 'I', 'L', 'F'));
    ArrayList<Character> corners = new ArrayList<Character>();
    corners.addAll(Arrays.asList('P', 'L', 'D', 'N', 'T', 'S', 'M', 'C'));
    PochmannSolution p = new PochmannSolution(edges, corners, false);
    Cube cube = new Cube();
    cube.scrambleOrientation();
    cube.execute(Sequence.getSequence("D2 F' L' B' U L2 D L' D2 F2 R2 F2 R B2 R' B2 R' U2 L' B D' Fw Uw"));
    cube.scrambleOrientation();
    System.out.println(cube.validSolution(p.toSequence()));
  }
}

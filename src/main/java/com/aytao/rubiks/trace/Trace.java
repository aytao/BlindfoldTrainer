package com.aytao.rubiks.trace;

import java.util.ArrayList;

public class Trace {
  ArrayList<Character> edges;
  ArrayList<Character> corners;
  boolean parity;

  public Trace(ArrayList<Character> edges, ArrayList<Character> corners, boolean parity) {
    this.edges = edges;
    this.corners = corners;
    this.parity = parity;
  }

}

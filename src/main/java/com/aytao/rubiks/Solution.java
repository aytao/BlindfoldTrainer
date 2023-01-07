package com.aytao.rubiks;

import java.util.ArrayList;

import com.aytao.rubiks.cube.Move;

public interface Solution {
  public ArrayList<Move> toMoves();
}

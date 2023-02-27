package com.aytao.rubiks.cube;

public enum Face {
  U(Cube.U),
  L(Cube.L),
  F(Cube.F),
  R(Cube.R),
  B(Cube.B),
  D(Cube.D);

  final int faceNum;

  /* Creates a new CubeColor with the specified letter associated */
  Face(int num) {
    this.faceNum = num;
  }
}

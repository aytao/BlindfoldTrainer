package com.aytao.blindfold;

import com.aytao.blindfold.cube.Cube;
import com.aytao.blindfold.cube.Move;

public class Test {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        Cube cube = new Cube();
        cube.execute(Move.L);
        System.out.println(cube);
    }
}

package com.aytao.blindfold;

public class Test {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        Cube cube = new Cube();
        cube.execute(Move.L);
        System.out.println(cube);
    }
}

/* *****************************************************************************
 *  Author:    Andrew Tao
 *
 *  Description:  Takes a scramble and a solution as an input file, and prints
 *                to stdout whether or not the provided solution is valid.
 *
 **************************************************************************** */

package com.aytao.blindfold;

import java.util.ArrayList;

public class Tester {

    /*
     * Handles the case when the cube is not solved after the solution is executed.
     * Prints both the scramble and the
     * full solution to stdout, as well as a drawing of the scrambled and
     * post-solution unsolved cube.
     */
    private static void handleFailure(ArrayList<Move> scramble, ArrayList<Move> solution) {
        Cube cube = new Cube();
        cube.scrambleOrientation();

        System.out.print("Scramble:");
        for (Move move : scramble) {
            System.out.print(" " + move);
            cube.execute(move);
        }
        System.out.println("\nScrambled State:\n" + cube);

        System.out.print("Solution:");
        for (Move move : solution) {
            System.out.print(" " + move);
            cube.execute(move);
        }
        System.out.println("\nResulting State:\n" + cube);
    }

    /*
     * Takes as input the names of a scramble file and a solution file and prints to
     * stdout whether or not
     * the cube is solved after executing the scramble followed by the solution.
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Command line args needed: [scramble file] [solution file]");
            return;
        }

        ArrayList<Move> scramble = Move.getAllMoves(args[0]);
        ArrayList<Move> solution = Move.getAllMoves(args[1]);

        Cube cube = new Cube();
        cube.scrambleOrientation();

        for (Move move : scramble) {
            cube.execute(move);
        }

        for (Move move : solution) {
            cube.execute(move);
        }

        if (cube.isSolved()) {
            System.out.println("Solved!");
        } else {
            System.out.println("Not Solved :(");
            handleFailure(scramble, solution);
        }
    }
}

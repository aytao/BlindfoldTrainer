/* *****************************************************************************
 *  Author:    Andrew Tao
 *
 *  Description:  Provides methods for handling sequences of moves, which are
 *                also commonly (and misrepresentatively) referred to as
 *                "algorithms".
 *
 **************************************************************************** */

package com.aytao.blindfold;

import java.util.ArrayList;
import java.util.Stack;

public class Sequence {    
    /*
     * Parses a given String for a sequence of a moves, and returns the moves as an
     * ArrayList of Moves.
     */
    public static ArrayList<Move> getSequence(String movesStr) {
        ArrayList<Move> moves = new ArrayList<>();
        if (movesStr.equals("")) {
            return moves;
        }

        String[] stringMoves = movesStr.split(" ");

        for (String move : stringMoves) {
            moves.add(Move.move(move));
        }
        return moves;
    }

    /* Returns the inverse of a given sequence of moves */
    public static ArrayList<Move> getInverse(ArrayList<Move> moves) {
        Stack<Move> stack = new Stack<>();
        ArrayList<Move> ret = new ArrayList<>();

        for (Move move : moves) {
            stack.push(move);
        }

        while (!stack.isEmpty()) {
            ret.add(Move.getInverse(stack.pop()));
        }

        return ret;
    }
}

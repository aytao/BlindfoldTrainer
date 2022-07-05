/* *****************************************************************************
 *  Author:    Andrew Tao
 *
 *  Description:  Has methods that allow for mapping a specified piece
 *                (represented as a set of colors) to a HashMap where CubeColors
 *                are then mapped to sticker letters. Used to identify sticker
 *                letters in StickerReport.java.
 *
 **************************************************************************** */

package com.aytao.blindfold.pochmann;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import com.aytao.blindfold.ResourceHandler;
import com.aytao.blindfold.cube.Cube;
import com.aytao.blindfold.cube.CubeColor;

public class PieceToStickers {
    private static final int NUM_EDGE_STICKERS = 24;
    private static final int NUM_CORNER_STICKERS = 24;

    private final static CubeColor[][][] SOLVED_STICKERS;
    private final static HashMap<Set<CubeColor>, HashMap<CubeColor, Character>> edges;
    private final static HashMap<Set<CubeColor>, HashMap<CubeColor, Character>> corners;

    static {
        Cube cube = new Cube();
        cube.scrambleOrientation();
        SOLVED_STICKERS = cube.getStickers();

        edges = getPieceMap("Labels/EdgeLabels.txt",
                "Connections/EdgeConnections.txt", NUM_EDGE_STICKERS);

        corners = getPieceMap("Labels/CornerLabels.txt",
                "Connections/CornerConnections.txt", NUM_CORNER_STICKERS);
    }

    /*
     * Takes the labels for each coordinate labelsFile, the set of connected
     * stickers
     * connectionsFile, and the number of stickers to be consider numStickers.
     * Builds
     * a hashmap which maps
     */
    private static HashMap<Set<CubeColor>, HashMap<CubeColor, Character>> getPieceMap(String labelsFileName,
                                                                                      String connectionsFileName, int numStickers) {
        HashMap<Set<CubeColor>, HashMap<CubeColor, Character>> pieceToStickersMap = new HashMap<>();
        
        CubeColor[] colors = new CubeColor[numStickers];
        try (Scanner labelsIn = new Scanner(ResourceHandler.getFile(labelsFileName), "utf-8")) {
            while (labelsIn.hasNext()) {
                String line = labelsIn.nextLine();
                String[] args = line.split(",");
    
                assert (args.length == 4);
    
                char c = args[0].charAt(0);
                int i = Integer.parseInt(args[1]);
                int j = Integer.parseInt(args[2]);
                int k = Integer.parseInt(args[3]);
                colors[c - 'a'] = SOLVED_STICKERS[i][j][k];
            }
        } catch (Exception e) {
            throw new RuntimeException("Error opening file '" + labelsFileName + "'");
        }

        

        try (Scanner in = new Scanner(ResourceHandler.getFile(connectionsFileName), "utf-8")) {
            while (in.hasNext()) {
                String line = in.nextLine();
                String[] stickers = line.split(",");
                HashMap<CubeColor, Character> map = new HashMap<>();
                for (String s : stickers) {
                    char sticker = s.charAt(0);
                    map.put(colors[sticker - 'a'], sticker);
                }
                pieceToStickersMap.put(map.keySet(), map);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error opening file '" + connectionsFileName + "'");
        }

        return pieceToStickersMap;
    }

    public static HashMap<CubeColor, Character> getEdge(Set<CubeColor> piece) {
        return edges.get(piece);
    }

    public static HashMap<CubeColor, Character> getCorner(Set<CubeColor> piece) {
        return corners.get(piece);
    }

    public static void main(String[] args) {
        HashSet<CubeColor> UF = new HashSet<>();
        UF.add(CubeColor.WHITE);
        UF.add(CubeColor.GREEN);
        HashMap<CubeColor, Character> map = getEdge(UF);
        assert (map != null);
        assert (map.get(CubeColor.WHITE) == 'c');
        assert (map.get(CubeColor.GREEN) == 'i');

        HashSet<CubeColor> UFL = new HashSet<>();
        UFL.add(CubeColor.WHITE);
        UFL.add(CubeColor.GREEN);
        UFL.add(CubeColor.ORANGE);
        HashMap<CubeColor, Character> corner = getCorner(UFL);
        assert (corner != null);
        assert (corner.get(CubeColor.WHITE) == 'd');
        assert (corner.get(CubeColor.GREEN) == 'i');
        assert (corner.get(CubeColor.ORANGE) == 'f');
    }

}

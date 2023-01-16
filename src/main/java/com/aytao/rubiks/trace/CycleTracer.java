package com.aytao.rubiks.trace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import com.aytao.rubiks.ResourceHandler;
import com.aytao.rubiks.cube.Cube;
import com.aytao.rubiks.cube.SpeffzUtils;
import com.aytao.rubiks.utils.Defines;

public class CycleTracer {

  /* Maps a specific sticker to a set of all stickers on the same piece */
  private static final HashMap<Character, HashSet<Character>> relatedEdgeStickers;
  private static final HashMap<Character, HashSet<Character>> relatedCornerStickers;

  static {
    relatedEdgeStickers = getRelatedStickers("Connections/EdgeConnections.txt");
    relatedCornerStickers = getRelatedStickers("Connections/CornerConnections.txt");
  }

  /*
   * Opens the file connectionsFile and retrieves all related stickers for each
   * piece. Returns
   * a HashMap where each sticker maps to a set of all stickers on the same piece
   */
  private static HashMap<Character, HashSet<Character>> getRelatedStickers(String connectionsFileName) {
    HashMap<Character, HashSet<Character>> map = new HashMap<>();

    try (Scanner in = new Scanner(ResourceHandler.getFile(connectionsFileName), "utf-8")) {
      while (in.hasNext()) {
        String line = in.nextLine();
        String[] stickers = line.split(",");

        HashSet<Character> set = new HashSet<>();
        for (String sticker : stickers) {
          map.put(sticker.charAt(0), set);
          set.add(sticker.charAt(0));
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("Error opening file: '" + connectionsFileName + "'");
    }

    return map;
  }

  /* Returns the order in which edge stickers should be swapped with the buffer */
  public static ArrayList<Character> edgeOrder(Cube cube, char edgeBuffer) {
    char[] edgeReport = SpeffzUtils.edgeReport(cube);

    ArrayList<Character> order = new ArrayList<>();
    boolean[] fixed = new boolean[Defines.NUM_SPEFFZ_LETTERS]; // keeps track of already addressed stickers

    // account for flipped and solved edges
    ArrayList<Character> flippedEdges = new ArrayList<>();
    for (int i = 0; i < edgeReport.length; i++) {
      if (fixed[i]) {
        continue;
      }
      HashSet<Character> set = relatedEdgeStickers.get((char) (i + 'a'));

      // correctly oriented edges
      if (edgeReport[i] == i + 'a') {
        for (char c : set) {
          fixed[c - 'a'] = true;
        }
      } // edges in the correct position but flipped - don't flip buffer piece
      else if (set.contains(edgeReport[i]) && !set.contains(edgeBuffer)) {
        flippedEdges.add((char) (i + 'a'));
        flippedEdges.add(edgeReport[i]);

        for (char c : set) {
          fixed[c - 'a'] = true;
        }
      }
    }

    // First cycle (from buffer)
    char location = edgeReport[edgeBuffer - 'a'];
    HashSet<Character> startStickers = relatedEdgeStickers.get(edgeBuffer);
    // Only executes if buffer isn't already in correct position
    if (!fixed[edgeBuffer - 'a']) {
      while (!startStickers.contains(location)) {
        HashSet<Character> set = relatedEdgeStickers.get(location);
        for (char c : set) {
          fixed[c - 'a'] = true;
        }
        order.add(location);
        location = edgeReport[location - 'a'];
      }
      for (char c : startStickers) {
        fixed[c - 'a'] = true;
      }
    }

    // subsequent cycles
    while (true) {
      // find new start, and break out of while loop if all edges are accounted for
      char newStart = 'b';
      int j;
      for (j = 0; j < fixed.length; j++) {
        if (!fixed[j]) {
          newStart = (char) (j + 'a');
          startStickers = relatedEdgeStickers.get(newStart);
          break;
        }
      }
      if (j == fixed.length) {
        break;
      }

      // non-buffer cycles include beginning and ending stickers
      order.add(newStart);
      location = edgeReport[newStart - 'a'];
      // find cycle from new start
      while (!startStickers.contains(location)) {
        HashSet<Character> set = relatedEdgeStickers.get(location);
        for (char c : set) {
          fixed[c - 'a'] = true;
        }
        order.add(location);
        location = edgeReport[location - 'a'];
      }
      for (char c : startStickers) {
        fixed[c - 'a'] = true;
      }
      // non-buffer cycles include beginning and ending stickers
      order.add(location);
    }

    // flipped edges must be fixed after cycles
    order.addAll(flippedEdges);
    return order;
  }

  /*
   * Returns the order in which corner stickers should be swapped with the buffer
   */
  public static ArrayList<Character> cornerOrder(Cube cube, char cornerBuffer) {
    char[] cornerReport = SpeffzUtils.cornerReport(cube);

    ArrayList<Character> order = new ArrayList<>();
    boolean[] fixed = new boolean[Defines.NUM_SPEFFZ_LETTERS]; // keeps track of already addressed stickers

    // account for flipped and solved edges
    ArrayList<Character> flippedCorners = new ArrayList<>();
    for (int i = 0; i < cornerReport.length; i++) {
      if (fixed[i]) {
        continue;
      }
      HashSet<Character> set = relatedCornerStickers.get((char) (i + 'a'));

      // correctly oriented corners
      if (cornerReport[i] == i + 'a') {
        for (char c : set) {
          fixed[c - 'a'] = true;
        }
      } // corners in the correct position but twisted - don't twist buffer
      else if (set.contains(cornerReport[i]) && !set.contains(cornerBuffer)) {
        flippedCorners.add((char) (i + 'a'));
        flippedCorners.add(cornerReport[i]);

        for (char c : set) {
          fixed[c - 'a'] = true;
        }
      }
    }

    // First cycle (from buffer)
    char location = cornerReport[cornerBuffer - 'a'];
    HashSet<Character> startStickers = relatedCornerStickers.get(cornerBuffer);
    // Only executes if buffer isn't already in correct position
    if (!fixed[cornerBuffer - 'a']) {
      while (!startStickers.contains(location)) {
        HashSet<Character> set = relatedCornerStickers.get(location);
        for (char c : set) {
          fixed[c - 'a'] = true;
        }
        order.add(location);
        location = cornerReport[location - 'a'];
      }
      for (char c : startStickers) {
        fixed[c - 'a'] = true;
      }
    }

    // subsequent cycles
    while (true) {
      // find new start, and break out of while loop if all edges are accounted for
      char newStart = 'e';
      int j;
      for (j = 0; j < fixed.length; j++) {
        if (!fixed[j]) {
          newStart = (char) (j + 'a');
          startStickers = relatedCornerStickers.get(newStart);
          break;
        }
      }
      if (j == fixed.length) {
        break;
      }

      // non-buffer cycles include beginning and ending stickers
      order.add(newStart);
      location = cornerReport[newStart - 'a'];
      // find cycle starting from new start
      while (!startStickers.contains(location)) {
        HashSet<Character> set = relatedCornerStickers.get(location);
        for (char c : set) {
          fixed[c - 'a'] = true;
        }
        order.add(location);
        location = cornerReport[location - 'a'];
      }
      for (char c : startStickers) {
        fixed[c - 'a'] = true;
      }
      // non-buffer cycles include beginning and ending stickers
      order.add(location);
    }

    // twisted edges must be fixed after cycles
    order.addAll(flippedCorners);
    return order;
  }

  public Trace getTrace(Cube cube, char edgeBuffer, char cornerBuffer) {
    return new Trace(null, null, false);
  }

}

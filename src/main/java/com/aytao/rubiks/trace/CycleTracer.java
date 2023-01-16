package com.aytao.rubiks.trace;

import java.util.ArrayList;
import java.util.Set;

import com.aytao.rubiks.cube.Cube;
import com.aytao.rubiks.cube.SpeffzUtils;
import com.aytao.rubiks.utils.Defines;

public class CycleTracer {

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
      Set<Character> set = SpeffzUtils.getRelatedEdgeStickersSet((char) (i + 'a'));

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
    Set<Character> startStickers = SpeffzUtils.getRelatedEdgeStickersSet(edgeBuffer);
    // Only executes if buffer isn't already in correct position
    if (!fixed[edgeBuffer - 'a']) {
      while (!startStickers.contains(location)) {
        Set<Character> set = SpeffzUtils.getRelatedEdgeStickersSet(location);
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
          startStickers = SpeffzUtils.getRelatedEdgeStickersSet(newStart);
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
        Set<Character> set = SpeffzUtils.getRelatedEdgeStickersSet(location);
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
      Set<Character> set = SpeffzUtils.getRelatedCornerStickersSet((char) (i + 'a'));

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
    Set<Character> startStickers = SpeffzUtils.getRelatedCornerStickersSet(cornerBuffer);
    // Only executes if buffer isn't already in correct position
    if (!fixed[cornerBuffer - 'a']) {
      while (!startStickers.contains(location)) {
        Set<Character> set = SpeffzUtils.getRelatedCornerStickersSet(location);
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
          startStickers = SpeffzUtils.getRelatedCornerStickersSet(newStart);
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
        Set<Character> set = SpeffzUtils.getRelatedCornerStickersSet(location);
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

  // TODO: Use trace as trace instead of one at a time
  public Trace getTrace(Cube cube, char edgeBuffer, char cornerBuffer) {
    return new Trace(null, null, false);
  }

}

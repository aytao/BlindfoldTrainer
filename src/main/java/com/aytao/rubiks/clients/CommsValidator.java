package com.aytao.rubiks.clients;

import java.util.Set;

import com.aytao.rubiks.comm.Comm;
import com.aytao.rubiks.comm.CommValidity;
import com.aytao.rubiks.cube.Cube;
import com.aytao.rubiks.cube.SpeffzUtils;
import com.aytao.rubiks.threeStyle.CommParser;
import com.aytao.rubiks.threeStyle.ThreeStyle;
import com.aytao.rubiks.utils.Defines;

public class CommsValidator {
  // TODO: Refactor into private method

  public static CommValidity checkEdgeComm(Comm comm, char buffer, char target1, char target2) {
    Cube cube = new Cube();
    cube.execute(comm.toSequence());

    char[] corners = SpeffzUtils.cornerReport(cube);
    if (!isAscendingOrder(corners)) {
      return CommValidity.DISRUPTS_OTHER_TYPE;
    }

    char[] edges = SpeffzUtils.edgeReport(cube);

    if (!otherStickersUndisturbed(edges, buffer, target1, target2)) {
      return CommValidity.DISRUPTS_SAME_TYPE;
    }

    if (!isCycle(edges, buffer, target1, target2)) {
      return CommValidity.INCORRECT_CYCLE;
    }

    return CommValidity.VALID;
  }

  public static CommValidity checkCornerComm(Comm comm, char buffer, char target1, char target2) {
    Cube cube = new Cube();
    cube.execute(comm.toSequence());

    char[] edges = SpeffzUtils.edgeReport(cube);
    if (!isAscendingOrder(edges)) {
      return CommValidity.DISRUPTS_OTHER_TYPE;
    }

    char[] corners = SpeffzUtils.cornerReport(cube);

    if (!otherStickersUndisturbed(corners, buffer, target1, target2)) {
      return CommValidity.DISRUPTS_SAME_TYPE;
    }

    if (!isCycle(corners, buffer, target1, target2)) {
      return CommValidity.INCORRECT_CYCLE;
    }

    return CommValidity.VALID;
  }

  private static boolean otherStickersUndisturbed(char[] report, char buffer, char target1, char target2) {
    Set<Character> ignore = SpeffzUtils.getRelatedEdgeStickersSet(buffer);
    ignore.addAll(SpeffzUtils.getRelatedEdgeStickersSet(target1));
    ignore.addAll(SpeffzUtils.getRelatedEdgeStickersSet(target2));

    for (int i = 0; i < Defines.NUM_SPEFFZ_LETTERS; i++) {
      if (ignore.contains((char) ('a' + i))) {
        continue;
      }

      if (report[i] != i + 'a') {
        return false;
      }
    }

    return true;
  }

  private static boolean isReplaced(char[] report, char source, char target) {
    return report[target - 'a'] == source;
  }

  private static boolean isCycle(char[] report, char b, char t1, char t2) {
    return isReplaced(report, b, t1) &&
        isReplaced(report, t1, t2) &&
        isReplaced(report, t2, b);
  }

  private static boolean isAscendingOrder(char[] report) {
    for (int i = 0; i < report.length; i++) {
      if (report[i] != 'a' + i) {
        return false;
      }
    }

    return true;
  }

  public static void main(String[] args) {
    char t1 = 'b';
    char t2 = 'f';
    Comm comm = CommParser.getEdgeComm(t1, t2);
    System.out.println(comm);
    System.out.println(checkEdgeComm(comm, ThreeStyle.EDGE_BUFFER, t1, t2));
  }
}

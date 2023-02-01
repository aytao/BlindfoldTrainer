package com.aytao.rubiks.comm;

public enum CommValidity {
  UNBALANCED, ILLEGAL_FORMATTING, ILLEGAL_MOVE,
  DISRUPTS_OTHER_TYPE, DISRUPTS_SAME_TYPE,
  INCORRECT_CYCLE,
  VALID;

  public static boolean isValid(CommValidity cv) {
    return cv == VALID;
  }
}

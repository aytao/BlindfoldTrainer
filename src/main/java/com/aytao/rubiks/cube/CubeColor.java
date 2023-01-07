/* *****************************************************************************
 *  Author:    Andrew Tao
 *
 *  Description:  An enum representing all six colors of stickers in the Cube
 *                class.
 *
 **************************************************************************** */

package com.aytao.rubiks.cube;

public enum CubeColor {
  WHITE("W"),
  YELLOW("Y"),
  RED("R"),
  ORANGE("O"),
  BLUE("B"),
  GREEN("G");

  // letter associated with this enum's color
  private final String letterLabel;

  /* Creates a new CubeColor with the specified letter associated */
  CubeColor(String letterLabel) {
    this.letterLabel = letterLabel;
  }

  /* Returns the associated letter as a String representation of a color */
  public String toString() {
    return letterLabel;
  }
}

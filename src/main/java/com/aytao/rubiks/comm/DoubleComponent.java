/* *****************************************************************************
 *  Author:    Andrew Tao
 *
 *  Description:  Represents a double component. A double is of the form (A)2,
 *                where A can be another nested component (but in practice is
 *                just a sequential component). Doubles are executed as A A.
 *
 **************************************************************************** */

package com.aytao.rubiks.comm;

import java.util.ArrayList;

import com.aytao.rubiks.cube.Move;

public class DoubleComponent extends Component {
  private final Component nestedComponent;

  DoubleComponent(Component nestedComponent) {
    super();
    this.nestedComponent = nestedComponent;
  }

  @Override
  ArrayList<Move> toSequence() {
    ArrayList<Move> ret = new ArrayList<>();

    ArrayList<Move> nestedComponentSequence = nestedComponent.toSequence();

    ret.addAll(nestedComponentSequence);
    ret.addAll(nestedComponentSequence);

    return ret;
  }
}

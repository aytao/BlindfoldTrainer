/* *****************************************************************************
 *  Author:    Andrew Tao
 *
 *  Description:  Represents a conjugate. A conjugate is of the form [A : B],
 *                where A is a setup sequence and B can be another nested
 *                component. Conjugates are executed as A B A'.
 *
 **************************************************************************** */

package com.aytao.rubiks.comm;

import java.util.ArrayList;

import com.aytao.rubiks.cube.Move;
import com.aytao.rubiks.cube.Sequence;

class ConjugateComponent extends Component {
  private final Component setup;
  private final Component nestedComponent;

  ConjugateComponent(Component setup, Component nestedComponent) {
    super();
    this.setup = setup;
    this.nestedComponent = nestedComponent;
  }

  @Override
  ArrayList<Move> toSequence() {
    ArrayList<Move> ret = new ArrayList<>();

    ArrayList<Move> setupSequence = setup.toSequence();

    ret.addAll(setupSequence);
    ret.addAll(nestedComponent.toSequence());
    ret.addAll(Sequence.getInverse(setupSequence));

    return ret;
  }

}

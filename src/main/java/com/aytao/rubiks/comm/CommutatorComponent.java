/* *****************************************************************************
 *  Author:    Andrew Tao
 *
 *  Description:  Represents a commutator component. A commutator component has
 *                the form [A, B], where A and B can be other nested components.
 *                Commutator components are executed as A B A' B'.
 *
 **************************************************************************** */

package com.aytao.rubiks.comm;

import java.util.ArrayList;

import com.aytao.rubiks.cube.Move;
import com.aytao.rubiks.cube.Sequence;

class CommutatorComponent extends Component {
  private final Component first;
  private final Component second;

  CommutatorComponent(Component first, Component second) {
    super();
    this.first = first;
    this.second = second;
  }

  @Override
  ArrayList<Move> toSequence() {
    ArrayList<Move> ret = new ArrayList<>();

    ArrayList<Move> firstSequence = first.toSequence();
    ArrayList<Move> secondSequence = second.toSequence();

    ret.addAll(firstSequence);
    ret.addAll(secondSequence);
    ret.addAll(Sequence.getInverse(firstSequence));
    ret.addAll(Sequence.getInverse(secondSequence));

    return ret;
  }

}

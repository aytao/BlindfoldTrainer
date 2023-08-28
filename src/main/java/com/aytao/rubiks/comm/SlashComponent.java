/* *****************************************************************************
 *  Author:    Andrew Tao
 *
 *  Description:  Represents a slash component. A slash is of the form A / B,
 *                executed as A B A2 B' A.
 *
 **************************************************************************** */

package com.aytao.rubiks.comm;

import java.util.ArrayList;

import com.aytao.rubiks.cube.Move;
import com.aytao.rubiks.cube.Sequence;

class SlashComponent extends Component {
  private final Component setup;
  private final Component slice;

  SlashComponent(Component setup, Component slice) {
    super();
    this.setup = setup;
    this.slice = slice;
  }

  @Override
  ArrayList<Move> toSequence() {
    ArrayList<Move> ret = new ArrayList<>();

    ArrayList<Move> setupSequence = setup.toSequence();
    ArrayList<Move> sliceSequence = slice.toSequence();

    ret.addAll(setupSequence);
    ret.addAll(sliceSequence);
    ret.addAll(setupSequence);
    ret.addAll(setupSequence);
    ret.addAll(Sequence.getInverse(sliceSequence));
    ret.addAll(setupSequence);

    return ret;
  }

}
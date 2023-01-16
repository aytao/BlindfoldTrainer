/* *****************************************************************************
 *  Author:    Andrew Tao
 *
 *  Description:  Represents a purely string sequence, with no nested
 *                components.
 *
 **************************************************************************** */

package com.aytao.rubiks.comm;

import java.util.ArrayList;

import com.aytao.rubiks.cube.Move;
import com.aytao.rubiks.cube.Sequence;

class SequenceComponent extends Component {
  private ArrayList<Move> sequence;

  SequenceComponent(String string) {
    super();
    sequence = Sequence.getSequence(string);
  }

  @Override
  ArrayList<Move> toSequence() {
    return new ArrayList<>(sequence);
  }

}

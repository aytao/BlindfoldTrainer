/* *****************************************************************************
 *  Author:    Andrew Tao
 *
 *  Description:  Represents a component that contains multiple nested
 *                components at the same level. For example, the flipping
 *                algorithm for UF-RF is [R' E R : U'] [R E2 R' : U], which has
 *                two commutators on the same level. The multiple components are
 *                executed sequentially in the order that they appear.
 *
 **************************************************************************** */

package com.aytao.rubiks.comm;

import java.util.ArrayList;
import java.util.List;

import com.aytao.rubiks.cube.Move;

class MultiComponent extends Component {
  private List<Component> allComponents;

  MultiComponent(List<Component> allComponents) {
    super();
    this.allComponents = new ArrayList<>(allComponents);
  }

  @Override
  ArrayList<Move> toSequence() {
    ArrayList<Move> ret = new ArrayList<>();

    for (Component comp : allComponents) {
      ret.addAll(comp.toSequence());
    }

    return ret;
  }
}

/* *****************************************************************************
 *  Author:    Andrew Tao
 *
 *  Description:  Represents the base abstract class for all comm components.
 *                Note that, while most blind algs are all colloquially referred
 *                to as "commutators", commutators are a specific component. For
 *                clarity's sake, this distinction will be made by exclusively
 *                using "comm" to represent the overall alg, and "commutator
 *                component" to represent the component.
 *
 **************************************************************************** */

package com.aytao.rubiks.comm;

import java.util.ArrayList;

import com.aytao.rubiks.cube.Move;

abstract class Component {

  abstract ArrayList<Move> toSequence();

}

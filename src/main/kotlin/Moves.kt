package fr.codestory.elevator.hodor

import java.util.SortedMap
import com.google.common.collect.Lists
import java.util.Collections
import com.google.common.collect.Maps
import fr.codestory.elevator.Elevator.Side
import java.util.TreeMap


data class CallRequest(val floor: Int = 0, val side: Side? = Side.DOWN) {

}

data class GoRequest( val floor : Int) {

}
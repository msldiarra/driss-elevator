

import java.util.SortedMap
import com.google.common.collect.Lists
import java.util.Collections
import com.google.common.collect.Maps
import fr.codestory.elevator.Elevator.Side
import java.util.TreeMap

/*
class Commands (val calls: List<CallRequest> = Collections.emptyList() , val destinations: List<DestinationRequest> = Collections.emptyList(),
            val lowestFloor : Int = 0, val higherFloor : Int = 19){

    val moves : SortedMap<Int, CallRequest>? = null

    public fun wait(): Int { return 0 }
    public fun travel(): Int {return 0 }

}
*/

/*class Shifter(private var destinations: SortedMap<Int, SortedMap<Int, Side>>? = Maps.newTreeMap<Int, SortedMap<Int, Side>>()) {

    public fun optimize(calls : List<Int> ?) : SortedMap<Int, SortedMap<Int, Side>> ? {

        val only_command = Maps.newTreeMap<Int, Side>()
        only_command!!.put(0, Side.UP)
        only_command!!.put(1, Side.UP)
        only_command!!.put(2, Side.UP)

        destinations!!.put(0, only_command)

        return destinations;
    }
}


class Move(){

    final val UP : String = "UP"
    final val DOWN : String = "DOWN"
    final val NOTHING : String = "NOTHING"
    final val OPEN : String = "OPEN"
    final val CLOSED : String = "CLOSED"

    class Doors() {

        fun open() {}
        fun close() {}

        fun areOpen() : Boolean  { return false}
        fun areClosed() : Boolean  { return false}
    }
}*/


data class CallRequest(val floor: Int = 0, val side: Side? = Side.DOWN) {

}

data class GoRequest( val floor : Int = 19) {

}
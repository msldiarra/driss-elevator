package fr.codestory.elevator.hodor

import fr.codestory.elevator.Elevator
import java.util.SortedMap
import fr.codestory.elevator.Elevator.Side
import com.google.common.collect.Maps
import java.util.LinkedHashMap
import java.util.TreeMap
import com.google.common.collect.Lists
import java.util.ArrayList
import java.util.Collections
import com.google.common.collect.Iterables
import com.google.common.base.Predicate
import java.util.HashMap
import java.util.List
import fr.codestory.elevator.BuildingDimension
import fr.codestory.elevator.hodor.HodorElevator.Command
import java.util.Iterator

class HodorElevator(public var currentFloor: Int = 0, val dimension: BuildingDimension = BuildingDimension(0, 19), val cabinSize: Int = 2) : Elevator {

    public var calls: ArrayList<CallRequest> = ArrayList<CallRequest>()
    public var gos: ArrayList<GoRequest> = ArrayList<GoRequest>()
    public var commands: Array<Command>? = null
    val moves: LinkedHashMap<Int, ArrayList<Command>?> =  LinkedHashMap<Int, ArrayList<Command>?>()
    var way = Going.UP
    var usersInCabin = 0;
    var userToEnterOnRoad = 0;

    override fun reset() {
        calls = ArrayList<CallRequest>()
        gos = ArrayList<GoRequest>()
        currentFloor = 0
        commands = Array<Command>(1, { Command.NOTHING } )
        moves.clear()
    }

    override fun go(to: Int) {
        when {
            !(gos.any { g -> to == g.floor } || calls.any { c -> to == c.floor }) -> {
                gos.add(GoRequest(to))


                if(usersInCabin >= cabinSize) {
                    // remove from moves all non-go commands
                    val callToRemove = calls.iterator().dropWhile { c -> gos.contains(c.floor) }
                    var it = callToRemove.iterator()
                    while (it.hasNext()){
                        val call = it.next()
                        if(moves.get(call.floor)?.get(0) !=  Command.CLOSE) // if doors are closing do not remove door close
                            moves.remove(call.floor)
                    }

                }

                commands = commandsForGo(to)

                if(commands != null ) {
                    moves.put(to, commands?.toList() as ArrayList<Command>?)
                }
            }
        }
    }

    override fun call(at: Int, side: Elevator.Side?) {

        when {

            !(calls.any { c -> at == c.floor } || gos.any { g -> at == g.floor }) -> {

                calls.add(CallRequest(at, side))

                if(( someoneLivingBeforeCalledFloor(at, side) && usersInCabin + userToEnterOnRoad <= cabinSize ) || usersInCabin < cabinSize) {

                    commands = commandsForCall(at, side)
                    // There is no command for floor if user is taken in current destination
                    if(commands != null ) {
                        moves.put(at , commands?.toList() as ArrayList<Command>)
                    }
                }

            }
        }
    }

    private fun someoneLivingBeforeCalledFloor(at: Int, side: Side?): Boolean{

        return (moves.entrySet().iterator().hasNext()) &&
          ((way == Going.UP &&  side ==  Side.UP && at >= getNextDestinationFloor(moves))
          ||
          (way == Going.DOWN &&  side ==  Side.DOWN && at <= getNextDestinationFloor(moves)))
    }

    private fun nextCommandInSameWay(at: Int, side: Side?) : Boolean {

        return (!moves.entrySet().iterator().drop(1).empty) && (((moves.entrySet().iterator().drop(1).first?.key as Int) > at && side == Side.UP )
                ||
               ((moves.entrySet().iterator().drop(1).first?.key as Int) < at && side == Side.DOWN))
    }

    private fun noNextCommand() : Boolean {
        return moves.entrySet().iterator().drop(1) == null;
    }

    override fun userHasEntered() {

        usersInCabin++

    }

    override fun userHasExited() {

        usersInCabin--

        Iterables.removeIf(gos, { g -> g?.floor == currentFloor})

        if(!calls.isEmpty() && moves.get(calls.get(0).floor) == null) {
            commands = commandsForCall(calls.get(0).floor, calls.get(0).side)
            if(commands != null ) {
                moves.put(calls.get(0).floor , commands?.toList() as ArrayList<Command>?)
            }
        }

    }

    override fun nextMove(): String? {

        var command =  Command.NOTHING.name()

        if(moves.isEmpty()) {
            return command
        }

        val key = moves.entrySet().iterator().next().getKey();
        val commands = moves.get(key)

        if(commands != null && commands.size != 0) {

            command = commands.get(0).name()
            commands.remove(0)

            if(command == Command.UP.name()) {
                way = Going.UP
                currentFloor++
            }
            if(command == Command.DOWN.name()) {
                way = Going.DOWN
                currentFloor--
            }

            if(commands.size == 0) {
                moves.remove(key)

                // You should allow calls and go registrations to those floor now
                Iterables.removeIf(calls, { c -> c?.floor == currentFloor})
            }
            else {
                if(command == Command.CLOSE.name()) {
                    //Todo Manage if all users at this floor has entered and we opened and closed the door at current floor then remove registered calls
                    // To know if all users has entered you have to register all calls at floor or count calls at one floor (maybe better)
                    Iterables.removeIf(calls, { c -> c?.floor == currentFloor}) // allow registering calls at this floor again
                    userEnteredOnRoadHasExited()
                }
            }
        }






        return command
    }

    private fun commandsForCall(at: Int, side: Side?) : Array<Command>? {

        var commands : Array<Command>? = null

        val nextDestinationFloor =  getNextDestinationFloor(moves)

        when {

            (side == Side.UP && way == Going.UP && nextDestinationFloor > currentFloor && currentFloor <= at && at <= nextDestinationFloor) -> {

                val floorsToGo = at - currentFloor
                addStopBefore(at, nextDestinationFloor, floorsToGo)
                userToEnterOnRoad++
            }

            /*(side == Side.UP && way == Going.UP && nextDestinationFloor > currentFloor && currentFloor <= at && at >= nextDestinationFloor) -> {

                val floorsToGo = at - nextDestinationFloor
                addStopAfter(at, nextDestinationFloor, floorsToGo, Command.UP)
                userToEnterOnRoad++
            }*/

            (side == Side.DOWN && way == Going.DOWN && nextDestinationFloor < currentFloor && currentFloor >= at && at >= nextDestinationFloor  ) -> {

                val floorsToGo = currentFloor - at
                addStopBefore(at, nextDestinationFloor ,floorsToGo)
                userToEnterOnRoad++
            }

            /*(side == Side.DOWN && way == Going.DOWN && nextDestinationFloor < currentFloor && currentFloor >= at && at <= nextDestinationFloor  ) -> {

                val floorsToGo = currentFloor - at
                addStopAfter(at, nextDestinationFloor ,floorsToGo, Command.DOWN)
                userToEnterOnRoad++
            }*/

            else -> {

                var lastFloor = currentFloor

                if(!moves.isEmpty()) {
                    val keys = moves.keySet();
                    lastFloor = keys.reverse().first()
                }

                commands = Commands(lastFloor).call(at, side)
            }
        }

        return commands

    }

    private fun getNextDestinationFloor(moves : LinkedHashMap<Int, ArrayList<Command>?>) : Int {

        var nextDestinationFloor =  0

        if(!moves.isEmpty()) {

            val it = moves.entrySet().iterator()
            // if door is opened next destination it key 1 in map
            val nextCommand = it.next().getValue()?.get(0) as Command

            if(nextCommand ==  Command.CLOSE && moves.size() >= 2) {
                nextDestinationFloor = it.next().getKey()
            }
            else {
                // we take next commands as nextDestination key

                nextDestinationFloor = moves.entrySet().iterator().next().getKey()
            }


        }

        return nextDestinationFloor as Int
    }

    private fun commandsForGo(to: Int) : Array<Command>?{

        var commands : Array<Command>? = null

        val nextDestinationFloor =  getNextDestinationFloor(moves)

        when {

            (way == Going.UP && nextDestinationFloor > currentFloor && currentFloor <= to && to <= nextDestinationFloor) -> {

                val floorsToGo = to - currentFloor
                addStopBefore(to, nextDestinationFloor, floorsToGo)
                userToEnterOnRoad++
            }

           /* (way == Going.UP && nextDestinationFloor > currentFloor && currentFloor <= to && to >= nextDestinationFloor) -> {

                val floorsToGo = to - nextDestinationFloor
                addStopAfter(to, nextDestinationFloor, floorsToGo, Command.UP)
                userToEnterOnRoad++
            }*/

            (way == Going.DOWN && nextDestinationFloor < currentFloor && currentFloor >= to && to >= nextDestinationFloor  ) -> {

                val floorsToGo = currentFloor - to
                addStopBefore(to, nextDestinationFloor ,floorsToGo)
                userToEnterOnRoad++
            }

            /*(way == Going.DOWN && nextDestinationFloor < currentFloor && currentFloor >= to && to <= nextDestinationFloor  ) -> {

                val floorsToGo = currentFloor - to
                addStopAfter(to, nextDestinationFloor ,floorsToGo, Command.DOWN)
                userToEnterOnRoad++
            }
*/

            else -> {

                var lastFloor = currentFloor

                if(!moves.isEmpty()) {
                    val keys = moves.keySet();
                    lastFloor = keys.reverse().first()
                }

                commands = Commands(lastFloor).go(to)
            }
        }

        return commands
    }

    private fun addStopBefore(at: Int, nextDestinationFloor: Int, remainingFloorsToGo: Int){

        // whe should calculate floors to go before introducing OPEN/CLOSE
        val stops = moves.get(nextDestinationFloor)?.count { c -> c.name() ==  Command.OPEN.name() } as Int

        moves.get(nextDestinationFloor)?.add(remainingFloorsToGo + (stops - 1) * 2, Command.OPEN)
        moves.get(nextDestinationFloor)?.add(remainingFloorsToGo + (stops - 1) * 2 +1 , Command.CLOSE)

    }

    private fun addStopAfter(at: Int, nextDestinationFloor: Int, remainingFloorsToGo: Int, way: Command){

        val nextKey = moves.get(nextDestinationFloor)?.count() as Int;
        val commands = Array<Command>(remainingFloorsToGo, {way})

        // whe should calculate floors to go before introducing OPEN/CLOSE
        val stops = moves.get(nextDestinationFloor)?.count { c -> c.name() ==  Command.OPEN.name()} as Int
        moves.get(nextDestinationFloor)?.addAll(nextKey,commands.toList() as ArrayList<Command>)
        moves.get(nextDestinationFloor)?.add(nextKey + remainingFloorsToGo, Command.OPEN)
        moves.get(nextDestinationFloor)?.add(nextKey + remainingFloorsToGo+1, Command.CLOSE)

        // remove UPs in next commands in Move object if exists
        if(!moves.entrySet().iterator().drop(1).empty){
            val nextFloor = moves.entrySet().iterator().drop(1).first?.key as Int
            moves.get(nextFloor)?.subList(0, remainingFloorsToGo)?.clear()
        }

    }

    private fun userEnteredOnRoadHasExited(){
        userToEnterOnRoad--
    }

    enum class Command {
        UP
        DOWN
        CLOSE
        OPEN
        NOTHING
    }

    enum class Going {
        UP
        DOWN
        NOWHERE
    }

}



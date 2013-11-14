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

class HodorElevator(public var currentFloor: Int = 0, val dimension: BuildingDimension = BuildingDimension(0, 19), val cabinSize: Int = 3) : Elevator {

    public var calls: ArrayList<CallRequest> = ArrayList<CallRequest>()
    public var gos: ArrayList<GoRequest> = ArrayList<GoRequest>()
    public var commands: Array<Command>? = null
    val moves: LinkedHashMap<Int, ArrayList<Command>?> =  LinkedHashMap<Int, ArrayList<Command>?>()
    var way = Going.NOWHERE

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
                commandsForGo(to)
                moves.put(to, commands?.toList() as ArrayList<Command>)
            }
        }
    }

    override fun call(at: Int, side: Elevator.Side?) {

        when {

            !(calls.any { c -> at == c.floor } || gos.any { g -> at == g.floor })-> {
                calls.add(CallRequest(at, side))
                commands = commandsForCall(at, side)

                // There is no command for floor if user is taken in current destination
                if(commands != null ) {
                    moves.put(at , commands?.toList() as ArrayList<Command>)
                }
            }
        }
    }


    override fun userHasEntered() {

    }
    override fun userHasExited() {

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

            if(commands.size == 0) {
                moves.remove(key)

                // You should allow calls and go registrations to those floor now
                Iterables.removeIf(calls, { c -> c?.floor == currentFloor})
                Iterables.removeIf(gos, { g -> g?.floor == currentFloor})
            }
        }

        if(command == Command.UP.name()) {
            way = Going.UP
            currentFloor++
        }
        if(command == Command.DOWN.name()) {
            way = Going.DOWN
            currentFloor--
        }
        if(command == Command.CLOSE.name()) {
            //Todo Manage if all users at this floor has entered and we opened and closed the door at current floor then remove registered calls
            // To know if all users has entered you have to register all calls at floor or count calls at one floor (maybe better)
            Iterables.removeIf(calls, { c -> c?.floor == currentFloor}) // allow registering calls at this floor again
        }



        return command
    }

    private fun commandsForCall(at: Int,
                                side: Side?) : Array<Command>? {

        var commands : Array<Command>? = null

        var nextDestinationFloor =  0

        if(!moves.isEmpty()) nextDestinationFloor = moves.entrySet().iterator().next().getKey()


        when {

            (side == Side.UP && way == Going.UP && nextDestinationFloor > currentFloor && currentFloor <= at ) -> {

                val floorsToGo = at - currentFloor
                addStop(at, nextDestinationFloor, floorsToGo)
            }

            (side == Side.DOWN && way == Going.DOWN && nextDestinationFloor < currentFloor && currentFloor >= at ) -> {

                val floorsToGo = currentFloor - at
                addStop(at, nextDestinationFloor ,floorsToGo)
            }
            else -> {

                var lastFloor = currentFloor

                if(!moves.isEmpty()) {
                    val keys = moves.keySet();
                    lastFloor = keys.reverse().first()
                }

                val way = at - lastFloor

                if(way == 0) {
                    commands = Array<Command>(2 , { Command.NOTHING } )
                }

                if(way > 0) {
                    commands = Array<Command>(Math.abs(way) + 2, { Command.UP } )
                }

                if(way < 0) {
                    commands = Array<Command>(Math.abs(way) + 2, { Command.DOWN  } )
                }

                commands?.set( Math.abs(way), Command.OPEN)
                commands?.set( Math.abs(way) + 1, Command.CLOSE)
            }
        }

        return commands

    }

    private fun commandsForGo(to: Int){

        var lastFloor = currentFloor

        if(!moves.isEmpty()) {
            val keys = moves.keySet();
            lastFloor = keys.reverse().first()
        }

        val way = to - lastFloor

        if(way == 0) {
            commands = Array<Command>(2, { Command.NOTHING } )
        }

        if(way > 0) {
            commands = Array<Command>(Math.abs(way) + 2, { Command.UP } )
        }

        if(way < 0) {
            commands = Array<Command>(Math.abs(way) + 2, { Command.DOWN  } )
        }

        commands?.set( Math.abs(way), Command.OPEN)
        commands?.set( Math.abs(way) + 1, Command.CLOSE)
    }

    private fun addStop(at: Int, nextDestinationFloor: Int, remainingFloorsToGo: Int){

        // Todo : make sure next comman is not OPEN/CLOSE
        // Todo  because gives
        // Are we at current calling floor?
        if (at == currentFloor) {
            // then we should add OPEN/CLOSE Commands
            moves.get(nextDestinationFloor)?.add(0, Command.OPEN)
            moves.get(nextDestinationFloor)?.add(1, Command.CLOSE)
        }
        else {
            // whe should calculate floors to go before introducing OPEN/CLOSE
            val stops = moves.get(nextDestinationFloor)?.count { c -> c.name() ==  Command.OPEN.name()} as Int
            moves.get(nextDestinationFloor)?.add(remainingFloorsToGo + (stops - 1) * 2, Command.OPEN)
            moves.get(nextDestinationFloor)?.add(remainingFloorsToGo + (stops - 1) * 2 +1 , Command.CLOSE)
        }

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



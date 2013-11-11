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

class HodorElevator(public var currentFloor: Int = 0) : Elevator {

    public var calls: ArrayList<CallRequest> = ArrayList<CallRequest>()
    public var gos: ArrayList<GoRequest> = ArrayList<GoRequest>()
    public var commands: Array<Command> = Array<Command>(1, { Command.NOTHING } )
    val moves: LinkedHashMap<Int, ArrayList<Command>> =  LinkedHashMap<Int, ArrayList<Command>>()

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
                moves.put(to, commands.toList() as ArrayList<Command>)
            }
        }
    }

    override fun call(at: Int, side: Elevator.Side?) {

        when {

            !(calls.any { c -> at == c.floor } || gos.any { g -> at == g.floor })-> {
                calls.add(CallRequest(at, side))
                commandsForCall(at)
                moves.put(at , commands.toList() as ArrayList<Command>)
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

            if(commands.size == 0) {
                moves.remove(key)

                // You should allow calls and go registration to those floor now
                Iterables.removeIf(calls, { c -> c?.floor == currentFloor})
                Iterables.removeIf(gos, { g -> g?.floor == currentFloor})
            }
        }

        if(command == Command.UP.name()) currentFloor++
        if(command == Command.DOWN.name()) currentFloor--



        return command
    }

    private fun commandsForCall(at :Int){

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

        commands.set( Math.abs(way), Command.OPEN)
        commands.set( Math.abs(way) + 1, Command.CLOSE)

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

        commands.set( Math.abs(way), Command.OPEN)
        commands.set( Math.abs(way) + 1, Command.CLOSE)
    }

    enum class Command {
        UP
        DOWN
        CLOSE
        OPEN
        NOTHING
    }

}



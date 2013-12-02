package fr.codestory.elevator.hodor

import java.util.HashSet
import fr.codestory.elevator.hodor.Cabin.Door.State
import fr.codestory.elevator.hodor.HodorElevator.Command


class Cabin(val size: Int = 40, var currentFloor: Int = 0, floorToGo: Int = 0, val users: HashSet<User> = hashSetOf()) {

    val door: Door = Door()

    fun currentFloorToUpper(){
        currentFloor++
    }

    fun currentFloorToLower(){
        currentFloor--
    }

    class Door {

        var state = State.CLOSED

        fun open() : Command {
            state = State.OPEN
            return Command.OPEN
        }

        fun close(): Command {
            state = State.CLOSED
            return Command.CLOSE
        }

        enum class State {
            CLOSED
            OPEN
        }
    }
}
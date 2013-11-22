package fr.codestory.elevator.hodor

import fr.codestory.elevator.Elevator
import fr.codestory.elevator.BuildingDimension
import java.util.HashSet
import java.util.ArrayList
import com.google.common.collect.Iterables
import fr.codestory.elevator.hodor.HodorElevator.Command
import fr.codestory.elevator.hodor.Door.State

class SandorElevator(public var currentFloor: Int = 0, val dimension: BuildingDimension = BuildingDimension(0, 24), val cabinSize: Int = 2) : Elevator {

    val users: HashSet<User> = hashSetOf<User>()
    val calls: ArrayList<CallRequest> =  ArrayList<CallRequest>()
    val gos: ArrayList<GoRequest> = ArrayList<GoRequest>()
    val controller = Controller(users)
    val door = Door()
    var floorToGo = currentFloor


    override fun nextMove(): String? {

        users.forEach { u -> u.tick() }

        if(users.isEmpty() && door.state == State.OPEN) return door.close().name()

        if(users.isEmpty() && door.state == State.CLOSED) return  Command.NOTHING.name()


        floorToGo = controller.compute(currentFloor)

        return  when {

            door.state == State.OPEN ->   door.close().name()

            currentFloor.isAbove(floorToGo) -> { currentFloor--;Command.DOWN.name() }

            currentFloor.isUnder(floorToGo) -> { currentFloor++;  Command.UP.name() }

            currentFloor.isSameAs(floorToGo) ->   door.open().name()

            else ->  Command.NOTHING.name()
        }

    }


    override fun reset() {
        throw UnsupportedOperationException()
    }


    override fun go(to: Int) {
        gos.add(GoRequest(to))
        users.reverse().first().destinationFloor = to
    }


    override fun call(at: Int, side: Elevator.Side?) {
        calls.add(CallRequest(at, side))
        users.add(User(at))
    }


    override fun userHasEntered() {
        users.dropWhile { u -> u.destinationFloor != 1000 }.sortBy{ u -> u.waitingTicks }.last().state = User.State.TRAVELLING
    }


    override fun userHasExited() {
        Iterables.removeIf(users, { users -> users?.destinationFloor == currentFloor})
    }
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


fun Int.isAbove(callFloor: Int) : Boolean {
    return this > callFloor
}

fun Int.isUnder(callFloor: Int) : Boolean {
    return this < callFloor
}

fun Int.isSameAs(floor: Int) : Boolean {
    return this == floor
}

fun Int.isNot(floor: Int) : Boolean {
    return this == floor
}
package fr.codestory.elevator.driss

import fr.codestory.elevator.Elevator.Side
import fr.codestory.elevator.order.Destinations
import fr.codestory.elevator.order.ElevatorRequest
import fr.codestory.elevator.order.Calls
import java.util.Enumeration
import fr.codestory.elevator.Elevator
import fr.codestory.elevator.BuildingDimension

public class DrissElevator(public var currentFloor: Int = 0, val dimension: BuildingDimension = BuildingDimension(0, 19), val cabinSize: Int = 20) : Elevator {

    val groom = Groom()
    val door = Door()
    val calls: Destinations<Calls> = Destinations.init(Calls.NONE)
    val gos: Destinations<ElevatorRequest> = Destinations.init(ElevatorRequest.NONE)

    var commands = Commands.NONE

    public override fun nextMove(): String {
        if ( door.opened || isSomeoneToTakeOrToLeave() ) return door.toggle {
            calls.reached(currentFloor)
            gos.reached(currentFloor)
        }.name()

        if ( !commands.hasMoreElements()) commands = groom.giveFollowingCommands(currentFloor, calls, gos)

        return updateReachedFloorAfter(commands.nextElement())
    }

    private inline fun updateReachedFloorAfter(chosenCommand: MoveCommand): String {
        when (chosenCommand) {
            MoveCommand.UP -> {
                currentFloor++
            }
            MoveCommand.DOWN -> {
                currentFloor--
            }
            MoveCommand.NOTHING -> {
            }
        }
        return chosenCommand.name()
    }

    private inline fun isSomeoneToTakeOrToLeave() = when {
        commands.isTwoSidesChargingAllowed() -> {
            gos.requestedTo(currentFloor) || calls.at(currentFloor) != Calls.NONE
        }
        else -> {
            gos.requestedTo(currentFloor) || calls.at(currentFloor).going(commands.side) != ElevatorRequest.NONE
        }
    }

    public override fun reset(): Unit {

        calls.clear()
        gos.clear()
        currentFloor = dimension.getLowerFloor()
        commands = Commands.NONE
    }
    public override fun go(to: Int): Unit {
        val timestampedCounter: ElevatorRequest? = gos.at(to)
        if (timestampedCounter == ElevatorRequest.NONE)
        {
            gos.add(to, ElevatorRequest())
        }
        else
        {
            timestampedCounter?.increase()
        }
    }
    public override fun call(at: Int, side: Side?): Unit {
        val callsAtFloor = calls.at(at)
        when(callsAtFloor) {
            Calls.NONE -> calls.add(at, Calls.create(side as Side))
            else -> {
                callsAtFloor.increase(side)
            }
        }
    }
}


class Commands(val side: Side,
               private val commands: Array<MoveCommand>) : Enumeration<MoveCommand> {
    override fun hasMoreElements() = remainingCommands > 0
    override fun nextElement(): MoveCommand {
        if (remainingCommands < 1) return MoveCommand.NOTHING
        val command: MoveCommand = commands.get(commands.size - remainingCommands--)
        return command
    }

    var remainingCommands: Int = commands.size

    public fun isTwoSidesChargingAllowed(): Boolean = remainingCommands < 1 || commands.all { command -> commands[0] != command }

    class object {
        public val NONE: Commands = Commands(Side.UP, array())
    }
}

class Groom {
    public inline fun giveFollowingCommands(currentFloor: Int, calls: Destinations<Calls>, gos: Destinations<ElevatorRequest>): Commands {

        if ((calls.isEmpty()) && gos.isEmpty())
            return Commands.NONE

        val callsAbove = calls.above(currentFloor)
        val callsBelow = calls.below(currentFloor)
        val gosAbove = gos.above(currentFloor)
        val gosBelow = gos.below(currentFloor)

        return when {
            gos.isEmpty() && numberOf(callsBelow) > numberOf(callsAbove) -> {

                val distance = callsBelow.distanceToNearestFloorFrom(currentFloor)
                Commands(Side.DOWN, MoveCommand.DOWN.times(distance))
            }

            gos.isEmpty() && numberOf(callsBelow) <= numberOf(callsAbove) -> {
                val distance: Int = callsAbove.distanceToNearestFloorFrom(currentFloor)
                Commands(Side.UP, MoveCommand.UP.times(distance))
            }

            sumOf(gosAbove) > sumOf(gosBelow) -> {
                val mainDirection = Side.UP
                val distance: Int = gosAbove.distanceToFarthestFloorFrom(currentFloor)
                when {
                    calls.at(currentFloor - 1).going(mainDirection) != ElevatorRequest.NONE && distance > 1 -> {
                        Commands(mainDirection, Array(distance + 2, invertFirst(MoveCommand.UP)))
                    }
                    else -> {
                        Commands(mainDirection, MoveCommand.UP.times(distance))
                    }
                }
            }
            else -> {
                val mainDirection = Side.DOWN
                val distance: Int = gosBelow.distanceToFarthestFloorFrom(currentFloor)
                when {
                    calls.at(currentFloor + 1).going(mainDirection) != ElevatorRequest.NONE && distance > 1 -> {
                        Commands(mainDirection, Array(distance + 2, invertFirst(MoveCommand.DOWN)))
                    }
                    else -> {
                        Commands(mainDirection, MoveCommand.DOWN.times(distance))
                    }
                }
            }
        }
    }

    private inline fun numberOf(destinations: Destinations<Calls>) = destinations.fold(0) {
        number, calls ->
        number + calls.going(Side.UP).number + calls.going(Side.DOWN).number
    }

    private inline fun sumOf(destinations: Iterable<ElevatorRequest>) =
            destinations.fold(0) { number, elevatorRequest -> number + elevatorRequest.number }


    inline private fun invertFirst(command: MoveCommand) = {(i: Int) ->
        when {
            i == 0 -> command.switch()
            else -> command
        }
    }
}


class Door(var opened: Boolean = false){

    enum class Command{ OPEN  CLOSE
    }

    inline fun toggle(onOpen: (() -> Unit)?) = if (opened) {
        opened = false
        Command.CLOSE
    }
    else {
        opened = true
        onOpen?.invoke()
        Command.OPEN
    }
}

enum class MoveCommand {

    UP
    DOWN
    NOTHING
    fun times(number: Int): Array<MoveCommand> {
        return Array(number) { this }
    }


    inline fun switch(): MoveCommand = when(this) {
        MoveCommand.DOWN -> MoveCommand.UP
        MoveCommand.UP -> MoveCommand.DOWN
        else -> MoveCommand.NOTHING }
}



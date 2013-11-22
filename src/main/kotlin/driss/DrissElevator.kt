package driss

import fr.codestory.elevator.Elevator.Side
import fr.codestory.elevator.BuildingDimension
import fr.codestory.elevator.Cabin
import fr.codestory.elevator.Elevator

import java.lang.Math.*

public class DrissElevator(public var currentFloor: Int = 0, val dimension: BuildingDimension = BuildingDimension(0, 19), val cabin: Cabin = Cabin(20)) : Elevator {

    val groom = this.Groom()
    val door = Door()

    val calls: Signals<Calls> = signals(dimension, Calls.NONE)
    val gos: Signals<ElevatorRequest> = signals(dimension, ElevatorRequest.NONE)

    public override fun nextMove(): String =
            when {
                door.opened || groom.wantsTheDoorToOpen() -> {
                    door.toggle {
                        calls.reached(currentFloor)
                        gos.reached(currentFloor)
                    }.name()
                }
                else -> {
                    updateReachedFloorAfter(groom.giveNextMoveCommand())
                }
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

    public override fun reset(): Unit {

        calls.clear()
        gos.clear()
        currentFloor = dimension.getLowerFloor()
    }
    public override fun go(floor: Int): Unit {
        val timestampedCounter: ElevatorRequest? = gos.at(floor)
        if (timestampedCounter == ElevatorRequest.NONE)
        {
            gos.add(floor, ElevatorRequest())
        }
        else
        {
            timestampedCounter?.increase()
        }
    }
    public override fun call(floor: Int, side: Side?): Unit {
        val callsAtFloor = calls.at(floor)
        when(callsAtFloor) {
            Calls.NONE -> calls.add(floor, calls(side as Side))
            else -> {
                callsAtFloor.increase(side)
            }
        }
    }


    override fun userHasEntered() {
        cabin.userHasEntered()
    }
    override fun userHasExited() {
        cabin.userHasExited()
    }


    inner class Groom {

        private var commands = Commands.NONE

        public inline fun giveNextMoveCommand(): MoveCommand {
            if ( !commands.hasMoreElements()) commands = groom.giveFollowingCommands(currentFloor, calls, gos)
            return commands.nextElement()
        }

        public inline fun wantsTheDoorToOpen(): Boolean = when {
            gos.requestedAt(currentFloor) -> {
                true
            }
            commands.isTwoSidesChargingAllowed() -> {
                (cabin.canAcceptSomeone() && calls.at(currentFloor) != Calls.NONE)
            }
            else -> {
                (cabin.canAcceptSomeone() && calls.at(currentFloor).going(commands.side) != ElevatorRequest.NONE)
            }
        }

        public inline fun giveFollowingCommands(currentFloor: Int, calls: Signals<Calls>, gos: Signals<ElevatorRequest>): Commands {

            if ((calls.isEmpty()) && gos.isEmpty())
                return Commands.NONE

            val gosAbove = gos.above(currentFloor)
            val gosBelow = gos.below(currentFloor)

            return when {
                gos.isEmpty() -> {

                    val nearestFloor = calls.nearestFloorFrom(currentFloor)

                    if (currentFloor < nearestFloor)
                        Commands(Side.UP, MoveCommand.UP.times(abs(nearestFloor - currentFloor)))
                    else
                        Commands(Side.DOWN, MoveCommand.DOWN.times(abs(nearestFloor - currentFloor)))
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

        private inline fun numberOf(destinations: Signals<Calls>) = destinations.fold(0) {
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

}

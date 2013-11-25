package driss

import fr.codestory.elevator.Elevator.Side
import fr.codestory.elevator.BuildingDimension
import fr.codestory.elevator.Cabin
import fr.codestory.elevator.Elevator

import java.lang.Math.*
import java.util.HashSet

public class DrissElevator(public var currentFloor: Int = 0, val dimension: BuildingDimension = BuildingDimension(0, 19), val cabin: Cabin = Cabin(20)) : Elevator {

    val groom = this.Groom()
    val door = Door()

    val calls = signals(dimension, emptyImmutableArrayList as MutableList<Call>)
    val gos: Signals<Go> = signals(dimension, Go(0))

    public override fun nextMove(): String =
            when {
                door.opened || groom.wantsTheDoorToOpen() -> {
                    door.toggle {
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
        val timestampedCounter: Signal? = gos.at(floor)
        if (timestampedCounter == gos.noneValue)
        {
            gos.add(floor, Go(1))
        }
        else
        {
            timestampedCounter?.increase()
        }
    }
    public override fun call(floor: Int, side: Side): Unit {

        val callsAtFloor = calls.at(floor)
        when(callsAtFloor) {
            calls.noneValue -> calls.add(floor, arrayListOf(Call(side, 1)))
            else -> {
                callsAtFloor.add(Call(side, 1))
            }
        }
    }


    override fun userHasEntered() {
        cabin.userHasEntered()
        if (calls.requestedAt(currentFloor)) calls.at(currentFloor).remove(0)
    }
    override fun userHasExited() {
        cabin.userHasExited()
    }


    inner class Groom {

        private var commands = Commands.NONE

        public inline fun giveNextMoveCommand(): MoveCommand {
            if ( !commands.hasMoreElements()) commands = groom.giveFollowingCommands()
            return commands.nextElement()
        }

        public inline fun wantsTheDoorToOpen(): Boolean = when {
            gos.requestedAt(currentFloor) -> {
                true
            }
            commands.isTwoSidesChargingAllowed() -> {
                cabin.canAcceptSomeone() && (calls.requestedAt(currentFloor))
            }
            else -> {
                cabin.canAcceptSomeone() &&
                calls.at(currentFloor).going(commands.side).count() > 0
            }
        }

        public inline fun giveFollowingCommands(): Commands {

            if ( calls.isEmpty() && gos.isEmpty() )
                return Commands.NONE

            val gosAbove = gos.above(currentFloor)
            val gosBelow = gos.below(currentFloor)

            return when {
                gos.isEmpty() -> {

                    val nearestFloor = nearestFloorFrom(currentFloor, calls.signaledFloors())

                    if (currentFloor < nearestFloor)
                        Commands(Side.UP, MoveCommand.UP.times(abs(nearestFloor - currentFloor)))
                    else
                        Commands(Side.DOWN, MoveCommand.DOWN.times(abs(nearestFloor - currentFloor)))
                }

                sumOf(gosAbove) > sumOf(gosBelow) -> {
                    val mainDirection = Side.UP
                    val distance: Int = gosAbove.distanceToFarthestFloorFrom(currentFloor)
                    when {
                        calls.at(currentFloor - 1).going(mainDirection).size > 0 && distance > 1 -> {
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
                        calls.at(currentFloor + 1).going(mainDirection).size > 0 && distance > 1 -> {
                            Commands(mainDirection, Array(distance + 2, invertFirst(MoveCommand.DOWN)))
                        }
                        else -> {
                            Commands(mainDirection, MoveCommand.DOWN.times(distance))
                        }
                    }
                }
            }
        }

        private inline fun sumOf(destinations: Iterable<Signal>) =
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

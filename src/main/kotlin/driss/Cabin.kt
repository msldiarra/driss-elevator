package driss

import driss.DrissElevator.MoveCommand
import fr.codestory.elevator.Elevator.Side

import java.lang.Math.abs

open class Cabin(val gos: Signals<Go>, val capacity: Int, var currentFloor: Int = 0){

    val door = Door()
    val groom = Groom()
    var peopleInside: Int = 0

    public fun canAcceptSomeone(): Boolean = capacity > peopleInside

    public fun userHasEntered(): Unit {
        if (peopleInside < capacity) peopleInside++
    }

    public fun userHasExited() {
        if (peopleInside > 0) peopleInside--
    }


    inner class Groom() {

        private var commands = Commands.NONE

        public inline fun    giveNextMoveCommand(calls: Signals<out List<Call>>): MoveCommand {
            if ( !commands.hasMoreElements()) commands = giveFollowingCommands(calls)
            return commands.nextElement()
        }

        public inline fun wantsTheDoorToOpen(calls: Signals<out List<Call>>): Boolean = when {
            gos.requestedAt(currentFloor) -> {
                true
            }
            commands.isTwoSidesChargingAllowed() -> {
                canAcceptSomeone() && (calls.requestedAt(currentFloor))
            }
            else -> {
                canAcceptSomeone() &&
                calls.at(currentFloor).going(commands.side).count() > 0
            }
        }

        private inline fun giveFollowingCommands(calls: Signals<out List<Call>>): Commands = with(this) {

            if ( calls.isEmpty() && gos.isEmpty() )
                Commands.NONE

            val gosAbove = gos.above(currentFloor)
            val gosBelow = gos.below(currentFloor)

            when {
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

}

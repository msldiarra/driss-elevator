package driss


import fr.codestory.elevator.Elevator.Side
import driss.Door.Command.*

import java.lang.Math.abs
import fr.codestory.elevator.BuildingDimension

class Cabin(val gos: Signals<Go>, val capacity: Int, var currentFloor: Int = 0){

    val door = Door()
    val groom = Groom()
    var peopleInside: Int = 0

    var lastOpenCommand: Door.Command = OPEN

    public fun canAcceptSomeone(): Boolean = capacity > peopleInside

    public fun userHasEntered(): Unit {
        if (peopleInside == 0) groom.resetCommands()
        if (peopleInside < capacity) peopleInside++
    }

    public fun userHasExited() {
        if (peopleInside > 0) peopleInside--
    }

    fun updateReachedFloorAfter(chosenCommand: MoveCommand): MoveCommand {
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
        return chosenCommand
    }



    inner class Groom() {

        var commands = Commands.NONE


        public fun giveNextMoveCommand(calls: Signals<out List<Call>>): String {
            if ( !commands.hasMoreElements()) commands = giveFollowingCommands(calls)

            return updateReachedFloorAfter(commands.nextElement()).name()
        }

        public fun wantsTheDoorToOpen(calls: Signals<out List<Call>>): Boolean = when {
            gos.requestedAt(currentFloor) -> true
            commands.hasMoreElements() && peopleInside == 0 -> canAcceptSomeone() && calls.requestedAt(currentFloor)

            commands.hasMoreElements() -> {
                canAcceptSomeone() &&
                calls.at(currentFloor).going(commands.side).count() > 0
            }
            else -> {
                canAcceptSomeone() && calls.requestedAt(currentFloor)
            }
        }

        public fun openTheDoor(dimension: BuildingDimension): Door.Command = door.toggle {

            gos.reached(currentFloor)

            lastOpenCommand = when {
                currentFloor.atLimit(dimension) -> OPEN
                commands.hasMoreElements() && peopleInside == 0 -> OPEN  // it follows a call
                commands.hasMoreElements() -> if (commands.side == Side.DOWN)  OPEN_DOWN else OPEN_UP
                else -> OPEN
            }
            lastOpenCommand
        }

        fun Int.atLimit(buildingDimmension: BuildingDimension): Boolean = this.equals(buildingDimmension.getHigherFloor())
        || this.equals(buildingDimmension.getLowerFloor())


        public fun closeTheDoor(): Door.Command = door.toggle { CLOSE }

        public fun resetCommands(): Unit {
            commands = Commands.NONE
        }

        private fun giveFollowingCommands(calls: Signals<out List<Call>>): Commands = with(this) {

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

        private fun sumOf(destinations: Iterable<Signal>) =
                destinations.fold(0) { number, elevatorRequest -> number + elevatorRequest.number }


        private fun invertFirst(command: MoveCommand) = {(i: Int) ->
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


        fun switch(): MoveCommand = when(this) {
            MoveCommand.DOWN -> MoveCommand.UP
            MoveCommand.UP -> MoveCommand.DOWN
            else -> MoveCommand.NOTHING }
    }
}





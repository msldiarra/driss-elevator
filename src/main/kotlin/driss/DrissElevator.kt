package driss

import fr.codestory.elevator.Elevator.Side
import fr.codestory.elevator.BuildingDimension
import fr.codestory.elevator.Elevator
import driss.Door.Command.*

public class DrissElevator(initialFloor: Int = 0,
                           val dimension: BuildingDimension = BuildingDimension(0, 19),
                           cabinSize: Int,
                           cabinNumber: Int) : Elevator {


    val calls = signals(dimension, emptyImmutableArrayList as MutableList<Call>)

    val cabins = Array(cabinNumber) {
        Cabin(signals(dimension, Go(0)), cabinSize, initialFloor)
    }

    public override fun nextMove(): String = cabins.map {
        with(it) {
            when {
                door.opened -> groom.closeTheDoor()
                groom.wantsTheDoorToOpen(calls) -> groom.openTheDoor()
                else -> updateReachedFloorAfter(groom.giveNextMoveCommand(calls))
            }
        }
    }.
    makeString("\n")

    fun optimize_open_command(commands: Commands, doorCommand: Door.Command): Door.Command = when(doorCommand) {
        OPEN -> {
            when(commands.side) {
                Side.UP -> OPEN_UP
                Side.DOWN -> OPEN_DOWN
                else -> OPEN
            }
        }
        else -> CLOSE
    }


    private inline fun Cabin.updateReachedFloorAfter(chosenCommand: MoveCommand): String {
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
        cabins.forEach { it.gos.clear() }
    }
    public override fun go(cabinNumber: Int, floor: Int): Unit {
        with(cabins[cabinNumber]) {
            if (gos.requestedAt(floor))
                gos.at(floor).increase()
            else
                gos.add(floor, Go(1))

        }

    }
    public override fun call(floor: Int, side: Side): Unit {

        if ( calls.requestedAt(floor)) {
            calls.at(floor).add(Call(side, 1))
        }
        else {
            calls.add(floor, arrayListOf(Call(side, 1)))
        }
    }

    override fun userHasEntered(cabinNumber: Int) {
        with(cabins[cabinNumber]) {
            userHasEntered()

            if (calls.requestedAt(currentFloor)){
                calls.at(currentFloor).remove(0)

                if (calls.at(currentFloor).size() == 0)
                    calls.reached(currentFloor)
            }
        }
    }
    override fun userHasExited(cabinNumber: Int) {
        cabins[cabinNumber].userHasExited()
    }


    override fun go(floor: Int) {
        throw UnsupportedOperationException()
    }
    override fun userHasEntered() {
        throw UnsupportedOperationException()
    }
    override fun userHasExited() {
        throw UnsupportedOperationException()
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


    override fun toString(): String? {

        return with(StringBuilder()) {
            append("DrissElevator state\n")
            append("Calls ${calls.count()}:\n")
            append("\t Signaled floors: ")
            calls.signaledFloors().forEach { floor -> append("${floor} ") }
            append("\n")

            cabins.forEach { cabin ->

                append("\n\nCabin____\n")
                append("\tcurrentFloor ${cabin.currentFloor}\n")
                append("\tGos: ")
                cabin.gos.signaledFloors().forEach { floor -> append("${floor} ") }
                append("\n")
                append("\t${cabin.peopleInside} persons inside\n")
            }

            this
        }.toString()
    }
}

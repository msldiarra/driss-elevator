package driss

import fr.codestory.elevator.Elevator.Side
import fr.codestory.elevator.BuildingDimension
import fr.codestory.elevator.Elevator
import java.util.ArrayList

public class DrissElevator(initialFloor: Int = 0,
                           val dimension: BuildingDimension = BuildingDimension(0, 19),
                           cabinSize: Int,
                           cabinNumber: Int) : Elevator {


    val calls = signals(dimension, ArrayList<Call>() : MutableList<Call>)

    val cabins = Array(cabinNumber) {
        Cabin(signals(dimension, Go(0)), cabinSize, initialFloor)
    }

    public override fun nextMove(): String = cabins.map {
        with(it) {
            when {
                door.opened || groom.wantsTheDoorToOpen(calls) -> {
                    door.toggle {
                        gos.reached(currentFloor)
                    }.name()
                }
                else -> updateReachedFloorAfter(groom.giveNextMoveCommand(calls))
            }
        }
    }.fold("") { commands, command -> command + "\n" + commands }


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
        val timestampedCounter: Signal? = cabins[cabinNumber].gos.at(floor)
        if (timestampedCounter == cabins[cabinNumber].gos.noneValue)
        {
            cabins[cabinNumber].gos.add(floor, Go(1))
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

    override fun userHasEntered(cabinNumber: Int) {
        with(cabins[cabinNumber]) {
            userHasEntered()
            if (calls.requestedAt(currentFloor)) calls.at(currentFloor).remove(0)
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

}

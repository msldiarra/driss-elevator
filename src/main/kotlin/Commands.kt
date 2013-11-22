package fr.codestory.elevator.hodor


import fr.codestory.elevator.hodor.HodorElevator.Command.UP
import fr.codestory.elevator.hodor.HodorElevator.Command.DOWN
import fr.codestory.elevator.hodor.HodorElevator.Command.OPEN
import fr.codestory.elevator.hodor.HodorElevator.Command.CLOSE
import fr.codestory.elevator.hodor.HodorElevator.Command.NOTHING
import fr.codestory.elevator.Elevator.Side
import fr.codestory.elevator.hodor.HodorElevator.Command

class Commands(val currentFloor:Int = 0) {

    fun forCall(at: Int, going: Side?): Array<Command>? {
        return commands(at);
    }

    fun forGo(to: Int): Array<Command>? {
        return commands(to);
    }

    private fun commands(to: Int) : Array<Command> {

        var commands = array<Command>()

        when {
            currentFloor.isUnder(to) -> { commands = go(UP, to) }
            currentFloor.isAbove(to) -> { commands = go(DOWN, to) }
        }

        return commands.plus(OPEN).plus(CLOSE).copyToArray()
    }

    private fun go(way: Command, to: Int) : Array<Command> {
        return Array<Command> (Math.abs(to - currentFloor), { way })
    }

    private fun Int.isAbove(callFloor: Int) : Boolean{
        return this > callFloor
    }

    private fun Int.isUnder(callFloor: Int) : Boolean{
        return this < callFloor
    }
}
package driss

import fr.codestory.elevator.Elevator.Side
import java.util.Enumeration
import driss.Cabin.MoveCommand


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
        public val NONE: Commands = Commands(Side.UNKOWN, array())
    }
}


package fr.codestory.elevator

import fr.codestory.elevator.ElevatorCommand.Side
import java.util.Observable

class Decision(val side: Side, val commands: Array<Command>) : Observable() {

    var remainingCommands: Int = 0
    private var twoSidesCharging: Boolean = true

    public fun allowsTwoSidesCharging(): Boolean {
        return twoSidesCharging
    }

    public fun nextCommand(): Command {
        if (remainingCommands <= 0)
        {
            return Command.NOTHING
        }

        val command: Command = commands.get(commands.size - remainingCommands--)
        if (remainingCommands == 0)
        {
            this.setChanged()
            notifyObservers()
        }

        return command
    }
    {
        this.remainingCommands = commands.size
    }


    class object {
        public val NONE: Decision = Decision(Side.UP, array())
        public fun tryNewOne(engine: ContinueOnItsDecisionElevatorCommand): Decision {
            val calls = engine.calls
            val gos = engine.gos

            if ((calls?.isEmpty()) as Boolean && engine.gos?.isEmpty() as Boolean)
                return NONE

            val currentFloor: Int = engine.currentFloor()
            val callsAbove = calls?.above(currentFloor)
            val callsBelow = calls?.below(currentFloor)

            var decision: Decision = NONE

            if (callsBelow != null && callsAbove != null && gos?.isEmpty() as Boolean)
            {
                if (numberOf(callsBelow) > numberOf(callsAbove))
                {
                    val distance = callsBelow.distanceToNearestFloorFrom(currentFloor) as Int

                    decision = Decision(Side.DOWN, Command.DOWN.times(distance) )
                } else
                {
                    val distance: Int = callsAbove.distanceToNearestFloorFrom(currentFloor) as Int
                    decision = Decision(Side.UP, Command.UP.times(distance))
                }
                decision.twoSidesCharging = true
            }
            else
            {
                val nextCommands: Array<Command>
                var mainDirection: Side = Side.UNKOWN
                var allowWrongSideCharging: Boolean = false
                val gosAbove = gos?.above(currentFloor) as Destinations<ElevatorRequest>
                val gosBelow = gos?.below(currentFloor) as Destinations<ElevatorRequest>
                if ((sumOf(gosAbove)) > (sumOf(gosBelow)))
                {
                    mainDirection = Side.UP
                    val distance: Int = gosAbove.distanceToFarthestFloorFrom(currentFloor)
                    when {
                        calls?.at(currentFloor - 1)?.goingUpside() != ElevatorRequest.NONE && distance > 1 -> {
                            nextCommands = Array(distance+1, { i -> if(i==0) Command.DOWN else Command.UP})

                            allowWrongSideCharging = true
                        }
                        else -> {
                            nextCommands = Command.UP.times(distance)
                        }
                    }
                }
                else
                {
                    mainDirection = Side.DOWN
                    val distance: Int = gosBelow.distanceToFarthestFloorFrom(currentFloor)
                    if (calls?.at(currentFloor + 1)?.goingDownside() != ElevatorRequest.NONE && distance > 1)
                    {
                        nextCommands = Array(distance+1, {i -> if(i==0) Command.UP else Command.DOWN } )
                        allowWrongSideCharging = true
                    }
                    else
                    {
                        nextCommands = Command.DOWN.times(distance)
                    }
                }
                decision = Decision(mainDirection, nextCommands)
                decision.twoSidesCharging = allowWrongSideCharging
            }
            decision.addObserver(engine.RenewDecision())
            return decision
        }

        private open fun numberOf(destinations: Destinations<Calls>): Int {
            var number = 0

            destinations.forEach { calls ->
                number += calls.goingUpside()?.getNumber() as Int
                number += calls.goingDownside()?.getNumber() as Int
            }

            return number
        }

        private open fun sumOf(destinations: Iterable<ElevatorRequest>): Int {
            var number: Int = 0

            destinations.forEach { elevatorRequest -> number += elevatorRequest.getNumber()  }

            return number
        }
    }
}

public enum class Command {
    UP
    DOWN
    NOTHING
    fun times(number : Int) : Array<Command> {
        return times[number]
    }
    private val times : Array<Array<Command>> =
            array<Array<Command>>(array<Command>(),
            array<Command>(this),
            array<Command>(this, this),
            array<Command>(this, this, this),
            array<Command>(this, this, this, this),
            array<Command>(this, this, this, this, this))
}


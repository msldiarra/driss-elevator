package fr.codestory.elevator

import fr.codestory.elevator.ElevatorCommand.Side
import java.util.Observable
import fr.codestory.elevator.order.Destinations
import fr.codestory.elevator.order.ElevatorRequest
import fr.codestory.elevator.order.Calls
import fr.codestory.elevator.Decision.FollowDecision

public class ContinueOnItsDecisionElevatorCommand(public var currentFloor: Int = 0) : ElevatorCommand, FollowDecision {
    override var decision: Decision = Decision.NONE

    private var openedDoors: Boolean = false
    var calls: Destinations<Calls> = Destinations.init(Calls.NONE)
    var gos: Destinations<ElevatorRequest> = Destinations.init(ElevatorRequest.NONE)

    public override fun nextMove(): String {
        if (isSomeoneToTakeOrToLeave()) return openThenClose()

        if (decision == Decision.NONE) {
            decision = Decision.tryNewOne(this.currentFloor,this.calls,this.gos)
            decision.addObserver { (terminatedDecision, arguments) ->
                terminatedDecision?.deleteObservers()
                this.noMoreDecision()
            }
        }

        var command: Command = decision.nextCommand()
        updateReachedFloorAfter(command)
        return command.name()
    }

    private inline fun updateReachedFloorAfter(chosenCommand: Command) {
        when (chosenCommand) {
            Command.UP -> {
                currentFloor++
            }
            Command.DOWN -> {
                currentFloor--
            }
            Command.NOTHING -> {
            }
        }
    }


    private fun isSomeoneToTakeOrToLeave(): Boolean {
        if (decision.allowsTwoSidesCharging())
        {
            return gos.contains(currentFloor) || calls.at(currentFloor) != Calls.NONE
        }
        else
        {
            return (gos.contains(currentFloor)) || calls.at(currentFloor).going(decision.side) != ElevatorRequest.NONE
        }
    }
    private fun openThenClose(): String {
        if (openedDoors)
        {
            openedDoors = false
            calls.reached(currentFloor)
            gos.reached(currentFloor)
            return "CLOSE"
        }
        else
        {
            openedDoors = true
            return "OPEN"
        }
    }
    public override fun reset(): Unit {
        calls.clear()
        gos.clear()
        currentFloor = 0
        decision = Decision.NONE
    }
    public override fun go(to: Int): Unit {
        var timestampedCounter: ElevatorRequest? = gos.at(to)
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
        var callsAtFloor: Calls = calls.at(at)
        if (callsAtFloor == Calls.NONE)
        {
            calls.add(at, if (side == Side.UP) Calls.goingUp() else Calls.goingDown() as Calls)
        }
        else
        {
            callsAtFloor.increase(side)
        }

        if(Math.abs(at - currentFloor) == 1 && decision.side == side && gos.isEmpty()){
            decision.trySomethingelseNextTime()
        }
    }


    class object {
        public open fun init(): ContinueOnItsDecisionElevatorCommand {
            val __ = ContinueOnItsDecisionElevatorCommand(0)
            return __
        }
    }
}


class Decision(val side: Side,
               private val commands: Array<Command>) : Observable() {

    var remainingCommands: Int = 0

    public fun allowsTwoSidesCharging(): Boolean = remainingCommands < 1 || commands.all { command -> commands[0] != command }

    public fun nextCommand(): Command {
        if (remainingCommands <= 0)
        {
            return Command.NOTHING
        }

        val command: Command = commands.get(commands.size - remainingCommands--)
        if (remainingCommands == 0) noMoreCommand()
        return command
    }

    public fun trySomethingelseNextTime(){
        noMoreCommand()
    }

    private fun noMoreCommand() {
        this.setChanged()
        notifyObservers()
    }
    {
        this.remainingCommands = commands.size
    }


    class object {
        public val NONE: Decision = Decision(Side.UP, array())

        public fun tryNewOne(currentFloor: Int, calls : Destinations<Calls> , gos :Destinations<ElevatorRequest>): Decision {

            if ((calls.isEmpty()) && gos.isEmpty())
                return NONE

            val callsAbove = calls.above(currentFloor)
            val callsBelow = calls.below(currentFloor)

            val decision = when {
                gos.isEmpty() && numberOf(callsBelow) > numberOf(callsAbove) -> {

                    val distance = callsBelow.distanceToNearestFloorFrom(currentFloor)
                    Decision(Side.DOWN, Command.DOWN.times(distance))
                }
                gos.isEmpty() && numberOf(callsBelow) <= numberOf(callsAbove) -> {
                    val distance: Int = callsAbove.distanceToNearestFloorFrom(currentFloor)
                    Decision(Side.UP, Command.UP.times(distance))
                }
                else -> {

                    val nextCommands: Array<Command>
                    var mainDirection: Side
                    val gosAbove = gos.above(currentFloor)
                    val gosBelow = gos.below(currentFloor)

                    when {
                        (sumOf(gosAbove)) > (sumOf(gosBelow)) -> {
                            mainDirection = Side.UP
                            val distance: Int = gosAbove.distanceToFarthestFloorFrom(currentFloor)
                            when {
                                calls.at(currentFloor - 1).going(mainDirection) != ElevatorRequest.NONE && distance > 1 -> {
                                    nextCommands = Array(distance + 1, invertFirst(Command.UP))
                                }
                                else -> {
                                    nextCommands = Command.UP.times(distance)
                                }
                            }
                        }
                        else -> {
                            mainDirection = Side.DOWN
                            val distance: Int = gosBelow.distanceToFarthestFloorFrom(currentFloor)
                            when {
                                calls.at(currentFloor + 1).going(mainDirection) != ElevatorRequest.NONE && distance > 1 -> {
                                    nextCommands = Array(distance + 1, invertFirst(Command.DOWN))
                                }
                                else -> {
                                    nextCommands = Command.DOWN.times(distance)
                                }
                            }
                        }
                    }
                    Decision(mainDirection, nextCommands)

                }
            }
            return decision
        }

        private fun numberOf(destinations: Destinations<Calls>): Int {
            var number = 0

            destinations.forEach { calls ->
                number += calls.going(Side.UP).number
                number += calls.going(Side.DOWN).number
            }

            return number
        }

        private fun sumOf(destinations: Iterable<ElevatorRequest>): Int {
            var number: Int = 0

            destinations.forEach { elevatorRequest -> number += elevatorRequest.number }

            return number
        }

        inline private fun invertFirst(command: Command) = {(i: Int) ->
            when {
                i == 0 -> command.switch()
                else -> command
            }
        }
    }

    trait FollowDecision{
        var decision : Decision

        fun noMoreDecision() {
            decision = Decision.NONE
        }
    }
}


enum class Command {

    UP
    DOWN
    NOTHING
    fun times(number: Int): Array<Command> {
        return times[number]
    }
    private val times: Array<Array<Command>> =
            array<Array<Command>>(array<Command>(),
                    array<Command>(this),
                    array<Command>(this, this),
                    array<Command>(this, this, this),
                    array<Command>(this, this, this, this),
                    array<Command>(this, this, this, this, this))

    inline fun switch(): Command = when(this) {
        Command.DOWN -> Command.UP
        Command.UP -> Command.DOWN
        else -> Command.NOTHING }
}



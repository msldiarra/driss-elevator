package fr.codestory.elevator

import fr.codestory.elevator.ElevatorCommand.Side
import java.util.Observable
import fr.codestory.elevator.order.Destinations
import fr.codestory.elevator.order.ElevatorRequest
import fr.codestory.elevator.order.Calls
import java.util.Observer
import fr.codestory.elevator.ContinueOnItsDecisionElevatorCommand.RenewDecision

public class ContinueOnItsDecisionElevatorCommand(private var floor: Int = 0) : ElevatorCommand {

    private var openedDoors: Boolean = false
    private var decision: Decision = Decision.NONE
    var calls: Destinations<Calls> = Destinations.init(Calls.NONE)
    var gos: Destinations<ElevatorRequest> = Destinations.init(ElevatorRequest.NONE)

    public override fun nextMove(): String {
        if (isSomeoneToTakeOrToLeave())
        {
            return openThenClose()
        }

        if (decision == Decision.NONE)
        {
            decision = Decision.tryNewOne(this)
        }

        var command: Command = decision.nextCommand()
        when (command) {
            Command.UP -> {
                floor++
            }
            Command.DOWN -> {
                floor--
            }
            Command.NOTHING -> {
            }
        }
        return command.name()
    }
    private fun isSomeoneToTakeOrToLeave(): Boolean {
        if (decision.allowsTwoSidesCharging())
        {
            return gos.contains(floor) || calls.at(floor) != Calls.NONE
        }
        else
        {
            return (gos.contains(floor)) || calls.at(floor).going(decision.side) != ElevatorRequest.NONE
        }
    }
    private fun openThenClose(): String {
        if (openedDoors)
        {
            openedDoors = false
            calls.reached(floor)
            gos.reached(floor)
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
        floor = 0
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
    }
    fun currentFloor(): Int {
        return floor
    }
    class RenewDecision(private val elevatorCommand: ContinueOnItsDecisionElevatorCommand) : Observer {


        public override fun update(decision: Observable?, arg: Any?): Unit {
            decision?.deleteObservers()

            elevatorCommand.decision= Decision.NONE
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

    public fun allowsTwoSidesCharging(): Boolean = commands.all { command -> commands[0] != command }

    public fun nextCommand(): Command {
        if (remainingCommands <= 0)
        {
            return Command.NOTHING
        }

        val command: Command = commands.get(commands.size - remainingCommands--)
        if (remainingCommands == 0) fireChanges()
        return command
    }

    fun fireChanges() {
        this.setChanged()
        notifyObservers()
    }
    {
        this.remainingCommands = commands.size
    }


    class object {
        public val NONE: Decision = Decision(Side.UP, array())

        public fun tryNewOne(engine: ContinueOnItsDecisionElevatorCommand): Decision {
            val calls = engine.calls
            val gos = engine.gos

            if ((calls.isEmpty()) && engine.gos.isEmpty())
                return NONE

            val currentFloor: Int = engine.currentFloor()

            val decision = when {
                gos.isEmpty() -> {

                    val callsAbove = calls.above(currentFloor)
                    val callsBelow = calls.below(currentFloor)

                    when {
                        numberOf(callsBelow) > numberOf(callsAbove) -> {
                            val distance = callsBelow.distanceToNearestFloorFrom(currentFloor)

                            Decision(Side.DOWN, Command.DOWN.times(distance))
                        }
                        else -> {
                            val distance: Int = callsAbove.distanceToNearestFloorFrom(currentFloor)
                            Decision(Side.UP, Command.UP.times(distance))
                        }
                    }
                }
                else -> {
                    val nextCommands: Array<Command>
                    var mainDirection: Side = Side.UNKOWN
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
            decision.addObserver(RenewDecision(engine))
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

        private fun invertFirst(command: Command) = {(i: Int) ->
            when {
                i == 0 -> command.switch()
                else -> command
            }
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

    fun switch(): Command = when {
        this == Command.DOWN -> Command.UP
        this == Command.UP -> Command.DOWN
        else -> Command.NOTHING }
}



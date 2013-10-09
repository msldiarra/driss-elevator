package fr.codestory.elevator;

import java.util.Observable;

import static com.google.common.collect.Sets.union;
import static fr.codestory.elevator.ElevatorCommand.Command;
import static fr.codestory.elevator.ElevatorCommand.Side;
import static java.lang.Math.abs;
import static java.util.Collections.max;
import static java.util.Collections.min;

/**
 * @author Miguel Basire
 */
class Decision extends Observable {

    public final static Decision NONE = new Decision(Side.UP, new Command[]{});

    final Side side;
    private final Command[] commands;
    private int remainingCommands;

    private boolean wrongSideChargingAllowed = true;

    Decision(Side side, Command[] commands) {
        this.side = side;
        this.commands = commands;
        this.remainingCommands = commands.length;
    }


    public boolean allowWrongSideCharging() {
        return wrongSideChargingAllowed;
    }

    public Command nextCommand() {
        if (remainingCommands <= 0) {
            return Command.NOTHING;
        }
        Command command = commands[remainingCommands-- - 1];
        if (remainingCommands == 0) {
            this.setChanged();
            notifyObservers();
        }

        return command;
    }


    public static Decision tryNewOne(ContinueOnItsDecisionElevatorCommand engine) {

        // La note max est de 20
        // 20 - TickToWait/2  - TickToGo  + bestTickToGo = note
        //
        // SUM (max( 20 - TickToWait/2 - TickToGo + bestTickToGo ))
        // max (SUM (20 - TickToWait/2 - TickToGo + bestTickToGo) )
        //

        int currentFloor = engine.currentFloor();
        Destinations<Calls> callsBelow = engine.calls.below(currentFloor);
        Destinations<Calls> callsAbove = engine.calls.above(currentFloor);

        Destinations<ElevatorRequest> gosBelow = engine.gos.below(currentFloor);
        Destinations<ElevatorRequest> gosAbove = engine.gos.above(currentFloor);


        if (callsBelow.floors().size() + callsAbove.floors().size() + gosAbove.floors().size() + gosBelow.floors().size() == 0) return Decision.NONE;

        Decision decision = Decision.NONE;

        if (gosAbove.floors().size() + callsAbove.floors().size() < gosBelow.floors().size() + callsBelow.floors().size()) {

            int farestFloor = min(union(callsBelow.floors(), gosBelow.floors()));
            int distance = currentFloor - farestFloor;
            decision = new Decision(Side.DOWN, Command.DOWN.times(distance));

        } else {
            int farestFloor = max(union(callsAbove.floors(), gosAbove.floors()));
            int distance = farestFloor - currentFloor;
            decision = new Decision(Side.UP, Command.UP.times(distance));
        }
        decision.wrongSideChargingAllowed = false;
        decision.addObserver(engine.new RenewDecision());
        return decision;
    }


    private static int cost(int from, Destinations<Integer> peopleByFloor) {
        int cost = 0;
        for (Integer floor : peopleByFloor.floors()) {
            cost += abs(floor - from) * peopleByFloor.at(floor);
        }
        return cost;
    }

}

package fr.codestory.elevator;

import com.google.common.collect.Sets;

import java.util.Observable;

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

        int currentFloor = engine.currentFloor();
        Destinations<Side> callsBelow = engine.calls.below(currentFloor);
        Destinations<Side> callsAbove = engine.calls.above(currentFloor);

        Destinations<Integer> gosBelow = engine.wishedFloors.below(currentFloor);
        Destinations<Integer> gosAbove = engine.wishedFloors.above(currentFloor);

        int costBelow = cost(currentFloor, gosBelow);
        int costAbove = cost(currentFloor, gosAbove);

        if (callsBelow.floors().size() + callsAbove.floors().size() + costBelow + costAbove == 0) return Decision.NONE;

        Decision decision = Decision.NONE;

        if (costAbove + callsAbove.floors().size() < costBelow + callsBelow.floors().size()) {

            int farestFloor = min(Sets.union(callsBelow.floors(),gosBelow.floors()));
            int distance = currentFloor - farestFloor;
            decision = new Decision(Side.DOWN, Command.DOWN.times(distance));

        } else {
            int farestFloor = max(Sets.union(callsAbove.floors(), gosAbove.floors()));
            int distance = farestFloor - currentFloor;
            decision = new Decision(Side.UP, Command.UP.times(distance));
        }
        decision.wrongSideChargingAllowed = false;
        decision.addObserver(engine);
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

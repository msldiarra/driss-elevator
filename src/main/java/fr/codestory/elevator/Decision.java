package fr.codestory.elevator;

import java.util.*;

import static java.lang.Math.abs;
import static java.util.Collections.max;
import static java.util.Collections.min;

/**
* @author Miguel Basire
*/
class Decision extends Observable {

    public final static Decision NONE = new Decision(ElevatorCommand.Side.UP,new ElevatorCommand.Command[]{});

    final ElevatorCommand.Side side;
    private final ElevatorCommand.Command[] commands;
    private int remainingCommands;

    private boolean wrongSideChargingAllowed = true;

    Decision(ElevatorCommand.Side side, ElevatorCommand.Command[] commands) {
        this.side = side;
        this.commands = commands;
        this.remainingCommands = commands.length ;
    }


    public boolean allowWrongSideCharging() {
        return wrongSideChargingAllowed;
    }

    public ElevatorCommand.Command nextCommand() {
        if(remainingCommands <= 0){
            return ElevatorCommand.Command.NOTHING;
        }
        ElevatorCommand.Command command = commands[remainingCommands-- -1];
        if (remainingCommands == 0) {
            this.setChanged();
            notifyObservers();
        }

        return command;
    }


    public static Decision tryNewOne(ContinueOnItsDecisionElevatorCommand elevatorEngine) {

        // La note max est de 20
        // 20 - TickToWait/2  - TickToGo  + bestTickToGo = note

        SortedMap<Integer, ElevatorCommand.Side> callsBelow = below(elevatorEngine.currentFloor(), elevatorEngine.calledFloors);
        SortedMap<Integer, ElevatorCommand.Side> callsAbove = above(elevatorEngine.currentFloor(), elevatorEngine.calledFloors);

        SortedMap<Integer, Integer> gosBelow = below(elevatorEngine.currentFloor(), elevatorEngine.wishedFloors);
        SortedMap<Integer, Integer> gosAbove = above(elevatorEngine.currentFloor(), elevatorEngine.wishedFloors);

        int costBelow = cost(elevatorEngine.currentFloor(), gosBelow);
        int costAbove = cost(elevatorEngine.currentFloor(), gosAbove);

        if (callsBelow.size() + callsAbove.size() + costBelow + costAbove == 0) return Decision.NONE;

        Decision decision = Decision.NONE;

        if (costAbove + callsAbove.size() > costBelow + callsBelow.size()) {

            List<Integer> floors = new ArrayList(floors(callsAbove));
            floors.addAll(floors(gosAbove));
            int distance = max(floors) - elevatorEngine.currentFloor();
            decision = new Decision(ElevatorCommand.Side.UP, ElevatorCommand.Command.UP.times(distance));
        } else {

            List<Integer> floors = new ArrayList(floors(callsBelow));
            floors.addAll(floors(gosBelow));
            int distance = elevatorEngine.currentFloor() - min(floors);
            decision = new Decision(ElevatorCommand.Side.DOWN, ElevatorCommand.Command.DOWN.times(distance));
        }
        decision.wrongSideChargingAllowed = false;
        decision.addObserver(elevatorEngine);
        return decision;
    }

    static private <T> SortedMap<Integer, T> below(int floorToExclude, SortedMap<Integer, T> calls) {
        return calls.headMap(floorToExclude);
    }

    static private <T> SortedMap<Integer, T> above(int floorToExclude, SortedMap<Integer, T> floors) {
        return floors.tailMap(Math.min(floors.size() - 1, floorToExclude + 1));
    }

    private static Set<Integer> floors(SortedMap<Integer, ?> floors) {
        return floors.keySet();
    }

    private static int cost(int from, SortedMap<Integer, Integer> peopleByFloor) {
        int cost = 0;
        for (Integer floor : peopleByFloor.keySet()) {
            cost += abs(floor - from) * peopleByFloor.get(floor);
        }
        return cost;
    }

}

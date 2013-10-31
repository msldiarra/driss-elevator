package fr.codestory.elevator;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

import static fr.codestory.elevator.ElevatorCommand.Command;
import static fr.codestory.elevator.ElevatorCommand.Command.DOWN;
import static fr.codestory.elevator.ElevatorCommand.Command.UP;
import static fr.codestory.elevator.ElevatorCommand.Side;

/**
 * @author Miguel Basire
 */
class Decision extends Observable {

    public final static Decision NONE = new Decision(Side.UP, Collections.EMPTY_LIST);

    final Side side;
    private final List<Command> commands;
    int remainingCommands;

    private boolean twoSidesCharging = true;

    Decision(Side side, List<Command> commands) {
        this.side = side;
        this.commands = commands;
        this.remainingCommands = commands.size();
    }


    public boolean allowsTwoSidesCharging() {
        return twoSidesCharging;
    }

    public Command nextCommand() {
        if (remainingCommands <= 0) {
            return Command.NOTHING;
        }
        Command command = commands.get(commands.size() - remainingCommands-- );
        if (remainingCommands == 0) {
            this.setChanged();
            notifyObservers();
        }

        return command;
    }


    public static Decision tryNewOne(DrissElevator engine) {

        Destinations<Calls> calls = engine.calls;
        Destinations<ElevatorRequest> gos = engine.gos;
        if (calls.isEmpty()
                && engine.gos.isEmpty()) return Decision.NONE;

        // La note max est de 20
        // 20 - TickToWait/2  - TickToGo  + bestTickToGo = note
        //
        // SUM (max( 20 - TickToWait/2 - TickToGo + bestTickToGo ))
        // max (SUM (20 - TickToWait/2 - TickToGo + bestTickToGo) )
        // SUM 20 - SUM min( TickToWait/2 + TickToGo ) + max SUM bestTickToGo

        Decision decision = Decision.NONE;

        int currentFloor = engine.currentFloor();

        Destinations<Calls> callsAbove = calls.above(currentFloor);
        Destinations<Calls> callsBelow = calls.below(currentFloor);

        if (gos.isEmpty()) {

            if (numberOf(callsBelow) > numberOf(callsAbove))
                decision = new Decision(Side.DOWN, Arrays.asList(
                        DOWN.times(callsBelow.distanceToNearestFloorFrom(currentFloor))));
            else decision = new Decision(Side.UP, Arrays.asList(
                    UP.times(callsAbove.distanceToNearestFloorFrom(currentFloor))));

            decision.twoSidesCharging = true;

        } else {
            Side mainDirection = Side.UNKOWN;
            List<Command> nextCommands = Collections.EMPTY_LIST;
            boolean allowWrongSideCharging = false;

            if (sumOf(gos.above(currentFloor)) > sumOf(gos.below(currentFloor))) {
                mainDirection = Side.UP;

                int distance = gos.above(currentFloor).distanceToFarthestFloorFrom(currentFloor);
                if (calls.at(currentFloor - 1).goingUpside() !=  ElevatorRequest.NONE && distance > 1) {

                    nextCommands = Lists.asList(DOWN,UP.times(distance + 1));
                    allowWrongSideCharging = true;

                } else {
                    nextCommands = Arrays.asList(UP.times(distance));
                }
            } else {
                mainDirection = Side.DOWN;

                int distance = gos.below(currentFloor).distanceToFarthestFloorFrom(currentFloor);
                if (calls.at(currentFloor + 1).goingDownside() != ElevatorRequest.NONE && distance > 1) {

                    nextCommands = Lists.asList(UP,DOWN.times(distance + 1));

                    allowWrongSideCharging = true;

                } else {
                    nextCommands = Arrays.asList(DOWN.times(distance));
                }
            }
            decision = new Decision(mainDirection, nextCommands);
            decision.twoSidesCharging = allowWrongSideCharging;
        }
        decision.addObserver(engine.new RenewDecision());
        return decision;
    }


    private static int numberOf(Destinations<Calls> destinations) {

        int number = 0;
        for (Calls calls : destinations) {
            number += calls.goingDownside().getNumber() + calls.goingUpside().getNumber();
        }
        return number;
    }

    private static int sumOf(Destinations<ElevatorRequest> destinations) {

        int number = 0;
        for (ElevatorRequest elevatorRequests : destinations) {
            number += elevatorRequests.getNumber();
        }
        return number;
    }


}

package fr.codestory.elevator;

import java.util.*;

/**
 * @author Miguel Basire
 */
public class ContinueOnItsDecisionElevatorCommand implements ElevatorCommand, Observer {

    private int floor;
    boolean openedDoors = false;

    private Decision decision = Decision.NONE;


    public static final Integer NOBODY = 0;

    Destinations<Side> calls = new Destinations<>(Side.UNKOWN);
    Destinations<Integer> wishedFloors = new Destinations<>(NOBODY);


    public ContinueOnItsDecisionElevatorCommand() {
        this(0);
    }

    ContinueOnItsDecisionElevatorCommand(int initialFloor) {
        this.floor = initialFloor;
    }

    @Override
    public String nextMove() {

        if (isSomeoneToTakeOrToLeave()) {
            return openThenClose();
        }

        if (decision == Decision.NONE) {
            decision = Decision.tryNewOne(this);
        }

        Command command = decision.nextCommand();
        switch (command) {

            case UP:
                floor++;
                break;
            case DOWN:
                floor--;
                break;
            case NOTHING:
                break;
        }
        return command.name();
    }

    private boolean isSomeoneToTakeOrToLeave() {

        if (decision.allowWrongSideCharging()) {
            return wishedFloors.contains(floor)
                    || calls.at(floor) != Side.UNKOWN;
        } else {
            return wishedFloors.contains(floor)
                    || calls.at(floor) == decision.side;
        }

    }

    private String openThenClose() {
        if (openedDoors) {
            openedDoors = false;
            calls.reached(floor);
            wishedFloors.reached(floor);
            return "CLOSE";
        } else {
            openedDoors = true;
            return "OPEN";
        }
    }

    @Override
    public void reset() {
        calls.clear();
        wishedFloors.clear();
        floor = 0;
        decision = Decision.NONE;
    }

    @Override
    public void go(int to) {
        if (wishedFloors.contains(to)) {
            wishedFloors.add(to, wishedFloors.at(to) + 1);
        } else {
            wishedFloors.add(to, 1);
        }
    }

    @Override
    public void call(int at, Side side) {
        calls.add(at, side);
    }

    int currentFloor() {
        return floor;
    }

    @Override
    public void update(Observable decision, Object arg) {
        this.decision = Decision.NONE;
    }

}


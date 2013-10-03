package fr.codestory.elevator;

import java.util.Observable;
import java.util.Observer;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Miguel Basire
 */
public class ContinueOnItsDecisionElevatorCommand implements ElevatorCommand, Observer {

    private int floor;
    boolean openedDoors = false;

    private Decision decision = Decision.NONE;

    SortedMap<Integer, Side> calledFloors = new TreeMap();
    SortedMap<Integer, Integer> wishedFloors = new TreeMap();


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
            return wishedFloors.containsKey(floor)
                    || calledFloors.containsKey(floor);
        } else {
            return wishedFloors.containsKey(floor)
                    || calledFloors.get(floor) == decision.side;
        }

    }

    private String openThenClose() {
        if (openedDoors) {
            openedDoors = false;
            calledFloors.remove(floor);
            wishedFloors.remove(floor);
            return "CLOSE";
        } else {
            openedDoors = true;
            return "OPEN";
        }
    }

    @Override
    public void reset() {
        calledFloors.clear();
        wishedFloors.clear();
        floor = 0;
        decision = Decision.NONE;
    }

    @Override
    public void go(int to) {
        if (wishedFloors.containsKey(to)) {
            wishedFloors.put(to, wishedFloors.get(to) + 1);
        } else {
            wishedFloors.put(to, 1);
        }
    }

    @Override
    public void call(int at, Side side) {
        calledFloors.put(at, side);
    }

    int currentFloor() {
        return floor;
    }

    @Override
    public void update(Observable decision, Object arg) {
        this.decision = Decision.NONE;
    }

}

class Call{
    private int numberOfPersons;

    private final ElevatorCommand.Side side;

    Call(ElevatorCommand.Side side, int numberOfPersons) {
        this.numberOfPersons = numberOfPersons;
        this.side = side;
    }


}

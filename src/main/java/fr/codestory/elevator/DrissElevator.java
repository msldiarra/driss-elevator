package fr.codestory.elevator;

import java.util.Observable;
import java.util.Observer;

/**
 * @author Miguel Basire
 */
public class DrissElevator implements ElevatorCommand {

    private int floor;
    boolean openedDoors = false;

    private Decision decision = Decision.NONE;


    Destinations<Calls> calls = new Destinations<>(Calls.NONE);
    Destinations<ElevatorRequest> gos = new Destinations<>(ElevatorRequest.NONE);


    public DrissElevator() {
        this(0);
    }

    DrissElevator(int initialFloor) {
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

        if (decision.allowsTwoSidesCharging()) {
            return gos.contains(floor)
                    || calls.at(floor) != Calls.NONE;
        } else {
            return gos.contains(floor)
                    || calls.at(floor).going(decision.side);
        }

    }

    private String openThenClose() {
        if (openedDoors) {
            openedDoors = false;
            calls.reached(floor);
            gos.reached(floor);
            return "CLOSE";
        } else {
            openedDoors = true;
            return "OPEN";
        }
    }

    @Override
    public void reset() {
        calls.clear();
        gos.clear();
        floor = 0;
        decision = Decision.NONE;
    }

    @Override
    public void go(int to) {

        ElevatorRequest timestampedCounter = gos.at(to);

        if (timestampedCounter == ElevatorRequest.NONE) {
            gos.add(to, new ElevatorRequest());
        } else {
            timestampedCounter.increase();
        }
    }

    @Override
    public void call(int at, Side side) {

        Calls callsAtFloor = calls.at(at);

        if (callsAtFloor == Calls.NONE) {
            calls.add(at, side == Side.UP ? Calls.up() : Calls.down());
        } else {
            callsAtFloor.increase(side);
        }
    }

    int currentFloor() {
        return floor;
    }


    class RenewDecision implements Observer {

        @Override
        public void update(Observable decision, Object arg) {
            decision.deleteObservers();
            DrissElevator.this.decision = Decision.NONE;
        }
    }

}


package fr.codestory.elevator;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Miguel Basire
 */
public class UpAndDownElevator implements Elevator {

    private final Map<Integer,Call> calls = new HashMap<Integer, Call>();

    private int floor = 0;
    private boolean doorsOpened = false;

    private Side currentSide = Side.UP;

    private boolean[] isSomeoneLeaving = new boolean[] { false,false,false,false,false,false};

    @Override
    public String nextMove() {
        if (isSomeoneToTakeAtOrToLeaveAt(currentFloor())) return openThenClose();
        else return goOn();
    }

    private String goOn() {

        if(currentFloor() == 0) currentSide = Side.UP;
        if(currentFloor() == 5) currentSide = Side.DOWN;

        switch (currentSide) {

            case UP:
                floor++;
                break;
            case DOWN:
                floor--;
                break;
        }

        return currentSide.name();
    }

    private boolean isSomeoneToTakeAtOrToLeaveAt(int floor) {
        return (calls.get(floor) != null  // someone to take
                && ( (floor==0 || floor==5) || calls.get(floor).side == currentSide))
                || isSomeoneLeaving[floor];
    }

    private String openThenClose() {
        if(doorsOpened){
            doorsOpened = false;
            calls.put(currentFloor(),null); // charging ended
            isSomeoneLeaving[currentFloor()] = false;
            return "CLOSE";
        }else {
            doorsOpened = true;
            return "OPEN";
        }
    }

    @Override
    public void reset() {
        currentSide = Side.UP;
        floor = 0;
    }

    @Override
    public void go(int to) {
        isSomeoneLeaving[to] = true;
    }

    @Override
    public void call(int at, Side side) {
        calls.put(at, new Call(at,side));
    }

    int currentFloor(){
        return floor;
    }

    class Call{
        private final int floor;
        private final Side side;

        Call(int floor, Side side){
            this.floor = floor;
            this.side = side;
        }
    }
}

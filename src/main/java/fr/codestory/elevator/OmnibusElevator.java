package fr.codestory.elevator;

/**
 * @author Miguel Basire
 */
public class OmnibusElevator implements Elevator {

    private BuildingDimension buildingDimension = new BuildingDimension(0, 19);

    final String[] moves = new String[]{
            "OPEN", "CLOSE", "UP",
            "OPEN", "CLOSE", "UP",
            "OPEN", "CLOSE", "UP",
            "OPEN", "CLOSE", "UP",
            "OPEN", "CLOSE", "UP",
            "OPEN", "CLOSE", "DOWN",
            "OPEN", "CLOSE", "DOWN",
            "OPEN", "CLOSE", "DOWN",
            "OPEN", "CLOSE", "DOWN",
            "OPEN", "CLOSE", "DOWN"
    };

    private int floor = 0;

    @Override
    public String nextMove() {
        if (floor % (3 * 5 * 2) == 0) floor = buildingDimension.getLowerFloor();
        return moves[floor++ % (3 * 5 * 2)];
    }

    @Override
    public void reset() {
        floor = buildingDimension.getLowerFloor();
    }

    @Override
    public void go(int to) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void call(int at, Side side) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}


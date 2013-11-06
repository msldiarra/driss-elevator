package fr.codestory.elevator;

/**
 * @author Miguel Basire
 */
public interface Elevator {
    public String nextMove();

    public void reset(BuildingDimension buildingDimension);

    public void go(int to);

    public void call(int at, Side side);

    public enum Side {UP, DOWN, UNKOWN}
}

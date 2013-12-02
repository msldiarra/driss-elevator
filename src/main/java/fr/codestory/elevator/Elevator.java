package fr.codestory.elevator;

/**
 * @author Miguel Basire
 */
public interface Elevator {
    public String nextMove();

    public void reset();

    public void go(int cabin, int to);

    public void call(int at, Side side);

    void userHasEntered(int cabin);

    void userHasExited(int cabin);

    public enum Side {UP, DOWN, UNKOWN}
}

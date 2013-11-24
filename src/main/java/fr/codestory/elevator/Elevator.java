package fr.codestory.elevator;

import jet.runtime.typeinfo.KotlinSignature;

/**
 * @author Miguel Basire
 */
public interface Elevator {
    public String nextMove();

    public void reset();

    public void go(int floor);

    @KotlinSignature("fun call(floor: Int, side: Elevator.Side): Unit")
    public void call(int floor, Side side);

    void userHasEntered();

    void userHasExited();

    public enum Side {UP, DOWN, UNKOWN}
}

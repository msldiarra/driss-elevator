package fr.codestory.elevator;

import jet.runtime.typeinfo.KotlinSignature;

/**
 * @author Miguel Basire
 */
public interface Elevator {
    public String nextMove();

    public void reset();

    @Deprecated
    public void go(int floor);

    public void go(int cabinNumber, int floor);

    @KotlinSignature("fun call(floor: Int, side: Elevator.Side): Unit")
    public void call(int floor, Side side);


    @Deprecated
    void userHasEntered();

    @Deprecated
    void userHasExited();

    void userHasEntered(int cabinNumber);

    void userHasExited(int cabinNumber);

    public enum Side {UP, DOWN, UNKOWN}
}

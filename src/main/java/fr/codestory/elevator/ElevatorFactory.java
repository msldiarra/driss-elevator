package fr.codestory.elevator;

/**
 * @author Miguel Basire
 */
public interface ElevatorFactory {

    Elevator newElevator(BuildingDimension dimension);
}
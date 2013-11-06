package fr.codestory.elevator;

/**
 * @author Miguel Basire
 */
public class BuildingDimension {
    private final int lowerFloor;
    private final int higherFloor;

    public BuildingDimension(int lowerFloor, int higherFloor) {
        this.lowerFloor = lowerFloor;
        this.higherFloor = higherFloor;
    }

    public int getLowerFloor() {
        return lowerFloor;
    }


    public int getHigherFloor() {
        return higherFloor;
    }
}

package fr.codestory.elevator.driss;

import driss.DrissElevator;
import fr.codestory.elevator.BuildingDimension;
import fr.codestory.elevator.Elevator;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Miguel Basire
 */
public class DrissElevatorTest {

    @Test
    public void should_do_nothing_when_no_call() {

        DrissElevator elevator = new DrissElevator(0, new BuildingDimension(0, 19), 5, 1);

        assertThat(elevator.getCabins()[0].getCurrentFloor()).isEqualTo(0);
        assertThat(elevator.nextMove()).isEqualTo("NOTHING");
        assertThat(elevator.nextMove()).isEqualTo("NOTHING");
        assertThat(elevator.getCabins()[0].getCurrentFloor()).isEqualTo(0);
    }


    @Test
    public void should_move_on_first_call_and_stop() {

        DrissElevator elevator = new DrissElevator(0, new BuildingDimension(0, 19), 5, 1);

        elevator.call(2, Elevator.Side.UP);

        assertThat(elevator.nextMove()).isEqualTo("UP");
        assertThat(elevator.nextMove()).isEqualTo("UP");
        assertThat(elevator.nextMove()).isEqualTo("OPEN");
        elevator.userHasEntered(0);
        assertThat(elevator.nextMove()).isEqualTo("CLOSE");

        assertThat(elevator.nextMove()).isEqualTo("NOTHING");
    }

    @Test
    public void should_move_on_first_call_then_go() {

        DrissElevator elevator = new DrissElevator(0, new BuildingDimension(0, 19), 5, 1);

        elevator.call(2, Elevator.Side.UP);

        assertThat(elevator.nextMove()).isEqualTo("UP");
        assertThat(elevator.nextMove()).isEqualTo("UP");

        assertThat(elevator.nextMove()).isEqualTo("OPEN");
        elevator.userHasEntered(0);
        elevator.go(0, 3);
        assertThat(elevator.nextMove()).isEqualTo("CLOSE");

        assertThat(elevator.nextMove()).isEqualTo("UP");
        assertThat(elevator.nextMove()).isEqualTo("OPEN");
        assertThat(elevator.nextMove()).isEqualTo("CLOSE");

        assertThat(elevator.nextMove()).isEqualTo("NOTHING");
    }

    @Test
    public void should_go_down_on_first_call() {

        DrissElevator elevator = new DrissElevator(3, new BuildingDimension(0, 19), 5, 1);

        elevator.call(2, Elevator.Side.DOWN);

        assertThat(elevator.nextMove()).isEqualTo("DOWN");

        assertThat(elevator.nextMove()).isEqualTo("OPEN");
        elevator.userHasEntered(0);
        elevator.go(0, 1);
        assertThat(elevator.nextMove()).isEqualTo("CLOSE");

        assertThat(elevator.nextMove()).isEqualTo("DOWN");
        assertThat(elevator.nextMove()).isEqualTo("OPEN");
        assertThat(elevator.nextMove()).isEqualTo("CLOSE");

        assertThat(elevator.nextMove()).isEqualTo("NOTHING");
    }

    @Test
    public void should_go_upside_first() {

        DrissElevator elevator = new DrissElevator(3, new BuildingDimension(0, 19), 5, 1);

        elevator.call(1, Elevator.Side.DOWN);
        elevator.call(4, Elevator.Side.DOWN);

        assertThat(elevator.nextMove()).isEqualTo("UP");

        assertThat(elevator.nextMove()).isEqualTo("OPEN");
        elevator.userHasEntered(0);
        elevator.go(0, 1);
        assertThat(elevator.nextMove()).isEqualTo("CLOSE");
        assertThat(elevator.getCabins()[0].getCurrentFloor()).isEqualTo(4);

        assertThat(elevator.nextMove()).isEqualTo("DOWN");
        assertThat(elevator.nextMove()).isEqualTo("DOWN");
        assertThat(elevator.nextMove()).isEqualTo("DOWN");
        assertThat(elevator.getCabins()[0].getCurrentFloor()).isEqualTo(1);
        assertThat(elevator.nextMove()).isEqualTo("OPEN");
        elevator.userHasEntered(0);
        assertThat(elevator.nextMove()).isEqualTo("CLOSE");

        assertThat(elevator.nextMove()).isEqualTo("NOTHING");
    }

    @Test
    public void should_take_someone_near() {

        DrissElevator elevator = new DrissElevator(1, new BuildingDimension(0, 19), 5, 1);
        elevator.go(0, 4);

        elevator.call(0, Elevator.Side.UP);
        elevator.call(4, Elevator.Side.DOWN);

        assertThat(elevator.nextMove()).isEqualTo("DOWN");

        assertThat(elevator.nextMove()).isEqualTo("OPEN");
        elevator.userHasEntered(0);
        elevator.go(0, 3);
        assertThat(elevator.nextMove()).isEqualTo("CLOSE");

        assertThat(elevator.getCabins()[0].getCurrentFloor()).isEqualTo(0);

        assertThat(elevator.nextMove()).isEqualTo("UP");
        assertThat(elevator.nextMove()).isEqualTo("UP");
        assertThat(elevator.nextMove()).isEqualTo("UP");

        assertThat(elevator.getCabins()[0].getCurrentFloor()).isEqualTo(3);

        assertThat(elevator.nextMove()).isEqualTo("OPEN");
        assertThat(elevator.nextMove()).isEqualTo("CLOSE");
        assertThat(elevator.nextMove()).isEqualTo("UP");

        assertThat(elevator.getCabins()[0].getCurrentFloor()).isEqualTo(4);

        assertThat(elevator.nextMove()).isEqualTo("OPEN");
        elevator.userHasEntered(0);
        elevator.go(0, 1);
        assertThat(elevator.nextMove()).isEqualTo("CLOSE");

        assertThat(elevator.nextMove()).isEqualTo("DOWN");

        assertThat(elevator.nextMove()).isEqualTo("DOWN");
        assertThat(elevator.nextMove()).isEqualTo("DOWN");

        assertThat(elevator.nextMove()).isEqualTo("OPEN");
        assertThat(elevator.nextMove()).isEqualTo("CLOSE");

        assertThat(elevator.nextMove()).isEqualTo("NOTHING");
    }

}

package fr.codestory.elevator;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Miguel Basire
 */
public class DrissElevatorTest {

    @Test
    public void should_do_nothing_when_no_call() {

        DrissElevator groom = new DrissElevator();

        assertThat(groom.getCurrentFloor()).isEqualTo(0);
        assertThat(groom.nextMove()).isEqualTo("NOTHING");
        assertThat(groom.nextMove()).isEqualTo("NOTHING");
        assertThat(groom.getCurrentFloor()).isEqualTo(0);
    }


    @Test
    public void should_move_on_first_call_and_stop() {

        DrissElevator groom = new DrissElevator();

        groom.call(2, ElevatorCommand.Side.UP);

        assertThat(groom.nextMove()).isEqualTo("UP");
        assertThat(groom.nextMove()).isEqualTo("UP");
        assertThat(groom.nextMove()).isEqualTo("OPEN");
        assertThat(groom.nextMove()).isEqualTo("CLOSE");

        assertThat(groom.nextMove()).isEqualTo("NOTHING");
    }

    @Test
    public void should_move_on_first_call_then_go() {

        DrissElevator groom = new DrissElevator();

        groom.call(2, ElevatorCommand.Side.UP);

        assertThat(groom.nextMove()).isEqualTo("UP");
        assertThat(groom.nextMove()).isEqualTo("UP");

        assertThat(groom.nextMove()).isEqualTo("OPEN");
        groom.go(3);
        assertThat(groom.nextMove()).isEqualTo("CLOSE");

        assertThat(groom.nextMove()).isEqualTo("UP");
        assertThat(groom.nextMove()).isEqualTo("OPEN");
        assertThat(groom.nextMove()).isEqualTo("CLOSE");

        assertThat(groom.nextMove()).isEqualTo("NOTHING");
    }

    @Test
    public void should_go_down_on_first_call() {

        DrissElevator groom = new DrissElevator(3);

        groom.call(2, ElevatorCommand.Side.DOWN);

        assertThat(groom.nextMove()).isEqualTo("DOWN");

        assertThat(groom.nextMove()).isEqualTo("OPEN");
        groom.go(1);
        assertThat(groom.nextMove()).isEqualTo("CLOSE");

        assertThat(groom.nextMove()).isEqualTo("DOWN");
        assertThat(groom.nextMove()).isEqualTo("OPEN");
        assertThat(groom.nextMove()).isEqualTo("CLOSE");

        assertThat(groom.nextMove()).isEqualTo("NOTHING");
    }

    @Test
    public void should_go_upside_first() {

        DrissElevator groom = new DrissElevator(3);

        groom.call(2, ElevatorCommand.Side.DOWN);
        groom.call(4, ElevatorCommand.Side.DOWN);

        assertThat(groom.nextMove()).isEqualTo("UP");

        assertThat(groom.nextMove()).isEqualTo("OPEN");
        groom.go(1);
        assertThat(groom.nextMove()).isEqualTo("CLOSE");
        assertThat(groom.getCurrentFloor()).isEqualTo(4);

        assertThat(groom.nextMove()).isEqualTo("DOWN");
        assertThat(groom.nextMove()).isEqualTo("DOWN");
        assertThat(groom.getCurrentFloor()).isEqualTo(2);
        assertThat(groom.nextMove()).isEqualTo("OPEN");
        groom.go(1);
        assertThat(groom.nextMove()).isEqualTo("CLOSE");

        assertThat(groom.nextMove()).isEqualTo("DOWN");

        assertThat(groom.nextMove()).isEqualTo("OPEN");
        assertThat(groom.nextMove()).isEqualTo("CLOSE");

        assertThat(groom.nextMove()).isEqualTo("NOTHING");
    }

    @Test
    public void should_take_someone_near() {

        DrissElevator groom = new DrissElevator(1);
        groom.go(4);

        groom.call(0, ElevatorCommand.Side.UP);
        groom.call(4, ElevatorCommand.Side.DOWN);

        assertThat(groom.nextMove()).isEqualTo("DOWN");

        assertThat(groom.nextMove()).isEqualTo("OPEN");
        groom.go(3);
        assertThat(groom.nextMove()).isEqualTo("CLOSE");

        assertThat(groom.getCurrentFloor()).isEqualTo(0);

        assertThat(groom.nextMove()).isEqualTo("UP");
        assertThat(groom.nextMove()).isEqualTo("UP");
        assertThat(groom.nextMove()).isEqualTo("UP");

        assertThat(groom.getCurrentFloor()).isEqualTo(3);

        assertThat(groom.nextMove()).isEqualTo("OPEN");
        assertThat(groom.nextMove()).isEqualTo("CLOSE");

        assertThat(groom.nextMove()).isEqualTo("UP");
        assertThat(groom.getCurrentFloor()).isEqualTo(4);
        assertThat(groom.nextMove()).isEqualTo("OPEN");
        groom.go(1);
        assertThat(groom.nextMove()).isEqualTo("CLOSE");

        assertThat(groom.nextMove()).isEqualTo("DOWN");
        assertThat(groom.nextMove()).isEqualTo("DOWN");
        assertThat(groom.nextMove()).isEqualTo("DOWN");

        assertThat(groom.nextMove()).isEqualTo("OPEN");
        assertThat(groom.nextMove()).isEqualTo("CLOSE");

        assertThat(groom.nextMove()).isEqualTo("NOTHING");
    }

}

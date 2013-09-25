package fr.codestory.elevator;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Miguel Basire
 */
public class EcologySuckslElevatorCommandTest {

    @Test
    public void should_not_stop_when_nobody_calls() {
        EcologySuckslElevatorCommand groom = new EcologySuckslElevatorCommand();

        assertThat(groom.nextMove()).isEqualTo("UP");
        assertThat(groom.nextMove()).isEqualTo("UP");
        assertThat(groom.nextMove()).isEqualTo("UP");
        assertThat(groom.nextMove()).isEqualTo("UP");
        assertThat(groom.nextMove()).isEqualTo("UP");
        assertThat(groom.currentFloor()).isEqualTo(5);

        assertThat(groom.nextMove()).isEqualTo("DOWN");
        assertThat(groom.nextMove()).isEqualTo("DOWN");
        assertThat(groom.nextMove()).isEqualTo("DOWN");
        assertThat(groom.nextMove()).isEqualTo("DOWN");
        assertThat(groom.nextMove()).isEqualTo("DOWN");

        assertThat(groom.currentFloor()).isEqualTo(0);
        assertThat(groom.nextMove()).isEqualTo("UP");
    }

    @Test
    public void should_take_some_one_on_road() {
        ElevatorCommand groom = new EcologySuckslElevatorCommand();

        assertThat(groom.nextMove()).isEqualTo("UP");

        groom.call(3, ElevatorCommand.Side.UP);

        assertThat(groom.nextMove()).isEqualTo("UP");
        assertThat(groom.nextMove()).isEqualTo("UP");

        assertThat(groom.nextMove()).isEqualTo("OPEN");
        assertThat(groom.nextMove()).isEqualTo("CLOSE");


        assertThat(groom.nextMove()).isEqualTo("UP");
        assertThat(groom.nextMove()).isEqualTo("UP");
    }

    @Test
    public void should_take_someone_on_the_other_side() {
        ElevatorCommand groom = new EcologySuckslElevatorCommand();

        assertThat(groom.nextMove()).isEqualTo("UP");

        groom.call(3, ElevatorCommand.Side.DOWN);

        assertThat(groom.nextMove()).isEqualTo("UP");
        assertThat(groom.nextMove()).isEqualTo("UP");
        assertThat(groom.nextMove()).isEqualTo("UP");
        assertThat(groom.nextMove()).isEqualTo("UP");
    }


    @Test
    public void should_take_someone_on_top() {
        EcologySuckslElevatorCommand groom = new EcologySuckslElevatorCommand();

        assertThat(groom.nextMove()).isEqualTo("UP");

        groom.call(5, ElevatorCommand.Side.DOWN);

        assertThat(groom.nextMove()).isEqualTo("UP");
        assertThat(groom.nextMove()).isEqualTo("UP");
        assertThat(groom.nextMove()).isEqualTo("UP");
        assertThat(groom.nextMove()).isEqualTo("UP");
        assertThat(groom.currentFloor()).isEqualTo(5);

        assertThat(groom.nextMove()).isEqualTo("OPEN");
        assertThat(groom.nextMove()).isEqualTo("CLOSE");
    }

    @Test
    public void should_open_then_close_the_door_after_a_call_request(){
        EcologySuckslElevatorCommand groom = new EcologySuckslElevatorCommand();
        groom.call(0, ElevatorCommand.Side.UP);

        assertThat(groom.nextMove()).isEqualTo("OPEN");
        assertThat(groom.nextMove()).isEqualTo("CLOSE");
        assertThat(groom.nextMove()).isEqualTo("UP");

        assertThat(groom.currentFloor()).isEqualTo(1);
    }

    @Test
    public void should_let_people_leave(){
        EcologySuckslElevatorCommand groom = new EcologySuckslElevatorCommand();
        groom.call(0, ElevatorCommand.Side.UP);
        groom.go(3);

        assertThat(groom.nextMove()).isEqualTo("OPEN");
        assertThat(groom.nextMove()).isEqualTo("CLOSE");
        assertThat(groom.nextMove()).isEqualTo("UP");
        assertThat(groom.nextMove()).isEqualTo("UP");
        assertThat(groom.nextMove()).isEqualTo("UP");

        assertThat(groom.currentFloor()).isEqualTo(3);

        assertThat(groom.nextMove()).isEqualTo("OPEN");
        assertThat(groom.nextMove()).isEqualTo("CLOSE");

        assertThat(groom.nextMove()).isEqualTo("UP");
    }




}

package fr.codestory.elevator;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Miguel Basire
 */
public class OmnibusElevatorTest {

    @Test
    public void should_go_to_the_top_then_go_down_to_the_ground() {

        ElevatorCommand omnibus = new OmnibusElevator();

        for (int floor = 0; floor < 5; floor++) {

            assertThat(omnibus.nextMove()).as("UP floor "+floor).isEqualTo("OPEN");
            assertThat(omnibus.nextMove()).as("UP floor "+floor).isEqualTo("CLOSE");
            assertThat(omnibus.nextMove()).as("UP floor "+floor).isEqualTo("UP");
        }

        for (int floor = 4; floor >=  0; floor--) {

            assertThat(omnibus.nextMove()).as("DOWN floor "+floor).isEqualTo("OPEN");
            assertThat(omnibus.nextMove()).as("DOWN floor "+floor).isEqualTo("CLOSE");
            assertThat(omnibus.nextMove()).as("DOWN floor "+floor).isEqualTo("DOWN");
        }

    }


}

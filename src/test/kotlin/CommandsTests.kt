package fr.codestory.elevator.hodor

import org.junit.Test as test
import fr.codestory.elevator.hodor.HodorElevator.Command
import fr.codestory.elevator.hodor.HodorElevator.Command.OPEN
import fr.codestory.elevator.hodor.HodorElevator.Command.CLOSE
import fr.codestory.elevator.hodor.HodorElevator.Command.UP
import fr.codestory.elevator.hodor.HodorElevator.Command.DOWN
import fr.codestory.elevator.Elevator.Side
import org.assertj.core.api.Assertions.assertThat

class CommandsTests {

    test fun to_upper_floor(){

        assertThat(Commands().call(2, Side.UP))?.isEqualTo(array(UP, UP, OPEN, CLOSE))
        assertThat(Commands().call(3, Side.DOWN))?.isEqualTo(array(UP, UP, UP, OPEN, CLOSE))
        assertThat(Commands().call(4, Side.UP))?.isEqualTo(array(UP, UP, UP,UP, OPEN, CLOSE))
        assertThat(Commands().call(0, Side.UP))?.isEqualTo(array(OPEN, CLOSE))

        assertThat(Commands().go(2))?.isEqualTo(array(UP, UP, OPEN, CLOSE))
        assertThat(Commands().go(3))?.isEqualTo(array(UP, UP, UP, OPEN, CLOSE))
        assertThat(Commands().go(4))?.isEqualTo(array(UP, UP, UP,UP, OPEN, CLOSE))
        assertThat(Commands().go(0))?.isEqualTo(array(OPEN, CLOSE))
    }

    test fun to_lower_floor(){

        assertThat(Commands(7).call(5, Side.UP))?.isEqualTo(array(DOWN, DOWN, OPEN, CLOSE))
        assertThat(Commands(7).call(4, Side.UP))?.isEqualTo(array(DOWN, DOWN, DOWN, OPEN, CLOSE))
        assertThat(Commands(7).call(3, Side.UP))?.isEqualTo(array(DOWN, DOWN, DOWN,DOWN, OPEN, CLOSE))

        assertThat(Commands(7).go(5))?.isEqualTo(array(DOWN, DOWN, OPEN, CLOSE))
        assertThat(Commands(7).go(4))?.isEqualTo(array(DOWN, DOWN, DOWN, OPEN, CLOSE))
        assertThat(Commands(7).go(3))?.isEqualTo(array(DOWN, DOWN, DOWN,DOWN, OPEN, CLOSE))
    }
}

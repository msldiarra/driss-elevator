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

        assertThat(Commands().forCall(2, Side.UP))?.isEqualTo(array(UP, UP, OPEN, CLOSE))
        assertThat(Commands().forCall(3, Side.DOWN))?.isEqualTo(array(UP, UP, UP, OPEN, CLOSE))
        assertThat(Commands().forCall(4, Side.UP))?.isEqualTo(array(UP, UP, UP,UP, OPEN, CLOSE))
        assertThat(Commands().forCall(0, Side.UP))?.isEqualTo(array(OPEN, CLOSE))

        assertThat(Commands().forGo(2))?.isEqualTo(array(UP, UP, OPEN, CLOSE))
        assertThat(Commands().forGo(3))?.isEqualTo(array(UP, UP, UP, OPEN, CLOSE))
        assertThat(Commands().forGo(4))?.isEqualTo(array(UP, UP, UP,UP, OPEN, CLOSE))
        assertThat(Commands().forGo(0))?.isEqualTo(array(OPEN, CLOSE))
    }

    test fun to_lower_floor(){

        assertThat(Commands(7).forCall(5, Side.UP))?.isEqualTo(array(DOWN, DOWN, OPEN, CLOSE))
        assertThat(Commands(7).forCall(4, Side.UP))?.isEqualTo(array(DOWN, DOWN, DOWN, OPEN, CLOSE))
        assertThat(Commands(7).forCall(3, Side.UP))?.isEqualTo(array(DOWN, DOWN, DOWN,DOWN, OPEN, CLOSE))

        assertThat(Commands(7).forGo(5))?.isEqualTo(array(DOWN, DOWN, OPEN, CLOSE))
        assertThat(Commands(7).forGo(4))?.isEqualTo(array(DOWN, DOWN, DOWN, OPEN, CLOSE))
        assertThat(Commands(7).forGo(3))?.isEqualTo(array(DOWN, DOWN, DOWN,DOWN, OPEN, CLOSE))
    }
}

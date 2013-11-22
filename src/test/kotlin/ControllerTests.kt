package fr.codestory.elevator.hodor

import org.junit.Test as test
import fr.codestory.elevator.hodor.HodorElevator.Command
import fr.codestory.elevator.hodor.HodorElevator.Command.OPEN
import fr.codestory.elevator.hodor.HodorElevator.Command.CLOSE
import fr.codestory.elevator.hodor.HodorElevator.Command.UP
import fr.codestory.elevator.hodor.HodorElevator.Command.DOWN
import fr.codestory.elevator.Elevator.Side
import org.assertj.core.api.Assertions.assertThat

class ControllerTests {

    test fun computation_should_return_the_one_with_less_ticks_to_called_floor(){

        val first = User(5); first.waitingTicks = 0
        val second = User(18); second.waitingTicks = 5
        val third = User(4); third.waitingTicks = 0

        val users = hashSetOf(first, second, third)
        val controller = Controller(users)

        assertThat(controller.compute(1))?.isEqualTo(4)
    }

    test fun computation_should_returned_go_floor_with_less_when_there() {

        val first = User(5, 7);
        first.waitingTicks = 0

        val second = User(10, 5);
        second.waitingTicks = 5

        val third = User(5);
        third.waitingTicks = 0

        val users = hashSetOf(first, second, third)

        val controller = Controller(users)

        assertThat(controller.compute(5))?.isEqualTo(7)
    }

}

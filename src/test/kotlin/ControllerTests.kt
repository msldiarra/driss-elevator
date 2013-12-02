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

        val first = User(5, Side.DOWN); first.waitingTicks = 0
        val second = User(18, Side.DOWN); second.waitingTicks = 5
        val third = User(4, Side.DOWN); third.waitingTicks = 0

        val users = hashSetOf(first, second, third)
        val controller = Controller(users)

        assertThat(controller.compute(Cabin(40,1)))?.isEqualTo(4)
    }

    test fun should_returned_go_floor_with_less_ticks_when_there() {

        val first = User(5, Side.DOWN, 7);
        first.waitingTicks = 0

        val second = User(10, Side.DOWN, 5);
        second.waitingTicks = 5

        val third = User(5, Side.DOWN);
        third.waitingTicks = 0

        val users = hashSetOf(first, second, third)

        val controller = Controller(users)

        assertThat(controller.compute(Cabin(40,5)))?.isEqualTo(5)
    }

    test fun should_return_call_with_less_waiting_ticks_when_go_floor_provide_no_point() {

        val first = User(-3, Side.DOWN, 26); first.waitingTicks = 200; first.travellingTicks = 50
        val second = User(5,Side.DOWN);

        val users = hashSetOf(first, second)

        val controller = Controller(users)

        assertThat(controller.compute(Cabin(40,-2)))?.isEqualTo(5)

    }

}

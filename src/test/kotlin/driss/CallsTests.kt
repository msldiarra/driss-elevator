package driss

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test as test
import fr.codestory.elevator.Elevator.Side
import driss.Calls
import driss.ElevatorRequest

class CallsTests {


    test fun inscrease_should_initialize_upside() {

        val calls = Calls(ElevatorRequest.NONE, ElevatorRequest.NONE)

        calls.increase(Side.UP)

        assertThat(calls.up)!!.isNotEqualTo(ElevatorRequest.NONE)
        assertThat(calls.up.number)!!.isEqualTo(1)

        assertThat(calls.down)!!.isEqualTo(ElevatorRequest.NONE)
    }

    test fun inscrease_should_initialize_downside() {

        val calls = Calls(ElevatorRequest.NONE, ElevatorRequest.NONE)

        calls.increase(Side.DOWN)

        assertThat(calls.down)!!.isNotEqualTo(ElevatorRequest.NONE)
        assertThat(calls.down.number)!!.isEqualTo(1)

        assertThat(calls.up)!!.isEqualTo(ElevatorRequest.NONE)
    }


    test fun inscrease_should_add_1_each_time() {

        val calls = Calls(ElevatorRequest.NONE, ElevatorRequest(1))

        calls.increase(Side.DOWN)

        assertThat(calls.down.number)!!.isEqualTo(2)

        calls.increase(Side.DOWN)

        assertThat(calls.down.number)!!.isEqualTo(3)
    }

    test fun inscrease_should_do_nothing_on_UNKNOWN_side() {

        val calls = Calls(up = ElevatorRequest(1), down = ElevatorRequest(1))

        calls.increase(Side.UNKOWN)

        assertThat(calls.down.number)!!.isEqualTo(1)
        assertThat(calls.up.number)!!.isEqualTo(1)
    }

}

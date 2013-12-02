package fr.codestory.elevator.hodor

import org.junit.Test as test
import org.assertj.core.api.Assertions.assertThat
import fr.codestory.elevator.Elevator.Side
import fr.codestory.elevator.hodor.HodorElevator.Command
import fr.codestory.elevator.hodor.User.State
import kotlin.test.assertTrue

class SandorElevatorTests {

    test fun call_should_add_new_user_with_call_floor(){

        val elevator = SandorElevator()
        elevator.call(1, Side.UP)

        assertThat(elevator.users)?.hasSize(1)
    }

    test fun multiple_calls_should_add_same_count_user() {

        val elevator = SandorElevator()
        elevator.call(1, Side.UP)
        elevator.call(1, Side.UP)
        elevator.call(1, Side.UP)
        elevator.call(1, Side.UP)

        assertThat(elevator.users)?.hasSize(4)
    }

    test fun call_to_n_floor_should_result_in_n_UPs_commands_from_ground_floor(){

        val elevator = SandorElevator()
        elevator.call(2, Side.UP)

        assertThat(elevator.nextMove())?.isEqualTo("UP\nNOTHING")
        assertThat(elevator.nextMove())?.isEqualTo("UP\nNOTHING")
    }

    test fun call_to_n_floor_should_result_in_n_DOWNs_commands_from_upper_floor(){

        val elevator = SandorElevator(hashMapOf(Pair(0,Cabin(40,5))))
        elevator.call(2, Side.UP)

        assertThat(elevator.nextMove())?.isEqualTo("DOWN\nNOTHING")
        assertThat(elevator.nextMove())?.isEqualTo("DOWN\nNOTHING")
        assertThat(elevator.nextMove())?.isEqualTo("DOWN\nNOTHING")
    }

    test fun user_should_be_removed_when_at_destination(){

        val elevator = SandorElevator()
        elevator.call(0, Side.UP)

        assertThat(elevator.nextMove())?.isEqualTo("OPEN\nNOTHING")
        elevator.userHasEntered(0)
        elevator.go(0,1)
        assertThat(elevator.nextMove())?.isEqualTo("CLOSE\nNOTHING")
        assertThat(elevator.nextMove())?.isEqualTo("UP\nNOTHING")
        assertThat(elevator.nextMove())?.isEqualTo("OPEN\nNOTHING")
        elevator.userHasExited(0)
        assertThat(elevator.nextMove())?.isEqualTo("CLOSE\nNOTHING")
        assertThat(elevator.nextMove())?.isEqualTo("NOTHING\nNOTHING")

        assertThat(elevator.users)?.hasSize(0)
    }

    test fun when_user_enters_elevator_state_changes_to_travelling() {

        /*//user which has been wainting most enters first
        val first = User(9,Side.DOWN); first.waitingTicks = 99
        val second = User(9,Side.DOWN); second.waitingTicks = 10

        val elevator = SandorElevator(hashMapOf(Pair(0,Cabin(40,9))))
        elevator.users.add(first)
        elevator.users.add(second)
        elevator.userHasEntered(0)

        assertThat(first.state)?.isEqualTo(State.TRAVELLING)*/
    }

    test fun user_ticks_should_increment_on_each_move(){

        val first = User(3, Side.DOWN, 10); first.waitingTicks = 5; first.travellingTicks = 2; first.state = State.TRAVELLING
        val second = User(4,Side.DOWN); second.waitingTicks = 10

        val elevator = SandorElevator();
        elevator.users.add(first)
        elevator.users.add(second)

        elevator.nextMove()

        assertThat(first.waitingTicks)?.isEqualTo(5)
        assertThat(second.waitingTicks)?.isEqualTo(11)
        assertThat(first.travellingTicks)?.isEqualTo(3)
        assertThat(second.travellingTicks)?.isEqualTo(0)
    }

    test fun should_go_to_nearest_with_most_point() {


        val elevator = SandorElevator();

        elevator.call(1,Side.UP)
        elevator.call(1,Side.UP)

        assertThat(elevator.nextMove())?.isEqualTo("UP\nNOTHING")
        assertThat(elevator.nextMove())?.isEqualTo("OPEN\nNOTHING")
        elevator.userHasEntered(0)
        elevator.go(0,8)
        elevator.userHasEntered(0)
        elevator.go(0,4)
        assertThat(elevator.nextMove())?.isEqualTo("CLOSE\nNOTHING")
        assertThat(elevator.nextMove())?.isEqualTo("UP\nNOTHING")
        assertThat(elevator.nextMove())?.isEqualTo("UP\nNOTHING")
        assertThat(elevator.nextMove())?.isEqualTo("UP\nNOTHING")
        assertThat(elevator.nextMove())?.isEqualTo("OPEN\nNOTHING")
        elevator.userHasExited(0)
        assertThat(elevator.nextMove())?.isEqualTo("CLOSE\nNOTHING")
        assertThat(elevator.nextMove())?.isEqualTo("UP\nNOTHING")
        assertThat(elevator.nextMove())?.isEqualTo("UP\nNOTHING")
        assertThat(elevator.nextMove())?.isEqualTo("UP\nNOTHING")
        assertThat(elevator.nextMove())?.isEqualTo("UP\nNOTHING")
        assertThat(elevator.nextMove())?.isEqualTo("OPEN\nNOTHING")
        elevator.userHasExited(0)
        assertThat(elevator.nextMove())?.isEqualTo("CLOSE\nNOTHING")

    }

    test fun when_going_to_upper_call_floor_should_take_call_at_lower_floor() {

        val elevator = SandorElevator(hashMapOf(Pair(0,Cabin(40,4)), Pair(1,Cabin(40,4))))

        elevator.call(12, Side.DOWN)
        assertThat(elevator.nextMove())?.isEqualTo("UP\nNOTHING")
        assertThat(elevator.nextMove())?.isEqualTo("UP\nNOTHING")
        elevator.call(3, Side.UP)
        assertThat(elevator.nextMove())?.isEqualTo("UP\nDOWN")
        assertThat(elevator.nextMove())?.isEqualTo("UP\nOPEN")
        /*assertThat(elevator.nextMove())?.isEqualTo("DOWN\nNOTHING")
        assertThat(elevator.nextMove())?.isEqualTo("OPEN\nNOTHING")
        elevator.userHasEntered(0)
        elevator.go(0,4)
        assertThat(elevator.nextMove())?.isEqualTo("CLOSE\nNOTHING")
        assertThat(elevator.nextMove())?.isEqualTo("UP\nNOTHING")
        assertThat(elevator.nextMove())?.isEqualTo("OPEN\nNOTHING")
        elevator.userHasExited(0)
        assertThat(elevator.nextMove())?.isEqualTo("CLOSE\nNOTHING")
        assertThat(elevator.score)?.isEqualTo(18)
        assertThat(elevator.nextMove())?.isEqualTo("UP\nNOTHING")
        assertThat(elevator.nextMove())?.isEqualTo("UP\nNOTHING")
        assertThat(elevator.nextMove())?.isEqualTo("UP\nNOTHING")
        assertThat(elevator.nextMove())?.isEqualTo("UP\nNOTHING")
        assertThat(elevator.nextMove())?.isEqualTo("UP\nNOTHING")
        assertThat(elevator.nextMove())?.isEqualTo("UP\nNOTHING")
        assertThat(elevator.nextMove())?.isEqualTo("UP\nNOTHING")
        assertThat(elevator.nextMove())?.isEqualTo("UP\nNOTHING")
        assertThat(elevator.nextMove())?.isEqualTo("OPEN\nNOTHING")
        elevator.userHasExited(0)
        assertThat(elevator.nextMove())?.isEqualTo("CLOSE\nNOTHING")
        assertThat(elevator.score)?.isEqualTo(18)*/

    }

    test fun first_call_should_not_return_nothing() {

        val elevator = SandorElevator()
        elevator.reset()
        elevator.call(3, Side.DOWN)

        assertThat(elevator.nextMove())?.isEqualTo("UP\nNOTHING")
    }

}
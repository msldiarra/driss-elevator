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

        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.toString())
        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.toString())
    }

    test fun call_to_n_floor_should_result_in_n_DOWNs_commands_from_upper_floor(){

        val elevator = SandorElevator(5)
        elevator.call(2, Side.UP)

        assertThat(elevator.nextMove())?.isEqualTo(Command.DOWN.toString())
        assertThat(elevator.nextMove())?.isEqualTo(Command.DOWN.toString())
        assertThat(elevator.nextMove())?.isEqualTo(Command.DOWN.toString())
    }

    test fun user_should_be_removed_when_at_destination(){

        val elevator = SandorElevator()
        elevator.call(0, Side.UP)

        assertThat(elevator.nextMove())?.isEqualTo("OPEN")
        elevator.userHasEntered()
        elevator.go(1)
        assertThat(elevator.nextMove())?.isEqualTo("CLOSE")
        assertThat(elevator.nextMove())?.isEqualTo("UP")
        assertThat(elevator.nextMove())?.isEqualTo("OPEN")
        elevator.userHasExited()
        assertThat(elevator.nextMove())?.isEqualTo("CLOSE")
        assertThat(elevator.nextMove())?.isEqualTo("NOTHING")

        assertThat(elevator.users)?.hasSize(0)
    }

    test fun when_user_enters_elevator_state_changes_to_travelling() {

        //user which has been wainting most enters first
        val first = User(9); first.waitingTicks = 99
        val second = User(9); second.waitingTicks = 10

        val elevator = SandorElevator(9)
        elevator.users.add(first)
        elevator.users.add(second)
        elevator.userHasEntered()

        assertThat(first.state)?.isEqualTo(State.TRAVELLING)
    }

    test fun user_ticks_should_increment_on_each_move(){

        val first = User(3, 10); first.waitingTicks = 5; first.travellingTicks = 2; first.state = State.TRAVELLING
        val second = User(4); second.waitingTicks = 10

        val elevator = SandorElevator();
        elevator.users.add(first)
        elevator.users.add(second)

        elevator.nextMove()

        assertThat(first.waitingTicks)?.isEqualTo(5)
        assertThat(second.waitingTicks)?.isEqualTo(11)
        assertThat(first.travellingTicks)?.isEqualTo(3)
        assertThat(second.travellingTicks)?.isEqualTo(0)
    }

    test fun next_command_should_not_keep_open_close() {

        val elevator = SandorElevator(3)
        assertThat(elevator.currentFloor)?.isEqualTo(3)

        elevator.call(1, Side.UP)
        elevator.call(1, Side.DOWN)
        assertThat(elevator.nextMove())?.isEqualTo("DOWN")
        assertThat(elevator.nextMove())?.isEqualTo("DOWN")
        assertThat(elevator.nextMove())?.isEqualTo("OPEN")
        elevator.userHasEntered()
        elevator.go(5)
        elevator.userHasEntered()
        elevator.go(0)
        assertThat(elevator.nextMove())?.isEqualTo("CLOSE")
        assertThat(elevator.nextMove())?.isEqualTo("DOWN")
        assertThat(elevator.nextMove())?.isEqualTo("OPEN")
        elevator.call(0, Side.UP)
        elevator.userHasExited()
        assertThat(elevator.nextMove())?.isEqualTo("CLOSE")
        assertThat(elevator.nextMove())?.isEqualTo("UP")
        assertThat(elevator.nextMove())?.isEqualTo("UP")
        assertThat(elevator.nextMove())?.isEqualTo("UP")
        assertThat(elevator.nextMove())?.isEqualTo("UP")
        assertThat(elevator.nextMove())?.isEqualTo("UP")
        assertThat(elevator.nextMove())?.isEqualTo("OPEN")
        elevator.userHasExited()
        assertThat(elevator.nextMove())?.isEqualTo("CLOSE")
        assertThat(elevator.nextMove())?.isEqualTo("DOWN")
        assertThat(elevator.nextMove())?.isEqualTo("DOWN")
        assertThat(elevator.nextMove())?.isEqualTo("DOWN")
        assertThat(elevator.nextMove())?.isEqualTo("DOWN")
        assertThat(elevator.nextMove())?.isEqualTo("DOWN")
        assertThat(elevator.nextMove())?.isEqualTo("OPEN")
        elevator.userHasEntered()
        elevator.go(1)
        assertThat(elevator.nextMove())?.isEqualTo("CLOSE")
        assertThat(elevator.nextMove())?.isEqualTo("UP")
        assertThat(elevator.nextMove())?.isEqualTo("OPEN")
        elevator.userHasExited()
        assertThat(elevator.nextMove())?.isEqualTo("CLOSE")
        assertThat(elevator.nextMove())?.isEqualTo("NOTHING")

    }

    test fun first_call_should_not_return_nothing() {

        val elevator = SandorElevator()
        elevator.reset()
        elevator.call(3, Side.DOWN)

        assertThat(elevator.nextMove())?.isEqualTo("UP")
    }

}
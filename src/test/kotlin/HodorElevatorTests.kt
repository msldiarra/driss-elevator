package fr.codestory.elevator.hodor

import org.junit.Test as test
import org.assertj.core.api.Assertions.assertThat
import fr.codestory.elevator.Elevator.Side
import java.util.SortedMap
import java.util.LinkedHashMap
import java.util.TreeMap
import com.google.common.collect.Maps
import kotlin.test.assertTrue
import fr.codestory.elevator.hodor.HodorElevator.Command

class HodorElevatorTests {

    test fun calls_stack_should_be_increased_when_elevator_is_called(){

        val elevator = HodorElevator();

        elevator.call(1, Side.DOWN)
        assertThat(elevator.calls)?.hasSize(1)

        elevator.call(5, Side.UP)
        assertThat(elevator.calls)?.hasSize(2)

    }

    test fun calls_stacks_should_not_increase_when_calls_contains_call_to_same_floor(){

        val elevator = HodorElevator();

        elevator.call(1, Side.DOWN)
        assertThat(elevator.calls)?.hasSize(1)

        elevator.call(1, Side.DOWN)
        assertThat(elevator.calls)?.hasSize(1)

        elevator.call(1, Side.UP)
        assertThat(elevator.calls)?.hasSize(1)

        assertThat(elevator.moves)?.hasSize(1)
    }

    test fun go_requests_should_increase_when_new_one_is_made(){

        val elevator = HodorElevator()

        elevator.go(5)
        assertThat(elevator.gos)?.hasSize(1)

        elevator.go(4)
        assertThat(elevator.gos)?.hasSize(2)
    }

    test fun go_requests_should_not_increase_when_gos_already_contains_same_floor(){

        val elevator = HodorElevator()

        elevator.go(5)
        assertThat(elevator.gos)?.hasSize(1)

        elevator.go(5)
        assertThat(elevator.gos)?.hasSize(1)

        assertThat(elevator.moves)?.hasSize(1)
    }

    test fun call_request_to_floor_should_add_necessary_commands_to_lead_to_floor(){

        val elevator = HodorElevator();
        elevator.call(2, Side.DOWN)

        val commands = Array<Command>(4, { Command.UP})
        commands.set(2, Command.OPEN)
        commands.set(3, Command.CLOSE)

        assertThat(elevator.moves)?.hasSize(1)
        assertThat(elevator.moves.get(2))?.hasSize(4)
        assertThat(elevator.commands)?.isEqualTo(commands)

    }

    test fun go_request_to_floor_should_add_necessary_commands_to_lead_to_floor(){

        val elevator =  HodorElevator();

        elevator.go(2)

        assertThat(elevator.moves)?.hasSize(1)
        assertThat(elevator.moves.get(2))?.hasSize(4)
    }

    test fun next_move_should_be_nothing_when_call_and_go_stacks_are_empty(){

        val elevator = HodorElevator()

        assertThat(elevator.nextMove())?.isEqualTo("NOTHING");
    }

    test fun next_move_should_be_first_registered_move_in_stack(){

        val elevator = HodorElevator();
        elevator.call(1, Side.UP)

        assertThat(elevator.nextMove())?.isEqualTo("UP")
    }

    test fun call_at_floor_below_current_should_generate_down_commands(){

        val elevator = HodorElevator(3)
        elevator.call(2, Side.UP)

        assertThat(elevator.commands)?.hasSize(3)
        assertThat(elevator.commands.get(0))?.isEqualTo(elevator.Command.DOWN)
    }

    test fun go_at_floor_below_should_generate_down_command(){

        val elevator = HodorElevator(3)
        elevator.go(2)

        assertThat(elevator.commands)?.hasSize(3)
        assertThat(elevator.commands.get(0))?.isEqualTo(elevator.Command.DOWN)
    }

    test fun arriving_at_destination_floor_should_remove_related_move_in_stack(){

        val elevator = HodorElevator()
        elevator.go(2)

        var nextCommand = elevator.nextMove() // UP
        assertThat(nextCommand)?.isEqualTo(Command.UP.name())

        nextCommand = elevator.nextMove() // UP
        assertThat(nextCommand)?.isEqualTo(Command.UP.name())

        nextCommand = elevator.nextMove() // OPEN
        assertThat(nextCommand)?.isEqualTo(Command.OPEN.name())

        nextCommand = elevator.nextMove() // CLOSE
        assertThat(nextCommand)?.isEqualTo(Command.CLOSE.name())

        assertThat(elevator.moves)?.isEmpty()
        assertThat(elevator.currentFloor)?.isEqualTo(2)
    }

    test fun arriving_at_destination_should_put_next_commands_on_top(){

        val elevator = HodorElevator()
        elevator.go(2)
        //assertThat(elevator.moves.lastKey())?.isEqualTo(2)
        elevator.call(5, Side.DOWN)
        //assertThat(elevator.moves.lastKey())?.isEqualTo(5)

        elevator.nextMove()
        elevator.nextMove()
        elevator.nextMove()
        elevator.nextMove() // Elevator is at Second fllor and has closed the door

        assertThat(elevator.nextMove())?.isEqualTo("UP")
        assertThat(elevator.commands)?.hasSize(5)

    }

    test fun door_should_close_after_go(){

        val elevator = HodorElevator()
        elevator.call(0, Side.UP)
        //assertThat(elevator.moves.lastKey())?.isEqualTo(0)
        elevator.nextMove()

        elevator.go(1)
        //assertThat(elevator.moves.lastKey())?.isEqualTo(1)
        elevator.go(4)
        //assertThat(elevator.moves.lastKey())?.isEqualTo(4)

        elevator.nextMove()
        elevator.nextMove()
        elevator.nextMove()

        assertThat(elevator.nextMove())?.isEqualTo("CLOSE")
    }

    test fun should_go_up_after_go(){

        val elevator = HodorElevator()

        elevator.call(0, Side.UP)
        //assertThat(elevator.moves.lastKey())?.isEqualTo(0)

        elevator.go(1)
        elevator.nextMove()

        elevator.call(1, Side.DOWN)
        //assertThat(elevator.moves.lastKey())?.isEqualTo(1)

        assertThat(elevator.nextMove())?.isEqualTo("CLOSE")
        assertThat(elevator.nextMove())?.isEqualTo("UP")
    }

    test fun should_not_go_down_when_at_ground_floor(){

        val elevator = HodorElevator()

        elevator.call(0, Side.UP)
        elevator.call(3, Side.UP)
        elevator.nextMove()
        elevator.go(1)
        elevator.call(0, Side.UP)

        assertThat(elevator.nextMove())?.isEqualTo("CLOSE")
        assertThat(elevator.nextMove())?.isEqualTo("UP")
    }

    /*test fun next_move_should_return_first_element_of_commands() {

        val groom = HodorElevator()

        groom.commands!!.put(1, Side.DOWN)
        groom.commands!!.put(0, Side.UP)
        groom.commands!!.put(2, Side.UNKOWN)

        assertThat(groom.nextMove())!!.isEqualTo(Side.UP.name())
    }

    test fun call_with_most_points_should_be_first_element_of_commands(){

        val calls = listOf(5, 3)

        val before = Maps.newTreeMap<Int, SortedMap<Int, Side>>()

        val after = Shifter(before).optimize(calls)

        val commands_with_most_points = mapOf(Pair(0, mapOf(Pair(0, Side.UP), Pair(1, Side.UP), Pair(2, Side.UP))))

        assertThat(after)!!.isNotEmpty()
        assertThat(after!!.get(0))!!.isEqualTo(commands_with_most_points.get(0))
    }*/
}

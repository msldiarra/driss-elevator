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

        elevator.go(0,5)
        assertThat(elevator.gos)?.hasSize(1)

        elevator.go(0,4)
        assertThat(elevator.gos)?.hasSize(2)
    }

    test fun go_requests_should_not_increase_when_gos_already_contains_same_floor(){

        val elevator = HodorElevator()

        elevator.go(0,5)
        assertThat(elevator.gos)?.hasSize(1)

        elevator.go(0,5)
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

        elevator.go(0,2)

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
        assertThat(elevator.commands?.get(0))?.isEqualTo(elevator.Command.DOWN)
    }

    test fun go_at_floor_below_should_generate_down_command(){

        val elevator = HodorElevator(3)
        elevator.go(0,2)

        assertThat(elevator.nextMove())?.isEqualTo(Command.DOWN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name())  // floor 1
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())  // floor 1
    }

    test fun arriving_at_destination_floor_should_remove_related_move_in_stack(){

        val elevator = HodorElevator()
        elevator.go(0,2)

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
        elevator.go(0,2)
        elevator.call(5, Side.DOWN)

        elevator.nextMove()
        elevator.nextMove()
        elevator.nextMove()
        elevator.nextMove() // Elevator is at Second floor and has closed the door

        assertThat(elevator.nextMove())?.isEqualTo("UP")
        assertThat(elevator.commands)?.hasSize(5)

    }

    test fun door_should_close_after_go(){

        val elevator = HodorElevator()
        elevator.call(0, Side.UP)
        elevator.nextMove()

        elevator.go(0,1)
        elevator.go(0,4)

        elevator.nextMove()
        elevator.nextMove()
        elevator.nextMove()

        assertThat(elevator.nextMove())?.isEqualTo("CLOSE")
    }

    test fun should_go_up_after_go(){

        val elevator = HodorElevator()

        elevator.call(0, Side.UP)

        elevator.go(0,1)
        elevator.nextMove()

        elevator.call(1, Side.DOWN)

        assertThat(elevator.nextMove())?.isEqualTo("CLOSE")
        assertThat(elevator.nextMove())?.isEqualTo("UP")
    }

    test fun should_not_go_down_when_at_ground_floor(){

        val elevator = HodorElevator()

        elevator.call(0, Side.UP)
        elevator.call(3, Side.UP)
        assertThat(elevator.nextMove())?.isEqualTo("OPEN")
        elevator.go(0,1)
        elevator.call(0, Side.UP)

        assertThat(elevator.nextMove())?.isEqualTo("CLOSE")
        assertThat(elevator.nextMove())?.isEqualTo("UP")
    }

    test fun call_at_current_floor_before_destination_floor_should_induce_stop_at_floor(){

        val elevator = HodorElevator();

        elevator.go(0,2)
        elevator.nextMove()
        elevator.call(1, Side.UP)

        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())
    }

    test fun call_at_upper_floor_before_destination_floor_should_induce_stop_at_floor(){

        val elevator = HodorElevator();

        elevator.go(0,3)
        elevator.nextMove()
        elevator.call(2, Side.UP)

        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())
    }

    test fun call_at_lower_floor_before_destination_floor_should_induce_stop_at_floor(){

        val elevator = HodorElevator(4);

        elevator.go(0,1)
        elevator.nextMove()
        elevator.call(2, Side.DOWN)

        assertThat(elevator.nextMove())?.isEqualTo(Command.DOWN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.DOWN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())
    }

    test fun call_at_lower_floor_after_destination_floor_should_not_have_next_command_as_nothing(){

        val elevator = HodorElevator(0);

        elevator.go(0,4)
        elevator.nextMove()
        elevator.nextMove()
        elevator.nextMove()
        elevator.nextMove()
        elevator.nextMove()
        elevator.go(0,5)
        elevator.call(0, Side.UP)

        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())
    }

    test fun should_not_go_up_when_highest_floor(){

        val elevator = HodorElevator();

        elevator.go(0,5)
        elevator.nextMove()
        elevator.nextMove()
        elevator.nextMove()
        elevator.nextMove()
        elevator.nextMove()
        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name())

        elevator.call(4, Side.DOWN)
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())
        elevator.call(0, Side.UP)
        assertThat(elevator.nextMove())?.isEqualTo(Command.DOWN.name())
    }

    test fun should_not_add_open_when_door_already_open(){

        val elevator = HodorElevator(5);

        elevator.go(0,3)
        elevator.nextMove()
        elevator.nextMove()
        elevator.nextMove()

        elevator.call(1, Side.DOWN)
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())

    }

    test fun up_and_down(){

        val elevator = HodorElevator(2);

        elevator.go(0,5)
        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name())

        elevator.call(4, Side.DOWN)
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.DOWN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name())

        elevator.go(0,0)
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())

        assertThat(elevator.nextMove())?.isEqualTo(Command.DOWN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.DOWN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.DOWN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.DOWN.name())

    }

    test fun two_calls_at_upper_floors_before_destination_should_induce_stops(){

        val elevator = HodorElevator();

        elevator.go(0,4)
        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        elevator.call(2, Side.UP)
        elevator.call(3, Side.UP)

        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())
    }

    test fun call_at_upper_floors_before_destination_should_not_induce_stops_when_cabin_is_full(){

        val elevator = HodorElevator();

        elevator.userHasEntered(0)
        elevator.userHasEntered(0)
        elevator.go(0,5)
        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        elevator.call(2, Side.UP)
        elevator.call(3, Side.UP)
        elevator.call(4, Side.UP)

        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name())
        elevator.userHasExited(0)
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())
    }

    test fun cabin_should_not_stop_when_call_at_same_floor_and_cabin_empty(){

        val elevator = HodorElevator();

        elevator.userHasEntered(0)
        elevator.userHasEntered(0)
        elevator.go(0,2)
        elevator.go(0,2)
        elevator.call(0, Side.UP)


        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name())
        elevator.userHasExited(0)
        elevator.userHasExited(0)
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.DOWN.name())
    }

/*    test fun call_at_upper_floors_before_destination_should_induce_stops_when_user_exits(){

        val elevator = HodorElevator();

        elevator.userHasEntered(0)
        elevator.userHasEntered(0)
        elevator.go(0,2)
        elevator.go(0,5)
        elevator.call(3, Side.UP)

        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name())
        elevator.userHasExited(0)
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())


        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name())
        elevator.userHasExited(0)
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name())
        elevator.userHasExited(0)
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())
    }*/

    test fun do_not_stop_at_floor_when_cabin_is_full(){

        val elevator = HodorElevator();

        elevator.userHasEntered(0) // A enters
        elevator.userHasEntered(0) // B enters

        elevator.go(0,4)
        elevator.go(0,5)
        elevator.call(2, Side.UP)  // C calls

        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name()) // floor 4
        elevator.userHasExited(0)  // A exits
        elevator.userHasEntered(0) // D enters
        elevator.go(0,1)
        assertThat(elevator.usersInCabin)?.isEqualTo(2)
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())  // floor 5
        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name())
        elevator.userHasExited(0)  // B exits
        elevator.userHasEntered(0) // E enters
        assertThat(elevator.usersInCabin)?.isEqualTo(2)
        elevator.go(0,1)
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.DOWN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.DOWN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.DOWN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.DOWN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name())  // floor 1
        elevator.userHasExited(0)  // D exits
        elevator.userHasExited(0)  // E exits
        assertThat(elevator.usersInCabin)?.isEqualTo(0)
        elevator.call(2, Side.DOWN)
        elevator.userHasEntered(0)  // C exits
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name()) // floor 2
        elevator.userHasExited(0)  // C exits
        assertThat(elevator.usersInCabin)?.isEqualTo(0)
        elevator.call(1, Side.DOWN)
        elevator.userHasEntered(0)
        elevator.go(0,-1)
        elevator.userHasEntered(0)
        elevator.go(0,-1)
        assertThat(elevator.usersInCabin)?.isEqualTo(2)
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.DOWN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.DOWN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.DOWN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name())
        elevator.userHasExited(0)
        elevator.userHasExited(0)
        assertThat(elevator.usersInCabin)?.isEqualTo(0)
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())



    }

    test fun do_not_stop_at_floor_when_cabin_is_full_and_no_one_is_leaving(){

        val elevator = HodorElevator();

        elevator.userHasEntered(0)
        elevator.userHasEntered(0)

        elevator.go(0,4)
        elevator.go(0,5)
        elevator.call(3, Side.UP)

        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name())
        elevator.userHasExited(0)
        elevator.userHasEntered(0)
        elevator.go(0,1)
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name())
        elevator.userHasExited(0)
        elevator.userHasEntered(0)
        elevator.go(0,1)
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.DOWN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.DOWN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.DOWN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.DOWN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name())
        elevator.userHasExited(0)
        elevator.userHasExited(0)
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name())
        elevator.userHasExited(0)
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())

    }

/*    test fun should_not_take_when_full_and_going_down(){

        val elevator = HodorElevator(5)

        elevator.call(3, Side.DOWN)
        elevator.call(4, Side.DOWN)

        assertThat(elevator.nextMove())?.isEqualTo(Command.DOWN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name())
        elevator.userHasEntered(0)
        elevator.go(0,0)
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.DOWN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name())
        elevator.go(0,0)
        elevator.call(0, Side.DOWN)
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.DOWN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.DOWN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.DOWN.name())


    }*/

    test fun do_not_go_up_when_doors_are_open(){

        val elevator = HodorElevator();

        elevator.call(0, Side.UP)
        elevator.call(0, Side.UP)
        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name())
        elevator.userHasEntered(0)
        elevator.go(0,2)
        elevator.userHasEntered(0)
        elevator.go(0,1)
        elevator.call(3, Side.UP)
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name())
        elevator.userHasExited(0)
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name())
        elevator.userHasExited(0)
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.UP.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name())
        elevator.userHasEntered(0)
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())
    }

    test fun two(){

        val elevator = HodorElevator(4);

        elevator.userHasEntered(0)
        elevator.call(0, Side.UP)
        elevator.userHasExited(0)
        elevator.call(0, Side.UP)

        assertThat(elevator.nextMove())?.isEqualTo(Command.DOWN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.DOWN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.DOWN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.DOWN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.OPEN.name())
        assertThat(elevator.nextMove())?.isEqualTo(Command.CLOSE.name())

    }

    /*

    test fun call_with_most_points_should_be_first_element_of_commands(){

        val calls = listOf(5, 3)

        val before = Maps.newTreeMap<Int, SortedMap<Int, Side>>()

        val after = Shifter(before).optimize(calls)

        val commands_with_most_points = mapOf(Pair(0, mapOf(Pair(0, Side.UP), Pair(1, Side.UP), Pair(2, Side.UP))))

        assertThat(after)!!.isNotEmpty()
        assertThat(after!!.get(0))!!.isEqualTo(commands_with_most_points.get(0))
    }*/
}

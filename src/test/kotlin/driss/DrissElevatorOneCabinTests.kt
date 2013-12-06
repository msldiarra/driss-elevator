package driss

import org.junit.Test as test
import org.assertj.core.api.Assertions.assertThat
import fr.codestory.elevator.Elevator.Side
import fr.codestory.elevator.BuildingDimension
import driss.DrissElevator.MoveCommand.*


class DrissElevatorOneCabinTests {


    test fun go_should_increment_only_request_at_its_floor() {

        with(DrissElevator(cabinNumber = 1, cabinSize = 2)) {
            go(0, 1)

            assertThat(cabins[0].gos.at(1).number)!!.isEqualTo(1)
            assertThat(cabins[0].gos.at(2).number)!!.isEqualTo(0)
        }
    }

    test fun go_should_not_take_someone_if_cabin_is_full() {

        with(DrissElevator(cabinNumber = 1, cabinSize = 1)) {

            cabins[0].peopleInside = 1
            go(0, 2)

            call(1, Side.UP)

            UP()
            UP()
            OPEN_UP()
            userHasExited(0)
            CLOSE()
            DOWN()
            OPEN_DOWN()
            userHasEntered(0)
            CLOSE()
        }
    }

    test fun should_not_move_beyond_building_limit_on_call() {

        with(DrissElevator(initialFloor = 0, dimension = BuildingDimension(0, 1), cabinSize = 1, cabinNumber = 1)) {
            call(1, Side.DOWN)

            UP()
            OPEN_UP()
            userHasEntered(0)
            CLOSE()
            NOTHING()
        }
    }

    test fun should_not_go_beyond_building_limit_on_go() {

        with(DrissElevator(initialFloor = 0, dimension = BuildingDimension(0, 1), cabinSize = 1, cabinNumber = 1)) {
            go(0, 1)

            UP()
            OPEN_UP()

            userHasExited(0)
            CLOSE()
            NOTHING()
        }
    }

    test fun should_move_to_the_nearest_call_first() {

        with(DrissElevator(initialFloor = 0, dimension = BuildingDimension(-3, 8), cabinSize = 1, cabinNumber = 1)) {
            call(-3, Side.UP)
            call(8, Side.DOWN)

            DOWN()
        }
    }

    test fun call_should_be_removed_when_user_has_entered() {

        with(DrissElevator(initialFloor = 8, dimension = BuildingDimension(-3, 8), cabinSize = 1, cabinNumber = 1)) {

            assertThat(calls.at(8))!!.isEmpty()

            call(8, Side.DOWN)
            assertThat(calls.at(8))!!.hasSize(1)

            call(8, Side.DOWN)
            assertThat(calls.at(8))!!.hasSize(2)

            OPEN_then_CLOSE {
                userHasEntered(0)
                userHasEntered(0)
            }

            assertThat(calls.at(8))!!.hasSize(0)
            assertThat(calls.at(8))!!.isSameAs(calls.noneValue)

            call(8, Side.DOWN)
            assertThat(calls.at(8))!!.hasSize(1)
        }
    }

    test fun bug_last_floor_user_has_entered_tries_to_remove_unexisting_call() {

        with(DrissElevator(initialFloor = 0, dimension = BuildingDimension(0, 1), cabinSize = 1, cabinNumber = 1)) {

            call(0, Side.UP)
            OPEN()
            userHasEntered_and_go(1)
            call(1, Side.DOWN)
            CLOSE()
            UP()

            assertThat(cabins[0].currentFloor)!!.isEqualTo(1)
            OPEN_UP()
            userHasExited(0)
            userHasEntered(0)
            go(0, 0)
            CLOSE()
            DOWN()

            assertThat(cabins[0].currentFloor)!!.isEqualTo(0)
            OPEN_DOWN()

            userHasExited(0)
            CLOSE()
            assertThat(calls.signaledFloors())!!.isEmpty()
        }
    }

    test fun should_not_forget_someone_is_waiting() {

        with(DrissElevator(initialFloor = 1, dimension = BuildingDimension(0, 2), cabinSize = 1, cabinNumber = 1)) {
            call(1, Side.UP)
            call(1, Side.DOWN)

            OPEN_then_CLOSE {
                userHasEntered_and_go(2)
            }
            assertThat(cabins[0].canAcceptSomeone())!!.isFalse()

            UP()

            OPEN_UP()
            userHasExited(0)
            CLOSE()

            assertThat(cabins[0].canAcceptSomeone())!!.isTrue()

            DOWN()

            OPEN_DOWN()
            userHasEntered_and_go(0)
            CLOSE()

            DOWN()
            OPEN_DOWN()
            userHasEntered(0)
            CLOSE()
            NOTHING()
        }
    }

    test public fun userHasEntered_should_detect_no_more_call_floor() {

        with(DrissElevator(
                initialFloor = 0,
                dimension = BuildingDimension(0, 5),
                cabinSize = 1,
                cabinNumber = 1
        )) {

            assertThat(calls.at(cabins[0].currentFloor))!!.isEqualTo(calls.noneValue)
            call(0, Side.UP)

            assertThat(calls.at(cabins[0].currentFloor))!!.hasSize(1)!!.isNotSameAs(calls.noneValue)

            userHasEntered(0)

            assertThat(calls.at(0))!!.isSameAs(calls.noneValue)
        }
    }

    test fun whenever_door_is_open_nextmove_should_be_CLOSE() {
        with(DrissElevator(
                cabinSize = 1,
                cabinNumber = 1
        )) {

            cabins[0].door.opened = true
            CLOSE()
        }
    }

    private inline fun DrissElevator.OPEN_then_CLOSE <T>  (enclosed: () -> T): Unit {
        OPEN()

        enclosed.invoke()

        CLOSE()
        this
    }

    private inline fun DrissElevator.userHasEntered_and_go(floor: Int) {
        userHasEntered(0)
        go(0, floor)
        this
    }

    private fun DrissElevator.OPEN() {
        assertThat(nextMove())!!.isEqualTo("OPEN")
    }
    private fun DrissElevator.OPEN_UP() {
        assertThat(nextMove())!!.isEqualTo("OPEN_UP")
    }
    private fun DrissElevator.OPEN_DOWN() {
        assertThat(nextMove())!!.isEqualTo("OPEN_DOWN")
    }


    private fun DrissElevator.CLOSE() {
        assertThat(nextMove())!!.isEqualTo("CLOSE")
    }


    private inline fun DrissElevator.UP() {
        assertThat(nextMove())!!.isEqualTo("UP")
    }

    private inline fun DrissElevator.DOWN() {
        assertThat(nextMove())!!.isEqualTo("DOWN")
    }

    private inline fun DrissElevator.NOTHING() {
        assertThat(nextMove())!!.isEqualTo("NOTHING")
    }
}

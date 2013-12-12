package driss

import org.junit.Test as test
import org.assertj.core.api.Assertions.assertThat

import fr.codestory.elevator.Elevator.Side
import fr.codestory.elevator.BuildingDimension

import driss.assertions.*


class DrissElevatorOneCabinTests {

    val firstCabin = 0

    test fun go_should_increment_only_request_at_its_floor() {

        with(DrissElevator(cabinNumber = 1, cabinSize = 2)) {
            go(firstCabin, 1)

            assertThat(cabins[0].gos.at(1).number)!!.isEqualTo(1)
            assertThat(cabins[0].gos.at(2).number)!!.isEqualTo(0)
        }
    }

    test fun go_should_not_take_someone_if_cabin_is_full() {

        with(DrissElevator(cabinNumber = 1, cabinSize = 1)) {

            cabins[firstCabin].peopleInside = 1
            go(firstCabin, 2)

            call(1, Side.UP)

            UP()
            UP()
            OPEN()
            userHasExited(firstCabin)
            CLOSE()
            DOWN()
            OPEN()
            userHasEntered(firstCabin)
            CLOSE()
        }
    }

    test fun should_not_move_beyond_building_limit_on_call() {

        with(DrissElevator(initialFloor = 0, dimension = BuildingDimension(0, 1), cabinSize = 1, cabinNumber = 1)) {
            call(1, Side.DOWN)

            UP()
            OPEN()
            userHasEntered(firstCabin)
            CLOSE()
            NOTHING()
        }
    }

    test fun should_not_go_beyond_building_limit_on_go() {

        with(DrissElevator(initialFloor = 0, dimension = BuildingDimension(0, 1), cabinSize = 1, cabinNumber = 1)) {
            go(firstCabin, 1)

            UP()
            OPEN()

            userHasExited(firstCabin)
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
                userHasEntered(firstCabin)
                userHasEntered(firstCabin)
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
            userHasEntered(firstCabin)
            go(firstCabin, 1)
            call(1, Side.DOWN)
            CLOSE()
            UP()

            assertThat(cabins[firstCabin].currentFloor)!!.isEqualTo(1)
            OPEN()
            userHasExited(firstCabin)
            userHasEntered(firstCabin)
            go(firstCabin, 0)
            CLOSE()
            DOWN()

            assertThat(cabins[firstCabin].currentFloor)!!.isEqualTo(0)
            OPEN()

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
                userHasEntered(firstCabin)
                go(firstCabin, 2)
            }
            assertThat(cabins[firstCabin].canAcceptSomeone())!!.isFalse()

            UP()

            OPEN()
            userHasExited(firstCabin)
            CLOSE()

            assertThat(cabins[firstCabin].canAcceptSomeone())!!.isTrue()

            DOWN()

            OPEN()
            userHasEntered(firstCabin)
            go(firstCabin, 0)
            CLOSE()

            DOWN()
            OPEN()
            userHasEntered(firstCabin)
            CLOSE()
            NOTHING()
        }
    }


    test fun userHasEntered_should_decrease_calls_at_floor() {

        with(DrissElevator(
                dimension = BuildingDimension(-1, 30),
                cabinSize = 30,
                cabinNumber = 1,
                initialFloor = 0)) {

            call(0, Side.UP)
            call(0, Side.UP)

            assertThat(calls.signaledFloors())!!.hasSize(1)
            assertThat(calls.at(0))!!.hasSize(2)

            userHasEntered(firstCabin)

            assertThat(calls.signaledFloors())!!.hasSize(1)
            assertThat(calls.at(0))!!.hasSize(1)

            userHasEntered(firstCabin)

            assertThat(calls.signaledFloors())!!.hasSize(0)
            assertThat(calls.at(0))!!.hasSize(0)
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

            userHasEntered(firstCabin)

            assertThat(calls.at(0))!!.isSameAs(calls.noneValue)
        }
    }

    test fun whenever_door_is_open_nextmove_should_be_CLOSE() {
        with(DrissElevator(
                cabinSize = 1,
                cabinNumber = 1
        )) {

            cabins[firstCabin].door.opened = true
            CLOSE()
        }
    }

    test fun a_go_should_not_allow_someone_going_the_wrong_way_to_enter() {
        with(DrissElevator(
                cabinSize = 1,
                cabinNumber = 1,
                dimension = BuildingDimension(0, 30),
                initialFloor = 0

        )) {

            cabins[firstCabin].door.opened = false

            userHasEntered(0)
            go(0, 2)
            call(1, Side.DOWN)

            UP()
            UP()
            OPEN()
            userHasExited(firstCabin)
            CLOSE()
            DOWN()
            OPEN()
            userHasEntered(firstCabin)
            go(0, 0)
            CLOSE()
            DOWN()
        }
    }

    test fun only_upside_call_should_be_removed_after_OPEN_UP() {
        with(DrissElevator(
                cabinSize = 2,
                cabinNumber = 1,
                dimension = BuildingDimension(0, 30),
                initialFloor = 0

        )) {

            userHasEntered(firstCabin)
            go(firstCabin, 2)

            call(1, Side.DOWN)
            call(1, Side.UP)

            UP()
            OPEN_UP()
            userHasEntered(firstCabin)

            assertThat(calls.at(1).going(Side.UP))!!.hasSize(0)
            assertThat(calls.at(1).going(Side.DOWN))!!.hasSize(1)
        }
    }

}

package driss

import org.junit.Test as test
import org.assertj.core.api.Assertions.assertThat
import fr.codestory.elevator.Elevator.Side
import fr.codestory.elevator.Cabin
import fr.codestory.elevator.BuildingDimension

class DrissElevatorTests {


    test fun go_should_increment_number_of_request_at_floor() {

        with(DrissElevator()) {
            go(1)
            assertThat(gos.at(1).number)!!.isEqualTo(1)

            go(1)
            assertThat(gos.at(1).number)!!.isEqualTo(2)
        }
    }


    test fun go_should_increment_only_request_at_its_floor() {

        with(DrissElevator()) {
            go(1)

            assertThat(gos.at(1).number)!!.isEqualTo(1)
            assertThat(gos.at(2).number)!!.isEqualTo(0)
        }
    }

    test fun go_should_not_take_someone_if_cabin_is_full() {

        with(DrissElevator(currentFloor = 0, cabin = Cabin(1, 1))) {
            call(1, Side.UP)
            go(2)

            assertThat(nextMove())!!.isEqualTo("UP")
            assertThat(nextMove())!!.isEqualTo("UP")
            assertThat(nextMove())!!.isEqualTo("OPEN")
            assertThat(nextMove())!!.isEqualTo("CLOSE")
            userHasExited()
            assertThat(nextMove())!!.isEqualTo("DOWN")
            assertThat(nextMove())!!.isEqualTo("OPEN")
            assertThat(nextMove())!!.isEqualTo("CLOSE")
        }
    }

    test fun should_not_go_beyond_building_limit_on_call() {

        with(DrissElevator(currentFloor = 0, cabin = Cabin(1, 0), dimension = BuildingDimension(0, 1))) {
            call(1, Side.DOWN)

            assertThat(nextMove())!!.isEqualTo("UP")
            assertThat(nextMove())!!.isEqualTo("OPEN")
            userHasEntered()
            assertThat(nextMove())!!.isEqualTo("CLOSE")

            assertThat(nextMove())!!.isEqualTo("NOTHING")
        }
    }

    test fun should_not_go_beyond_building_limit_on_go() {

        with(DrissElevator(currentFloor = 0, cabin = Cabin(1, 0), dimension = BuildingDimension(0, 1))) {
            go(1)

            assertThat(nextMove())!!.isEqualTo("UP")
            assertThat(nextMove())!!.isEqualTo("OPEN")
            assertThat(nextMove())!!.isEqualTo("CLOSE")
            assertThat(nextMove())!!.isEqualTo("NOTHING")
        }
    }

    test fun should_move_down_before_down() {

        with(DrissElevator(currentFloor = 0, cabin = Cabin(1, 0), dimension = BuildingDimension(-3, 8))) {
            call(-3, Side.UP)
            call(8, Side.DOWN)

            assertThat(nextMove())!!.isEqualTo("DOWN")
        }
    }

    test fun should_not_forget_someone_is_calling() {

        with(DrissElevator(currentFloor = 0, cabin = Cabin(1, 0), dimension = BuildingDimension(0, 1))) {
            call(0, Side.UP)
            call(0, Side.UP)

            assertThat(nextMove())!!.isEqualTo("OPEN")
            userHasEntered()
            go(1)
            assertThat(cabin.canAcceptSomeone())!!.isFalse()
            assertThat(nextMove())!!.isEqualTo("CLOSE")
            assertThat(nextMove())!!.isEqualTo("UP")

            assertThat(nextMove())!!.isEqualTo("OPEN")
            userHasExited()
            assertThat(cabin.canAcceptSomeone())!!.isTrue()
            assertThat(nextMove())!!.isEqualTo("CLOSE")

            assertThat(nextMove())!!.isEqualTo("DOWN")

            assertThat(nextMove())!!.isEqualTo("OPEN")
            userHasEntered()
            assertThat(nextMove())!!.isEqualTo("CLOSE")
            assertThat(nextMove())!!.isEqualTo("NOTHING")
        }
    }
}

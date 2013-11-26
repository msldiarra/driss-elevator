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

            up()
            up()
            open_then_close { userHasExited() }
            down()
            open_then_close { userHasEntered() }
        }
    }

    test fun should_not_go_beyond_building_limit_on_call() {

        with(DrissElevator(currentFloor = 0, cabin = Cabin(1, 0), dimension = BuildingDimension(0, 1))) {
            call(1, Side.DOWN)

            up()
            open_then_close { userHasEntered() }
            nothing()
        }
    }

    test fun should_not_go_beyond_building_limit_on_go() {

        with(DrissElevator(currentFloor = 0, cabin = Cabin(1, 0), dimension = BuildingDimension(0, 1))) {
            go(1)

            up()
            open_then_close { userHasExited() }
            nothing()
        }
    }

    test fun should_move_down_before_down() {

        with(DrissElevator(currentFloor = 0, cabin = Cabin(1, 0), dimension = BuildingDimension(-3, 8))) {
            call(-3, Side.UP)
            call(8, Side.DOWN)

            down()
        }
    }

    test fun should_not_forget_someone_is_waiting() {

        with(DrissElevator(currentFloor = 1, cabin = Cabin(1, 0), dimension = BuildingDimension(0, 2))) {
            call(1, Side.UP)
            call(1, Side.DOWN)

            open_then_close {
                goTo(2)
            }
            assertThat(cabin.canAcceptSomeone())!!.isFalse()

            up()

            open_then_close {
                userHasExited()
            }

            assertThat(cabin.canAcceptSomeone())!!.isTrue()

            down()

            open_then_close {
                goTo(0)
            }

            down()
            open_then_close { }
        }
    }

    private inline fun DrissElevator.open_then_close <T>  (enclosed: () -> T): Unit {
        assertThat(nextMove())!!.isEqualTo("OPEN")

        enclosed.invoke()

        assertThat(nextMove())!!.isEqualTo("CLOSE")
        this
    }

    private inline fun DrissElevator.goTo(floor: Int) {
        userHasEntered()
        go(floor)
        this
    }

    private inline fun DrissElevator.up() {
        assertThat(nextMove())!!.isEqualTo("UP")
    }

    private inline fun DrissElevator.down() {
        assertThat(nextMove())!!.isEqualTo("DOWN")
    }

    private inline fun DrissElevator.nothing() {
        assertThat(nextMove())!!.isEqualTo("NOTHING")
    }
}

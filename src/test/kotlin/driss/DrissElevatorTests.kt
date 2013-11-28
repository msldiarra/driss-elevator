package driss

import org.junit.Test as test
import org.assertj.core.api.Assertions.assertThat
import fr.codestory.elevator.Elevator.Side
import fr.codestory.elevator.BuildingDimension

class DrissElevatorTests {


    test fun go_should_increment_number_of_request_at_floor() {

        with(DrissElevator(cabinNumber = 1, cabinSize = 2)) {
            firstCabinGoesTo(1)
            assertThat(cabins[0].gos.at(1).number)!!.isEqualTo(1)

            firstCabinGoesTo(1)
            assertThat(cabins[0].gos.at(1).number)!!.isEqualTo(2)
        }
    }


    test fun go_should_increment_only_request_at_its_floor() {

        with(DrissElevator(cabinNumber = 1, cabinSize = 2)) {
            firstCabinGoesTo(1)

            assertThat(cabins[0].gos.at(1).number)!!.isEqualTo(1)
            assertThat(cabins[0].gos.at(2).number)!!.isEqualTo(0)
        }
    }

    test fun go_should_not_take_someone_if_cabin_is_full() {

        with(DrissElevator(cabinNumber = 1, cabinSize = 1)) {

            cabins[0].peopleInside = 1
            firstCabinGoesTo(2)

            call(1, Side.UP)

            up()
            up()
            open_then_close { userHasExited(0) }
            down()
            open_then_close { userHasEntered(0) }
        }
    }

    test fun should_not_go_beyond_building_limit_on_call() {

        with(DrissElevator(initialFloor = 0, dimension = BuildingDimension(0, 1), cabinSize = 1, cabinNumber = 1)) {
            call(1, Side.DOWN)

            up()
            open_then_close { userHasEntered(0) }
            nothing()
        }
    }

    test fun should_not_go_beyond_building_limit_on_go() {

        with(DrissElevator(initialFloor = 0, dimension = BuildingDimension(0, 1), cabinSize = 1, cabinNumber = 1)) {
            firstCabinGoesTo(1)

            up()
            open_then_close { userHasExited(0) }
            nothing()
        }
    }

    test fun should_move_down_before_down() {

        with(DrissElevator(initialFloor = 0, dimension = BuildingDimension(-3, 8), cabinSize = 1, cabinNumber = 1)) {
            call(-3, Side.UP)
            call(8, Side.DOWN)

            down()
        }
    }

    test fun should_not_forget_someone_is_waiting() {

        with(DrissElevator(initialFloor = 1, dimension = BuildingDimension(0, 1), cabinSize = 1, cabinNumber = 1)) {
            call(1, Side.UP)
            call(1, Side.DOWN)

            open_then_close {
                goTo(2)
            }
            assertThat(cabins[0].canAcceptSomeone())!!.isFalse()

            up()

            open_then_close {
                userHasExited(0)
            }

            assertThat(cabins[0].canAcceptSomeone())!!.isTrue()

            down()

            open_then_close {
                goTo(0)
            }

            down()
            open_then_close { }
        }
    }

    private inline fun DrissElevator.open_then_close <T>  (enclosed: () -> T): Unit {
        assertThat(nextMove())!!.isEqualTo("OPEN\n")

        enclosed.invoke()

        assertThat(nextMove())!!.isEqualTo("CLOSE\n")
        this
    }

    private inline fun DrissElevator.goTo(floor: Int) {
        userHasEntered(0)
        firstCabinGoesTo(floor)
        this
    }

    private inline fun DrissElevator.up() {
        assertThat(nextMove())!!.isEqualTo("UP\n")
    }

    private inline fun DrissElevator.down() {
        assertThat(nextMove())!!.isEqualTo("DOWN\n")
    }

    private inline fun DrissElevator.nothing() {
        assertThat(nextMove())!!.isEqualTo("NOTHING\n")
    }

    private inline fun DrissElevator.firstCabinGoesTo(floor: Int) {
        go(0, floor)
    }
}

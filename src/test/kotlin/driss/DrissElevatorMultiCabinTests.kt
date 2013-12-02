package driss

import org.junit.Test as test
import org.assertj.core.api.Assertions.assertThat
import fr.codestory.elevator.Elevator.Side
import fr.codestory.elevator.BuildingDimension
import driss.DrissElevator.MoveCommand.*


class DrissElevatorMultiCabinTests {


    test public fun should_not_change_no_call_list() {

        with(DrissElevator(
                initialFloor = 0,
                dimension = BuildingDimension(0, 5),
                cabinSize = 1,
                cabinNumber = 2
        )) {

            val noneValue = calls.noneValue

            call(1, Side.UP)

            assertThat(calls.noneValue)!!
                    .isEqualTo(noneValue)!!
                    .isEmpty()
        }
    }

    test public fun should_not_throw_NPE_on_empty_call_list_for_goes_decision() {

        with(DrissElevator(
                initialFloor = 5,
                dimension = BuildingDimension(0, 5),
                cabinSize = 1,
                cabinNumber = 2
        )) {

            userHasEntered(0)

            assertThat(cabins[0].peopleInside)!!.isEqualTo(1)
            assertThat(cabins[1].peopleInside)!!.isEqualTo(0)

            firstCabinGoesTo(4)

            moves(DOWN, NOTHING)
            open_then_close { userHasExited(1) }
        }
    }


    private inline fun DrissElevator.open_then_close <T>  (enclosed: () -> T): Unit {
        assertThat(nextMove())!!.startsWith("OPEN")

        enclosed.invoke()

        assertThat(nextMove())!!.startsWith("CLOSE")
        this
    }

    private inline fun DrissElevator.goTo(floor: Int) {
        userHasEntered(0)
        firstCabinGoesTo(floor)
        this
    }

    private inline fun DrissElevator.moves(vararg commands: DrissElevator.MoveCommand) {

        assertThat(nextMove())!!.isEqualTo(commands.makeString(separator = "\n"))

    }

    private inline fun DrissElevator.firstCabin() {
        cabins[0]
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

    private inline fun DrissElevator.firstCabinGoesTo(floor: Int) {
        go(0, floor)
    }


}

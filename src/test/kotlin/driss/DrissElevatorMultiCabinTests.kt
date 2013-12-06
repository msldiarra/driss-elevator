package driss

import org.junit.Test as test
import org.assertj.core.api.Assertions.assertThat
import fr.codestory.elevator.Elevator.Side
import fr.codestory.elevator.BuildingDimension
import driss.DrissElevator.MoveCommand.*
import driss.Door.Command.*


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
            OPEN_then_CLOSE { userHasExited(1) }
        }
    }

    test fun should_accept_call_at_lower_floor() {

        with(DrissElevator(
                dimension = BuildingDimension(-1, 48),
                cabinSize = 30,
                cabinNumber = 2)) {

            call(-1, Side.UP)
            moves(DOWN, DOWN)

            moves(OPEN, OPEN)
            userHasEntered(0)
            go(0, 0)
            moves(CLOSE, CLOSE)
            moves(UP, NOTHING)
            moves(OPEN, NOTHING)
            userHasExited(0)
            moves(CLOSE, NOTHING)
        }
    }

    test fun should_leave_someone_going_in_the_wrong_side() {

        with(DrissElevator(
                dimension = BuildingDimension(-1, 30),
                cabinSize = 30,
                cabinNumber = 2)) {

            call(5, Side.UP)

            moves(UP, UP)
            call(3, Side.DOWN)
            moves(UP, UP)
            moves(UP, UP)
            moves(UP, UP)
            moves(UP, UP)

            moves(OPEN, OPEN)
            userHasEntered(0)
            moves(CLOSE, CLOSE)
        }
    }

    private fun DrissElevator.OPEN_then_CLOSE <T>  (enclosed: () -> T): Unit {
        assertThat(nextMove())!!.startsWith("OPEN")

        enclosed.invoke()

        assertThat(nextMove())!!.startsWith("CLOSE")
        this
    }

    private  fun DrissElevator.goTo(floor: Int) {
        userHasEntered(0)
        firstCabinGoesTo(floor)
        this
    }

    private fun DrissElevator.moves(vararg commands: Any) {

        assertThat(nextMove())!!.isEqualTo(commands.makeString(separator = "\n"))

    }

    private fun DrissElevator.firstCabin() {
        cabins[0]
    }

    private fun DrissElevator.up() {
        assertThat(nextMove())!!.isEqualTo("UP")
    }

    private fun DrissElevator.down() {
        assertThat(nextMove())!!.isEqualTo("DOWN")
    }

    private fun DrissElevator.nothing() {
        assertThat(nextMove())!!.isEqualTo("NOTHING")
    }

    private fun DrissElevator.firstCabinGoesTo(floor: Int) {
        go(0, floor)
    }


}

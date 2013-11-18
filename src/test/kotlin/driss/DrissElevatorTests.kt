package driss

import org.junit.Test as test
import org.assertj.core.api.Assertions.assertThat
import fr.codestory.elevator.Elevator.Side
import fr.codestory.elevator.Cabin
import fr.codestory.elevator.BuildingDimension

class DrissElevatorTests {


    test fun go_should_increment_number_of_request_at_floor() {

        val elevator = DrissElevator()

        elevator.go(1)

        assertThat(elevator.gos.at(1).number)!!.isEqualTo(1)

        elevator.go(1)

        assertThat(elevator.gos.at(1).number)!!.isEqualTo(2)
    }


    test fun go_should_increment_only_request_at_its_floor() {

        val elevator = DrissElevator()

        elevator.go(1)

        assertThat(elevator.gos.at(1).number)!!.isEqualTo(1)
        assertThat(elevator.gos.at(2).number)!!.isEqualTo(0)
    }

    test fun go_not_take_someone_if_cabin_is_full() {

        val elevator = DrissElevator(currentFloor = 0, cabin = Cabin(1, 1))

        elevator.call(1, Side.UP)
        elevator.go(2)

        assertThat(elevator.nextMove())!!.isEqualTo("UP")
        assertThat(elevator.nextMove())!!.isEqualTo("UP")
        assertThat(elevator.nextMove())!!.isEqualTo("OPEN")
        assertThat(elevator.nextMove())!!.isEqualTo("CLOSE")
        elevator.userHasExited()
        assertThat(elevator.nextMove())!!.isEqualTo("DOWN")
        assertThat(elevator.nextMove())!!.isEqualTo("OPEN")
        assertThat(elevator.nextMove())!!.isEqualTo("CLOSE")
    }

    test fun should_not_go_beyond_building_limit_on_call() {

        val elevator = DrissElevator(currentFloor = 0, cabin = Cabin(1, 0), dimension = BuildingDimension(0, 1))

        elevator.call(1, Side.DOWN)

        assertThat(elevator.nextMove())!!.isEqualTo("UP")
        assertThat(elevator.nextMove())!!.isEqualTo("OPEN")
        assertThat(elevator.nextMove())!!.isEqualTo("CLOSE")
        assertThat(elevator.nextMove())!!.isEqualTo("NOTHING")
    }

    test fun should_not_go_beyond_building_limit_on_go() {

        val elevator = DrissElevator(currentFloor = 0, cabin = Cabin(1, 0), dimension = BuildingDimension(0, 1))

        elevator.go(1)

        assertThat(elevator.nextMove())!!.isEqualTo("UP")
        assertThat(elevator.nextMove())!!.isEqualTo("OPEN")
        assertThat(elevator.nextMove())!!.isEqualTo("CLOSE")
        assertThat(elevator.nextMove())!!.isEqualTo("NOTHING")
    }

    test fun should_go_up_before_down() {

        val elevator = DrissElevator(currentFloor = 0, cabin = Cabin(1, 0), dimension = BuildingDimension(-3, 8))

        elevator.call(-3, Side.UP)
        elevator.call(8, Side.DOWN)

        assertThat(elevator.nextMove())!!.isEqualTo("DOWN")
    }
}

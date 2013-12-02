
package fr.codestory.elevator;

import org.junit.Test as test
import org.assertj.core.api.Assertions.assertThat
import fr.codestory.elevator.driss.DrissElevator

class DrissElevatorTests {


    test fun go_should_increment_number_of_request_at_floor(){

        val cabin = DrissElevator()

        cabin.go(0,1)

        assertThat(cabin.gos.at(1).number)!!.isEqualTo(1)

        cabin.go(0,1)

        assertThat(cabin.gos.at(1).number)!!.isEqualTo(2)
    }


    test fun go_should_increment_only_request_at_its_floor(){

        val cabin = DrissElevator()

        cabin.go(0,1)

        assertThat(cabin.gos.at(1).number)!!.isEqualTo(1)
        assertThat(cabin.gos.at(2).number)!!.isEqualTo(0)
    }
}

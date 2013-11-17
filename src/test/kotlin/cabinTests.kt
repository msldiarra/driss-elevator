package fr.codestory.elevator


import org.junit.Test as test
import fr.codestory.elevator.Cabin
import org.assertj.core.api.Assertions.assertThat


class CabinTests {

    test public fun should_allow_someone_to_enter() {

        val cabin = Cabin(capacity = 3)

        assertThat(cabin.peopleInside)!!.isEqualTo(0)

        cabin.userHasEntered()

        assertThat(cabin.peopleInside)!!.isEqualTo(1)
    }

    test public fun should_let_someone_leave() {

        val cabin = Cabin(capacity = 3, peopleInside = 1)

        cabin.userHasExited()

        assertThat(cabin.peopleInside)!!.isEqualTo(0)
    }

    test public fun should_tell_if_someone_can_enter() {

        val cabin = Cabin(capacity = 1)

        assertThat(cabin.canAcceptSomeone())!!.isTrue()
    }


}




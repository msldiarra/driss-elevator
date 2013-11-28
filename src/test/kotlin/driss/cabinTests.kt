package driss


import org.junit.Test as test
import org.assertj.core.api.Assertions.assertThat
import fr.codestory.elevator.BuildingDimension


class CabinTests {

    test public fun should_allow_someone_to_enter() {

        val cabin = cabin(3)

        assertThat(cabin.peopleInside)!!.isEqualTo(0)

        cabin.userHasEntered()

        assertThat(cabin.peopleInside)!!.isEqualTo(1)
    }

    test public fun should_let_someone_leave() {

        val cabin = cabin(3)

        cabin.peopleInside = 1

        cabin.userHasExited()

        assertThat(cabin.peopleInside)!!.isEqualTo(0)
    }

    test public fun should_tell_if_someone_can_enter() {

        val cabin = cabin(1)

        assertThat(cabin.canAcceptSomeone())!!.isTrue()
    }

    test public fun should_tell_if_someone_can_not_enter() {

        val cabin = cabin(1)

        cabin.peopleInside = 1

        assertThat(cabin.canAcceptSomeone())!!.isFalse()
    }


    private fun noGoSignal(): Signals<Go> = signals(BuildingDimension(0, 0), Go(0))
    private fun cabin(capacity: Int): Cabin = Cabin(noGoSignal(), capacity, 0)

}




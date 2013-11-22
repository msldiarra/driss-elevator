package fr.codestory.elevator.hodor

import org.junit.Test as test
import org.assertj.core.api.Assertions.assertThat
import fr.codestory.elevator.hodor.User.State


class UserTests {

    test fun ticks_should_increment_when_waiting(){

        val user = User(0)

        assertThat(user.waitingTicks)?.isEqualTo(0)
        user.tick()
        assertThat(user.waitingTicks)?.isEqualTo(1)
        user.tick()
        assertThat(user.waitingTicks)?.isEqualTo(2)
        user.tick()
        assertThat(user.waitingTicks)?.isEqualTo(3)
    }

    test fun ticks_should_increment_when_travelling(){

        val user = User(0); user.state = State.TRAVELLING

        assertThat(user.travellingTicks)?.isEqualTo(0)
        user.tick()
        assertThat(user.travellingTicks)?.isEqualTo(1)
        user.tick()
        assertThat(user.travellingTicks)?.isEqualTo(2)

    }
}
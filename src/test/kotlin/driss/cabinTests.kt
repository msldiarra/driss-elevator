package driss


import org.junit.Test as test
import org.assertj.core.api.Assertions.assertThat
import fr.codestory.elevator.BuildingDimension
import fr.codestory.elevator.Elevator.Side
import driss.DrissElevator.MoveCommand.*


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

    test fun openTheDoor_when_no_move_command() {

        val cabin = cabin(1)
        cabin.door.opened = false

        assertThat(cabin.groom.commands.side)!!.isEqualTo(Side.UNKOWN)
        assertThat(cabin.groom.openTheDoor())!!.isEqualTo(Door.Command.OPEN)
    }

    test fun openTheDoor_when_side_is_up() {

        val cabin = cabin(1)
        cabin.door.opened = false

        cabin.groom.commands = Commands(Side.UP, array(UP))
        assertThat(cabin.groom.openTheDoor())!!.isEqualTo(Door.Command.OPEN_UP)
    }

    test fun openTheDoor_when_side_is_down() {

        val cabin = cabin(1)
        cabin.door.opened = false

        cabin.groom.commands = Commands(Side.DOWN, array(DOWN))
        assertThat(cabin.groom.openTheDoor())!!.isEqualTo(Door.Command.OPEN_DOWN)
    }

    test fun should_closeTheDoor() {

        val cabin = cabin(1)
        cabin.door.opened = true

        assertThat(cabin.groom.closeTheDoor())!!.isEqualTo(Door.Command.CLOSE)
    }


    private fun noGoSignal(): Signals<Go> = signals(BuildingDimension(0, 0), Go(0))
    private fun cabin(capacity: Int): Cabin = Cabin(noGoSignal(), capacity, 0)

}




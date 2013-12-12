package driss.assertions

import org.assertj.core.api.Assertions.assertThat
import fr.codestory.elevator.Elevator


public fun Elevator.OPEN_then_CLOSE <T>  (enclosed: () -> T): Unit {
    OPEN()

    enclosed.invoke()

    CLOSE()
    this
}


public fun Elevator.OPEN() {
    assertThat(nextMove())!!.isEqualTo("OPEN")
}

public fun Elevator.OPEN_UP() {
    assertThat(nextMove())!!.isEqualTo("OPEN_UP")
}

public fun Elevator.OPEN_DOWN() {
    assertThat(nextMove())!!.isEqualTo("OPEN_DOWN")
}


public fun Elevator.CLOSE() {
    assertThat(nextMove())!!.isEqualTo("CLOSE")
}


public fun Elevator.UP() {
    assertThat(nextMove())!!.isEqualTo("UP")
}

public fun Elevator.DOWN() {
    assertThat(nextMove())!!.isEqualTo("DOWN")
}

public fun Elevator.NOTHING() {
    assertThat(nextMove())!!.isEqualTo("NOTHING")
}

fun Elevator.moves(vararg commands: Any) {

    assertThat(nextMove())!!.isEqualTo(commands.makeString(separator = "\n"))

}


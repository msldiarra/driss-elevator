package driss

class Door(var opened: Boolean = false){

    enum class Command{ OPEN  CLOSE OPEN_UP OPEN_DOWN
    }

    inline fun toggle(onChange: ((Door.Command) -> Door.Command) = { it }) = if (opened) {
        opened = false
        onChange(Command.CLOSE)
    }
    else {
        opened = true
        onChange(Command.OPEN)
    }
}

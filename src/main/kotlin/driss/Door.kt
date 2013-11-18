package driss

class Door(var opened: Boolean = false){

    enum class Command{ OPEN  CLOSE
    }

    inline fun toggle(onOpen: (() -> Unit)?) = if (opened) {
        opened = false
        Command.CLOSE
    }
    else {
        opened = true
        onOpen?.invoke()
        Command.OPEN
    }
}

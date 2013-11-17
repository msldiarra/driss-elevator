package fr.codestory.elevator


class Cabin(val capacity: Int, var peopleInside: Int = 0){

    public fun canAcceptSomeone(): Boolean = capacity > peopleInside

    public fun userHasEntered(): Unit {
        if (peopleInside < capacity) peopleInside++
    }

    public fun userHasExited() {
        if (peopleInside > 0) peopleInside--
    }

}

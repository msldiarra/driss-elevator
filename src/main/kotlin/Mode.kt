package fr.codestory.elevator.hodor


trait Mode {

    fun compute()
}

class BaseMode (val currentFloor: Int, val calls: List<CallRequest>, val gos: List<GoRequest>) : Mode {

    override fun compute() {
        throw UnsupportedOperationException()
    }
}

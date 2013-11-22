package fr.codestory.elevator.hodor


class User(val callFloor:Int, var destinationFloor: Int = 1000) {

    var waitingTicks: Int = 0
    var travellingTicks: Int = 0
    var state: State = State.WAITING

    public fun tick() {
        if (waiting()) waitingTicks++
        if(travelling()) travellingTicks++
    }

    private fun waiting() : Boolean{
        return state == State.WAITING
    }

    private fun travelling() : Boolean{
        return state == State.TRAVELLING
    }

    enum class State {
        WAITING
        TRAVELLING
        ARRIVED
    }
}

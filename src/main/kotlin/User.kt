package fr.codestory.elevator.hodor


class User(val callFloor:Int, var destinationFloor: Int = 1000) {

    var waitingTicks: Int = 0
    var travellingTicks: Int = 0
    var state: State = State.WAITING

    public fun tick() {
        if (waiting()) waitingTicks++
        if(travelling()) travellingTicks++
    }

    fun isWaiting() : Boolean {
        return this.state == State.WAITING
    }

    fun isTravelling() : Boolean {
        return this.state == State.TRAVELLING
    }

    fun take(floor: Int) {
        destinationFloor  = floor
        state = State.TRAVELLING
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

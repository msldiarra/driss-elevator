package fr.codestory.elevator.hodor

import java.lang.Math.abs
import fr.codestory.elevator.hodor.HodorElevator.Command
import fr.codestory.elevator.hodor.Cabin.Door.State
import java.util.HashSet
import java.util.HashMap

class Controller(val users: HashSet<User> = hashSetOf<User>()) {

    /**
     * Should compute floor to go with most point
     */
    fun compute(cabin: Cabin): Int {

        if(cabin.users.isEmpty() && users.isEmpty()) return cabin.currentFloor

        var newUser: User? = null

        val floorToGo =  when {

            cabin.users.size() == 30 -> {

                cabin.users.filter {  u -> u.isTravelling()  }
                    .sortBy { u -> (u.waitingTicks + 2 * abs(cabin.currentFloor - u.destinationFloor))}
                    .first().destinationFloor }

            cabin.users.filterNot {  u -> u.isWaiting() }.count { u ->  Score().isPositiveForGo(u, cabin.currentFloor)  } > 0 ->{

                cabin.users.filterNot {  u -> u.isWaiting() && !Score().isPositiveForGo(u, cabin.currentFloor) }
                            .sortBy { u -> (u.waitingTicks + 2 * abs(cabin.currentFloor - u.destinationFloor))}
                            .first().destinationFloor
            }

            cabin.users.filter {  u -> u.isWaiting() }.count() > 0 -> {

                cabin.users.sortBy { u -> u.waitingTicks + 2 * abs(cabin.currentFloor - u.callFloor) }.first().callFloor
            }

            else -> {
                newUser = users.sortBy { u -> u.waitingTicks + 2 * abs(cabin.currentFloor - u.callFloor) }.first()
                newUser?.callFloor!!
            }
        }

        if(newUser != null) registerUserIn(newUser!!, cabin)

        return floorToGo
    }

    fun nextCommand(cabin: Cabin, floorToGo: Int) : String {

        return when {

            cabin.users.isEmpty() && cabin.door.state == State.OPEN  ->  cabin.door.close().name()

            cabin.users.isEmpty() && cabin.door.state == State.CLOSED -> Command.NOTHING.name()

            cabin.door.state == State.OPEN ->   cabin.door.close().name()

            cabin.currentFloor.isAbove(floorToGo) -> { cabin.currentFloorToLower(); Command.DOWN.name() }

            cabin.currentFloor.isUnder(floorToGo) -> { cabin.currentFloorToUpper(); Command.UP.name() }

            cabin.currentFloor.isSameAs(floorToGo) ->   cabin.door.open().name()

            else ->  Command.NOTHING.name()
        }
    }

    fun resetCabins(cabinCount:Int, cabinSize: Int, initialFloor: Int) : Map<Int, Cabin> {

        var cabins = hashMapOf<Int, Cabin>()
        var i =0
        do {
            cabins.put(i, Cabin(cabinSize, initialFloor))
            i++
        } while(i< cabinCount)

        return cabins
    }

    fun takeIn(cabin: Cabin, usersToTake: HashSet<User>) {

        var users = usersToTake.filter { u -> u.isWaiting()  && cabin.currentFloor == u.callFloor }

        if(users.isEmpty()) users = cabin.users.filter { u -> u.isWaiting()  && cabin.currentFloor == u.callFloor }

        val user = users.sortBy{ u -> u.waitingTicks }.last()

        user.state = User.State.TRAVELLING

        cabin.users.add(user)
    }

    fun go(cabin: Cabin, to: Int) {
        cabin.users.filter { u -> u.isTravelling() && u.destinationFloor == 1000 }
                .sortBy{ u -> u.waitingTicks }.last().destinationFloor = to
    }

    fun leaveUserFrom(cabin: Cabin) {
        val user = cabin.users.find { user -> user.destinationFloor == cabin.currentFloor && user.state == User.State.TRAVELLING }!!
        cabin.users.remove(user)
    }

    private fun registerUserIn(user: User, cabin: Cabin){
        cabin.users.add(user)
        users.remove(user)
    }
}

class Score {

    fun isPositiveForGo(u: User, currentFloor: Int): Boolean {

        val under =  22 + abs(u.callFloor - u.destinationFloor) > u.waitingTicks/2 + abs(currentFloor - u.destinationFloor)
        return under
    }

    fun plus(user: User) : Int {
        return  22 + abs(user.callFloor - user.destinationFloor) - (user.waitingTicks/2 + user.travellingTicks)
    }

}

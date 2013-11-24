package fr.codestory.elevator.hodor

import java.lang.Math.abs

class Controller(val users: Set<User> = hashSetOf<User>()) {

    /**
     * Should compute floor to go with most point
     */
    fun compute(currentFloor: Int): Int {

        if(users.empty) return currentFloor

        val floorToGo =  when {

            users.filterNot{  u -> u.isWaiting() || u.destinationFloor == 1000 }.count { u ->  Score().isPositiveForGo(u, currentFloor)  } > 0 ->
                 users.filterNot {  u -> u.isWaiting() }.sortBy { u -> u.waitingTicks + abs(currentFloor - u.destinationFloor)}
                 .first().destinationFloor

            else -> users.sortBy { u -> u.waitingTicks + 2 * abs(currentFloor - u.callFloor) }.first().callFloor
        }

        return floorToGo
    }

}

class Score {

    fun isPositiveForGo(u: User, currentFloor: Int): Boolean {

        val under =  22 + abs(u.callFloor - u.destinationFloor) > u.waitingTicks/2 + abs(currentFloor - u.destinationFloor)
        return under
    }

}

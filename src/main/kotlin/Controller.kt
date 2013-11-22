package fr.codestory.elevator.hodor

import java.lang.Math.abs

class Controller(val users: Set<User> = hashSetOf<User>()) {

    /**
     * Should compute floor to go with most point
     */
    fun compute(currentFloor: Int): Int {

        if(users.empty) return currentFloor

        val floorToGo =  when {

            users.all { u -> u.destinationFloor == 1000 } -> users.sortBy { u -> u.waitingTicks + abs(currentFloor - u.callFloor) }.first().callFloor

            else -> users.sortBy { u -> u.waitingTicks + abs(currentFloor - u.destinationFloor)}.first().destinationFloor
        }

        return floorToGo
    }
}

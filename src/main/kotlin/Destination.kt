package fr.codestory.elevator.order

import fr.codestory.elevator.Elevator
import java.util.Date
import java.util.ArrayList
import java.util.TreeMap
import java.util.SortedMap
import fr.codestory.elevator.Elevator.Side
import org.omg.CORBA.UNKNOWN

/**
 * @author Miguel Basire
 */
class Destinations<T>(private val destinations: SortedMap<Int, T>, private val noneValue: T) : Iterable<T> {

    public fun add(floor: Int, value: T): Unit {
        destinations.put(floor, value)
    }
    public fun clear(): Unit {
        destinations.clear()
    }
    public fun above(floor: Int): Destinations<T> {
        if (destinations.isEmpty())
            return this
        else
            return Destinations<T>(destinations.tailMap((Math.min(destinations.lastKey() as Int + 1, floor + 1))), noneValue)
    }
    public fun below(floor: Int): Destinations<T> {
        return Destinations<T>(destinations.headMap(floor), noneValue)
    }
    public fun reached(floor: Int): T {
        return destinations.remove(floor) as T
    }
    public fun at(floor: Int): T {
        return (if (destinations.containsKey(floor))
            destinations.get(floor) as T
        else
            noneValue)
    }
    public fun requestedTo(to: Int): Boolean {
        return destinations.containsKey(to)
    }
    public fun isEmpty(): Boolean {
        return destinations.isEmpty()
    }
    public override fun iterator(): Iterator<T> {
        return destinations.values().iterator()
    }
    public fun distanceToFarthestFloorFrom(floor: Int): Int {
        if (destinations.keySet().isEmpty())
        {
            return 0
        }

        var lastFloor = destinations.lastKey() as Int
        var firstFloor = destinations.firstKey() as Int
        return Math.max(Math.abs(floor - lastFloor), Math.abs(floor - firstFloor))
    }
    public  fun distanceToNearestFloorFrom(floor: Int): Int {
        if (destinations.keySet().isEmpty())
        {
            return 0
        }

        var lastFloor = destinations.lastKey() as Int
        var firstFloor = destinations.firstKey() as Int
        return Math.min(Math.abs(floor - lastFloor), Math.abs(floor - firstFloor))
    }

    class object {
        public open fun init<T>(noneValue: T): Destinations<T> {
            val __ = Destinations(TreeMap<Int, T>(), noneValue)
            return __
        }
    }

}


class Calls(var up: ElevatorRequest, var  down: ElevatorRequest) {

    public fun increase(side: Elevator.Side?): Unit {

        inline fun elevatorRequest(side: Side?): ElevatorRequest? {
            when (side) {
                Side.UP -> {
                    if(up == ElevatorRequest.NONE) up = ElevatorRequest(0)
                    return up
                }
                Side.DOWN -> {
                    if(down == ElevatorRequest.NONE) down = ElevatorRequest(0)
                    return down
                }
                Side.UNKOWN -> {
                    return null
                }
            }
        }

        elevatorRequest(side)?.increase()
    }

    public  fun going(side: Elevator.Side): ElevatorRequest {
        if (side == Elevator.Side.UP)
            return up
        else
            return down
    }

    class object {
        public val NONE: Calls = Calls(ElevatorRequest.NONE, ElevatorRequest.NONE)

        public fun create(side: Side): Calls = when(side){
            Side.UP -> { goingUp()}
            Side.DOWN -> { goingDown() }
            else -> { NONE }

        }


        private  fun goingUp(): Calls {
            return Calls(ElevatorRequest(1), ElevatorRequest.NONE)
        }
        private  fun goingDown(): Calls {
            return Calls(ElevatorRequest.NONE, ElevatorRequest(1))
        }
    }
}

data class ElevatorRequest(var number: Int = 1,
                           val timestamp: Date = Date()) {

    public fun increase(): ElevatorRequest {
        if (this == NONE)
            throw IllegalStateException("You can not increase ElevatorRequest.None")

        number++
        return this
    }

    class object {
        public val NONE: ElevatorRequest = ElevatorRequest(0)
    }
}


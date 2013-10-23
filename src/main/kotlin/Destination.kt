package fr.codestory.elevator.order

import fr.codestory.elevator.ElevatorCommand
import java.util.Date
import java.util.ArrayList
import java.util.TreeMap
import java.util.SortedMap

/**
 * @author Miguel Basire
 */
class Destinations<T>(val destinations: SortedMap<Int, T>, private val noneValue: T) : Iterable<T> {

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
    public fun contains(to: Int): Boolean {
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
    fun list(): List<T> {
        return ArrayList<T>(destinations.values())
    }

    class object {
        public open fun init<T>(noneValue: T): Destinations<T> {
            val __ = Destinations(TreeMap<Int, T>(), noneValue)
            return __
        }
    }
}



class Calls(private var up: ElevatorRequest, private var  down: ElevatorRequest) {

    public  fun increase(side: ElevatorCommand.Side?): Unit {
        var sideToIncrease: ElevatorRequest? = (if (side == ElevatorCommand.Side.UP)
            up
        else
            down)
        if (sideToIncrease == ElevatorRequest.NONE)
        {
            if (side == ElevatorCommand.Side.UP)
            {
                up = ElevatorRequest()
            }
            else
                if (side == ElevatorCommand.Side.DOWN)
                {
                    down = ElevatorRequest()
                }
        }
        else
        {
            sideToIncrease?.increase()
        }
    }
    public  fun going(side: ElevatorCommand.Side): ElevatorRequest {
        if (side == ElevatorCommand.Side.UP)
            return up
        else
            return down
    }

    class object {
        public val NONE: Calls = Calls(ElevatorRequest.NONE, ElevatorRequest.NONE)
        public  fun goingUp(): Calls {
            return Calls(ElevatorRequest(), ElevatorRequest.NONE)
        }
        public  fun goingDown(): Calls? {
            return Calls(ElevatorRequest.NONE, ElevatorRequest())
        }
    }
}


data class ElevatorRequest(
        public val timestamp: Date = Date(),
        var number: Int = 1) {

    public fun increase(): ElevatorRequest {
        if (this == NONE)
            throw IllegalStateException("You can not increase ElevatorRequest.None")

        number++
        return this
    }

    class object {
        public val NONE: ElevatorRequest = ElevatorRequest()
    }
}


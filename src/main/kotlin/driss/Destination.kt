package driss

import fr.codestory.elevator.Elevator
import java.util.Date
import java.util.TreeMap
import java.util.SortedMap
import fr.codestory.elevator.Elevator.Side
import java.lang.Math.*
import fr.codestory.elevator.BuildingDimension
import com.google.common.collect.Maps


public fun signals<T>(buildingDimension: BuildingDimension,
                      noneValue: T): Signals<T> {

    val floors = TreeMap<Int, T>()

    with(buildingDimension) {
        for (floor in getLowerFloor()..getHigherFloor()){
            floors.put(floor, noneValue)
        }
    }
    return Signals(floors, noneValue)
}

/**
 * @author Miguel Basire
 */
class Signals<T>(private val floors: SortedMap<Int, T>, private val noneValue: T) : Iterable<T> {

    public fun add(floor: Int, value: T): Unit {
        floors.put(floor, value)
    }

    public fun clear(): Unit = floors.keySet().forEach { k -> floors.put(k, noneValue) }

    public fun above(floor: Int): Signals<out T> =
            Signals<T>(floors.tailMap((min(floors.lastKey() as Int + 1, floor + 1))), noneValue)

    public fun below(floor: Int): Signals<out T> = Signals<T>(floors.headMap(floor), noneValue)

    public fun at(floor: Int): T =
            if (floor in floors.keySet().indices)
                floors.get(floor) as T
            else
                noneValue

    public fun reached(floor: Int): T = floors.put(floor, noneValue) as T

    public fun requestedAt(floor: Int): Boolean = floors.get(floor) != noneValue

    public fun isEmpty(): Boolean = floors.values().all { v -> v == noneValue }

    public override fun iterator(): Iterator<T> = floors.values().iterator()

    public fun nearestFloorFrom(here: Int): Int =
            signaledFloors()!!.keySet().fold(here) { nearest, floor ->
                val previousDistance = abs(nearest - here)
                when {
                    previousDistance == 0 -> {
                        floor
                    }
                    previousDistance > abs(floor - here) -> {
                        floor
                    }
                    else -> {
                        nearest
                    }
                }
            }

    private inline fun  signaledFloors() = Maps.filterEntries(floors) { e -> e?.getValue() != noneValue }

    public fun distanceToFarthestFloorFrom(floor: Int): Int =
            if (isEmpty()) 0
            else{

                val lastFloor = signaledFloors()?.lastKey() as Int
                val firstFloor = signaledFloors()?.firstKey() as Int

                max(abs(floor - lastFloor), abs(floor - firstFloor))
            }

    public  fun distanceToNearestFloorFrom(floor: Int): Int =
            if (isEmpty()) 0
            else {
                val lastFloor = signaledFloors()?.lastKey() as Int
                val firstFloor = signaledFloors()?.firstKey() as Int

                min(abs(floor - lastFloor), abs(floor - firstFloor))
            }
}


public fun calls(side: Side): Calls = when(side) {
    Side.UP -> {
        Calls(ElevatorRequest(1), ElevatorRequest.NONE)
    }
    Side.DOWN -> {
        Calls(ElevatorRequest.NONE, ElevatorRequest(1))
    }
    else -> {
        Calls.NONE
    }
}


class Calls(var up: ElevatorRequest, var  down: ElevatorRequest) {

    public fun increase(side: Elevator.Side?): Unit {

        inline fun elevatorRequest(side: Side?): ElevatorRequest? {
            when (side) {
                Side.UP -> {
                    if (up == ElevatorRequest.NONE) up = ElevatorRequest(0)
                    return up
                }
                Side.DOWN -> {
                    if (down == ElevatorRequest.NONE) down = ElevatorRequest(0)
                    return down
                }
                Side.UNKOWN -> {
                    return null
                }
            }
        }

        elevatorRequest(side)?.increase()
    }

    public  fun going(side: Elevator.Side): ElevatorRequest =
            if (side == Elevator.Side.UP)
                up
            else
                down


    class object {
        public val NONE: Calls = Calls(ElevatorRequest.NONE, ElevatorRequest.NONE)
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


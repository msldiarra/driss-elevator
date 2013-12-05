package driss

import java.util.Date
import java.util.TreeMap
import java.util.SortedMap
import java.lang.Math.*
import fr.codestory.elevator.BuildingDimension
import fr.codestory.elevator.Elevator.Side


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

class Signals<T>(private val floors: SortedMap<Int, T>, val noneValue: T) : Iterable<T>{

    public fun add(floor: Int, value: T): Unit {
        floors.put(floor, value)
    }

    public fun clear(): Unit = floors.keySet().forEach { k -> floors.put(k, noneValue) }

    public fun above(floor: Int): Signals<out T> =
            Signals<T>(floors.tailMap((min(floors.lastKey() as Int + 1, floor + 1))), noneValue)

    public fun below(floor: Int): Signals<out T> = Signals<T>(floors.headMap(floor), noneValue)

    public fun at(floor: Int): T =
            if (floors.keySet().contains(floor))
                floors.get(floor) as T
            else
                noneValue

    public fun reached(floor: Int): T = floors.put(floor, noneValue) as T

    public fun requestedAt(floor: Int): Boolean = !( floors.getOrElse(floor) { noneValue } identityEquals noneValue)

    public fun isEmpty(): Boolean = floors.values().all { v -> v identityEquals noneValue }

    public override fun iterator(): Iterator<T> = floors.values().iterator()

    public fun  signaledFloors(): List<Int> = floors.keySet().filter { !(floors.get(it) identityEquals noneValue) }

    public fun distanceToFarthestFloorFrom(floor: Int): Int =
            if (isEmpty()) 0
            else{

                val lastFloor = signaledFloors().first as Int
                val firstFloor = signaledFloors().last as Int

                max(abs(floor - lastFloor), abs(floor - firstFloor))
            }

    public  fun distanceToNearestFloorFrom(floor: Int): Int =
            if (isEmpty()) 0
            else {

                val lastFloor = signaledFloors().first as Int
                val firstFloor = signaledFloors().last as Int

                min(abs(floor - lastFloor), abs(floor - firstFloor))
            }
}


public fun nearestFloorFrom(here: Int, floors: List<Int>): Int =
        floors.fold(here) { nearest, floor ->
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


trait Signal {

    var number: Int
    var timestamp: Date

    public fun increase(): Signal {
        number++
        return this
    }
}

class Go(number: Int = 1) : Signal {
    override var number = number
    override var timestamp = Date()
}
class Call(val side: Side = Side.UNKOWN, number: Int = 1) : Signal {
    override var number: Int = number
    override var timestamp: Date = Date()
}

public fun List<Call>.going(side: Side): List<Call> = this.filter { it.side == side }


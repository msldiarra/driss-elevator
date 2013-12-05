package fr.codestory.elevator.hodor

import fr.codestory.elevator.Elevator
import fr.codestory.elevator.BuildingDimension
import java.util.HashSet
import java.util.ArrayList
import com.google.common.collect.Iterables
import fr.codestory.elevator.hodor.HodorElevator.Command
import fr.codestory.elevator.ElevatorServer
import org.apache.log4j.Logger
import fr.codestory.elevator.Elevator.Side

class SandorElevator(val dimension: BuildingDimension = BuildingDimension(0, 35), val cabinSize: Int = 30, val cabinCount: Int = 2) : Elevator {

    private val LOG: Logger = Logger.getLogger(javaClass<SandorElevator>()) as Logger

    val users: HashSet<User> = hashSetOf<User>()
    val calls: ArrayList<CallRequest> =  ArrayList<CallRequest>()
    val gos: ArrayList<GoRequest> = ArrayList<GoRequest>()
    val controller = Controller(users)
    var score = 0
    var cabins: Map<Int,Cabin> = Controller().resetCabins(cabinCount, cabinSize, 0)


    override fun nextMove(): String? {

        users.forEach { u -> u.tick() }

        var command = ArrayList<String>()
        for(cabin in cabins.values()) {
            cabin.users.forEach { u -> u.tick() }
            command.add(Controller().nextCommand(cabin, controller.compute(cabin)))
        }

        return command.reduce { (x, y) -> x + "\n" +y }
    }


    override fun reset() {
        users.clear()
        cabins = Controller().resetCabins(cabinCount, cabinSize, 0)
    }


    override fun go(cabin: Int, to: Int) {
        Controller().go(cabins.get(cabin)!!, to)
    }


    override fun call(at: Int, side: Elevator.Side?) {
        users.add(User(at, side as Side))
    }


    override fun userHasEntered(cabin: Int) {
        Controller().takeIn(cabins.get(cabin)!!, users)
    }


    override fun userHasExited(cabin:Int) {
        Controller().leaveUserFrom(cabins.get(cabin)!!)
    }
}




fun Int.isAbove(callFloor: Int) : Boolean {
    return this > callFloor
}

fun Int.isUnder(callFloor: Int) : Boolean {
    return this < callFloor
}

fun Int.isSameAs(floor: Int) : Boolean {
    return this == floor
}

fun Int.isNot(floor: Int) : Boolean {
    return this == floor
}
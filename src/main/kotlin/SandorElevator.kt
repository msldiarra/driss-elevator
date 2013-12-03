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
    var cabins: Map<Int,Cabin> = hashMapOf()


    override fun nextMove(): String? {

        users.forEach { u -> u.tick() }
        cabins.get(0)?.users?.forEach { u -> u.tick() }
        cabins.get(1)?.users?.forEach { u -> u.tick() }

        var firstCabinCommand = Command.NOTHING.name()
        var secondCabinCommand = Command.NOTHING.name()

        val firstCabinDestination = controller.compute(cabins.get(0)!! as Cabin)
        var secondCabinDestination: Int? = null
        if(cabins.get(1) != null) secondCabinDestination = controller.compute(cabins.get(1)!! as Cabin)

        LOG?.info("Floor to Go" + firstCabinDestination)

        firstCabinCommand = Controller().nextCommand(cabins.get(0) as Cabin, firstCabinDestination)
        if(secondCabinDestination != null) {
            secondCabinCommand = Controller().nextCommand(cabins.get(1) as Cabin, secondCabinDestination!!)
        }

        return firstCabinCommand+"\n"+secondCabinCommand
    }


    override fun reset() {
        users.clear()
        cabins = Controller().resetCabins(cabinCount, cabinSize, 0)
    }


    override fun go(cabin: Int, to: Int) {
        Controller().go(cabins.get(cabin)!! as Cabin, to)
    }


    override fun call(at: Int, side: Elevator.Side?) {
        users.add(User(at, side as Side))
    }


    override fun userHasEntered(cabin: Int) {
        Controller().takeIn(cabins.get(cabin)!! as Cabin, users)
    }


    override fun userHasExited(cabin:Int) {
        Controller().leaveUserFrom(cabins.get(cabin)!! as Cabin)
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
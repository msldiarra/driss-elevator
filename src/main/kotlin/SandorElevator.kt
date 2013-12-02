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

class SandorElevator(public var cabins: Map<Int,Cabin> = hashMapOf(Pair(0, Cabin())), val dimension: BuildingDimension = BuildingDimension(0, 24), val cabinSize: Int = 30, val cabinCount: Int = 2) : Elevator {

    private val LOG: Logger = Logger.getLogger(javaClass<SandorElevator>()) as Logger

    val users: HashSet<User> = hashSetOf<User>()
    val calls: ArrayList<CallRequest> =  ArrayList<CallRequest>()
    val gos: ArrayList<GoRequest> = ArrayList<GoRequest>()
    val controller = Controller(users)
    var score = 0



    override fun nextMove(): String? {

        users.forEach { u -> u.tick() }   // Controller will all cabins users
        cabins.get(0)?.users?.forEach { u -> u.tick() }   // Controller will all cabins users
        cabins.get(1)?.users?.forEach { u -> u.tick() }   // Controller will all cabins users

        var firstCabinCommand = Command.NOTHING.name()
        var secondCabinCommand = Command.NOTHING.name()

        // Method to manage response when no users
       /* if(users.isEmpty() && door.state == State.OPEN) return door.close().name()
        if(users.isEmpty() && door.state == State.CLOSED) return  Command.NOTHING.name()*/

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
        cabins = Controller().resetCabins(2, cabinSize, dimension.getLowerFloor())
    }


    override fun go(cabin: Int, to: Int) {
        Controller().go(cabins.get(cabin)!! as Cabin, to)
        //users.filter { u -> u.isTravelling() && u.destinationFloor == 1000 }.sortBy{ u -> u.waitingTicks }.last().destinationFloor = to
    }


    override fun call(at: Int, side: Elevator.Side?) {
        users.add(User(at, side as Side))
    }


    override fun userHasEntered(cabin: Int) {
        Controller().takeIn(cabins.get(cabin)!! as Cabin, users)
        //users.filter { u -> u.isWaiting()  && cabins.get(cabin)?.currentFloor == u.callFloor }.sortBy{ u -> u.waitingTicks }.last().state = User.State.TRAVELLING
    }


    override fun userHasExited(cabin:Int) {

        //val userToExit =
        Controller().leaveUserFrom(cabins.get(cabin)!! as Cabin)
/*        if(userToExit!= null) {
            score = score + Score().plus(userToExit)
            users.remove(userToExit)
        }*/
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
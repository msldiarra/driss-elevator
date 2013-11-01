package fr.codestory.elevator




fun main( args : Array<String> ){


    var server8883 : CommandServer? = CommandServer(8883, DrissElevator())
    server8883?.listenToElevatorEvents()
}

package fr.codestory.elevator




fun main( args : Array<String> ){

    val port = Integer.parseInt(args.get(0))

    val server  = CommandServer(port, DrissElevator())
    server.listenToElevatorEvents()
}

package fr.codestory.elevator

import org.apache.log4j.Logger


fun main( args : Array<String> ){

    val logger = Logger.getLogger("MAIN")


    val port = Integer.parseInt(args.get(0))

    logger?.info("Loading application on port "+port)

    val server  = CommandServer(port, DrissElevator())
    server.listenToElevatorEvents()
}

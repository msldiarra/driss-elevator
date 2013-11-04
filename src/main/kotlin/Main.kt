package fr.codestory.elevator

import org.apache.log4j.Logger


enum class ElevatorAlgorithm {
    UPANDDOWN
    OMNIBUS
    DRISS
}


fun newElevatorCommand(algo: ElevatorAlgorithm): ElevatorCommand {
    return when(algo) {
        ElevatorAlgorithm.UPANDDOWN -> {
            UpAndDownElevator()
        }
        ElevatorAlgorithm.OMNIBUS -> {
            OmnibusElevator()
        }
        ElevatorAlgorithm.DRISS -> {
            DrissElevator()
        }
        else -> {
            throw IllegalArgumentException("Unknown algorithm")
        }
    }
}


fun main(args: Array<String>) {

    val logger = Logger.getLogger("MAIN")

    val port = Integer.parseInt(args.get(0))
    val algorithm = ElevatorAlgorithm.valueOf( if (args.size > 1)  args.get(1).toUpperCase() else "DRISS")

    logger?.info("Loading $algorithm algorithm on port $port")

    val elevator = newElevatorCommand(algorithm)
    val server = CommandServer(port, elevator)
    server.listenToElevatorEvents()
}

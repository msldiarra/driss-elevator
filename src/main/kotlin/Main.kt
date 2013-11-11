package fr.codestory.elevator

import org.apache.log4j.Logger
import fr.codestory.elevator.driss.DrissElevator
import fr.codestory.elevator.hodor.HodorElevator


enum class ElevatorAlgorithm {
    UPANDDOWN
    OMNIBUS
    DRISS
    HODOR
}


fun newElevatorCommand(algo: ElevatorAlgorithm): Elevator {

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
        ElevatorAlgorithm.HODOR-> {
            HodorElevator()
        }
        else -> {
            throw IllegalArgumentException("Unknown algorithm")
        }
    }
}


fun main(args: Array<String>) {

    val logger = Logger.getLogger("MAIN")

    val port = Integer.parseInt(args.get(0))
    val algorithm = ElevatorAlgorithm.valueOf( if (args.size > 1)  args.get(1).toUpperCase() else "HODOR")

    logger?.info("Loading $algorithm algorithm on port $port")

    val elevator = newElevatorCommand(algorithm)

    val server = ElevatorServer(port, elevator)
    server.listenToElevatorEvents()
}

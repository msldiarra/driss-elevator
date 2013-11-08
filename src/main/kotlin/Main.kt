package fr.codestory.elevator

import org.apache.log4j.Logger
import fr.codestory.elevator.driss.DrissElevator


enum class ElevatorAlgorithm {
    UPANDDOWN
    OMNIBUS
    DRISS
}

fun factory(algo: ElevatorAlgorithm): ElevatorFactory {
    return when(algo) {
        ElevatorAlgorithm.UPANDDOWN -> {
            ElevatorFactory { UpAndDownElevator() }
        }
        ElevatorAlgorithm.OMNIBUS -> {
            ElevatorFactory { OmnibusElevator() }
        }
        ElevatorAlgorithm.DRISS -> {
            ElevatorFactory { buildingDimension ->
                DrissElevator(dimension = buildingDimension as BuildingDimension);
            }
        }
        else -> {
            throw IllegalArgumentException("Unknown algorithm")
        }
    }
}


fun main(args: Array<String>) {

    val logger = Logger.getLogger("MAIN")

    val port = Integer.parseInt(args.get(0))
    val algorithm = ElevatorAlgorithm.valueOf(if (args.size > 1)  args.get(1).toUpperCase() else "DRISS")

    logger?.info("Loading $algorithm algorithm on port $port")

    val server = ElevatorServer(port, factory(algorithm))
    server.listenToElevatorEvents()
}

package fr.codestory.elevator

import org.apache.log4j.Logger
import fr.codestory.elevator.hodor.HodorElevator
import driss.DrissElevator


enum class ElevatorAlgorithm {
    UPANDDOWN
    OMNIBUS
    DRISS
    HODOR
}

fun factory(algo: ElevatorAlgorithm): ElevatorFactory {

    return when(algo) {
        ElevatorAlgorithm.UPANDDOWN -> {
            ElevatorFactory { buildingDimension, cabinSize, numberOfCabin -> UpAndDownElevator() }
        }
        ElevatorAlgorithm.OMNIBUS -> {
            ElevatorFactory { buildingDimension, cabinSize, numberOfCabin -> OmnibusElevator() }
        }
        ElevatorAlgorithm.DRISS -> {
            ElevatorFactory { buildingDimension, cabinSize, numberOfCabin ->
                DrissElevator(dimension = buildingDimension as BuildingDimension,
                        cabinSize = cabinSize!!, cabinNumber = numberOfCabin!!);
            }
        }
        ElevatorAlgorithm.HODOR -> {
            ElevatorFactory { buildingDimension, cabinSize, numberOfCabin -> HodorElevator() }
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

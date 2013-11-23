package fr.codestory.elevator

import org.apache.log4j.Logger
import fr.codestory.elevator.driss.DrissElevator
import fr.codestory.elevator.hodor.HodorElevator


enum class ElevatorAlgorithm {
    UPANDDOWN
    OMNIBUS
    DRISS
    HODOR
    SANDOR
}

fun factory(algo: ElevatorAlgorithm): ElevatorFactory {

    return when(algo) {
        ElevatorAlgorithm.UPANDDOWN -> {
            ElevatorFactory { buildingDimension, cabinSize -> UpAndDownElevator() }
        }
        ElevatorAlgorithm.OMNIBUS -> {
            ElevatorFactory { buildingDimension, cabinSize -> OmnibusElevator() }
        }
        ElevatorAlgorithm.DRISS -> {
            ElevatorFactory { buildingDimension, cabinSize ->
                DrissElevator(dimension = buildingDimension as BuildingDimension,
                        cabinSize = cabinSize as Int);
            }
        }
        ElevatorAlgorithm.HODOR -> {
            ElevatorFactory { buildingDimension, cabinSize ->
                HodorElevator(dimension = buildingDimension as BuildingDimension,
                        cabinSize = cabinSize as Int);
            }
        }
        ElevatorAlgorithm.SANDOR -> {
            ElevatorFactory { buildingDimension, cabinSize ->
                HodorElevator(dimension = buildingDimension as BuildingDimension,
                        cabinSize = cabinSize as Int);
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
    val algorithm = ElevatorAlgorithm.valueOf(if (args.size > 1)  args.get(1).toUpperCase() else "SANDOR")

    logger?.info("Loading $algorithm algorithm on port $port")

    val server = ElevatorServer(port, factory(algorithm))

    server.listenToElevatorEvents()
}

package fr.codestory.elevator




fun main( args : Array<String> ){


    var server8881 : CommandServer? = CommandServer(8881, OmnibusElevatorCommand())
    var server8882 : CommandServer? = CommandServer(8882, EcologySuckslElevatorCommand())
    var server8883 : CommandServer? = CommandServer(8883, FollowCommandsCabin())
    server8881?.listenToElevatorEvents()
    server8882?.listenToElevatorEvents()
    server8883?.listenToElevatorEvents()
    System.`in`?.read()
    server8881?.stopListening()
    server8882?.stopListening()
    server8883?.stopListening()



}

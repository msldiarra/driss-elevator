package fr.codestory.elevator;

import com.google.common.base.Stopwatch;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * @author Miguel Basire
 */
public class ElevatorServer {

    private final static Logger LOG = Logger.getLogger(ElevatorServer.class);

    private final HttpServer httpServer;
    private final ElevatorFactory elevatorFactory;
    private Elevator elevator;

    private final ThreadLocal<Stopwatch> stopWatch = new ThreadLocal<Stopwatch>() {
        @Override
        protected Stopwatch initialValue() {
            return new Stopwatch();
        }
    };

    ElevatorServer(int port, ElevatorFactory elevatorFactory) throws IOException {

        this.elevatorFactory = elevatorFactory;
        this.elevator = elevatorFactory.newElevator(new BuildingDimension(0, 19)); // if the the first request is not a reset...

        httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.setExecutor(new Executor() {
            @Override
            public void execute(Runnable command) {

                stopWatch.get().start();

                command.run();

                stopWatch.get().stop();
                long elapsedTime = stopWatch.get().elapsed(TimeUnit.MILLISECONDS);
                if (elapsedTime > 999) {

                    LOG.warn("Elevator server has taken more than a second to answer: " + elapsedTime + " ms");
                }
                stopWatch.get().reset();
            }
        });
        answerToElevatorEvent(httpServer);
    }

    private void answerToElevatorEvent(HttpServer serverToConfigure) {
        serverToConfigure.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {

                String elevatorEvent = exchange.getRequestURI().getPath();

                String nextMove = "";
                try {
                    String[] params;

                    switch (elevatorEvent) {
                        case "/nextCommand":
                            nextMove = elevator.nextMove();
                            break;

                        case "/go":
                            String to = exchange.getRequestURI().getQuery().replaceFirst("floorToGo=", "");
                            elevator.go(Integer.parseInt(to));
                            break;

                        case "/reset":
                            LOG.warn("A reset has been received: " + exchange.getRequestURI().getQuery());

                            params = extractParameters(exchange);

                            String lowerFloor = params[0].replaceFirst("lowerFloor=", "");
                            String higherFloor = params[1].replaceFirst("higherFloor=", "");

                            BuildingDimension dimension = new BuildingDimension(Integer.parseInt(lowerFloor), Integer.parseInt(higherFloor));
                            elevator = elevatorFactory.newElevator(dimension);

                            break;

                        case "/userHasEntered":
                            break;

                        case "/userHasExited":
                            break;

                        case "/call":
                            params = extractParameters(exchange);
                            String at = params[0].replaceFirst("atFloor=", "");

                            elevator.call(Integer.parseInt(at), Elevator.Side.valueOf(params[1].replaceFirst("to=", "")));
                            break;
                    }
                } catch (Exception e) {
                    LOG.error("elevatorEvent " + elevatorEvent + "" + e.getMessage(), e);
                    nextMove = "NOTHING";
                }

                LOG.debug(elevatorEvent + ": " + nextMove);

                Writer out = new OutputStreamWriter(exchange.getResponseBody());
                out.write(nextMove);
                exchange.sendResponseHeaders(200, nextMove.length());
                out.close();
            }
        });
    }

    private String[] extractParameters(HttpExchange exchange) {
        return exchange.getRequestURI().getQuery().split("&");
    }


    public void listenToElevatorEvents() {
        httpServer.start();

    }

    public void stopListening() {
        httpServer.stop(0);
    }
}
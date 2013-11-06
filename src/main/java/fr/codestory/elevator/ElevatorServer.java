package fr.codestory.elevator;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

/**
 * @author Miguel Basire
 */
public class ElevatorServer {

    private final static Logger LOG = Logger.getLogger(ElevatorServer.class);

    private final HttpServer httpServer;
    private final Elevator groom;


    ElevatorServer(int port, Elevator elevator) throws IOException {

        this.groom = elevator;

        httpServer = HttpServer.create(new InetSocketAddress(port), 0);


        httpServer.setExecutor(new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
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
                            nextMove = groom.nextMove();
                            break;

                        case "/go":
                            String to = exchange.getRequestURI().getQuery().replaceFirst("floorToGo=", "");
                            groom.go(Integer.parseInt(to));
                            break;

                        case "/reset":
                            params = extractParameters(exchange);

                            String lowerFloor = params[0].replaceFirst("lowerFloor=", "");
                            String higherFloor = params[1].replaceFirst("higherFloor=","");

                            groom.reset(new BuildingDimension(Integer.parseInt(lowerFloor),Integer.parseInt(higherFloor)));
                            LOG.warn("A reset has been received: " + exchange.getRequestURI().getQuery());
                            break;

                        case "/userHasEntered":
                            break;

                        case "/userHasExited":
                            break;

                        case "/call":
                            params = extractParameters(exchange);
                            String at = params[0].replaceFirst("atFloor=", "");

                            groom.call(Integer.parseInt(at), Elevator.Side.valueOf(params[1].replaceFirst("to=", "")));
                            break;
                    }
                } catch (Exception e) {
                    LOG.error("elevatorEvent "+elevatorEvent+""+e.getMessage(),e);
                    nextMove = "NOTHING";
                }

                LOG.debug(elevatorEvent + ": " + nextMove);

                Writer out = new OutputStreamWriter(exchange.getResponseBody());
                out.write(nextMove);
                exchange.sendResponseHeaders(200, nextMove.length());
                out.close();
            }
        }

        );
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

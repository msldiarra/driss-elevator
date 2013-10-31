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
public class CommandServer {

    private final static Logger LOG = Logger.getLogger(CommandServer.class);

    private final HttpServer httpServer;
    private final ElevatorCommand groom;


    CommandServer(int port, ElevatorCommand elevatorCommand) throws IOException {

        this.groom = elevatorCommand;

        httpServer = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);


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
                    switch (elevatorEvent) {
                        case "/nextCommand":
                            nextMove = groom.nextMove();
                            break;

                        case "/go":
                            String to = exchange.getRequestURI().getQuery().replaceFirst("floorToGo=", "");
                            groom.go(Integer.parseInt(to));
                            break;

                        case "/reset":
                            groom.reset();
                            break;

                        case "/userHasEntered":
                            break;

                        case "/userHasExited":
                            break;

                        case "/call":
                            String[] param = exchange.getRequestURI().getQuery().split("&");
                            String at = param[0].replaceFirst("atFloor=", "");

                            groom.call(Integer.parseInt(at), ElevatorCommand.Side.valueOf(param[1].replaceFirst("to=", "")));
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


    public void listenToElevatorEvents() {
        httpServer.start();

    }

    public void stopListening() {
        httpServer.stop(0);
    }
}

package fr.codestory.elevator;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetSocketAddress;

/**
 * @author Miguel Basire
 */
public class CommandServer {

    static ElevatorCommand groom = new EcologySuckslElevatorCommand();

    private final HttpServer httpServer;

    CommandServer() throws IOException {

        httpServer = HttpServer.create(new InetSocketAddress("127.0.0.1", 8881), 0);
        answerToElevatorEvent(httpServer);
    }

    private void answerToElevatorEvent(HttpServer serverToConfigure) {
        serverToConfigure.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {

                String elevatorEvent = exchange.getRequestURI().getPath();

                System.out.println(elevatorEvent);

                String nextMove = "";

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
                        String at = param[0].replaceFirst("atFloor=","" );

                        groom.call(Integer.parseInt(at), ElevatorCommand.Side.valueOf(param[1].replaceFirst("to=", "")));
                        break;
                }

                Writer out = new OutputStreamWriter(exchange.getResponseBody());
                exchange.sendResponseHeaders(200, nextMove.length());
                out.write(nextMove);
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


    public static void main(String[] args) throws IOException {

        CommandServer server = new CommandServer();

        server.listenToElevatorEvents();

        System.in.read();
        server.stopListening();


    }
}

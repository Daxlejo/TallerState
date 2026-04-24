package AgriDrone.server;

import AgriDrone.context.DroneContext;
import AgriDrone.exception.InvalidStateTransitionException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DroneWebServer {

    private final HttpServer server;
    private final DroneContext droneContext;

    public DroneWebServer(int port) throws IOException {
        this.droneContext = new DroneContext();
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        setupRoutes();
    }

    private void setupRoutes() {
        server.createContext("/", exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                serveHtmlPage(exchange);
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        });

        server.createContext("/api/status", exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                String json = droneContext.getStatusJson();
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                sendResponse(exchange, 200, json);
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        });

        server.createContext("/api/action", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                handleAction(exchange);
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        });
    }

    private void handleAction(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        String action = extractJsonValue(body, "action");

        String responseJson;
        try {
            switch (action) {
                case "takeOff":
                    droneContext.takeOff();
                    break;
                case "startMapping":
                    droneContext.startMapping();
                    break;
                case "takePhoto":
                    droneContext.takePhoto();
                    break;
                case "returnToBase":
                    droneContext.returnToBase();
                    break;
                case "motorFailure":
                    droneContext.motorFailure();
                    break;
                case "reset":
                    droneContext.reset();
                    break;
                default:
                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    sendResponse(exchange, 400, "{\"error\":\"Acci\u00f3n desconocida: " + action + "\"}");
                    return;
            }
            responseJson = droneContext.getStatusJson();
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            sendResponse(exchange, 200, responseJson);
        } catch (InvalidStateTransitionException e) {
            droneContext.getLogger().log("\u274C " + e.getMessage());
            responseJson = droneContext.getStatusJson();
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            sendResponse(exchange, 200, responseJson);
        }
    }

    private void serveHtmlPage(HttpExchange exchange) throws IOException {
        Path htmlPath = Paths.get("AgriDrone", "server", "drone.html");
        byte[] htmlBytes;

        if (Files.exists(htmlPath)) {
            htmlBytes = Files.readAllBytes(htmlPath);
        } else {
            InputStream is = getClass().getResourceAsStream("/AgriDrone/server/drone.html");
            if (is != null) {
                htmlBytes = is.readAllBytes();
                is.close();
            } else {
                String errorPage = "<html><body><h1>Error: drone.html not found</h1></body></html>";
                htmlBytes = errorPage.getBytes(StandardCharsets.UTF_8);
            }
        }

        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, htmlBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(htmlBytes);
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\"";
        int keyIndex = json.indexOf(searchKey);
        if (keyIndex == -1)
            return "";

        int colonIndex = json.indexOf(":", keyIndex);
        if (colonIndex == -1)
            return "";

        int valueStart = json.indexOf("\"", colonIndex);
        if (valueStart == -1)
            return "";

        int valueEnd = json.indexOf("\"", valueStart + 1);
        if (valueEnd == -1)
            return "";

        return json.substring(valueStart + 1, valueEnd);
    }

    public void start() {
        server.setExecutor(null);
        server.start();
        System.out.println("========================================");
        System.out.println("  \uD83D\uDE81 Dron de Fotogrametr\u00eda Agr\u00edcola");
        System.out.println("  Servidor iniciado en:");
        System.out.println("  http://localhost:" + server.getAddress().getPort());
        System.out.println("========================================");
    }

    public void stop() {
        server.stop(0);
    }
}

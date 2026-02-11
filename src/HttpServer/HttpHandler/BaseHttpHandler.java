package HttpServer.HttpHandler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {
    protected final Gson gson;

    protected BaseHttpHandler(Gson gson) {
        this.gson = gson;
    }

    protected void sendJson(HttpExchange h, int code, Object body) throws IOException {
        String json = (body == null) ? "" : gson.toJson(body);
        byte[] resp = json.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(code, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendText(HttpExchange h, int code, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(code, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        sendText(h, 404, "{\"error\":\"Not Found\"}");
    }

    protected void sendHasIntersections(HttpExchange h, String message) throws IOException {
        sendText(h, 406, "{\"error\":\"" + message + "\"}");
    }

    protected void sendServerError(HttpExchange h, String message) throws IOException {
        sendText(h, 500, "{\"error\":\"" + message + "\"}");
    }

    protected String readBody(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    protected Integer parseIdFromPath(String path, String basePath) {
        String[] parts = path.split("/");
        if (parts.length == 3 && parts[1].equals(basePath.replace("/", ""))) {
            return Integer.parseInt(parts[2]);
        }
        return null;
    }
}
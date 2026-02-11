package HttpServer.HttpHandler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import interfaces.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public PrioritizedHandler(TaskManager manager, Gson gson) {
        super(gson);
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        try {
            if ("GET".equals(h.getRequestMethod()) && "/prioritized".equals(h.getRequestURI().getPath())) {
                sendJson(h, 200, manager.getPrioritizedTasks());
                return;
            }
            sendNotFound(h);
        } catch (Exception e) {
            sendServerError(h, e.getMessage());
        }
    }
}
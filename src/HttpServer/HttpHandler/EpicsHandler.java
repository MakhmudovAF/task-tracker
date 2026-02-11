package HttpServer.HttpHandler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import interfaces.TaskManager;
import model.Epic;

import java.io.IOException;

public class EpicsHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public EpicsHandler(TaskManager manager, Gson gson) {
        super(gson);
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        try {
            String method = h.getRequestMethod();
            String path = h.getRequestURI().getPath();

            if ("GET".equals(method) && "/epics".equals(path)) {
                sendJson(h, 200, manager.getAllEpics());
                return;
            }

            // GET /epics/{id}/subtasks
            if ("GET".equals(method) && path.matches("^/epics/\\d+/subtasks$")) {
                String[] parts = path.split("/");
                int epicId = Integer.parseInt(parts[2]);

                Epic epic = manager.getEpicById(epicId);
                if (epic == null) {
                    sendNotFound(h);
                    return;
                }
                sendJson(h, 200, manager.getSubtasksOfEpic(epicId));
                return;
            }

            // GET /epics/{id}
            if ("GET".equals(method) && path.startsWith("/epics/") && !path.endsWith("/subtasks")) {
                Integer id = parseIdFromPath(path, "/epics");
                Epic epic = manager.getEpicById(id);
                if (epic == null) sendNotFound(h);
                else sendJson(h, 200, epic);
                return;
            }

            // POST /epics (create/update)
            if ("POST".equals(method) && "/epics".equals(path)) {
                Epic epic = gson.fromJson(readBody(h), Epic.class);

                Epic result;
                if (epic.getId() == 0) {
                    result = manager.createEpic(epic);
                } else {
                    result = manager.updateEpic(epic);
                    if (result == null) {
                        sendNotFound(h);
                        return;
                    }
                }
                sendJson(h, 201, result);
                return;
            }

            // DELETE /epics/{id}
            if ("DELETE".equals(method) && path.startsWith("/epics/") && !path.endsWith("/subtasks")) {
                Integer id = parseIdFromPath(path, "/epics");
                Epic removed = manager.deleteEpicById(id);
                if (removed == null) sendNotFound(h);
                else sendJson(h, 200, removed);
                return;
            }

            sendNotFound(h);

        } catch (Exception e) {
            sendServerError(h, e.getMessage());
        }
    }
}
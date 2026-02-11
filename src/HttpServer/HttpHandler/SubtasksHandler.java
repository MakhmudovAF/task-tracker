package HttpServer.HttpHandler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import interfaces.TaskManager;
import model.Subtask;

import java.io.IOException;

public class SubtasksHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public SubtasksHandler(TaskManager manager, Gson gson) {
        super(gson);
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        try {
            String method = h.getRequestMethod();
            String path = h.getRequestURI().getPath();

            if ("GET".equals(method) && "/subtasks".equals(path)) {
                sendJson(h, 200, manager.getAllSubtasks());
                return;
            }

            if ("GET".equals(method) && path.startsWith("/subtasks/")) {
                Integer id = parseIdFromPath(path, "/subtasks");
                Subtask st = manager.getSubtaskById(id);
                if (st == null) sendNotFound(h);
                else sendJson(h, 200, st);
                return;
            }

            if ("POST".equals(method) && "/subtasks".equals(path)) {
                Subtask st = gson.fromJson(readBody(h), Subtask.class);

                Subtask result;
                if (st.getId() == 0) {
                    result = manager.createSubtask(st);
                    if (result == null) { // эпик не найден
                        sendNotFound(h);
                        return;
                    }
                } else {
                    result = manager.updateSubtask(st);
                    if (result == null) {
                        sendNotFound(h);
                        return;
                    }
                }

                sendJson(h, 201, result);
                return;
            }

            if ("DELETE".equals(method) && path.startsWith("/subtasks/")) {
                Integer id = parseIdFromPath(path, "/subtasks");
                Subtask removed = manager.deleteSubtaskById(id);
                if (removed == null) sendNotFound(h);
                else sendJson(h, 200, removed);
                return;
            }

            sendNotFound(h);

        } catch (IllegalArgumentException e) {
            sendHasIntersections(h, e.getMessage());
        } catch (Exception e) {
            sendServerError(h, e.getMessage());
        }
    }
}
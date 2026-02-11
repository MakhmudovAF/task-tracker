package HttpServer.HttpHandler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import interfaces.TaskManager;
import model.Task;

import java.io.IOException;

public class TasksHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public TasksHandler(TaskManager manager, Gson gson) {
        super(gson);
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        try {
            String method = h.getRequestMethod();
            String path = h.getRequestURI().getPath();

            // GET /tasks
            if ("GET".equals(method) && "/tasks".equals(path)) {
                sendJson(h, 200, manager.getAllTasks());
                return;
            }

            // GET /tasks/{id}
            if ("GET".equals(method) && path.startsWith("/tasks/")) {
                Integer id = parseIdFromPath(path, "/tasks");
                Task task = manager.getTaskById(id);
                if (task == null) {
                    sendNotFound(h);
                } else {
                    sendJson(h, 200, task);
                }
                return;
            }

            // POST /tasks (create/update)
            if ("POST".equals(method) && "/tasks".equals(path)) {
                String body = readBody(h);
                Task task = gson.fromJson(body, Task.class);

                Task result;
                if (task.getId() == 0) {
                    result = manager.createTask(task);
                } else {
                    result = manager.updateTask(task);
                    if (result == null) {
                        sendNotFound(h);
                        return;
                    }
                }
                sendJson(h, 201, result);
                return;
            }

            // DELETE /tasks/{id}
            if ("DELETE".equals(method) && path.startsWith("/tasks/")) {
                Integer id = parseIdFromPath(path, "/tasks");
                Task removed = manager.deleteTaskById(id);
                if (removed == null) {
                    sendNotFound(h);
                } else {
                    sendJson(h, 200, removed);
                }
                return;
            }

            sendNotFound(h);

        } catch (IllegalArgumentException e) {
            // пересечения по времени у вас кидаются IllegalArgumentException
            sendHasIntersections(h, e.getMessage());
        } catch (Exception e) {
            sendServerError(h, e.getMessage());
        }
    }
}
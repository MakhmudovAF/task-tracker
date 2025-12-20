import java.util.List;

public class Epic extends Task {
    protected List<Integer> subtaskList;

    public Epic(String name, String description, int id, String status) {
        super(name, description, id, status);
    }
}
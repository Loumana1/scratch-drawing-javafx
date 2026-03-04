package scratch.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ProgramFileService {

    public static List<Action> load(File file) throws IOException{
        List<Action> actions = new ArrayList<>();
        List<String> lines = Files.readAllLines(file.toPath());
        for (String line : lines) {
            if (line.isBlank()) continue;
            String[] parts = line.split(";");
            String type = parts[0].trim();
            int val = Integer.parseInt(parts[1].trim());
            actions.add(createFromType(type , val));
        }
        return actions;
    }

    private static Action createFromType(String type, int val) {
        return switch (type) {
            case "MOVE_FORWARD" -> new MoveForwardAction(val);
            case "TURN_LEFT" -> new TurnLeftAction(val);
            case "TURN_RIGHT" -> new TurnRightAction(val);
            case "PEN_UP" -> new PenUpAction();
            case "PEN_DOWN" -> new PenDownAction();
            default -> throw new IllegalArgumentException("Type inconnu: " + type);
        };
    }

}

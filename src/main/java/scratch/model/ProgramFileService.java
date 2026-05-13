package scratch.model;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ProgramFileService {


    public static void save(File file, List<Action> actions) throws IOException {
        try (PrintWriter writer = new PrintWriter(file)) {
            for (Action action : actions) {
                writer.println(action.format());
            }
        }
    }


    public static List<Action> load(File file) throws IOException {
        List<Action> actions = new ArrayList<>();
        List<String> lines = Files.readAllLines(file.toPath());

        for (String line : lines) {
            if (line.isBlank()) continue;

            String[] parts = line.split(";");
            String type = parts[0].trim();

            actions.add(createFromType(type, parts));
        }
        return actions;
    }

    private static Action createFromType(String type, String[] parts) {
        switch (type) {
            case "VAR_DECLARATION":
                VarDeclarationAction varDecl = new VarDeclarationAction();
                if (parts.length > 1) varDecl.setVarName(parts[1].trim());
                return varDecl;

            case "VAR_ASSIGNMENT":
                VarAssignmentAction varAss = new VarAssignmentAction();
                if (parts.length > 1) varAss.setTargetVar(parts[1].trim());
                if (parts.length > 2) varAss.setValue(parts[2].trim());
                return varAss;

            case "INCREMENT_VARIABLE":
                IncrementVariableAction incVar = new IncrementVariableAction();
                if (parts.length > 1) incVar.setTargetVar(parts[1].trim());
                if (parts.length > 2) incVar.setValue(parts[2].trim());
                return incVar;

            case "REPEAT":
                if (parts.length > 1) {
                    String countStr = parts[1].trim();
                    if (countStr.matches("^-?\\d+$")) {
                        return new RepeatAction(Integer.parseInt(countStr));
                    } else {
                        return new RepeatAction(countStr);
                    }
                }
                return new RepeatAction(0);

            case "END_REPEAT":
                return new EndRepeatAction();

            case "DRAW_POLYGON":
                PolygonAction poly = new PolygonAction();
                if (parts.length > 1) poly.setSize(parts[1].trim());
                if (parts.length > 2) poly.setNum(parts[2].trim());
                return poly;

            case "MOVE_FORWARD":
                return fillParameterizedAction(new MoveForwardAction(), parts);

            case "TURN_LEFT":
                return fillParameterizedAction(new TurnLeftAction(), parts);

            case "TURN_RIGHT":
                return fillParameterizedAction(new TurnRightAction(), parts);

            case "PEN_UP":
                return new PenUpAction();

            case "PEN_DOWN":
                return new PenDownAction();

            default:
                return null;
        }
    }

    private static Action fillParameterizedAction(Action action, String[] parts) {
        if (parts.length > 1) {
            String valStr = parts[1].trim();
            List<ActionParameter> params = action.getParameters();
            if (!params.isEmpty()) {
                params.get(0).setValue(valStr);
            }
        }
        return action;
    }
}
package scratch.viewmodel;

import scratch.model.*;

public class MainViewModel {

    private final Program program;

    private final ProgramViewModel programViewModel;
    private final SceneViewModel sceneViewModel;
    private final ActionConfigViewModel configViewModel;

    public MainViewModel(Program program) {
        this.program = program;

        this.programViewModel = new ProgramViewModel(program);
        this.sceneViewModel = new SceneViewModel(program, programViewModel);
        this.configViewModel = new ActionConfigViewModel(programViewModel, sceneViewModel);

        programViewModel.selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            Action selected = programViewModel.getSelectedAction();
            configViewModel.updateForAction(selected);

            int fault = sceneViewModel.getExecutionFaultLineIndex();
            if (fault >= 0 && newVal != null && newVal.intValue() != fault) {
                sceneViewModel.clearFaultLine();
            }
        });

        programViewModel.programChangeCounterProperty().addListener((obs, oldVal, newVal) -> {
            sceneViewModel.onProgramStructureChanged();
        });
    }

    public ProgramViewModel getProgramViewModel() { return programViewModel; }
    public SceneViewModel getSceneViewModel() { return sceneViewModel; }
    public ActionConfigViewModel getConfigViewModel() { return configViewModel; }
}

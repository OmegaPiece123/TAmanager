package jtps;

import djf.ui.AppGUI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import tam.TAManagerApp;
import tam.data.TeachingAssistant;
import djf.components.AppjTPS;
import java.util.HashMap;
import javafx.scene.control.Label;
import tam.data.TAData;
import tam.file.TAFiles;
import tam.file.TimeSlot;
import tam.workspace.TAWorkspace;

/**
 *
 * @author McKillaGorilla
 */
public class jTPS implements AppjTPS {

    private final ArrayList<TeachingAssistant> listTA = new ArrayList<>();
    private final ArrayList<TeachingAssistant> afterChange = new ArrayList<>();
    private final ArrayList<String> taList = new ArrayList<>();
    private final ArrayList<String> taList2 = new ArrayList<>();
    private final ArrayList<String> keys = new ArrayList<>();
    private final ArrayList<String> table = new ArrayList<>();
    private final ArrayList<String> table2 = new ArrayList<>();
    private final ArrayList<ArrayList<String>> tooManyKeys = new ArrayList<>();
    private final ArrayList<Integer> doingList = new ArrayList<>();
    private ArrayList<String> temp = new ArrayList<>();

    private int pointer = -1;
    private int updatePointer = -1;
    private int updatePointer2 = -1;
    private int taPointer = -1;
    private int taPointer2 = -1;
    private int keyPointer = -1;
    private int tablePointer = -1;
    private int tablePointer2 = -1;
    private int manyKeyPointer = -1;

    private int DELETE_TA = 1;
    private int ADD_TA = 2;
    private int UPDATE_TA = 3;
    private int TOGGLE_CELL = 4;
    private int MODIFIED_TIME = 5;

    TAManagerApp app;
    AppGUI gui;

    public jTPS(TAManagerApp initApp) {
        // KEEP THIS FOR LATER
        app = initApp;
        gui = app.getGUI();

    }

    public void ifChangeTA(int doing, String ta, String email) {
        doingList.add(doing);
        pointer++;
        taPointer++;
        taList.add(ta + "_" + email);
    }
    public void ifUpdateTA(int doing, TeachingAssistant ta,TeachingAssistant ta2,String name, String email,String name2, String email2) {
        doingList.add(doing);
        pointer++;
        updatePointer++;
        updatePointer2++;
        listTA.add(ta);
        afterChange.add(ta2);
        taPointer++;
        taList.add(name + "_" + email);
        taList2.add(name2 + "_" + email2);
        taPointer2++;
        
    }

    public void ifChangeCell(int doing, String key, String name) {
        doingList.add(doing);
        pointer++;
        keyPointer++;
        keys.add(key + "/" + name);
    }

    public void ifChangeTime(int doing, int value1, int value2) {
        doingList.add(doing);
        pointer++;
        tablePointer++;
        table.add(value1 + " " + value2);
    }
    public void ifChangeTime2(int doing, int value1, int value2) {
        doingList.add(doing);
        pointer++;
        tablePointer2++;
        table2.add(value1 + " " + value2);
    }

    public void addKeys(String s, String name) {
        temp.add(s + "/" + name);

    }

    public void addToIndex() {
        tooManyKeys.add(temp);
        temp = new ArrayList<>();
        manyKeyPointer++;
    }

    @Override
    public void doTransaction() {
        TAData data = (TAData) app.getDataComponent();
        TAWorkspace workspace = (TAWorkspace) app.getWorkspaceComponent();
        TAFiles taFile = (TAFiles) app.getFileComponent();

        if (pointer + 1 < doingList.size()) {
            if (doingList.get(pointer + 1) == DELETE_TA) {
                taPointer++;
                if (taPointer < taList.size() && manyKeyPointer < tooManyKeys.size()) {
                    String taName = taList.get(taPointer).split("_")[0];
                    data.getTeachingAssistants().remove(data.getTA(taName));
                    HashMap<String, Label> labels = workspace.getOfficeHoursGridTACellLabels();
                    for (Label label : labels.values()) {
                        if (label.getText().equals(taName)
                                || (label.getText().contains(taName + "\n"))
                                || (label.getText().contains("\n" + taName))) {
                            data.removeTAFromCell(label.textProperty(), taName);
                        }
                    }
                    manyKeyPointer++;
                } else {
                    taPointer = taList.size();
                    manyKeyPointer = tooManyKeys.size();
                }

            } else if (doingList.get(pointer + 1) == ADD_TA) {
                taPointer++;
                if (taPointer < taList.size()) {
                    String[] temp = taList.get(taPointer).split("_");
                    data.addTA(temp[0], temp[1]); 
                } else {
                    taPointer = taList.size();
                }

            } else if (doingList.get(pointer + 1) == TOGGLE_CELL) {
                keyPointer++;
                if (keyPointer < keys.size()) {
                    String[] temp = keys.get(keyPointer).split("/");
                    System.out.println(temp[0] + " "+ temp[1]);
                    data.toggleTAOfficeHours(temp[0], temp[1]);
                } else {
                    keyPointer = keys.size();
                }

            } else if (doingList.get(pointer + 1) == MODIFIED_TIME) {
                tablePointer2--;
                if (tablePointer2 >= 0) {
                    ArrayList<TimeSlot> officeHours = taFile.saveHours(data);
                    int x = Integer.parseInt(table2.get(tablePointer2).split(" ")[0]);
                    int y = Integer.parseInt(table2.get(tablePointer2).split(" ")[1]);
                    System.out.println(x + " "+ y);
                    data.changeStartAndEnd(x, y);
                    workspace.resetWorkspace();
                    workspace.reloadWorkspace(app.getDataComponent());
                    data.saveReloadData(officeHours);
                    tablePointer++;
                    tablePointer2--;
                }

            }
            else if (doingList.get(pointer + 1) == UPDATE_TA) {
                taPointer2++;
                updatePointer++;
                if (taPointer2 < taList2.size() && updatePointer < listTA.size()) {
                    String[] temp = taList2.get(taPointer2).split("_");
                    data.updateTA(listTA.get(updatePointer), temp[0], temp[1]);
                    taPointer++;
                    updatePointer2++;
                } else {
                    taPointer2 = taList2.size();
                    updatePointer = listTA.size();
                }

            }
        }
        pointer++;
        if (pointer >= doingList.size()) {
            pointer = doingList.size() - 1;
        }
    }

    @Override
    public void undoTransaction() {
        TAData data = (TAData) app.getDataComponent();
        TAWorkspace workspace = (TAWorkspace) app.getWorkspaceComponent();
        TAFiles taFile = (TAFiles) app.getFileComponent();

        if (pointer >= 0) {
            if (doingList.get(pointer) == DELETE_TA) {
                if (taPointer >= 0 || manyKeyPointer >= 0) {
                    String[] temp = taList.get(taPointer).split("_");
                    data.addTA(temp[0], temp[1]);
                    for (int i = 0; i < tooManyKeys.get(manyKeyPointer).size(); i++) {
                        
                        temp = tooManyKeys.get(manyKeyPointer).get(i).split("/");
                        data.toggleTAOfficeHours(temp[0], temp[1]);
                    }
                    manyKeyPointer--;
                    taPointer--;
                }
            } else if (doingList.get(pointer) == ADD_TA) {
                if (taPointer >= 0) {
                    String name = taList.get(taPointer).split("_")[0];
                    data.getTeachingAssistants().remove(data.getTA(name));
                    taPointer--;
                }
            } else if (doingList.get(pointer) == TOGGLE_CELL) {
                if (keyPointer >= 0) {
                    String[] temp = keys.get(keyPointer).split("/");
                    data.toggleTAOfficeHours(temp[0], temp[1]);
                    keyPointer--;
                }

            } else if (doingList.get(pointer) == MODIFIED_TIME) {
                if (tablePointer >= 0) {
                    ArrayList<TimeSlot> officeHours = taFile.saveHours(data);
                    int x = Integer.parseInt(table.get(tablePointer).split(" ")[0]);
                    int y = Integer.parseInt(table.get(tablePointer).split(" ")[1]);
                    System.out.println(x + " "+ y);
                    data.changeStartAndEnd(x, y);
                    workspace.resetWorkspace();
                    workspace.reloadWorkspace(app.getDataComponent());
                    data.saveReloadData(officeHours);
                    tablePointer2++;
                    tablePointer--;
                }
            }
            else if (doingList.get(pointer) == UPDATE_TA) {
                if (taPointer >= 0 && updatePointer2 >= 0) {
                    
                    String[] temp = taList.get(taPointer).split("_");
                    data.updateTA(afterChange.get(updatePointer2), temp[0], temp[1]); 
                    updatePointer--;
                    taPointer2--;
                    updatePointer2--;
                }
            }
        }
        pointer--;
        if (pointer <= -1) {
            pointer = -1;
        }
    }

}

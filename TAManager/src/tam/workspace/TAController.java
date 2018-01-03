package tam.workspace;

import djf.ui.AppGUI;
import static tam.TAManagerProp.*;
import djf.ui.AppMessageDialogSingleton;
import djf.ui.AppYesNoCancelDialogSingleton;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import properties_manager.PropertiesManager;
import tam.TAManagerApp;
import tam.data.TAData;
import tam.data.TeachingAssistant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.control.Label;
import jtps.jTPS;
import tam.file.TAFiles;
import tam.file.TimeSlot;

/**
 * This class provides responses to all workspace interactions, meaning
 * interactions with the application controls not including the file toolbar.
 *
 * @author Richard McKenna
 * @version 1.0
 */
public class TAController {

    // THE APP PROVIDES ACCESS TO OTHER COMPONENTS AS NEEDED
    TAManagerApp app;
    AppGUI gui;
    private int DELETE_TA = 1;
    private int ADD_TA = 2;
    private int UPDATE_TA = 3;
    private int TOGGLE_CELL = 4;
    private int MODIFIED_TIME = 5;

    /**
     * Constructor, note that the app must already be constructed.
     */
    public TAController(TAManagerApp initApp) {
        // KEEP THIS FOR LATER
        app = initApp;
        gui = app.getGUI();
        

    }

    /**
     * This method responds to when the user requests to add a new TA via the
     * UI. Note that it must first do some validation to make sure a unique name
     * and email address has been provided.
     */
    public void handleAddTA() {
        // WE'LL NEED THE WORKSPACE TO RETRIEVE THE USER INPUT VALUES
        TAWorkspace workspace = (TAWorkspace) app.getWorkspaceComponent();
        jTPS tps = (jTPS) app.getTPS();
        TextField nameTextField = workspace.getNameTextField();
        TextField emailTextField = workspace.getEmailTextField();

        String name = nameTextField.getText();
        String email = emailTextField.getText();
        String Epattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(Epattern);
        Matcher matcher = pattern.matcher(email);
        Boolean ifMatch = matcher.matches();

        // WE'LL NEED TO ASK THE DATA SOME QUESTIONS TOO
        TAData data = (TAData) app.getDataComponent();

        // WE'LL NEED THIS IN CASE WE NEED TO DISPLAY ANY ERROR MESSAGES
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        

        

        // DID THE USER NEGLECT TO PROVIDE A TA NAME?
        if (name.isEmpty() || email.isEmpty()) {
            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
            if (name.isEmpty()) {
                dialog.show(props.getProperty(MISSING_TA_NAME_TITLE), props.getProperty(MISSING_TA_NAME_MESSAGE));
            }
            if (email.isEmpty()) {
                dialog.show(props.getProperty(MISSING_TA_EMAIL_TITLE), props.getProperty(MISSING_TA_EMAIL_MESSAGE));
            }

        } // DOES A TA ALREADY HAVE THE SAME NAME OR EMAIL?
        else if (data.containsTA(name, email)) {
            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
            dialog.show(props.getProperty(TA_NAME_AND_EMAIL_NOT_UNIQUE_TITLE), props.getProperty(TA_NAME_AND_EMAIL_NOT_UNIQUE_MESSAGE));
        } // If email does not not match pattern
        else if (!ifMatch) {
            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
            dialog.setWidth(300);
            dialog.show("Email", "Bad Email");
        } // EVERYTHING IS FINE, ADD A NEW TA
        else {
            //jTPS
            workspace.tps.ifChangeTA(ADD_TA, name, email);
            // ADD THE NEW TA TO THE DATA
            
                    
            data.addTA(name, email);

            // CLEAR THE TEXT FIELDS
            nameTextField.setText("");
            emailTextField.setText("");

            // AND SEND THE CARET BACK TO THE NAME TEXT FIELD FOR EASY DATA ENTRY
            nameTextField.requestFocus();
            emailTextField.requestFocus();
        }

        //Handles save button enable
        gui.updateToolbarControls(false);

    }

    public void updateTA() {
        TAWorkspace workspace = (TAWorkspace) app.getWorkspaceComponent();
        TableView taTable = workspace.getTATable();
        TextField nameTextField = workspace.getNameTextField();
        TextField emailTextField = workspace.getEmailTextField();
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        TAData data = (TAData) app.getDataComponent();

        String name = nameTextField.getText();
        String email = emailTextField.getText();
        String Epattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(Epattern);
        Matcher matcher = pattern.matcher(email);
        Boolean ifMatch = matcher.matches();

        // IS A TA SELECTED IN THE TABLE?
        Object selectedItem = taTable.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            TeachingAssistant ta = (TeachingAssistant) selectedItem;

            if (name.isEmpty() || email.isEmpty()) {
                AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
                if (name.isEmpty()) {
                    dialog.show(props.getProperty(MISSING_TA_NAME_TITLE), props.getProperty(MISSING_TA_NAME_MESSAGE));
                }
                if (email.isEmpty()) {
                    dialog.show(props.getProperty(MISSING_TA_EMAIL_TITLE), props.getProperty(MISSING_TA_EMAIL_MESSAGE));
                }

            } // DOES A TA ALREADY HAVE THE SAME NAME OR EMAIL?
            else if (data.containsTA(name, email)) {
                AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
                dialog.show(props.getProperty(TA_NAME_AND_EMAIL_NOT_UNIQUE_TITLE), props.getProperty(TA_NAME_AND_EMAIL_NOT_UNIQUE_MESSAGE));
            } // If email does not not match pattern
            else if (!ifMatch) {
                AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
                dialog.setWidth(300);
                dialog.show("Email", "Bad Email");
            } else {
                TeachingAssistant ta2 = new TeachingAssistant(name, email);
                workspace.tps.ifUpdateTA(UPDATE_TA, ta, ta2, ta.getName(), ta.getEmail(), name, email);
                data.updateTA(ta, name, email);
                workspace.getAddButton().setText("Add TA");
                workspace.getAddButton().setOnAction(e -> {
                    handleAddTA();

                });

                //Get keys from each TA object and delete each item that has the key
                ArrayList<String> list = ta.getKeys();

                for (int i = list.size() - 1; i >= 0; i--) {

                    String[] key = list.get(i).split("_");
                    data.removeTAFromCell(data.getCellTextProperty(Integer.parseInt(key[0]), Integer.parseInt(key[1])), ta.getName());
                    data.toggleTAOfficeHours(list.get(i), name);

                }
                // CLEAR THE TEXT FIELDS
                nameTextField.setText("");
                emailTextField.setText("");

                // AND SEND THE CARET BACK TO THE NAME TEXT FIELD FOR EASY DATA ENTRY
                nameTextField.requestFocus();
                emailTextField.requestFocus();
            }

        }
    }

    public void deleteTA() {
        // GET THE TABLE
        TAWorkspace workspace = (TAWorkspace) app.getWorkspaceComponent();
        TableView taTable = workspace.getTATable();
        
        // IS A TA SELECTED IN THE TABLE?
        Object selectedItem = taTable.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            // GET THE TA
            TeachingAssistant ta = (TeachingAssistant) selectedItem;
            
            String taName = ta.getName();
            String email = ta.getEmail();
            
            workspace.tps.ifChangeTA(DELETE_TA, taName, email);
            
            TAData data = (TAData) app.getDataComponent();
            //remove ta from the list
            data.getTeachingAssistants().remove(ta);
            //Get keys from each TA object and delete each item that has the key

            // AND BE SURE TO REMOVE ALL THE TA'S OFFICE HOURS
            
            
            //workspace.tps.addKeys(labels)
            
            HashMap<String, Label> labels = workspace.getOfficeHoursGridTACellLabels();
            for (Label label : labels.values()) {
                if (label.getText().equals(taName)
                        || (label.getText().contains(taName + "\n"))
                        || (label.getText().contains("\n" + taName))) {
                    workspace.tps.addKeys(label.getId(), taName);
                    data.removeTAFromCell(label.textProperty(), taName);
                }
            }
            workspace.tps.addToIndex();
            gui.updateToolbarControls(false);
        }
        //Handles save button enable

    }

    /**
     * This function provides a response for when the user clicks on the office
     * hours grid to add or remove a TA to a time slot.
     *
     * @param pane The pane that was toggled.
     */
    public void handleCellToggle(Pane pane) {

        // GET THE TABLE
        TAWorkspace workspace = (TAWorkspace) app.getWorkspaceComponent();
        TableView taTable = workspace.getTATable();
        workspace.getAddButton().setText("Add TA");
        workspace.getAddButton().setOnAction(e -> {
            handleAddTA();

        });
        workspace.nameTextField.setText("");
        workspace.emailTextField.setText("");

        // IS A TA SELECTED IN THE TABLE?
        Object selectedItem = taTable.getSelectionModel().getSelectedItem();

        //If nothing is selected
        if (selectedItem != null) {
            // GET THE TA
            TeachingAssistant ta = (TeachingAssistant) selectedItem;

            String taName = ta.getName();
            
            

            TAData data = (TAData) app.getDataComponent();
            //Get the key
            String cellKey = pane.getId();
            // AND TOGGLE THE OFFICE HOURS IN THE CLICKED CELL
            workspace.tps.ifChangeCell(TOGGLE_CELL, cellKey, taName);
                data.toggleTAOfficeHours(cellKey, taName);

        }
        //Handles save button enable
        gui.updateToolbarControls(false);
    }

    public void handleJbox() {
        TAWorkspace workspace = (TAWorkspace) app.getWorkspaceComponent();
        Boolean Proper = true;
        TAData data = (TAData) app.getDataComponent();
        TAFiles taFile = new TAFiles(app);
        ArrayList<TimeSlot> officeHours = taFile.saveHours(data);
        int jboxValue = workspace.getJbox().getSelectionModel().selectedIndexProperty().getValue();
        int jbox2Value = workspace.getJbox2().getSelectionModel().selectedIndexProperty().getValue();

        AppYesNoCancelDialogSingleton yesNoDialog = AppYesNoCancelDialogSingleton.getSingleton();
        yesNoDialog.show("Confirmation", "Are you sure about changing the time? It will affect the office hours.");
        String selection = yesNoDialog.getSelection();

        if (selection.equals(AppYesNoCancelDialogSingleton.YES)) {
            if (jboxValue != -1 && jbox2Value != -1 && jboxValue >= jbox2Value) {
                AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
                dialog.show("Combo Box", "The start time is greater than or equal to end time, reselect please");
                Proper = false;
            } else {
                if (Proper && jboxValue == -1) {
                    int x = (int) (workspace.getJbox2().getSelectionModel().selectedIndexProperty().get());
                    workspace.tps.ifChangeTime(MODIFIED_TIME, data.getStartHour(), data.getEndHour());
                    data.changeStartAndEnd(data.getStartHour(), x);
                    workspace.tps.ifChangeTime2(MODIFIED_TIME, data.getStartHour(), data.getEndHour());
                    

                } else if (Proper && jbox2Value == -1) {
                    int x = (int) (workspace.getJbox().getSelectionModel().selectedIndexProperty().get());
                    workspace.tps.ifChangeTime(MODIFIED_TIME, data.getStartHour(), data.getEndHour());
                    data.changeStartAndEnd(x, data.getEndHour());
                    workspace.tps.ifChangeTime2(MODIFIED_TIME, data.getStartHour(), data.getEndHour());
                    

                } else if (Proper) {
                    int x = (int) (workspace.getJbox().getSelectionModel().selectedIndexProperty().get());
                    int y = (int) (workspace.getJbox2().getSelectionModel().selectedIndexProperty().get());
                    workspace.tps.ifChangeTime(MODIFIED_TIME, data.getStartHour(), data.getEndHour());
                    data.changeStartAndEnd(x, y);
                    workspace.tps.ifChangeTime(MODIFIED_TIME, x, y);
                }
                workspace.resetWorkspace();
                workspace.reloadWorkspace(app.getDataComponent());
                data.saveReloadData(officeHours);
            }
        }

    }

}

package tam.workspace;

import djf.components.AppDataComponent;
import djf.components.AppWorkspaceComponent;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.collections.FXCollections;
import tam.TAManagerApp;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javax.swing.JComboBox;
import jtps.jTPS;
import properties_manager.PropertiesManager;
import tam.TAManagerProp;
import tam.style.TAStyle;
import tam.data.TAData;
import tam.data.TeachingAssistant;

/**
 * This class serves as the workspace component for the TA Manager application.
 * It provides all the user interface controls in the workspace area.
 *
 * @author Richard McKenna
 */
public class TAWorkspace extends AppWorkspaceComponent {

    // THIS PROVIDES US WITH ACCESS TO THE APP COMPONENTS
    TAManagerApp app;

    // THIS PROVIDES RESPONSES TO INTERACTIONS WITH THIS WORKSPACE
    TAController controller;

    // NOTE THAT EVERY CONTROL IS PUT IN A BOX TO HELP WITH ALIGNMENT
    // FOR THE HEADER ON THE LEFT
    HBox tasHeaderBox;
    Label tasHeaderLabel;

    // FOR THE TA TABLE
    TableView<TeachingAssistant> taTable;
    TableColumn<TeachingAssistant, String> nameColumn;
    TableColumn<TeachingAssistant, String> emailColumn;
    
    //jTPS
    jTPS tps;

    // THE TA INPUT
    HBox addBox;
    TextField nameTextField;
    Button addButton;
    Button clearButton;

    HBox addEmail;
    TextField emailTextField;

    // THE HEADER ON THE RIGHT
    HBox officeHoursHeaderBox;
    Label officeHoursHeaderLabel;

    //ComboBox
    ObservableList<String> time = FXCollections.observableArrayList();;
    ComboBox<String> jbox;
    ComboBox<String> jbox2;
    
    // THE OFFICE HOURS GRID
    GridPane officeHoursGridPane;
    HashMap<String, Pane> officeHoursGridTimeHeaderPanes;
    HashMap<String, Label> officeHoursGridTimeHeaderLabels;
    HashMap<String, Pane> officeHoursGridDayHeaderPanes;
    HashMap<String, Label> officeHoursGridDayHeaderLabels;
    HashMap<String, Pane> officeHoursGridTimeCellPanes;
    HashMap<String, Label> officeHoursGridTimeCellLabels;
    HashMap<String, Pane> officeHoursGridTACellPanes;
    
    HashMap<String, Label> officeHoursGridTACellLabels;

    /**
     * The contstructor initializes the user interface, except for the full
     * office hours grid, since it doesn't yet know what the hours will be until
     * a file is loaded or a new one is created.
     */
    public TAWorkspace(TAManagerApp initApp) {
        // KEEP THIS FOR LATER
        app = initApp;
        // WE'LL NEED THIS TO GET LANGUAGE PROPERTIES FOR OUR UI
        PropertiesManager props = PropertiesManager.getPropertiesManager();

        // INIT THE HEADER ON THE LEFT
        tasHeaderBox = new HBox();
        String tasHeaderText = props.getProperty(TAManagerProp.TAS_HEADER_TEXT.toString());
        tasHeaderLabel = new Label(tasHeaderText);
        tasHeaderBox.getChildren().add(tasHeaderLabel);

        // MAKE THE TABLE AND SETUP THE DATA MODEL
        taTable = new TableView();
        taTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        TAData data = (TAData) app.getDataComponent();
        ObservableList<TeachingAssistant> tableData = data.getTeachingAssistants();
        taTable.setItems(tableData);
        String nameColumnText = props.getProperty(TAManagerProp.NAME_COLUMN_TEXT.toString());
        String emailColumnText = props.getProperty(TAManagerProp.EMAIL_COLUMN_TEXT.toString());
        emailColumn = new TableColumn(emailColumnText);
        emailColumn.prefWidthProperty().bind(taTable.widthProperty().divide(2));
        emailColumn.setCellValueFactory(
                new PropertyValueFactory<>("email")
        );
        nameColumn = new TableColumn(nameColumnText);
        nameColumn.prefWidthProperty().bind(taTable.widthProperty().divide(2));
        nameColumn.setCellValueFactory(
                new PropertyValueFactory<>("name")
        );
        taTable.getColumns().add(nameColumn);
        taTable.getColumns().add(emailColumn);

        // ADD BOX FOR ADDING A TA
        String namePromptText = props.getProperty(TAManagerProp.NAME_PROMPT_TEXT.toString());
        String emailPromptText = props.getProperty(TAManagerProp.EMAIL_PROMPT_TEXT.toString());
        String addButtonText = props.getProperty(TAManagerProp.ADD_BUTTON_TEXT.toString());

        nameTextField = new TextField();
        emailTextField = new TextField();

        nameTextField.setPromptText(namePromptText);
        emailTextField.setPromptText(emailPromptText);

        addButton = new Button(addButtonText);
        clearButton = new Button("Clear");
        addBox = new HBox();
        nameTextField.prefWidthProperty().bind(addBox.widthProperty().multiply(.4));
        emailTextField.prefWidthProperty().bind(addBox.widthProperty().multiply(.4));
        addButton.prefWidthProperty().bind(addBox.widthProperty().multiply(.2));
        clearButton.prefWidthProperty().bind(addBox.widthProperty().multiply(.2));

        addBox.getChildren().add(nameTextField);
        addBox.getChildren().add(emailTextField);
        addBox.getChildren().add(addButton);
        addBox.getChildren().add(clearButton);

        // INIT THE HEADER ON THE RIGHT
        
        time.add("12:00 am");
        for(int i = 1; i < 24; i++)
        {
            if(i/12 == 0)
            {
                time.add(i + ":00 am");
            }
            else
            {
                if(i == 12)
                {
                    time.add("12" + ":00 pm");
                }
                else
                {
                    time.add(i%12 + ":00 pm");
                }
            }
        }
        jbox = new ComboBox<>(time);
        jbox2 = new ComboBox<>(time);
        officeHoursHeaderBox = new HBox();
        String officeHoursGridText = props.getProperty(TAManagerProp.OFFICE_HOURS_SUBHEADER.toString());
        officeHoursHeaderLabel = new Label(officeHoursGridText);
        officeHoursHeaderBox.getChildren().add(officeHoursHeaderLabel);
        officeHoursHeaderBox.getChildren().add(jbox);
        officeHoursHeaderBox.getChildren().add(jbox2);

        // THESE WILL STORE PANES AND LABELS FOR OUR OFFICE HOURS GRID
        officeHoursGridPane = new GridPane();
        officeHoursGridTimeHeaderPanes = new HashMap();
        officeHoursGridTimeHeaderLabels = new HashMap();
        officeHoursGridDayHeaderPanes = new HashMap();
        officeHoursGridDayHeaderLabels = new HashMap();
        officeHoursGridTimeCellPanes = new HashMap();
        officeHoursGridTimeCellLabels = new HashMap();
        officeHoursGridTACellPanes = new HashMap();
        officeHoursGridTACellLabels = new HashMap();

        // ORGANIZE THE LEFT AND RIGHT PANES
        VBox leftPane = new VBox();
        leftPane.getChildren().add(tasHeaderBox);
        leftPane.getChildren().add(taTable);
        leftPane.getChildren().add(addBox);
        VBox rightPane = new VBox();
        rightPane.getChildren().add(officeHoursHeaderBox);
        rightPane.getChildren().add(officeHoursGridPane);

        // BOTH PANES WILL NOW GO IN A SPLIT PANE
        SplitPane sPane = new SplitPane(leftPane, new ScrollPane(rightPane));
        workspace = new BorderPane();

        // AND PUT EVERYTHING IN THE WORKSPACE
        ((BorderPane) workspace).setCenter(sPane);

        // MAKE SURE THE TABLE EXTENDS DOWN FAR ENOUGH
        taTable.prefHeightProperty().bind(workspace.heightProperty().multiply(1.9));
        
        //workspace
        tps = new jTPS(app);
        leftPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.isControlDown() && event.getCode() == KeyCode.Y) {
                    tps.doTransaction();
                }
                if (event.isControlDown() && event.getCode() == KeyCode.Z) {
                    tps.undoTransaction();
                }
            }
        });
        rightPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.isControlDown() && event.getCode() == KeyCode.Y) {
                    tps.doTransaction();
                }
                if (event.isControlDown() && event.getCode() == KeyCode.Z) {
                    tps.undoTransaction();
                }
            }
        });
        
        
        
        // NOW LET'S SETUP THE EVENT HANDLING
        controller = new TAController(app);
        
        //Handle combobox
        jbox.setOnAction(e ->{
            controller.handleJbox();
        });
        jbox2.setOnAction(e ->{
            controller.handleJbox();
        });

        // CONTROLS FOR ADDING TAs
        nameTextField.setOnAction(e -> {
            controller.handleAddTA();
        });
        emailTextField.setOnAction(e -> {
            controller.handleAddTA();
        });
        addButton.setOnAction(e -> {
            controller.handleAddTA();
        });
        clearButton.setOnAction(e -> {
            nameTextField.setText("");
            emailTextField.setText("");
        });

        //Qixin Chen, added delete TA function
        final EventHandler<KeyEvent> keyEventHandler
                = (final KeyEvent keyEvent) -> {
                    if (keyEvent.getCode() == KeyCode.DELETE) {
                        controller.deleteTA();
                    }
                };
        taTable.getSelectionModel().getTableView().setOnMouseClicked(e -> {
            Object selectedItem = taTable.getSelectionModel().getSelectedItem();

            if (selectedItem != null) {
                TeachingAssistant ta = (TeachingAssistant) selectedItem;
                String taName = ta.getName();
                String taEmail = ta.getEmail();
                nameTextField.setText(taName);
                emailTextField.setText(taEmail);
                addButton.setText("Update TA");
                addButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        controller.updateTA();
                    }
                });

            }
        });
        taTable.getSelectionModel().getTableView().setOnKeyPressed(keyEventHandler);

        for (Pane p : officeHoursGridTACellPanes.values()) {
            officeHoursGridTACellPanes.get(this.getCellKey(p)).setOnMouseEntered(e -> {
                p.setStyle("-fx-background-color: #000000");
            });
        }

        //officeHoursGridDayHeaderPanes
    }

    // WE'LL PROVIDE AN ACCESSOR METHOD FOR EACH VISIBLE COMPONENT
    // IN CASE A CONTROLLER OR STYLE CLASS NEEDS TO CHANGE IT
    public HBox getTAsHeaderBox() {
        return tasHeaderBox;
    }

    public Label getTAsHeaderLabel() {
        return tasHeaderLabel;
    }

    public TableView getTATable() {
        return taTable;
    }

    public HBox getAddBox() {
        return addBox;
    }

    public TextField getNameTextField() {
        return nameTextField;
    }

    public TextField getEmailTextField() {
        return emailTextField;
    }

    public Button getAddButton() {
        return addButton;
    }
    public Button getClearButton() {
        return clearButton;
    }

    public HBox getOfficeHoursSubheaderBox() {
        return officeHoursHeaderBox;
    }

    public Label getOfficeHoursSubheaderLabel() {
        return officeHoursHeaderLabel;
    }
    public ComboBox getJbox(){
        return jbox;
    }
    public ComboBox getJbox2(){
        return jbox2;
    }
    public ObservableList<String> getTime()
    {
        return time;
    }
    public GridPane getOfficeHoursGridPane() {
        return officeHoursGridPane;
    }

    public HashMap<String, Pane> getOfficeHoursGridTimeHeaderPanes() {
        return officeHoursGridTimeHeaderPanes;
    }

    public HashMap<String, Label> getOfficeHoursGridTimeHeaderLabels() {
        return officeHoursGridTimeHeaderLabels;
    }

    public HashMap<String, Pane> getOfficeHoursGridDayHeaderPanes() {
        return officeHoursGridDayHeaderPanes;
    }

    public HashMap<String, Label> getOfficeHoursGridDayHeaderLabels() {
        return officeHoursGridDayHeaderLabels;
    }

    public HashMap<String, Pane> getOfficeHoursGridTimeCellPanes() {
        return officeHoursGridTimeCellPanes;
    }

    public HashMap<String, Label> getOfficeHoursGridTimeCellLabels() {
        return officeHoursGridTimeCellLabels;
    }

    public HashMap<String, Pane> getOfficeHoursGridTACellPanes() {
        return officeHoursGridTACellPanes;
    }

    public HashMap<String, Label> getOfficeHoursGridTACellLabels() {
        return officeHoursGridTACellLabels;
    }

    public String getCellKey(Pane testPane) {
        for (String key : officeHoursGridTACellLabels.keySet()) {
            if (officeHoursGridTACellPanes.get(key) == testPane) {
                return key;
            }
        }
        return null;
    }

    public Label getTACellLabel(String cellKey) {
        return officeHoursGridTACellLabels.get(cellKey);
    }

    public Pane getTACellPane(String cellPane) {
        return officeHoursGridTACellPanes.get(cellPane);
    }

    public String buildCellKey(int col, int row) {
        return "" + col + "_" + row;
    }

    public String buildCellText(int militaryHour, String minutes) {
        // FIRST THE START AND END CELLS
        int hour = militaryHour;
        if (hour > 12) {
            hour -= 12;
        }
        String cellText = "" + hour + ":" + minutes;
        if (militaryHour < 12) {
            cellText += "am";
        } else {
            cellText += "pm";
        }
        return cellText;
    }

    @Override
    public void resetWorkspace() {
        // CLEAR OUT THE GRID PANE
        officeHoursGridPane.getChildren().clear();

        // AND THEN ALL THE GRID PANES AND LABELS
        officeHoursGridTimeHeaderPanes.clear();
        officeHoursGridTimeHeaderLabels.clear();
        officeHoursGridDayHeaderPanes.clear();
        officeHoursGridDayHeaderLabels.clear();
        officeHoursGridTimeCellPanes.clear();
        officeHoursGridTimeCellLabels.clear();
        officeHoursGridTACellPanes.clear();
        officeHoursGridTACellLabels.clear();
        
        addButton.setText("Add TA");
        addButton.setOnAction(e -> {
            controller.handleAddTA();

        });
        nameTextField.setText("");
        emailTextField.setText("");
    }

    @Override
    public void reloadWorkspace(AppDataComponent dataComponent) {
        TAData taData = (TAData) dataComponent;
        reloadOfficeHoursGrid(taData);
    }

    public void reloadOfficeHoursGrid(TAData dataComponent) {
        ArrayList<String> gridHeaders = dataComponent.getGridHeaders();

        // ADD THE TIME HEADERS
        for (int i = 0; i < 2; i++) {
            addCellToGrid(dataComponent, officeHoursGridTimeHeaderPanes, officeHoursGridTimeHeaderLabels, i, 0);
            dataComponent.getCellTextProperty(i, 0).set(gridHeaders.get(i));
        }

        // THEN THE DAY OF WEEK HEADERS
        for (int i = 2; i < 7; i++) {
            addCellToGrid(dataComponent, officeHoursGridDayHeaderPanes, officeHoursGridDayHeaderLabels, i, 0);
            dataComponent.getCellTextProperty(i, 0).set(gridHeaders.get(i));
        }

        // THEN THE TIME AND TA CELLS
        int row = 1;
        for (int i = dataComponent.getStartHour(); i < dataComponent.getEndHour(); i++) {
            // START TIME COLUMN
            int col = 0;
            addCellToGrid(dataComponent, officeHoursGridTimeCellPanes, officeHoursGridTimeCellLabels, col, row);
            dataComponent.getCellTextProperty(col, row).set(buildCellText(i, "00"));
            addCellToGrid(dataComponent, officeHoursGridTimeCellPanes, officeHoursGridTimeCellLabels, col, row + 1);
            dataComponent.getCellTextProperty(col, row + 1).set(buildCellText(i, "30"));

            // END TIME COLUMN
            col++;
            int endHour = i;
            addCellToGrid(dataComponent, officeHoursGridTimeCellPanes, officeHoursGridTimeCellLabels, col, row);
            dataComponent.getCellTextProperty(col, row).set(buildCellText(endHour, "30"));
            addCellToGrid(dataComponent, officeHoursGridTimeCellPanes, officeHoursGridTimeCellLabels, col, row + 1);
            dataComponent.getCellTextProperty(col, row + 1).set(buildCellText(endHour + 1, "00"));
            col++;

            // AND NOW ALL THE TA TOGGLE CELLS
            while (col < 7) {
                addCellToGrid(dataComponent, officeHoursGridTACellPanes, officeHoursGridTACellLabels, col, row);
                addCellToGrid(dataComponent, officeHoursGridTACellPanes, officeHoursGridTACellLabels, col, row + 1);
                col++;
            }
            row += 2;
        }

        // CONTROLS FOR TOGGLING TA OFFICE HOURS
        for (Pane p : officeHoursGridTACellPanes.values()) {
            p.setOnMouseClicked(e -> {
                controller.handleCellToggle((Pane) e.getSource());
            });
            // get the key based on each pane
            String[] theKey = p.getId().split("_");

            //Set on mouse enter for each pane
            p.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    //Get the row and col values
                    int colTemp = Integer.parseInt(theKey[0]);
                    int tempC = colTemp;
                    int rowTemp = Integer.parseInt(theKey[1]);
                    int tempR = rowTemp;
                    //highlight the actual pane
                    p.setStyle("-fx-border-color: #ffff00");
                    // Highlight everything above the pane
                    while (tempR >= 1) {
                        officeHoursGridTACellPanes.get(tempC + "_" + tempR).setStyle("-fx-border-color: #ffffb3");
                        tempR--;
                    }
                    tempR = rowTemp;
                    //Highlight everything to the left of the pane
                    while (tempC >= 2) {
                        officeHoursGridTACellPanes.get(tempC + "_" + tempR).setStyle("-fx-border-color: #ffffb3");
                        tempC--;
                    }

                    //Highlighting the day and time panes
                    officeHoursGridDayHeaderPanes.get(colTemp + "_0").setStyle("-fx-border-color: #ffffb3");
                    officeHoursGridTimeCellPanes.get("0_" + rowTemp).setStyle("-fx-border-color: #ffffb3");
                    officeHoursGridTimeCellPanes.get("1_" + rowTemp).setStyle("-fx-border-color: #ffffb3");

                }
            });
            //Return to original color upon mouse exiting
            p.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    int colTemp = Integer.parseInt(theKey[0]);
                    int tempC = colTemp;
                    int rowTemp = Integer.parseInt(theKey[1]);
                    int tempR = rowTemp;
                    p.setStyle("-fx-border-color: #221786");
                    while (tempR >= 1) {
                        officeHoursGridTACellPanes.get(tempC + "_" + tempR).setStyle("-fx-border-color: #221786");
                        tempR--;
                    }
                    tempR = rowTemp;
                    while (tempC >= 2) {
                        officeHoursGridTACellPanes.get(tempC + "_" + tempR).setStyle("-fx-border-color: #221786");
                        tempC--;
                    }
                    officeHoursGridDayHeaderPanes.get(colTemp + "_0").setStyle("-fx-border-color: #221786");
                    officeHoursGridTimeCellPanes.get("0_" + rowTemp).setStyle("-fx-border-color: #221786");
                    officeHoursGridTimeCellPanes.get("1_" + rowTemp).setStyle("-fx-border-color: #221786");
                }
            });
        }

        // AND MAKE SURE ALL THE COMPONENTS HAVE THE PROPER STYLE
        TAStyle taStyle = (TAStyle) app.getStyleComponent();
        taStyle.initOfficeHoursGridStyle();
    }

    public void addCellToGrid(TAData dataComponent, HashMap<String, Pane> panes, HashMap<String, Label> labels, int col, int row) {
        // MAKE THE LABEL IN A PANE
        Label cellLabel = new Label("");
        HBox cellPane = new HBox();
        cellPane.setAlignment(Pos.CENTER);
        cellPane.getChildren().add(cellLabel);

        // BUILD A KEY TO EASILY UNIQUELY IDENTIFY THE CELL
        String cellKey = dataComponent.getCellKey(col, row);
        cellPane.setId(cellKey);
        cellLabel.setId(cellKey);

        // NOW PUT THE CELL IN THE WORKSPACE GRID
        officeHoursGridPane.add(cellPane, col, row);

        // AND ALSO KEEP IN IN CASE WE NEED TO STYLIZE IT
        panes.put(cellKey, cellPane);
        labels.put(cellKey, cellLabel);

        // AND FINALLY, GIVE THE TEXT PROPERTY TO THE DATA MANAGER
        // SO IT CAN MANAGE ALL CHANGES
        dataComponent.setCellProperty(col, row, cellLabel.textProperty());
    }
}

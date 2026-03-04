package gui;

import dto.SiteDTO;
import gui.navigation.NavigableController;
import gui.navigation.Navigator;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import lombok.Setter;
import main.AppContext;
import util.OperationeleStatus;
import util.ProductieStatus;

public class SiteOverviewController implements NavigableController {

    @FXML private TableView<SiteDTO> siteTable;
    @FXML private TableColumn<SiteDTO, String> naamCol;
    @FXML private TableColumn<SiteDTO, String> locatieCol;
    @FXML private TableColumn<SiteDTO, Integer> capaciteitCol;
    @FXML private TableColumn<SiteDTO, OperationeleStatus> operationeleCol;
    @FXML private TableColumn<SiteDTO, ProductieStatus> productieCol;
    @FXML private TableColumn<SiteDTO, SiteDTO> actiesCol;
    @FXML private Button addBtn;
//    @FXML private Button editBtn;
//    @FXML private Button deleteBtn;
//    @FXML private Button refreshBtn;

    @Setter private Navigator navigator;
    private AppContext context;
    private ObservableSites observableSites;
    @Setter private LayoutController layout;

    @Override
    public void setContext(AppContext ctx) {
        // this.context = ctx;
        this.observableSites = ctx.getObservableSites();
        this.observableSites.reload(); // recente data van DB ophalen
    }

    // TODO -> badge-factory maken?
    @FXML
    private void initialize() {
        naamCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().naam()));
        locatieCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().locatie()));
        capaciteitCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().capaciteit()));

        operationeleCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().operationeleStatus()));
        operationeleCol.setCellFactory(col -> new TableCell<>() {

            private final Label label = new Label();

            @Override
            protected void updateItem(OperationeleStatus status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setGraphic(null);
                    return;
                }

                label.setText(status.toString());

                label.getStyleClass().clear();
                label.getStyleClass().add("status-badge");

                switch (status) {
                    case ACTIEF -> label.getStyleClass().add("status-green");
                    case NON_ACTIEF -> label.getStyleClass().add("status-red");
                }

                // tablecell heeft of text of een graphic (bij ons dus laatste)
                setGraphic(label);
            }
        });

        productieCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().productieStatus()));
        productieCol.setCellFactory(col -> new TableCell<>() {

            private final Label label = new Label();

            @Override
            protected void updateItem(ProductieStatus status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setGraphic(null);
                    return;
                }

                label.setText(status.toString());

                label.getStyleClass().clear();
                label.getStyleClass().add("status-badge");

                switch (status) {
                    case GEZOND -> label.getStyleClass().add("status-green");
                    case PROBLEMEN -> label.getStyleClass().add("status-yellow");
                    case OFFLINE -> label.getStyleClass().add("status-red");
                }

                // tablecell heeft of text of een graphic (bij ons dus laatste)
                setGraphic(label);
            }
        });

        actiesCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue()));
        actiesCol.setCellFactory(col -> new TableCell<>() {

            private final Button editBtn = new Button();
            private final Button deleteBtn = new Button();
            private final HBox box = new HBox(8, editBtn, deleteBtn);
            {
                box.setAlignment(Pos.CENTER);

                ImageView editIcon = new ImageView(new Image("/images/pencil-write.png"));
                editIcon.setFitHeight(16);
                editIcon.setFitWidth(16);

                ImageView deleteIcon = new ImageView(new Image("/images/bin.png"));
                deleteIcon.setFitHeight(16);
                deleteIcon.setFitWidth(16);

                editBtn.setGraphic(editIcon);
                deleteBtn.setGraphic(deleteIcon);

                editBtn.setTooltip(new Tooltip("Bewerken"));
                deleteBtn.setTooltip(new Tooltip("Verwijderen"));

                editBtn.getStyleClass().add("icon-button");
                deleteBtn.getStyleClass().add("icon-button");
            }

            @Override
            protected void updateItem(SiteDTO item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null ? null : box);
            }
        });

        naamCol.setStyle("-fx-alignment: CENTER;");
        locatieCol.setStyle("-fx-alignment: CENTER;");
        capaciteitCol.setStyle("-fx-alignment: CENTER;");
        operationeleCol.setStyle("-fx-alignment: CENTER;");
        productieCol.setStyle("-fx-alignment: CENTER;");
        actiesCol.setStyle("-fx-alignment: CENTER;");
        siteTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        siteTable.setFixedCellSize(44); // rijhoogte
        siteTable.setSelectionModel(null);

         //SortedList in GUI
        SortedList<SiteDTO> sortedList = new SortedList<>(observableSites.getFilteredSiteList());
        //binding voor kolomsortering
         sortedList.comparatorProperty().bind(siteTable.comparatorProperty());

         siteTable.setItems(sortedList);

        //default sortering
        // idCol.setSortType(TableColumn.SortType.ASCENDING);
        //siteTable.getSortOrder().add(idCol);
        siteTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);// kolommen vullen automatisch de breedte

    }


    @FXML
    private void onAdd() {
        // navigator.showDialog(View.SITES_FORM, "Site toevoegen",null);
        System.out.println("add site");
    }
}


//    @FXML
//    private void initialize() {
//        idCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().siteId()));
//        naamCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().naam()));
//        locatieCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().locatie()));
//        capaciteitCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().capaciteit()));
//        operationeleCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().operationeleStatus()));
//        productieCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().productieStatus()));

        // SortedList in GUI
//        SortedList<SiteDTO> sortedList = new SortedList<>(observableSites.getFilteredSiteList());
        //binding voor kolomsortering
       // sortedList.comparatorProperty().bind(siteTable.comparatorProperty());

       // siteTable.setItems(sortedList);

        //default sortering
//        idCol.setSortType(TableColumn.SortType.ASCENDING);
//        siteTable.getSortOrder().add(idCol);
//        siteTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);// kolommen vullen automatisch de breedte
//
//        editBtn.disableProperty().bind(siteTable.getSelectionModel().selectedItemProperty().isNull());
//        deleteBtn.disableProperty().bind(siteTable.getSelectionModel().selectedItemProperty().isNull());
//    }


//
//    @FXML
//    private void onEdit() {
//      //  SiteDTO selected = siteTable.getSelectionModel().getSelectedItem();
//      //  if (selected == null) return;
//
////        navigator.showDialog(View.SITES_FORM, "Site wijzigen",controller -> {
////                SiteFormController form = (SiteFormController) controller;
////                form.loadForEdit(selected); // prefill
////        });
//    }
//
//    @FXML
//    private void onDelete() {
//    //    SiteDTO selected = siteTable.getSelectionModel().getSelectedItem();
////        if (selected == null) return;
////
////        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
////        alert.setTitle("Site verwijderen");
////        alert.setHeaderText("Ben je zeker dat je deze site wil verwijderen?");
////        alert.setContentText(selected.naam() + " (" + selected.locatie() + ")");
////
////        ButtonType deleteBtn = new ButtonType("Verwijderen");
////        ButtonType cancelBtn = new ButtonType("Annuleren", ButtonBar.ButtonData.CANCEL_CLOSE);
////        alert.getButtonTypes().setAll(deleteBtn, cancelBtn);
////
////        alert.showAndWait().ifPresent(response -> {
////            if (response == deleteBtn) {
////                try {
////                    observableSites.deleteSite(selected.siteId());
////                } catch (IllegalArgumentException ex) {
////                    new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
////                } catch (RuntimeException ex) {
////                    new Alert(Alert.AlertType.ERROR, "Verwijderen mislukt: " + ex.getMessage()).showAndWait();
////                }
////            }
////        });
////
////        observableSites.reload();
//    }
//
//    @FXML
//    private void onBack() {
//        navigator.goTo(View.MAIN_MENU);
//    }
//
//    @FXML
//    private void onRefresh() {
//        observableSites.reload();
//    }


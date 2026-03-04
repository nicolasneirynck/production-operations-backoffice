package gui;

import dto.SiteDTO;
import gui.navigation.NavigableController;
import gui.navigation.Navigator;
import gui.navigation.View;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Setter;
import main.AppContext;
import util.OperationeleStatus;
import util.ProductieStatus;

import java.io.IOException;

public class SiteOverviewController implements NavigableController {


    @FXML
    private TableView<SiteDTO> siteTable;
    @FXML
    private TableColumn<SiteDTO, String> naamCol;
    @FXML
    private TableColumn<SiteDTO, String> locatieCol;
    @FXML
    private TableColumn<SiteDTO, Integer> capaciteitCol;
    @FXML
    private TableColumn<SiteDTO, OperationeleStatus> operationeleCol;
    @FXML
    private TableColumn<SiteDTO, ProductieStatus> productieCol;
    @FXML
    private TableColumn<SiteDTO, SiteDTO> actiesCol;

    @FXML
    private VBox formHost;
    @FXML
    private Button addBtn;

    @Setter
    private Navigator navigator;
    private AppContext context;
    private ObservableSites observableSites;
    @Setter
    private LayoutController layout;

    @Override
    public void setContext(AppContext ctx) {
        this.context = ctx;
        this.observableSites = ctx.getObservableSites();
        this.observableSites.reload(); // recente data van DB ophalen
    }

    // TODO -> badge-factory maken?
    @FXML
    private void initialize() {
        naamCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().naam()));
        locatieCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().locatie()));
        capaciteitCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().capaciteit()));

        configureStatusColumns();
        configureActionColumn();

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

        addBtn.disableProperty().bind(formHost.visibleProperty());
    }

    private void configureStatusColumns() {

        operationeleCol.setCellValueFactory(
                c -> new SimpleObjectProperty<>(c.getValue().operationeleStatus())
        );

        operationeleCol.setCellFactory(col -> new TableCell<>() {

            private final Label label = new Label();

            {
                setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(OperationeleStatus status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setGraphic(null);
                    return;
                }

                label.setText(status.toString());
                label.getStyleClass().setAll("status-badge");

                switch (status) {
                    case ACTIEF -> label.getStyleClass().add("status-green");
                    case NON_ACTIEF -> label.getStyleClass().add("status-red");
                }

                setGraphic(label);
            }
        });

        productieCol.setCellValueFactory(
                c -> new SimpleObjectProperty<>(c.getValue().productieStatus())
        );

        productieCol.setCellFactory(col -> new TableCell<>() {

            private final Label label = new Label();

            {
                setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(ProductieStatus status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setGraphic(null);
                    return;
                }

                label.setText(status.toString());
                label.getStyleClass().setAll("status-badge");

                switch (status) {
                    case GEZOND -> label.getStyleClass().add("status-green");
                    case PROBLEMEN -> label.getStyleClass().add("status-yellow");
                    case OFFLINE -> label.getStyleClass().add("status-red");
                }

                setGraphic(label);
            }
        });
    }

    private void configureActionColumn() {

        actiesCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue()));

        actiesCol.setCellFactory(col -> new TableCell<>() {

            private final Button editBtn = new Button("");
            private final Button deleteBtn = new Button("");
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

                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }

                //editBtn.setOnAction(e -> showEditForm(item));
                //deleteBtn.setOnAction(e -> deleteSite(item));

                setGraphic(box);
            }
        });
    }

    // tijdelijke oplossing
    @FXML
    private void onAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(View.SITES_FORM.fxml));
            Parent form = loader.load();

            SiteFormController formController = loader.getController();
            formController.setContext(context); // context moet niet null zijn
            formController.loadForCreate();

            formController.setOnClose(() -> {
                formHost.getChildren().clear();
                formHost.setManaged(false);
                formHost.setVisible(false);
            });

            formHost.getChildren().setAll(form);
            formHost.setManaged(true);
            formHost.setVisible(true);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}


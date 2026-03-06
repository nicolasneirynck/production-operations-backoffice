package gui;

import dto.LocatieDTO;
import dto.SiteDTO;
import exception.SiteException;
import gui.navigation.NavigableController;
import gui.navigation.Navigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import lombok.Setter;
import main.AppContext;
import util.OperationeleStatus;
import util.ProductieStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SiteFormController implements NavigableController {

    @FXML private VBox root;
    @FXML private Label titleLabel;
    @FXML private TextField naamTf;
    @FXML private TextField capaciteitTf;
    @FXML private ComboBox<OperationeleStatus> operationeelCb;
    @FXML private ComboBox<ProductieStatus> productieCb;
    @FXML private TextField straatTf;
    @FXML private TextField nummerTf;
    @FXML private TextField postcodeTf;
    @FXML private TextField stadTf;
   // @FXML private TextField landTf;
   @FXML private ComboBox<String> landCb;

    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    @FXML private Label naamErr;
    @FXML private Label capaciteitErr;
    @FXML private Label operationeelErr;
    @FXML private Label productieErr;
    @FXML private Label straatErr;
    @FXML private Label nummerErr;
    @FXML private Label postcodeErr;
    @FXML private Label stadErr;
    @FXML private Label landErr;

    @Setter private Navigator navigator;
    private AppContext context;
    private ObservableSites observableSites;

    @Setter private Runnable onClose;

    private Long editingSiteId = null;

    @Override
    public void setContext(AppContext ctx) {
        context = ctx;
        this.observableSites = ctx.getObservableSites();
        this.observableSites.reload();
    }

    @FXML
    private void initialize() {
        operationeelCb.setItems(FXCollections.observableArrayList(OperationeleStatus.values()));
        productieCb.setItems(FXCollections.observableArrayList(ProductieStatus.values()));

        operationeelCb.getSelectionModel().select(OperationeleStatus.ACTIEF);
        productieCb.getSelectionModel().select(ProductieStatus.GEZOND);

        operationeelCb.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == OperationeleStatus.NON_ACTIEF) {
                productieCb.setValue(ProductieStatus.OFFLINE);
                productieCb.setDisable(true);

                clearError("operationeleStatus");
                clearError("productieStatus");
            } else {
                productieCb.setDisable(false);

                if (productieCb.getValue() == null) {
                    productieCb.setValue(ProductieStatus.GEZOND);
                }
            }
        });

        naamTf.textProperty().addListener((o, a, b) -> clearError("naam"));
        capaciteitTf.textProperty().addListener((o, a, b) -> clearError("capaciteit"));

        operationeelCb.valueProperty().addListener((o, a, b) -> clearError("operationeleStatus"));
        productieCb.valueProperty().addListener((o, a, b) -> clearError("productieStatus"));

        straatTf.textProperty().addListener((o, a, b) -> clearError("locatie.straat"));
        nummerTf.textProperty().addListener((o, a, b) -> clearError("locatie.nummer"));
        postcodeTf.textProperty().addListener((o, a, b) -> clearError("locatie.postcode"));
        stadTf.textProperty().addListener((o, a, b) -> clearError("locatie.stad"));
        //landTf.textProperty().addListener((o, a, b) -> clearError("locatie.land"));

        List<String> landen = Arrays.stream(Locale.getISOCountries())
                .map(code -> Locale.of("", code).getDisplayCountry())
                .sorted()
                .toList();

        landCb.setItems(FXCollections.observableArrayList(landen));
        landCb.setVisibleRowCount(10);

        nummerTf.setTextFormatter(new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();
            return newText.matches("\\d*") ? change : null; // zo kunt ge enkel cijfers ingeven
        }));
        capaciteitTf.setTextFormatter(new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();
            return newText.matches("\\d*") ? change : null;
        }));
    }

    public void loadForCreate() {
        editingSiteId = null;
        titleLabel.setText("Site aanmaken");
        naamTf.clear();
        capaciteitTf.clear();
        straatTf.clear();
        nummerTf.clear();
        postcodeTf.clear();
        stadTf.clear();

        operationeelCb.getSelectionModel().select(OperationeleStatus.ACTIEF);
        productieCb.getSelectionModel().select(ProductieStatus.GEZOND);

        clearErrors();
    }

    public void loadForEdit(SiteDTO site) {
        editingSiteId = site.siteId();
        titleLabel.setText("Site wijzigen");

        naamTf.setText(site.naam());
        capaciteitTf.setText(String.valueOf(site.capaciteit()));

        LocatieDTO loc = site.locatie();

        straatTf.setText(loc.straat());
        nummerTf.setText(loc.nummer());
        postcodeTf.setText(loc.postcode());
        stadTf.setText(loc.stad());
        landCb.setValue(loc.land());

        operationeelCb.getSelectionModel().select(site.operationeleStatus());
        productieCb.getSelectionModel().select(site.productieStatus());

        clearErrors();
    }

    @FXML
    private void onCancel() {
        close();
    }

    private void close() {
        if (onClose != null) onClose.run();
    }

    @FXML
    private void onSave() {
        clearErrors();

        int capaciteit;

        try {
            capaciteit = parseCapaciteit(capaciteitTf.getText());
        } catch (IllegalArgumentException ex) {
            return;
        }

        try {
            String naam = naamTf.getText();
            String straat = straatTf.getText();
            String nummer = nummerTf.getText();
            String postcode = postcodeTf.getText();
            String stad = stadTf.getText();
            String land = landCb.getValue();

            OperationeleStatus op = operationeelCb.getValue();
            ProductieStatus prod = productieCb.getValue();

            if (editingSiteId == null) {
                observableSites.addSite(naam, straat, nummer, postcode, stad, land, capaciteit, op, prod);
            } else {
                observableSites.updateSite(editingSiteId, naam, straat, nummer, postcode, stad, land, capaciteit, op, prod);
            }

            observableSites.reload();
            close();

        } catch (SiteException ex) {
            showValidationErrors(ex);
        } catch (IllegalArgumentException ex) {
            naamErr.setText(ex.getMessage());
            naamErr.setManaged(true);
            naamErr.setVisible(true);
            naamTf.getStyleClass().add("field-error");
        } catch (RuntimeException ex) {
            new Alert(Alert.AlertType.ERROR, "Opslaan mislukt: " + ex.getMessage()).showAndWait();
        }
    }

    private void showValidationErrors(SiteException ex) {
        clearErrors();

        ex.getExceptionMap().forEach((field, iae) -> {
            String msg = iae.getMessage();

            switch (field) {
                case "naam" -> {
                    showError(naamErr,msg);
                    naamTf.getStyleClass().add("field-error");
                }
                case "capaciteit" -> {
                    showError(capaciteitErr,msg);
                    capaciteitTf.getStyleClass().add("field-error");
                }
                case "operationeleStatus" -> {
                    showError(operationeelErr,msg);
                    operationeelCb.getStyleClass().add("field-error");
                }
                case "productieStatus" -> {
                    showError(productieErr,msg);
                    productieCb.getStyleClass().add("field-error");
                }

                case "locatie.straat" -> {
                    showError(straatErr,msg);
                    straatTf.getStyleClass().add("field-error");
                }
                case "locatie.nummer" -> {
                    showError(nummerErr,msg);
                    nummerTf.getStyleClass().add("field-error");
                }
                case "locatie.postcode" -> {
                    showError(postcodeErr,msg);
                    postcodeTf.getStyleClass().add("field-error");
                }
                case "locatie.stad" -> {
                    showError(stadErr,msg);
                    stadTf.getStyleClass().add("field-error");
                }
                case "locatie.land" -> {
                    showError(landErr,msg);
                    landCb.getStyleClass().add("field-error"); // nog nodig?
                }
                default -> {
                    new Alert(Alert.AlertType.ERROR, msg).showAndWait();
                }
            }
        });
    }

    private void showError(Label lbl) {
        lbl.setManaged(true);
        lbl.setVisible(true);
    }

    private int parseCapaciteit(String input) {
        String s = (input == null) ? "" : input.trim();

        if (s.isEmpty()) return 0;

        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            showError(capaciteitErr, "Capaciteit moet een getal zijn.");
            capaciteitTf.getStyleClass().add("field-error");
            throw new IllegalArgumentException("Capaciteit moet een getal zijn.");
        }
    }

    private void clearError(String key) {
        switch (key) {
            case "naam" -> {
                clearLabel(naamErr);
                naamTf.getStyleClass().remove("field-error");
            }
            case "capaciteit" -> {
                clearLabel(capaciteitErr);
                capaciteitTf.getStyleClass().remove("field-error");
            }
            case "operationeleStatus" -> {
                clearLabel(operationeelErr);
                operationeelCb.getStyleClass().remove("field-error");
            }
            case "productieStatus" -> {
                clearLabel(productieErr);
                productieCb.getStyleClass().remove("field-error");
            }

            case "locatie.straat" -> {
                clearLabel(straatErr);
                straatTf.getStyleClass().remove("field-error");
            }
            case "locatie.nummer" -> {
                clearLabel(nummerErr);
                nummerTf.getStyleClass().remove("field-error");
            }
            case "locatie.postcode" -> {
                clearLabel(postcodeErr);
                postcodeTf.getStyleClass().remove("field-error");
            }
            case "locatie.stad" -> {
                clearLabel(stadErr);
                stadTf.getStyleClass().remove("field-error");
            }
            case "locatie.land" -> {
                clearLabel(landErr);
                landCb.getStyleClass().remove("field-error"); // nog nodig?
            }
        }
    }

    private void clearLabel(Label lbl) {
        lbl.setText("");
    }

    private void showError(Label lbl, String msg) {
        lbl.setText(msg);
    }

    private void clearErrors() {
        clearLabel(naamErr);
        clearLabel(capaciteitErr);
        clearLabel(operationeelErr);
        clearLabel(productieErr);
        clearLabel(straatErr);
        clearLabel(nummerErr);
        clearLabel(postcodeErr);
        clearLabel(stadErr);
        clearLabel(landErr);

        naamTf.getStyleClass().remove("field-error");
        capaciteitTf.getStyleClass().remove("field-error");
        operationeelCb.getStyleClass().remove("field-error");
        productieCb.getStyleClass().remove("field-error");
        straatTf.getStyleClass().remove("field-error");
        nummerTf.getStyleClass().remove("field-error");
        postcodeTf.getStyleClass().remove("field-error");
        stadTf.getStyleClass().remove("field-error");
        landCb.getStyleClass().remove("field-error"); // nog nodig?
    }

}
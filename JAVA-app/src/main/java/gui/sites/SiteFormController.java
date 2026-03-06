package gui.sites;

import dto.LocatieDTO;
import dto.SiteDTO;
import exception.SiteException;
import gui.navigation.FormController;
import gui.navigation.Navigator;
import javafx.collections.FXCollections;
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

public class SiteFormController implements FormController {

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

    @FXML private Label naamErr;
    @FXML private Label capaciteitErr;
    @FXML private Label operationeelErr;
    @FXML private Label productieErr;
    @FXML private Label straatErr;
    @FXML private Label nummerErr;
    @FXML private Label postcodeErr;
    @FXML private Label stadErr;
    @FXML private Label landErr;

    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    @Setter private Runnable onClose;

    private AppContext context;
    private ObservableSites observableSites;


    private Long editingSiteId = null;

    @Override
    public void setContext(AppContext ctx) {
        context = ctx;
        this.observableSites = ctx.getObservableSites();
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

    @Override
    public void loadData() {
        observableSites.reload();
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
        landCb.setValue(null);

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
//        } catch (IllegalArgumentException ex) {
//            naamErr.setText(ex.getMessage());
//            naamErr.setManaged(true);
//            naamErr.setVisible(true);
//            naamTf.getStyleClass().add("field-error");
        } catch (RuntimeException ex) {
            new Alert(Alert.AlertType.ERROR, "Opslaan mislukt: " + ex.getMessage()).showAndWait();
        }
    }

    @FXML
    private void onCancel() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Annuleren");
        alert.setHeaderText("Wijzigingen annuleren?");
        alert.setContentText("Niet-opgeslagen wijzigingen gaan verloren.");

        ButtonType yesBtn = new ButtonType("Ja");
        ButtonType noBtn = new ButtonType("Nee", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(yesBtn, noBtn);

        alert.showAndWait().ifPresent(response -> {
            if (response == yesBtn) {
                close();
            }
        });
    }

    private void close() {
        if (onClose != null) onClose.run();
    }


    private void showValidationErrors(SiteException ex) {
        clearErrors();

        ex.getExceptionMap().forEach((field, iae) -> {
            String msg = iae.getMessage();

            switch (field) {
                case "naam" -> {
                    naamErr.setText(msg);
                    naamTf.getStyleClass().add("field-error");
                }
                case "capaciteit" -> {
                    capaciteitErr.setText(msg);
                    capaciteitTf.getStyleClass().add("field-error");
                }
                case "operationeleStatus" -> {
                    operationeelErr.setText(msg);
                    operationeelCb.getStyleClass().add("field-error");
                }
                case "productieStatus" -> {
                    productieErr.setText(msg);
                    productieCb.getStyleClass().add("field-error");
                }

                case "locatie.straat" -> {
                    straatErr.setText(msg);
                    straatTf.getStyleClass().add("field-error");
                }
                case "locatie.nummer" -> {
                    nummerErr.setText(msg);
                    nummerTf.getStyleClass().add("field-error");
                }
                case "locatie.postcode" -> {
                    postcodeErr.setText(msg);
                    postcodeTf.getStyleClass().add("field-error");
                }
                case "locatie.stad" -> {
                    stadErr.setText(msg);
                    stadTf.getStyleClass().add("field-error");
                }
                case "locatie.land" -> {
                    landErr.setText(msg);
                    landCb.getStyleClass().add("field-error"); // nog nodig?
                }
                default -> {
                    new Alert(Alert.AlertType.ERROR, msg).showAndWait();
                }
            }
        });
    }

    private int parseCapaciteit(String input) {
        String s = (input == null) ? "" : input.trim();

        if (s.isEmpty()) return 0;

        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            capaciteitErr.setText("Capaciteit moet een getal zijn.");
            capaciteitTf.getStyleClass().add("field-error");
            throw new IllegalArgumentException("Capaciteit moet een getal zijn.");
        }
    }

    private void clearError(String key) {
        switch (key) {
            case "naam" -> {
                naamErr.setText("");
                naamTf.getStyleClass().remove("field-error");
            }
            case "capaciteit" -> {
                capaciteitErr.setText("");
                capaciteitTf.getStyleClass().remove("field-error");
            }
            case "operationeleStatus" -> {
                operationeelErr.setText("");
                operationeelCb.getStyleClass().remove("field-error");
            }
            case "productieStatus" -> {
                productieErr.setText("");
                productieCb.getStyleClass().remove("field-error");
            }

            case "locatie.straat" -> {
                straatErr.setText("");
                straatTf.getStyleClass().remove("field-error");
            }
            case "locatie.nummer" -> {
                nummerErr.setText("");
                nummerTf.getStyleClass().remove("field-error");
            }
            case "locatie.postcode" -> {
                postcodeErr.setText("");
                postcodeTf.getStyleClass().remove("field-error");
            }
            case "locatie.stad" -> {
                stadErr.setText("");
                stadTf.getStyleClass().remove("field-error");
            }
            case "locatie.land" -> {
                landErr.setText("");
                landCb.getStyleClass().remove("field-error"); // nog nodig?
            }
        }
    }


    private void clearErrors() {
        naamErr.setText("");
        capaciteitErr.setText("");
        operationeelErr.setText("");
        productieErr.setText("");
        straatErr.setText("");
        nummerErr.setText("");
        postcodeErr.setText("");
        stadErr.setText("");
        landErr.setText("");

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
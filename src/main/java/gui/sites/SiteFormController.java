package gui.sites;

import dto.GebruikerDTO;
import dto.LocatieDTO;
import dto.SiteDTO;
import exception.ValidationException;
import gui.navigation.ClosableFormGuard;
import gui.navigation.FormController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import lombok.Setter;
import main.AppContext;
import util.OperationeleStatus;
import util.ProductieStatus;
import util.Rollen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SiteFormController implements FormController, ClosableFormGuard {

    @FXML private Label titleLabel;

    @FXML private TextField naamTf;
    @FXML private ComboBox<GebruikerDTO> verantwoordelijkeCb;
    @FXML private TextField capaciteitTf;
    @FXML private ComboBox<OperationeleStatus> operationeelCb;
    @FXML private ComboBox<ProductieStatus> productieCb;
    @FXML private TextField straatTf;
    @FXML private TextField nummerTf;
    @FXML private TextField postcodeTf;
    @FXML private TextField gemeenteTf;
   @FXML private ComboBox<String> landCb;

    @FXML private Label naamErr;
    @FXML private Label verantwoordelijkeErr;
    @FXML private Label capaciteitErr;
    @FXML private Label operationeelErr;
    @FXML private Label productieErr;
    @FXML private Label straatErr;
    @FXML private Label nummerErr;
    @FXML private Label postcodeErr;
    @FXML private Label gemeenteErr;
    @FXML private Label landErr;
    @FXML private Label formInfoLbl;

    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    @Setter private Runnable onClose;

    private AppContext context;
    private ObservableSites observableSites;

    private Long editingSiteId = null;
    private SiteFormData initialFormData;

    @Override
    public void setContext(AppContext ctx) {
        context = ctx;
        this.observableSites = ctx.getObservableSites();
    }

    @FXML
    private void initialize() {
        configureVerantwoordelijkeComboBox();
        configureStatusComboBoxen();
        configureValidationListeners();
        configureLandComboBox();
        configureNumericFields();
    }

    private void configureVerantwoordelijkeComboBox() {
        verantwoordelijkeCb.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(GebruikerDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.voornaam() + " " + item.naam());
            }
        });

        verantwoordelijkeCb.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(GebruikerDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.voornaam() + " " + item.naam());
            }
        });
    }

    private void configureStatusComboBoxen() {
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
    }

    private void configureLandComboBox() {
        List<String> landen = Arrays.stream(Locale.getISOCountries())
                .map(code -> Locale.of("", code).getDisplayCountry())
                .sorted()
                .toList();

        landCb.setItems(FXCollections.observableArrayList(landen));
        landCb.setVisibleRowCount(10);
    }


    private void configureValidationListeners() {
        verantwoordelijkeCb.valueProperty().addListener((o, a, b) -> clearError("verantwoordelijke"));

        naamTf.textProperty().addListener((o, a, b) -> clearError("naam"));
        capaciteitTf.textProperty().addListener((o, a, b) -> clearError("capaciteit"));

        operationeelCb.valueProperty().addListener((o, a, b) -> clearError("operationeleStatus"));
        productieCb.valueProperty().addListener((o, a, b) -> clearError("productieStatus"));

        straatTf.textProperty().addListener((o, a, b) -> clearError("locatie.straat"));
        nummerTf.textProperty().addListener((o, a, b) -> clearError("locatie.nummer"));
        postcodeTf.textProperty().addListener((o, a, b) -> clearError("locatie.postcode"));
        gemeenteTf.textProperty().addListener((o, a, b) -> clearError("locatie.gemeente"));
    }

    private void configureNumericFields() {
        nummerTf.setTextFormatter(new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();
            return newText.matches("\\d*") ? change : null;
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

    private void loadVerantwoordelijkenVoorCreate() {
        List<GebruikerDTO> verantwoordelijken = context.getGebruikerController()
                .getVerantwoordelijkenZonderSite()
                .stream()
                .sorted((g1, g2) -> {
                    int result = g1.naam().compareToIgnoreCase(g2.naam());
                    return result != 0 ? result : g1.voornaam().compareToIgnoreCase(g2.voornaam());
                })
                .toList();

        verantwoordelijkeCb.setItems(FXCollections.observableArrayList(verantwoordelijken));
    }

    private void loadVerantwoordelijkenVoorEdit(GebruikerDTO huidigeVerantwoordelijke) {
        List<GebruikerDTO> verantwoordelijken = new ArrayList<>(
                context.getGebruikerController().getVerantwoordelijkenZonderSite()
        );

        if (huidigeVerantwoordelijke != null) {
            boolean alAanwezig = verantwoordelijken.stream()
                    .anyMatch(g -> g.gebruikerId() == huidigeVerantwoordelijke.gebruikerId());

            if (!alAanwezig) {
                verantwoordelijken.add(huidigeVerantwoordelijke);
            }
        }

        verantwoordelijken.sort((g1, g2) -> {
            int result = g1.naam().compareToIgnoreCase(g2.naam());
            return result != 0 ? result : g1.voornaam().compareToIgnoreCase(g2.voornaam());
        });

        verantwoordelijkeCb.setItems(FXCollections.observableArrayList(verantwoordelijken));
    }

    public void loadForCreate() {
        editingSiteId = null;
        titleLabel.setText("Site aanmaken");
        initialFormData = currentFormData();

        loadVerantwoordelijkenVoorCreate();

        naamTf.clear();
        verantwoordelijkeCb.setValue(null);
        capaciteitTf.clear();
        straatTf.clear();
        nummerTf.clear();
        postcodeTf.clear();
        gemeenteTf.clear();
        landCb.setValue(null);

        operationeelCb.getSelectionModel().select(OperationeleStatus.ACTIEF);
        productieCb.getSelectionModel().select(ProductieStatus.GEZOND);

        clearErrors();

        boolean geenBeschikbareVerantwoordelijken = verantwoordelijkeCb.getItems().isEmpty();
        verantwoordelijkeCb.setDisable(geenBeschikbareVerantwoordelijken);

        if (geenBeschikbareVerantwoordelijken) {
            formInfoLbl.setText("Momenteel is er geen vrije verantwoordelijke beschikbaar.");
            formInfoLbl.setVisible(true);
            formInfoLbl.setManaged(true);
        } else {
            formInfoLbl.setVisible(false);
            formInfoLbl.setManaged(false);
        }
    }

    public void loadForEdit(SiteDTO site) {
        editingSiteId = site.siteId();
        titleLabel.setText("Site wijzigen");

        loadVerantwoordelijkenVoorEdit(site.verantwoordelijke());

        saveBtn.setDisable(false);
        verantwoordelijkeCb.setDisable(false);

        naamTf.setText(site.naam());
        verantwoordelijkeCb.setValue(site.verantwoordelijke());
        capaciteitTf.setText(String.valueOf(site.capaciteit()));

        LocatieDTO loc = site.locatie();
        straatTf.setText(loc == null ? "" : loc.straat());
        nummerTf.setText(loc == null ? "" : loc.nummer());
        postcodeTf.setText(loc == null ? "" : loc.postcode());
        gemeenteTf.setText(loc == null ? "" : loc.gemeente());
        landCb.setValue(loc == null ? null : loc.land());

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

        GebruikerDTO verantwoordelijke = verantwoordelijkeCb.getValue();

        try {
            String naam = naamTf.getText();
            String straat = straatTf.getText();
            String nummer = nummerTf.getText();
            String postcode = postcodeTf.getText();
            String gemeente = gemeenteTf.getText();
            String land = landCb.getValue();

            Long verantwoordelijkeId = verantwoordelijke != null ? verantwoordelijke.gebruikerId() : null;

            OperationeleStatus op = operationeelCb.getValue();
            ProductieStatus prod = productieCb.getValue();

            if (editingSiteId == null) {
                observableSites.addSite(
                        naam, verantwoordelijkeId, straat, nummer, postcode, gemeente, land, capaciteit, op, prod
                );
            } else {
                observableSites.updateSite(
                        editingSiteId, verantwoordelijkeId, naam, straat, nummer, postcode, gemeente, land, capaciteit, op, prod
                );
            }
            close();

        } catch (ValidationException ex) {
            showValidationErrors(ex);
        } catch (RuntimeException ex) {
            new Alert(Alert.AlertType.ERROR, "Opslaan mislukt: " + ex.getMessage()).showAndWait();
        }
    }

    @FXML
    private void onCancel() {
        if (canClose()) {
            close();
        }
    }

    private void close() {
        if (onClose != null) onClose.run();
    }


    private void showValidationErrors(ValidationException ex) {
        clearErrors();

        ex.getExceptionMap().forEach((field, iae) -> {
            String msg = iae.getMessage();

            switch (field) {
                case "naam" -> {
                    naamErr.setText(msg);
                    naamTf.getStyleClass().add("field-error");
                }
                case "verantwoordelijke" -> {
                    verantwoordelijkeErr.setText(msg);
                    verantwoordelijkeCb.getStyleClass().add("field-error");
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
                case "locatie.gemeente" -> {
                    gemeenteErr.setText(msg);
                    gemeenteTf.getStyleClass().add("field-error");
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
            case "verantwoordelijke" -> {
                verantwoordelijkeErr.setText("");
                verantwoordelijkeCb.getStyleClass().remove("field-error");
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
            case "locatie.gemeente" -> {
                gemeenteErr.setText("");
                gemeenteTf.getStyleClass().remove("field-error");
            }
            case "locatie.land" -> {
                landErr.setText("");
                landCb.getStyleClass().remove("field-error"); // nog nodig?
            }
        }
    }


    private void clearErrors() {
        naamErr.setText("");
        verantwoordelijkeErr.setText("");
        capaciteitErr.setText("");
        operationeelErr.setText("");
        productieErr.setText("");
        straatErr.setText("");
        nummerErr.setText("");
        postcodeErr.setText("");
        gemeenteErr.setText("");
        landErr.setText("");

        naamTf.getStyleClass().remove("field-error");
        verantwoordelijkeCb.getStyleClass().remove("field-error");
        capaciteitTf.getStyleClass().remove("field-error");
        operationeelCb.getStyleClass().remove("field-error");
        productieCb.getStyleClass().remove("field-error");
        straatTf.getStyleClass().remove("field-error");
        nummerTf.getStyleClass().remove("field-error");
        postcodeTf.getStyleClass().remove("field-error");
        gemeenteTf.getStyleClass().remove("field-error");
        landCb.getStyleClass().remove("field-error");
    }

    @Override
    public boolean canClose() {
        if (!hasUnsavedChanges()) {
            return true;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Formulier sluiten");
        alert.setHeaderText("Niet-opgeslagen wijzigingen");
        alert.setContentText("Je wijzigen werden niet opgeslagen. Ben je zeker dat je de pagina wil verlaten?");

        ButtonType ja = new ButtonType("Ja");
        ButtonType nee = new ButtonType("Nee", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(ja, nee);

        return alert.showAndWait().orElse(nee) == ja;
    }

    private SiteFormData currentFormData() {
        GebruikerDTO verantwoordelijke = verantwoordelijkeCb.getValue();

        return new SiteFormData(
                naamTf.getText(),
                verantwoordelijke == null ? null : verantwoordelijke.gebruikerId(),
                capaciteitTf.getText(),
                operationeelCb.getValue(),
                productieCb.getValue(),
                straatTf.getText(),
                nummerTf.getText(),
                postcodeTf.getText(),
                gemeenteTf.getText(),
                landCb.getValue()
        );}

    private boolean hasUnsavedChanges() {
        return !currentFormData().equals(initialFormData);
    }


    private record SiteFormData(
            String naam,
            Long verantwoordelijkeId,
            String capaciteit,
            OperationeleStatus operationeleStatus,
            ProductieStatus productieStatus,
            String straat,
            String nummer,
            String postcode,
            String gemeente,
            String land
    ) {
    }


}

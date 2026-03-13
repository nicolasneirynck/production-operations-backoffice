package gui.sites;

import domein.controllers.SiteController;
import dto.SiteDTO;
import exception.ValidationException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import lombok.Getter;
import util.OperationeleStatus;
import util.ProductieStatus;

public class ObservableSites {

    private final SiteController controller;

    private final ObservableList<SiteDTO> observableSiteList;
    @Getter private final FilteredList<SiteDTO> filteredSiteList;

    public ObservableSites(SiteController controller) {
        this.controller = controller;
        this.observableSiteList = FXCollections.observableArrayList();
        this.filteredSiteList = new FilteredList<>(observableSiteList, s -> true);
    }

    public void addSite(String naam, Long verantwoordelijkeId, String straat, String nummer, String postcode, String gemeente, String land, int capaciteit, OperationeleStatus op, ProductieStatus prod) throws ValidationException {
        controller.addSite(naam, verantwoordelijkeId, straat, nummer, postcode, gemeente, land, capaciteit, op, prod);
        reload();
    }

    public void updateSite(long id,Long verantwoordelijkeId, String naam, String straat, String nummer, String postcode, String gemeente, String land, int capaciteit, OperationeleStatus op, ProductieStatus prod) throws ValidationException {
        controller.updateSite(id, verantwoordelijkeId,naam, straat, nummer, postcode , gemeente, land, capaciteit, op, prod);
        reload();
    }

    public void deleteSite(long id) {
        controller.deleteSite(id);
        observableSiteList.removeIf(s -> s.siteId() == id);
        reload();
    }

    public void reload() {
        observableSiteList.setAll(controller.getAllSites());
    }
}


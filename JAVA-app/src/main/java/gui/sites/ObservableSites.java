package gui.sites;

import domein.controllers.SiteController;
import dto.SiteDTO;
import exception.SiteException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import lombok.Getter;
import util.OperationeleStatus;
import util.ProductieStatus;

public class ObservableSites {

    private final SiteController controller;
    private final ObservableList<SiteDTO> observableSiteList;

    @Getter
    private final FilteredList<SiteDTO> filteredSiteList;

    public ObservableSites(SiteController controller) {
        this.controller = controller;
        this.observableSiteList = FXCollections.observableArrayList();
//        this.observableSiteList.addAll(controller.getAllSites());
        this.filteredSiteList = new FilteredList<>(observableSiteList, s -> true);
    }

    public void addSite(String naam, String straat, String nummer, String postcode, String gemeente, String land, int capaciteit, OperationeleStatus op, ProductieStatus prod) throws SiteException {
        controller.addSite(naam, straat, nummer, postcode, gemeente, land, capaciteit, op, prod);
        //observableSiteList.add(created);
        reload(); // recente data uit DB halen
    }

    public void updateSite(long id, String naam, String straat, String nummer, String postcode, String gemeente, String land, int capaciteit, OperationeleStatus op, ProductieStatus prod) throws SiteException {
        controller.updateSite(id, naam, straat, nummer, postcode , gemeente, land, capaciteit, op, prod);
        reload();
    }

    public void deleteSite(long id) {
        controller.deleteSite(id);
        observableSiteList.removeIf(s -> s.siteId() == id);
        reload(); // recente data uit DB halen
    }

    public void reload() {
        observableSiteList.setAll(controller.getAllSites());
    }

    private int indexOf(long id) {
        for (int i = 0; i < observableSiteList.size(); i++) {
            if (observableSiteList.get(i).siteId() == id) return i;
        }
        return -1;
    }
}


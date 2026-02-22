package gui;

import domein.SiteController;
import dto.SiteDTO;
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
        this.observableSiteList.addAll(controller.getAllSites());
        this.filteredSiteList = new FilteredList<>(observableSiteList, s -> true);
    }

    public void addSite(String naam, String locatie, int capaciteit, OperationeleStatus op, ProductieStatus prod) {
        SiteDTO created = controller.addSite(naam, locatie, capaciteit, op, prod);
        observableSiteList.add(created);
    }

    public SiteDTO editSite(long id, String naam, String locatie, int capaciteit, OperationeleStatus op, ProductieStatus prod) {
        SiteDTO updated = controller.updateSite(id, naam, locatie, capaciteit, op, prod);

        int idx = indexOf(id);
        if (idx >= 0) observableSiteList.set(idx, updated);

        return updated;
    }

    public void deleteSite(long id) {
        controller.deleteSite(id);
        observableSiteList.removeIf(s -> s.siteId() == id);
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


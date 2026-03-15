package gui.sites;

import dto.SiteDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import lombok.Getter;

import java.util.List;

public class ObservableSites {

    private final ObservableList<SiteDTO> observableSiteList;
    @Getter private final FilteredList<SiteDTO> filteredSiteList;

    public ObservableSites() {
        this.observableSiteList = FXCollections.observableArrayList();
        this.filteredSiteList = new FilteredList<>(observableSiteList, s -> true);
    }

    public void setSites(List<SiteDTO> sites) {
        observableSiteList.setAll(sites);
    }

    public void removeSite(long id) {
        observableSiteList.removeIf(s -> s.siteId() == id);
    }
}

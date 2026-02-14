package domein;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Site {

	public enum ProductieStatus{
		GEZOND, // productie loopt normaal
		PROBLEMEN, // productie draait maar er zijn storingen
		OFFLINE // productie ligt volledig stil (technisch defect, stroomuitval,..)
	}

	public enum OperationeleStatus {
		ACTIEF, // site is open
		NON_ACTIEF // site ligt stil (bewust, seizoenstop, geen productie gepland,..)
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long siteId;

	//private Collection<Machine> machines; // TODO later -> als machines klasse bestaat
	private String naam;
	private String locatie; // TODO aparte klasse? momenteel miss wat overkill..
	// Miss wel handig als ge op termijn wilt sites per land filteren? maar nu nog niet echt nodig
	private int capaciteit;
	@Enumerated(EnumType.STRING)
	private ProductieStatus productieStatus;
	@Enumerated(EnumType.STRING)
	private OperationeleStatus operationeleStatus;

	public Site (String naam, String locatie, int capaciteit, OperationeleStatus operationeleStatus,ProductieStatus productieStatus){
		setNaam(naam);
		setLocatie(locatie);
		setCapaciteit(capaciteit);
		setOperationeleStatus(operationeleStatus);
		setProductieStatus(productieStatus);
	}

	public void setNaam(String naam) {
		if (naam == null || naam.isBlank())
			throw new IllegalArgumentException("Naam is verplicht.");
		this.naam = naam;
	}

	public void setLocatie(String locatie) {
		if (locatie == null || locatie.isBlank())
			throw new IllegalArgumentException("Locatie is verplicht.");
		this.locatie = locatie;
	}

	public void setCapaciteit(int capaciteit) {
		if (capaciteit <= 0) {
			throw new IllegalArgumentException("Capaciteit moet groter zijn dan 0.");
		}
		this.capaciteit = capaciteit;
	}

	public void setOperationeleStatus(OperationeleStatus status) {
		if (status == null)
			throw new IllegalArgumentException("Operationele status is verplicht.");

		this.operationeleStatus = status;

		if (status == OperationeleStatus.NON_ACTIEF) {
			this.productieStatus = ProductieStatus.OFFLINE;
		}
	}

	public void setProductieStatus(ProductieStatus status) {
		if (status == null)
			throw new IllegalArgumentException("Productiestatus is verplicht.");

		if (operationeleStatus == OperationeleStatus.NON_ACTIEF && status != ProductieStatus.OFFLINE) {
			throw new IllegalArgumentException("NON-ACTIEF vereist productiestatus OFFLINE.");
		}
		this.productieStatus = status;
	}
}
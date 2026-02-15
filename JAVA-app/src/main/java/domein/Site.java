package domein;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

	private Site(Builder builder){
		this.naam = builder.naam;
		this.locatie = builder.locatie;
		this.capaciteit = builder.capaciteit;
		this.operationeleStatus = builder.operationeleStatus;
		this.productieStatus = builder.productieStatus;
	}

	public static Builder builder(){
		return new Builder();
	}

	private static void validate(String naam, String locatie, int capaciteit, OperationeleStatus op, ProductieStatus prod) {

		if (naam == null || naam.isBlank()) throw new IllegalArgumentException("Naam is verplicht.");
		if (locatie == null || locatie.isBlank()) throw new IllegalArgumentException("Locatie is verplicht.");
		if (capaciteit <= 0) throw new IllegalArgumentException("Capaciteit moet groter zijn dan 0.");
		if (op == null) throw new IllegalArgumentException("Operationele status is verplicht.");
		if (prod == null) throw new IllegalArgumentException("Productiestatus is verplicht.");

		if (op == OperationeleStatus.NON_ACTIEF && prod != ProductieStatus.OFFLINE) {
			throw new IllegalArgumentException("Wanneer een site non-actief is, moet productie OFFLINE zijn.");
		}
	}

	// voor edit
	public void update(String naam, String locatie, int capaciteit, OperationeleStatus op, ProductieStatus prod){
		validate(naam,locatie,capaciteit,op,prod);

		this.naam = naam;
		this.locatie = locatie;
		this.capaciteit = capaciteit;
		this.operationeleStatus = op;
		this.productieStatus = prod;
	}


	public static class Builder {
		private String naam;
		private String locatie;
		private int capaciteit;
		private OperationeleStatus operationeleStatus;
		private ProductieStatus productieStatus;

		public Builder naam(String naam){
			this.naam = naam;
			return this;
		}

		public Builder locatie(String locatie){
			this.locatie = locatie;
			return this;
		}

		public Builder capaciteit(int capaciteit){

			this.capaciteit = capaciteit;
			return this;
		}

		public Builder operationeleStatus(OperationeleStatus status){

			this.operationeleStatus = status;


			return this;
		}

		public Builder productieStatus(ProductieStatus status){
			this.productieStatus = status;
			return this;
		}

		public Site build(){
			validate(naam,locatie,capaciteit,operationeleStatus,productieStatus);
			return new Site(this);
		}

	}
}
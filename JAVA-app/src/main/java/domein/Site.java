package domein;

import exception.SiteException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import util.OperationeleStatus;
import util.ProductieStatus;

import java.util.*;

@Entity
@NamedQueries({
		@NamedQuery(name = "Site.existsByName",
						query = """
								SELECT COUNT(s)
								FROM Site s
								WHERE LOWER(s.naam) = LOWER(:naam)
								AND (:id IS NULL OR s.siteId <> :id)
								""")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Site {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long siteId;

	//private Collection<Machine> machines; // TODO later -> als machines klasse bestaat
	@Column(unique = true)
	private String naam;

	@Embedded
	private Locatie locatie;

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


	public void update(String naam, Locatie locatie, int capaciteit,
					   OperationeleStatus op, ProductieStatus prod) throws SiteException {

		validate(naam, locatie, capaciteit, op, prod);

		this.naam = naam;
		this.locatie = locatie;
		this.capaciteit = capaciteit; // safe
		this.operationeleStatus = op;
		this.productieStatus = prod;
	}

	private static void validate(String naam, Locatie locatie, int capaciteit,
								 OperationeleStatus op, ProductieStatus prod) throws SiteException {

		Map<String, IllegalArgumentException> errors = new HashMap<>();

		if (naam == null || naam.isBlank())
			errors.put("naam", new IllegalArgumentException("Naam vereist."));

//		if (locatie == null)
//			errors.put("locatie", new IllegalArgumentException("Locatie vereist."));

		if (capaciteit <= 0)
			errors.put("capaciteit", new IllegalArgumentException("Capaciteit moet groter zijn dan 0."));

		if (op == null)
			errors.put("operationeleStatus", new IllegalArgumentException("Operationele status vereist."));

		if (prod == null)
			errors.put("productieStatus", new IllegalArgumentException("Productiestatus vereist."));

		if (op != null && prod != null) {
			if (op == OperationeleStatus.NON_ACTIEF && prod != ProductieStatus.OFFLINE) {
				errors.put("productieStatus", new IllegalArgumentException(
						"Wanneer een site non-actief is, moet productie OFFLINE zijn."
				));
			}
		}

		if (!errors.isEmpty())
			throw new SiteException(errors);
	}


	public static class Builder {
		private String naam;
		private Locatie locatie;
		private int capaciteit;
		private OperationeleStatus operationeleStatus;
		private ProductieStatus productieStatus;

		public Builder naam(String naam){
			this.naam = naam;
			return this;
		}

		public Builder locatie(Locatie locatie){
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

		public Site build() throws SiteException {
			validate(naam, locatie, capaciteit, operationeleStatus, productieStatus);
			return new Site(this);
		}

	}
}
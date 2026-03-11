package domein.entiteiten;

import exception.ValidationException;
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
								AND (:id IS NULL OR s.id <> :id)
								""")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(of = "naam")
public class Site {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(unique = true)
	private String naam;

	@Embedded
	private Locatie locatie;

	private int capaciteit;
	@Enumerated(EnumType.STRING)
	private ProductieStatus productieStatus;
	@Enumerated(EnumType.STRING)
	private OperationeleStatus operationeleStatus;

	@ManyToOne
	private Gebruiker verantwoordelijke;

	// een team hoort tot 1 site
	@OneToOne(mappedBy = "site")
	private Team team;

	private Site(Builder builder){
		this.naam = builder.naam;
		this.verantwoordelijke = builder.verantwoordelijke;
		this.locatie = builder.locatie;
		this.capaciteit = builder.capaciteit;
		this.operationeleStatus = builder.operationeleStatus;
		this.productieStatus = builder.productieStatus;
	}

	public static Builder builder(){
		return new Builder();
	}


	public void update(String naam, Gebruiker verantwoordelijke, Locatie locatie, int capaciteit,
					   OperationeleStatus op, ProductieStatus prod) throws ValidationException {

		validate(naam,verantwoordelijke, locatie, capaciteit, op, prod);

		this.naam = naam;
		this.verantwoordelijke = verantwoordelijke;
		this.locatie = locatie;
		this.capaciteit = capaciteit;
		this.operationeleStatus = op;
		this.productieStatus = prod;
	}

	private static void validate(String naam, Gebruiker verantwoordelijke, Locatie locatie, int capaciteit,
								 OperationeleStatus op, ProductieStatus prod) throws ValidationException {

		Map<String, IllegalArgumentException> errors = new HashMap<>();

		if (naam == null || naam.isBlank())
			errors.put("naam", new IllegalArgumentException("Naam vereist."));

		if (verantwoordelijke != null && verantwoordelijke.getRol() != util.Rollen.VERANTWOORDELIJKE) {
			errors.put("verantwoordelijke", new IllegalArgumentException("Gebruiker moet de rol VERANTWOORDELIJKE hebben."));
		}
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
			throw new ValidationException(errors);
	}


	public static class Builder {
		private String naam;
		private Gebruiker verantwoordelijke;
		private Locatie locatie;
		private int capaciteit;
		private OperationeleStatus operationeleStatus;
		private ProductieStatus productieStatus;

		public Builder naam(String naam){
			this.naam = naam;
			return this;
		}

		public Builder verantwoordelijke(Gebruiker verantwoordelijke) {
			this.verantwoordelijke = verantwoordelijke;
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

		public Site build() throws ValidationException {
			validate(naam, verantwoordelijke, locatie, capaciteit, operationeleStatus, productieStatus);
			return new Site(this);
		}

	}
}
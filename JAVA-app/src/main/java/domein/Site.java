package domein;

import java.util.*;

//TODO link met database
public class Site {

	public enum ProductieStatus{
		GEZOND,PROBLEMEN,OFFLINE
	}

	public enum OperationeleStatus {
		ACTIEF,
		NON_ACTIEF
	}

	//private Collection<Machine> machines;
	private String naam;
	private String locatie;
	private int capaciteit;

	//TODO setter bij het wijzigen van een site
	// ik vermoed dat OFFLINE altijd zorgt voor -> NON_ACTIEF?
	private boolean operationeleStatus;
	private ProductieStatus status;

	//TODO constructor (zorgen dat niet tegelijk OFFLINE en ACTIEF KAN ZIJN

}
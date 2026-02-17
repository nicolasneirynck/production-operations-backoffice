package domein;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import util.TaakType;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Taak {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long taakId;
    @Enumerated(EnumType.STRING)
    private TaakType taakType;
    private String omschrijving;
    private int duurtijd;

    public Taak(TaakType type, String omschrijving, int duurtijd) {
        setTaakType(type);
        setOmschrijving(omschrijving);
        setDuurtijd(duurtijd);
    }

    private void setTaakType(TaakType type) {
        if (type == null)
            throw new IllegalArgumentException("Type is verplicht");
        this.taakType = type;
    }

    private void setOmschrijving(String omschrijving) {
        if (omschrijving == null || omschrijving.isBlank())
            throw new IllegalArgumentException("Omschrijving is verplicht");
        this.omschrijving = omschrijving;
    }

    private void setDuurtijd(int minuten) {
        if (minuten <= 0 || minuten > 240 || minuten % 15 != 0)
            throw new IllegalArgumentException("Duurtijd moet in blokken van 15 min zijn en max 4 uur.");
        this.duurtijd = minuten;
    }

    public void update(TaakType type, String omschrijving, int duurtijd){
        setTaakType(type);
        setOmschrijving(omschrijving);
        setDuurtijd(duurtijd);
    }
}

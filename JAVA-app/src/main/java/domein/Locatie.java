package domein;

import exception.SiteException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode
public class Locatie {

    private String straat;
    private String nummer;
    private String postcode;
    private String gemeente;
    private String land;

    private Locatie(String straat, String nummer, String postcode, String gemeente, String land) {
        this.straat = straat;
        this.nummer = nummer;
        this.postcode = postcode;
        this.gemeente = gemeente;
        this.land = land;
    }

    public static Locatie builder(String straat, String nummer, String postcode, String gemeente, String land) throws SiteException {
        validate(straat, nummer, postcode, gemeente, land);
        return new Locatie(straat, nummer, postcode, gemeente, land);
    }

    @Override
    public String toString() {
        return "%s %s, %s %s, %s".formatted(straat, nummer, postcode, gemeente, land);
    }

    private static void validate(String straat, String nummer, String postcode, String gemeente, String land) throws SiteException {
        Map<String, IllegalArgumentException> errors = new HashMap<>();

        if (straat == null || straat.isBlank())
            errors.put("locatie.straat", new IllegalArgumentException("Straat vereist."));

        if (nummer == null || nummer.isBlank())
            errors.put("locatie.nummer", new IllegalArgumentException("Nummer vereist."));

        if (postcode == null || postcode.isBlank())
            errors.put("locatie.postcode", new IllegalArgumentException("Postcode vereist."));

        if (gemeente == null || gemeente.isBlank())
            errors.put("locatie.gemeente", new IllegalArgumentException("Gemeente vereist."));

        if (land == null || land.isBlank())
            errors.put("locatie.land", new IllegalArgumentException("Land vereist."));

        if (!errors.isEmpty())
            throw new SiteException(errors);
    }
}
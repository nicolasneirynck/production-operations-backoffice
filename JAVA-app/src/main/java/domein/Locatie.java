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
    private String stad;
    private String land;

    private Locatie(String straat, String nummer, String postcode, String stad, String land) {
        this.straat = straat;
        this.nummer = nummer;
        this.postcode = postcode;
        this.stad = stad;
        this.land = land;
    }

    public static Locatie builder(String straat, String nummer, String postcode, String stad, String land) throws SiteException {
        validate(straat, nummer, postcode, stad, land);
        return new Locatie(straat, nummer, postcode, stad, land);
    }

    @Override
    public String toString() {
        return "%s %s, %s %s, %s".formatted(straat, nummer, postcode, stad, land);
    }

    private static void validate(String straat, String nummer, String postcode, String stad, String land) throws SiteException {
        Map<String, IllegalArgumentException> errors = new HashMap<>();

        if (straat == null || straat.isBlank())
            errors.put("locatie.straat", new IllegalArgumentException("Straat vereist."));

        if (nummer == null || nummer.isBlank())
            errors.put("locatie.nummer", new IllegalArgumentException("Nummer vereist."));

        if (postcode == null || postcode.isBlank())
            errors.put("locatie.postcode", new IllegalArgumentException("Postcode vereist."));

        if (stad == null || stad.isBlank())
            errors.put("locatie.stad", new IllegalArgumentException("Stad vereist."));

        if (land == null || land.isBlank())
            errors.put("locatie.land", new IllegalArgumentException("Land vereist."));

        if (!errors.isEmpty())
            throw new SiteException(errors);
    }
}
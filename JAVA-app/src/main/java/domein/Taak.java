package domein;

import exception.TaakException;
import jakarta.persistence.*;
import lombok.*;
import util.OperationeleStatus;
import util.ProductieStatus;
import util.TaakType;

import java.util.HashMap;
import java.util.Map;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Taak {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long taakId;
    @Setter(AccessLevel.PROTECTED)
    private String taakType;
    @Setter(AccessLevel.PROTECTED)
    private String omschrijving;
    @Setter(AccessLevel.PROTECTED)
    private int duurtijd;

    private Taak(Builder builder){
        this.taakType = builder.type.toUpperCase();
        this.omschrijving = builder.omschrijving;
        this.duurtijd = builder.duurtijd;
    }

    public static Builder builder(){
        return new Builder();
    }

    public void update(String type, String omschrijving, int duurtijd) throws TaakException {
        validate(type, omschrijving, duurtijd);

        this.taakType = type.toUpperCase();
        this.omschrijving = omschrijving;
        this.duurtijd = duurtijd;
    }

    private static void validate(String type, String omschrijving, int duurtijd) throws TaakException {
        Map<String, IllegalArgumentException> errors = new HashMap<>();

        if (type == null || type.isBlank()) errors.put("taakType", new IllegalArgumentException("Type is vereist"));
        if (omschrijving == null || omschrijving.isBlank())
            errors.put("omschrijving", new IllegalArgumentException("Omschrijving is vereist"));

//        if (duurtijd == null) {
//            errors.put("duurtijd", new IllegalArgumentException("Duurtijd is verplicht"));
        if (duurtijd <= 0) {
            errors.put("duurtijd", new IllegalArgumentException("Duurtijd moet groter zijn dan 0"));
        } else if (duurtijd > 240 || duurtijd % 15 != 0) {
            errors.put("duurtijd", new IllegalArgumentException(
                    "Duurtijd moet in blokken van 15 min zijn en max 4 uur."
            ));
        }

        if (!errors.isEmpty())
            throw new TaakException(errors);
    }

    public static class Builder {
        private String type;
        private String omschrijving;
        private Integer duurtijd;

        public Builder type(String type){
            this.type = type;
            return this;
        }

        public Builder omschrijving(String omschrijving){
            this.omschrijving = omschrijving;
            return this;
        }

        public Builder duurtijd(Integer minuten){
            this.duurtijd = minuten;
            return this;
        }

        public Taak build() throws TaakException {
//            Map<String,IllegalArgumentException> errors = new HashMap<>();
//
//            if (type == null)
//                errors.put("taakType",new IllegalArgumentException("Type is verplicht"));
//
//            if (omschrijving == null || omschrijving.isBlank())
//                errors.put("omschrijving",new IllegalArgumentException("Omschrijving is verplicht"));
//
//            if (duurtijd == 0){
//                errors.put("duurtijd",new IllegalArgumentException("Duurtijd is verplicht"));
//            } else if (duurtijd <= 0) {
//                errors.put("duurtijd", new IllegalArgumentException("Duurtijd moet groter zijn dan 0"));
//            } else if (duurtijd > 240 || duurtijd % 15 != 0) {
//                errors.put("duurtijd", new IllegalArgumentException(
//                        "Duurtijd moet in blokken van 15 min zijn en max 4 uur."
//                ));
//            }
//
//            if (!errors.isEmpty())
//                throw new TaakException(errors);

            validate(type,omschrijving,duurtijd);

            return new Taak(this);
        }

    }
}

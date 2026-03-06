package dto;

public record LocatieDTO(
        String straat,
        String nummer,
        String postcode,
        String stad,
        String land
) {

    public String volledigeLocatie() {
        return straat + " " + nummer + ", " + postcode + " " + stad + ", " + land;
    }

}
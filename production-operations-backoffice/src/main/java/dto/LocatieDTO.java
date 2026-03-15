package dto;

public record LocatieDTO(
        String straat,
        String nummer,
        String postcode,
        String gemeente,
        String land
) {

    public String volledigeLocatie() {
        return straat + " " + nummer + ", " + postcode + " " + gemeente + ", " + land;
    }

}
package ch.epfl.javions.aircraft;

import java.util.Objects;


/**
 * L'enregistrement AircraftData collecte les données fixes d'un aéronef.
 * @param registration immatriculation de l'aéronef
 * @param typeDesignator l'indicateur de type d'un aéronef
 * @param model model de l'aéronef
 * @param description description de l'aéronef
 * @param wakeTurbulenceCategory la catégorie de turbulence de sillage d'un aéronef
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public record AircraftData(AircraftRegistration registration,
                           AircraftTypeDesignator typeDesignator,
                           String model,
                           AircraftDescription description,
                           WakeTurbulenceCategory wakeTurbulenceCategory) {
/**
 * Constructeur compact vérifiant que ses attributs ne sont pas nuls.
 */
    public AircraftData{
        Objects.requireNonNull(registration);
        Objects.requireNonNull(typeDesignator);
        Objects.requireNonNull(model);
        Objects.requireNonNull(description);
        Objects.requireNonNull(wakeTurbulenceCategory);
    }
}

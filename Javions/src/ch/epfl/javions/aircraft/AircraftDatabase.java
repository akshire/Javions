package ch.epfl.javions.aircraft;

import java.io.*;
import java.util.Objects;
import java.util.zip.ZipFile;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * La classe AircraftDatabase représente la base de données mictronics des aéronefs.
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */


public final class AircraftDatabase {

    private final String fileName;

    /**
     * Constructeur qui retourne un objet représentant la base de données mictronics,
     * stockée dans le fichier de nom donné, ou lève NullPointerException si celui-ci est nul
     * @param fileName nom du fichier.
     */
    public AircraftDatabase(String fileName){
        Objects.requireNonNull(fileName);
        this.fileName = fileName;
    }

    /**
     *
     * @param address adresse international civil aviation organization de l'aéronef.
     * @return AircraftData (les données de l'aéronef).
     * @throws IOException si la méthode relève un problème avec le flot.
     */
    public AircraftData get(IcaoAddress address) throws IOException {

        int length = address.string().length();
        //fichier avec le nom des deux derniers caractères de l'adresse international civil aviation organization
        String extractLetters = address.string().substring(length - 2, length);

        try (ZipFile zipFile = new ZipFile(fileName);
             InputStream stream = zipFile.getInputStream(zipFile.getEntry(extractLetters + ".csv"));
             Reader reader = new InputStreamReader(stream, UTF_8);
             BufferedReader bufferedReader = new BufferedReader(reader)) {

            //recherche de la ligne
            String line = bufferedReader.readLine();

            while (line != null && !line.startsWith(address.string()) && (address.string().compareTo(line) > 0)) {
                line = bufferedReader.readLine();
                //si on arrive a la fin du fichier
                if(line == null){return null;}
            }

            //si c'est la bonne expression
            if (line != null && line.startsWith(address.string())) {
                //split les colones de la ligne autour des ","

                String[] columns = line.split(",", -1);
                return new AircraftData(new AircraftRegistration(columns[1]),
                                        new AircraftTypeDesignator(columns[2]),columns[3],
                                        new AircraftDescription(columns[4]),
                                        WakeTurbulenceCategory.of(columns[5]));

            } else {
                return null;
            }
        }
    }
}

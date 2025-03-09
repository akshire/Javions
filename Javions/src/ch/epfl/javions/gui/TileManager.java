package ch.epfl.javions.gui;



import ch.epfl.javions.Preconditions;
import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * La classe TileManager représente un gestionnaire de tuiles OSM
 *
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public final class TileManager {
    private final Path path;
    private final String serverName;

    private final Map<TileId,Image> memoryCache;

    /**
     * Constructeur
     *
     * @param path le chemin d'accès au dossier contenant le cache disque
     * @param serverName le nom du serveur de tuile
     */
    public TileManager(Path path, String serverName){
        this.path = path;
        this.serverName = serverName;
        memoryCache = new LinkedHashMap<>(100,0.75f,true);

    }

    /**
     * Méthode retournant la tuile correspondant à l'ID donné
     *
     * @param tileId l'identité d'une tuile OSM
     * @return l'image associée à l'index de la tuile.
     * @throws IOException si il y a un problème avec le flot
     */
    public Image imageForTileAt(TileId tileId) throws IOException{
        if(memoryCache.values().size()==100){
            memoryCache.remove(memoryCache.keySet().iterator().next());
        }


        if(memoryCache.containsKey(tileId)){
            return memoryCache.get(tileId);
        }

        //chemin du dossier dans lequel l'image est contenue
        Path toDoc = path.resolve(Integer.toString(tileId.zoomLevel())).resolve(Integer.toString(tileId.indexX));
        //chemin jusqu'au fichier de l'image
        Path toFile = toDoc.resolve(tileId.indexY+".png");

        if(Files.exists(toFile)) {

            try (InputStream stream = new FileInputStream(toFile.toString())) {
                memoryCache.put(tileId,new Image(stream));
            }
            return memoryCache.get(tileId);
        }

        URL u = new URL("https://"+serverName+"/"+tileId.zoomLevel+"/"+tileId.indexX+"/"+tileId.indexY+".png");
        URLConnection c = u.openConnection();
        c.setRequestProperty("User-Agent", "Javions");
        byte[] bytes;
        try(InputStream stream = c.getInputStream()){
            bytes = stream.readAllBytes();
        }

        Files.createDirectories(toDoc);

        try(OutputStream stream = new FileOutputStream(toFile.toString())){
            stream.write(bytes);
        }

        try(InputStream stream = new ByteArrayInputStream(bytes)) {
            memoryCache.put(tileId,new Image(stream));
        }

        return memoryCache.get(tileId);
    }


    /**
     * L'enregistrement TileId représente l'identité d'une tuile OSM
     *
     * @param zoomLevel niveau de zoom
     * @param indexX index X de la tuile
     * @param indexY index Y de la tuile
     */
    record TileId(int zoomLevel, int indexX, int indexY){

        /**
         * Check la validité d'un index de tuile
         *
         * @param zoomLevel niveau de zoom
         * @param indexX index X de la tuile
         * @param indexY index Y de la tuile
         * @return vrai si c'est un index de tuile valide
         */
        public static boolean isValid(int zoomLevel, int indexX, int indexY){
            Preconditions.checkArgument(6 <= zoomLevel && zoomLevel <= 19);
            return (0 <= indexX && indexX < Math.pow(2,zoomLevel)) && (0 <= indexY && indexY < Math.pow(2,zoomLevel)) ;
        }
    }
}

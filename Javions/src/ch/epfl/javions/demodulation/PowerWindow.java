package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;


import java.io.IOException;
import java.io.InputStream;

/**
 * La classe PowerWindow représente une fenêtre de taille fixe
 * sur une séquence d'échantillons de puissance produits par un
 * calculateur de puissance.
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public final class PowerWindow {
    private final static int MAX_WINDOW_SIZE = 1<<16;
    private final PowerComputer pwComputer;
    private final int[] powerSamplesOdd;
    private final int[] powerSamplesEven;
    private final int windowSize;
    private long position;
    private long numberOfPowers;
    private boolean replaceEvenArray;



    /**
     * Constructeur
     *
     * @param stream flot entrant
     * @param windowSize taille de la fenêtre.
     * @throws IOException s'il y a un problème avec le flot
     * @throws IllegalArgumentException si la windowSize est plus petite ou égale à 0 ou si elle est plus grande que
     *                                  WindowMaxSize
     */

    public PowerWindow(InputStream stream, int windowSize) throws IOException {
        Preconditions.checkArgument(( windowSize > 0 ) && ( windowSize <= MAX_WINDOW_SIZE)) ;
        this.windowSize = windowSize;
        this.powerSamplesOdd = new int[MAX_WINDOW_SIZE];
        this.powerSamplesEven = new int[MAX_WINDOW_SIZE];
        pwComputer = new PowerComputer(stream, MAX_WINDOW_SIZE);


        numberOfPowers = pwComputer.readBatch(powerSamplesOdd);
        this.position = 0;
        //true quand l'index est pair, false quand l'index du lot est impair
        this.replaceEvenArray = true;

    }


    /**
     *
     * @return la taille de la fenêtre
     */
    public int size(){
        return windowSize;
    }

    /**
     * @return la position dans le fichier d'échantillons.
     */
    public long position(){
        return position;
    }

    /**
     * @return true si la fenêtre se situe sur un ensemble d'échantillons valide.
     */
    public boolean isFull() {return numberOfPowers != 0 && !((position + windowSize) % numberOfPowers == 1);}

    /**
     *
     * @param i index de l'élément souhaité dans la fenêtre.
     * @return l'élément de la fenêtre à l'index donné.
     * @throws IndexOutOfBoundsException si i n'est pas inclu dans les bornes
     */
    public int get(int i){
        if (!(i >= 0 && i < windowSize)) {
            throw new IndexOutOfBoundsException();
        }
        if (((position % MAX_WINDOW_SIZE) + windowSize) > MAX_WINDOW_SIZE) {
            //si on se trouve dans le deuxième tableau
            if ((position % MAX_WINDOW_SIZE) + i > (MAX_WINDOW_SIZE - 1)) {

                return replaceEvenArray ?
                        powerSamplesOdd[(int)((position + i) % MAX_WINDOW_SIZE)] :
                        powerSamplesEven[(int)((position + i) % MAX_WINDOW_SIZE)];
            }
            //si on se trouve dans le premier tableau
            else {
                return replaceEvenArray ?
                        powerSamplesEven[(int)((position % MAX_WINDOW_SIZE) + i)] :
                        powerSamplesOdd[(int)((position % MAX_WINDOW_SIZE) + i)];
            }
        } else {
            return replaceEvenArray ?
                    powerSamplesOdd[(int)((position % MAX_WINDOW_SIZE) + i)] :
                    powerSamplesEven[(int)((position % MAX_WINDOW_SIZE) + i)];
        }
    }

    /**
     * Cette méthode fait avancer la fenêtre d'un échantillon.
     * @throws IOException s'il y a un problème avec le flot
     */
    public void advance() throws IOException{
        position += 1;
        if ((position + windowSize) % MAX_WINDOW_SIZE == 1) {
            if (replaceEvenArray) {
                numberOfPowers += pwComputer.readBatch(powerSamplesEven);
                replaceEvenArray = false;

            } else {
                numberOfPowers += pwComputer.readBatch(powerSamplesOdd);
                replaceEvenArray = true;

            }
        }
    }


    /**
     *
     * @param offset nombre d'avancements souhaité.
     * @throws IOException si il y a un problème avec le flot.
     * @throws IllegalArgumentException si le nombre de pas n'est pas strictement positif
     */
    public void advanceBy(int offset) throws IOException {
        Preconditions.checkArgument(offset >= 0);
        for (int i = 0; i < offset; i++) this.advance();
    }
}

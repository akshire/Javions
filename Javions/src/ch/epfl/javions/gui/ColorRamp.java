package ch.epfl.javions.gui;

import ch.epfl.javions.Math2;
import ch.epfl.javions.Preconditions;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * La classe ColorRamp représente un dégradé de couleurs
 *
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public final class ColorRamp {
    private final List<Color> gradient;
    private final double step;
    public static final ColorRamp PLASMA = new ColorRamp(
            Color.valueOf("0x0d0887ff"), Color.valueOf("0x220690ff"),
            Color.valueOf("0x320597ff"), Color.valueOf("0x40049dff"),
            Color.valueOf("0x4e02a2ff"), Color.valueOf("0x5b01a5ff"),
            Color.valueOf("0x6800a8ff"), Color.valueOf("0x7501a8ff"),
            Color.valueOf("0x8104a7ff"), Color.valueOf("0x8d0ba5ff"),
            Color.valueOf("0x9814a0ff"), Color.valueOf("0xa31d9aff"),
            Color.valueOf("0xad2693ff"), Color.valueOf("0xb6308bff"),
            Color.valueOf("0xbf3984ff"), Color.valueOf("0xc7427cff"),
            Color.valueOf("0xcf4c74ff"), Color.valueOf("0xd6556dff"),
            Color.valueOf("0xdd5e66ff"), Color.valueOf("0xe3685fff"),
            Color.valueOf("0xe97258ff"), Color.valueOf("0xee7c51ff"),
            Color.valueOf("0xf3874aff"), Color.valueOf("0xf79243ff"),
            Color.valueOf("0xfa9d3bff"), Color.valueOf("0xfca935ff"),
            Color.valueOf("0xfdb52eff"), Color.valueOf("0xfdc229ff"),
            Color.valueOf("0xfccf25ff"), Color.valueOf("0xf9dd24ff"),
            Color.valueOf("0xf5eb27ff"), Color.valueOf("0xf0f921ff"));

    /**
     * Constructeur
     *
     * @param colors couleurs dans le dégradé
     */
    public ColorRamp(Color...colors){
        Preconditions.checkArgument(colors.length >= 2);
        this.gradient = List.of(colors);
        this.step = 1d / (gradient.size()-1d);

    }

    /**
     * Constructeur
     *
     * @param gradient dégradé
     */
    public ColorRamp(List<Color> gradient){
        Preconditions.checkArgument(gradient.size() >= 2);
        this.gradient = gradient;
        this.step =  1d / (gradient.size() - 1d);
    }


    /**
     * Méthode servant à determiner un mélange de couleur dans la liste de couleurs
     *
     * @param colorIndex index de la couleur
     * @return un mélange de couleur selon l'index où on se situe
     */
    public Color at(double colorIndex){
        double colorIndexClamp = Math2.clamp(0,colorIndex,1);
        if(colorIndexClamp == 1) return gradient.get(gradient.size() - 1);
        else{
            int firstColorIndex = (int) (colorIndexClamp / step);
            double percentageOfOtherColor = 1d - ((colorIndex % step) / step);
            return gradient.get(firstColorIndex).interpolate(gradient.get(firstColorIndex + 1), percentageOfOtherColor);
        }

    }
}

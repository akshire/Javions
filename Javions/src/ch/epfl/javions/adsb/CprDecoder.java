package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;


import static java.lang.Math.PI;

/**
 * La classe CprDecoder représente un décodeur de position CPR.
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public final class CprDecoder {
    private static final double Z_PHI_IMPAIR = 59;
    private static final double Z_PHI_PAIR = 60;

    //Unités des deltas exprimées en tours
    private static final double DELTA_PHI_UN = 1/ Z_PHI_IMPAIR;
    private static final double DELTA_PHI_ZERO = 1 / Z_PHI_PAIR;


    private CprDecoder(){}

    /**
     *
     * @param x0 la longitude locale normalisée d'un message pair
     * @param y0 la latitude locale normalisée d'un message pair
     * @param x1 la longitude locale normalisée d'un message impair
     * @param y1 la latitude locale normalisée d'un message impair
     * @param plusRecent sachant que les positions les plus récentes sont celles d'index (0 ou 1)
     * @return un GeoPos ou null si la latitude de la position décodée n'est pas valide.
     */
    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int plusRecent){
        Preconditions.checkArgument(plusRecent == 0 || plusRecent == 1);

        double zPhi = Math.rint(y0 * Z_PHI_IMPAIR - y1 * Z_PHI_PAIR);
        double zPhiUn = zPhi < 0 ? zPhi + Z_PHI_IMPAIR : zPhi;
        double zPhiZero = zPhi < 0 ? zPhi + Z_PHI_PAIR : zPhi;


        double phiUn = DELTA_PHI_UN * (zPhiUn + y1);
        double phiZero = DELTA_PHI_ZERO * (zPhiZero + y0);

        //défini A0 et A1 pour pouvoir comparer les nombres de zones de longitude
        double A0 = Math.acos(1 -
                (1 - Math.cos(2d * PI * DELTA_PHI_ZERO)) /
                        Math.pow(Math.cos(Units.convertFrom(phiZero,Units.Angle.TURN)), 2)
        );
        double A1 = Math.acos(1d -
                (1d - Math.cos(2d * PI * DELTA_PHI_ZERO)) /
                        Math.pow(Math.cos(Units.convertFrom(phiUn,Units.Angle.TURN)),2)
        );

        //si ce n'est pas un nombre alors, on remplace par 1.
        double bigZLambdaZero = Double.isNaN(A0) ? 1 : Math.floor((2d * PI / A0));
        double bigZLambdaOne = Double.isNaN(A1) ? 1 : Math.floor((2d * PI / A1));

        //comparaison
        if(bigZLambdaOne!= bigZLambdaZero){return null;}
        bigZLambdaOne = bigZLambdaZero - 1;


        double zLambda = Math.rint(x0 * bigZLambdaOne - x1 * bigZLambdaZero);


        double zLambdaUn = zLambda < 0 ? zLambda + bigZLambdaOne : zLambda;
        double zLambdaZero = zLambda < 0 ? zLambda + bigZLambdaZero : zLambda;


        double lambdaZero = bigZLambdaZero == 1 ? x0 : (1/bigZLambdaZero) * (zLambdaZero + x0);
        double lambdaUn = bigZLambdaZero == 1 ? x1 : (1/bigZLambdaOne) * (zLambdaUn + x1);


        //recentrage des angles autour de 0.
        int lambdaFinal = CprDecoder.centredT32(plusRecent == 1 ? lambdaUn : lambdaZero);
        int phiFinal = CprDecoder.centredT32(plusRecent == 1 ? phiUn : phiZero);


        //vérification
        return GeoPos.isValidLatitudeT32(phiFinal) ?  new GeoPos(lambdaFinal,phiFinal) : null;

    }

    /**
     * Méthode privée servant à recentrer les angles
     * @param valeur de l'angle en turn
     * @return valeur recentrée autour de 0 et en T32
     */
    private static int centredT32(double valeur){
        double centring = valeur >= 0.5 ? valeur - 1 : valeur;
        //conversion en T32
        return (int) Math.rint(Units.convert(centring,
                Units.Angle.TURN,
                Units.Angle.T32));

    }
}

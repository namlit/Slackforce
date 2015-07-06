package slacklib;

/**
 * Created by tilman on 06.07.15.
 */
public class StretchPoint {

    private double mForce;
    private double mStretch;

    public StretchPoint(double force, double stretch) {
        mForce = force;
        mStretch = stretch;
    }
    public double getForce() {
        return mForce;
    }
    public double getStretch() {
        return mStretch;
    }
}
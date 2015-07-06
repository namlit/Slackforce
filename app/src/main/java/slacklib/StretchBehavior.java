package slacklib;

/**
 * Created by tilman on 06.07.15.
 */
public class StretchBehavior {
    private StretchPoint mStretchPoints[];

    public StretchBehavior(StretchPoint... stretchPoints)
    {
        mStretchPoints = new StretchPoint[stretchPoints.length + 1];
        mStretchPoints[0] = new StretchPoint(0, 0);

        for(int i = 0; i < stretchPoints.length; i++)
        {
            mStretchPoints[i+1] = stretchPoints[i];
        }
    }

    public StretchBehavior(double forcePoints[], double stretchPoints[])
    {
        if (forcePoints.length != stretchPoints.length)
            return;

        mStretchPoints = new StretchPoint[forcePoints.length + 1];
        mStretchPoints[0] = new StretchPoint(0, 0);
        for (int i = 0; i < forcePoints.length; i++) {
            mStretchPoints[i+1] = new StretchPoint(forcePoints[i], stretchPoints[i]);
        }
    }

    public double getForce(double knownForce, double relativeChangeInLength)
    {
        double deltaL0 = getStretch(knownForce);
        return getForce(deltaL0 + relativeChangeInLength);
    }

    public double getForce(double stretch) {

        if (stretch <= 0)
            return 0;
        if (mStretchPoints.length <= 1)
            return 0;

        int i = 0;
        while (mStretchPoints[i].getStretch() < stretch) {

            i++;
            if (i >= mStretchPoints.length-1)
                break;
        }

        double x1 = mStretchPoints[i-1].getStretch();
        double x2 = mStretchPoints[i].getStretch();
        double y1 = mStretchPoints[i-1].getForce();
        double y2 = mStretchPoints[i].getForce();

        return (y2-y1) / (x2 - x1) * (stretch - x1) + y1;
    }

    public double getStretch(double force) {
        if (force <= 0)
            return 0;
        if (mStretchPoints.length <= 1)
            return 0;

        int i = 0;
        while (mStretchPoints[i].getForce() < force) {

            i++;
            if (i >= mStretchPoints.length-1)
                break;
        }

        double x1 = mStretchPoints[i-1].getForce();
        double x2 = mStretchPoints[i].getForce();
        double y1 = mStretchPoints[i-1].getStretch();
        double y2 = mStretchPoints[i].getStretch();

        return (y2-y1) / (x2 - x1) * (force - x1) + y1;
    }

}
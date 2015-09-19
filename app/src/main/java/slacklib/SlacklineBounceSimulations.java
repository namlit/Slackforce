package slacklib;

import java.io.*;
import static java.lang.System.out;

public class SlacklineBounceSimulations {
	private final double gravityAcceleration = 9.81;

	private SlacklineCalculations mSlackCalc;
    private Webbing mWebbing;
	private double mSpeedOfSlackliner; // direction downwards
	private double mVerticalPositionOfSlackliner; // corresponds to -sag of slackline for positive values
	private double mDeltaT = 0.0001;

    private final SagDerivative mSagDerivative = new SagDerivative();
    private final SpeedDerivative mSpeedDerivative = new SpeedDerivative();
    private final AccelerationDerivative mAccelerationDerivative = new AccelerationDerivative();

	/*
	 * mDeltaT:
	 * Test: 30s simalation, length=50m, sag=2m, weight=80kg, heightOfFall=1m, Webbing=sonic2.0
	 * mDeltaT = 0.01: 1.67m deviation in max height at 27.04s (9 jumps)
	 * mDeltaT = 0.001: 0.144m deviation in max height at 28,7s
	 * mDeltaT = 0.0001: 0.014m deviation in max height at 28,58s
	 * mDeltaT = 0.00005: 0.007m deviation in max height at 28.57s (10 jumps)
	 */

	public SlacklineBounceSimulations() {
		this(new SlacklineCalculations());
		//mDeltaT = 0.001;
	}

	public SlacklineBounceSimulations(SlacklineCalculations slackCalc) {
		this.mSlackCalc = slackCalc;
        this.mWebbing = new Webbing();
		//mDeltaT = 0.001;
	}

	private double getKineticEnergy() {
		return 0.5 * mSlackCalc.getWeightOfSlackliner() * mSpeedOfSlackliner * mSpeedOfSlackliner;
	}

	private void setSpeedFromEkin(double Ekin) {
		mSpeedOfSlackliner = Math.sqrt((2 * Ekin) / mSlackCalc.getWeightOfSlackliner());
	}

	private void iterationStep() {
		//double Ekin = 0.5 * mSlackCalc.getWeightOfSlackliner() * mSpeedOfSlackliner * mSpeedOfSlackliner;
		double m = mSlackCalc.getWeightOfSlackliner();
		mSlackCalc.setSag(-mVerticalPositionOfSlackliner);
		double F = m * gravityAcceleration - mSlackCalc.calculateVerticalForce(); // orientation downwards

		double a = F / m; // orientation downwards
		double deltaV = a * mDeltaT; // orientation downwards
		double deltaS = 0.5 * a * mDeltaT * mDeltaT + mSpeedOfSlackliner * mDeltaT; // orientation downwards

		mVerticalPositionOfSlackliner -= deltaS;
		mSpeedOfSlackliner += deltaV;

	}

	/**
	 * Does a simulation of a bounce or jump and returns the maximum forces involved
	 * @param heightOfFall the maximum height during bouncing or jumping relative to the horizontal connection of the anchor points
	 * @return array with the following maximum values: g-factor, Force on Slackliner, Force in Line, Sag of Line
	 */
	public double[] calculateMaximumForces(double heightOfFall) throws IllegalArgumentException{

		if (mSlackCalc.getStretchCoefficient() == 0)
			throw new IllegalArgumentException("Can not simulate bouncing with zero stretch");

		if (heightOfFall < 0) {
			mVerticalPositionOfSlackliner = heightOfFall;
			mSpeedOfSlackliner = 0;
		} else {
			mVerticalPositionOfSlackliner = 0;
			double Ekin = mSlackCalc.getWeightOfSlackliner() * gravityAcceleration * heightOfFall; // Ekin = Epot
			setSpeedFromEkin(Ekin);
		}

		do {
			iterationStep();
		} while (mSpeedOfSlackliner > 0);

		double forces[] = new double[4];
		double vForce = mSlackCalc.getVerticalForce();
		forces[0] = vForce / (mSlackCalc.getWeightOfSlackliner() * mSlackCalc.GRAVITY_ACCELERATION);
		forces[1] = vForce;
		forces[2] = mSlackCalc.getAnchorForce();
		forces[3] = -mVerticalPositionOfSlackliner;
		return forces;
	}


	/**
	 *
	 * @param heightOfBounce the maximum height during bouncing or jumping relative to the static standing position
	 * @return the maximum height during bouncing or jumping relative to the horizontal connection of the anchor points
	 */
	public double calculateHeightOfFallFromStandingReference(double heightOfBounce)
	{
		mSlackCalc.updateVerticalForceFromWeight();
		double sag = mSlackCalc.calculateSag();
		return heightOfBounce - sag;
	}

	public void simulateBouncing(double heightOfFall, double simulationTime) {

		double time[] = new double[(int) (simulationTime / mDeltaT) + 1];
		double verticalPos[] = new double[(int) (simulationTime / mDeltaT) + 1];
		double anchorForce[] = new double[(int) (simulationTime / mDeltaT) + 1];
		double verticalForce[] = new double[(int) (simulationTime / mDeltaT) + 1];

		mVerticalPositionOfSlackliner = heightOfFall;
		mSpeedOfSlackliner = 0;

		int i = 0;
		for (double currentTime = 0; currentTime < simulationTime; currentTime += mDeltaT, i++) {
			iterationStep();
			time[i] = currentTime;
			verticalPos[i] = mVerticalPositionOfSlackliner;
			anchorForce[i] = mSlackCalc.getAnchorForce(); //calculateAnchorForce?
			verticalForce[i] = mSlackCalc.getVerticalForce();

			//out.printf("Time, Pos, Fv, Fh:\t%.3f\t%.3f\t%.0f\t%.0f\n", currentTime, mVerticalPositionOfSlackliner, mSlackCalc.getVerticalForce(), mSlackCalc.getAnchorForce());
		}
		writeSimulationResultToCSVFile(time, verticalPos, anchorForce, verticalForce, "simulation.csv");
	}

	private void writeSimulationResultToCSVFile(double time[], double verticalPos[], double anchorForce[],
												double verticalForce[], String filename) {
		//File csvFile = new File(filename);

		Writer csvFile = null;

		try {
			csvFile = new FileWriter(filename);
			for (int i = 0; i < time.length; i++) {
				csvFile.append(time[i] + "," + verticalPos[i] + "," + verticalForce[i] + "," + anchorForce[i] + "\n");
			}

		} catch (IOException e) {
			System.err.println("Konnte Datei nicht erstellen");
		} finally {
			if (csvFile != null)
				try {
					csvFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}


	public void setSlackCalc(SlacklineCalculations slackCalc) {
		this.mSlackCalc = slackCalc;
	}

	public double getSpeedOfSlackliner() {
		return mSpeedOfSlackliner;
	}

	public void setSpeedOfSlackliner(double speedOfSlackliner) {
		this.mSpeedOfSlackliner = speedOfSlackliner;
	}

	public double getVerticalPositionOfSlackliner() {
		return mVerticalPositionOfSlackliner;
	}

	public void setVerticalPositionOfSlackliner(double verticalPositionOfSlackliner) {
		this.mVerticalPositionOfSlackliner = verticalPositionOfSlackliner;
	}

	public double getDeltaT() {
		return mDeltaT;
	}

	public void setDeltaT(double deltaT) {
		this.mDeltaT = deltaT;
	}

	public double getStretchCoefficient()
	{
		return mSlackCalc.getStretchCoefficient();
	}

	public String getWebbingName()
	{
		return mWebbing.getName();
	}

	public double getLength()
	{
		return mSlackCalc.getLength();
	}

	public double getPretension()
	{
		return mSlackCalc.getPretension();
	}

	public double getInitialSag()
	{
		return mSlackCalc.getSagWithoutSlacker();
	}

	public double getWeight()
	{
		return mSlackCalc.getWeightOfSlackliner();
	}

	public void setLength(double length) {
		mSlackCalc.setLength(length);
	}

	public void setPretension(double pretension) {
		mSlackCalc.setPretension(pretension);
	}

	public void setInitialSag(double initialSag) {
		mSlackCalc.setSagWithoutSlacker(initialSag);
	}

	public void setWeight(double weight)
	{
		mSlackCalc.setWeightOfSlackliner(weight);
	}

	public void setWebbing(Webbing webbing)
	{
        mWebbing = webbing;
		mSlackCalc.setWebbing(webbing);
	}


	interface RungeKuttaFunction
	{
		public double getFunctionValue(double s, double v, double a);
	}
	
	class SagDerivative implements RungeKuttaFunction
	{
		@Override
		public double getFunctionValue(double s, double v, double a)
		{
			return v;
		}
	}

	class SpeedDerivative implements RungeKuttaFunction
	{
		@Override
		public double getFunctionValue(double s, double v, double a)
		{
			return a;
		}
	}

	class AccelerationDerivative implements RungeKuttaFunction
	{
		@Override
		public double getFunctionValue(double s, double v, double a)
		{
			double l = getLength();
			double m = getWeight();
			double stretchCoefficient = getStretchCoefficient();
			double alpha1 = mWebbing.getLinearSolidModelStretchCoefficient1();
			double alpha2 = mWebbing.getLinearSolidModelStretchCoefficient2();
			double c1 = 1 / (alpha1 * (l/2) );
			double c2 = 1 / (alpha2 * (l/2) );
			double d = mWebbing.getLinearSolidModelDampingFactor() / (l/2);
			double g = gravityAcceleration;
			double F0 = getPretension();
			double deltaL0 = stretchCoefficient * F0 * l;
			double l1 = Math.sqrt(s * s + l*l/4);


			return (   (c1+c2)*(g-a)/d +
					2*s*v*(g-a)*(1/(2*l1)-l1/(s*s)) / l1 -
					2*s*c1*c2*((deltaL0/2)-(l/2)+l1) / (d*m*l1) -
					2*s*s*v*c1 / (m*l1*l1) );
		}
	}


	public void rungeKuttaIteration(double s0, double v0, double a0, double deltaT, double simulationTime)
	{
		
		double s = s0;
		double v = v0;
		double a = a0;
		double t = 0;
		
		double l = getLength();
		double m = getWeight();
		double l1 = Math.sqrt(s * s + l*l/4);
		
		double fv = m * (gravityAcceleration - a);
		double gfactor = (gravityAcceleration - a) / gravityAcceleration;
		double fa = l1 * fv / (2*s);


		Writer csvFile = null;
		String filename = "simulation.csv";

		try {
			csvFile = new FileWriter(filename);

			boolean alreadyprinted = false;

			for (int i = 0; i < simulationTime/deltaT; i++)
			{
				double m1 = mSagDerivative.getFunctionValue(s, v, a);
				double n1 = mSpeedDerivative.getFunctionValue(s, v, a);
				double o1 = mAccelerationDerivative.getFunctionValue(s, v, a);

				double m2 = mSagDerivative.getFunctionValue(s + 0.5*deltaT*m1, v + 0.5*deltaT*n1, a + 0.5*deltaT*o1);
				double n2 = mSpeedDerivative.getFunctionValue(s + 0.5*deltaT*m1, v + 0.5*deltaT*n1, a + 0.5*deltaT*o1);
				double o2 = mAccelerationDerivative.getFunctionValue(s + 0.5*deltaT*m1, v + 0.5*deltaT*n1, a + 0.5*deltaT*o1);

				double m3 = mSagDerivative.getFunctionValue(s + 0.5*deltaT*m2, v + 0.5*deltaT*n2, a + 0.5*deltaT*o2);
				double n3 = mSpeedDerivative.getFunctionValue(s + 0.5*deltaT*m2, v + 0.5*deltaT*n2, a + 0.5*deltaT*o2);
				double o3 = mAccelerationDerivative.getFunctionValue(s + 0.5*deltaT*m2, v + 0.5*deltaT*n2, a + 0.5*deltaT*o2);
	
				double m4 = mSagDerivative.getFunctionValue(s + deltaT*m3, v + deltaT*n3, a + deltaT*o3);
				double n4 = mSpeedDerivative.getFunctionValue(s + deltaT*m3, v + deltaT*n3, a + deltaT*o3);
				double o4 = mAccelerationDerivative.getFunctionValue(s + deltaT*m3, v + deltaT*n3, a + deltaT*o3);

				double deltaS = (1./6.) * deltaT * (m1 + 2*m2 + 2*m3 + m4);
				double deltaV = (1./6.) * deltaT * (n1 + 2*n2 + 2*n3 + n4);
				double deltaA = (1./6.) * deltaT * (o1 + 2*o2 + 2*o3 + o4);
				
				
				
				s += deltaS;
				v += deltaV;
				a += deltaA;
				t += deltaT;
				
				l1 = Math.sqrt(s * s + l*l/4);
				fv = m * (gravityAcceleration - a);
				gfactor = (gravityAcceleration - a) / gravityAcceleration;
				fa = l1 * fv / (2*s);

				csvFile.append(t + "," + s + "," + v + "," + a + "," + fv + "," + gfactor + "," + fa + "\n");

				
				if (deltaS <= 0 && !alreadyprinted)
				{
					out.printf("g-factor:\t\t%.5f\n", gfactor);
					out.printf("max. slacker force:\t%.5f kN\n", fv/1e3);
					out.printf("max. line force:\t%.5f kN\n", fa/1e3);
					out.printf("max. sag:\t\t%.5f m\n", s);
					alreadyprinted = true;
				}
				
			}

		} catch (IOException e) {
			System.err.println("Konnte Datei nicht erstellen");
		} finally {
			if (csvFile != null)
				try {
					csvFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}


//	private SlacklineCalculations getSlackCalc()
//	{
//		return mSlackCalc;
//	}

	
}

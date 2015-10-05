package slacklib;

import java.io.*;

public class SlacklineBounceSimulations {
	private final double gravityAcceleration = 9.81;

	private SlacklineCalculations mSlackCalc;
    private Webbing mWebbing;
	private double mSpeedOfSlackliner; // direction downwards
	private double mVerticalPositionOfSlackliner; // corresponds to -sag of slackline for positive values
	private double mDeltaT = 0.001;

	private double s, v, a, t, l, l2, m, fv, f0, gFactor, fa, deltaS, deltaV, deltaA, deltaFa;

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
		double sag = mSlackCalc.calculateSag();
		return sag - heightOfBounce;
	}

//	public void simulateBouncing(double heightOfFall, double simulationTime) {
//
//		double time[] = new double[(int) (simulationTime / mDeltaT) + 1];
//		double verticalPos[] = new double[(int) (simulationTime / mDeltaT) + 1];
//		double anchorForce[] = new double[(int) (simulationTime / mDeltaT) + 1];
//		double verticalForce[] = new double[(int) (simulationTime / mDeltaT) + 1];
//
//		mVerticalPositionOfSlackliner = heightOfFall;
//		mSpeedOfSlackliner = 0;
//
//		int i = 0;
//		for (double currentTime = 0; currentTime < simulationTime; currentTime += mDeltaT, i++) {
//			iterationStep();
//			time[i] = currentTime;
//			verticalPos[i] = mVerticalPositionOfSlackliner;
//			anchorForce[i] = mSlackCalc.getAnchorForce(); //calculateAnchorForce?
//			verticalForce[i] = mSlackCalc.getVerticalForce();
//
//			//out.printf("Time, Pos, Fv, Fh:\time%.3f\time%.3f\time%.0f\time%.0f\n", currentTime, mVerticalPositionOfSlackliner, mSlackCalc.getVerticalForce(), mSlackCalc.getAnchorForce());
//		}
//		writeSimulationResultToCSVFile(time, verticalPos, anchorForce, verticalForce, "simulation.csv");
//	}

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


	private interface RungeKuttaFunction
	{
		double getFunctionValue(double s, double v, double a);
	}

	private class SagDerivative implements RungeKuttaFunction
	{
		@Override
		public double getFunctionValue(double s, double v, double a)
		{
			return v;
		}
	}

	private class SpeedDerivative implements RungeKuttaFunction
	{
		@Override
		public double getFunctionValue(double s, double v, double a)
		{
			return a;
		}
	}

	private class AccelerationDerivative implements RungeKuttaFunction
	{
		@Override
		public double getFunctionValue(double s, double v, double a) throws IllegalArgumentException
		{
			double l = getLength();
			double m = getWeight();
			double initialSag = getInitialSag();
			double stretchCoefficient = getStretchCoefficient();
			double alpha1 = mWebbing.getLinearSolidModelStretchCoefficient1();
			double alpha2 = mWebbing.getLinearSolidModelStretchCoefficient2();
			double k1 = 1 / (alpha1 * (l/2) );
			double k2 = 1 / (alpha2 * (l/2) );
			double d = mWebbing.getLinearSolidModelDampingFactor() / (l/2);
			double g = gravityAcceleration;
			double deltaL0 = stretchCoefficient * f0 * l;
			double l1 = Math.sqrt(initialSag * initialSag + l*l/4);
			double l2 = Math.sqrt(s * s + l*l/4);

			if(l <= 0 || m <= 0 || alpha1 <= 0 || alpha2 <= 0 || d <= 0)
				throw new IllegalArgumentException("Illegal parameter value in dynamic simulation");
			
			if(s == 0)
				return 0;
			
			return (   (k1 + k2)*(g-a)/d -
					v*l*l*(g-a) / (4*s*l2*l2) -
					2*s* k1 * k2 *((deltaL0/2)- l1 + l2) / (d*m* l2) -
					2*s*s*v* k1 / (m* l2 * l2) );
		}
	}

	private void simulationIterationStep()
	{
		double initialSag = getInitialSag();

		if(s <= initialSag)
		{
			freeFallIterationStep();
		}
		else
		{
			rungeKuttaIterationStep();
		}
	}

	private void rungeKuttaIterationStep()
	{

		double m1 = mSagDerivative.getFunctionValue(s, v, a);
		double n1 = mSpeedDerivative.getFunctionValue(s, v, a);
		double o1 = mAccelerationDerivative.getFunctionValue(s, v, a);

		double m2 = mSagDerivative.getFunctionValue(s + 0.5* mDeltaT *m1, v + 0.5* mDeltaT *n1, a + 0.5* mDeltaT *o1);
		double n2 = mSpeedDerivative.getFunctionValue(s + 0.5* mDeltaT *m1, v + 0.5* mDeltaT *n1, a + 0.5* mDeltaT *o1);
		double o2 = mAccelerationDerivative.getFunctionValue(s + 0.5* mDeltaT *m1, v + 0.5* mDeltaT *n1, a + 0.5* mDeltaT *o1);

		double m3 = mSagDerivative.getFunctionValue(s + 0.5* mDeltaT *m2, v + 0.5* mDeltaT *n2, a + 0.5* mDeltaT *o2);
		double n3 = mSpeedDerivative.getFunctionValue(s + 0.5* mDeltaT *m2, v + 0.5* mDeltaT *n2, a + 0.5* mDeltaT *o2);
		double o3 = mAccelerationDerivative.getFunctionValue(s + 0.5* mDeltaT *m2, v + 0.5* mDeltaT *n2, a + 0.5* mDeltaT *o2);

		double m4 = mSagDerivative.getFunctionValue(s + mDeltaT *m3, v + mDeltaT *n3, a + mDeltaT *o3);
		double n4 = mSpeedDerivative.getFunctionValue(s + mDeltaT *m3, v + mDeltaT *n3, a + mDeltaT *o3);
		double o4 = mAccelerationDerivative.getFunctionValue(s + mDeltaT *m3, v + mDeltaT *n3, a + mDeltaT *o3);

		deltaS = (1./6.) * mDeltaT * (m1 + 2*m2 + 2*m3 + m4);
		deltaV = (1./6.) * mDeltaT * (n1 + 2*n2 + 2*n3 + n4);
		deltaA = (1./6.) * mDeltaT * (o1 + 2*o2 + 2*o3 + o4);

		s += deltaS;
		v += deltaV;
		a += deltaA;
		t += mDeltaT;

		l2 = Math.sqrt(s * s + l*l/4);
		fv = m * (gravityAcceleration - a);
		gFactor = (gravityAcceleration - a) / gravityAcceleration;
		deltaFa =  l2 * fv / (2*s) - fa;
		fa = l2 * fv / (2*s);

	}

	private void freeFallIterationStep()
	{
		deltaS = 0.5*gravityAcceleration*mDeltaT*mDeltaT + v*mDeltaT;
		deltaV = gravityAcceleration * mDeltaT;
		deltaA = 0;

		s += deltaS;
		v += deltaV;
		a = gravityAcceleration;
		t += mDeltaT;

		fv = 0;
		gFactor = 0;
		fa = getPretension();
		deltaFa = 0;
	}


	private void initializeSimulation(double s0, double v0, double a0)
	{
		s = s0;
		v = v0;
		a = a0;
		t = 0;

		l = getLength();
		m = getWeight();
		f0 = getPretension();
		l2 = Math.sqrt(s * s + l * l / 4);

		fv = m * (gravityAcceleration - a);
		gFactor = (gravityAcceleration - a) / gravityAcceleration;

		if(s > 0)
			fa = l2 * fv / (2*s);
		else
			fa = getPretension();
	}

	public class SimulationValues
	{
		public double sag, speed, acceleration, time, verticalForce, anchorForce, gFactor;
	}

	public SimulationValues calculateMaximumSimulationValues(double s0)
	{
		double v0, a0 = 0;
		double initialSag = getInitialSag();

		if (s0 < initialSag)
		{
			v0 = Math.sqrt(2*gravityAcceleration*(initialSag - s0));
			s0 = 0;
		}
		else
			v0 = 0;

		initializeSimulation(s0, v0, a0);

		SimulationValues maximumValues = new SimulationValues();
		final double maxSimulationTime = 100;
		boolean maxSagSet = false;
		boolean maxSpeedSet = false;
		boolean maxAccelerationSet = false;
		boolean maxForceSet = false;

		while(t < maxSimulationTime)
		{
			simulationIterationStep();


			if(deltaS < 0 && !maxSagSet)
			{
				maximumValues.sag = s;
				maxSagSet = true;
			}
			if(deltaV < 0 && !maxSpeedSet)
			{
				maximumValues.speed = v;
				maxSpeedSet = true;
			}
			if(deltaA > 0 && !maxAccelerationSet && maxSpeedSet)
			{
				maximumValues.acceleration = a;
				maximumValues.verticalForce = fv;
				maximumValues.gFactor = gFactor;
				maxAccelerationSet = true;
			}
			if(deltaFa < 0 && !maxForceSet && maxSpeedSet)
			{
				maximumValues.anchorForce = fa;
				maxForceSet = true;
			}

			if(maxSagSet && maxSpeedSet && maxAccelerationSet && maxForceSet)
				break;
		}

		return maximumValues;
	}

	public void writeSimulationToFile(double s0, double simulationTime, String filename)
	{
		double v0 = 0;
		double a0 = 0;
		initializeSimulation(s0, v0, a0);

		Writer csvFile = null;

		try {
			csvFile = new FileWriter(filename);

			while (t < simulationTime)
			{

				simulationIterationStep();

				csvFile.append(t + "," + s + "," + v + "," + a + "," + fv + "," + gFactor + "," + fa + "\n");
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

package slacklib;

import java.io.*;

public class SlacklineBounceSimulations {
	private final double gravityAcceleration = 9.81;

	private SlacklineCalculations mSlackCalc;
	private double mSpeedOfSlackliner; // direction downwards
	private double mVerticalPositionOfSlackliner; // corresponds to -sag of slackline for positive values
	private double mDeltaT = 0.0001;
	/*
	 * mDeltaT:
	 * Test: 30s simalation, length=50m, sag=2m, weight=80kg, heightOfFall=1m, Webbing=sonic2.0
	 * mDeltaT = 0.01: 1.67m deviation in max height at 27.04s (9 jumps)
	 * mDeltaT = 0.001: 0.144m deviation in max height at 28,7s
	 * mDeltaT = 0.0001: 0.014m deviation in max height at 28,58s
	 * mDeltaT = 0.00005: 0.007m deviation in max height at 28.57s (10 jumps)
	 */

	public SlacklineBounceSimulations() {
		mSlackCalc = new SlacklineCalculations();
		//mDeltaT = 0.001;
	}

	public SlacklineBounceSimulations(SlacklineCalculations slackCalc) {
		this.mSlackCalc = slackCalc;
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

	public double[] calculateMaximumForces(double heightOfBounce) {
		double heightOfFall = calculateHeightOfFall(heightOfBounce);
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

	public void simulateBouncing(double heightOfBounce, double simulationTime) {
		double heightOfFall = calculateHeightOfFall(heightOfBounce);

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
		return mSlackCalc.getWebbingName();
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
		mSlackCalc.setWebbing(webbing);
	}

//	private SlacklineCalculations getSlackCalc()
//	{
//		return mSlackCalc;
//	}

	private double calculateHeightOfFall(double heightOfBounce)
	{
		mSlackCalc.updateVerticalForceFromWeight();
		double sag = mSlackCalc.calculateSag();
		return heightOfBounce - sag;
	}
	
}

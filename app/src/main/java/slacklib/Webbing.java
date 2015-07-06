package slacklib;

import java.util.*;

public class Webbing 
{
	private static List<Webbing> webbings = new LinkedList<Webbing>();
	
	private String mName;
	private Manufacturer mManufacturer;
	private double mWeightPerMeter; // [kg/m]
	//private double mStretchCoefficient; // [percent / 100N]
	private StretchBehavior mStretchBehavior;
	private double mBreakingStrength; // [kN]
	private double mWidth; // [mm]
	
//	public static void addWebbing(Webbing webbing)
//	{
//		webbings.add(webbing);
//	}
	
	public static void updateListOfWebbings()
	{
		webbings.clear();
		for( Manufacturer manufacturer : Manufacturer.getManufactorers())
		{
			webbings.addAll(manufacturer.getWebbings());
		}
	}
	
	public static List<Webbing> getAllWebbings()
	{
		return webbings;
	}
	
	public static void initializeWebbings()
	{
		webbings.clear();
		Manufacturer.getManufactorers().clear();

		Manufacturer balanceCommunity = new Manufacturer("Balance Community");
		Manufacturer elephant = new Manufacturer("Elephant");
		Manufacturer landcruising = new Manufacturer("Landcruising");
		Manufacturer slackliner = new Manufacturer("Slackliner.de");
		Manufacturer slacklineTools = new Manufacturer("Sackline-Tools");
		Manufacturer slackpro = new Manufacturer("SlackPro");
		Manufacturer slacktivity = new Manufacturer("Slacktivity");


		double aeroForces[] = {1e3, 2e3, 3e3, 4e3, 5e3, 6e3, 7e3, 8e3, 9e3, 10e3, 11e3, 12e3, 13e3, 14e3, 15e3, 16e3, 17e3, 18e3, 19e3, 20e3};
		double aeroStretch[] = {0.002, 0.004, 0.011, 0.0193, 0.0333, 0.052, 0.0686, 0.0812, 0.0932, 0.0997, 0.1062, 0.1108, 0.1150, 0.1193, 0.1236, 0.1277, 0.1318, 0.1357, 0.1391, 0.1424};
		double mantraForces[] = {1e3, 2e3, 3e3, 4e3, 5e3, 6e3, 7e3, 8e3, 9e3, 10e3, 11e3, 12e3, 13e3, 14e3, 15e3, 16e3, 17e3, 18e3, 19e3, 20e3};
		double mantraStretch[] = {0.0026, 0.0056, 0.0092, 0.0133, 0.0178, 0.0226, 0.0275, 0.0325, 0.0373, 0.0421, 0.0467, 0.0511, 0.0552, 0.059, 0.0625, 0.0657, 0.0687, 0.0715, 0.0741, 0.0765};
		double type18forces[] = {1e3, 2e3, 3e3, 4e3, 5e3, 6e3, 7e3, 8e3, 9e3, 10e3, 11e3, 12e3, 13e3};
		double type18stretch[] ={0.0106, 0.0239, 0.0452, 0.0694, 0.0880, 0.1033, 0.1169, 0.1278, 0.1384, 0.1479, 0.1570, 0.1665, 0.1740};
		balanceCommunity.addWebbing("Aero", 59e-3, new StretchBehavior(aeroForces, aeroStretch), 33.4, 25.4);
		balanceCommunity.addWebbing("Mantra MK4: Flight", 76e-3, new StretchBehavior(mantraForces, mantraStretch), 42, 25.4);
		balanceCommunity.addWebbing("RAGEline", 104e-3, new StretchBehavior(new StretchPoint(12e3, 0.0831)), 42, 31.8);
		balanceCommunity.addWebbing("Spider Silk MKII", 48e-3, new StretchBehavior(new StretchPoint(13.4e3, 0.025)), 67, 25.4);
		balanceCommunity.addWebbing("Threaded Slack-Spec Tubular", 70e-3, new StretchBehavior(new StretchPoint(6.7e3, 0.111)), 33.4, 25.4);
		balanceCommunity.addWebbing("Type 18 MKII", 62e-3, new StretchBehavior(type18forces, type18stretch), 35.6, 25.4);


		elephant.addWebbing("blueWing", 63e-3, new StretchBehavior(new StretchPoint(7e3, 0.072), new StretchPoint(15e3, 0.145)), 30, 25);
		elephant.addWebbing("flash'line", 86e-3, new StretchBehavior(new StretchPoint(7.5e3, 0.02)), 45, 50);
		elephant.addWebbing("Passion", 69e-3, new StretchBehavior(new StretchPoint(7.5e3, 0.11), new StretchPoint(15e3, 0.15)), 33, 25);
		elephant.addWebbing("Wing 3.5", 54e-3, new StretchBehavior(new StretchPoint(7.5e3, 0.059)), 30, 35);

		double coreHSForce[] = {4e3, 6e3, 7e3, 8e3, 9e3, 10e3, 11e3, 12e3, 13e3, 14e3, 15e3, 16e3};
		double coreHSStretch[] = {0.011, 0.016, 0.022, 0.03, 0.042, 0.057, 0.061, 0.065, 0.069, 0.073, 0.077, 0.081};
		double coreLSForce[] = {4e3, 6e3, 7e3, 8e3, 9e3, 10e3, 11e3, 12e3, 13e3, 14e3, 15e3, 16e3};
		double coreLSStretch[]= {0.008, 0.011, 0.013, 0.016, 0.018, 0.02, 0.022, 0.025, 0.028, 0.03, 0.033, 0.037};
		double tWaveForce[] = {2e3, 4e3, 6e3, 7e3, 8e3, 9e3, 10e3, 11e3, 12e3, 13e3, 14e3, 15e3, 16e3};
		double tWaveStretch[] = {0.031, 0.072, 0.1, 0.115, 0.126, 0.136, 0.143, 0.152, 0.158, 0.166, 0.172, 0.178, 0.184};
		double whiteMagicForces[] = {2e3, 4e3, 6e3, 7e3, 8e3, 9e3, 10e3};
		double whiteMagicStretch[] = {0.005, 0.01, 0.02, 0.026, 0.033, 0.041, 0.048};
		landcruising.addWebbing("Core 2 HS", 0.0715, new StretchBehavior(coreHSForce, coreHSStretch), 44, 25);
		landcruising.addWebbing("Core 2 LS", 0.0699, new StretchBehavior(coreLSForce, coreLSStretch), 44, 25);
		landcruising.addWebbing("Matrix", 100e-3, new StretchBehavior(new StretchPoint(12e3, 0.145), new StretchPoint(45.5e3, 0.34)), 45.5, 32);
		landcruising.addWebbing("Matrix Outer", 58e-3, new StretchBehavior(new StretchPoint(9e3, 0.14), new StretchPoint(27e3, 0.32)), 27, 32);
		landcruising.addWebbing("Sonic 2.0", 68e-3, new StretchBehavior(new StretchPoint(10e3, 0.13), new StretchPoint(35e3, 0.27)), 35, 25);
		landcruising.addWebbing("T-Wave", 80e-3, new StretchBehavior(tWaveForce, tWaveStretch), 38, 25);
		landcruising.addWebbing("Verve 25mm", 59e-3, new StretchBehavior(new StretchPoint(10e3, 0.11)), 32, 25);
		landcruising.addWebbing("Verve 35mm", 67e-3, new StretchBehavior(new StretchPoint(11.6e3, 0.085)), 36, 35);
		landcruising.addWebbing("Wave", 42e-3, new StretchBehavior(new StretchPoint(7e3, 0.145), new StretchPoint(21e3, 0.32)), 21, 25);
		landcruising.addWebbing("Wave Tape", 38e-3, new StretchBehavior(new StretchPoint(6e3, 0.14), new StretchPoint(18e3, 0.32)), 18, 19);
		landcruising.addWebbing("White Magic", 0.058, new StretchBehavior(whiteMagicForces, whiteMagicStretch), 32.5, 25);


		slackliner.addWebbing("Sigma Impact", 64e-3, new StretchBehavior(new StretchPoint(11.7e3, 0.12)), 35, 25);
		slackliner.addWebbing("Sigma LG", 66e-3, new StretchBehavior(new StretchPoint(12e3, 0.038)), 38, 66);
		slackliner.addWebbing("Sigma N (Jormungand)", 0.055, new StretchBehavior(new StretchPoint(8.7e3, 0.139), new StretchPoint(10e3, 0.17), new StretchPoint(26e3, 0.28)), 26, 25);
		slackliner.addWebbing("Sigma X (HeliX)", 44e-3, new StretchBehavior(new StretchPoint(8e3, 0.155), new StretchPoint(21e3, 0e264)), 21, 25);

		slacklineTools.addWebbing("AIR", 63e-3, new StretchBehavior(new StretchPoint(6e3, 0.04), new StretchPoint(25e3, 0.15)), 25, 45);
		slacklineTools.addWebbing("CLASSIC", 52e-3, new StretchBehavior(new StretchPoint(6e3, 0.05), new StretchPoint(25e3, 0.13)), 25, 30);
		slacklineTools.addWebbing("KIDS", 64e-3, new StretchBehavior(new StretchPoint(6e3, 0.06), new StretchPoint(25e3, 0.16)), 25, 45);
		slacklineTools.addWebbing("SOFT", 48e-3, new StretchBehavior(new StretchPoint(6e3, 0.13), new StretchPoint(21e3, 0.32)), 21, 30);
		slacklineTools.addWebbing("STRONG II", 67e-3, new StretchBehavior(new StretchPoint(6e3, 0.045), new StretchPoint(36e3, 0.15)), 36, 25);

		slackpro.addWebbing("NEON-Light", 0.057, new StretchBehavior(new StretchPoint(10e3, 0.0375)), 33, 25);
		slackpro.addWebbing("Green-Flash", 0.072, new StretchBehavior(new StretchPoint(10e3, 0.035), new StretchPoint(15e3, 0.05), new StretchPoint(20e3, 0.065)), 42, 25);

		slacktivity.addWebbing("BLACK-WHITE", 89e-3, new StretchBehavior(new StretchPoint(10e3, 6)), 43, 25);
		slacktivity.addWebbing("Marathon", 61.5e-3, new StretchBehavior(new StretchPoint(10e3, 0.04)), 37, 25);
		slacktivity.addWebbing("redTUBE", 76e-3, new StretchBehavior(new StretchPoint(10e3, 0.18)), 32, 25);

		updateListOfWebbings();
	}

	public static Webbing getWebbingByName(String name)
	{
		for( Webbing webbing: getAllWebbings())
		{
			if (name.equals(webbing.getName()))
				return webbing;
		}
		return null;
	}

	public Webbing(String name, StretchBehavior stretchBehavior)
	{
		this("Custom", null, 0, stretchBehavior, 0, 0);
	}

	public Webbing(String name, double lineWeight , StretchBehavior stretchBehavior)
	{
		this("Custom", null, lineWeight, stretchBehavior, 0, 0);
	}

	public Webbing(String name, Manufacturer manufacturer, double weightPerMeter, StretchBehavior stretchBehavior, double breakingStrength, double width)
	{
		this.mName = name;
		this.mManufacturer = manufacturer;
		this.mWeightPerMeter = weightPerMeter;
		this.mStretchBehavior = stretchBehavior;
		this.mBreakingStrength = breakingStrength;
		this.mWidth = width;
	}
	
	public Webbing()
	{
		this("Custom", null, 0.05, new StretchBehavior(new StretchPoint(30e3,5e-6)), 30000, 25);
	}
	

	public String getName()
	{
		return mName;
	}



	public Manufacturer getManufacturer()
	{
		return mManufacturer;
	}



	public double getWeightPerMeter()
	{
		return mWeightPerMeter;
	}



	public double getStretchCoefficient()
	{
		return mStretchBehavior.getStretch(10e3) / 10e3;
	}

	public double getForce(double knownForce, double relativeChangeInLength)
	{
		return mStretchBehavior.getForce(knownForce, relativeChangeInLength);
	}

	public double getStretch(double force)
	{
		return mStretchBehavior.getStretch(force);
	}

	
	public double getBreakingStrength()
	{
		return mBreakingStrength;
	}

	public double getWidth()
	{
		return mWidth;
	}


	public boolean equals(Webbing webbing)
	{
		return mName.equals(webbing.getName());
	}
	
	@Override public String toString()
	{
		return mName;
	}


	private void setName(String name)
	{
		this.mName = name;
	}

	private void setManufacturer(Manufacturer manufacturer)
	{
		this.mManufacturer = manufacturer;
	}

	private void setWeightPerMeter(double weightPerMeter)
	{
		this.mWeightPerMeter = weightPerMeter;
	}

//	private void setStretchCoefficient(double stretchCoefficient)
//	{
//		this.mStretchCoefficient = stretchCoefficient;
//	}

	private void setBreakingStrength(double breakingStrength)
	{
		this.mBreakingStrength = breakingStrength;
	}

	private void setWidth(double width)
	{
		this.mWidth = width;
	}


}

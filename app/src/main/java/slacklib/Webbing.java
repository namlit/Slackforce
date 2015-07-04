package slacklib;

import java.util.*;

public class Webbing 
{
	private static List<Webbing> webbings = new LinkedList<Webbing>();
	
	private String mName;
	private Manufacturer mManufacturer;
	private double mWeightPerMeter; // [kg/m]
	private double mStretchCoefficient; // [percent / 100N]
	private StretchPoint[] mStretchPoints = null;
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

		balanceCommunity.addWebbing("Aero", 59e-3, 9.97e-6, 33.4, 25.4);
		balanceCommunity.addWebbing("Mantra MK4: Flight", 76e-3, 4.21e-6, 42, 25.4);
		balanceCommunity.addWebbing("RAGEline", 104e-3, 6.9e-6, 42, 31.8);
		balanceCommunity.addWebbing("Spider Silk MKII", 48e-3, 1.9e-6, 67, 25.4);
		balanceCommunity.addWebbing("Threaded Slack-Spec Tubular", 70e-3, 16e-6, 33.4, 25.4);
		balanceCommunity.addWebbing("Type 18 MKII", 62e-3, 14.79e-6, 35.6, 25.4);

		elephant.addWebbing("blueWing", 63e-3, 10e-6, 30, 25);
		elephant.addWebbing("flash'line", 86e-3, 2.67e-6, 45, 50);
		elephant.addWebbing("Passion", 69e-3, 12e-6, 33, 25);
		elephant.addWebbing("Wing 3.5", 54e-3, 7.8e-6, 30, 35);

		landcruising.addWebbing("Core 2 HS", 0.0715, 6*1e-6, 44, 25);
		landcruising.addWebbing("Core 2 LS", 0.0699, 2*1e-6, 44, 25);
		landcruising.addWebbing("Matrix", 100e-3, 10e-6, 45.5, 32);
		landcruising.addWebbing("Matrix Outer", 58e-3, 15e-6, 27, 32);
		landcruising.addWebbing("Sonic 2.0", 68e-3, 13e-6, 35, 25);
		landcruising.addWebbing("T-Wave", 80e-3, 12e-6, 38, 25);
		landcruising.addWebbing("Verve 25mm", 59e-3, 11e-6, 32, 25);
		landcruising.addWebbing("Verve 35mm", 67e-3, 7e-6, 36, 35);
		landcruising.addWebbing("Wave", 42e-3, 20e-6, 21, 25);
		landcruising.addWebbing("Wave Tape", 38e-3, 23e-6, 18, 19);
		landcruising.addWebbing("White Magic", 0.058, 5*1e-6, 32.5, 25);


		slackliner.addWebbing("Sigma Impact", 64e-3, 10.3e-6, 35, 25);
		slackliner.addWebbing("Sigma LG", 66e-3, 3.2e-6, 38, 66);
		slackliner.addWebbing("Sigma N (Jormungand)", 0.055, 17*1e-6, 26, 25);
		slackliner.addWebbing("Sigma P", 0.057, 5*1e-6, 30, 25);
		slackliner.addWebbing("Sigma X (HeliX)", 44e-3, 19e-6, 21, 25);

		slacklineTools.addWebbing("AIR", 63e-3, 6.6e-6, 25, 45);
		slacklineTools.addWebbing("CLASSIC", 52e-3, 8e-6, 25, 30);
		slacklineTools.addWebbing("KIDS", 64e-3, 10, 25, 45);
		slacklineTools.addWebbing("SOFT", 48e-3, 26e-3, 21, 30);
		slacklineTools.addWebbing("STRONG II", 67e-3, 7.5e-6, 36, 25);

		slackpro.addWebbing("NEON-Light", 0.057, 3.75*1e-6, 33, 25);
		slackpro.addWebbing("Green-Lightning", 0.072, 3.5*1e-6, 42, 25);

		slacktivity.addWebbing("BLACK-WHITE", 89e-3, 6e-6, 43, 25);
		slacktivity.addWebbing("Marathon", 61.5e-3, 4e-6, 37, 25);
		slacktivity.addWebbing("redTUBE", 76e-3, 18e-6, 32, 25);

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

	public Webbing(String name, double stretchCoefficient)
	{
		this("Custom", null, 0, stretchCoefficient, 0, 0);
	}

	public Webbing(String name, double lineWeight , double stretchCoefficient)
	{
		this("Custom", null, lineWeight, stretchCoefficient, 0, 0);
	}

	public Webbing(String name, Manufacturer manufacturer, double weightPerMeter, double stretchCoefficient, double breakingStrength, double width)
	{
		this.mName = name;
		this.mManufacturer = manufacturer;
		this.mWeightPerMeter = weightPerMeter;
		this.mStretchCoefficient = stretchCoefficient;
		this.mBreakingStrength = breakingStrength;
		this.mWidth = width;
	}
	
	public Webbing()
	{
		this("Custom", null, 0.05, 1e-5, 30000, 25);
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
		return mStretchCoefficient;
	}

	public double getStretch(double workingForce)
	{ //todo: sollte noch überarbeitet werden
		if (mStretchPoints == null || mStretchPoints.length == 0)
			return workingForce * getStretchCoefficient();
		int i = 0;
		while(workingForce > mStretchPoints[i].getForce())
		{
			if (i == mStretchPoints.length - 1)
				return mStretchPoints[i].getStretch();
			i++;
		}

		double x1 = mStretchPoints[i-1].getForce();
		double x2 = mStretchPoints[i].getForce();
		double y1 = mStretchPoints[i-1].getStretch();
		double y2 = mStretchPoints[i].getStretch();

		return (y2-y1) / (x2-x1) * (workingForce - x1) + y1;
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

	public class StretchPoint{
		private double force;
		private double stretch;
		StretchPoint(double force, double stretch){
			this.force = force;
			this.stretch = stretch;
		}

		public double getForce() {
			return force;
		}

		public double getStretch() {
			return stretch;
		}
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

	private void setStretchCoefficient(double stretchCoefficient)
	{
		this.mStretchCoefficient = stretchCoefficient;
	}

	private void setBreakingStrength(double breakingStrength)
	{
		this.mBreakingStrength = breakingStrength;
	}

	private void setWidth(double width)
	{
		this.mWidth = width;
	}


}

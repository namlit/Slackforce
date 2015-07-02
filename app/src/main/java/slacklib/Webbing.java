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

		Manufacturer landcruising = new Manufacturer("Landcruising");
		Manufacturer slackliner = new Manufacturer("Slackliner.de");
		Manufacturer slackpro = new Manufacturer("SlackPro");
		
		landcruising.addWebbing("Sonic 2.0", 0.068, 13*1e-6, 35, 25);
		landcruising.addWebbing("White Magic", 0.058, 5*1e-6, 32.5, 25);
		landcruising.addWebbing("Core 2 HS", 0.0715, 6*1e-6, 44, 25);
		landcruising.addWebbing("Core 2 LS", 0.0699, 2*1e-6, 44, 25);
		
		slackliner.addWebbing("Sigma N (Jormungand)", 0.055, 17*1e-6, 26, 25);
		slackliner.addWebbing("Sigma P", 0.057, 5*1e-6, 30, 25);
		
		slackpro.addWebbing("NEON-Light", 0.057, 3.75*1e-6, 33, 25);
		slackpro.addWebbing("Green-Lightning", 0.072, 3.5*1e-6, 42, 25);

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

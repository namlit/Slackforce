package slacklib;

import java.util.*;

import slacklib.Manufacturer;

public class Webbing 
{
	private static List<Webbing> webbings = new LinkedList<Webbing>();
	
	private String name;
	private Manufacturer manufacturer;
	private double weightPerMeter; // [kg/m]
	private double stretchCoefficient; // [percent / 100N]
	private StretchPoint[] stretchPoints = null;
	private double breakingStrength; // [kN]
	private double width; // [mm]
	
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
		this.name = name;
		this.manufacturer = manufacturer;
		this.weightPerMeter = weightPerMeter;
		this.stretchCoefficient = stretchCoefficient;
		this.breakingStrength = breakingStrength;
		this.width = width;
	}
	
	public Webbing()
	{
		this("Custom", null, 0.05, 1e-5, 30000, 25);
	}
	

	public String getName()
	{
		return name;
	}



	public Manufacturer getManufacturer()
	{
		return manufacturer;
	}



	public double getWeightPerMeter()
	{
		return weightPerMeter;
	}



	public double getStretchCoefficient()
	{
		return stretchCoefficient;
	}

	public double getStretchCoefficient(double workingForce)
	{
		if (stretchPoints == null || stretchPoints.length == 0)
			return getStretchCoefficient();
		int i = 0;
		while(workingForce > stretchPoints[i].getForce())
		{
			if (i == stretchPoints.length - 1)
				return stretchPoints[i].getStretch();
			i++;
		}

		double x1 = stretchPoints[i-1].getForce();
		double x2 = stretchPoints[i].getForce();
		double y1 = stretchPoints[i-1].getStretch();
		double y2 = stretchPoints[i].getStretch();

		return (y2-y1) / (x2-x1) * (workingForce - x1) + y1;
	}


	
	public double getBreakingStrength()
	{
		return breakingStrength;
	}

	public double getWidth()
	{
		return width;
	}


	public boolean equals(Webbing webbing)
	{
		return name.equals(webbing.getName());
	}
	
	@Override public String toString()
	{
		return name;
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
		this.name = name;
	}

	private void setManufacturer(Manufacturer manufacturer)
	{
		this.manufacturer = manufacturer;
	}

	private void setWeightPerMeter(double weightPerMeter)
	{
		this.weightPerMeter = weightPerMeter;
	}

	private void setStretchCoefficient(double stretchCoefficient)
	{
		this.stretchCoefficient = stretchCoefficient;
	}

	private void setBreakingStrength(double breakingStrength)
	{
		this.breakingStrength = breakingStrength;
	}

	private void setWidth(double width)
	{
		this.width = width;
	}


}

package slacklib;

import java.util.*;

import slacklib.Webbing;

public class Manufacturer 
{
	private static List<Manufacturer> manufacturers = new LinkedList<Manufacturer>();
	
	private String name;
	private List<Webbing> webbings;
	
	private static void addManufactorer(Manufacturer manufactorer)
	{
		manufacturers.add(manufactorer);
	}
	
	public static List<Manufacturer> getManufactorers()
	{
		return manufacturers;
	}

	public static Manufacturer getManufacturerByID(int id)
	{
		return manufacturers.get(id);
	}

	public static void insertManufacorersFromFile()
	{
		
	}
	
	public Manufacturer(String name)
	{
		webbings = new LinkedList<Webbing>();
		this.name = name;
		addManufactorer(this);
	}
	
	
	
	public String getName()
	{
		return name;
	}


	public void setName(String name)
	{
		this.name = name;
	}

	public boolean equals(Manufacturer manufactorer)
	{
		return name.equals(manufactorer.getName());
	}
	
	@Override public String toString()
	{
		return name;
	}
	
	public void addWebbing(String name, double weightPerMeter, double stretchCoefficient, double breakingStrength, double width)
	{
		webbings.add(new Webbing(name, this, weightPerMeter, stretchCoefficient, breakingStrength, width));
	}
	
	public void addWebbing(Webbing webbing)
	{
		webbings.add(webbing);
	}
	
	public List<Webbing> getWebbings()
	{
		return webbings;
	}

	public Webbing getWebbingByID(int id)
	{
		return getWebbings().get(id);
	}
}

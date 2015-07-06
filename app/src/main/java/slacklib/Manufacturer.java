package slacklib;

import java.util.*;

public class Manufacturer 
{
	private static List<Manufacturer> manufacturers = new LinkedList<Manufacturer>();
	
	private String mName;
	private List<Webbing> mWebbings;
	
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
		mWebbings = new LinkedList<Webbing>();
		this.mName = name;
		addManufactorer(this);
	}
	
	
	
	public String getName()
	{
		return mName;
	}


	public void setName(String name)
	{
		this.mName = name;
	}

	public boolean equals(Manufacturer manufactorer)
	{
		return mName.equals(manufactorer.getName());
	}
	
	@Override public String toString()
	{
		return mName;
	}
	
	public void addWebbing(String name, double weightPerMeter, StretchBehavior stretchCoefficient, double breakingStrength, double width)
	{
		mWebbings.add(new Webbing(name, this, weightPerMeter, stretchCoefficient, breakingStrength, width));
	}
	
	public void addWebbing(Webbing webbing)
	{
		mWebbings.add(webbing);
	}
	
	public List<Webbing> getWebbings()
	{
		return mWebbings;
	}

	public Webbing getWebbingByID(int id)
	{
		return getWebbings().get(id);
	}
}

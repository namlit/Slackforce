package slacklib;

import java.math.*;

import slacklib.Manufacturer;
import slacklib.Webbing;

public class SlacklineMeasurements
{
	private Webbing webbing;
	private double length; // [m]
	//private double timeOfOscillation; // [s]
	
	public SlacklineMeasurements()
	{
		webbing = new Webbing();
		length = 100;
	}

	public Webbing getWebbing()
	{
		return webbing;
	}

	public void setWebbing(Webbing webbing)
	{
		this.webbing = webbing;
	}

	public double getLength()
	{
		return length;
	}

	public void setLength(double length)
	{
		this.length = length;
	}

	public double calculateForce(double timeOfOscillation)
	{
		if (webbing == null)
			return 0;
		if (webbing.getStretchCoefficient() == 0)
		{
			return (webbing.getWeightPerMeter() * 4 * length*length) / (timeOfOscillation*timeOfOscillation);
		}
		return (Math.sqrt(1 / (4 * Math.pow(webbing.getStretchCoefficient(), 2)) + (webbing.getWeightPerMeter() * 4 * length*length) / 
				(webbing.getStretchCoefficient() * Math.pow(timeOfOscillation, 2))) - 1 / (2 * webbing.getStretchCoefficient()) );
	}
	
}

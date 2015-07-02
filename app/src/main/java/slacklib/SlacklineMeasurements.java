package slacklib;

public class SlacklineMeasurements
{
	private Webbing mWebbing;
	private double mLength; // [m]
	//private double timeOfOscillation; // [s]
	
	public SlacklineMeasurements()
	{
		mWebbing = new Webbing();
		mLength = 100;
	}


	public double getStretchCoefficient()
	{
		return mWebbing.getStretchCoefficient();
	}

	public double getWeightPerMeter()
	{
		return mWebbing.getWeightPerMeter();
	}

	public String getWebbingName()
	{
		return mWebbing.getName();
	}

	public void setWebbing(Webbing webbing)
	{
		this.mWebbing = webbing;
	}

	public double getLength()
	{
		return mLength;
	}

	public void setLength(double length)
	{
		this.mLength = length;
	}

	public double calculateForce(double timeOfOscillation)
	{
		if (mWebbing == null)
			return 0;
		if (mWebbing.getStretchCoefficient() == 0)
		{
			return (mWebbing.getWeightPerMeter() * 4 * mLength * mLength) / (timeOfOscillation*timeOfOscillation);
		}
		return (Math.sqrt(1 / (4 * Math.pow(mWebbing.getStretchCoefficient(), 2)) + (mWebbing.getWeightPerMeter() * 4 * mLength * mLength) /
				(mWebbing.getStretchCoefficient() * Math.pow(timeOfOscillation, 2))) - 1 / (2 * mWebbing.getStretchCoefficient()) );
	}


//	private Webbing getWebbing()
//	{
//		return mWebbing;
//	}
}

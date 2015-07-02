package slacklib;

public class SlacklineCalculations
{
	public final double GRAVITY_ACCELERATION = 9.81;
	
	private double sag; // [m]
	private double sagWithoutSlacker; // [m] for rodeo lines, normally 0
	private double length; // [m]
	private double weightOfSlackliner; // [kg]
	private double verticalForce; // N
	private double anchorForce; // [N]
	private double pretension; // [kN]
	private Webbing webbing;

	private boolean isPretensionConstant = true; // is also used if the sag without Slackliner on Rodeolines changes
	
	public SlacklineCalculations()
	{
		this(new Webbing(), 100, 3, 80);
	}

	public SlacklineCalculations(Webbing webbing, double length, double sag, double weight)
	{
		this.sag = sag;
		this.length = length;
		this.weightOfSlackliner = weight;
		this.verticalForce = calculateVerticalForceFromWeight();
		this.webbing = webbing;
		this.anchorForce = calculateAnchorForce();
		calculatePretension();
	}

	@Override public String toString()
	{
		return "Length:\t\t" + length + " m\n" +
				"Sag:\t\t" + sag + " m\n" +
				"Weigth:\t\t" + weightOfSlackliner + " kg\n" +
				"Force:\t\t" + anchorForce/1000 + " kN\n" +
				"Pretension:\t" + pretension/1000 + " kN\n" +
				"Initial Sag:\t" + sagWithoutSlacker + " m" +
				"Webbing:\t" + webbing + "\n"
				;
	}

	/**
	 * This method does not check that the parameters sag, length, weight, force
	 * and pretension are consistent. Normally you should use method calculateSag()
	 * instead.
	 */
	public double getSag()
	{
		return sag;
	}
	
	public double getSagWithoutSlacker()
	{
		return sagWithoutSlacker;
	}
	
	/**
	 * This method does not check that the parameters sag, length, weight, force
	 * and pretension are consistent. Normally you should use method calculateLenght()
	 * instead.
	 */
	public double getLength()
	{
		return length;
	}
	
	/**
	 * This method does not check that the parameters sag, length, weight, force
	 * and pretension are consistent. Normally you should use method calculateWeight()
	 * instead.
	 */
	public double getWeightOfSlackliner()
	{
		return weightOfSlackliner;
	}
	/**
	 * This method does not check that the parameters sag, length, weight, force
	 * and pretension are consistent. Normally you should use method calculateVerticalForce()
	 * instead.
	 */
	public double getVerticalForce()
	{
		return verticalForce;
	}
	/**
	 * This method returns the current anchorForce calculated from the pretension.
	 * The anchorForce does not get calculated from the current sag, length and
	 * verticalForce Values. To do this use the method caculateAnchorForce().
	 */
	public double getAnchorForce()
	{
		return anchorForce;
	}
	
	/**
	 * This method does not check that the parameters sag, length, weight, force
	 * and pretension are consistent. Normally you should use method calculatePretension()
	 * instead.
	 */
	public double getPretension()
	{
		return pretension;
	}

	public Webbing getWebbing()
	{
		return webbing;
	}
	
	public void setSag(double sag)
	{
		this.sag = sag;
		if(this.sag < 0)
			this.sag = 0;
	}

	public void setSagWithoutSlacker(double sag)
	{
		sagWithoutSlacker = sag;
		isPretensionConstant = true;
		
		if(sagWithoutSlacker < 0)
			sagWithoutSlacker = 0;
		
		if(sagWithoutSlacker > 0)
		{
			pretension = 0;
		}
	}
	public void setLength(double length)
	{
		this.length = length;
		if(this.length < 0)
			this.length = 0;
	}
	
	/**
	 * If the weight of the slackliner gets set, the vertical force is 
	 * automatically recalculated and may change, too. Balanced conditions
	 * are assumed for that
	 * @param weightOfSlackliner
	 */
	public void setWeightOfSlackliner(double weightOfSlackliner)
	{
		this.weightOfSlackliner = weightOfSlackliner;
		setVerticalForce(weightOfSlackliner * GRAVITY_ACCELERATION);
	}
	public void setVerticalForce(double force)
	{
		this.verticalForce = force;
	}
	/**
	 * setting the AnchorForce causes the pretension to get calculated from the
	 * current sag, length, verticalForce and the force given as parameter.
	 * @param force
	 */
	public void setAnchorForce(double force)
	{
		if(force < 0.5*verticalForce)
		{
			// todo maybe throw exeption or do some error handling
			return;
		}

		this.anchorForce = force;
		isPretensionConstant = false;

		calculatePretensionFromAnchorForce();
		
		//setPretension(pretension);
	}
	
	/**
	 * when the pretension gets set the tension on the anchor is automatically
	 * recalculated avoid inconsistent values.
	 * @param pretension
	 */
	public void setPretension(double pretension)
	{
		this.pretension = pretension;
		isPretensionConstant = true;
		
		if(this.pretension < 0)
			this.pretension = 0;
		
		if(this.pretension > 0)
		{
			setSagWithoutSlacker(0);
		}
	}

	public void setWebbing(Webbing webbing)
	{
		this.webbing = webbing;
	}
	


	public double calculateAnchorForce()
	{
		anchorForce = (Math.sqrt(sag*sag + length*length / 4) * verticalForce) / (2 * sag);
		
		//setAnchorForce(anchorForce);
		return anchorForce;
	}
	public double calculatePretension()
	{
		calculateAnchorForce();
		return calculatePretensionFromAnchorForce();
	}

	public double calculateWeight()
	{
		return calculateVerticalForce() / GRAVITY_ACCELERATION;
	}

	public double calculateVerticalForce()
	{
		if(isPretensionConstant)
		{
			calculateAnchorForceFromPretension();
		}

		verticalForce = calculateVerticalForceFromAnchorForce();

		if (!isPretensionConstant)
			calculatePretensionFromAnchorForce();

		return verticalForce;
		
	}


	public double calculateSag()
	{
		if(isPretensionConstant)
		{
			calculateSagFromPretension();
			calculateAnchorForce();
			return sag;
		}
		else
		{
			calculateSagFromAnchorForce();
			calculatePretensionFromAnchorForce();
		}
		return sag;
	}

	public double calculateLength()
	{
		if(isPretensionConstant)
		{
			calculateLengthFromPretension();
			calculateAnchorForce();
			return length;
		}
		else
		{
			calculateLengthFromAnchorForce();
			calculatePretensionFromAnchorForce();
		}
		return length;
	}




	private double calculateVerticalForceFromAnchorForce()
	{
		return (2 * sag * anchorForce) / (Math.sqrt(sag*sag + length*length/4));
	}

	private double calculateVerticalForceFromWeight()
	{
		return weightOfSlackliner * GRAVITY_ACCELERATION;
	}

	private double calculateSagFromAnchorForce()
	{
		sag = Math.sqrt((verticalForce * verticalForce * length * length) /
				(4 * (4 * anchorForce * anchorForce - verticalForce * verticalForce)));
		return sag;
	}

	/**
	 * normally the sag is calculated by the current AnchorForce. As the relationship between
	 * the anchorForce and the Pretension is dependent on the sag, the pretension will change,too.
	 * If you want to keep the pretension constant you should use this method. As the mathematical
	 * formula for this calculation is much more complicated an iterative approach is used. The
	 * Pretension is guarantied to change less than 0.1 N.
	 *
	 * @return the calculated sag
	 */
	private double calculateSagFromPretension()
	{
		if (pretension <= 0 && sagWithoutSlacker > 0)
			return calculateSagFromSagWithoutSlacker();

		sag = iterativeApproximation(new SagFromPretensionIterative(), 1e-4, 1e4);

		//calculateAnchorForce();

		return sag;
	}

	private double calculateSagFromSagWithoutSlacker()
	{
		if(sagWithoutSlacker <= 0 && pretension > 0)
		{
			return calculateSagFromPretension();
		}
		sag = iterativeApproximation(new SagFromSagWithoutSlackerIterative(), 1e-6, 1e6);
		//calculateAnchorForce();
		return sag;

	}

	private double calculateLengthFromAnchorForce()
	{
		double anchorForce = getAnchorForce();
		length =  Math.sqrt((4 * sag * (4 * anchorForce * anchorForce - verticalForce * verticalForce)) /
				(verticalForce * verticalForce));
		return length;
	}

	private double calculateLengthFromPretension()
	{
		if (pretension <= 0 && sagWithoutSlacker > 0)
			return calculateLengthFromSagWithoutSlackliner();

		length = iterativeApproximation(new LengthFromPretensionIterative(), 1e-6, 1e6);

		//calculateAnchorForce();
		//calculatePretensionFromAnchorForce();

		return length;
	}

	private double calculateLengthFromSagWithoutSlackliner()
	{
		if (sagWithoutSlacker <= 0 && pretension > 0)
			return calculateLengthFromPretension();


		length = iterativeApproximation(new LengthFromSagWithoutSlackerIterative(), 1e-4, 1e4);
		//calculateAnchorForce();
		return length;
	}


	private double calculatePretensionFromAnchorForce()
	{
		if(webbing == null || webbing.getStretchCoefficient() == 0)
		{
			if(sag > 0)
			{
				pretension = 0;
				sagWithoutSlacker = sag;
			}
			else
				pretension = anchorForce;
		}
		else
		{
			pretension = anchorForce - ((2 * Math.sqrt(sag*sag + length*length / 4) - length) / (webbing.getStretchCoefficient() * length));

		}
		if (pretension <= 0)
		{
			pretension = 0;
			sagWithoutSlacker = Math.sqrt( (Math.pow(length,2)/4 + sag*sag) / Math.pow(1 + webbing.getStretchCoefficient() * anchorForce,2) - Math.pow(length,2)/4);
		}
		else
			sagWithoutSlacker = 0;
		return pretension;
	}

	/**
	 * This method calculates the AnchorForce from the pretension of the Line. For the calculation
	 * a constant length and sag are assumed. Therefore it is not possible to use that function
	 * for calculating the length or the sag of the line.
	 * @return
	 */
	private double calculateAnchorForceFromPretension()
	{

		if(pretension == 0 && sagWithoutSlacker > 0)
			return calculateAnchorForceFromSagWithoutSlackliner();

		if(webbing == null || webbing.getStretchCoefficient() == 0)
		{
			anchorForce = pretension;
		}
		else
		{
			anchorForce = pretension + ((2 * Math.sqrt(sag * sag + length * length / 4) - length) / (webbing.getStretchCoefficient() * length));
		}

		return anchorForce;
	}

	/**
	 * This method calculates the AnchorForce from the sag without a Slacker on a rodeo line. For the calculation
	 * a constant length and sag are assumed. Therefore it is not possible to use that function
	 * for calculating the length or the sag of the line.
	 * @return
	 */
	private double calculateAnchorForceFromSagWithoutSlackliner()
	{
		if(sagWithoutSlacker == 0 && pretension > 0)
			return calculateAnchorForceFromPretension();

		if(webbing == null || webbing.getStretchCoefficient() == 0)
		{
			anchorForce = (Math.sqrt(Math.pow(sagWithoutSlacker, 2) + Math.pow(length, 2) / 4) * verticalForce) / (2*sagWithoutSlacker);
		}
		else
			anchorForce = (Math.sqrt( (Math.pow(length,2)/4 + sag*sag) / (Math.pow(sagWithoutSlacker, 2) + Math.pow(length,2)/4 )) - 1) / webbing.getStretchCoefficient();

		if(anchorForce 	< 0)
			anchorForce = 0;

		return anchorForce;
	}

	interface itarativeApproximationFunction
	{
		double getFunctionValue(double x);
	}

	class LengthFromPretensionIterative implements itarativeApproximationFunction {
		@Override
		public double getFunctionValue(double length) {
			double l1 = Math.sqrt(sag*sag + length*length/4);
			return l1 * (verticalForce/(2*sag) - 2/(webbing.getStretchCoefficient()*length)) - pretension + 1/webbing.getStretchCoefficient();
		}
	}
	class SagFromPretensionIterative implements itarativeApproximationFunction {
		@Override
		public double getFunctionValue(double sag) {
			double l1 = Math.sqrt(sag*sag + length*length/4);
			return l1 * (verticalForce/(2*sag) - 2/(webbing.getStretchCoefficient()*length)) - pretension + 1/webbing.getStretchCoefficient();
		}
	}

	class LengthFromSagWithoutSlackerIterative implements itarativeApproximationFunction {
		@Override
		public double getFunctionValue(double length) {
			double l1 = Math.sqrt(sagWithoutSlacker*sagWithoutSlacker + length*length/4);
			double l2 = Math.sqrt(sag*sag + length*length/4);
			double stretch = webbing.getStretchCoefficient();
			return (l2/l1 - 1) / stretch - l2 * verticalForce / (2*sag);
		}
	}
	class SagFromSagWithoutSlackerIterative implements itarativeApproximationFunction {
		@Override
		public double getFunctionValue(double sag) {
			double l1 = Math.sqrt(sagWithoutSlacker*sagWithoutSlacker + length*length/4);
			double l2 = Math.sqrt(sag*sag + length*length/4);
			double stretch = webbing.getStretchCoefficient();
			return (l2/l1 - 1) / stretch - l2 * verticalForce / (2*sag);
		}
	}



	private double iterativeApproximation(itarativeApproximationFunction function, double x1, double x2) {
		final double epsilon = 1e-5;
		final double m = 0.5;
		double x = 0;
		double fx1, fx2, fx;

		if (Math.signum(function.getFunctionValue(x1)) == Math.signum(function.getFunctionValue(x2)))
			return 0;

		fx1 = function.getFunctionValue(x1);
		fx2 = function.getFunctionValue(x2);

		while (Math.abs(x2 - x1) > epsilon) {

			x = (x1 * fx2 - x2 * fx1) / (fx2 - fx1);
			fx = function.getFunctionValue(x);

			if (fx * fx2 < 0) {
				x1 = x2;
				fx1 = fx2;
				x2 = x;
				fx2 = fx;
			} else {
				fx1 *= m;
				x2 = x;
				fx2 = fx;
			}
		}
		return x;
	}

}

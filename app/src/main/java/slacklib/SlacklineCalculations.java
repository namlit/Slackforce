package slacklib;

public class SlacklineCalculations
{
	public final double GRAVITY_ACCELERATION = 9.81;
	
	private double mSag; // [m]
	private double mSagWithoutSlacker; // [m] for rodeo lines, normally 0
	private double mLength; // [m]
	private double mWeightOfSlackliner; // [kg]
	private double mVerticalForce; // N
	private double mAnchorForce; // [N]
	private double mPretension; // [kN]
	private Webbing mWebbing;

	private boolean isPretensionConstant = true; // is also used if the mSag without Slackliner on Rodeolines changes
	
	public SlacklineCalculations()
	{
		this(new Webbing(), 100, 3, 80);
	}

	public SlacklineCalculations(Webbing webbing, double length, double sag, double weight)
	{
		this.mSag = sag;
		this.mLength = length;
		this.mWeightOfSlackliner = weight;
		this.mVerticalForce = calculateVerticalForceFromWeight();
		this.mWebbing = webbing;
		this.mAnchorForce = calculateAnchorForce();
		calculatePretension();
	}

	@Override public String toString()
	{
		return "Length:\t\t" + mLength + " m\n" +
				"Sag:\t\t" + mSag + " m\n" +
				"Weigth:\t\t" + mWeightOfSlackliner + " kg\n" +
				"Force:\t\t" + mAnchorForce /1000 + " kN\n" +
				"Pretension:\t" + mPretension /1000 + " kN\n" +
				"Initial Sag:\t" + mSagWithoutSlacker + " m" +
				"Webbing:\t" + mWebbing + "\n"
				;
	}

	/**
	 * This method does not check that the parameters mSag, mLength, weight, force
	 * and mPretension are consistent. Normally you should use method calculateSag()
	 * instead.
	 */
	public double getSag()
	{
		return mSag;
	}
	
	public double getSagWithoutSlacker()
	{
		return mSagWithoutSlacker;
	}
	
	/**
	 * This method does not check that the parameters mSag, mLength, weight, force
	 * and mPretension are consistent. Normally you should use method calculateLenght()
	 * instead.
	 */
	public double getLength()
	{
		return mLength;
	}
	
	/**
	 * This method does not check that the parameters mSag, mLength, weight, force
	 * and mPretension are consistent. Normally you should use method calculateWeight()
	 * instead.
	 */
	public double getWeightOfSlackliner()
	{
		return mWeightOfSlackliner;
	}
	/**
	 * This method does not check that the parameters mSag, mLength, weight, force
	 * and mPretension are consistent. Normally you should use method calculateVerticalForce()
	 * instead.
	 */
	public double getVerticalForce()
	{
		return mVerticalForce;
	}
	/**
	 * This method returns the current mAnchorForce calculated from the mPretension.
	 * The mAnchorForce does not get calculated from the current mSag, mLength and
	 * mVerticalForce Values. To do this use the method caculateAnchorForce().
	 */
	public double getmAnchorForce()
	{
		return mAnchorForce;
	}
	
	/**
	 * This method does not check that the parameters mSag, mLength, weight, force
	 * and mPretension are consistent. Normally you should use method calculatePretension()
	 * instead.
	 */
	public double getPretension()
	{
		return mPretension;
	}

	public double getStretchCoefficient()
	{
		return mWebbing.getStretchCoefficient();
	}

	public String getWebbingName()
	{
		return mWebbing.getName();
	}


	
	public void setSag(double sag)
	{
		this.mSag = sag;
		if(this.mSag < 0)
			this.mSag = 0;
	}

	public void setSagWithoutSlacker(double sag)
	{
		mSagWithoutSlacker = sag;
		isPretensionConstant = true;
		
		if(mSagWithoutSlacker < 0)
			mSagWithoutSlacker = 0;
		
		if(mSagWithoutSlacker > 0)
		{
			mPretension = 0;
		}
	}
	public void setLength(double length)
	{
		this.mLength = length;
		if(this.mLength < 0)
			this.mLength = 0;
	}
	
	/**
	 * If the weight of the slackliner gets set, the vertical force is 
	 * automatically recalculated and may change, too. Balanced conditions
	 * are assumed for that
	 * @param weightOfSlackliner
	 */
	public void setWeightOfSlackliner(double weightOfSlackliner)
	{
		this.mWeightOfSlackliner = weightOfSlackliner;
		setVerticalForce(weightOfSlackliner * GRAVITY_ACCELERATION);
	}
	public void setVerticalForce(double force)
	{
		this.mVerticalForce = force;
	}
	/**
	 * setting the AnchorForce causes the mPretension to get calculated from the
	 * current mSag, mLength, mVerticalForce and the force given as parameter.
	 * @param force
	 */
	public void setmAnchorForce(double force)
	{
		if(force < 0.5* mVerticalForce)
		{
			// todo maybe throw exeption or do some error handling
			return;
		}

		this.mAnchorForce = force;
		isPretensionConstant = false;

		calculatePretensionFromAnchorForce();
		
		//setPretension(mPretension);
	}
	
	/**
	 * when the mPretension gets set the tension on the anchor is automatically
	 * recalculated avoid inconsistent values.
	 * @param pretension
	 */
	public void setPretension(double pretension)
	{
		this.mPretension = pretension;
		isPretensionConstant = true;
		
		if(this.mPretension < 0)
			this.mPretension = 0;
		
		if(this.mPretension > 0)
		{
			setSagWithoutSlacker(0);
		}
	}

	public void setWebbing(Webbing webbing)
	{
		this.mWebbing = webbing;
	}
	


	public double calculateAnchorForce()
	{
		mAnchorForce = (Math.sqrt(mSag * mSag + mLength * mLength / 4) * mVerticalForce) / (2 * mSag);
		
		//setmAnchorForce(mAnchorForce);
		return mAnchorForce;
	}
	public double calculatePretension()
	{
		calculateAnchorForce();
		return calculatePretensionFromAnchorForce();
	}

	public double calculateWeight()
	{
		mWeightOfSlackliner = calculateVerticalForce() / GRAVITY_ACCELERATION;
		return mWeightOfSlackliner;
	}

	public double calculateVerticalForce()
	{
		if(isPretensionConstant)
		{
			calculateAnchorForceFromPretension();
		}

		mVerticalForce = calculateVerticalForceFromAnchorForce();

		if (!isPretensionConstant)
			calculatePretensionFromAnchorForce();

		return mVerticalForce;
		
	}


	public double calculateSag()
	{
		if(isPretensionConstant)
		{
			calculateSagFromPretension();
			calculateAnchorForce();
			return mSag;
		}
		else
		{
			calculateSagFromAnchorForce();
			calculatePretensionFromAnchorForce();
		}
		return mSag;
	}

	public double calculateLength()
	{
		if(isPretensionConstant)
		{
			calculateLengthFromPretension();
			calculateAnchorForce();
			return mLength;
		}
		else
		{
			calculateLengthFromAnchorForce();
			calculatePretensionFromAnchorForce();
		}
		return mLength;
	}


//	private Webbing getWebbing()
//	{
//		return mWebbing;
//	}

	private double calculateVerticalForceFromAnchorForce()
	{
		return (2 * mSag * mAnchorForce) / (Math.sqrt(mSag * mSag + mLength * mLength /4));
	}

	private double calculateVerticalForceFromWeight()
	{
		return mWeightOfSlackliner * GRAVITY_ACCELERATION;
	}

	private double calculateSagFromAnchorForce()
	{
		mSag = Math.sqrt((mVerticalForce * mVerticalForce * mLength * mLength) /
				(4 * (4 * mAnchorForce * mAnchorForce - mVerticalForce * mVerticalForce)));
		return mSag;
	}

	/**
	 * normally the mSag is calculated by the current AnchorForce. As the relationship between
	 * the mAnchorForce and the Pretension is dependent on the mSag, the mPretension will change,too.
	 * If you want to keep the mPretension constant you should use this method. As the mathematical
	 * formula for this calculation is much more complicated an iterative approach is used. The
	 * Pretension is guarantied to change less than 0.1 N.
	 *
	 * @return the calculated mSag
	 */
	private double calculateSagFromPretension()
	{
		if (mPretension <= 0 && mSagWithoutSlacker > 0)
			return calculateSagFromSagWithoutSlacker();

		mSag = iterativeApproximation(new SagFromPretensionIterative(), 1e-4, 1e4);

		//calculateAnchorForce();

		return mSag;
	}

	private double calculateSagFromSagWithoutSlacker()
	{
		if(mSagWithoutSlacker <= 0 && mPretension > 0)
		{
			return calculateSagFromPretension();
		}
		mSag = iterativeApproximation(new SagFromSagWithoutSlackerIterative(), 1e-6, 1e6);
		//calculateAnchorForce();
		return mSag;

	}

	private double calculateLengthFromAnchorForce()
	{
		double anchorForce = getmAnchorForce();
		mLength =  Math.sqrt((4 * mSag * (4 * anchorForce * anchorForce - mVerticalForce * mVerticalForce)) /
				(mVerticalForce * mVerticalForce));
		return mLength;
	}

	private double calculateLengthFromPretension()
	{
		if (mPretension <= 0 && mSagWithoutSlacker > 0)
			return calculateLengthFromSagWithoutSlackliner();

		mLength = iterativeApproximation(new LengthFromPretensionIterative(), 1e-6, 1e6);

		//calculateAnchorForce();
		//calculatePretensionFromAnchorForce();

		return mLength;
	}

	private double calculateLengthFromSagWithoutSlackliner()
	{
		if (mSagWithoutSlacker <= 0 && mPretension > 0)
			return calculateLengthFromPretension();


		mLength = iterativeApproximation(new LengthFromSagWithoutSlackerIterative(), 1e-4, 1e4);
		//calculateAnchorForce();
		return mLength;
	}


	private double calculatePretensionFromAnchorForce()
	{
		if(mWebbing == null || mWebbing.getStretchCoefficient() == 0)
		{
			if(mSag > 0)
			{
				mPretension = 0;
				mSagWithoutSlacker = mSag;
			}
			else
				mPretension = mAnchorForce;
		}
		else
		{
			mPretension = mAnchorForce - ((2 * Math.sqrt(mSag * mSag + mLength * mLength / 4) - mLength) / (mWebbing.getStretchCoefficient() * mLength));

		}
		if (mPretension <= 0)
		{
			mPretension = 0;
			mSagWithoutSlacker = Math.sqrt( (Math.pow(mLength,2)/4 + mSag * mSag) / Math.pow(1 + mWebbing.getStretchCoefficient() * mAnchorForce,2) - Math.pow(mLength,2)/4);
		}
		else
			mSagWithoutSlacker = 0;
		return mPretension;
	}

	/**
	 * This method calculates the AnchorForce from the mPretension of the Line. For the calculation
	 * a constant mLength and mSag are assumed. Therefore it is not possible to use that function
	 * for calculating the mLength or the mSag of the line.
	 * @return
	 */
	private double calculateAnchorForceFromPretension()
	{

		if(mPretension == 0 && mSagWithoutSlacker > 0)
			return calculateAnchorForceFromSagWithoutSlackliner();

		if(mWebbing == null || mWebbing.getStretchCoefficient() == 0)
		{
			mAnchorForce = mPretension;
		}
		else
		{
			mAnchorForce = mPretension + ((2 * Math.sqrt(mSag * mSag + mLength * mLength / 4) - mLength) / (mWebbing.getStretchCoefficient() * mLength));
		}

		return mAnchorForce;
	}

	/**
	 * This method calculates the AnchorForce from the mSag without a Slacker on a rodeo line. For the calculation
	 * a constant mLength and mSag are assumed. Therefore it is not possible to use that function
	 * for calculating the mLength or the mSag of the line.
	 * @return
	 */
	private double calculateAnchorForceFromSagWithoutSlackliner()
	{
		if(mSagWithoutSlacker == 0 && mPretension > 0)
			return calculateAnchorForceFromPretension();

		if(mWebbing == null || mWebbing.getStretchCoefficient() == 0)
		{
			mAnchorForce = (Math.sqrt(Math.pow(mSagWithoutSlacker, 2) + Math.pow(mLength, 2) / 4) * mVerticalForce) / (2* mSagWithoutSlacker);
		}
		else
			mAnchorForce = (Math.sqrt( (Math.pow(mLength,2)/4 + mSag * mSag) / (Math.pow(mSagWithoutSlacker, 2) + Math.pow(mLength,2)/4 )) - 1) / mWebbing.getStretchCoefficient();

		if(mAnchorForce < 0)
			mAnchorForce = 0;

		return mAnchorForce;
	}

	interface itarativeApproximationFunction
	{
		double getFunctionValue(double x);
	}

	class LengthFromPretensionIterative implements itarativeApproximationFunction {
		@Override
		public double getFunctionValue(double length) {
			double l1 = Math.sqrt(mSag * mSag + length*length/4);
			return l1 * (mVerticalForce /(2* mSag) - 2/(mWebbing.getStretchCoefficient()*length)) - mPretension + 1/ mWebbing.getStretchCoefficient();
		}
	}
	class SagFromPretensionIterative implements itarativeApproximationFunction {
		@Override
		public double getFunctionValue(double sag) {
			double l1 = Math.sqrt(sag*sag + mLength * mLength /4);
			return l1 * (mVerticalForce /(2*sag) - 2/(mWebbing.getStretchCoefficient()* mLength)) - mPretension + 1/ mWebbing.getStretchCoefficient();
		}
	}

	class LengthFromSagWithoutSlackerIterative implements itarativeApproximationFunction {
		@Override
		public double getFunctionValue(double length) {
			double l1 = Math.sqrt(mSagWithoutSlacker * mSagWithoutSlacker + length*length/4);
			double l2 = Math.sqrt(mSag * mSag + length*length/4);
			double stretch = mWebbing.getStretchCoefficient();
			return (l2/l1 - 1) / stretch - l2 * mVerticalForce / (2* mSag);
		}
	}
	class SagFromSagWithoutSlackerIterative implements itarativeApproximationFunction {
		@Override
		public double getFunctionValue(double sag) {
			double l1 = Math.sqrt(mSagWithoutSlacker * mSagWithoutSlacker + mLength * mLength /4);
			double l2 = Math.sqrt(sag*sag + mLength * mLength /4);
			double stretch = mWebbing.getStretchCoefficient();
			return (l2/l1 - 1) / stretch - l2 * mVerticalForce / (2*sag);
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

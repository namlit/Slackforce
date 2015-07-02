package slacklib;

import static java.lang.System.out;
import java.io.*;
import java.math.*;
import java.util.*;

import slacklib.Manufacturer;
import slacklib.Webbing;

public class SlacklineBounceSimulations
{
	private final double gravityAcceleration = 9.81;
	
	private SlacklineCalculations slackCalc;
	private double speedOfSlackliner; // direction downwards
	private double verticalPositionOfSlackliner; // corresponds to -sag of slackline for positive values
	private double deltaT = 0.0001;
	/*
	 * deltaT:
	 * Test: 30s simalation, length=50m, sag=2m, weight=80kg, heightOfFall=1m, Webbing=sonic2.0
	 * deltaT = 0.01: 1.67m deviation in max height at 27.04s (9 jumps)
	 * deltaT = 0.001: 0.144m deviation in max height at 28,7s
	 * deltaT = 0.0001: 0.014m deviation in max height at 28,58s
	 * deltaT = 0.00005: 0.007m deviation in max height at 28.57s (10 jumps)
	 */
	
	public SlacklineBounceSimulations()
	{
		slackCalc = new SlacklineCalculations();
		//deltaT = 0.001;
	}
	
	public SlacklineBounceSimulations(SlacklineCalculations slackCalc)
	{
		this.slackCalc = slackCalc;
		//deltaT = 0.001;
	}
	
	private double getKineticEnergy()
	{
		return 0.5 * slackCalc.getWeightOfSlackliner() * speedOfSlackliner * speedOfSlackliner;
	}
	
	private void setSpeedFromEkin(double Ekin)
	{
		speedOfSlackliner = Math.sqrt((2 * Ekin) / slackCalc.getWeightOfSlackliner());
	}
	
	private void iterationStep()
	{
		//double Ekin = 0.5 * slackCalc.getWeightOfSlackliner() * speedOfSlackliner * speedOfSlackliner;
		double m = slackCalc.getWeightOfSlackliner();
		slackCalc.setSag(-verticalPositionOfSlackliner);
		double F = m * gravityAcceleration - slackCalc.calculateVerticalForce(); // orientation downwards
		
		double a = F / m; // orientation downwards
		double deltaV = a * deltaT; // orientation downwards
		double deltaS = 0.5 * a * deltaT * deltaT + speedOfSlackliner * deltaT; // orientation downwards
		
		verticalPositionOfSlackliner -= deltaS;
		speedOfSlackliner += deltaV;
		
	}
	
	public double[] calculateMaximumForces(double heightOfFall)
	{
		if (heightOfFall < 0)
		{
			verticalPositionOfSlackliner = heightOfFall;
			speedOfSlackliner = 0;
		}
		else
		{
			verticalPositionOfSlackliner = 0;
			double Ekin = slackCalc.getWeightOfSlackliner() * gravityAcceleration * heightOfFall; // Ekin = Epot
			setSpeedFromEkin(Ekin);
		}
		
		do
		{
			iterationStep();
		} while (speedOfSlackliner > 0);

		double forces[] = new double[4];
		double vForce = slackCalc.getVerticalForce();
		forces[0] = vForce / (slackCalc.getWeightOfSlackliner() * slackCalc.GRAVITY_ACCELERATION);
		forces[1] = vForce;
		forces[2] = slackCalc.getAnchorForce();
		forces[3] = -verticalPositionOfSlackliner;
		return forces;
	}

	public void simulateJumping(double jumpHeight, double simulationTime)
	{
		double time[] = new double[(int)(simulationTime / deltaT) + 1];
		double verticalPos[] = new double[(int)(simulationTime / deltaT) + 1];
		double anchorForce[] = new double[(int)(simulationTime / deltaT) + 1];
		double verticalForce[] = new double[(int)(simulationTime / deltaT) + 1];
		
		verticalPositionOfSlackliner = jumpHeight;
		speedOfSlackliner = 0;
		
		int i = 0;
		for(double currentTime = 0; currentTime < simulationTime; currentTime += deltaT, i++)
		{
			iterationStep();
			time[i] = currentTime;
			verticalPos[i] = verticalPositionOfSlackliner;
			anchorForce[i] = slackCalc.getAnchorForce(); //calculateAnchorForce?
			verticalForce[i] = slackCalc.getVerticalForce();
			
			//out.printf("Time, Pos, Fv, Fh:\t%.3f\t%.3f\t%.0f\t%.0f\n", currentTime, verticalPositionOfSlackliner, slackCalc.getVerticalForce(), slackCalc.getAnchorForce());
		}
		writeSimulationResultToCSVFile(time, verticalPos, anchorForce, verticalForce, "simulation.csv");
	}
	
	private void writeSimulationResultToCSVFile(double time[], double verticalPos[], double anchorForce[],
			double verticalForce[], String filename)
	{
		//File csvFile = new File(filename);
		
		Writer csvFile = null;

		try
		{
			csvFile = new FileWriter( filename );
			for(int i = 0; i < time.length; i++)
			{
				csvFile.append( time[i] + "," + verticalPos[i] + "," + verticalForce[i] + "," + anchorForce[i] + "\n");
			}
			
		}
		catch ( IOException e ) {
		  System.err.println( "Konnte Datei nicht erstellen" );
		}
		finally {
		  if ( csvFile != null )
		    try { csvFile.close(); } catch ( IOException e ) { e.printStackTrace(); }
		}
		
	}
	
	public SlacklineCalculations getSlackCalc()
	{
		return slackCalc;
	}

	public void setSlackCalc(SlacklineCalculations slackCalc)
	{
		this.slackCalc = slackCalc;
	}

	public double getSpeedOfSlackliner()
	{
		return speedOfSlackliner;
	}

	public void setSpeedOfSlackliner(double speedOfSlackliner)
	{
		this.speedOfSlackliner = speedOfSlackliner;
	}

	public double getVerticalPositionOfSlackliner()
	{
		return verticalPositionOfSlackliner;
	}

	public void setVerticalPositionOfSlackliner(double verticalPositionOfSlackliner)
	{
		this.verticalPositionOfSlackliner = verticalPositionOfSlackliner;
	}

	public double getDeltaT()
	{
		return deltaT;
	}

	public void setDeltaT(double deltaT)
	{
		this.deltaT = deltaT;
	}
	
}

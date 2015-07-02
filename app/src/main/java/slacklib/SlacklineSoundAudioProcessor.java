package slacklib;

/**
 * Created by tilman on 28.06.15.
 */

import java.io.*;
import WavFile.*;

public class SlacklineSoundAudioProcessor {

    private final short THRESHOLD = (short) (0.9 * 32767);
    private final int MAX_NUMBER_OF_PEAKS = 5;
    private final int MINIMUM_NUMBER_OF_BOUNCES = 2;
    private final double BOUNCING_TIME = 0.08;
    private final double COMPRESSION_TIME = 0.002;
//    private final double MAX_TIME_BETWEEN_PEAKS = 2;
    private final double MAX_VARIATION_TIME_BEETWEEN_PEAKS = BOUNCING_TIME;

    enum ProcessStatus {
        STOPPED, ACTIVE, WAIT_FOT_DEBOUNCE
    }

    private double framerate;
    private double currentTime;
    private double debounceFinishedTime;
    private double compressionFinishedTime = 0;
    private double peakTimes[];
    private double timeOfOscillation;
    private int numberOfDetectedPeaks;
    private int numberOfBounces;
    private int requiredNumberOfPeaks = 4;
    private ProcessStatus processStatus = ProcessStatus.STOPPED;

    public SlacklineSoundAudioProcessor(double framerate)
    {
        this.framerate = framerate;
        this.currentTime = 0;
        this.debounceFinishedTime = 0;
        this.peakTimes = new double [MAX_NUMBER_OF_PEAKS];
        this.timeOfOscillation = 0;
        this.numberOfDetectedPeaks = 0;
        processStatus = ProcessStatus.STOPPED;
    }

    public boolean process(short[] audioData, int offsetInShorts, int sizeInShorts)
    {
        short currentSamplingPoint;

        if (currentTime == 0)
            processStatus = ProcessStatus.ACTIVE;

        for( int i = offsetInShorts; i < sizeInShorts; i++)
        {
            currentSamplingPoint = audioData[i];
            processSamplingPoint(currentSamplingPoint);
            currentTime += 1/framerate;

            if (processStatus == ProcessStatus.STOPPED)
                break;
        }

        if (numberOfDetectedPeaks >= requiredNumberOfPeaks)
        {
            return calculateTimeOfOscillation();
        }

        return false;
    }

    public double getTimeOfOscillation()
    {
        return timeOfOscillation;
    }

    public void reset()
    {
        currentTime = 0;
        numberOfDetectedPeaks = 0;
        processStatus = ProcessStatus.STOPPED;
    }

    public boolean processFromFile(String filename)
    {
        try {

            WavFile wavFile = WavFile.openWavFile( new File(filename) );

            int numChannels = wavFile.getNumChannels();
            final int BUFFERSIZE = 1000;
            short buffer[] = new short[BUFFERSIZE * numChannels];
            int numberOfFrames = 0;

            do {

                numberOfFrames = wavFile.readFrames(buffer, 0, BUFFERSIZE);
            } while (!process(buffer, 0, numberOfFrames));


        } catch (Throwable t) {

            t.printStackTrace();
        }
        return true;
    }

    private void processSamplingPoint(short samplingPoint)
    {
        switch (processStatus)
        {
            case STOPPED:
                return;
            case WAIT_FOT_DEBOUNCE:
                if (samplingPoint > THRESHOLD && currentTime > compressionFinishedTime)
                {
                    numberOfBounces++;
                    compressionFinishedTime = currentTime + COMPRESSION_TIME;
                }
                if(currentTime >= debounceFinishedTime) {
                    if (numberOfBounces < MINIMUM_NUMBER_OF_BOUNCES)
                        numberOfDetectedPeaks--; // ignore Last Peak
                    processStatus = ProcessStatus.ACTIVE;
                }
                return;

        }
        if (samplingPoint > THRESHOLD)
        {
            if (timeVariationToLastMeasurement(currentTime) > MAX_VARIATION_TIME_BEETWEEN_PEAKS)
                numberOfDetectedPeaks = 0;

            adjustRequiredNumberOfPeaks();

            peakTimes[numberOfDetectedPeaks] = currentTime;
            numberOfDetectedPeaks++;
            debounceFinishedTime = currentTime + BOUNCING_TIME;
            compressionFinishedTime = currentTime + COMPRESSION_TIME;
            processStatus = ProcessStatus.WAIT_FOT_DEBOUNCE;
            numberOfBounces = 1;

            if (numberOfDetectedPeaks >= requiredNumberOfPeaks)
                processStatus = ProcessStatus.STOPPED;
        }
    }

    private double timeVariationToLastMeasurement(double peaktime)
    {
        if (numberOfDetectedPeaks <= 1)
            return 0;
        double timeDifferenceBetweenLastPeaks = peakTimes[numberOfDetectedPeaks-1] - peakTimes[numberOfDetectedPeaks-2];
        double timeDifferenceToLastPeak = peaktime - peakTimes[numberOfDetectedPeaks-1];
        return Math.abs(timeDifferenceToLastPeak - timeDifferenceBetweenLastPeaks);
    }

    private double distanceToLastPeak(double peaktime)
    {
        if (numberOfDetectedPeaks <= 0)
            return 0;
        return peaktime - peakTimes[numberOfDetectedPeaks-1];
    }

    private boolean calculateTimeOfOscillation()
    {
        if(numberOfDetectedPeaks < requiredNumberOfPeaks)
            return false;

        double timesBeetweenPeaks[] = new double[requiredNumberOfPeaks -1];

        for (int i = 1; i < requiredNumberOfPeaks; i++)
        {
            timesBeetweenPeaks[i-1] = peakTimes[i] - peakTimes[i-1];
        }
        for ( int i = 1; i < timesBeetweenPeaks.length; i++)
        {
            if (timesBeetweenPeaks[i] - timesBeetweenPeaks[i-1] > MAX_VARIATION_TIME_BEETWEEN_PEAKS)
            {
                reset();
                return false;
            }

        }

        double timeOfOscillationSum = 0;

        for(double time : timesBeetweenPeaks)
        {
            timeOfOscillationSum += time;
        }

        timeOfOscillation = timeOfOscillationSum / timesBeetweenPeaks.length;
        reset();
        return true;
    }

    private void adjustRequiredNumberOfPeaks()
    {
        double distanceToLastPeak = distanceToLastPeak(currentTime);

        requiredNumberOfPeaks = MAX_NUMBER_OF_PEAKS;

        if (distanceToLastPeak > 0.2)
            requiredNumberOfPeaks = 4;

        if (distanceToLastPeak > 0.28)
            requiredNumberOfPeaks = 3;
    }
}

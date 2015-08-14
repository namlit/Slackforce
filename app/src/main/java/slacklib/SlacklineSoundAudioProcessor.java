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

    private double mFramerate;
    private double mCurrentTime;
    private double mDebounceFinishedTime;
    private double mCompressionFinishedTime = 0;
    private double mPeakTimes[];
    private double mTimeOfOscillation;
    private int mNumberOfDetectedPeaks;
    private int mNumberOfBounces;
    private int mRequiredNumberOfPeaks = 4;
    private ProcessStatus mProcessStatus = ProcessStatus.STOPPED;

    public SlacklineSoundAudioProcessor(double framerate)
    {
        this.mFramerate = framerate;
        this.mCurrentTime = 0;
        this.mDebounceFinishedTime = 0;
        this.mPeakTimes = new double [MAX_NUMBER_OF_PEAKS];
        this.mTimeOfOscillation = 0;
        this.mNumberOfDetectedPeaks = 0;
        mProcessStatus = ProcessStatus.STOPPED;
    }

    public boolean process(short[] audioData, int offsetInShorts, int sizeInShorts)
    {
        short currentSamplingPoint;

        if (mCurrentTime == 0)
            mProcessStatus = ProcessStatus.ACTIVE;

        for( int i = offsetInShorts; i < sizeInShorts; i++)
        {
            currentSamplingPoint = audioData[i];
            processSamplingPoint(currentSamplingPoint);
            mCurrentTime += 1/ mFramerate;

            if (mProcessStatus == ProcessStatus.STOPPED)
                break;
        }

        if (mNumberOfDetectedPeaks >= mRequiredNumberOfPeaks)
        {
            return calculateTimeOfOscillation();
        }

        return false;
    }

    public double getTimeOfOscillation()
    {
        return mTimeOfOscillation;
    }

    public void reset()
    {
        mCurrentTime = 0;
        mNumberOfDetectedPeaks = 0;
        mProcessStatus = ProcessStatus.STOPPED;
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
                if(numberOfFrames == 0)
                    return false;
            } while (!process(buffer, 0, numberOfFrames));


        } catch (Throwable t) {

            t.printStackTrace();
        }
        return true;
    }

    private void processSamplingPoint(short samplingPoint)
    {
        switch (mProcessStatus)
        {
            case STOPPED:
                return;
            case WAIT_FOT_DEBOUNCE:
                if (samplingPoint > THRESHOLD && mCurrentTime > mCompressionFinishedTime)
                {
                    mNumberOfBounces++;
                    mCompressionFinishedTime = mCurrentTime + COMPRESSION_TIME;
                }
                if(mCurrentTime >= mDebounceFinishedTime) {
                    if (mNumberOfBounces < MINIMUM_NUMBER_OF_BOUNCES)
                        mNumberOfDetectedPeaks--; // ignore Last Peak
                    mProcessStatus = ProcessStatus.ACTIVE;
                }
                return;

        }
        if (samplingPoint > THRESHOLD)
        {
            if (timeVariationToLastMeasurement(mCurrentTime) > MAX_VARIATION_TIME_BEETWEEN_PEAKS)
                mNumberOfDetectedPeaks = 0;

            adjustRequiredNumberOfPeaks();

            mPeakTimes[mNumberOfDetectedPeaks] = mCurrentTime;
            mNumberOfDetectedPeaks++;
            mDebounceFinishedTime = mCurrentTime + BOUNCING_TIME;
            mCompressionFinishedTime = mCurrentTime + COMPRESSION_TIME;
            mProcessStatus = ProcessStatus.WAIT_FOT_DEBOUNCE;
            mNumberOfBounces = 1;

            if (mNumberOfDetectedPeaks >= mRequiredNumberOfPeaks)
                mProcessStatus = ProcessStatus.STOPPED;
        }
    }

    private double timeVariationToLastMeasurement(double peaktime)
    {
        if (mNumberOfDetectedPeaks <= 1)
            return 0;
        double timeDifferenceBetweenLastPeaks = mPeakTimes[mNumberOfDetectedPeaks -1] - mPeakTimes[mNumberOfDetectedPeaks -2];
        double timeDifferenceToLastPeak = peaktime - mPeakTimes[mNumberOfDetectedPeaks -1];
        return Math.abs(timeDifferenceToLastPeak - timeDifferenceBetweenLastPeaks);
    }

    private double distanceToLastPeak(double peaktime)
    {
        if (mNumberOfDetectedPeaks <= 0)
            return 0;
        return peaktime - mPeakTimes[mNumberOfDetectedPeaks -1];
    }

    private boolean calculateTimeOfOscillation()
    {
        if(mNumberOfDetectedPeaks < mRequiredNumberOfPeaks)
            return false;

        double timesBeetweenPeaks[] = new double[mRequiredNumberOfPeaks -1];

        for (int i = 1; i < mRequiredNumberOfPeaks; i++)
        {
            timesBeetweenPeaks[i-1] = mPeakTimes[i] - mPeakTimes[i-1];
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

        mTimeOfOscillation = timeOfOscillationSum / timesBeetweenPeaks.length;
        reset();
        return true;
    }

    private void adjustRequiredNumberOfPeaks()
    {
        double distanceToLastPeak = distanceToLastPeak(mCurrentTime);

        mRequiredNumberOfPeaks = MAX_NUMBER_OF_PEAKS;

        if (distanceToLastPeak > 0.2)
            mRequiredNumberOfPeaks = 4;

        if (distanceToLastPeak > 0.28)
            mRequiredNumberOfPeaks = 3;
    }
}

package slacklib;

/**
 * Created by tilman on 28.06.15.
 */

import java.io.*;
import WavFile.*;

public class SlacklineSoundAudioProcessor {

    private final short MAX_AUDIO_VALUE = (short) 32767;
    private final double MAXIMUM_DAMPING_FACTOR = 0.4;
    private final int MAX_NUMBER_OF_PEAKS = 5;
    private final int MINIMUM_NUMBER_OF_BOUNCES = 2;
    private final double BOUNCING_TIME = 0.08;
    //private final double COMPRESSION_TIME = 0.002;
//    private final double MAX_TIME_BETWEEN_PEAKS = 2;
    private final double MAX_VARIATION_TIME_BEETWEEN_PEAKS = BOUNCING_TIME;

    enum ProcessStatus {
        STOPPED, ACTIVE, WAIT_FOT_DEBOUNCE
    }

    private double mFramerate;
    private double mCurrentTime;
    private short mCurrentThreshold;
    private double mDebounceFinishedTime;
    private int mCompressionCount = 0;
    private boolean mIsSignChangeDetected = false;
    private double mPeakTimes[];
    private double mTimeOfOscillation;
    private int mNumberOfDetectedPeaks;
    private int mNumberOfBounces;
    private int mRequiredNumberOfPeaks = 4;
    private int mRequiredNumberOfBounces;
    private short mMaximumValueDuringDebounce;
    private double mTimeOfMaximumValueDuringDebounce;
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

        if (mCurrentTime == 0) {
            mProcessStatus = ProcessStatus.ACTIVE;
            mCurrentThreshold = (short) (MAXIMUM_DAMPING_FACTOR * MAX_AUDIO_VALUE);
            mRequiredNumberOfBounces = MINIMUM_NUMBER_OF_BOUNCES;
        }

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
                processSamplingPointsDuringDebounce(samplingPoint);
                return;

        }
        if (samplingPoint > mCurrentThreshold)
        {
            mTimeOfMaximumValueDuringDebounce = mCurrentTime;
            mMaximumValueDuringDebounce = samplingPoint;
            mDebounceFinishedTime = mCurrentTime + BOUNCING_TIME;
            //mCompressionFinishedTime = mCurrentTime + COMPRESSION_TIME;
            mIsSignChangeDetected = false;
            mCompressionCount = 0;
            mProcessStatus = ProcessStatus.WAIT_FOT_DEBOUNCE;
            mNumberOfBounces = 1;


        }
    }

    private void processSamplingPointsDuringDebounce(short samplingPoint)
    {
        if (samplingPoint > mMaximumValueDuringDebounce) {
            mMaximumValueDuringDebounce = samplingPoint;
            mTimeOfMaximumValueDuringDebounce = mCurrentTime;
        }

        if (samplingPoint == MAX_AUDIO_VALUE)
            mCompressionCount++;

        if(samplingPoint < 0)
        {
            mIsSignChangeDetected = true;
        }

        if (samplingPoint > mCurrentThreshold && mIsSignChangeDetected)
        {
            mNumberOfBounces++;
            mIsSignChangeDetected = false;
            //mCompressionFinishedTime = mCurrentTime + COMPRESSION_TIME;
        }
        if(mCurrentTime >= mDebounceFinishedTime) {

            if (timeVariationToLastMeasurement(mTimeOfMaximumValueDuringDebounce) > MAX_VARIATION_TIME_BEETWEEN_PEAKS)
                mNumberOfDetectedPeaks = 0;

            adjustRequiredNumberOfPeaks(mTimeOfMaximumValueDuringDebounce);
            mCurrentThreshold = (short) (MAXIMUM_DAMPING_FACTOR * mMaximumValueDuringDebounce);
            if(mCompressionCount > 20)
                mCurrentThreshold = (short) (0.7 * mMaximumValueDuringDebounce);
            mRequiredNumberOfBounces = (int) (0.6 * mNumberOfBounces);
            if(mRequiredNumberOfBounces < MINIMUM_NUMBER_OF_BOUNCES)
                mRequiredNumberOfBounces = MINIMUM_NUMBER_OF_BOUNCES;

            mPeakTimes[mNumberOfDetectedPeaks] = mTimeOfMaximumValueDuringDebounce;
            mNumberOfDetectedPeaks++;

            if (mNumberOfBounces < mRequiredNumberOfBounces)
                mNumberOfDetectedPeaks--; // ignore Last Peak

            mProcessStatus = ProcessStatus.ACTIVE;

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

    private void adjustRequiredNumberOfPeaks(double timeOfLastPeak)
    {
        double distanceToLastPeak = distanceToLastPeak(timeOfLastPeak);

        mRequiredNumberOfPeaks = MAX_NUMBER_OF_PEAKS;

        if (distanceToLastPeak > 0.2)
            mRequiredNumberOfPeaks = 4;

        if (distanceToLastPeak > 0.28)
            mRequiredNumberOfPeaks = 3;
    }
}

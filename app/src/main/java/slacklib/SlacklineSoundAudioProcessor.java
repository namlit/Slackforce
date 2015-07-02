package slacklib;

/**
 * Created by tilman on 28.06.15.
 */
public class SlacklineSoundAudioProcessor {

    private final short THRESHOLD = (short) (0.9 * 32767);
    private final int REQUIRED_NUMBER_OF_PEAKS = 4;
    private final double BOUNCING_TIME = 0.08;
    private final double MAX_TIME_BETWEEN_PEAKS = 2;
    private final double MAX_VARIATION_TIME_BEETWEEN_PEAKS = BOUNCING_TIME;

    enum ProcessStatus {
        STOPPED, ACTIVE, WAIT_FOT_DEBOUNCE
    }

    private double framerate;
    private double currentTime;
    private double debounceFinishedTime;
    private double peakTimes[];
    private double timeOfOscillation;
    private int numberOfDetectedPeaks;
    private ProcessStatus processStatus = ProcessStatus.STOPPED;

    public SlacklineSoundAudioProcessor(double framerate)
    {
        this.framerate = framerate;
        this.currentTime = 0;
        this.debounceFinishedTime = 0;
        this.peakTimes = new double [REQUIRED_NUMBER_OF_PEAKS];
        this.timeOfOscillation = 0;
        this.numberOfDetectedPeaks = 0;
        processStatus = ProcessStatus.STOPPED;
    }

    public boolean process(short[] audioData, int offsetInShorts, int sizeInShorts)
    {
        short currentSamplingPoint;
        processStatus = ProcessStatus.ACTIVE;

        for( int i = offsetInShorts; i < sizeInShorts; i++)
        {
            currentSamplingPoint = audioData[i];
            processSamplingPoint(currentSamplingPoint);
            currentTime += 1/framerate;

            if (processStatus == ProcessStatus.STOPPED)
                break;
        }

        if (numberOfDetectedPeaks >= REQUIRED_NUMBER_OF_PEAKS)
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

    private void processSamplingPoint(short samplingPoint)
    {
        switch (processStatus)
        {
            case STOPPED:
                return;
            case WAIT_FOT_DEBOUNCE:
                if(currentTime >= debounceFinishedTime)
                    processStatus = ProcessStatus.ACTIVE;
                return;

        }
        if (samplingPoint > THRESHOLD)
        {
            if (distanceToLastPeak(currentTime) > MAX_TIME_BETWEEN_PEAKS)
                numberOfDetectedPeaks = 0;

            peakTimes[numberOfDetectedPeaks] = currentTime;
            numberOfDetectedPeaks++;
            debounceFinishedTime = currentTime + BOUNCING_TIME;
            processStatus = ProcessStatus.WAIT_FOT_DEBOUNCE;

            if (numberOfDetectedPeaks == REQUIRED_NUMBER_OF_PEAKS)
                processStatus = ProcessStatus.STOPPED;
        }
    }

    private double distanceToLastPeak(double peaktime)
    {
        if (numberOfDetectedPeaks <= 0)
            return 0;
        return peaktime - peakTimes[numberOfDetectedPeaks-1];
    }

    private boolean calculateTimeOfOscillation()
    {
        if(numberOfDetectedPeaks < REQUIRED_NUMBER_OF_PEAKS)
            return false;

        double timesBeetweenPeaks[] = new double[REQUIRED_NUMBER_OF_PEAKS-1];

        for (int i = 1; i < REQUIRED_NUMBER_OF_PEAKS; i++)
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
}

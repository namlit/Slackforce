package namlit.slackforce;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import slacklib.SlacklineSoundAudioProcessor;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MeasureForceAutomaticallyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MeasureForceAutomaticallyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeasureForceAutomaticallyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters

    private Button mAbortButton;
    private TextView mStatusText;

    private final int mFrameRate =44100;
    final SlacklineSoundAudioProcessor mAudioProcessor = new SlacklineSoundAudioProcessor(mFrameRate);
    private AudioRecord mAudioRecord = null;
    private Thread mAudioThread = null;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MeasureForceAutomaticallyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MeasureForceAutomaticallyFragment newInstance() {
        MeasureForceAutomaticallyFragment fragment = new MeasureForceAutomaticallyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MeasureForceAutomaticallyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_measure_force_automatically, container, false);

        mAbortButton = (Button) view.findViewById(R.id.abortButton);

        mAbortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abortMeasurement();
            }
        });

        startMeasurement();

        return view;
    }

//    public interface OnMeasurementResultListener
//    {
//        public void onMeasurementResult(double timeOfOscillation);
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }



    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        freeAudioResources();
    }


    @Override
    public void onStop()
    {
        super.onStop();
        //stopMeasuring();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        freeAudioResources();
    }




    //private AudioManager audioManager=null;
    //private AudioTrack audioTrack=null;
    //byte[] buffer = new byte[freq];

    public void startMeasurement()
    {

        mAudioThread = new Thread(new Runnable() {
            public void run() {

                int numberOfShortsWritten = 0;


                try {

                    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
                    final int bufferSize = AudioRecord.getMinBufferSize(mFrameRate,
                            AudioFormat.CHANNEL_IN_MONO,
                            AudioFormat.ENCODING_PCM_16BIT);



                    mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, mFrameRate,
                            AudioFormat.CHANNEL_IN_MONO,
                            AudioFormat.ENCODING_PCM_16BIT, bufferSize);



                    final short[] buffer = new short[bufferSize];
                    mAudioRecord.startRecording();


                    do  {
                    numberOfShortsWritten = mAudioRecord.read(buffer, 0, bufferSize);

                    } while (!mAudioProcessor.process(buffer, 0, numberOfShortsWritten));



                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((MeasureForceFragment) getParentFragment()).onMeasurementResult(mAudioProcessor.getTimeOfOscillation());
                    }
                });

                } catch (Throwable t) {

                    //Toast.makeText(getActivity(), getString(R.string.audio_resource_error), Toast.LENGTH_LONG).show();
                    //abortMeasurement();
                    t.printStackTrace();
                }

                finally {
                    freeAudioResources();
                }

            }
        });


        mAudioThread.start();


    }

    private void abortMeasurement()
    {
        ((MeasureForceFragment) getParentFragment()).abortAutomaticMeasurement();
    }

    private void freeAudioResources()
    {
        if (mAudioThread != null)
            mAudioThread.interrupt();

        if (mAudioRecord != null)
        {
            //mAudioRecord.stop();
            mAudioRecord.release();
            mAudioRecord = null;
        }
        if (mAudioProcessor != null)
            mAudioProcessor.reset();
    }
}

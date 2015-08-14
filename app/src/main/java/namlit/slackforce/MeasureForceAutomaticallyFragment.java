package namlit.slackforce;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

import slacklib.Manufacturer;
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

    static final int GET_AUDIO_RECORDING_REQUEST = 1;

    private OnMeasurementResultListener mListener;

    private Button mStartStopButton;
    private TextView mStatusText;
    private boolean mIsMeasuring = false;

    private final int mFrameRate =44100;
    final SlacklineSoundAudioProcessor mAudioProcessor = new SlacklineSoundAudioProcessor(mFrameRate);
    private AudioRecord mAudioRecord = null;
    private Thread mAudioThread = null;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MeasureForceAutomaticallyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MeasureForceAutomaticallyFragment newInstance(boolean isMeasuring) {
        MeasureForceAutomaticallyFragment fragment = new MeasureForceAutomaticallyFragment();
        Bundle args = new Bundle();
        args.putBoolean("ISMEASURENG", isMeasuring);
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

        //mIsMeasuring = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_measure_force_automatically, container, false);

        mStartStopButton = (Button) view.findViewById(R.id.startStopButton);
        mStatusText = (TextView) view.findViewById(R.id.statusText);

        mStatusText.setText(R.string.measure_force_automatically__status_text_start_measurement);

//        if (mIsMeasuring)
//            mStartStopButton.setEnabled(false);

        mStartStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsMeasuring)
                    mIsMeasuring = false;
                else
                    startMeasurement();
            }
        });

        return view;
    }

//    public interface OnMeasurementResultListener
//    {
//        public void onMeasurementResult(double timeOfOscillation);
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnMeasurementResultListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMeasurementResultListener");
        }
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

//    @Override
//    public void onViewStateRestored(Bundle bundle) {
//        super.onViewStateRestored(bundle);
//
//    }




    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        stopMeasuring();
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
        stopMeasuring();
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == GET_AUDIO_RECORDING_REQUEST) {

            if (resultCode == Activity.RESULT_OK)
            {
                String filename = data.getData().getPath();
                processMeasurement(filename);
            }
        }
    }


    //private AudioManager audioManager=null;
    //private AudioTrack audioTrack=null;
    //byte[] buffer = new byte[freq];


    public void processMeasurement(final String filename)
    {
        mStartStopButton.setEnabled(false);
        //mStartStopButton.setText("Abort measurement");
        mStatusText.setText(R.string.measure_force_automatically__status_text_measuring);
        mIsMeasuring = true;

        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        final int bufferSize = AudioRecord.getMinBufferSize(mFrameRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);


        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, mFrameRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize);


        final short[] buffer = new short[bufferSize];
        mAudioRecord.startRecording();


        int numberOfShortsWritten = 0;

        try {

            mAudioProcessor.processFromFile(filename);

            if (mListener != null)
                mListener.onMeasurementResult(mAudioProcessor.getTimeOfOscillation());

            stopMeasuring();


        } catch (Throwable t) {

            t.printStackTrace();
        }


    }
    public void startMeasurement()
    {
        //Uri startDir = Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/Music/");
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //intent.setData(startDir);
        intent.setType("*/.wav");

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, GET_AUDIO_RECORDING_REQUEST);
        }
    }

    private void stopMeasuring()
    {
        mIsMeasuring = false;
        if (mAudioRecord != null)
        {
            //mAudioRecord.stop();
            mAudioRecord.release();
            mAudioRecord = null;
        }
        if (mAudioProcessor != null)
            mAudioProcessor.reset();
        if (mAudioThread != null)
            mAudioThread.interrupt();
        mStartStopButton.setEnabled(true);
        mStatusText.setText(R.string.measure_force_automatically__status_text_start_measurement);
    }

}

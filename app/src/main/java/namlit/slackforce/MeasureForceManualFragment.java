package namlit.slackforce;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MeasureForceManualFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MeasureForceManualFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeasureForceManualFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters

    public static int MAX_NUMBER_OF_TAPS = 4;

    private OnMeasurementResultListener mListener;
    private Button mRhythmButton;
    private double mTapStartTime;
    private int mNumberOfTaps = MAX_NUMBER_OF_TAPS;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MeasureForceManualFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MeasureForceManualFragment newInstance() {
        MeasureForceManualFragment fragment = new MeasureForceManualFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MeasureForceManualFragment() {
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
        View view = inflater.inflate(R.layout.fragment_measure_force_manual, container, false);

        mRhythmButton = (Button) view.findViewById(R.id.rhythmButton);
        mRhythmButton.setText("Tap " + MAX_NUMBER_OF_TAPS + " times");

        mRhythmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mNumberOfTaps == MAX_NUMBER_OF_TAPS)
                {
                    mTapStartTime = System.currentTimeMillis();
                }
                mNumberOfTaps--;

                if(mNumberOfTaps <= 0)
                {
                    double timeOfOscillation = (System.currentTimeMillis() - mTapStartTime) / (1000 * (MAX_NUMBER_OF_TAPS-1));
                    mNumberOfTaps = MAX_NUMBER_OF_TAPS;
                    mListener.onMeasurementResult(timeOfOscillation);
                }

                mRhythmButton.setText("Tap " + mNumberOfTaps + " times");


            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        public void onFragmentInteraction(Uri uri);
//    }


}


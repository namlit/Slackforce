package namlit.slackforce;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShowMeasurementResultFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ShowMeasurementResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowMeasurementResultFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    static public final String ARG_PRETENSION = "PRETENSION";

    // TODO: Rename and change types of parameters


    private Button mCopyValuesButton;
    private double mPretension = 2;
    private double mTimeOfOscillation = 0;
    private OnFragmentInteractionListener mListener;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShowMeasurementResultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShowMeasurementResultFragment newInstance() {
        ShowMeasurementResultFragment fragment = new ShowMeasurementResultFragment();
//        Bundle args = new Bundle();
//        args.putDouble(ARG_PRETENSION, pretension);
//        fragment.setArguments(args);
        return fragment;
    }

    public ShowMeasurementResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//       }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_measurement_result, container, false);

        mCopyValuesButton = (Button) view.findViewById(R.id.copyValuesButton);
        TextView forceText = (TextView) view.findViewById(R.id.resultText);
        forceText.setText(String.format("The Pretension is %.2f kN", mPretension / 1e3));
        TextView oscillationTimeText = (TextView) view.findViewById(R.id.timeOfOscillationText);
        oscillationTimeText.setText(String.format("The measured time of oscillation is %.3f s", mTimeOfOscillation));

        mCopyValuesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MeasureForceFragment) getParentFragment()).copyValues();
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public void setPretension(double pretension)
    {
        mPretension = pretension;
    }

    public void setmTimeOfOscillation(double timeOfOscillation)
    {
        mTimeOfOscillation = timeOfOscillation;
    }
}

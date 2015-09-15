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
 * {@link StartAutomaticMeasurementFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StartAutomaticMeasurementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StartAutomaticMeasurementFragment extends Fragment {

    private Button mStartButton;
    private TextView mStatusText;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment StartAutomaticMeasurementFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StartAutomaticMeasurementFragment newInstance() {
        StartAutomaticMeasurementFragment fragment = new StartAutomaticMeasurementFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public StartAutomaticMeasurementFragment() {
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
        View view = inflater.inflate(R.layout.fragment_start_automatic_measurement, container, false);

        mStartButton = (Button) view.findViewById(R.id.startButton);


        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MeasureForceFragment) getParentFragment()).startAutomaticMeasurement();
            }
        });

        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}

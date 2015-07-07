package namlit.slackforce;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.Locale;

import slacklib.SlacklineMeasurements;
import slacklib.*;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MeasureForceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MeasureForceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeasureForceFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters

    static final int GET_WEBBING_REQUEST = 1;

    public enum Parameter
    {
        STRETCH, LINE_WEIGHT, LENGTH
    }

    private OnFragmentInteractionListener mListener;
    private Button mWebbingButton;
    private EditText mStretch;
    private EditText mLineWeight;
    private EditText mLength;
    private RadioButton mMeasureAutomaticallyButton;
    private RadioButton mMeasureManualButton;

    private boolean mIsMeasureAutomatically;

    //private Parameter mParameterChanged = Parameter.LENGTH;
    //private Button mBackToMeasurementButton;

    private SlacklineMeasurements mSlacklineMeasurements;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MeasureForceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MeasureForceFragment newInstance() {
        MeasureForceFragment fragment = new MeasureForceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MeasureForceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        mSlacklineMeasurements = new SlacklineMeasurements();

        restoreParameters();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_measure_force, container, false);

        mWebbingButton = (Button) view.findViewById(R.id.webbing);
        mStretch = (EditText) view.findViewById(R.id.stretch);
        mLineWeight = (EditText) view.findViewById(R.id.lineWeight);
        mLength = (EditText) view.findViewById(R.id.length);
        mMeasureAutomaticallyButton = (RadioButton) view.findViewById(R.id.automaticButton);
        mMeasureManualButton = (RadioButton) view.findViewById(R.id.manualButton);
        //mBackToMeasurementButton = (Button) view.findViewById(R.id.backToMeasurementButton);

        updateMeasureFragment();
        updateTextFields();

        mMeasureAutomaticallyButton.setChecked(mIsMeasureAutomatically);
        mMeasureManualButton.setChecked(!mIsMeasureAutomatically);

        mMeasureAutomaticallyButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsMeasureAutomatically = isChecked;
                updateMeasureFragment();
            }
        });

//        mMeasureManualButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                updateMeasureFragment();
//            }
//        });

        mWebbingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SelectWebbingManufacturerActivity.class);
                startActivityForResult(intent, GET_WEBBING_REQUEST);
            }
        });

        mStretch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    //mParameterChanged = Parameter.STRETCH;
                    update();
                }
            }
        });
        mStretch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //mParameterChanged = Parameter.STRETCH;
                update();
                return true;
            }
        });
        mLineWeight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    //mParameterChanged = Parameter.LINE_WEIGHT;
                    update();
                }
            }
        });
        mLineWeight.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //mParameterChanged = Parameter.LINE_WEIGHT;
                update();
                return true;
            }
        });
        mLength.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    //mParameterChanged = Parameter.LENGTH;
                    update();
                }
            }
        });
        mLength.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //mParameterChanged = Parameter.LENGTH;
                update();
                return true;
            }
        });

        setInfoClickListeners(view);

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
    public void onStop() {
        super.onStop();
        saveParameters();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == GET_WEBBING_REQUEST)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                int manufacturerID = data.getIntExtra("MANUFACTURER_ID", 0);
                int webbingID = data.getIntExtra("WEBBING_ID", 0);

                mSlacklineMeasurements.setWebbing(Manufacturer.getManufacturerByID(manufacturerID).getWebbingByID(webbingID));

                updateTextFields();

            }
        }
    }

    private void updateMeasureFragment()
    {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(mIsMeasureAutomatically)
        {
            MeasureForceAutomaticallyFragment fragment = new MeasureForceAutomaticallyFragment();
            //fragment.setRetainInstance(true);
            fragmentTransaction.replace(R.id.measureFragmentContainer, fragment);
        }
        else
        {
            MeasureForceManualFragment fragment = new MeasureForceManualFragment();
            //fragment.setRetainInstance(true);
            fragmentTransaction.replace(R.id.measureFragmentContainer, fragment);
        }
        fragmentTransaction.commit();
    }
    public void onMeasurementResult(double timeOfOscillation)
    {
        update();

        double pretension = mSlacklineMeasurements.calculateForce(timeOfOscillation);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ShowMeasurementResultFragment fragment = new ShowMeasurementResultFragment();
        fragment.setPretension(pretension);
        fragment.setmTimeOfOscillation(timeOfOscillation);
        fragmentTransaction.replace(R.id.measureFragmentContainer, fragment);
        fragmentTransaction.commit();

    }

    public void measureAgainButtonPressed()
    {
        updateMeasureFragment();
    }

    private void update()
    {
        readFromTextFields();
        updateTextFields();
    }

    private  void updateTextFields()
    {
        mWebbingButton.setText(mSlacklineMeasurements.getWebbingName());
        mStretch.setText(String.format(Locale.ENGLISH, "%.2f", mSlacklineMeasurements.getStretchCoefficient() / 1e-6));
        mLineWeight.setText(String.format(Locale.ENGLISH, "%.1f", mSlacklineMeasurements.getWeightPerMeter() * 1e3));
        mLength.setText(String.format(Locale.ENGLISH, "%.1f", mSlacklineMeasurements.getLength()));
    }

    private void readFromTextFields()
    {
        double stretchCoeff = Double.valueOf(mStretch.getText().toString()) * 1e-6; // unit at textfield is %/10kN
        double lineWeight = Double.valueOf(mLineWeight.getText().toString()) / 1e3;
        if (Math.abs(mSlacklineMeasurements.getStretchCoefficient() - stretchCoeff) > 5e-9 ||
                Math.abs(mSlacklineMeasurements.getWeightPerMeter() - lineWeight) > 5e-5)
        {
            mSlacklineMeasurements.setWebbing(new Webbing("Custom", lineWeight, new StretchBehavior(new StretchPoint(10e3, stretchCoeff*1e4))));
        }

        double length = Double.valueOf(mLength.getText().toString());
        mSlacklineMeasurements.setLength(length);
    }

    private void restoreParameters()
    {
        Context context = getActivity();
        SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.measure_force_preference_key), Context.MODE_PRIVATE);

        String webbingName = sharedPreferences.getString(getString(R.string.preference_webbing_name), "Custom");
        double stretch = sharedPreferences.getFloat(getString(R.string.preference_stretch_coefficient), (float) 1e-5);
        double weightPerMeter = sharedPreferences.getFloat(getString(R.string.preference_weight_per_meter), (float) 0.05);
        double length = sharedPreferences.getFloat(getString(R.string.preference_line_length), 50);
        boolean isAutomaticMeasurement = sharedPreferences.getBoolean(getString(R.string.preference_is_automatic_measurement), true);

        Webbing webbing = Webbing.getWebbingByName(webbingName);
        if (webbing != null)
            mSlacklineMeasurements.setWebbing(webbing);
        else {
            mSlacklineMeasurements.setWebbing(new Webbing(webbingName, weightPerMeter, new StretchBehavior(new StretchPoint(30e3, 3*stretch*1e4))));
        }
        mSlacklineMeasurements.setLength(length);
        mIsMeasureAutomatically = isAutomaticMeasurement;
    }

    private void saveParameters()
    {
        Context context = getActivity();
        SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.measure_force_preference_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String webbingName = mSlacklineMeasurements.getWebbingName();
        double stretch = mSlacklineMeasurements.getStretchCoefficient();
        double weightPerMeter = mSlacklineMeasurements.getWeightPerMeter();
        double length = mSlacklineMeasurements.getLength();
        //boolean isAutomaticMeasurement =

        editor.putString(getString(R.string.preference_webbing_name), webbingName);
        editor.putFloat(getString(R.string.preference_stretch_coefficient), (float) stretch);
        editor.putFloat(getString(R.string.preference_weight_per_meter), (float) weightPerMeter);
        editor.putFloat(getString(R.string.preference_line_length), (float) length);
        editor.putBoolean(getString(R.string.preference_is_automatic_measurement), mIsMeasureAutomatically);

        editor.commit();

    }

    private void setInfoClickListeners(View view)
    {
        TextView webbingText = (TextView) view.findViewById(R.id.webbingText);
        TextView stretchText = (TextView) view.findViewById(R.id.stretchText);
        TextView lineWeightText = (TextView) view.findViewById(R.id.lineWeightText);
        TextView lengthText = (TextView) view.findViewById(R.id.lengthText);

        webbingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParameterInfoDialog.showDialog(getString(R.string.measure_force__info_title_webbing), getString(R.string.measure_force__info_text_webbing), getActivity());
            }
        });

        stretchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParameterInfoDialog.showDialog(getString(R.string.measure_force__info_title_stretch), getString(R.string.measure_force__info_text_stretch), getActivity());
            }
        });

        lineWeightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParameterInfoDialog.showDialog(getString(R.string.measure_force__info_title_line_weight), getString(R.string.measure_force__info_text_line_weigth), getActivity());
            }
        });

        lengthText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParameterInfoDialog.showDialog(getString(R.string.measure_force__info_title_length), getString(R.string.measure_force__info_text_length), getActivity());
            }
        });
    }

}

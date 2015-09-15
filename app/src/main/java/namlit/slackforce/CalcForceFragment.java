package namlit.slackforce;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

import slacklib.*;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CalcForceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CalcForceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalcForceFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //private static final String ARG_PARAM1 = "param1";
    //private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    //private String mParam1;
    //private String mParam2;

    public enum Parameter
    {
        STRETCH, LENGTH, SAG, WEIGHT, FORCE, PRETENSION, SAGWITHOUTSLACKER
    }


    static final int GET_WEBBING_REQUEST = 1;
    static final int GET_PARAMETER_TO_CALCULATE_REQUEST = 2;

    static final String FRAGMENT_NAME = "FRAGMENT_NAME";
    static final String CALC_FORCE_FRAGMENT = "CALC_FORCE_FRAGMENT";
    static final String WEBBING_NAME = "WEBBING_NAME";
    static final String WEBBING_STRETCH = "WEBBING_STRETCH";
    static final String LINE_LENGTH = "LINE_LENGTH";
    static final String SLACKLINER_WEIGHT = "SLACKLINER_WEIGHT";
    static final String PRETENSION = "PRETENSION";

    private OnFragmentInteractionListener mListener;
    private SlacklineCalculations mSlackineCalculations;
    private Parameter mParameterToCalculate = Parameter.FORCE;
    private Parameter mParameterChanged = Parameter.LENGTH;

    private Button mWebbing;
    private EditText mStretch;
    private EditText mLength;
    private EditText mSag;
    private EditText mWeight;
    private EditText mForce;
    private EditText mPretension;
    private EditText mSagWithoutSlacker;
    private Button mChooseParameterButton;
    private Button mCopyValuesButton;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalcForceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalcForceFragment newInstance() {
        CalcForceFragment fragment = new CalcForceFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public CalcForceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mSlackineCalculations = new SlacklineCalculations();
        restoreParameters();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calc_force, container, false);

        mWebbing = (Button) view.findViewById(R.id.webbing);
        mStretch = (EditText) view.findViewById(R.id.stretch);
        mLength = (EditText) view.findViewById(R.id.length);
        mSag = (EditText) view.findViewById(R.id.sag);
        mWeight = (EditText) view.findViewById(R.id.weight);
        mForce = (EditText) view.findViewById(R.id.force);
        mPretension = (EditText) view.findViewById(R.id.pretension);
        mSagWithoutSlacker = (EditText) view.findViewById(R.id.sagWithoutSlacker);
        mChooseParameterButton = (Button) view.findViewById(R.id.chooseParameterButton);
        mCopyValuesButton = (Button) view.findViewById(R.id.copyValuesButton);


        updateCalculations();


        mStretch.clearFocus();

        mWebbing.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SelectWebbingManufacturerActivity.class);
                startActivityForResult(intent, GET_WEBBING_REQUEST);
            }
        });
        mStretch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                double stretchCoeff = Double.valueOf(mStretch.getText().toString()) * 1e-6; // unit at textfield is %/10kN
                if (Math.abs(mSlackineCalculations.getStretchCoefficient() - stretchCoeff) > 5e-9) {
                    mParameterChanged = Parameter.STRETCH;
                    update();
                }
                return true;
            }
        });
        mStretch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    double stretchCoeff = Double.valueOf(mStretch.getText().toString()) * 1e-6; // unit at textfield is %/10kN
                    if (Math.abs(mSlackineCalculations.getStretchCoefficient() - stretchCoeff) > 5e-9) {
                        mParameterChanged = Parameter.STRETCH;
                        update();
                    }
                }
            }
        });
        mLength.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (Math.abs(mSlackineCalculations.getLength() - Double.valueOf(mLength.getText().toString())) > 0.05) {
                    mParameterChanged = Parameter.LENGTH;
                    update();
                }
                return true;
            }
        });
        mLength.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (Math.abs(mSlackineCalculations.getLength() - Double.valueOf(mLength.getText().toString())) > 0.05) {
                        mParameterChanged = Parameter.LENGTH;
                        update();
                    }
                }
            }
        });
        mSag.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (Math.abs(mSlackineCalculations.getSag() - Double.valueOf(mSag.getText().toString())) > 0.005) {
                    mParameterChanged = Parameter.SAG;
                    update();
                }
                return true;
            }
        });
        mSag.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (Math.abs(mSlackineCalculations.getSag() - Double.valueOf(mSag.getText().toString())) > 0.005) {
                        mParameterChanged = Parameter.SAG;
                        update();
                    }
                }
            }
        });
        mWeight.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (Math.abs(mSlackineCalculations.getWeightOfSlackliner() - Double.valueOf(mWeight.getText().toString())) > 0.05) {
                    mParameterChanged = Parameter.WEIGHT;
                    update();
                }
                return true;
            }
        });
        mWeight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    if (Math.abs(mSlackineCalculations.getWeightOfSlackliner() - Double.valueOf(mWeight.getText().toString())) > 0.05) {
                        mParameterChanged = Parameter.WEIGHT;
                        update();
                    }
                }
            }
        });
        mForce.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (Math.abs(mSlackineCalculations.getAnchorForce() - Double.valueOf(mForce.getText().toString())) > 0.005) {
                    mParameterChanged = Parameter.FORCE;
                    update();
                }
                return true;
            }
        });
        mForce.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    if (Math.abs(mSlackineCalculations.getAnchorForce() - Double.valueOf(mForce.getText().toString())) > 0.005) {
                        mParameterChanged = Parameter.FORCE;
                        update();
                    }
                }
            }
        });
        mPretension.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (Math.abs(mSlackineCalculations.getPretension() - Double.valueOf(mPretension.getText().toString())) > 0.005) {
                    mParameterChanged = Parameter.PRETENSION;
                    update();
                }
                return true;
            }
        });
        mPretension.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    if (Math.abs(mSlackineCalculations.getPretension() - Double.valueOf(mPretension.getText().toString())) > 0.005) {
                        mParameterChanged = Parameter.PRETENSION;
                        update();
                    }
                }
            }
        });
        mSagWithoutSlacker.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (Math.abs(mSlackineCalculations.getSagWithoutSlacker() - Double.valueOf(mSagWithoutSlacker.getText().toString())) > 0.005) {
                    mParameterChanged = Parameter.SAGWITHOUTSLACKER;
                    update();
                }
                return true;
            }
        });
        mSagWithoutSlacker.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    if (Math.abs(mSlackineCalculations.getSagWithoutSlacker() - Double.valueOf(mSagWithoutSlacker.getText().toString())) > 0.005) {
                        mParameterChanged = Parameter.SAGWITHOUTSLACKER;
                        update();
                    }
                }
            }
        });
        mChooseParameterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectParameterToCalculate();
            }
        });

        mCopyValuesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyValues();
            }
        });

        setInfoClickListeners(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAllTextFields();
        markParameterToCalculate();
    }




//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

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

                mSlackineCalculations.setWebbing(Manufacturer.getManufacturerByID(manufacturerID).getWebbingByID(webbingID));

                mWebbing.setText(mSlackineCalculations.getWebbingName());
                mStretch.setText(String.format(Locale.ENGLISH, "%.2f", mSlackineCalculations.getStretchCoefficient() / 1e-6));

                updateCalculations();
            }
        }
//        if(requestCode == GET_PARAMETER_TO_CALCULATE_REQUEST)
//        {
//            if(resultCode == Activity.RESULT_OK)
//            {
//                mParameterToCalculate = (Parameter) data.getSerializableExtra("PARAMETER_TO_CALCULATE");
//                markParameterToCalculate();
//                update();
//            }
//        }
    }

    private void readInputField()
    {
        try {

            switch (mParameterChanged) {
                case STRETCH:
                    double stretchCoeff = Double.valueOf(mStretch.getText().toString()) * 1e-6; // unit at textfield is %/10kN
                        mSlackineCalculations.setWebbing(new Webbing(getString(R.string.custom), new StretchBehavior(new StretchPoint(30e3, 3* stretchCoeff *1e4))));
                    break;

                case LENGTH:
                    double length = Double.valueOf(mLength.getText().toString());
                    mSlackineCalculations.setLength(length);
                    break;

                case SAG:
                    double sag = Double.valueOf(mSag.getText().toString());
                    mSlackineCalculations.setSag(sag);
                    break;

                case WEIGHT:
                    double weight = Double.valueOf(mWeight.getText().toString());
                    mSlackineCalculations.setWeightOfSlackliner(weight);
                    break;

                case FORCE:
                    double force = Double.valueOf(mForce.getText().toString());
                    mSlackineCalculations.setAnchorForce(force * 1e3);
                    break;

                case PRETENSION:
                    double pretension = Double.valueOf(mPretension.getText().toString());
                    mSlackineCalculations.setPretension(pretension * 1e3);
                    break;

                case SAGWITHOUTSLACKER:
                    double sagWithoutSlacker = Double.valueOf(mSagWithoutSlacker.getText().toString());
                    mSlackineCalculations.setSagWithoutSlacker(sagWithoutSlacker);
                    break;
            }
        } catch (Throwable t) {

            t.printStackTrace();
        }
    }

    private void updateCalculations()
    {


        switch (mParameterToCalculate)
        {
            case FORCE:
                mSlackineCalculations.calculatePretension();
                break;

            case LENGTH:
                mSlackineCalculations.calculateLength();
                break;

            case SAG:
                mSlackineCalculations.calculateSag();
                break;

            case WEIGHT:
                mSlackineCalculations.calculateWeight();
                break;
        }
        mSlackineCalculations.calculatePretension();
    }

    private void update()
    {
        readInputField();
        updateCalculations();
        updateAllTextFields();
    }

    private void updateAllTextFields()
    {
        mWebbing.setText(mSlackineCalculations.getWebbingName());
        mStretch.setText(String.format(Locale.ENGLISH, "%.2f", mSlackineCalculations.getStretchCoefficient() / 1e-6));
        mLength.setText(String.format(Locale.ENGLISH, "%.1f", mSlackineCalculations.getLength()));
        mSag.setText(String.format(Locale.ENGLISH, "%.2f", mSlackineCalculations.getSag()));
        mWeight.setText(String.format(Locale.ENGLISH, "%.1f", mSlackineCalculations.getWeightOfSlackliner()));
        mForce.setText(String.format(Locale.ENGLISH, "%.2f", mSlackineCalculations.getAnchorForce() / 1e3));
        mPretension.setText(String.format(Locale.ENGLISH, "%.2f", mSlackineCalculations.getPretension() / 1e3));
        mSagWithoutSlacker.setText(String.format(Locale.ENGLISH, "%.2f", mSlackineCalculations.getSagWithoutSlacker()));
    }

    private void markParameterToCalculate()
    {
        mLength.setTextColor(Color.BLACK);
        mSag.setTextColor(Color.BLACK);
        mWeight.setTextColor(Color.BLACK);
        mForce.setTextColor(Color.BLACK);
        mPretension.setTextColor(Color.BLACK);
        mSagWithoutSlacker.setTextColor(Color.BLACK);

        switch (mParameterToCalculate)
        {
            case FORCE:
                mForce.setTextColor(Color.BLUE);
                mPretension.setTextColor(Color.BLUE);
                mSagWithoutSlacker.setTextColor(Color.BLUE);
                break;
            case LENGTH:
                mLength.setTextColor(Color.BLUE);
                break;
            case SAG:
                mSag.setTextColor(Color.BLUE);
                break;
            case WEIGHT:
                mWeight.setTextColor(Color.BLUE);
                break;
        }
    }

    private void selectParameterToCalculate()
    {
        //Intent intent = new Intent(getActivity(), ChooseResultParameterActivity.class);
        //startActivityForResult(intent, GET_PARAMETER_TO_CALCULATE_REQUEST);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String items[] = {getString(R.string.length), getString(R.string.sag), getString(R.string.weight), getString(R.string.force)};

        builder.setTitle(getString(R.string.choose_parameter_title))
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                mParameterToCalculate = Parameter.LENGTH;
                                break;
                            case 1:
                                mParameterToCalculate = Parameter.SAG;
                                break;
                            case 2:
                                mParameterToCalculate = Parameter.WEIGHT;
                                break;
                            case 3:
                                mParameterToCalculate = Parameter.FORCE;
                                break;
                        }
                        markParameterToCalculate();
                        update();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void copyValues()
    {
        Bundle valueBundle = new Bundle();
        valueBundle.putString(FRAGMENT_NAME, CALC_FORCE_FRAGMENT);
        valueBundle.putString(WEBBING_NAME, mWebbing.getText().toString());
        valueBundle.putDouble(WEBBING_STRETCH, Double.valueOf(mStretch.getText().toString()));
        valueBundle.putDouble(LINE_LENGTH, Double.valueOf(mLength.getText().toString()));
        valueBundle.putDouble(SLACKLINER_WEIGHT, Double.valueOf(mWeight.getText().toString()));
        valueBundle.putDouble(PRETENSION, 1e3*Double.valueOf(mPretension.getText().toString()));

        ((MainActivity) getActivity()).pasteValuesToAllFragments(valueBundle);
    }

    public void pasteValues(Bundle valueBundle) {
        if (valueBundle.getString(FRAGMENT_NAME).equals(CALC_FORCE_FRAGMENT))
            return;

        String webbingName = valueBundle.getString(WEBBING_NAME);
        double webbingStretch = valueBundle.getDouble(WEBBING_STRETCH);
        double lineLength = valueBundle.getDouble(LINE_LENGTH);
        double slacklinerWeight = valueBundle.getDouble(SLACKLINER_WEIGHT);
        double pretension = valueBundle.getDouble(PRETENSION);

        Webbing webbing = Webbing.getWebbingByName(webbingName);
        if (webbing != null)
            mSlackineCalculations.setWebbing(webbing);
        else {
            mSlackineCalculations.setWebbing(new Webbing(webbingName, new StretchBehavior(new StretchPoint(10e3, 0.01 * webbingStretch))));
        }
        if (lineLength != 0)
            mSlackineCalculations.setLength(lineLength);
        if (slacklinerWeight != 0)
            mSlackineCalculations.setWeightOfSlackliner(slacklinerWeight);
        if (pretension != 0)
            mSlackineCalculations.setPretension(pretension);

        mParameterToCalculate = Parameter.SAG;

        updateCalculations();
        if (isResumed())
        {
            updateAllTextFields();
            markParameterToCalculate();
        }
    }

    private void restoreParameters()
    {
        Context context = getActivity();
        SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.calculate_force_preference_key), Context.MODE_PRIVATE);

        String webbingName = sharedPreferences.getString(getString(R.string.preference_webbing_name), "White Magic");
        double stretch = sharedPreferences.getFloat(getString(R.string.preference_stretch_coefficient), (float) 1e-5);
        double length = sharedPreferences.getFloat(getString(R.string.preference_line_length), 50);
        double sag = sharedPreferences.getFloat(getString(R.string.preference_line_sag), 2);
        double weight = sharedPreferences.getFloat(getString(R.string.preference_weight_of_slackliner), 80);
        mParameterToCalculate = Parameter.valueOf(sharedPreferences.getString(getString(R.string.preference_parameter_to_calculate), Parameter.FORCE.toString()));

        Webbing webbing = Webbing.getWebbingByName(webbingName);
        if (webbing != null)
            mSlackineCalculations.setWebbing(webbing);
        else {
            mSlackineCalculations.setWebbing(new Webbing(webbingName, new StretchBehavior(new StretchPoint(30e3, 3*stretch*1e4))));
        }
        mSlackineCalculations.setLength(length);
        mSlackineCalculations.setSag(sag);
        mSlackineCalculations.setWeightOfSlackliner(weight);

        mSlackineCalculations.calculatePretension();
    }

    private void saveParameters()
    {
        Context context = getActivity();
        SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.calculate_force_preference_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String webbingName = mSlackineCalculations.getWebbingName();
        double stretch = mSlackineCalculations.getStretchCoefficient();
        double length = mSlackineCalculations.getLength();
        double sag = mSlackineCalculations.getSag();
        double weight = mSlackineCalculations.getWeightOfSlackliner();

        editor.putString(getString(R.string.preference_webbing_name), webbingName);
        editor.putFloat(getString(R.string.preference_stretch_coefficient), (float) stretch);
        editor.putFloat(getString(R.string.preference_line_length), (float) length);
        editor.putFloat(getString(R.string.preference_line_sag), (float) sag);
        editor.putFloat(getString(R.string.preference_weight_of_slackliner), (float) weight);
        editor.putString(getString(R.string.preference_parameter_to_calculate), mParameterToCalculate.toString());

        editor.commit();

    }

    private void setInfoClickListeners(View view)
    {
        TextView webbingText = (TextView) view.findViewById(R.id.webbingText);
        TextView stretchText = (TextView) view.findViewById(R.id.stretchText);
        TextView lengthText = (TextView) view.findViewById(R.id.lengthText);
        TextView sagText = (TextView) view.findViewById(R.id.sagText);
        TextView weightText = (TextView) view.findViewById(R.id.weightText);
        TextView forceText = (TextView) view.findViewById(R.id.forceText);
        TextView pretensionText = (TextView) view.findViewById(R.id.pretensionText);
        TextView initialSagText = (TextView) view.findViewById(R.id.sagWithoutSlackerText);

        webbingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParameterInfoDialog.showDialog(getString(R.string.calculate_force__info_title_webbing), getString(R.string.calculate_force__info_text_webbing), getActivity());
            }
        });

        stretchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParameterInfoDialog.showDialog(getString(R.string.calculate_force__info_title_stretch), getString(R.string.calculate_force__info_text_stretch), getActivity());
            }
        });

        lengthText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParameterInfoDialog.showDialog(getString(R.string.calculate_force__info_title_length), getString(R.string.calculate_force__info_text_length), getActivity());
            }
        });

        sagText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParameterInfoDialog.showDialog(getString(R.string.calculate_force__info_title_sag), getString(R.string.calculate_force__info_text_sag), getActivity());
            }
        });

        weightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParameterInfoDialog.showDialog(getString(R.string.calculate_force__info_title_weight), getString(R.string.calculate_force__info_text_weight), getActivity());
            }
        });

        forceText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParameterInfoDialog.showDialog(getString(R.string.calculate_force__info_title_force), getString(R.string.calculate_force__info_text_force), getActivity());
            }
        });

        pretensionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParameterInfoDialog.showDialog(getString(R.string.calculate_force__info_title_pretension), getString(R.string.calculate_force__info_text_pretension), getActivity());
            }
        });

        initialSagText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParameterInfoDialog.showDialog(getString(R.string.calculate_force__info_title_initial_sag), getString(R.string.calculate_force__info_text_initial_sag), getActivity());
            }
        });

    }
}

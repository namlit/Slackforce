package namlit.slackforce;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
 * {@link DynamicForceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DynamicForceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DynamicForceFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    static final int GET_WEBBING_REQUEST = 1;

    private OnFragmentInteractionListener mListener;
    private SlacklineBounceSimulations mBounceSimulations;
    private Button mWebbing;
    private EditText mStretch;
    private EditText mLength;
    private EditText mPretension;
    private EditText mInitialSag;
    private EditText mWeight;
    private EditText mHeightOfFallEditText;
    private TextView mGFactor;
    private TextView mMaxSag;
    private TextView mSlackerForce;
    private TextView mMaxLineForce;
    private double mHeightOfFallValue;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DynamicForceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DynamicForceFragment newInstance() {
        DynamicForceFragment fragment = new DynamicForceFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public DynamicForceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mBounceSimulations = new SlacklineBounceSimulations();
        restoreParameters();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dynamic_force, container, false);

        mWebbing = (Button) view.findViewById(R.id.webbing);
        mStretch = (EditText) view.findViewById(R.id.stretch);
        mLength = (EditText) view.findViewById(R.id.length);
        mPretension = (EditText) view.findViewById(R.id.pretension);
        mInitialSag = (EditText) view.findViewById(R.id.initialSag);
        mWeight = (EditText) view.findViewById(R.id.weight);
        mHeightOfFallEditText = (EditText) view.findViewById(R.id.heightOfFall);
        mGFactor = (TextView) view.findViewById(R.id.gFactor);
        mSlackerForce = (TextView) view.findViewById(R.id.maxVerticalForce);
        mMaxLineForce = (TextView) view.findViewById(R.id.maxLineForce);
        mMaxSag = (TextView) view.findViewById(R.id.maxSag);

        mWebbing.setText(mBounceSimulations.getWebbingName());
        mStretch.setText(String.format(Locale.ENGLISH, "%.1f", mBounceSimulations.getStretchCoefficient() / 1e-6));
        mLength.setText(String.format(Locale.ENGLISH, "%.1f", mBounceSimulations.getLength()));
        mPretension.setText(String.format(Locale.ENGLISH, "%.1f", mBounceSimulations.getPretension()/1e3 ));
        mWeight.setText(String.format(Locale.ENGLISH, "%.1f", mBounceSimulations.getWeight() ));
        mHeightOfFallEditText.setText(String.format(Locale.ENGLISH, "%.1f", mHeightOfFallValue));

        updateTextFields();
        calculateDynamicForces();

        mWebbing.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SelectWebbingManufacturerActivity.class);
                startActivityForResult(intent, GET_WEBBING_REQUEST);
            }
        });
        mStretch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    update();
                }
            }
        });
        mStretch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                update();
                return true;
            }
        });
        mLength.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    update();
                }
            }
        });
        mLength.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                update();
                return true;
            }
        });
        mPretension.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    update();
                }
            }
        });
        mPretension.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                update();
                return true;
            }
        });
        mInitialSag.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    update();
                }
            }
        });
        mInitialSag.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                update();
                return true;
            }
        });
        mWeight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    update();
                }
            }
        });
        mWeight.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                update();
                return true;
            }
        });
        mHeightOfFallEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    update();
                }
            }
        });
        mHeightOfFallEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
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

                mBounceSimulations.setWebbing(Manufacturer.getManufacturerByID(manufacturerID).getWebbingByID(webbingID));

                mWebbing.setText(mBounceSimulations.getWebbingName());
                mStretch.setText(String.format(Locale.ENGLISH, "%.2f", mBounceSimulations.getStretchCoefficient() / 1e-6));

                calculateDynamicForces();
            }
        }
    }


    private void update()
    {
        readFromTextFields();
        updateTextFields();
        calculateDynamicForces();
    }

    private void calculateDynamicForces()
    {
        double maxForces[] = mBounceSimulations.calculateMaximumForces(mHeightOfFallValue);
        mGFactor.setText(String.format(Locale.ENGLISH, "%.2f", maxForces[0]));
        mSlackerForce.setText(String.format(Locale.ENGLISH, "%.2f kN", maxForces[1] / 1e3));
        mMaxLineForce.setText(String.format(Locale.ENGLISH, "%.2f kN", maxForces[2] / 1e3));
        mMaxSag.setText(String.format(Locale.ENGLISH, "%.2f m", maxForces[3]));
    }

    private  void updateTextFields()
    {
        mWebbing.setText(mBounceSimulations.getWebbingName());
        mStretch.setText(String.format(Locale.ENGLISH, "%.2f", mBounceSimulations.getStretchCoefficient() / 1e-6));
        mLength.setText(String.format(Locale.ENGLISH, "%.1f", mBounceSimulations.getLength()));
        mPretension.setText(String.format(Locale.ENGLISH, "%.1f", mBounceSimulations.getPretension() / 1e3 ));
        mInitialSag.setText(String.format(Locale.ENGLISH, "%.1f", mBounceSimulations.getInitialSag()));
        mWeight.setText(String.format(Locale.ENGLISH, "%.1f", mBounceSimulations.getWeight()));
        mHeightOfFallEditText.setText(String.format(Locale.ENGLISH, "%.1f", mHeightOfFallValue));
    }

    private void readFromTextFields()
    {
        try
        {
            double stretch = Double.valueOf(mStretch.getText().toString()) * 1e-2; // unit at textfield is %/10kN
            if (mBounceSimulations.getStretchCoefficient() != stretch)
            {
                mBounceSimulations.setWebbing(new Webbing("Custom", new StretchBehavior(new StretchPoint(30e3, 3*stretch))));
                mWebbing.setText(mBounceSimulations.getWebbingName());
            }

            double length = Double.valueOf(mLength.getText().toString());
            double pretension = Double.valueOf(mPretension.getText().toString()) * 1e3;
            double initialSag = Double.valueOf(mInitialSag.getText().toString());
            double weight = Double.valueOf(mWeight.getText().toString());
            mHeightOfFallValue =  Double.valueOf(mHeightOfFallEditText.getText().toString());

            mBounceSimulations.setLength(length);

            if (pretension != mBounceSimulations.getPretension())
                mBounceSimulations.setPretension(pretension);
            else
                mBounceSimulations.setInitialSag(initialSag);

            mBounceSimulations.setWeight(weight);

        } catch (Throwable t) {

            t.printStackTrace();
        }
    }

    private void restoreParameters()
    {
        Context context = getActivity();
        SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.dynamic_force_preference_key), Context.MODE_PRIVATE);

        String webbingName = sharedPreferences.getString(getString(R.string.preference_webbing_name), "White Magic");
        double stretch = sharedPreferences.getFloat(getString(R.string.preference_stretch_coefficient), (float) 1e-5);
        double length = sharedPreferences.getFloat(getString(R.string.preference_line_length), 50);
        double pretension = sharedPreferences.getFloat(getString(R.string.preference_pretension), 6000);
        double initialSag = sharedPreferences.getFloat(getString(R.string.preference_sag_without_slackliner), 0);
        double weight = sharedPreferences.getFloat(getString(R.string.preference_weight_of_slackliner), 80);
        double heightOfFall = sharedPreferences.getFloat(getString(R.string.preference_height_of_fall), 1);


        Webbing webbing = Webbing.getWebbingByName(webbingName);
        if (webbing != null)
            mBounceSimulations.setWebbing(webbing);
        else {
            mBounceSimulations.setWebbing(new Webbing("Custom", new StretchBehavior(new StretchPoint(30e3, 3*stretch*1e4))));
        }
        mBounceSimulations.setLength(length);
        mBounceSimulations.setPretension(pretension);
        mBounceSimulations.setInitialSag(initialSag);
        mBounceSimulations.setWeight(weight);
        mHeightOfFallValue = heightOfFall;

    }

    private void saveParameters()
    {
        Context context = getActivity();
        SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.dynamic_force_preference_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String webbingName = mBounceSimulations.getWebbingName();
        double stretch = mBounceSimulations.getStretchCoefficient();
        double length = mBounceSimulations.getLength();
        double pretension = mBounceSimulations.getPretension();
        double initialSag = mBounceSimulations.getInitialSag();
        double weight = mBounceSimulations.getWeight();
        double heightOfFall = mHeightOfFallValue;

        editor.putString(getString(R.string.preference_webbing_name), webbingName);
        editor.putFloat(getString(R.string.preference_stretch_coefficient), (float) stretch);
        editor.putFloat(getString(R.string.preference_line_length), (float) length);
        editor.putFloat(getString(R.string.preference_pretension), (float) pretension);
        editor.putFloat(getString(R.string.preference_sag_without_slackliner), (float) initialSag);
        editor.putFloat(getString(R.string.preference_weight_of_slackliner), (float) weight);
        editor.putFloat(getString(R.string.preference_height_of_fall), (float) heightOfFall);

        editor.commit();

    }

    private void setInfoClickListeners(View view)
    {
        TextView webbingText = (TextView) view.findViewById(R.id.webbingText);
        TextView stretchText = (TextView) view.findViewById(R.id.stretchText);
        TextView lengthText = (TextView) view.findViewById(R.id.lengthText);
        TextView pretensionText = (TextView) view.findViewById(R.id.pretensionText);
        TextView initialSagText = (TextView) view.findViewById(R.id.initialSagText);
        TextView weightText = (TextView) view.findViewById(R.id.weightText);
        TextView fallHeightText = (TextView) view.findViewById(R.id.heightOfFallText);
        TextView gFactorText = (TextView) view.findViewById(R.id.gFactorText);
        TextView maxSlackerForce = (TextView) view.findViewById(R.id.maxSlackerForceText);
        TextView maxLineForceText = (TextView) view.findViewById(R.id.maxLineForceText);
        TextView maxSag = (TextView) view.findViewById(R.id.maxSagText);



        webbingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParameterInfoDialog.showDialog(getString(R.string.dynamic_force__info_title_webbing), getString(R.string.dynamic_force__info_text_webbing), getActivity());
            }
        });

        stretchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParameterInfoDialog.showDialog(getString(R.string.dynamic_force__info_title_stretch), getString(R.string.dynamic_force__info_text_stretch), getActivity());
            }
        });

        lengthText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParameterInfoDialog.showDialog(getString(R.string.dynamic_force__info_title_length), getString(R.string.dynamic_force__info_text_length), getActivity());
            }
        });

        pretensionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParameterInfoDialog.showDialog(getString(R.string.dynamic_force__info_title_pretension), getString(R.string.dynamic_force__info_text_pretension), getActivity());
            }
        });

        initialSagText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParameterInfoDialog.showDialog(getString(R.string.dynamic_force__info_title_initial_sag), getString(R.string.dynamic_force__info_text_initial_sag), getActivity());
            }
        });

        weightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParameterInfoDialog.showDialog(getString(R.string.dynamic_force__info_title_weight), getString(R.string.dynamic_force__info_text_weight), getActivity());
            }
        });

        fallHeightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParameterInfoDialog.showDialog(getString(R.string.dynamic_force__info_title_fall_height), getString(R.string.dynamic_force__info_text_fall_height), getActivity());
            }
        });

        gFactorText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParameterInfoDialog.showDialog(getString(R.string.dynamic_force__info_title_g_factor), getString(R.string.dynamic_force__info_text_g_factor), getActivity());
            }
        });

        maxSlackerForce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParameterInfoDialog.showDialog(getString(R.string.dynamic_force__info_title_slacker_force), getString(R.string.dynamic_force__info_text_slacker_force), getActivity());
            }
        });

        maxLineForceText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParameterInfoDialog.showDialog(getString(R.string.dynamic_force__info_title_max_line_force), getString(R.string.dynamic_force__info_text_max_line_force), getActivity());
            }
        });

        maxSag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParameterInfoDialog.showDialog(getString(R.string.dynamic_force__info_title_max_sag), getString(R.string.dynamic_force__info_text_max_sag), getActivity());
            }
        });

    }

}

package namlit.slackforce;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
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

        //mParameterToCalculate = Parameter.FORCE;
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

        updateAllTextFields();
        updateCalculations();
        markParameterToCalculate();

        mWebbing.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SelectWebbingManufacturerActivity.class);
                startActivityForResult(intent, GET_WEBBING_REQUEST);
            }
        });
        mStretch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                mParameterChanged = Parameter.STRETCH;
                update();
                return true;
            }
        });
        mStretch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mParameterChanged = Parameter.STRETCH;
                    update();
                }
            }
        });
        mLength.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                mParameterChanged = Parameter.LENGTH;
                update();
                return true;
            }
        });
        mLength.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    mParameterChanged = Parameter.LENGTH;
                    update();
                }
            }
        });
        mSag.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                mParameterChanged = Parameter.SAG;
                update();
                return true;
            }
        });
        mSag.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    mParameterChanged = Parameter.SAG;
                    update();
                }
            }
        });
        mWeight.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                mParameterChanged = Parameter.WEIGHT;
                update();
                return true;
            }
        });
        mWeight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    mParameterChanged = Parameter.WEIGHT;
                    update();
                }
            }
        });
        mForce.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {


                mParameterChanged = Parameter.FORCE;
                update();
                return true;
            }
        });
        mForce.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    mParameterChanged = Parameter.FORCE;
                    update();
                }
            }
        });
        mPretension.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                mParameterChanged = Parameter.PRETENSION;
                update();
                return true;
            }
        });
        mPretension.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    mParameterChanged = Parameter.PRETENSION;
                    update();
                }
            }
        });
        mSagWithoutSlacker.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                mParameterChanged = Parameter.SAGWITHOUTSLACKER;
                update();
                return true;
            }
        });
        mSagWithoutSlacker.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    mParameterChanged = Parameter.SAGWITHOUTSLACKER;
                    update();
                }
            }
        });


        return view;
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

                mWebbing.setText(mSlackineCalculations.getWebbing().toString());
                mStretch.setText( String.format(Locale.ENGLISH, "%.1f", mSlackineCalculations.getWebbing().getStretchCoefficient() / 1e-6));

                updateCalculations();
            }
        }
        if(requestCode == GET_PARAMETER_TO_CALCULATE_REQUEST)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                mParameterToCalculate = (Parameter) data.getSerializableExtra("PARAMETER_TO_CALCULATE");
                markParameterToCalculate();
                update();
            }
        }
    }

    private void readInputField()
    {
        try {

            switch (mParameterChanged) {
                case STRETCH:
                    double stretch = Double.valueOf(mStretch.getText().toString()) * 1e-6; // unit at textfield is %/10kN
                    if (mSlackineCalculations.getWebbing().getStretchCoefficient() != stretch)
                        mSlackineCalculations.setWebbing(new Webbing("Custom", stretch));
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
    }

    private void update()
    {
        readInputField();
        if(mParameterToCalculate == mParameterChanged ||
                (mParameterToCalculate == Parameter.FORCE && (mParameterChanged == Parameter.PRETENSION || mParameterChanged == Parameter.SAGWITHOUTSLACKER)))
        {
            selectParameterToCalculate();
            return;
        }
        updateCalculations();
        updateAllTextFields();
    }

    private void updateAllTextFields()
    {
        mWebbing.setText(mSlackineCalculations.getWebbing().toString());
        mStretch.setText(String.format(Locale.ENGLISH, "%.1f", mSlackineCalculations.getWebbing().getStretchCoefficient() / 1e-6));
        mLength.setText(String.format(Locale.ENGLISH, "%.1f", mSlackineCalculations.getLength()));
        mSag.setText(String.format(Locale.ENGLISH, "%.2f", mSlackineCalculations.getSag()));
        mWeight.setText(String.format(Locale.ENGLISH, "%.1f", mSlackineCalculations.getWeightOfSlackliner()));
        mForce.setText(String.format(Locale.ENGLISH, "%.2f", mSlackineCalculations.getAnchorForce()/1e3 ));
        mPretension.setText(String.format(Locale.ENGLISH, "%.2f", mSlackineCalculations.getPretension() / 1e3));
        mSagWithoutSlacker.setText(String.format(Locale.ENGLISH, "%.2f", mSlackineCalculations.getSagWithoutSlacker()));
    }

    private void markParameterToCalculate()
    {
        mLength.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
        mSag.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
        mWeight.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
        mForce.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
        mPretension.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
        mSagWithoutSlacker.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);

        switch (mParameterToCalculate)
        {
            case FORCE:
                mForce.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
                mPretension.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
                mSagWithoutSlacker.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
                break;
            case LENGTH:
                mLength.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
                break;
            case SAG:
                mSag.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
                break;
            case WEIGHT:
                mWeight.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
                break;
        }
    }

    private void selectParameterToCalculate()
    {
        Intent intent = new Intent(getActivity(), ChooseResultParameterActivity.class);
        startActivityForResult(intent, GET_PARAMETER_TO_CALCULATE_REQUEST);
    }

}

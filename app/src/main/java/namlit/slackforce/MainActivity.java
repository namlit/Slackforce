package namlit.slackforce;

import java.util.Locale;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import slacklib.*;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener, OnMeasurementResultListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    static public final int MEASURE_FORCE_POSITION = 0;
    static public final int CALCULATE_FORCE_POSITION = 1;
    static public final int DYNAMIC_FORCE_POSITION = 2;

    ViewPager mViewPager;
    static final int GET_WEBBING_REQUEST = 1;
    private MeasureForceFragment mMeasureForceFragment;
    //private SlacklineCalculations mSlackineCalculations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Webbing.initializeWebbings();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //mSlackineCalculations = new SlacklineCalculations();
        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public void selectWebbing(View view)
    {
//        Intent intent = new Intent(this, SelectWebbingManufacturerActivity.class);
//        startActivityForResult(intent, GET_WEBBING_REQUEST);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
//        if(requestCode == GET_WEBBING_REQUEST)
//        {
//            if(resultCode == RESULT_OK)
//            {
//                int manufacturerID = data.getIntExtra("MANUFACTURER_ID", 0);
//                int webbingID = data.getIntExtra("WEBBING_ID", 0);
//
//                mSlackineCalculations.setWebbing(Manufacturer.getManufacturerByID(manufacturerID).getWebbingByID(webbingID));
//
//                Button webbingButton = (Button) findViewById(R.id.webbing);
//                webbingButton.setText(mSlackineCalculations.getWebbing().toString());
//
//            }
//        }
//    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private MeasureForceFragment mMeasurementForceFragment;

        public MeasureForceFragment getMeasurementForceFragment()
        {
            return mMeasurementForceFragment;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position)
            {
                case 0:
                    mMeasureForceFragment = MeasureForceFragment.newInstance();
                    return mMeasureForceFragment;
                case 1:
                    return CalcForceFragment.newInstance();
            }
            return DynamicForceFragment.newInstance();
            //return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case MEASURE_FORCE_POSITION:
                    return getString(R.string.title_section1).toUpperCase(l);
                case CALCULATE_FORCE_POSITION:
                    return getString(R.string.title_section2).toUpperCase(l);
                case DYNAMIC_FORCE_POSITION:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    @Override
    public void onMeasurementResult(double timeOfOscillation)
    {
        MeasureForceFragment measurementFragment = (MeasureForceFragment) getSupportFragmentManager().findFragmentByTag(makeFragmentName(MEASURE_FORCE_POSITION));
        measurementFragment.onMeasurementResult(timeOfOscillation);
    }

    public void measureAgainButtonPressed(View v)
    {
        MeasureForceFragment measurementFragment = (MeasureForceFragment) getSupportFragmentManager().findFragmentByTag(makeFragmentName(MEASURE_FORCE_POSITION));
        measurementFragment.measureAgainButtonPressed();
    }

    public String makeFragmentName(int position)
    {
        return "android:switcher:" + mViewPager.getId() + ":"
                + mSectionsPagerAdapter.getItemId(position);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}

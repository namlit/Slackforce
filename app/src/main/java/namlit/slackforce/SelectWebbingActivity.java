package namlit.slackforce;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import slacklib.*;


public class SelectWebbingActivity extends ListActivity {

    private int mManufacturerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        setTitle("Select Webbing");
        //setContentView(R.layout.activity_select_webbing);

        final ListView listView = getListView();
        mManufacturerID = intent.getIntExtra("MANUFACTURER_ID", 0);
        List<Webbing> webbings = Manufacturer.getManufacturerByID(mManufacturerID).getWebbings();

        final ArrayAdapter adapter = new ArrayAdapter<Webbing>(this, android.R.layout.simple_list_item_1, webbings);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_webbing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {

        Intent resultIntent = new Intent();
        resultIntent.putExtra("MANUFACTURER_ID", mManufacturerID);
        resultIntent.putExtra("WEBBING_ID", position);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}

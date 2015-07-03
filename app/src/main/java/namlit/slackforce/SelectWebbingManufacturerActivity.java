package namlit.slackforce;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import slacklib.*;


public class SelectWebbingManufacturerActivity extends ListActivity {

    static final int GET_WEBBING_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        setTitle("Select Manufacturer");
        //setContentView(R.layout.activity_select_webbing_manufacturer);

        final ListView listView = getListView();
        List<Manufacturer> manufacturers = Manufacturer.getManufactorers();

        final ArrayAdapter adapter = new ArrayAdapter<Manufacturer>(this, android.R.layout.simple_list_item_1, manufacturers);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_webbing_manufactorer_action, menu);
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

        Intent intent = new Intent(this, SelectWebbingActivity.class);
        intent.putExtra("MANUFACTURER_ID", position);
        startActivityForResult(intent, GET_WEBBING_REQUEST); //startactivityforresult

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == GET_WEBBING_REQUEST)
        {
            if(resultCode == RESULT_OK)
            {
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }
}

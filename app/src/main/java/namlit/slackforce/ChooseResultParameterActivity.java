package namlit.slackforce;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class ChooseResultParameterActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_result_parameter);

        setTitle("Select Parameter to Calculate");

        Button lenght = (Button) findViewById(R.id.length);
        Button sag = (Button) findViewById(R.id.sag);
        Button weight = (Button) findViewById(R.id.weight);
        Button force = (Button) findViewById(R.id.force);

        lenght.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("PARAMETER_TO_CALCULATE", CalcForceFragment.Parameter.LENGTH);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
        sag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("PARAMETER_TO_CALCULATE", CalcForceFragment.Parameter.SAG);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
        weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("PARAMETER_TO_CALCULATE", CalcForceFragment.Parameter.WEIGHT);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
        force.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("PARAMETER_TO_CALCULATE", CalcForceFragment.Parameter.FORCE);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_result_parameter, menu);
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
}

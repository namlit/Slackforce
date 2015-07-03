package namlit.slackforce;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.TextView;

/**
 * Created by tilman on 03.07.15.
 */
public class ParameterInfoDialog {

    static void showDialog(String title, String infoText, Activity activity)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        TextView view = new TextView(activity);
        view.setText(infoText);

        builder.setTitle(title);
        builder.setView(view);
        builder.setNeutralButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

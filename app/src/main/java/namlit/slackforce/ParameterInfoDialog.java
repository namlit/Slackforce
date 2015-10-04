package namlit.slackforce;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

/**
 * Created by tilman on 03.07.15.
 */
public class ParameterInfoDialog {

    static void showDialog(String title, String infoText, Activity activity)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        TextView view = new TextView(activity);
        view.setText(Html.fromHtml(infoText));
        view.setTextColor(Color.BLACK);
        view.setPadding(10, 10, 10, 10);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setLinksClickable(true);

        //builder.setTitle(title);
        builder.setView(view);
        builder.setNeutralButton(activity.getString(R.string.back), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

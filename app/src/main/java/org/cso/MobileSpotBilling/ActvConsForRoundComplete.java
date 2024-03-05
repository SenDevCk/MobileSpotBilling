package org.cso.MobileSpotBilling;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ActvConsForRoundComplete extends AppCompatActivity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Message");
			alertDialog.setMessage("Proceed for Round Complete Case");
			alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes",
					(dialog, which) -> {
						// TODO Auto-generated method stub
						alertDialog.dismiss();
						System.out.printf("Round Complete case \n\n\n");
						Intent intent = getIntent();
						intent.putExtra("proceed", true);
						setResult(RESULT_OK, intent);
						finish();
					});
			alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No",
					(dialog, which) -> {
						// TODO Auto-generated method stub
						alertDialog.dismiss();
						System.out.printf("Round not Complete case \n\n\n");
						Intent intent = getIntent();
						intent.putExtra("proceed", false);
						setResult(RESULT_OK, intent);
						finish();
					});
			alertDialog.show();
    }
}

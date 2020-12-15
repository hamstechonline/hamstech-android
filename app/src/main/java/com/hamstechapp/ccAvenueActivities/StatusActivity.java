package com.hamstechapp.ccAvenueActivities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hamstechapp.R;
import com.hamstechapp.activities.RegisterCourseActivity;

public class StatusActivity extends AppCompatActivity {

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_status);

		Intent mainIntent = getIntent();
		TextView tv4 = (TextView) findViewById(R.id.textView1);
		tv4.setText(mainIntent.getStringExtra("transStatus"));

		Toast.makeText(this, ""+mainIntent.getStringExtra("transStatus"), Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(StatusActivity.this, RegisterCourseActivity.class);
		startActivity(intent);
	}
	
	public void showToast(String msg) {
		Toast.makeText(this, "Toast: " + msg, Toast.LENGTH_LONG).show();
	}

} 
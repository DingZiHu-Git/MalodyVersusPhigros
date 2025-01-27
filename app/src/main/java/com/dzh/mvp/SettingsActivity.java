package com.dzh.mvp;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.json.JSONObject;
import org.json.JSONStringer;

public class SettingsActivity extends AppCompatActivity {
	public JSONObject jo;
	public File settings;
	public File temp;
	public Switch systemFileSelector;
	public EditText defaultPath;
	public Switch lastPath;
	public Switch deleteConverted;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		Toolbar toolbar = findViewById(R.id.settings_toolbar);
		setSupportActionBar(toolbar);
		try {
			jo = new JSONObject(getIntent().getStringExtra("jo"));
			temp = new File("/data/data/" + getPackageName() + "/cache");
			systemFileSelector = findViewById(R.id.system_file_selector);
			defaultPath = findViewById(R.id.default_path);
			final Button checkPath = findViewById(R.id.check_path);
			lastPath = findViewById(R.id.last_path);
			deleteConverted = findViewById(R.id.delete_converted);
			Button showFileList = findViewById(R.id.show_file_list);
			systemFileSelector.setChecked(jo.getBoolean("system_file_selector"));
			defaultPath.setText(jo.getString("default_path"));
			lastPath.setChecked(jo.getBoolean("last_path"));
			deleteConverted.setChecked(jo.getBoolean("delete_converted"));
			systemFileSelector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						defaultPath.setEnabled(!(lastPath.isChecked() || isChecked));
						checkPath.setEnabled(!(lastPath.isChecked() || isChecked));
						lastPath.setEnabled(!isChecked);
					}
				}
			);
			if (systemFileSelector.isChecked()) {
				defaultPath.setEnabled(false);
				checkPath.setEnabled(false);
				lastPath.setEnabled(false);
			}
			if (lastPath.isChecked()) {
				defaultPath.setEnabled(false);
				checkPath.setEnabled(false);
			}
			checkPath.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View v) {
						if (defaultPath.getText().toString().isEmpty()) Toast.makeText(SettingsActivity.this, "您留空了输入框，默认目录将会在您保存并退出设置界面时被设置为/storage/emulated/0。", Toast.LENGTH_LONG).show();
						else if (!new File(defaultPath.getText().toString()).exists()) Toast.makeText(SettingsActivity.this, "您输入的目录不存在。请检查您的拼写是否正确！", Toast.LENGTH_LONG).show();
						else if (!defaultPath.getText().toString().startsWith("/storage/emulated/0")) Toast.makeText(SettingsActivity.this, "您输入的目录不合法。请使用内部存储目录下的的文件夹！", Toast.LENGTH_LONG).show();
						else Toast.makeText(SettingsActivity.this, "您输入的目录可以使用。", Toast.LENGTH_SHORT).show();
					}
				}
			);
			lastPath.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						defaultPath.setEnabled(!isChecked);
						checkPath.setEnabled(!isChecked);
					}
				}
			);
			showFileList.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View view) {
						String[] fileList = temp.list();
						String str = "";
						for (String s : fileList) str += (s + "\n");
						AlertDialog.Builder adb = new AlertDialog.Builder(SettingsActivity.this);
						adb.setIcon(android.R.drawable.ic_input_get).setTitle("文件列表").setMessage(str).setPositiveButton(R.string.ok, null).show();
					}
				}
			);
		} catch (Exception e) {
			catcher(e);
		}
	}
	public void catcher(final Exception e) {
		final StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw, true));
		runOnUiThread(new Runnable() {
				@Override
				public void run() {
					AlertDialog.Builder adb = new AlertDialog.Builder(SettingsActivity.this);
					adb.setIcon(android.R.drawable.ic_delete).setTitle(R.string.crash_title).setMessage(sw.toString()).setPositiveButton(R.string.crash_ok, new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface p1, int p2) {
								ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
								ClipData cd = ClipData.newPlainText("Error message from MalodyVersusPhigros by DingZiHu", sw.toString());
								cm.setPrimaryClip(cd);
								finish();
							}
						}
					).setNegativeButton(R.string.crash_cancel, new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface p1, int p2) {
								finish();
							}
						}
					).setCancelable(false).show();
				}
			}
		);
	}
	@Override
	public void onBackPressed() {
		try {
			String dp = "";
			if (defaultPath.getText().toString().isEmpty() || !new File(defaultPath.getText().toString()).exists() || !defaultPath.getText().toString().startsWith("/storage/emulated/0")) dp = "/storage/emulated/0";
			else dp = defaultPath.getText().toString();
			setResult(RESULT_OK, new Intent().putExtra("jo", jo.put("system_file_selector", systemFileSelector.isChecked()).put("last_path", lastPath.isChecked()).put("default_path", dp).put("delete_converted", deleteConverted.isChecked()).toString()));
		} catch (Exception e) {
			catcher(e);
		}
		super.onBackPressed();
	}
}

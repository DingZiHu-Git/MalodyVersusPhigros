package com.dzh.mvp;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.dzh.mvp.R;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import org.json.JSONObject;
import org.json.JSONStringer;

public class SettingsActivity extends AppCompatActivity {
	public File temp;
	public File settings;
	public int speed;
	public boolean wide;
	public boolean slide;
	public int guide;
	public boolean guideFake;
	public String guideInterval;
	public boolean randomFalling;
	public boolean luck;
	public double defaultSpeed;
	public double defaultPosition;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		Toolbar toolbar = findViewById(R.id.settings_toolbar);
		setSupportActionBar(toolbar);
		try {
			temp = new File("/data/data/" + getPackageName() + "/cache");
			settings = new File("/data/data/" + getPackageName() + "/files/settings.json");
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(settings), "UTF-8"));
			JSONObject jo = new JSONObject(br.readLine());
			final EditText defaultPath = findViewById(R.id.default_path);
			final Button checkPath = findViewById(R.id.check_path);
			final Switch lastPath = findViewById(R.id.last_path);
			Switch deleteConverted = findViewById(R.id.delete_converted);
			Button showFileList = findViewById(R.id.show_file_list);
			speed = jo.getInt("speed");
			wide = jo.getBoolean("wide");
			slide = jo.getBoolean("slide");
			guide = jo.getInt("guide");
			guideFake = jo.getBoolean("guide_fake");
			guideInterval = jo.getString("interval");
			randomFalling = jo.getBoolean("random_falling");
			luck = jo.getBoolean("luck");
			defaultSpeed = jo.getDouble("default_speed");
			defaultPosition = jo.getDouble("default_position");
			defaultPath.setText(jo.getString("default_path"));
			lastPath.setChecked(jo.getBoolean("last_path"));
			deleteConverted.setChecked(jo.getBoolean("delete_converted"));
			if (lastPath.isChecked()) {
				defaultPath.setEnabled(false);
				checkPath.setEnabled(false);
			}
			checkPath.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View v) {
						if (defaultPath.getText().toString().isEmpty()) Toast.makeText(SettingsActivity.this, "您留空了输入框，默认目录将会在您保存并退出设置界面时被设置为/storage/emulated/0。", Toast.LENGTH_LONG).show();
						else if (!new File(defaultPath.getText().toString()).exists()) Toast.makeText(SettingsActivity.this, "您输入的目录不存在。请检查您的拼写是否正确！", Toast.LENGTH_LONG).show();
						else if (!defaultPath.getText().toString().startsWith("/storage/emulated/0")) Toast.makeText(SettingsActivity.this, "您输入的目录不合法。请使用内部存储内的目录！", Toast.LENGTH_LONG).show();
						else Toast.makeText(SettingsActivity.this, "您输入的目录可以使用。", Toast.LENGTH_SHORT).show();
					}
				}
			);
			lastPath.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (lastPath.isChecked()) {
							defaultPath.setEnabled(false);
							checkPath.setEnabled(false);
						} else {
							defaultPath.setEnabled(true);
							checkPath.setEnabled(true);
						}
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
						adb.setIcon(android.R.drawable.ic_input_get).setTitle("File List").setMessage(str).setPositiveButton(R.string.ok, null).show();
					}
				}
			);
		} catch (Exception e) {
			catcher(e);
		}
	}
	public void catcher(final Exception e) {
		for (File f : temp.listFiles()) {
			if (f.isDirectory()) for (File c : f.listFiles()) c.delete();
			f.delete();
		}
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setIcon(android.R.drawable.ic_delete);
		adb.setTitle(R.string.crash_title);
		adb.setMessage(e.toString());
		adb.setPositiveButton(R.string.crash_ok, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface p1, int p2) {
					ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
					ClipData cd = ClipData.newPlainText("Error message from MalodyVersusPhigros by DingZiHu", e.toString());
					cm.setPrimaryClip(cd);
					finish();
				}
			}
		);
		adb.setNegativeButton(R.string.crash_cancel, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface p1, int p2) {
					finish();
				}
			}
		);
		adb.show();
	}
	@Override
	public void onBackPressed() {
		try {
			EditText defaultPath = findViewById(R.id.default_path);
			Switch lastPath = findViewById(R.id.last_path);
			Switch deleteConverted = findViewById(R.id.delete_converted);
			String dp = "";
			if (defaultPath.getText().toString().isEmpty() || !new File(defaultPath.getText().toString()).exists() || !defaultPath.getText().toString().startsWith("/storage/emulated/0")) dp = "/storage/emulated/0";
			else dp = defaultPath.getText().toString();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(settings, false), "UTF-8"));
			JSONStringer js = new JSONStringer();
			js.object().key("default_path").value(dp).key("last_path").value(lastPath.isChecked()).key("delete_converted").value(deleteConverted.isChecked()).key("speed").value(speed).key("wide").value(wide).key("slide").value(slide).key("guide").value(guide).key("guide_fake").value(guideFake).key("interval").value(guideInterval).key("random_falling").value(randomFalling).key("luck").value(luck).key("default_speed").value(defaultSpeed).key("default_position").value(defaultPosition).endObject();
			bw.write(js.toString());
			bw.close();
		} catch (Exception e) {
			catcher(e);
		}
		super.onBackPressed();
	}
}

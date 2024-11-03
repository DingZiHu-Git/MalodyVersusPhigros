package com.dzh.mvp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import com.dzh.mvp.R;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

public class MainActivity extends AppCompatActivity {
	public static String packageName;
	public static Resources resources;
	public static File settings;
	public static File temp;
	public File dir = new File("/storage/emulated/0/data/MVP");
	public String crash = null;
	public ProgressDialog loading = null;
	public static List<File> charts = new ArrayList<File>();
	public static List<Switch> chartList = new ArrayList<Switch>();
	public Functions functions;
	public void init() {
		try {
			for (File file : temp.listFiles()) {
				final Switch chart = new Switch(MainActivity.this);
				chart.setChecked(true);
				if (file.getName().endsWith(".mc")) {
					BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
					String json = "";
					String line = null;
					while ((line = br.readLine()) != null) json += line;
					JSONObject meta = new JSONObject(json).getJSONObject("meta");
					String mode = "未知";
					switch (meta.getInt("mode")) {
						case 0:
							mode = meta.getJSONObject("mode_ext").getInt("column") + "K";
							break;
						case 3:
							mode = "Catch";
							break;
						case 5:
							mode = "Taiko";
							break;
						case 7:
							mode = "Slide";
							break;
					}
					chart.setText(file.getName() + "\n    曲名：" + meta.getJSONObject("song").getString("title") + "\n    曲师：" + meta.getJSONObject("song").getString("artist") + "\n    模式：" + mode + "(" + meta.getInt("mode") + ")\n    难度：" + meta.getString("version") + "\n    谱师：" + meta.getString("creator"));
					chartList.add(chart);
				} else if (file.getName().endsWith(".osu")) {
					chart.setText(file.getName() + "\n    osu!谱面");
					chartList.add(chart);
				} else if (file.getName().endsWith(".json") || file.getName().endsWith(".pec")) {
					chart.setText(file.getName() + "\n    Phigros谱面");
					chartList.add(chart);
				} else if (file.getName().equalsIgnoreCase("effect.mvp")) {
					chart.setText(file.getName() + "\n    MalodyVersusPhigros效果文件");
					chartList.add(chart);
				}
			}
			functions = new Functions();
			loading = new ProgressDialog(this);
			loading.setCancelable(false);
			loading.setMessage("请稍后……");
			loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			loading.setIndeterminate(true);
			Button loadChart = findViewById(R.id.load);
			Button manageLoadedChart = findViewById(R.id.manage_loaded_chart);
			Button startConvert = findViewById(R.id.convert);
			Button keyToSlide = findViewById(R.id.key_to_slide);
			final Switch bpm = findViewById(R.id.bpm);
			final Switch scroll = findViewById(R.id.scroll);
			final Switch wide = findViewById(R.id.wide);
			final Switch slide = findViewById(R.id.slide);
			final Switch guide = findViewById(R.id.guide);
			final RadioButton guideLegacy = findViewById(R.id.guide_legacy);
			final RadioButton guideNew = findViewById(R.id.guide_new);
			final CheckBox guideFake = findViewById(R.id.guide_fake);
			final EditText guideInterval = findViewById(R.id.guide_interval);
			final Switch randomFalling = findViewById(R.id.random_falling);
			final Switch luck = findViewById(R.id.luck);
			final EditText speed = findViewById(R.id.speed);
			final EditText position = findViewById(R.id.position);
			final EditText illustrator = findViewById(R.id.illustrator);
			Button speedTool = findViewById(R.id.speed_tool);
			if (!settings.exists()) {
				settings.getParentFile().mkdirs();
				settings.createNewFile();
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(settings, false), "UTF-8"));
				JSONStringer js = new JSONStringer();
				js.object().key("default_path").value("/storage/emulated/0").key("last_path").value(false).key("delete_converted").value(false).key("speed").value(0).key("wide").value(false).key("slide").value(false).key("guide").value(0).key("guide_fake").value(true).key("interval").value("0:1/12").key("random_falling").value(false).key("luck").value(false).key("default_speed").value(10.0).key("default_position").value(-278.1).endObject();
				bw.write(js.toString());
				bw.close();
			}
			if (!temp.exists()) temp.mkdirs();
			if (!dir.exists()) dir.mkdirs();
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(settings), "UTF-8"));
			final JSONObject jo = new JSONObject(br.readLine());
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(settings, false), "UTF-8"));
			JSONStringer js = new JSONStringer();
			js.object().key("default_path").value(jo.has("default_path") ? jo.getString("default_path") : "/storage/emulated/0").key("last_path").value(jo.has("last_path") ? jo.getBoolean("last_path") : false).key("delete_converted").value(jo.has("delete_converted") ? jo.getBoolean("delete_converted") : false).key("speed").value(jo.has("speed") ? jo.getInt("speed") : 0).key("wide").value(jo.has("wide") ? jo.getBoolean("wide") : false).key("slide").value(jo.has("slide") ? jo.getBoolean("slide") : false).key("guide").value(jo.has("guide") ? jo.getInt("guide") : 0).key("guide_fake").value(jo.has("guide_fake") ? jo.getBoolean("guide_fake") : true).key("interval").value(jo.has("interval") ? jo.getString("interval") : "0:1/12").key("random_falling").value(jo.has("random_falling") ? jo.getBoolean("random_falling") : false).key("luck").value(jo.has("luck") ? jo.getBoolean("luck") : false).key("default_speed").value(jo.has("default_speed") ? jo.getDouble("default_speed") : 10d).key("default_position").value(jo.has("default_position") ? jo.getDouble("default_position") : -278.15d).endObject();
			bw.write(js.toString());
			bw.close();
			loadChart.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View p1) {
						if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
							Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
							i.setType("*/*");
							i.addCategory(Intent.CATEGORY_OPENABLE);
							Toast.makeText(MainActivity.this, "请选择一张Malody谱面文件（.mcz或.zip）或Phigros谱面文件（.pez或.zip）！", Toast.LENGTH_LONG).show();
							startActivityForResult(i, 114);
						} else startActivityForResult(new Intent(MainActivity.this, FileSelectorActivity.class), 1);
					}
				}
			);
			manageLoadedChart.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view){
						if (chartList.isEmpty()) {
							Toast.makeText(MainActivity.this, "请先加载一张谱面！", Toast.LENGTH_SHORT).show();
							return;
						}
						final List<Switch> newChartList = new ArrayList<Switch>();
						for (Switch s : chartList) {
							Switch chart = new Switch(MainActivity.this);
							chart.setPadding(10, 0, 10, 5);
							chart.setText(s.getText());
							chart.setChecked(s.isChecked());
							newChartList.add(chart);
						}
						ScrollView sv = new ScrollView(MainActivity.this);
						final LinearLayout ll = new LinearLayout(MainActivity.this);
						ll.setOrientation(LinearLayout.VERTICAL);
						for (Switch s : newChartList) ll.addView(s);
						sv.addView(ll);
						AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
						adb.setIcon(R.drawable.ic_format_list_text).setTitle("管理已加载谱面").setMessage("选中一些谱面参与转换或将其删除：").setView(sv).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int p) {
									chartList = newChartList;
								}
							}
						).setNegativeButton(R.string.cancel, null).setNeutralButton("删除已选谱面", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int p) {
									for (int i = 0; i < newChartList.size(); i++) {
										if (newChartList.get(i).isChecked()) {
											new File(temp.getAbsolutePath() + File.separator + newChartList.get(i).getText().toString().split("\n")[0]).delete();
											chartList.remove(i);
										}
									}
								}
							}
						).show();
					}
				}
			);
			startConvert.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View p1) {
						final boolean isBPM = bpm.isChecked();
						final boolean isScroll = scroll.isChecked();
						final boolean isWide = wide.isChecked();
						final boolean isSlide = slide.isChecked();
						final boolean isGuideLegacy = guideLegacy.isEnabled() && guideLegacy.isChecked();
						final boolean isGuideNew = guideNew.isEnabled() && guideNew.isChecked();
						final boolean isGuideFake = guideFake.isEnabled() && guideFake.isChecked();
						final Integer[] interval = new Integer[]{ Integer.valueOf(guideInterval.getText().toString().substring(0, guideInterval.getText().toString().indexOf(":"))), Integer.valueOf(guideInterval.getText().toString().substring(guideInterval.getText().toString().indexOf(":") + 1, guideInterval.getText().toString().indexOf("/"))), Integer.valueOf(guideInterval.getText().toString().substring(guideInterval.getText().toString().indexOf("/") + 1)) };
						final boolean randomFallingValue = randomFalling.isChecked();
						final boolean luckValue = luck.isChecked();
						final double speedValue = Double.valueOf(speed.getText().toString());
						final double positionValue = Double.valueOf(position.getText().toString());
						final String illustratorValue = illustrator.getText().toString();
						if (chartList.isEmpty()) Toast.makeText(MainActivity.this, "请先加载一张谱面！", Toast.LENGTH_SHORT).show();
						else {
							new Thread(new Runnable(){
									@Override
									public void run() {
										int object = 0;
										if (isGuideLegacy) object = 1;
										else if (isGuideNew) object = 2;
										crash = functions.convert(false, isBPM, isScroll, isWide, isSlide, object, isGuideFake, interval, randomFallingValue, luckValue, speedValue, positionValue, illustratorValue);
										loading.cancel();
										runOnUiThread(new Runnable(){
												@Override
												public void run() {
													if (crash == null) Toast.makeText(MainActivity.this, "谱面转换完成！请到/storage/emulated/0/data/MVP寻找您的谱面！", Toast.LENGTH_LONG).show();
													else if (crash.isEmpty()) Toast.makeText(MainActivity.this, "?🤔🤔!", Toast.LENGTH_SHORT).show();
													else {
														try {
															throw new Exception("Failed to convert chart:\n" + crash);
														} catch (Exception e) {
															catcher(e);
														}
													}
												}
											}
										);
									}
								}
								, "convert").start();
							loading.show();
						}
					}
				}
			);
			keyToSlide.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View v) {
						final boolean isBPM = bpm.isChecked();
						final boolean isScroll = scroll.isChecked();
						final boolean isWide = wide.isChecked();
						final boolean isSlide = slide.isChecked();
						final boolean isGuideLegacy = guideLegacy.isEnabled() && guideLegacy.isChecked();
						final boolean isGuideNew = guideNew.isEnabled() && guideNew.isChecked();
						final boolean isGuideFake = guideFake.isEnabled() && guideFake.isChecked();
						final Integer[] interval = new Integer[]{ Integer.valueOf(guideInterval.getText().toString().substring(0, guideInterval.getText().toString().indexOf(":"))), Integer.valueOf(guideInterval.getText().toString().substring(guideInterval.getText().toString().indexOf(":") + 1, guideInterval.getText().toString().indexOf("/"))), Integer.valueOf(guideInterval.getText().toString().substring(guideInterval.getText().toString().indexOf("/") + 1)) };
						final boolean randomFallingValue = randomFalling.isChecked();
						final boolean luckValue = luck.isChecked();
						final double speedValue = Double.valueOf(speed.getText().toString());
						final double positionValue = Double.valueOf(position.getText().toString());
						final String illustratorValue = illustrator.getText().toString();
						if (chartList.isEmpty()) Toast.makeText(MainActivity.this, "请先加载一张谱面！", Toast.LENGTH_SHORT).show();
						else {
							new Thread(new Runnable(){
									@Override
									public void run() {
										int object = 0;
										if (isGuideLegacy) object = 1;
										else if (isGuideNew) object = 2;
										crash = functions.convert(true, isBPM, isScroll, isWide, isSlide, object, isGuideFake, interval, randomFallingValue, luckValue, speedValue, positionValue, illustratorValue);
										loading.cancel();
										runOnUiThread(new Runnable(){
												@Override
												public void run() {
													if (crash == null) Toast.makeText(MainActivity.this, "谱面转换完成！请到/storage/emulated/0/data/MVP寻找您的谱面！", Toast.LENGTH_LONG).show();
													else if (crash.isEmpty()) Toast.makeText(MainActivity.this, "?🤔🤔!", Toast.LENGTH_SHORT).show();
													else {
														try {
															throw new Exception("Failed to Key to Slide:\n" + crash);
														} catch (Exception e) {
															catcher(e);
														}
													}
												}
											}
										);
									}
								}
								, "keyToSlide").start();
							loading.show();
						}
					}
				}
			);
			if (jo.getInt("speed") == 1) bpm.setChecked(true);
			if (jo.getInt("speed") == 2) scroll.setChecked(true);
			if (jo.getInt("speed") == 3) {
				bpm.setChecked(true);
				scroll.setChecked(true);
			}
			if (jo.getBoolean("wide")) wide.setChecked(true);
			if (jo.getBoolean("slide")) {
				slide.setChecked(true);
				guide.setEnabled(true);
				if (guide.isChecked()) {
					guideLegacy.setEnabled(true);
					guideNew.setEnabled(true);
					guideFake.setEnabled(true);
					guideInterval.setEnabled(true);
					if (!guideLegacy.isChecked() && !guideNew.isChecked()) guideLegacy.setChecked(true);
				} else {
					guideLegacy.setEnabled(false);
					guideNew.setEnabled(false);
					guideFake.setEnabled(false);
					guideInterval.setEnabled(false);
				}
			}
			slide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (slide.isChecked()) {
							guide.setEnabled(true);
							if (guide.isChecked()) {
								guideLegacy.setEnabled(true);
								guideNew.setEnabled(true);
								guideFake.setEnabled(true);
								guideInterval.setEnabled(true);
								if (!guideLegacy.isChecked() && !guideNew.isChecked()) guideLegacy.setChecked(true);
							} else {
								guideLegacy.setEnabled(false);
								guideNew.setEnabled(false);
								guideFake.setEnabled(false);
								guideInterval.setEnabled(false);
							}
						} else {
							guide.setEnabled(false);
							guideLegacy.setEnabled(false);
							guideNew.setEnabled(false);
							guideFake.setEnabled(false);
							guideInterval.setEnabled(false);
						}
					}
				}
			);
			if (jo.getInt("guide") != 0) {
				guide.setEnabled(true);
				guide.setChecked(true);
				guideLegacy.setEnabled(true);
				guideNew.setEnabled(true);
				guideFake.setEnabled(true);
				guideInterval.setEnabled(true);
				if (jo.getInt("guide") == 1) guideLegacy.setChecked(true);
				if (jo.getInt("guide") == 2) guideNew.setChecked(true);
				if (!jo.getBoolean("guide_fake")) guideFake.setChecked(false);
			}
			guide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (guide.isChecked()) {
							guideLegacy.setEnabled(true);
							guideNew.setEnabled(true);
							guideFake.setEnabled(true);
							guideInterval.setEnabled(true);
							if (!guideLegacy.isChecked() && !guideNew.isChecked()) guideLegacy.setChecked(true);
						} else {
							guideLegacy.setEnabled(false);
							guideNew.setEnabled(false);
							guideFake.setEnabled(false);
							guideInterval.setEnabled(false);
						}
					}
				}
			);
			guideInterval.setText(jo.getString("interval"));
			randomFalling.setChecked(jo.getBoolean("random_falling"));
			luck.setChecked(jo.getBoolean("luck"));
			speed.setText(String.valueOf(jo.getDouble("default_speed")));
			position.setText(String.valueOf(jo.getDouble("default_position")));
			speedTool.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View v) {
						if (chartList.isEmpty()) Toast.makeText(MainActivity.this, "请先加载一张Phigros谱面！", Toast.LENGTH_SHORT).show();
						else {
							AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
							final EditText et = new EditText(MainActivity.this);
							et.setInputType(InputType.TYPE_CLASS_NUMBER);
							et.setText("1.0");
							adb.setTitle("谱面流速工具 v1.0").setMessage("请输入谱面流速更改的倍率（例如输入2.0会使谱面流速变为原来的2倍）：").setView(et).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
									@Override
									public void onClick(DialogInterface dialog, int which) {
										try {
											for (File f : temp.listFiles()) {
												if (f.getName().endsWith(".json")) {
													BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
													String json = "";
													String line = null;
													while ((line = reader.readLine()) != null) json += line;
													JSONObject main = new JSONObject(json);
													JSONStringer js = new JSONStringer();
													if (main.has("formatVersion") && main.getInt("formatVersion") == 3) {
														js.object().key("formatVersion").value(3).key("offset").value(main.getDouble("offset")).key("judgeLineList").array();
														JSONArray lineList = main.getJSONArray("judgeLineList");
														for (int i = 0; i < lineList.length(); i++) {
															JSONObject obj = lineList.getJSONObject(i);
															js.object().key("bpm").value(obj.getDouble("bpm"));
															JSONArray notesAbove = obj.getJSONArray("notesAbove");
															JSONArray notesBelow = obj.getJSONArray("notesBelow");
															JSONArray speedEvents = obj.getJSONArray("speedEvents");
															JSONArray moveEvents = obj.getJSONArray("judgeLineMoveEvents");
															JSONArray rotateEvents = obj.getJSONArray("judgeLineRotateEvents");
															JSONArray disappearEvents = obj.getJSONArray("judgeLineDisappearEvents");
															js.key("notesAbove").array();
															for (int j = 0; j < notesAbove.length(); j++) {
																JSONObject note = notesAbove.getJSONObject(j);
																if (note.getInt("type") == 3) js.object().key("type").value(3).key("time").value(note.getInt("time")).key("positionX").value(note.getDouble("positionX")).key("holdTime").value(note.getDouble("holdTime")).key("speed").value(note.getDouble("speed") * Double.valueOf(et.getText().toString())).key("floorPosition").value(note.getDouble("floorPosition") * Double.valueOf(et.getText().toString())).endObject();
																else js.object().key("type").value(note.getInt("type")).key("time").value(note.getInt("time")).key("positionX").value(note.getDouble("positionX")).key("holdTime").value(0.0).key("speed").value(note.getDouble("speed")).key("floorPosition").value(note.getDouble("floorPosition") * Double.valueOf(et.getText().toString())).endObject();
															}
															js.endArray().key("notesBelow").array();
															for (int j = 0; j < notesBelow.length(); j++) {
																JSONObject note = notesBelow.getJSONObject(j);
																if (note.getInt("type") == 3) js.object().key("type").value(3).key("time").value(note.getInt("time")).key("positionX").value(note.getDouble("positionX")).key("holdTime").value(note.getDouble("holdTime")).key("speed").value(note.getDouble("speed") * Double.valueOf(et.getText().toString())).key("floorPosition").value(note.getDouble("floorPosition") * Double.valueOf(et.getText().toString())).endObject();
																else js.object().key("type").value(note.getInt("type")).key("time").value(note.getInt("time")).key("positionX").value(note.getDouble("positionX")).key("holdTime").value(0.0).key("speed").value(note.getDouble("speed")).key("floorPosition").value(note.getDouble("floorPosition") * Double.valueOf(et.getText().toString())).endObject();
															}
															js.endArray().key("speedEvents").array();
															for (int j = 0; j < speedEvents.length(); j++) js.object().key("startTime").value(speedEvents.getJSONObject(j).getDouble("startTime")).key("endTime").value(speedEvents.getJSONObject(j).getDouble("endTime")).key("value").value(speedEvents.getJSONObject(j).getDouble("value") * Double.valueOf(et.getText().toString())).endObject();
															js.endArray().key("judgeLineMoveEvents").array();
															for (int j = 0; j < moveEvents.length(); j++) js.object().key("startTime").value(moveEvents.getJSONObject(j).getDouble("startTime")).key("endTime").value(moveEvents.getJSONObject(j).getDouble("endTime")).key("start").value(moveEvents.getJSONObject(j).getDouble("start")).key("end").value(moveEvents.getJSONObject(j).getDouble("end")).key("start2").value(moveEvents.getJSONObject(j).getDouble("start2")).key("end2").value(moveEvents.getJSONObject(j).getDouble("end2")).endObject();
															js.endArray().key("judgeLineRotateEvents").array();
															for (int j = 0; j < rotateEvents.length(); j++) js.object().key("startTime").value(rotateEvents.getJSONObject(j).getDouble("startTime")).key("endTime").value(rotateEvents.getJSONObject(j).getDouble("endTime")).key("start").value(rotateEvents.getJSONObject(j).getDouble("start")).key("end").value(rotateEvents.getJSONObject(j).getDouble("end")).endObject();
															js.endArray().key("judgeLineDisappearEvents").array();
															for (int j = 0; j < disappearEvents.length(); j++) js.object().key("startTime").value(disappearEvents.getJSONObject(j).getDouble("startTime")).key("endTime").value(disappearEvents.getJSONObject(j).getDouble("endTime")).key("start").value(disappearEvents.getJSONObject(j).getDouble("start")).key("end").value(disappearEvents.getJSONObject(j).getDouble("end")).endObject();
															js.endArray().endObject();
														}
														js.endArray().endObject();
													} else if (main.has("META") && main.getJSONObject("META").has("RPEVersion")) {
														Toast.makeText(MainActivity.this, "?🤔🤔!", Toast.LENGTH_SHORT).show();
													}
													BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f, false), "UTF-8"));
													writer.write(js.toString());
													writer.close();
													doZip(temp.getAbsolutePath(), dir.getAbsolutePath() + File.separator + f.getName() + ".pez");
												}
											}
										} catch (Exception e) {
											catcher(e);
										}
									}
								}
							).setNegativeButton(R.string.cancel, null).show();
						}
					}
				}
			);
		} catch (Exception e) {
			catcher(e);
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		packageName = getPackageName();
		resources = getResources();
		settings = new File("/data/data/" + getPackageName() + "/files/settings.json");
		temp = new File("/data/data/" + getPackageName() + "/cache");
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS }, 114);
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) init();
	}
	public void catcher(final Exception e) {
		for (File f : temp.listFiles()) {
			if (f.isDirectory()) for (File c : f.listFiles()) c.delete();
			f.delete();
		}
		final StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw, true));
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setIcon(android.R.drawable.ic_delete);
		adb.setTitle(R.string.crash_title);
		adb.setMessage(sw.toString());
		adb.setPositiveButton(R.string.crash_ok, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface p1, int p2) {
					ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
					ClipData cd = ClipData.newPlainText("Error message from MalodyVersusPhigros by DingZiHu", sw.toString());
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
		adb.setCancelable(false).show();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater mi = new MenuInflater(this);
		mi.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.clear_tmp:
				for (File f : temp.listFiles()) {
					if (f.isDirectory()) for (File c : f.listFiles()) c.delete();
					f.delete();
				}
				charts.clear();
				chartList.clear();
				Toast.makeText(this, "已清空已加载谱面！", Toast.LENGTH_SHORT).show();
				return true;
			case R.id.settings:
				Intent i = new Intent(this, SettingsActivity.class);
				startActivity(i);
				return true;
			case R.id.about:
				AlertDialog.Builder adb = new AlertDialog.Builder(this);
				adb.setIcon(R.drawable.ic_launcher).setTitle(R.string.app_name).setMessage("MalodyVersusPhigros v1.5.3 by 起名钉子户\n想催更？给我的视频投114514颗币就可以啦！（被打）\n如果你想向我报告一些bug的话，请立即联系我！！！\n（因为晚一点可能就被其他人抢走了😂）");
				adb.setPositiveButton(R.string.about_ok, null).show();
				return true;
			case R.id.update_log:
				try {
					InputStream is = getResources().openRawResource(R.raw.update);
					BufferedReader reader = new BufferedReader(new InputStreamReader(is));
					String line = null;
					String log = "";
					while ((line = reader.readLine()) != null) log += (line + "\n");
					adb = new AlertDialog.Builder(this);
					adb.setIcon(android.R.drawable.ic_dialog_info).setTitle("更新日志").setMessage(log.substring(0, log.length() - 1)).setPositiveButton(R.string.about_ok, null).show();
				} catch (Exception e) {
					catcher(e);
				}
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onActivityResult(final int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			try {
				final String path = (requestCode == 1 ? data.getStringExtra("path") : getPath(this, data.getData()));
				new Thread(new Runnable(){
						@Override
						public void run() {
							crash = load(path);
							charts.add(new File(path));
							loading.cancel();
							runOnUiThread(new Runnable(){
									@Override
									public void run() {
										if (crash == null) Toast.makeText(MainActivity.this, "谱面加载完成！", Toast.LENGTH_SHORT).show();
										else if (crash.isEmpty()) Toast.makeText(MainActivity.this, "?🤔🤔!", Toast.LENGTH_SHORT).show();
										else {
											try {
												throw new Exception("Failed to load chart:\n" + crash);
											} catch (Exception e) {
												catcher(e);
											}
										}
									}
								}
							);
						}
					}
				, "load").start();
				loading.show();
			} catch (Exception e) {
				catcher(e);
			}
		}
	}
	public String load(final String path) {
		try {
			if (!temp.exists()) temp.mkdirs();
			if (!dir.exists()) dir.mkdirs();
			ZipInputStream zis = new ZipInputStream(new FileInputStream(path));
			ZipEntry ze = zis.getNextEntry();
			byte[] buffer = new byte[1024 * 1024 * 8];
			int count = 0;
			while (ze != null) {
				if (!ze.isDirectory()) {
					String name = ze.getName();
					name = name.substring(name.lastIndexOf("/") + 1);
					File file = new File(temp.getAbsolutePath() + File.separator + name);
					file.createNewFile();
					FileOutputStream fos = new FileOutputStream(file);
					while ((count = zis.read(buffer)) > 0) fos.write(buffer, 0, count);
					fos.flush();
					fos.close();
					final Switch chart = new Switch(MainActivity.this);
					runOnUiThread(new Runnable() {
							@Override
							public void run() {
								chart.setChecked(true);
							}
						}
					);
					if (file.getName().endsWith(".mc")) {
						BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
						String json = "";
						String line = null;
						while ((line = br.readLine()) != null) json += line;
						JSONObject meta = new JSONObject(json).getJSONObject("meta");
						String mode = "未知";
						switch (meta.getInt("mode")) {
							case 0:
								mode = meta.getJSONObject("mode_ext").getInt("column") + "K";
								break;
							case 3:
								mode = "Catch";
								break;
							case 5:
								mode = "Taiko";
								break;
							case 7:
								mode = "Slide";
								break;
						}
						chart.setText(file.getName() + "\n    曲名：" + meta.getJSONObject("song").getString("title") + "\n    曲师：" + meta.getJSONObject("song").getString("artist") + "\n    模式：" + mode + "(" + meta.getInt("mode") + ")\n    难度：" + meta.getString("version") + "\n    谱师：" + meta.getString("creator"));
						chartList.add(chart);
					} else if (file.getName().endsWith(".osu")) {
						chart.setText(file.getName() + "\n    osu!谱面");
						chartList.add(chart);
					} else if (file.getName().endsWith(".json") || file.getName().endsWith(".pec")) {
						chart.setText(file.getName() + "\n    Phigros谱面");
						chartList.add(chart);
					} else if (file.getName().equalsIgnoreCase("effect.mvp")) {
						chart.setText(file.getName() + "\n    MalodyVersusPhigros效果文件");
						chartList.add(chart);
					}
				}
				ze = zis.getNextEntry();
			}
			zis.close();
			return null;
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			return sw.toString();
		}
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case 114:
				if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
					Toast.makeText(this, "权限申请失败。请在您手机的设置中授予。", Toast.LENGTH_LONG).show();
					finish();
				} else init();
				break;
			default:
		}
	}
	public void copy(File source, String dest) {
		try {
			InputStream is = null;
			OutputStream os = null;
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			byte[] buffer = new byte[1024 * 1024 * 8];
			int length;
			while ((length = is.read(buffer)) > 0) os.write(buffer, 0, length);
			is.close();
			os.flush();
			os.close();
		} catch (Exception e) {
			catcher(e);
		}
	}
	public void doZip(String srcPath, String targetPath) {
		try {
			FileOutputStream fos = new FileOutputStream(targetPath);
			ZipOutputStream zos = new ZipOutputStream(fos);
			File src = new File(srcPath);
			zip(src, src.getName(), zos);
			zos.flush();
			zos.close();
			fos.flush();
			fos.close();
		} catch (Exception e) {
			catcher(e);
		}
	}
	private void zip(File srcFile, String srcName, ZipOutputStream zos) throws IOException {
		if (srcFile.isDirectory()) {
			File[] children = srcFile.listFiles();
			for (File cFile : children) zip(cFile, cFile.getName(), zos);
			return;
		} else {
			FileInputStream fis = new FileInputStream(srcFile);
			ZipEntry ze = new ZipEntry(srcName);
			zos.putNextEntry(ze);
			byte[] bs = new byte[1024 * 1024 * 8];
			int length;
			while ((length = fis.read(bs)) >= 0) zos.write(bs, 0, length);
			fis.close();
		}
	}
	public String getPath(final Context context, final Uri uri) {
		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];
				if ("primary".equalsIgnoreCase(type)) return Environment.getExternalStorageDirectory() + "/" + split[1];
			} else if (isDownloadsDocument(uri)) {
				final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), ContentUris.parseId(uri));
				return getDataColumn(context, contentUri, null, null);
			} else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];
				Uri contentUri = null;
				if ("image".equals(type)) contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				else if ("video".equals(type)) contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				else if ("audio".equals(type)) contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				final String selection = "_id=?";
				final String[] selectionArgs = new String[]{ split[1] };
				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		} else if ("content".equalsIgnoreCase(uri.getScheme())) return getDataColumn(context, uri, null, null);
        else if ("file".equalsIgnoreCase(uri.getScheme())) return uri.getPath();
		return null;
	}
	public String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };
		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int column_index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(column_index);
			}
		} finally {
			if (cursor != null) cursor.close();
		}
		return null;
	}
	public boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}
	public boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}
	public boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}
}

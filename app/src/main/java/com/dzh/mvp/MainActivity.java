package com.dzh.mvp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.documentfile.provider.DocumentFile;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

public class MainActivity extends AppCompatActivity {
	public static Resources resources;
	public static File settings;
	public static File temp;
	public File dir = new File("/storage/emulated/0/data/MVP");
	public String crash = null;
	public ProgressDialog loading = null;
	public static List<File> charts = new ArrayList<File>();
	public static List<Map<String, Object>> chartList = new ArrayList<Map<String, Object>>();
	public Functions functions;
	public JSONObject jo;
	public void init() {
		try {
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
			final Spinner slide = findViewById(R.id.slide);
			final Switch guide = findViewById(R.id.guide);
			final EditText guideInterval = findViewById(R.id.guide_interval);
			final EditText defaultWide = findViewById(R.id.default_wide);
			final Switch randomFalling = findViewById(R.id.random_falling);
			final Switch luck = findViewById(R.id.luck);
			final EditText speed = findViewById(R.id.speed);
			final EditText position = findViewById(R.id.position);
			final EditText illustrator = findViewById(R.id.illustrator);
			if (!settings.exists()) {
				settings.getParentFile().mkdirs();
				settings.createNewFile();
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(settings, false), "UTF-8"));
				bw.write(new JSONStringer().object().key("system_file_selector").value(false).key("default_path").value("/storage/emulated/0").key("last_path").value(false).key("delete_converted").value(false).key("bpm").value(false).key("scroll").value(false).key("wide").value(false).key("slide").value(0).key("guide").value(false).key("interval").value("0:1/12").key("default_wide").value(51).key("random_falling").value(false).key("luck").value(false).key("default_speed").value(10d).key("default_position").value(-278.1).endObject().toString());
				bw.close();
			}
			if (!temp.exists()) temp.mkdirs();
			if (!dir.exists()) dir.mkdirs();
			BufferedReader setbr = new BufferedReader(new InputStreamReader(new FileInputStream(settings), "UTF-8"));
			JSONObject setjo = new JSONObject(setbr.readLine());
			setbr.close();
			jo = new JSONObject();
			jo.put("system_file_selector", setjo.has("system_file_selector") ? setjo.getBoolean("system_file_selector") : false).put("default_path", setjo.has("default_path") ? setjo.getString("default_path") : "/storage/emulated/0").put("last_path", setjo.has("last_path") ? setjo.getBoolean("last_path") : false).put("delete_converted", setjo.has("delete_converted") ? setjo.getBoolean("delete_converted") : false).put("bpm", setjo.has("bpm") ? setjo.getBoolean("bpm") : false).put("scroll", setjo.has("scroll") ? setjo.getBoolean("scroll") : false).put("wide", setjo.has("wide") ? setjo.getBoolean("wide") : false).put("slide", setjo.has("slide") ? setjo.getInt("slide") : 0).put("guide", setjo.has("guide") ? setjo.getBoolean("guide") : false).put("interval", setjo.has("interval") ? setjo.getString("interval") : "0:1/12").put("default_wide", setjo.has("default_wide") ? setjo.getInt("default_wide") : 51).put("random_falling", setjo.has("random_falling") ? setjo.getBoolean("random_falling") : false).put("luck", setjo.has("luck") ? setjo.getBoolean("luck") : false).put("default_speed", setjo.has("default_speed") ? setjo.getDouble("default_speed") : 10d).put("default_position", setjo.has("default_position") ? setjo.getDouble("default_position") : -278.1);
			loadChart.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View p1) {
						final AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
						adb.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, new String[]{ "从Malody本地谱面加载", "加载其他谱面" }), new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									try {
										switch (which) {
											case 0:
												startActivityForResult(new Intent(MainActivity.this, LoadFromMalodyActivity.class), 1);
												break;
											case 1:
												if (jo.getBoolean("system_file_selector")) startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE).setType("*/*"), 321);
												else startActivityForResult(new Intent(MainActivity.this, FileSelectorActivity.class).putExtra("path", jo.getString("default_path")), 2);
												break;
										}
									} catch (Exception e) {
										catcher(e);
									}
								}
							}
						).show();
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
						final List<Map<String, Object>> newChartList = new ArrayList<Map<String, Object>>();
						for (Map<String, Object> m : chartList) {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("name", m.get("name"));
							map.put("properties", m.get("properties"));
							map.put("checked", m.get("checked"));
							newChartList.add(map);
						}
						final ScrollView sv = new ScrollView(MainActivity.this);
						ListView lv = new ListView(MainActivity.this);
						SimpleAdapter sa = new SimpleAdapter(MainActivity.this, newChartList, R.layout.chart_manager_items, new String[]{ "properties", "name", "checked" }, new int[]{ R.id.properties, R.id.name, R.id.checked });
						lv.setAdapter(sa);
						lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
								@Override
								public void onItemClick(AdapterView<?> adapterView, View view, int p, long p1) {
									Switch s = view.findViewById(R.id.checked);
									s.setChecked(!s.isChecked());
									newChartList.get(p).put("checked", s.isChecked());
								}
							}
						);
						sv.addView(lv);
						AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
						adb.setIcon(R.drawable.ic_format_list_text).setTitle("管理已加载谱面").setMessage("选中一些文件参与转换或将其删除：").setView(sv).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int p) {
									chartList = newChartList;
									sv.removeAllViews();
								}
							}
						).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int p) {
									sv.removeAllViews();
								}
							}
						).setNeutralButton("删除已选文件", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int p) {
									for (int i = newChartList.size() - 1; i >= 0; i--) {
										Map<String, Object> m = newChartList.get(i);
										if ((boolean) m.get("checked")) {
											new File(temp.getAbsolutePath() + File.separator + ((String) m.get("name"))).delete();
											chartList.remove(i);
										}
									}
									sv.removeAllViews();
								}
							}
						).setCancelable(false).show();
					}
				}
			);
			startConvert.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View p1) {
						final boolean isBPM = bpm.isChecked();
						final boolean isScroll = scroll.isChecked();
						final boolean isWide = wide.isChecked();
						final int slideValue = slide.getSelectedItemPosition();
						final boolean isGuide = guide.isChecked();
						final String interval = guideInterval.getText().toString();
						final int wideValue = Integer.parseInt(defaultWide.getText().toString());
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
										crash = functions.convert(false, isBPM, isScroll, isWide, slideValue, isGuide, interval, wideValue, randomFallingValue, luckValue, speedValue, positionValue, illustratorValue);
										loading.cancel();
										runOnUiThread(new Runnable(){
												@Override
												public void run() {
													if (crash == null) Toast.makeText(MainActivity.this, "谱面转换完成！请到" + dir.getAbsolutePath() + "寻找您的谱面！", Toast.LENGTH_LONG).show();
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
						final int slideValue = slide.getSelectedItemPosition();
						final boolean isGuide = guide.isChecked();
						final String interval = guideInterval.getText().toString();
						final int wideValue = Integer.parseInt(defaultWide.getText().toString());
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
										crash = functions.convert(true, isBPM, isScroll, isWide, slideValue, isGuide, interval, wideValue, randomFallingValue, luckValue, speedValue, positionValue, illustratorValue);
										loading.cancel();
										runOnUiThread(new Runnable(){
												@Override
												public void run() {
													if (crash == null) Toast.makeText(MainActivity.this, "谱面转换完成！请到" + dir.getAbsolutePath() + "寻找您的谱面！", Toast.LENGTH_LONG).show();
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
								, "keyToSlide").start();
							loading.show();
						}
					}
				}
			);
			if (jo.getBoolean("bpm")) bpm.setChecked(true);
			if (jo.getBoolean("scroll")) scroll.setChecked(true);
			if (jo.getBoolean("wide")) wide.setChecked(true);
			slide.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{ "直长条", "Tap+Drag", "移动长条" }));
			slide.setSelection(jo.getInt("slide"));
			switch (jo.getInt("slide")) {
				case 0:
					guide.setEnabled(false);
					guideInterval.setEnabled(false);
					break;
				case 1:
					guide.setEnabled(false);
					guideInterval.setEnabled(true);
					break;
				case 2:
					guide.setEnabled(true);
					guideInterval.setEnabled(true);
			}
			guide.setChecked(jo.getBoolean("guide"));
			slide.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> adapterView, View view, int p, long p1) {
						switch (p) {
							case 0:
								guide.setEnabled(false);
								guideInterval.setEnabled(false);
								break;
							case 1:
								guide.setEnabled(false);
								guideInterval.setEnabled(true);
								break;
							case 2:
								guide.setEnabled(true);
								try {
									guideInterval.setEnabled(guide.isChecked());
								} catch (Exception e) {
									catcher(e);
								}
						}
					}
					@Override
					public void onNothingSelected(AdapterView<?> adapterView) {}
				}
			);
			guide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						guideInterval.setEnabled(isChecked);
					}
				}
			);
			guideInterval.setText(jo.getString("interval"));
			defaultWide.setText(String.valueOf(jo.getInt("default_wide")));
			randomFalling.setChecked(jo.getBoolean("random_falling"));
			luck.setChecked(jo.getBoolean("luck"));
			speed.setText(String.valueOf(jo.getDouble("default_speed")));
			position.setText(String.valueOf(jo.getDouble("default_position")));
			new Thread(new Runnable() {
					@Override
					public void run() {
						refreshChartList();
						loading.cancel();
					}
				}
				, "refreshChartList").start();
			loading.show();
		} catch (Exception e) {
			catcher(e);
		}
	}
	public void refreshChartList() {
		try {
			chartList.clear();
			for (File file : temp.listFiles()) {
				String name = file.getName();
				String properties = "";
				Map<String, Object> chart = new HashMap<String, Object>();
				if (file.getName().toLowerCase().endsWith(".mc")) {
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
					properties = "Malody谱面\n曲名：" + meta.getJSONObject("song").getString("title") + "\n曲师：" + meta.getJSONObject("song").getString("artist") + "\n模式：" + mode + "(" + meta.getInt("mode") + ")\n难度：" + meta.getString("version") + "\n谱师：" + meta.getString("creator");
					br.close();
				} else if (file.getName().toLowerCase().endsWith(".osu")) {
					String[] prop = new String[5];
					BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
						String line = null;
						while ((line = br.readLine()) != null) {
							if (line.startsWith("Title:")) prop[0] = line.substring(line.indexOf(":") + 1).trim() + "\n";
							else if (line.startsWith("Artist:")) prop[1] = "曲师：" + line.substring(line.indexOf(":") + 1).trim() + "\n";
							else if (line.startsWith("Mode:")) prop[2] = "模式：" + (Integer.parseInt(line.split(":")[1].trim()) == 3 ? "osu!mania(3)\n" : "未知(" + line.split(":")[1] + ")\n");
							else if (line.startsWith("Version:")) prop[3] = "难度：" + line.substring(line.indexOf(":") + 1).trim() + "\n";
							else if (line.startsWith("Creator:")) prop[4] = "谱师：" + line.substring(line.indexOf(":") + 1).trim();
						}
					properties = "osu!谱面\n曲名：" + prop[0] + prop[1] + prop[2] + prop[3] + prop[4];
					br.close();
				} else if (file.getName().toLowerCase().endsWith(".json") || file.getName().endsWith(".pec")) properties = "Phigros谱面";
				else if (file.getName().toLowerCase().endsWith(".mvp")) properties = "MalodyVersusPhigros脚本文件";
				else if (file.getName().toLowerCase().endsWith(".extra")) properties = "无需额外处理的文件集";
				if (!properties.isEmpty()) {
					chart.put("name", name);
					chart.put("properties", properties);
					chart.put("checked", true);
				}
				if (!chart.isEmpty()) chartList.add(chart);
			}
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
		resources = getResources();
		settings = new File(getFilesDir().getAbsolutePath() + File.separator + "settings.json");
		temp = getCacheDir();
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 114);
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) init();
	}
	public void catcher(final Exception e) {
		final StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw, true));
		runOnUiThread(new Runnable() {
				@Override
				public void run() {
					AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater mi = new MenuInflater(this);
		mi.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try {
			switch (item.getItemId()) {
				case R.id.save_prop:
					Switch bpm = findViewById(R.id.bpm);
					Switch scroll = findViewById(R.id.scroll);
					Switch wide = findViewById(R.id.wide);
					Spinner slide = findViewById(R.id.slide);
					Switch guide = findViewById(R.id.guide);
					EditText guideInterval = findViewById(R.id.guide_interval);
					Switch randomFalling = findViewById(R.id.random_falling);
					Switch luck = findViewById(R.id.luck);
					EditText speed = findViewById(R.id.speed);
					EditText position = findViewById(R.id.position);
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(settings, false), "UTF-8"));
					bw.write(new JSONStringer().object().key("default_path").value(jo.getString("default_path")).key("last_path").value(jo.getBoolean("last_path")).key("delete_converted").value(jo.getBoolean("delete_converted")).key("bpm").value(bpm.isChecked()).key("scroll").value(scroll.isChecked()).key("wide").value(wide.isChecked()).key("slide").value(slide.getSelectedItemPosition()).key("guide").value(guide.isChecked()).key("interval").value(guideInterval.getText().toString()).key("random_falling").value(randomFalling.isChecked()).key("luck").value(luck.isChecked()).key("default_speed").value(speed.getText().toString()).key("default_position").value(position.getText().toString()).endObject().toString());
					bw.close();
					Toast.makeText(this, "配置已保存！", Toast.LENGTH_SHORT).show();
					break;
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
					startActivityForResult(new Intent(this, SettingsActivity.class).putExtra("jo", jo.toString()), 3);
					return true;
				case R.id.about:
					AlertDialog.Builder adb = new AlertDialog.Builder(this);
					adb.setIcon(R.drawable.ic_launcher).setTitle(R.string.app_name).setMessage("MalodyVersusPhigros v" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName + " by 起名钉子户\nGitHub开源地址：https://github.com/DingZiHu-Git/MalodyVersusPhigros\n想催更？给我的视频投114514颗币就可以啦！（被打）\n如果你想向我报告一些bug的话，请立即联系我！！！\n（因为晚一点可能就被其他人抢走了😂）").setPositiveButton(R.string.about_ok, null).show();
					return true;
				case R.id.update_log:
					InputStream is = getResources().openRawResource(R.raw.update);
					BufferedReader reader = new BufferedReader(new InputStreamReader(is));
					String line = null;
					String log = "";
					while ((line = reader.readLine()) != null) log += (line + "\n");
					adb = new AlertDialog.Builder(this);
					adb.setIcon(android.R.drawable.ic_dialog_info).setTitle("更新日志").setMessage(log.substring(0, log.length() - 1)).setPositiveButton(R.string.about_ok, null).show();
					return true;
			}
		} catch (Exception e) {
			catcher(e);
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onActivityResult(final int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			try {
				switch (requestCode) {
					case 1:
						final String[] files = data.getStringArrayExtra("files");
						new Thread(new Runnable() {
								@Override
								public void run() {
									try {
										for (String file : files) copy(new File(file), temp.getAbsolutePath() + File.separator + file.substring(file.lastIndexOf("/") + 1));
										refreshChartList();
									} catch (Exception e) {
										catcher(e);
									}
									loading.cancel();
									runOnUiThread(new Runnable() {
											@Override
											public void run() {
												Toast.makeText(MainActivity.this, "谱面加载完成！", Toast.LENGTH_SHORT).show();
											}
										}
									);
								}
							}
						, "load_from_malody").start();
						loading.show();
						break;
					case 2:
						final String path = data.getStringExtra("path");
						if (jo.getBoolean("last_path")) jo.put("default_path", new File(path).getParent());
						if (!(path.toLowerCase().endsWith(".mcz") || path.toLowerCase().endsWith(".zip") || path.toLowerCase().endsWith(".pez") || path.toLowerCase().endsWith(".osz"))) {
							Toast.makeText(MainActivity.this, "文件格式不正确！", Toast.LENGTH_SHORT).show();
							return;
						}
						new Thread(new Runnable() {
								@Override
								public void run() {
									crash = load(path);
									charts.add(new File(path));
									refreshChartList();
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
						break;
					case 3:
						jo = new JSONObject(data.getStringExtra("jo"));
						break;
					case 321:
						new Thread(new Runnable() {
								@Override
								public void run() {
									try {
										String name = DocumentFile.fromSingleUri(MainActivity.this, data.getData()).getName();
										if (!(name.toLowerCase().endsWith(".mcz") || name.toLowerCase().endsWith(".zip") || name.toLowerCase().endsWith(".pez") || name.toLowerCase().endsWith(".osz"))) {
											Toast.makeText(MainActivity.this, "文件格式不正确！", Toast.LENGTH_SHORT).show();
											return;
										}
										ZipInputStream zis = new ZipInputStream(getContentResolver().openInputStream(data.getData()));
										ZipEntry ze = zis.getNextEntry();
										byte[] buffer = new byte[1024 * 1024];
										int count = 0;
										while (ze != null) {
											if (!ze.isDirectory()) {
												String fn = ze.getName();
												fn = fn.substring(fn.lastIndexOf("/") + 1);
												File file = new File(temp.getAbsolutePath() + File.separator + fn);
												file.createNewFile();
												FileOutputStream fos = new FileOutputStream(file);
												while ((count = zis.read(buffer)) > 0) fos.write(buffer, 0, count);
												fos.flush();
												fos.close();
											}
											ze = zis.getNextEntry();
										}
										zis.close();
										refreshChartList();
										loading.cancel();
										runOnUiThread(new Runnable() {
												@Override
												public void run() {
													Toast.makeText(MainActivity.this, "谱面加载完成！", Toast.LENGTH_SHORT).show();
												}
											}
										);
									} catch (Exception e) {
										catcher(e);
									}
								}
							}
						).start();
						loading.show();
						break;
				}
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(settings, false), "UTF-8"));
				bw.write(jo.toString());
				bw.close();
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
			byte[] buffer = new byte[1024 * 1024];
			int count = 0;
			while (ze != null) {
				if (!ze.isDirectory()) {
					String fn = ze.getName();
					fn = fn.substring(fn.lastIndexOf("/") + 1);
					File file = new File(temp.getAbsolutePath() + File.separator + fn);
					file.createNewFile();
					FileOutputStream fos = new FileOutputStream(file);
					while ((count = zis.read(buffer)) > 0) fos.write(buffer, 0, count);
					fos.flush();
					fos.close();
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
		int result = 0;
		for (int i : grantResults) if (i == PackageManager.PERMISSION_GRANTED) result++;
		if (result != permissions.length) {
			Toast.makeText(this, "获取权限失败。请前往手机设置授予。", Toast.LENGTH_LONG).show();
			finish();
		} else init();
	}
	public void copy(File source, String dest) {
		try {
			InputStream is = null;
			OutputStream os = null;
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			byte[] buffer = new byte[1024 * 1024];
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
			byte[] bs = new byte[1024 * 1024];
			int length;
			while ((length = fis.read(bs)) >= 0) zos.write(bs, 0, length);
			fis.close();
		}
	}
}

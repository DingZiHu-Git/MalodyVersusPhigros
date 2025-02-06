package com.dzh.mvp;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import android.view.MenuItem;

public class LoadFromMalodyActivity extends AppCompatActivity {
	public ProgressDialog loading;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load_from_malody);
		Toolbar toolbar = findViewById(R.id.load_from_malody_toolbar);
		setSupportActionBar(toolbar);
		loading = new ProgressDialog(this);
		loading.setCancelable(false);
		loading.setMessage("请稍后……");
		loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		loading.setIndeterminate(true);
		final List<Map<String, String>> charts = new ArrayList<Map<String, String>>();
		ListView malodyCharts = findViewById(R.id.malody_charts);
		final SimpleAdapter malodyChartsAdapter = new SimpleAdapter(this, charts, R.layout.malody_charts_items, new String[]{ "music", "description" }, new int[]{ R.id.music, R.id.description });
		malodyCharts.setAdapter(malodyChartsAdapter);
		malodyCharts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					setResult(RESULT_OK, new Intent().putExtra("files", new String[]{ charts.get(position).get("chart"), charts.get(position).get("audio"), charts.get(position).get("background") }));
					finish();
				}
			}
		);
		new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						for (File d : new File("/storage/emulated/0/data/malody/beatmap").listFiles()) {
							if (d.isDirectory()) for (File f : d.listFiles()) {
								if (f.getName().toLowerCase().endsWith(".mc")) {
									Map<String, String> map = new HashMap<String, String>();
									map.put("chart", f.getAbsolutePath());
									BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
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
									JSONArray note = new JSONObject(json).getJSONArray("note");
									for (int i = note.length() - 1; i > 0; i--) if (note.getJSONObject(i).has("sound")) map.put("audio", d.getAbsolutePath() + File.separator + note.getJSONObject(i).getString("sound"));
									map.put("background", d.getAbsolutePath() + File.separator + meta.getString("background"));
									map.put("music", meta.getJSONObject("song").getString("title"));
									map.put("description", "曲师：" + meta.getJSONObject("song").getString("artist") + "\n模式：" + mode + "(" + meta.getInt("mode") + ")\n难度：" + meta.getString("version") + "\n谱师：" + meta.getString("creator"));
									br.close();
									charts.add(map);
								} else if (f.getName().toLowerCase().endsWith(".osu")) {
									Map<String, String> map = new HashMap<String, String>();
									map.put("chart", f.getAbsolutePath());
									String[] description = new String[4];
									BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
									String line = null;
									while ((line = br.readLine()) != null) {
									if (line.startsWith("AudioFilename:")) map.put("audio", d.getAbsolutePath() + File.separator + line.substring(line.indexOf(":") + 1).trim());
										else if (line.startsWith("0,0,")) map.put("background", d.getAbsolutePath() + File.separator + line.split(",")[2].trim().substring(1, line.split(",")[2].trim().length() - 1));
										else if (line.startsWith("Title:")) map.put("music", line.substring(line.indexOf(":") + 1).trim());
										else if (line.startsWith("Artist:")) description[0] = "曲师：" + line.substring(line.indexOf(":") + 1).trim() + "\n";
										else if (line.startsWith("Mode:")) description[1] = "模式：" + (Integer.parseInt(line.split(":")[1].trim()) == 3 ? "osu!mania(3)\n" : "未知(" + line.split(":")[1] + ")\n");
										else if (line.startsWith("Version:")) description[2] = "难度：" + line.substring(line.indexOf(":") + 1).trim() + "\n";
										else if (line.startsWith("Creator:")) description[3] = "谱师：" + line.substring(line.indexOf(":") + 1).trim();
									}
									map.put("description", description[0] + description[1] + description[2] + description[3]);
									br.close();
									charts.add(map);
								} else if (f.isDirectory()) for (File c : f.listFiles()) {
									if (c.getName().toLowerCase().endsWith(".mc")) {
										Map<String, String> map = new HashMap<String, String>();
										map.put("chart", c.getAbsolutePath());
										BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(c), "UTF-8"));
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
										JSONArray note = new JSONObject(json).getJSONArray("note");
										for (int i = note.length() - 1; i > 0; i--) if (note.getJSONObject(i).has("sound")) map.put("audio", f.getAbsolutePath() + File.separator + note.getJSONObject(i).getString("sound"));
										map.put("background", f.getAbsolutePath() + File.separator + meta.getString("background"));
										map.put("music", meta.getJSONObject("song").getString("title"));
										map.put("description", "曲师：" + meta.getJSONObject("song").getString("artist") + "\n模式：" + mode + "(" + meta.getInt("mode") + ")\n难度：" + meta.getString("version") + "\n谱师：" + meta.getString("creator"));
										br.close();
										charts.add(map);
									} else if (c.getName().toLowerCase().endsWith(".osu")) {
										Map<String, String> map = new HashMap<String, String>();
										map.put("chart", c.getAbsolutePath());
										String[] description = new String[4];
										BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(c), "UTF-8"));
										String line = null;
										while ((line = br.readLine()) != null) {
											if (line.startsWith("AudioFilename:")) map.put("audio", f.getAbsolutePath() + File.separator + line.substring(line.indexOf(":") + 1).trim());
											else if (line.startsWith("0,0,")) map.put("background", f.getAbsolutePath() + File.separator + line.split(",")[2].trim().substring(1, line.split(",")[2].trim().length() - 1));
											else if (line.startsWith("Title:")) map.put("music", line.substring(line.indexOf(":") + 1).trim());
											else if (line.startsWith("Artist:")) description[0] = "曲师：" + line.substring(line.indexOf(":") + 1).trim() + "\n";
											else if (line.startsWith("Mode:")) description[1] = "模式：" + (Integer.parseInt(line.split(":")[1].trim()) == 3 ? "osu!mania(3)\n" : "未知(" + line.split(":")[1] + ")\n");
											else if (line.startsWith("Version:")) description[2] = "难度：" + line.substring(line.indexOf(":") + 1).trim() + "\n";
											else if (line.startsWith("Creator:")) description[3] = "谱师：" + line.substring(line.indexOf(":") + 1).trim();
										}
										map.put("description", description[0] + description[1] + description[2] + description[3]);
										br.close();
										charts.add(map);
									}
								}
							}
						}
						runOnUiThread(new Runnable() {
								@Override
								public void run() {
									malodyChartsAdapter.notifyDataSetChanged();
								}
							}
						);
						loading.cancel();
					} catch (final Exception e) {
						runOnUiThread(new Runnable() {
								@Override
								public void run() {
									catcher(e);
								}
							}
						);
					}
				}
			}
		, "init").start();
		loading.show();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater mi = new MenuInflater(this);
		mi.inflate(R.menu.load_from_malody, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try {
			switch (item.getItemId()) {
				case R.id.filter:
					AlertDialog.Builder adb = new AlertDialog.Builder(this);
					adb.setIcon(R.drawable.ic_filter_outline);
					break;
			}
		} catch (Exception e) {
			catcher(e);
		}
		return super.onOptionsItemSelected(item);
	}
	public void catcher(final Exception e) {
		final StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw, true));
		runOnUiThread(new Runnable() {
				@Override
				public void run() {
					AlertDialog.Builder adb = new AlertDialog.Builder(LoadFromMalodyActivity.this);
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
}

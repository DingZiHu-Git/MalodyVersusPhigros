package com.dzh.mvp;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.json.JSONStringer;

public class FileSelectorActivity extends AppCompatActivity {
	public File settings;
	public File directory;
	public List<Map<String, Object>> listItems;
	List<File> items;
	SimpleAdapter listAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_selector);
		Toolbar toolbar = findViewById(R.id.file_selector_toolbar);
		setSupportActionBar(toolbar);
		try {
			settings = new File("/data/data/" + getPackageName() + "/files/settings.json");
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(settings), "UTF-8"));
			final JSONObject jo = new JSONObject(br.readLine());
			directory = new File(jo.getString("default_path"));
			final boolean deleteConverted = jo.getBoolean("delete_converted");
			final int speed = jo.getInt("speed");
			final boolean wide = jo.getBoolean("wide");
			final boolean slide = jo.getBoolean("slide");
			final int guide = jo.getInt("guide");
			final boolean guideFake = jo.getBoolean("guide_fake");
			final String guideInterval = jo.getString("interval");
			final boolean randomFalling = jo.getBoolean("random_falling");
			final boolean luck = jo.getBoolean("luck");
			final double defaultSpeed = jo.getDouble("default_speed");
			final double defaultPosition = jo.getDouble("default_position");
			final TextView dir = findViewById(R.id.dir);
			dir.setText(directory.getAbsolutePath());
			listItems = new ArrayList<Map<String, Object>>();
			items = sortFile(directory.listFiles());
			for (File file : items){
				Map<String, Object> map = new HashMap<String, Object>();
				if (file.isDirectory()) map.put("image", R.drawable.ic_folder_outline);
				else {
					if (file.getName().endsWith(".zip") || file.getName().endsWith(".mcz") || file.getName().endsWith(".osz") || file.getName().endsWith(".pez")) map.put("image", R.drawable.ic_compressed_file_outline);
					else map.put("image", R.drawable.ic_file_outline);
				}
				map.put("title", file.getName());
				listItems.add(map);
			}
			listAdapter = new SimpleAdapter(this, listItems, R.layout.file_selector_items, new String[]{ "title", "image" }, new int[]{ R.id.title, R.id.image });
			ListView list = findViewById(R.id.list);
			list.setAdapter(listAdapter);
			list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id){
						if (items.get(position).isDirectory()){
							directory = items.get(position);
							dir.setText(directory.getAbsolutePath());
							listItems.clear();
							items = sortFile(directory.listFiles());
							for (File file : items){
								Map<String, Object> map = new HashMap<String, Object>();
								if (file.isDirectory()) map.put("image", R.drawable.ic_folder_outline);
								else {
									if (file.getName().endsWith(".zip") || file.getName().endsWith(".mcz") || file.getName().endsWith(".osz") || file.getName().endsWith(".pez")) map.put("image", R.drawable.ic_compressed_file_outline);
									else map.put("image", R.drawable.ic_file_outline);
								}
								map.put("title", file.getName());
								listItems.add(map);
							}
							listAdapter.notifyDataSetChanged();
						} else if (items.get(position).getName().endsWith(".zip") || items.get(position).getName().endsWith(".mcz") || items.get(position).getName().endsWith(".osz") || items.get(position).getName().endsWith(".pez")){
							try {
								if (jo.getBoolean("last_path")){
									JSONStringer js = new JSONStringer();
									BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(settings, false), "UTF-8"));
									js.object().key("default_path").value(directory.getAbsolutePath()).key("last_path").value(true).key("delete_converted").value(deleteConverted).key("speed").value(speed).key("wide").value(wide).key("slide").value(slide).key("guide").value(guide).key("guide_fake").value(guideFake).key("interval").value(guideInterval).key("random_falling").value(randomFalling).key("luck").value(luck).key("default_speed").value(defaultSpeed).key("default_position").value(defaultPosition).endObject();
									bw.write(js.toString());
									bw.close();
								}
							} catch (Exception e){
								catcher(e);
							}
							setResult(RESULT_OK, new Intent().putExtra("path", items.get(position).getAbsolutePath()));
							finish();
						} else Toast.makeText(FileSelectorActivity.this, "请选择一张Malody谱面文件（.mcz或.zip）或Phigros谱面文件（.pez或.zip）！", Toast.LENGTH_LONG).show();
					}
				}
			);
		} catch (Exception e){
			catcher(e);
		}
	}
	public void catcher(final Exception e){
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setIcon(android.R.drawable.ic_delete);
		adb.setTitle(R.string.crash_title);
		adb.setMessage(e.toString());
		adb.setPositiveButton(R.string.crash_ok, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface p1, int p2){
					ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
					ClipData cd = ClipData.newPlainText("Error message from MalodyVersusPhigros by DingZiHu", e.toString());
					cm.setPrimaryClip(cd);
					finish();
				}
			}
		);
		adb.setNegativeButton(R.string.crash_cancel, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface p1, int p2){
					finish();
				}
			}
		);
		adb.show();
	}
	@Override
	public void onBackPressed(){
		if (!directory.getAbsolutePath().equals("/storage/emulated/0")){
			directory = directory.getParentFile();
			TextView dir = findViewById(R.id.dir);
			dir.setText(directory.getAbsolutePath());
			listItems.clear();
			items = sortFile(directory.listFiles());
			for (File file : items){
				Map<String, Object> map = new HashMap<String, Object>();
				if (file.isDirectory()) map.put("image", R.drawable.ic_folder_outline);
				else {
					if (file.getName().endsWith(".zip") || file.getName().endsWith(".mcz") || file.getName().endsWith(".osz") || file.getName().endsWith(".pez")) map.put("image", R.drawable.ic_compressed_file_outline);
					else map.put("image", R.drawable.ic_file_outline);
				}
				map.put("title", file.getName());
				listItems.add(map);
			}
			listAdapter.notifyDataSetChanged();
			return;
		}
		super.onBackPressed();
	}
	public List<File> sortFile(File[] array){
		List<File> file = Arrays.asList(array);
		Collections.sort(file, new Comparator<File>(){
				@Override
				public int compare(File o1, File o2) {
					if (o1.isDirectory() && o2.isFile()) return -1;
					if (o1.isFile() && o2.isDirectory()) return 1;
					return o1.getName().compareToIgnoreCase(o2.getName());
				}
			}
		);
		return file;
	}
}

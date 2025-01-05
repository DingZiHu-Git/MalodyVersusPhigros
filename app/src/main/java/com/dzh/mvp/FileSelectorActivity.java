package com.dzh.mvp;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileSelectorActivity extends AppCompatActivity {
	private File dir;
	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_selector);
		Toolbar toolbar = findViewById(R.id.file_selector_toolbar);
		setSupportActionBar(toolbar);
		try {
			dir = new File(getIntent().getStringExtra("path"));
			final ImageButton lastDirectory = findViewById(R.id.last_directory);
			final TextView directory = findViewById(R.id.directory);
			ListView fileList = findViewById(R.id.file_list);
			refreshFileList();
			final SimpleAdapter fileListAdapter = new SimpleAdapter(this, list, R.layout.file_selector_items, new String[]{ "icon", "title" }, new int[]{ R.id.file_selector_items_icon, R.id.file_selector_items_title });
			lastDirectory.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						if (!dir.getAbsolutePath().equals("/storage/emulated/0")) dir = dir.getParentFile();
						directory.setText(dir.getAbsolutePath());
						refreshFileList();
						fileListAdapter.notifyDataSetChanged();
					}
				}
			);
			directory.setText(dir.getAbsolutePath());
			directory.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						final EditText et = new EditText(FileSelectorActivity.this);
						et.setText(directory.getText());
						et.selectAll();
						AlertDialog.Builder adb = new AlertDialog.Builder(FileSelectorActivity.this);
						adb.setIcon(R.drawable.ic_form_textbox).setTitle("跳转到……").setView(et).setPositiveButton("确定", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int p) {
									if (new File(et.getText().toString()).exists() && new File(et.getText().toString()).getAbsolutePath().startsWith("/storage/emulated/0")) dir = new File(et.getText().toString());
									else Toast.makeText(FileSelectorActivity.this, "目录不存在或无法访问", Toast.LENGTH_SHORT).show();
									directory.setText(dir.getAbsolutePath());
									refreshFileList();
									fileListAdapter.notifyDataSetChanged();
								}
							}
						).setNegativeButton("取消", null).setCancelable(false).show();
					}
				}
			);
			fileList.setAdapter(fileListAdapter);
			fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> adapterView, View view, int p, long p1) {
						if (sortFile(dir.listFiles()).get(p).isDirectory()) {
							dir = sortFile(dir.listFiles()).get(p);
							directory.setText(dir.getAbsolutePath());
							refreshFileList();
							fileListAdapter.notifyDataSetChanged();
						} else {
							Intent data = new Intent();
							data.putExtra("path", sortFile(dir.listFiles()).get(p).getAbsolutePath());
							setResult(RESULT_OK, data);
							finish();
						}
					}
				}
			);
		} catch (Exception e) {
			catcher(e);
		}
	}
	private void refreshFileList() {
		try {
			list.clear();
			for (File f : sortFile(dir.listFiles())) {
				Map<String, Object> item = new HashMap<String, Object>();
				String name = f.getName().toLowerCase();
				if (f.isDirectory()) item.put("icon", R.drawable.ic_folder_outline);
				else if (name.endsWith(".zip") || name.endsWith(".rar") || name.endsWith(".7z") || name.endsWith(".tar") || name.endsWith(".gz")) item.put("icon", R.drawable.ic_compressed_file_outline);
				else if (name.endsWith(".txt")) item.put("icon", R.drawable.ic_file_document_outline);
				else if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".gif")) item.put("icon", R.drawable.ic_file_image_outline);
				else if (name.endsWith(".mp3") || name.endsWith(".ogg") || name.endsWith(".flac") || name.endsWith(".wav") || name.endsWith(".m4a")) item.put("icon", R.drawable.ic_file_music_outline);
				else if (name.endsWith(".mp4") || name.endsWith(".m4v")) item.put("icon", R.drawable.ic_file_video_outline);
				else item.put("icon", R.drawable.ic_file_outline);
				item.put("title", f.getName());
				list.add(item);
			}
		} catch (Exception e) {
			catcher(e);
		}
	}
	private List<File> sortFile(File[] array){
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
	private void catcher(final Exception e) {
		final StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw, true));
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setIcon(android.R.drawable.ic_delete).setTitle("PhigrOS已崩溃").setMessage(sw.toString()).setPositiveButton("复制错误信息后退出", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface p1, int p2) {
					ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
					ClipData cd = ClipData.newPlainText("Error message from MalodyVersusPhigros by DingZiHu", sw.toString());
					cm.setPrimaryClip(cd);
					finish();
				}
			}
		).setNegativeButton("直接退出", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface p1, int p2) {
					finish();
				}
			}
		).setCancelable(false).show();
	}
}

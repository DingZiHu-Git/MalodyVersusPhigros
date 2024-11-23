package com.dzh.mvp;

import android.widget.Switch;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InvalidObjectException;
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

public class Functions {
	public File dir = new File("/storage/emulated/0/data/MVP");
	public String convert(final boolean keyToSlide, final boolean bpm, final boolean scroll, final boolean wide, final boolean slide, final int guide, final boolean fake, final Integer[] interval, final boolean randomFalling, final boolean luck, final double speed, final double position, final String illustrator){
		try {
			List<Line> lines = new ArrayList<Line>();
			lines.add(new Line("main").init(255, 0d, position, 0d, speed));
			BufferedReader setbr = new BufferedReader(new InputStreamReader(new FileInputStream(MainActivity.settings), "UTF-8"));
			JSONObject setjo = new JSONObject(setbr.readLine());
			String defaultPath = setjo.getString("default_path");
			boolean lastPath = setjo.getBoolean("last_path");
			boolean deleteConverted = setjo.getBoolean("delete_converted");
			for (int i = 0; i < MainActivity.chartList.size(); i++){
				if (!((boolean) MainActivity.chartList.get(i).get("checked"))) continue;
				int seed = Random.nextInt(10000, 99999999);
				String name = String.valueOf(seed);
				String type = null;
				File file = new File(MainActivity.temp.getAbsolutePath() + File.separator + ((String) MainActivity.chartList.get(i).get("name")));
				if (file.getName().toLowerCase().endsWith(".mc")){
					if (keyToSlide){
						File path = new File(MainActivity.temp.getAbsolutePath() + File.separator + seed);
						if (!path.exists()) if (!path.mkdirs()) return "Error when creating temp directory: " + path.getAbsolutePath();
						BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
						String line = null;
						String json = "";
						while ((line = br.readLine()) != null) json = json + line;
						JSONObject main = new JSONObject(json);
						JSONObject meta = main.getJSONObject("meta");
						if (meta.getInt("mode") != 0) continue;
						JSONObject songmeta = meta.getJSONObject("song");
						int column = meta.getJSONObject("mode_ext").getInt("column");
						JSONArray note = main.getJSONArray("note");
						JSONArray time = main.getJSONArray("time");
						JSONArray effect = main.has("effect") ? main.getJSONArray("effect") : new JSONArray();
						if (songmeta.has("titleorg") && !songmeta.getString("titleorg").isEmpty()) name = songmeta.getString("titleorg");
						else if (songmeta.has("title") && !songmeta.getString("title").isEmpty()) name = songmeta.getString("title");
						String level = meta.getString("version");
						JSONObject extra = main.has("extra") ? main.getJSONObject("extra") : null;
						File music = null;
						File picture = null;
						if (meta.has("background") && !meta.getString("background").isEmpty()) picture = new File(MainActivity.temp.getAbsolutePath() + File.separator + meta.getString("background"));
						if (picture != null) copy(picture, path.getAbsolutePath() + File.separator + picture.getName());
						JSONObject jo = new JSONObject();
						jo.put("meta", meta).put("meta", jo.getJSONObject("meta").put("mode", 7).put("mode_ext", new JSONObject("{}")));
						jo.put("time", time).put("effect", effect);
						JSONArray noteSlide = new JSONArray();
						for (int j = 0; j < note.length(); j++){
							JSONObject item = note.getJSONObject(j);
							if (item.has("sound")){
								music = new File(MainActivity.temp.getAbsolutePath() + File.separator + item.getString("sound"));
								copy(music, path.getAbsolutePath() + File.separator + music.getName());
								noteSlide.put(item);
							} else if (item.has("endbeat")){
								int x = 255 / (column + 1) * (item.getInt("column") + 1);
								JSONObject ln = new JSONObject();
								JSONArray segs = new JSONArray();
								JSONObject seg = new JSONObject();
								String[] endbeat = fractionAddition(String.valueOf(item.getJSONArray("endbeat").getInt(0) * item.getJSONArray("endbeat").getInt(2) + item.getJSONArray("endbeat").getInt(1)) + "/" + String.valueOf(item.getJSONArray("endbeat").getInt(2)) + "-" + String.valueOf(item.getJSONArray("beat").getInt(0) * item.getJSONArray("beat").getInt(2) + item.getJSONArray("beat").getInt(1)) + "/" + item.getJSONArray("beat").getInt(2)).split("/");
								int ef = 0;
								int es = Integer.valueOf(endbeat[0]);
								int et = Integer.valueOf(endbeat[1]);
								while (es >= et){
									es = es - et;
									ef++;
								}
								seg.put("beat", new JSONArray().put(ef).put(es).put(et)).put("x", 0);
								segs.put(seg);
								ln.put("beat", item.getJSONArray("beat")).put("x", x).put("w", 51).put("seg", segs);
								noteSlide.put(ln);
							} else {
								int x = 255 / (column + 1) * (item.getInt("column") + 1);
								JSONObject n = new JSONObject();
								n.put("beat", item.getJSONArray("beat")).put("x", x).put("w", 51);
								noteSlide.put(n);
							}
						}
						jo.put("note", noteSlide).put("extra", extra);
						if (extra != null) jo.put("extra", extra);
						File output = new File(path.getAbsolutePath() + File.separator + file.getName());
						copy(file, output.getAbsolutePath());
						BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output, false), "UTF-8"));
						bw.write(jo.toString());
						bw.close();
						doZip(path.getAbsolutePath(), dir.getAbsolutePath() + File.separator + name.replaceAll("/", " ") + " " + column + "K_" + level.replaceAll("/", " ") + ".mcz");
					} else {
						File path = new File(MainActivity.temp.getAbsolutePath() + File.separator + seed);
						if (!path.exists()) if (!path.mkdirs()) return "Error when creating temp directory: " + path.getAbsolutePath();
						BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
						String line = null;
						String json = "";
						while ((line = br.readLine()) != null) json = json + line;
						JSONObject main = new JSONObject(json);
						JSONObject meta = main.getJSONObject("meta");
						JSONObject songmeta = meta.getJSONObject("song");
						JSONObject mode_ext = meta.getJSONObject("mode_ext");
						JSONArray note = main.getJSONArray("note");
						JSONArray time = main.getJSONArray("time");
						JSONArray effect = main.has("effect") ? main.getJSONArray("effect") : new JSONArray();
						if (songmeta.has("titleorg") && !songmeta.getString("titleorg").isEmpty()) name = songmeta.getString("titleorg");
						else if (songmeta.has("title") && !songmeta.getString("title").isEmpty()) name = songmeta.getString("title");
						String level = meta.getString("version");
						String composer = "";
						if (songmeta.has("artistorg")) composer = songmeta.getString("artistorg");
						else if (songmeta.has("artist")) composer = songmeta.getString("artist");
						String charter = meta.getString("creator");
						double offset = 0;
						for (int j = 0; j < note.length(); j++) if (note.getJSONObject(j).has("offset")) offset = -note.getJSONObject(j).getDouble("offset");
						File music = null;
						File picture = null;
						if (!meta.getString("background").isEmpty()) picture = new File(MainActivity.temp.getAbsolutePath() + File.separator + meta.getString("background"));
						else {
							picture = new File(MainActivity.temp.getAbsolutePath() + File.separator + String.valueOf(seed) + ".png");
							InputStream is = MainActivity.resources.openRawResource(R.raw.no_illustration);
							FileOutputStream fos = new FileOutputStream(picture);
							byte[] b = new byte[1024 * 128];
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							int l = 0;
							while ((l = is.read(b)) != -1) baos.write(b, 0, l);
							byte[] bs = baos.toByteArray();
							fos.write(bs);
							baos.close();
							is.close();
							fos.flush();
							fos.close();
						}
						final JSONObject ct = new JSONObject();
						int mode = meta.getInt("mode");
						JSONArray bpmList = new JSONArray();
						for (int j = 0; j < time.length(); j++) bpmList.put(new JSONObject().put("bpm", time.getJSONObject(j).getDouble("bpm")).put("startTime", new JSONArray().put(time.getJSONObject(j).getJSONArray("beat").getInt(0)).put(time.getJSONObject(j).getJSONArray("beat").getInt(1)).put(time.getJSONObject(j).getJSONArray("beat").getInt(2))));
						ct.put("BPMList", bpmList);
						String background = "";
						if (picture != null) background = String.valueOf(seed) + ".png";
						ct.put("META", new JSONObject().put("RPEVersion", 140).put("background", background).put("charter", charter).put("composer", composer).put("id", seed).put("level", level).put("name", name).put("offset", offset).put("song", String.valueOf(seed) + ".ogg")).put("judgeLineGroup", new JSONArray().put("Default"));
						double BPM = time.getJSONObject(0).getDouble("bpm");
						for (int j = 1; j < time.length(); j++) {
							JSONArray eoc = null;
							for (int k = note.length() - 1; k > 0; k--) if (!note.getJSONObject(k).has("sound")) eoc = note.getJSONObject(k).getJSONArray("beat");
							JSONArray lastBeat = time.getJSONObject(j - 1).getJSONArray("beat");
							JSONArray beat = time.getJSONObject(j).getJSONArray("beat");
							JSONArray nextBeat = j == time.length() - 1 ? eoc : time.getJSONObject(j + 1).getJSONArray("beat");
							if ((nextBeat.getInt(0) + nextBeat.getInt(1) / Double.valueOf(nextBeat.getInt(2))) - (beat.getInt(0) + beat.getInt(1) / Double.valueOf(beat.getInt(2))) > (beat.getInt(0) + beat.getInt(1) / Double.valueOf(beat.getInt(2))) - (lastBeat.getInt(0) + lastBeat.getInt(1) / Double.valueOf(lastBeat.getInt(2)))) BPM = time.getJSONObject(j).getDouble("bpm");
						}
						final List<Double[]> speeds = new ArrayList<Double[]>();
						if (bpm) for (int j = 0; j < time.length(); j++) speeds.add(new Double[]{ time.getJSONObject(j).getJSONArray("beat").getDouble(0), time.getJSONObject(j).getJSONArray("beat").getDouble(1), time.getJSONObject(j).getJSONArray("beat").getDouble(2), time.getJSONObject(j).getDouble("bpm") / BPM });
						if (scroll) for (int j = 0; j < effect.length(); j++) {
							if (effect.getJSONObject(j).has("scroll")) speeds.add(new Double[]{ effect.getJSONObject(j).getJSONArray("beat").getDouble(0), effect.getJSONObject(j).getJSONArray("beat").getDouble(1), effect.getJSONObject(j).getJSONArray("beat").getDouble(2), effect.getJSONObject(j).getDouble("scroll") });
							else if (effect.getJSONObject(j).has("sv")) speeds.add(new Double[]{ effect.getJSONObject(j).getJSONArray("beat").getDouble(0), effect.getJSONObject(j).getJSONArray("beat").getDouble(1), effect.getJSONObject(j).getJSONArray("beat").getDouble(2), effect.getJSONObject(j).getDouble("sv") });
						}
						for (int j = 0; j < speeds.size(); j++) lines.get(0).addSpeedEvent(speeds.get(j)[3] * speed, String.valueOf((j == speeds.size() - 1) ? (speeds.get(j)[1].intValue() + 1) : speeds.get(j + 1)[0].intValue()) + ":" + speeds.get(j + (j == speeds.size() - 1 ? 0 : 1))[1].intValue() + "/" + speeds.get(j + (j == speeds.size() - 1 ? 0 : 1))[2].intValue(), speeds.get(j)[3] * speed, String.valueOf(speeds.get(j)[0].intValue()) + ":" + speeds.get(j)[1].intValue() + "/" + speeds.get(j)[2].intValue());
						if (mode == 0){
							type = mode_ext.getInt("column") + "K";
							for (int j = 0; j < note.length(); j++){
								JSONObject jo = note.getJSONObject(j);
								if (jo.has("sound")) music = new File(MainActivity.temp.getPath() + File.separator + jo.getString("sound"));
								else if (jo.has("endbeat")){
									JSONArray endTime = jo.getJSONArray("endbeat");
									JSONArray startTime = jo.getJSONArray("beat");
									int ef = endTime.getInt(0);
									int es = endTime.getInt(1);
									int et = endTime.getInt(2);
									int sf = startTime.getInt(0);
									int ss = startTime.getInt(1);
									int st = startTime.getInt(2);
									int track = jo.getInt("column");
									double positionX = 1350.0 / Double.valueOf(mode_ext.getInt("column") + 1) * Double.valueOf((luck ? Random.nextInt(0, mode_ext.getInt("column") - 1) : track) + 1) - 675.0;
									lines.get(0).addNote(randomFalling ? Random.nextInt(0, 1) : 1, 255, String.valueOf(ef) + ":" + es + "/" + et, 0, positionX, 1d, 1d, String.valueOf(sf) + ":" + ss + "/" + st, 2, 0d);
								} else {
									JSONArray startTime = jo.getJSONArray("beat");
									int f = startTime.getInt(0);
									int s = startTime.getInt(1);
									int t = startTime.getInt(2);
									int track = jo.getInt("column");
									double positionX = 1350.0 / Double.valueOf(mode_ext.getInt("column") + 1) * Double.valueOf((luck ? Random.nextInt(0, mode_ext.getInt("column") - 1) : track) + 1) - 675.0;
									lines.get(0).addNote(randomFalling ? Random.nextInt(0, 1) : 1, 255, String.valueOf(f) + ":" + s + "/" + t, 0, positionX, 1d, 1d, String.valueOf(f) + ":" + s + "/" + t, 1, 0d);
								}
							}
						} else if (mode == 3){
							type = "Catch";
							for (int j = 0; j < note.length(); j++){
								JSONObject jo = note.getJSONObject(j);
								if (jo.has("sound")) music = new File(MainActivity.temp.getPath() + File.separator + jo.getString("sound"));
								else if (!jo.has("endbeat")){
									JSONArray startTime = jo.getJSONArray("beat");
									int f = startTime.getInt(0);
									int s = startTime.getInt(1);
									int t = startTime.getInt(2);
									double positionX = (jo.getInt("x") - 256.0) / 256.0 * 675.0;
									lines.get(0).addNote(randomFalling ? Random.nextInt(0, 1) : 1, 255, String.valueOf(f) + ":" + s + "/" + t, 0, positionX, 1d, 1d, String.valueOf(f) + ":" + s + "/" + t, 1, 0d);
								}
							}
						} else if (mode == 5){
							type = "Taiko";
							double positionXRed = 135.0;
							double positionXBlue = -405.0;
							for (int j = 0; j < note.length(); j++){
								JSONObject jo = note.getJSONObject(j);
								JSONArray beat = jo.getJSONArray("beat");
								JSONArray endbeat = null;
								if (jo.has("endbeat")) endbeat = jo.getJSONArray("endbeat");
								int f = beat.getInt(0);
								int s = beat.getInt(1);
								int t = beat.getInt(2);
								if (jo.has("sound")) music = new File(MainActivity.temp.getPath() + File.separator + jo.getString("sound"));
								else switch (jo.getInt("style")){
									case 0:
										lines.get(0).addNote(randomFalling ? Random.nextInt(0, 1) : 1, 255, String.valueOf(f) + ":" + s + "/" + t, 0, positionXRed, 1d, 1d, String.valueOf(f) + ":" + s + "/" + t, 1, 0d);
										positionXRed = -positionXRed;
										break;
									case 1:
										lines.get(0).addNote(randomFalling ? Random.nextInt(0, 1) : 1, 255, String.valueOf(f) + ":" + s + "/" + t, 0, positionXRed, 1d, 1d, String.valueOf(f) + ":" + s + "/" + t, 1, 0d);
										lines.get(0).addNote(randomFalling ? Random.nextInt(0, 1) : 1, 255, String.valueOf(f) + ":" + s + "/" + t, 0, -positionXRed, 1d, 1d, String.valueOf(f) + ":" + s + "/" + t, 1, 0d);
										break;
									case 2:
										lines.get(0).addNote(randomFalling ? Random.nextInt(0, 1) : 1, 255, String.valueOf(f) + ":" + s + "/" + t, 0, positionXBlue, 1d, 1d, String.valueOf(f) + ":" + s + "/" + t, 1, 0d);
										positionXBlue = -positionXBlue;
										break;
									case 3:
										lines.get(0).addNote(randomFalling ? Random.nextInt(0, 1) : 1, 255, String.valueOf(f) + ":" + s + "/" + t, 0, positionXBlue, 1d, 1d, String.valueOf(f) + ":" + s + "/" + t, 1, 0d);
										lines.get(0).addNote(randomFalling ? Random.nextInt(0, 1) : 1, 255, String.valueOf(f) + ":" + s + "/" + t, 0, -positionXBlue, 1d, 1d, String.valueOf(f) + ":" + s + "/" + t, 1, 0d);
										break;
									case 4:
										while (f + s / Double.valueOf(t) + 1 / 4.0 < endbeat.getInt(0) + endbeat.getInt(1) / Double.valueOf(endbeat.getInt(2))){
											lines.get(0).addNote(randomFalling ? Random.nextInt(0, 1) : 1, 255, String.valueOf(f) + ":" + s + "/" + t, 0, positionXRed, 1d, 1d, String.valueOf(f) + ":" + s + "/" + t, 1, 0d);
											String[] next = fractionAddition(s + "/" + t + "+1/4").split("/");
											s = Integer.valueOf(next[0]);
											t = Integer.valueOf(next[1]);
											while (s >= t){
												s -= t;
												f++;
											}
											positionXRed = -positionXRed;
										}
										break;
									case 5:
										while (f + s / Double.valueOf(t) + 1 / 4.0 < endbeat.getInt(0) + endbeat.getInt(1) / Double.valueOf(endbeat.getInt(2))){
											lines.get(0).addNote(randomFalling ? Random.nextInt(0, 1) : 1, 255, String.valueOf(f) + ":" + s + "/" + t, 0, positionXRed, 1d, 1d, String.valueOf(f) + ":" + s + "/" + t, 1, 0d);
											String[] next = fractionAddition(s + "/" + t + "+1/4").split("/");
											s = Integer.valueOf(next[0]);
											t = Integer.valueOf(next[1]);
											while (s >= t){
												s -= t;
												f++;
											}
											positionXRed = -positionXRed;
										}
										break;
									case 6:
										String[] delta = fractionAddition((endbeat.getInt(0) * endbeat.getInt(2) + endbeat.getInt(1)) + "/" + endbeat.getInt(2) + "-" + (f * t + s) + "/" + t).split("/");
										int deltaF = 0;
										int deltaS = Integer.valueOf(delta[0]);
										int deltaT = Integer.valueOf(delta[1]);
										while (deltaS >= deltaT){
											deltaS -= deltaT;
											deltaF++;
										}
										for (int k = 0; k < jo.getInt("hits"); k++){
											lines.get(0).addNote(randomFalling ? Random.nextInt(0, 1) : 1, 255, String.valueOf(f) + ":" + s + "/" + t, 0, positionXRed, 1d, 1d, String.valueOf(f) + ":" + s + "/" + t, 1, 0d);
											String[] next = fractionAddition(s + "/" + t + "+" + (deltaF * deltaT + deltaS) + "/" + deltaT * jo.getInt("hits")).split("/");
											s = Integer.valueOf(next[0]);
											t = Integer.valueOf(next[1]);
											while (s >= t){
												s -= t;
												f++;
											}
											positionXRed = -positionXRed;
										}
										break;
								}
							}
						} else if (mode == 7) {
							type = "Slide";
							for (int j = 0; j < note.length(); j++) {
								JSONObject jo = note.getJSONObject(j);
								if (jo.has("sound")) music = new File(MainActivity.temp.getPath() + File.separator + jo.getString("sound"));
								else if (jo.has("seg")) {
									JSONArray seg = jo.getJSONArray("seg");
									if (seg.length() == 1 && (!seg.getJSONObject(0).has("x") || seg.getJSONObject(0).getInt("x") == 0)) {
										JSONArray endTime = seg.getJSONObject(seg.length() - 1).getJSONArray("beat");
										JSONArray startTime = jo.getJSONArray("beat");
										int ef = endTime.getInt(0);
										int es = endTime.getInt(1);
										int et = endTime.getInt(2);
										int sf = startTime.getInt(0);
										int ss = startTime.getInt(1);
										int st = startTime.getInt(2);
										ef = sf + ef;
										String[] end = fractionAddition(String.valueOf(es) + "/" + String.valueOf(et) + "+" + String.valueOf(ss) + "/" + String.valueOf(st)).split("/");
										es = Integer.valueOf(end[0]);
										et = Integer.valueOf(end[1]);
										while (es >= et) {
											es = es - et;
											ef++;
										}
										int x = jo.getInt("x");
										double positionX = (x - 128.0) / 128.0 * 675.0;
										double size = wide && jo.has("w") ? jo.getDouble("w") / 51d : 1.0;
										lines.get(0).addNote(randomFalling ? Random.nextInt(0, 1) : 1, 255, String.valueOf(ef) + ":" + es + "/" + et, 0, positionX, size, 1d, String.valueOf(sf) + ":" + ss + "/" + st, 2, 0d);
									} else if (slide) {
										Line hold = new Line("main");
										hold.init(0, 0d, 0d, 0d, speed);
										hold.father = 0;
										int falling = randomFalling ? Random.nextInt(0, 1) : 1;
										JSONArray endTime = seg.getJSONObject(seg.length() - 1).getJSONArray("beat");
										JSONArray startTime = jo.getJSONArray("beat");
										int ef = endTime.getInt(0);
										int es = endTime.getInt(1);
										int et = endTime.getInt(2);
										int sf = startTime.getInt(0);
										int ss = startTime.getInt(1);
										int st = startTime.getInt(2);
										ef = sf + ef;
										String[] end = fractionAddition(String.valueOf(es) + "/" + String.valueOf(et) + "+" + String.valueOf(ss) + "/" + String.valueOf(st)).split("/");
										es = Integer.valueOf(end[0]);
										et = Integer.valueOf(end[1]);
										while (es >= et) {
											es = es - et;
											ef++;
										}
										int x = jo.getInt("x");
										double positionX = (x - 128.0) / 128.0 * 675.0;
										double size = wide && jo.has("w") ? jo.getDouble("w") / 51d : 1.0;
										hold.addNote(falling, 255, String.valueOf(ef) + ":" + es + "/" + et, 0, positionX, size, 1d, String.valueOf(sf) + ":" + ss + "/" + st, 2, 0d);
										double lastPositionX = 0;
										int lastF = sf;
										int lastS = ss;
										int lastT = st;
										int currentF = sf;
										int currentS = ss;
										int currentT = st;
										for (int k = 0; k < seg.length(); k++) {
											int moveX = 0;
											if (seg.getJSONObject(k).has("x")) moveX = seg.getJSONObject(k).getInt("x");
											int segF = seg.getJSONObject(k).getJSONArray("beat").getInt(0);
											int segS = seg.getJSONObject(k).getJSONArray("beat").getInt(1);
											int segT = seg.getJSONObject(k).getJSONArray("beat").getInt(2);
											int moveF = sf + segF;
											String[] move = fractionAddition(String.valueOf(segS) + "/" + String.valueOf(segT) + "+" + String.valueOf(ss) + "/" + String.valueOf(st)).split("/");
											int moveS = Integer.valueOf(move[0]);
											int moveT = Integer.valueOf(move[1]);
											while (moveS >= moveT) {
												moveS = moveS - moveT;
												moveF++;
											}
											double movePositionX = moveX / 128.0 * 675.0;
											switch (guide) {
												case 1:
													lines.get(0).addNote(falling, fake ? 128 : 255, String.valueOf(moveF) + ":" + moveS + "/" + moveT, fake ? 1 : 0, positionX + movePositionX, wide ? size : 1d, 1d, String.valueOf(moveF) + ":" + moveS + "/" + moveT, 4, 0d);
													break;
												case 2:
													double v = (movePositionX - lastPositionX) / ((moveF + moveS / Double.valueOf(moveT)) - (lastF + lastS / Double.valueOf(lastT)));
													while ((currentF + currentS / Double.valueOf(currentT)) + (interval[0] + interval[1] / Double.valueOf(interval[2])) <= moveF + moveS / Double.valueOf(moveT)){
														String[] current = fractionAddition(String.valueOf(currentS) + "/" + String.valueOf(currentT) + "+" + String.valueOf(interval[0] * interval[2] + interval[1]) + "/" + String.valueOf(interval[2])).split("/");
														currentS = Integer.valueOf(current[0]);
														currentT = Integer.valueOf(current[1]);
														while (currentS >= currentT){
															currentS = currentS - currentT;
															currentF++;
														}
														lines.get(0).addNote(falling, fake ? 128 : 255, String.valueOf(currentF) + ":" + currentS + "/" + currentT, fake ? 1 : 0, positionX + lastPositionX + v * ((currentF + currentS / Double.valueOf(currentT)) - (lastF + lastS / Double.valueOf(lastT))), wide ? size : 1d, 1d, String.valueOf(currentF) + ":" + currentS + "/" + currentT, 4, 0d);
													}
													break;
											}
											hold.addMoveXEvent(1, 1, movePositionX, String.valueOf(moveF) + ":" + moveS + "/" + moveT, lastPositionX, String.valueOf(lastF) + ":" + lastS + "/" + lastT);
											lastF = moveF;
											lastS = moveS;
											lastT = moveT;
											lastPositionX = movePositionX;
										}
										for (int k = 0; k < speeds.size(); k++) hold.addSpeedEvent(speeds.get(k)[3] * speed, String.valueOf((k == speeds.size() - 1) ? (speeds.get(k)[1].intValue() + 1) : speeds.get(k + 1)[0].intValue()) + ":" + speeds.get(k + (k == speeds.size() - 1 ? 0 : 1))[1].intValue() + "/" + speeds.get(k + (k == speeds.size() - 1 ? 0 : 1))[2].intValue(), speeds.get(k)[3] * speed, String.valueOf(speeds.get(k)[0].intValue()) + ":" + speeds.get(k)[1].intValue() + "/" + speeds.get(k)[2].intValue());
										lines.add(hold);
									} else {
										JSONArray endTime = seg.getJSONObject(seg.length() - 1).getJSONArray("beat");
										JSONArray startTime = jo.getJSONArray("beat");
										int ef = endTime.getInt(0);
										int es = endTime.getInt(1);
										int et = endTime.getInt(2);
										int sf = startTime.getInt(0);
										int ss = startTime.getInt(1);
										int st = startTime.getInt(2);
										ef = sf + ef;
										String[] end = fractionAddition(String.valueOf(es) + "/" + String.valueOf(et) + "+" + String.valueOf(ss) + "/" + String.valueOf(st)).split("/");
										es = Integer.valueOf(end[0]);
										et = Integer.valueOf(end[1]);
										while (es >= et){
											es = es - et;
											ef++;
										}
										int x = jo.getInt("x");
										double positionX = (x - 128.0) / 128.0 * 675.0;
										double size = wide && jo.has("w") ? jo.getDouble("w") / 51d : 1.0;
										lines.get(0).addNote(randomFalling ? Random.nextInt(0, 1) : 1, 255, String.valueOf(ef) + ":" + es + "/" + et, 0, positionX, wide ? size : 1d, 1d, String.valueOf(sf) + ":" + ss + "/" + st, 2, 0d);
									}
								} else if (jo.has("type")){
									JSONArray startTime = jo.getJSONArray("beat");
									int f = startTime.getInt(0);
									int s = startTime.getInt(1);
									int t = startTime.getInt(2);
									int x = jo.getInt("x");
									double positionX = (x - 128.0) / 128.0 * 675.0;
									double size = wide && jo.has("w") ? jo.getDouble("w") / 51d : 1.0;
									if (jo.has("dir")) lines.get(0).addNote(randomFalling ? Random.nextInt(0, 1) : 1, 255, String.valueOf(f) + ":" + s + "/" + t, 0, positionX, size, 1d, String.valueOf(f) + ":" + s + "/" + t, 3, 0d);
									else lines.get(0).addNote(randomFalling ? Random.nextInt(0, 1) : 1, 255, String.valueOf(f) + ":" + s + "/" + t, 0, positionX, size, 1d, String.valueOf(f) + ":" + s + "/" + t, 4, 0d);
								} else if (jo.has("dir")){
									int falling = randomFalling ? Random.nextInt(0, 1) : 1;
									JSONArray startTime = jo.getJSONArray("beat");
									int f = startTime.getInt(0);
									int s = startTime.getInt(1);
									int t = startTime.getInt(2);
									int x = jo.getInt("x");
									double positionX = (x - 128.0) / 128.0 * 675.0;
									double flickPositionX = positionX;
									switch (jo.getInt("dir")){
										case 8:
											flickPositionX = positionX - 100.0;
											break;
										case 2:
											flickPositionX = positionX + 100.0;
											break;
									}
									double size = wide && jo.has("w") ? jo.getDouble("w") / 51d : 1.0;
									lines.get(0).addNote(falling, 255, String.valueOf(f) + ":" + s + "/" + t, 0, positionX, size, 1d, String.valueOf(f) + ":" + s + "/" + t, 1, 0d);
									lines.get(0).addNote(falling, 255, String.valueOf(f) + ":" + (32 / t * s + 1) + "/" + 32, 0, flickPositionX, size, 1d, String.valueOf(f) + ":" + (32 / t * s + 1) + "/" + 32, 3, 0d);
								} else {
									JSONArray startTime = jo.getJSONArray("beat");
									int f = startTime.getInt(0);
									int s = startTime.getInt(1);
									int t = startTime.getInt(2);
									int x = jo.getInt("x");
									double positionX = (x - 128.0) / 128.0 * 675.0;
									double size = wide && jo.has("w") ? jo.getDouble("w") / 51d : 1.0;
									lines.get(0).addNote(randomFalling ? Random.nextInt(0, 1) : 1, 255, String.valueOf(f) + ":" + s + "/" + t, 0, positionX, size, 1d, String.valueOf(f) + ":" + s + "/" + t, 1, 0d);
								}
							}
						}
						for (Map<String, Object> m : MainActivity.chartList) if (((String) m.get("name")).toLowerCase().endsWith(".mvp") && ((boolean) m.get("checked"))) {
							File f = new File(MainActivity.temp.getAbsolutePath() + File.separator + ((String) m.get("name")));
							BufferedReader effectBr = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
							String str = null;
							int index = 0;
							while ((str = effectBr.readLine()) != null){
								try {
									index++;
									if (str.isEmpty()) continue;
									String[] args = str.split(" ");
									if (args[0].startsWith("#")) continue;
									else if (args[0].equalsIgnoreCase("line")) for (int j = 1; j < args.length; j++) lines.add(new Line(args[j]));
									else if (args[0].equalsIgnoreCase("init")) {
										boolean found = false;
										for (Line l : lines) {
											if (l.name.equalsIgnoreCase(args[1]) && l.father == -1) {
												found = true;
												l.init(Integer.valueOf(args[2]), Double.valueOf(args[3]), Double.valueOf(args[4]), Double.valueOf(args[5]), Double.valueOf(args[6]));
											}
										}
										if (!found) throw new InvalidObjectException("At line " + index + ": Not found a judge line named " + args[1]);
									} else if (args[0].equalsIgnoreCase("alpha")) {
										boolean found = false;
										for (Line l : lines) {
											if (l.name.equalsIgnoreCase(args[1]) && l.father == -1) {
												found = true;
												l.addAlphaEvent(Integer.valueOf(args[2]), Integer.valueOf(args[3]), args[4], Integer.valueOf(args[5]), args[6]);
											}
										}
										if (!found) throw new InvalidObjectException("At line " + index + ": Not found a judge line named " + args[1]);
									} else if (args[0].equalsIgnoreCase("moveX")) {
										boolean found = false;
										for (Line l : lines) {
											if (l.name.equalsIgnoreCase(args[1]) && l.father == -1) {
												found = true;
												l.addMoveXEvent(0, Integer.valueOf(args[2]), Double.valueOf(args[3]), args[4], Double.valueOf(args[5]), args[6]);
											}
										}
										if (!found) throw new InvalidObjectException("At line " + index + ": Not found a judge line named " + args[1]);
									} else if (args[0].equalsIgnoreCase("moveY")) {
										boolean found = false;
										for (Line l : lines) {
											if (l.name.equalsIgnoreCase(args[1]) && l.father == -1) {
												found = true;
												l.addMoveYEvent(Integer.valueOf(args[2]), Double.valueOf(args[3]), args[4], Double.valueOf(args[5]), args[6]);
											}
										}
										if (!found) throw new InvalidObjectException("At line " + index + ": Not found a judge line named " + args[1]);
									} else if (args[0].equalsIgnoreCase("rotate")) {
										boolean found = false;
										for (Line l : lines) {
											if (l.name.equalsIgnoreCase(args[1])) {
												found = true;
												l.addRotateEvent(Integer.valueOf(args[2]), Double.valueOf(args[3]), args[4], Double.valueOf(args[5]), args[6]);
											}
										}
										if (!found) throw new InvalidObjectException("At line " + index + ": Not found a judge line named " + args[1]);
									} else if (args[0].equalsIgnoreCase("speed")) {
										boolean found = false;
										for (Line l : lines) {
											if (l.name.equalsIgnoreCase(args[1])) {
												found = true;
												l.addSpeedEvent(Double.valueOf(args[2]), args[3], Double.valueOf(args[4]), args[5]);
											}
										}
										if (!found) throw new InvalidObjectException("At line " + index + ": Not found a judge line named " + args[1]);
									} else if (args[0].equalsIgnoreCase("tap")) {
										boolean found = false;
										for (Line l : lines) {
											if (l.name.equalsIgnoreCase(args[1]) && l.father == -1) {
												found = true;
												l.addNote(Integer.valueOf(args[2]), Integer.valueOf(args[3]), args[8], Integer.valueOf(args[4]), Double.valueOf(args[5]), Double.valueOf(args[6]), Double.valueOf(args[7]), args[8], 1, 0d);
											}
										}
										if (!found) throw new InvalidObjectException("At line " + index + ": Not found a judge line named " + args[1]);
									} else if (args[0].equalsIgnoreCase("hold")) {
										boolean found = false;
										for (Line l : lines) {
											if (l.name.equalsIgnoreCase(args[1]) && l.father == -1) {
												found = true;
												l.addNote(Integer.valueOf(args[2]), Integer.valueOf(args[3]), args[8], Integer.valueOf(args[4]), Double.valueOf(args[5]), Double.valueOf(args[6]), Double.valueOf(args[7]), args[9], 2, 0d);
											}
										}
										if (!found) throw new InvalidObjectException("At line " + index + ": Not found a judge line named " + args[1]);
									} else if (args[0].equalsIgnoreCase("flick")) {
										boolean found = false;
										for (Line l : lines) {
											if (l.name.equalsIgnoreCase(args[1]) && l.father == -1) {
												found = true;
												l.addNote(Integer.valueOf(args[2]), Integer.valueOf(args[3]), args[8], Integer.valueOf(args[4]), Double.valueOf(args[5]), Double.valueOf(args[6]), Double.valueOf(args[7]), args[8], 3, 0d);
											}
										}
										if (!found) throw new InvalidObjectException("At line " + index + ": Not found a judge line named " + args[1]);
									} else if (args[0].equalsIgnoreCase("drag")) {
										boolean found = false;
										for (Line l : lines) {
											if (l.name.equalsIgnoreCase(args[1]) && l.father == -1) {
												found = true;
												l.addNote(Integer.valueOf(args[2]), Integer.valueOf(args[3]), args[8], Integer.valueOf(args[4]), Double.valueOf(args[5]), Double.valueOf(args[6]), Double.valueOf(args[7]), args[8], 4, 0d);
											}
										}
										if (!found) throw new InvalidObjectException("At line " + index + ": Not found a judge line named " + args[1]);
									} else throw new InvalidObjectException("At line " + index + ": Unknown command " + args[0]);
								} catch (Exception e) {
									StringWriter sw = new StringWriter();
									e.printStackTrace(new PrintWriter(sw, true));
									String err = sw.toString();
									try {
										File error = new File(MainActivity.temp.getAbsolutePath() + File.separator + "error.log");
										BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(error, false), "UTF-8"));
										bw.write("At line " + index + ": " + err);
									} catch (Exception nothing) {} finally {
										return "At line " + index + ": " + err;
									}
								}
							}
						}
						JSONArray judgeLineList = new JSONArray();
						for (Line l : lines) judgeLineList.put(l.toJSONObject());
						ct.put("judgeLineList", judgeLineList).put("multiLineString", "").put("multiScale", 1d);
						File output = new File(path.getPath() + File.separator + String.valueOf(seed) + ".json");
						File info = new File(path.getPath() + File.separator + "info.txt");
						output.createNewFile();
						info.createNewFile();
						BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output, false), "UTF-8"));
						bw.write(ct.toString());
						bw.flush();
						bw.close();
						bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(info, false), "UTF-8"));
						bw.write("#\nName: " + name + "\nPath: " + String.valueOf(seed) + "\nSong: " + String.valueOf(seed) + ".ogg\nPicture: " + String.valueOf(seed) + ".png\nChart: " + String.valueOf(seed) + ".json\nLevel: " + level + "\nComposer: " + composer + "\nCharter: " + charter + "\nIllustrator: " + illustrator + "\n");
						bw.flush();
						bw.close();
						copy(music, path + File.separator + String.valueOf(seed) + ".ogg");
						if (picture != null) copy(picture, path + File.separator + String.valueOf(seed) + ".png");
						for (Map<String, Object> m : MainActivity.chartList) if (((boolean) m.get("checked")) && ((String) m.get("name")).toLowerCase().endsWith(".extra")) {
							ZipInputStream zis = new ZipInputStream(new FileInputStream(((String) m.get("name"))));
							ZipEntry ze = zis.getNextEntry();
							byte[] buffer = new byte[1024 * 1024];
							int count = 0;
							while (ze != null) {
								if (!ze.isDirectory()) {
									String fn = ze.getName();
									fn = fn.substring(fn.lastIndexOf("/") + 1);
									File f = new File(path.getAbsolutePath() + File.separator + fn);
									f.createNewFile();
									FileOutputStream fos = new FileOutputStream(f);
									while ((count = zis.read(buffer)) > 0) fos.write(buffer, 0, count);
									fos.flush();
									fos.close();
								}
								ze = zis.getNextEntry();
							}
							zis.close();
						}
						doZip(path.getAbsolutePath(), dir.getAbsolutePath() + File.separator + name.replaceAll("/", " ") + " " + type + "_" + level.replaceAll("/", " ") + ".pez");
					}
				} else if (file.getName().toLowerCase().endsWith(".osu")){
					File path = new File(MainActivity.temp.getAbsolutePath() + File.separator + String.valueOf(seed));
					if (!path.exists()) if (!path.mkdirs()) return "Error when creating temp directory: " + path.getAbsolutePath();
					BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
					String line = null;
					File music = null;
					File picture = null;
					String composer = null;
					String charter = null;
					String level = null;
					int cs = 0;
					boolean mania = false;
					boolean events = false;
					boolean hitObjects = false;
					JSONStringer js = new JSONStringer();
					js.object().key("formatVersion").value(3).key("offset").value(0.0).key("judgeLineList").array().object().key("bpm").value(120.0).key("notesAbove").array();
					while ((line = br.readLine()) != null){
						if (line.startsWith("AudioFilename")){
							music = new File(MainActivity.temp.getAbsolutePath() + File.separator + line.split(":", 2)[1].trim());
							copy(music, path.getAbsolutePath() + File.separator + String.valueOf(seed) + ".mp3");
						}
						if (line.startsWith("Mode") && line.split(":", 2)[1].trim().equals("3")) mania = true;
						if (mania){
							if (line.startsWith("TitleUnicode")) name = line.split(":", 2)[1].trim();
							if (line.startsWith("ArtistUnicode")) composer = line.split(":", 2)[1].trim();
							if (line.startsWith("Creator")) charter = line.split(":", 2)[1].trim();
							if (line.startsWith("Version")) level = line.split(":", 2)[1].trim();
							if (line.startsWith("CircleSize")) cs = Integer.valueOf(line.split(":", 2)[1].trim());
							if (line.isEmpty()) {
								events = false;
								hitObjects = false;
							}
							if (events){
								if (line.startsWith("0,0,")){
									picture = new File(MainActivity.temp.getAbsolutePath() + File.separator + line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\"")));
									if (!picture.exists()) return "Error when copying picutre: " + picture.getAbsolutePath();
									copy(picture, path.getAbsolutePath() + File.separator + String.valueOf(seed) + ".jpg");
								}
							}
							if (line.equals("[Events]")) events = true;
							if (hitObjects){
								String[] objects = line.split(",");
								if (objects[5].split(":").length > 4) js.object().key("type").value(3).key("time").value((int) (Integer.valueOf(objects[2]) / 1000.0 * (120.0 / 1.875))).key("positionX").value(160 / 9d / (cs + 1) * ((luck ? Random.nextInt(0, cs - 1) : (Integer.valueOf(objects[0]) * cs / 512)) + 1) - 80 / 9d).key("holdTime").value((int) ((Integer.valueOf(objects[5].split(":")[0]) - Integer.valueOf(objects[2])) / 1000.0 * (120.0 / 1.875))).key("speed").value(2 / 9d * speed).key("floorPosition").value(1.875 / 120.0 * (Integer.valueOf(objects[2]) / 1000.0 * (120.0 / 1.875)) * (2 / 9d * speed)).endObject();
								else js.object().key("type").value(1).key("time").value((int) (Integer.valueOf(objects[2]) / 1000.0 * (120.0 / 1.875))).key("positionX").value(160 / 9d / (cs + 1) * ((luck ? Random.nextInt(0, cs - 1) : (Integer.valueOf(objects[0]) * cs / 512)) + 1) - 80 / 9d).key("holdTime").value(0).key("speed").value(1.0).key("floorPosition").value(1.875 / 120.0 * (Integer.valueOf(objects[2]) / 1000.0 * (120.0 / 1.875)) * (2 / 9d * speed)).endObject();
							}
							if (line.equals("[HitObjects]")) hitObjects = true;
						}
					}
					br.close();
					js.endArray().key("notesBelow").array().endArray().key("speedEvents").array().object().key("startTime").value(0).key("endTime").value(100000000).key("value").value(2 / 9d * speed).endObject().endArray().key("judgeLineDisappearEvents").array().object().key("startTime").value(-999999).key("endTime").value(100000000).key("start").value(1.0).key("end").value(1.0).endObject().endArray().key("judgeLineMoveEvents").array().object().key("startTime").value(-999999).key("endTime").value(100000000).key("start").value(0.5).key("end").value(0.5).key("start2").value((position > 0 ? position : -position) / 675.0 * 0.5).key("end2").value((position > 0 ? position : -position) / 675.0 * 0.5).endObject().endArray();
					js.key("judgeLineRotateEvents").array().object().key("startTime").value(-999999).key("endTime").value(100000000).key("start").value(0.0).key("end").value(0.0).endObject().endArray().endObject().endArray().endObject();
					File output = new File(path.getPath() + File.separator + String.valueOf(seed) + ".json");
					File info = new File(path.getPath() + File.separator + "info.txt");
					if (!output.createNewFile()) return "Error when creating chart file";
					if (!info.createNewFile()) return "Error when creating info file";
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output, false), "UTF-8"));
					bw.write(js.toString());
					bw.flush();
					bw.close();
					bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(info, false), "UTF-8"));
					bw.write("#\nName: " + name + "\nPath: " + String.valueOf(seed) + "\nSong: " + String.valueOf(seed) + ".mp3\nPicture: " + String.valueOf(seed) + ".jpg\nChart: " + String.valueOf(seed) + ".json\nLevel: " + level + "\nComposer: " + composer + "\nCharter: " + charter + "\n");
					bw.flush();
					bw.close();
					for (Map<String, Object> m : MainActivity.chartList) if (((boolean) m.get("checked")) && ((String) m.get("name")).toLowerCase().endsWith(".extra")) {
						ZipInputStream zis = new ZipInputStream(new FileInputStream(((Switch) m.get("name")).getText().toString()));
						ZipEntry ze = zis.getNextEntry();
						byte[] buffer = new byte[1024 * 1024];
						int count = 0;
						while (ze != null) {
							if (!ze.isDirectory()) {
								String fn = ze.getName();
								fn = fn.substring(fn.lastIndexOf("/") + 1);
								File f = new File(path.getAbsolutePath() + File.separator + fn);
								f.createNewFile();
								FileOutputStream fos = new FileOutputStream(f);
								while ((count = zis.read(buffer)) > 0) fos.write(buffer, 0, count);
								fos.flush();
								fos.close();
							}
							ze = zis.getNextEntry();
						}
						zis.close();
					}
					doZip(path.getAbsolutePath(), dir.getAbsolutePath() + File.separator + name.replaceAll("/", " ") + " osu_" + level.replaceAll("/", " ") + ".pez");
				}
			}
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(MainActivity.settings, false), "UTF-8"));
			JSONStringer js = new JSONStringer();
			js.object().key("default_path").value(defaultPath).key("last_path").value(lastPath).key("delete_converted").value(deleteConverted).key("speed").value(bpm && scroll ? 3 : (bpm ? 1 : (scroll ? 2 : 0))).key("wide").value(wide).key("slide").value(slide).key("guide").value(guide).key("guide_fake").value(fake).key("interval").value(interval[0] + ":" + interval[1] + "/" + interval[2]).key("random_falling").value(randomFalling).key("luck").value(luck).key("default_speed").value(speed).key("default_position").value(position).endObject();
			bw.write(js.toString());
			bw.close();
			if (setjo.getBoolean("delete_converted")) for (File f : MainActivity.charts) f.delete();
			return null;
		} catch (Exception e){
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String err = sw.toString();
			try {
				File error = new File(MainActivity.temp.getAbsolutePath() + File.separator + "error.log");
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(error, false), "UTF-8"));
				bw.write(err);
			} catch (Exception nothing) {} finally {
				return err;
			}
		}
	}
	public void copy(File source, String dest){
		try {
			InputStream is = null;
			OutputStream os = null;
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			byte[] buffer = new byte[1024 * 1024];
			int length;
			while ((length = is.read(buffer)) > 0) os.write(buffer, 0, length);
			is.close();
			os.close();
		} catch (Exception e){}
	}
	public void doZip(String srcPath, String targetPath){
		try {
			FileOutputStream fos = new FileOutputStream(targetPath);
			ZipOutputStream zos = new ZipOutputStream(fos);
			File src = new File(srcPath);
			zip(src, src.getName(), zos);
			zos.close();
			fos.close();
		} catch (Exception e){}
	}
	private void zip(File srcFile, String srcName, ZipOutputStream zos) throws IOException {
		if (srcFile.isDirectory()){
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
	private long ans1,ans2;
	public String fractionAddition(String expression){
		long a = 0, b = 0;
		ans1 = 0; ans2 = 0;
		boolean flag = false, mark = false;
		for (int i = 0; i < expression.length(); i++){
			if (expression.charAt(i) == '/'){
				a = mark ? - a : a;
				flag = true;
				mark = false;
				continue;
			}
			if (expression.charAt(i) == '-' || expression.charAt(i) == '+'){
				work(ans1, ans2, a, b);
				a = 0; b = 0; flag = false;
				if (expression.charAt(i) == '-') mark = true;
				continue;
			}
			if (!flag) a = a * 10 + expression.charAt(i) - '0';
			else b = b * 10 + expression.charAt(i) - '0';
			if (i == expression.length() - 1) work(ans1, ans2, a, b);
		}
		if (ans2 == 0) ans2 = 1;
		StringBuilder str = new StringBuilder();
		str.append(String.valueOf(ans1));
		str.append('/');
		str.append(String.valueOf(ans2));
		return str.toString();
	}
	private void work(long a, long b, long c, long d){
		if (b == 0 && a == 0){
			ans1 = c;
			ans2 = d;
			return;
		}
		long res1 = a * d + b * c;
		long res2 = b * d;
		boolean flag = false;
		if (res1 < 0){
			flag = true;
			res1 = - res1;
		}
		long tmp = gcd(res1, res2);
		ans1 = res1 / tmp; ans2 = res2 / tmp;
		if (flag) ans1 = - ans1;
	}
	private long gcd(long a, long b){
		if (b == 0) return a;
		return gcd(b, a % b);
	}
}

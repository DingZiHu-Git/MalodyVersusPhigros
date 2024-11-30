package com.dzh.mvp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class Line extends Object {
	public boolean init;
	public JSONObject main;
	public JSONArray alphaEvents;
	public JSONArray[] moveXEvents;
	public JSONArray moveYEvents;
	public JSONArray rotateEvents;
	public JSONArray speedEvents;
	public int father;
	public JSONArray notes;
	public String name;
	public int numOfNotes;
	public int[] occupy;
	public Line(String lineName) {
		init = false;
		main = new JSONObject();
		alphaEvents = new JSONArray();
		moveXEvents = new JSONArray[2];
		moveYEvents = new JSONArray();
		rotateEvents = new JSONArray();
		speedEvents = new JSONArray();
		alphaEvents = new JSONArray();
		moveXEvents[0] = new JSONArray();
		moveXEvents[1] = new JSONArray();
		moveYEvents = new JSONArray();
		rotateEvents = new JSONArray();
		speedEvents = new JSONArray();
		father = -1;
		notes = new JSONArray();
		name = lineName;
		numOfNotes = 0;
		occupy = new int[]{ 0, 0, 1 };
	}
	private void sortAll() {
		try {
			Comparator<JSONObject> c = new Comparator<JSONObject>() {
				@Override
				public int compare(JSONObject t, JSONObject t1){
					try {
						JSONArray time = t.getJSONArray("startTime");
						JSONArray nextTime = t.getJSONArray("startTime");
						if (time.getInt(0) + time.getInt(1) / time.getDouble(2) < nextTime.getInt(0) + nextTime.getInt(1) / nextTime.getDouble(2)) return -1;
						else return 1;
					} catch (Exception e) {
						catcher(e);
						return 0;
					}
				}
			};
			List<JSONObject> temp = new ArrayList<JSONObject>();
			for (int i = 0; i < alphaEvents.length(); i++) temp.add(alphaEvents.getJSONObject(i));
			temp.sort(c);
			alphaEvents = new JSONArray();
			for (JSONObject jo : temp) alphaEvents.put(jo);
			temp.clear();
			for (int i = 0; i < moveXEvents[0].length(); i++) temp.add(moveXEvents[0].getJSONObject(i));
			temp.sort(c);
			moveXEvents[0] = new JSONArray();
			for (JSONObject jo : temp) moveXEvents[0].put(jo);
			temp.clear();
			for (int i = 0; i < moveXEvents[1].length(); i++) temp.add(moveXEvents[1].getJSONObject(i));
			temp.sort(c);
			moveXEvents[1] = new JSONArray();
			for (JSONObject jo : temp) moveXEvents[1].put(jo);
			temp.clear();
			for (int i = 0; i < moveYEvents.length(); i++) temp.add(moveYEvents.getJSONObject(i));
			temp.sort(c);
			moveYEvents = new JSONArray();
			for (JSONObject jo : temp) moveYEvents.put(jo);
			temp.clear();
			for (int i = 0; i < rotateEvents.length(); i++) temp.add(rotateEvents.getJSONObject(i));
			temp.sort(c);
			rotateEvents = new JSONArray();
			for (JSONObject jo : temp) rotateEvents.put(jo);
			temp.clear();
			for (int i = 0; i < speedEvents.length(); i++) temp.add(speedEvents.getJSONObject(i));
			temp.sort(c);
			speedEvents = new JSONArray();
			for (JSONObject jo : temp) speedEvents.put(jo);
			temp.clear();
			for (int i = 0; i < notes.length(); i++) temp.add(notes.getJSONObject(i));
			temp.sort(c);
			notes = new JSONArray();
			for (JSONObject jo : temp) notes.put(jo);
			temp.clear();
		} catch (Exception e) {
			catcher(e);
		}
	}
	public Line init(int alpha, double x, double y, double rotate, double speed) {
		try {
			if (init) {
				alphaEvents.put(0, new JSONObject().put("bezier", 0).put("bezierPoints", new JSONArray().put(0d).put(0d).put(0d).put(0d)).put("easingLeft", 0d).put("easingRight", 1d).put("easingType", 1).put("end", alpha).put("endTime", new JSONArray().put(1).put(0).put(1)).put("linkgroup", 0).put("start", alpha).put("startTime", new JSONArray().put(0).put(0).put(1)));
				moveXEvents[0].put(0, new JSONObject().put("bezier", 0).put("bezierPoints", new JSONArray().put(0d).put(0d).put(0d).put(0d)).put("easingLeft", 0d).put("easingRight", 1d).put("easingType", 1).put("end", x).put("endTime", new JSONArray().put(1).put(0).put(1)).put("linkgroup", 0).put("start", x).put("startTime", new JSONArray().put(0).put(0).put(1)));
				moveXEvents[1].put(0, new JSONObject().put("bezier", 0).put("bezierPoints", new JSONArray().put(0d).put(0d).put(0d).put(0d)).put("easingLeft", 0d).put("easingRight", 1d).put("easingType", 1).put("end", 0d).put("endTime", new JSONArray().put(1).put(0).put(1)).put("linkgroup", 0).put("start", 0d).put("startTime", new JSONArray().put(0).put(0).put(1)));
				moveYEvents.put(0, new JSONObject().put("bezier", 0).put("bezierPoints", new JSONArray().put(0d).put(0d).put(0d).put(0d)).put("easingLeft", 0d).put("easingRight", 1d).put("easingType", 1).put("end", y).put("endTime", new JSONArray().put(1).put(0).put(1)).put("linkgroup", 0).put("start", y).put("startTime", new JSONArray().put(0).put(0).put(1)));
				rotateEvents.put(0, new JSONObject().put("bezier", 0).put("bezierPoints", new JSONArray().put(0d).put(0d).put(0d).put(0d)).put("easingLeft", 0d).put("easingRight", 1d).put("easingType", 1).put("end", rotate).put("endTime", new JSONArray().put(1).put(0).put(1)).put("linkgroup", 0).put("start", rotate).put("startTime", new JSONArray().put(0).put(0).put(1)));
				speedEvents.put(0, new JSONObject().put("end", speed).put("endTime", new JSONArray().put(1).put(0).put(1)).put("linkgroup", 0).put("start", speed).put("startTime", new JSONArray().put(0).put(0).put(1)));
			} else {
				alphaEvents.put(new JSONObject().put("bezier", 0).put("bezierPoints", new JSONArray().put(0d).put(0d).put(0d).put(0d)).put("easingLeft", 0d).put("easingRight", 1d).put("easingType", 1).put("end", alpha).put("endTime", new JSONArray().put(1).put(0).put(1)).put("linkgroup", 0).put("start", alpha).put("startTime", new JSONArray().put(0).put(0).put(1)));
				moveXEvents[0].put(new JSONObject().put("bezier", 0).put("bezierPoints", new JSONArray().put(0d).put(0d).put(0d).put(0d)).put("easingLeft", 0d).put("easingRight", 1d).put("easingType", 1).put("end", x).put("endTime", new JSONArray().put(1).put(0).put(1)).put("linkgroup", 0).put("start", x).put("startTime", new JSONArray().put(0).put(0).put(1)));
				moveXEvents[1].put(new JSONObject().put("bezier", 0).put("bezierPoints", new JSONArray().put(0d).put(0d).put(0d).put(0d)).put("easingLeft", 0d).put("easingRight", 1d).put("easingType", 1).put("end", 0d).put("endTime", new JSONArray().put(1).put(0).put(1)).put("linkgroup", 0).put("start", 0d).put("startTime", new JSONArray().put(0).put(0).put(1)));
				moveYEvents.put(new JSONObject().put("bezier", 0).put("bezierPoints", new JSONArray().put(0d).put(0d).put(0d).put(0d)).put("easingLeft", 0d).put("easingRight", 1d).put("easingType", 1).put("end", y).put("endTime", new JSONArray().put(1).put(0).put(1)).put("linkgroup", 0).put("start", y).put("startTime", new JSONArray().put(0).put(0).put(1)));
				rotateEvents.put(new JSONObject().put("bezier", 0).put("bezierPoints", new JSONArray().put(0d).put(0d).put(0d).put(0d)).put("easingLeft", 0d).put("easingRight", 1d).put("easingType", 1).put("end", rotate).put("endTime", new JSONArray().put(1).put(0).put(1)).put("linkgroup", 0).put("start", rotate).put("startTime", new JSONArray().put(0).put(0).put(1)));
				speedEvents.put(new JSONObject().put("end", speed).put("endTime", new JSONArray().put(1).put(0).put(1)).put("linkgroup", 0).put("start", speed).put("startTime", new JSONArray().put(0).put(0).put(1)));
				sortAll();
			}
			init = true;
			return this;
		} catch (Exception e) {
			catcher(e);
			return null;
		}
	}
	public Line addAlphaEvent(int easingType, int end, String endTime, int start, String startTime) {
		try {
			String[] et = endTime.split(":");
			int endNum = Integer.valueOf(et[0]);
			int endUp = Integer.valueOf(et[1].split("/")[0]);
			int endDown = Integer.valueOf(et[1].split("/")[1]);
			String[] st = startTime.split(":");
			int startNum = Integer.valueOf(st[0]);
			int startUp = Integer.valueOf(st[1].split("/")[0]);
			int startDown = Integer.valueOf(st[1].split("/")[1]);
			alphaEvents.put(new JSONObject().put("bezier", 0).put("bezierPoints", new JSONArray().put(0d).put(0d).put(0d).put(0d)).put("easingLeft", 0d).put("easingRight", 1d).put("easingType", easingType).put("end", end).put("endTime", new JSONArray().put(endNum).put(endUp).put(endDown)).put("linkgroup", 0).put("start", start).put("startTime", new JSONArray().put(startNum).put(startUp).put(startDown)));
			sortAll();
			return this;
		} catch (Exception e) {
			catcher(e);
			return null;
		}
	}
	public Line addMoveXEvent(int eventLayer, int easingType, double end, String endTime, double start, String startTime) {
		try {
			String[] et = endTime.split(":");
			int endNum = Integer.valueOf(et[0]);
			int endUp = Integer.valueOf(et[1].split("/")[0]);
			int endDown = Integer.valueOf(et[1].split("/")[1]);
			String[] st = startTime.split(":");
			int startNum = Integer.valueOf(st[0]);
			int startUp = Integer.valueOf(st[1].split("/")[0]);
			int startDown = Integer.valueOf(st[1].split("/")[1]);
			moveXEvents[eventLayer].put(new JSONObject().put("bezier", 0).put("bezierPoints", new JSONArray().put(0d).put(0d).put(0d).put(0d)).put("easingLeft", 0d).put("easingRight", 1d).put("easingType", easingType).put("end", end).put("endTime", new JSONArray().put(endNum).put(endUp).put(endDown)).put("linkgroup", 0).put("start", start).put("startTime", new JSONArray().put(startNum).put(startUp).put(startDown)));
			sortAll();
			return this;
		} catch (Exception e) {
			catcher(e);
			return null;
		}
	}
	public Line addMoveYEvent(int easingType, double end, String endTime, double start, String startTime) {
		try {
			String[] et = endTime.split(":");
			int endNum = Integer.valueOf(et[0]);
			int endUp = Integer.valueOf(et[1].split("/")[0]);
			int endDown = Integer.valueOf(et[1].split("/")[1]);
			String[] st = startTime.split(":");
			int startNum = Integer.valueOf(st[0]);
			int startUp = Integer.valueOf(st[1].split("/")[0]);
			int startDown = Integer.valueOf(st[1].split("/")[1]);
			moveYEvents.put(new JSONObject().put("bezier", 0).put("bezierPoints", new JSONArray().put(0d).put(0d).put(0d).put(0d)).put("easingLeft", 0d).put("easingRight", 1d).put("easingType", easingType).put("end", end).put("endTime", new JSONArray().put(endNum).put(endUp).put(endDown)).put("linkgroup", 0).put("start", start).put("startTime", new JSONArray().put(startNum).put(startUp).put(startDown)));
			sortAll();
			return this;
		} catch (Exception e) {
			catcher(e);
			return null;
		}
	}
	public Line addRotateEvent(int easingType, double end, String endTime, double start, String startTime) {
		try {
			String[] et = endTime.split(":");
			int endNum = Integer.valueOf(et[0]);
			int endUp = Integer.valueOf(et[1].split("/")[0]);
			int endDown = Integer.valueOf(et[1].split("/")[1]);
			String[] st = startTime.split(":");
			int startNum = Integer.valueOf(st[0]);
			int startUp = Integer.valueOf(st[1].split("/")[0]);
			int startDown = Integer.valueOf(st[1].split("/")[1]);
			rotateEvents.put(new JSONObject().put("bezier", 0).put("bezierPoints", new JSONArray().put(0d).put(0d).put(0d).put(0d)).put("easingLeft", 0d).put("easingRight", 1d).put("easingType", easingType).put("end", end).put("endTime", new JSONArray().put(endNum).put(endUp).put(endDown)).put("linkgroup", 0).put("start", start).put("startTime", new JSONArray().put(startNum).put(startUp).put(startDown)));
			sortAll();
			return this;
		} catch (Exception e) {
			catcher(e);
			return null;
		}
	}
	public Line addSpeedEvent(double end, String endTime, double start, String startTime) {
		try {
			String[] et = endTime.split(":");
			int endNum = Integer.valueOf(et[0]);
			int endUp = Integer.valueOf(et[1].split("/")[0]);
			int endDown = Integer.valueOf(et[1].split("/")[1]);
			String[] st = startTime.split(":");
			int startNum = Integer.valueOf(st[0]);
			int startUp = Integer.valueOf(st[1].split("/")[0]);
			int startDown = Integer.valueOf(st[1].split("/")[1]);
			speedEvents.put(new JSONObject().put("end", end).put("endTime", new JSONArray().put(endNum).put(endUp).put(endDown)).put("linkgroup", 0).put("start", start).put("startTime", new JSONArray().put(startNum).put(startUp).put(startDown)));
			sortAll();
			return this;
		} catch (Exception e) {
			catcher(e);
			return null;
		}
	}
	public Line addNote(int above, int alpha, String endTime, int isFake, double positionX, double size, double speed, String startTime, int type, double yOffset) {
		try {
			String[] end = endTime.split(":");
			int endNum = Integer.valueOf(end[0]);
			int endUp = Integer.valueOf(end[1].split("/")[0]);
			int endDown = Integer.valueOf(end[1].split("/")[1]);
			String[] start = startTime.split(":");
			int startNum = Integer.valueOf(start[0]);
			int startUp = Integer.valueOf(start[1].split("/")[0]);
			int startDown = Integer.valueOf(start[1].split("/")[1]);
			notes.put(new JSONObject().put("above", above).put("alpha", alpha).put("endTime", new JSONArray().put(endNum).put(endUp).put(endDown)).put("isFake", isFake).put("positionX", positionX).put("size", size).put("speed", speed).put("startTime", new JSONArray().put(startNum).put(startUp).put(startDown)).put("type", type).put("visibleTime", 999999d).put("yOffset", yOffset));
			sortAll();
			numOfNotes++;
			return this;
		} catch (Exception e) {
			catcher(e);
			return null;
		}
	}
	@Override
	public String toString() {
		try {
			JSONObject[] eventLayer = new JSONObject[2];
			eventLayer[0] = new JSONObject().put("alphaEvents", alphaEvents).put("moveXEvents", moveXEvents[0]).put("moveYEvents", moveYEvents).put("rotateEvents", rotateEvents).put("speedEvents", speedEvents);
			eventLayer[1] = new JSONObject().put("moveXEvents", moveXEvents[1]);
			main.put("Group", 0).put("Name", name).put("Texture", "line.png").put("alphaControl", new JSONArray().put(new JSONObject().put("alpha", 1d).put("easing", 1).put("x", 0d)).put(new JSONObject().put("alpha", 1d).put("easing", 1).put("x", 9999999d))).put("bpmfactor", 1d).put("eventLayers", new JSONArray().put(eventLayer[0]).put(eventLayer[1]));
			main.put("extended", new JSONObject().put("inclineEvents", new JSONArray().put(new JSONObject().put("bezier", 0).put("bezierPoints", new JSONArray().put(0d).put(0d).put(0d).put(0d)).put("easingLeft", 0d).put("easingRight", 1d).put("easingType", 0).put("end", 0d).put("endTime", new JSONArray().put(1).put(0).put(1)).put("linkgroup", 0).put("start", 0d).put("startTime", new JSONArray().put(0).put(0).put(1))))).put("father", father).put("isCover", 1);
			main.put("notes", notes).put("numOfNotes", numOfNotes).put("posControl", new JSONArray().put(new JSONObject().put("easing", 1).put("pos", 1d).put("x", 0d)).put(new JSONObject().put("easing", 1).put("pos", 1d).put("x", 9999999d))).put("sizeControl", new JSONArray().put(new JSONObject().put("easing", 1).put("size", 1d).put("x", 0d)).put(new JSONObject().put("easing", 1).put("size", 1d).put("x", 9999999d)));
			main.put("skewControl", new JSONArray().put(new JSONObject().put("easing", 1).put("skew", 0d).put("x", 0d)).put(new JSONObject().put("easing", 1).put("skew", 0d).put("x", 9999999d))).put("yControl", new JSONArray().put(new JSONObject().put("easing", 1).put("x", 0d).put("y", 1d)).put(new JSONObject().put("easing", 1).put("x", 9999999d).put("y", 1d))).put("zOrder", 0);
			return main.toString();
		} catch (Exception e) {
			catcher(e);
			return null;
		}
	}
	public JSONObject toJSONObject() {
		try {
			JSONObject[] eventLayer = new JSONObject[2];
			eventLayer[0] = new JSONObject().put("alphaEvents", alphaEvents).put("moveXEvents", moveXEvents[0]).put("moveYEvents", moveYEvents).put("rotateEvents", rotateEvents).put("speedEvents", speedEvents);
			eventLayer[1] = new JSONObject().put("moveXEvents", moveXEvents[1]);
			main.put("Group", 0).put("Name", name).put("Texture", "line.png").put("alphaControl", new JSONArray().put(new JSONObject().put("alpha", 1d).put("easing", 1).put("x", 0d)).put(new JSONObject().put("alpha", 1d).put("easing", 1).put("x", 9999999d))).put("bpmfactor", 1d).put("eventLayers", new JSONArray().put(eventLayer[0]).put(eventLayer[1]));
			main.put("extended", new JSONObject().put("inclineEvents", new JSONArray().put(new JSONObject().put("bezier", 0).put("bezierPoints", new JSONArray().put(0d).put(0d).put(0d).put(0d)).put("easingLeft", 0d).put("easingRight", 1d).put("easingType", 0).put("end", 0d).put("endTime", new JSONArray().put(1).put(0).put(1)).put("linkgroup", 0).put("start", 0d).put("startTime", new JSONArray().put(0).put(0).put(1))))).put("father", father).put("isCover", 1);
			main.put("notes", notes).put("numOfNotes", numOfNotes).put("posControl", new JSONArray().put(new JSONObject().put("easing", 1).put("pos", 1d).put("x", 0d)).put(new JSONObject().put("easing", 1).put("pos", 1d).put("x", 9999999d))).put("sizeControl", new JSONArray().put(new JSONObject().put("easing", 1).put("size", 1d).put("x", 0d)).put(new JSONObject().put("easing", 1).put("size", 1d).put("x", 9999999d)));
			main.put("skewControl", new JSONArray().put(new JSONObject().put("easing", 1).put("skew", 0d).put("x", 0d)).put(new JSONObject().put("easing", 1).put("skew", 0d).put("x", 9999999d))).put("yControl", new JSONArray().put(new JSONObject().put("easing", 1).put("x", 0d).put("y", 1d)).put(new JSONObject().put("easing", 1).put("x", 9999999d).put("y", 1d))).put("zOrder", 0);
			return main;
		} catch (Exception e) {
			catcher(e);
			return null;
		}
	}
	private void catcher(Exception e) {
		try {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/storage/emulated/0/Android/data/" + MainActivity.packageName + File.separator + System.currentTimeMillis() + ".log")));
			bw.write(sw.toString());
			bw.close();
		} catch (Exception nothing) {}
	}
}

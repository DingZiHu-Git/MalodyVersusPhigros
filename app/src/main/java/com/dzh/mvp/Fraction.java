package com.dzh.mvp;

import java.math.BigDecimal;
import org.json.JSONArray;
import org.json.JSONException;

public class Fraction {
	private int num;
	private int up;
	private int down;
	public Fraction() {
		num = 0;
		up = 0;
		down = 1;
	}
	public Fraction(int num, int up, int down) {
		this.num = num;
		this.up = up;
		this.down = down;
	}
	public Fraction(String expression) {
		String[] split1 = expression.split(":");
		num = Integer.parseInt(split1[0]);
		String[] split2 = split1[1].split("/");
		up = Integer.parseInt(split2[0]);
		down = Integer.parseInt(split2[1]);
		format();
	}
	public Fraction(JSONArray beat) throws JSONException {
		num = beat.getInt(0);
		up = beat.getInt(1);
		down = beat.getInt(2);
		format();
	}
	public Fraction add(Fraction p) {
		num += p.num;
		up = up * p.down + p.up * down;
		down *= p.down;
		format();
		return this;
	}
	public Fraction subtract(Fraction p) {
		num -= p.num;
		up = up * p.down - p.up * down;
		down *= p.down;
		format();
		return this;
	}
	public Fraction multiply(Fraction p) {
		up += num * down;
		up *= p.up + p.num * p.down;
		down *= p.down;
		num = 0;
		format();
		return this;
	}
	public Fraction divide(Fraction p) {
		int pup = p.up;
		int pdown = p.down;
		pup += p.num * pdown;
		int temp = pup;
		pup = pdown;
		pdown = temp;
		up += num * down;
		up *= pup;
		down *= pdown;
		num = 0;
		format();
		return this;
	}
	public boolean equals(Fraction p) {
		return num == p.num && up == p.up && down == p.down;
	}
	public boolean gt(Fraction p) {
		if (num > p.num) return true;
		else if (num < p.num) return false;
		else {
			int tup = up;
			int pup = p.up;
			tup *= p.down;
			pup *= down;
			if (tup > pup) return true;
			else return false;
		}
	}
	public boolean lt(Fraction p) {
		if (num < p.num) return true;
		else if (num > p.num) return false;
		else {
			int tup = up;
			int pup = p.up;
			tup *= p.down;
			pup *= down;
			if (tup < pup) return true;
			else return false;
		}
	}
	public BigDecimal toBigDecimal() {
		return new BigDecimal(num + up / (double) down);
	}
	public int[] toIntArray() {
		return new int[]{ num, up, down };
	}
	public JSONArray toJSONArray() {
		return new JSONArray().put(num).put(up).put(down);
	}
	@Override
	public String toString() {
		return num + ":" + up + "/" + down;
	}
	@Override
	public Fraction clone() {
		return new Fraction(num, up, down);
	}
	private void format() {
		if (down < 0) num = -num;
		while (up < 0) {
			up += down;
			num--;
		}
		while (up >= down) {
			up -= down;
			num++;
		}
		int gcd = gcd(up, down);
		up /= gcd;
		down /= gcd;
	}
	private int gcd(int a, int b) {
		if (b == 0) return a;
		return gcd(b, a % b);
	}
}

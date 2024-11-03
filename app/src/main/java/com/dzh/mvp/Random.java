package com.dzh.mvp;

import java.io.InvalidObjectException;

public class Random {
	public static int nextInt(int min, int max){
		return (int) ((max - min + 1) * Math.random() + min);
	}
	public static long nextLong(long min, long max){
		return (long) ((max - min + 1) * Math.random() + min);
	}
	public static short nextShort(short min, short max){
		return (short) ((max - min + 1) * Math.random() + min);
	}
	public static float nextFloat(float min, float max){
		return (float) ((max - min + 1) * Math.random() + min);
	}
	public static double nextDouble(double min, double max){
		return ((max - min + 1) * Math.random() + min);
	}
	public static boolean nextBoolean(){
		int test = (int) ((1 - 0 + 1) * Math.random() + 0);
		if (test == 0){
			return false;
		} else {
			return true;
		}
	}
	public static String nextString(String[] stringArray) throws InvalidObjectException {
		if (stringArray.length < 2){
			throw new InvalidObjectException("An invalid array given(An empty array or only have 1 item)");
		} else {
			return stringArray[(int) (((stringArray.length - 1) - 0 + 1) * Math.random() + 0)];
		}
	}
	public static String nextString(int length){
		String[] strs = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "@", "#", "$", "_", "&", "-", "+", "(", ")", "/", "*", "\"", "'", ":", ";", "!", "?" };
		String result = "";
		for (int i = 0; i < length; i++){
			result = result + strs[(int) (((strs.length - 1) - 0 + 1) * Math.random() + 0)];
		}
		return result;
	}
}

package rmc.utils;

import java.util.Random;

public class RandomUtils {
	private static final int[] SCORE_VALUES = { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 4 };

	public static int getRandomInt(int from, int to) {
		Random rand = new Random();
		return from + rand.nextInt(to - from + 1);
	}

	public static double rollOneHundred() {
		Random rand = new Random();
		return rand.nextDouble() * 100.0;
	}

	public static boolean flipCoin() {
		Random rand = new Random();
		return rand.nextBoolean();
	}

	public static int getRandomScore() {
		return SCORE_VALUES[getRandomInt(0, SCORE_VALUES.length - 1)];
	}
}

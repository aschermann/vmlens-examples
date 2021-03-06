package com.vmlens.examples.tests;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ConcurrentHashMap;
import org.junit.Test;
import com.vmlens.api.AllInterleavings;

/**
 * 
 * This example shows how to write multi-threaded tests with vmlens.
 * It is <a href="https://vmlens.com/help/manual/#interleave">described here.</a>
 * 
 * @author thomas
 *
 */


public class TestUpdateWrong {
	public void update(ConcurrentHashMap<Integer, Integer> map) {
		Integer result = map.get(1);
		if (result == null) {
			map.put(1, 1);
		} else {
			map.put(1, result + 1);
		}
	}
	@Test
	public void testUpdate() throws InterruptedException {
		try (AllInterleavings allInterleavings = 
				new AllInterleavings("tests.TestUpdateWrong");) {
			while (allInterleavings.hasNext()) {
				final ConcurrentHashMap<Integer, Integer> map = 
						new ConcurrentHashMap<Integer, Integer>();
				Thread first = new Thread(() -> {
					update(map);
				});
				Thread second = new Thread(() -> {
					update(map);
				});
				first.start();
				second.start();
				first.join();
				second.join();
				assertEquals(2, 
				  map.get(1).intValue());
			}
		}
	}
}

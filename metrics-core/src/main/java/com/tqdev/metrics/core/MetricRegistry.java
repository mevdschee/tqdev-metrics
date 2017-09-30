/* Copyright (C) 2017 Maurits van der Schee
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.tqdev.metrics.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

public class MetricRegistry {

	private volatile ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> values;

	public MetricRegistry() {
		values = new ConcurrentHashMap<>();
	}

	public void increment(String type, String key) {
		((LongAdder) values.computeIfAbsent(type, t -> new ConcurrentHashMap<>()).computeIfAbsent(key,
				k -> new LongAdder())).increment();
	}

	public void decrement(String type, String key) {
		((LongAdder) values.computeIfAbsent(type, t -> new ConcurrentHashMap<>()).computeIfAbsent(key,
				k -> new LongAdder())).decrement();
	}

	public void add(String type, String key, long value) {
		((LongAdder) values.computeIfAbsent(type, t -> new ConcurrentHashMap<>()).computeIfAbsent(key,
				k -> new LongAdder())).add(value);
	}

	public void set(String type, String key, long value) {
		LongAdder adder = new LongAdder();
		adder.add(value);
		values.computeIfAbsent(type, t -> new ConcurrentHashMap<>()).put(key, adder);
	}

	public void set(String type, String key, Gauge value) {
		values.computeIfAbsent(type, t -> new ConcurrentHashMap<>()).put(key, value);
	}

	public Iterable<String> getTypes() {
		return values.keySet();
	}

	public Iterable<String> getKeys(String type) throws NullPointerException {
		return values.get(type).keySet();
	}

	public boolean has(String type, String key) throws NullPointerException {
		return values.get(type).containsKey(key);
	}

	public long get(String type, String key) throws NullPointerException {
		Object o = values.get(type).get(key);
		if (o instanceof LongAdder) {
			return ((LongAdder) o).sum();
		} else if (o instanceof Gauge) {
			return ((Gauge) o).measure();
		} else {
			return -1;
		}
	}

	// singleton pattern

	/** The single instance of Storage. */
	private static final MetricRegistry instance = new MetricRegistry();

	/**
	 * Gets the single instance of the Storage.
	 *
	 * @return single instance of Storage
	 */
	public static MetricRegistry getInstance() {
		return instance;
	}

}

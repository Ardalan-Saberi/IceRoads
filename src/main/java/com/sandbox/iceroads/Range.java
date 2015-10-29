package com.sandbox.iceroads;

import java.util.Optional;

public class Range<T extends Comparable<T>> {

	private final Optional<T> upperBound;

	public Optional<T> getUpperBound() {
		return upperBound;
	}

	public Optional<T> getLowerBound() {
		return lowerBound;
	}

	private final Optional<T> lowerBound;

	public static class IllegalRangeException extends Exception {
		private static final long serialVersionUID = -3840903002864503185L;};

	public class Builder {
		private T upperBound;
		private T lowerBound;

		public Builder setUpperBound(T u) {
			upperBound = u;
			return this;
		}

		public Builder setLowerBound(T l) {
			lowerBound = l;
			return this;
		}

		public Range<T> build() throws IllegalRangeException {
			if (upperBound != null && lowerBound != null) {
				if (upperBound.compareTo(lowerBound) < 0) {
					throw new IllegalRangeException();
				}
			}
			return new Range<T>(Optional.of(upperBound), Optional.of(lowerBound));
		}
	}

	private Range(Optional<T> upperBound, Optional<T> lowerBound) {
		this.upperBound = upperBound;
		this.lowerBound = lowerBound;
	}
}
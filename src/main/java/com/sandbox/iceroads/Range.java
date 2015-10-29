package com.sandbox.iceroads;

import java.util.Optional;

public class Range<T extends Comparable<T>> {

	private final Optional<T> upperBound;
	private final Optional<T> lowerBound;
	
	public Optional<T> getUpperBound() {
		return upperBound;
	}

	public Optional<T> getLowerBound() {
		return lowerBound;
	}

	public static class IllegalRangeException extends Exception {
		private static final long serialVersionUID = -3840903002864503185L;};

	public static final class Builder<T extends Comparable<T>> {
		private T upperBound;
		private T lowerBound;

		public Builder<T> setUpperBound(T u) {
			upperBound = u;
			return this;
		}

		public Builder<T> setLowerBound(T l) {
			lowerBound = l;
			return this;
		}

		public Range<T> build() throws IllegalRangeException {
			if (upperBound != null && lowerBound != null) {
				if (upperBound.compareTo(lowerBound) < 0) {
					throw new IllegalRangeException();
				}
			}else if (upperBound == null && lowerBound == null) {
				throw new IllegalRangeException();
			}
			return new Range<T>(Optional.ofNullable(upperBound), Optional.ofNullable(lowerBound));
		}
	}

	private Range(Optional<T> upperBound, Optional<T> lowerBound) {
		this.upperBound = upperBound;
		this.lowerBound = lowerBound;
	}
	
	public boolean inRange(T t){
		return ((!upperBound.isPresent() ||  upperBound.get().compareTo(t) > 0) &&
		(!lowerBound.isPresent() ||  lowerBound.get().compareTo(t) <= 0));
	}
	
	@Override
	public String toString(){
		String result = "Range";
		
		if (upperBound.isPresent() && lowerBound.isPresent()){
			result += "[" +  lowerBound.get().toString() + " < x < " + upperBound.get().toString() + "]";
		}else{
			if (upperBound.isPresent()){
				result += "[x < " + upperBound.get().toString() + "]";
			}else if (lowerBound.isPresent()){
				result += "[" +  lowerBound.get().toString() + " < x]";
			}
		}
		return result;
	}
}
package com.sandbox.iceroads;

import java.util.Optional;

public class Range <T extends Comparable>{
	
		private final Optional<T> upperBound;
		private final Optional<T> lowerBound;
		public static class IllegalRangeException extends Exception{};
		
		public class Builder{	
			private T upperBound;
			private T lowerBound;
			
			public Builder setUpperBound(T u){
				upperBound = u;
				return this;
			}
			public Builder setLowerBound(T l){
				lowerBound = l;
				return this;
			}
			
			public Range<T> build() throws IllegalRangeException{
				if (upperBound != null && lowerBound != null){
					if (upperBound.compareTo(lowerBound)<0){
						throw new IllegalRangeException();
					}
				}
				return new Range(Optional.of(upperBound), Optional.of(lowerBound));
			}
		}
		private Range(Optional<T> upperBound, Optional<T> lowerBound){
			this.upperBound = upperBound;
			this.lowerBound = lowerBound;
		}
	}
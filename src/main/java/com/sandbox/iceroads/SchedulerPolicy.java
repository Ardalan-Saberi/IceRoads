package com.sandbox.iceroads;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class SchedulerPolicy {

	private final Instant start;
	private final List<PolicyRule> rules;

	public Instant getStart() {
		return start;
	}

	public List<PolicyRule> getRules() {
		return rules;
	}

	public static class Builder {
		private final Instant start;
		private final List<PolicyRule> rules = new ArrayList<PolicyRule>();

		public Builder(Instant start) {
			this.start = start;
		}

		public Builder addRule(int duration, boolean isWeightAscending) {
			if (isWeightAscending) {
				this.rules.add(new PolicyRule(
						PolicyRule.WEIGHT_ASCENDING_COMPARATOR, duration));
			} else {
				this.rules.add(new PolicyRule(
						PolicyRule.WEIGHT_DESCENDING_COMPARATOR, duration));
			}
			return this;
		}

		public Builder addRule(int duration, boolean isWeightAscending,
				Range<BigDecimal> weightRange) {
			if (isWeightAscending) {
				this.rules.add(new PolicyRule(
						PolicyRule.WEIGHT_ASCENDING_COMPARATOR, duration,
						weightRange));
			} else {
				this.rules.add(new PolicyRule(
						PolicyRule.WEIGHT_DESCENDING_COMPARATOR, duration,
						weightRange));
			}
			return this;
		}

		public Builder remainingRule(boolean isWeightAscending) {
			if (isWeightAscending) {
				this.rules.add(new PolicyRule(
						PolicyRule.WEIGHT_ASCENDING_COMPARATOR));
			} else {
				this.rules.add(new PolicyRule(
						PolicyRule.WEIGHT_DESCENDING_COMPARATOR));
			}
			return this;
		}

		public SchedulerPolicy build() {
			return new SchedulerPolicy(this);
		}
	}

	private SchedulerPolicy(Builder builder) {
		this.start = builder.start;
		this.rules = builder.rules;
	}

	public static class PolicyRule {
		private final Optional<Integer> duration;
		private final Comparator<Shipment> comparator;
		private final Optional<Range<BigDecimal>> weightRange;

		public static Comparator<Shipment> DEFAULT_COMPARATOR = new Comparator<Shipment>() {
			@Override
			public int compare(Shipment o1, Shipment o2) {
				return o2.getPriority() - o1.getPriority();
			}

		};
		public static Comparator<Shipment> WEIGHT_ASCENDING_COMPARATOR = new Comparator<Shipment>() {

			@Override
			public int compare(Shipment o1, Shipment o2) {
				if (DEFAULT_COMPARATOR.compare(o1, o2) != 0) {
					return DEFAULT_COMPARATOR.compare(o1, o2);
				} else {
					return o2.getWeight().subtract(o1.getWeight()).signum();
				}
			}
		};

		public static Comparator<Shipment> WEIGHT_DESCENDING_COMPARATOR = new Comparator<Shipment>() {

			@Override
			public int compare(Shipment o1, Shipment o2) {
				if (DEFAULT_COMPARATOR.compare(o1, o2) != 0) {
					return DEFAULT_COMPARATOR.compare(o1, o2);
				} else {
					return o1.getWeight().subtract(o2.getWeight()).signum();
				}
			}
		};

		public Optional<Integer> getDuration() {
			return duration;
		}

		public Comparator<Shipment> getComparator() {
			return comparator;
		}

		public Optional<Range<BigDecimal>> getWeightRange() {
			return weightRange;
		}

		public boolean weightCheck(BigDecimal weight) {
			boolean result = true;

			if (weightRange.isPresent()) {
				if (this.getWeightRange().get().getLowerBound().isPresent()
						&& weight.compareTo(this.getWeightRange().get()
								.getLowerBound().get()) <= 0) {
					result = false;

				}
				if (this.getWeightRange().get().getUpperBound().isPresent()
						&& weight.compareTo(this.getWeightRange().get()
								.getUpperBound().get()) > 0) {
					result = false;

				}
			}

			return result;

		}

		public PolicyRule(Comparator<Shipment> comparator) {
			this(comparator, Optional.empty(), Optional.empty());
		}

		public PolicyRule(Comparator<Shipment> comparator, int duration) {
			this(comparator, Optional.of(duration), Optional.empty());
		}

		public PolicyRule(Comparator<Shipment> comparator, int duration,
				Range<BigDecimal> weightRange) {
			this(comparator, Optional.of(duration), Optional.of(weightRange));
		}

		private PolicyRule(Comparator<Shipment> comparator,
				Optional<Integer> duration,
				Optional<Range<BigDecimal>> weightRange) {
			this.comparator = comparator;
			this.duration = duration;
			this.weightRange = weightRange;
		}

	}

}

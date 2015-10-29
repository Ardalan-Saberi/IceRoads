package com.sandbox.iceroads;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class SchedulerPolicy {
	private final LocalDateTime policyStart;
	private final List<PolicyRule> rules;

	public LocalDateTime getStart() {
		return policyStart;
	}

	public List<PolicyRule> getRules() {
		return rules;
	}

	public static class Builder {
		private final LocalDateTime policyStart;
		private final List<PolicyRule> rules = new ArrayList<PolicyRule>();

		public Builder(LocalDateTime policyStart) {
			this.policyStart = policyStart;
		}

		public Builder addRule(boolean isWeightAscending,Optional<Period> period,
				Optional<Range<BigDecimal>> weightRange) {

			rules.add(new PolicyRule(
					(isWeightAscending ? PolicyRule.WEIGHT_ASCENDING_COMPARATOR
							: PolicyRule.WEIGHT_DESCENDING_COMPARATOR), period,
					weightRange));

			return this;
		}

		public SchedulerPolicy build() {

			return new SchedulerPolicy(this);
		}
	}

	private SchedulerPolicy(Builder builder) {
		this.policyStart = builder.policyStart;
		this.rules = builder.rules;
	}

	public static final class PolicyRule {
		private final Comparator<Shipment> comparator;
		private final Optional<Range<BigDecimal>> weightRange;
		private final Optional<Period> period;

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

		public Comparator<Shipment> getComparator() {
			return comparator;
		}

		public Optional<Range<BigDecimal>> getWeightRange() {
			return weightRange;
		}
		
		public Optional<Period> getPeriod() {
			return period;
		}


		public boolean canShip(BigDecimal weight) {
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

		private PolicyRule(Comparator<Shipment> comparator,
				Optional<Period> period, Optional<Range<BigDecimal>> weightRange) {
			this.comparator = comparator;
			this.period = period;
			this.weightRange = weightRange;
		}

	}

}

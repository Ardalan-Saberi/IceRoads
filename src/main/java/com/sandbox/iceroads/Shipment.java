package com.sandbox.iceroads;

import java.math.BigDecimal;

class Shipment {
	private final int id;
	private final BigDecimal weight;
	private final int priority;
	
	

	public Shipment(int id, BigDecimal weight, int priority) {
		this.id = id;
		this.priority = priority;
		this.weight = weight;
	}

	public int getId() {
		return id;
	}

	public BigDecimal getWeight() {
		return weight;
	}
	
	public int getPriority() {
		return priority;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof Shipment))
			return false;
		return ((Shipment) o).id == id;
	}
	
	@Override
	public String toString() {
		return "Shipment [id=" + id + ", weight=" + weight + ", priority="
				+ priority + "]";
	}
}
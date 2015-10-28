package com.sandbox.iceroads;

import java.math.BigDecimal;

class Shipment {
	private final short id;
	private final BigDecimal weight;
	private final short priority;
	
	

	public Shipment(short id, BigDecimal weight, short priority) {
		this.id = id;
		this.priority = priority;
		this.weight = weight;
	}

	public short getId() {
		return id;
	}

	public BigDecimal getWeight() {
		return weight;
	}
	
	public short getPriority() {
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
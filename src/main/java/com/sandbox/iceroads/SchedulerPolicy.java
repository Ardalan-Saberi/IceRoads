package com.sandbox.iceroads;

import java.time.Instant;
import java.util.List;

public class SchedulerPolicy {
	private final Instant start;
	private final List<PolicyRule> rules;
	
	public static class Builder{
		
	}
	private SchedulerPolicy(Instant start, List<PolicyRule> rules){
		this.start = start;
		this.rules = rules;
	}
	
}

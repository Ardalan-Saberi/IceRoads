package com.sandbox.iceroads;

import static org.junit.Assert.*;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.sandbox.iceroads.ShipmentScheduler.ShipmentFileParsingError;

@RunWith(Parameterized.class)
public class ShipmentSchedulerTest {
	private String inStr;
	private String outStr;
	private String exStr;
	private Class<? extends Exception> expected;

	public static final String TEST_RESOURCE_FOLDER = "./src/test/resources/";

	public ShipmentSchedulerTest(

	String inStr, String outStr, String exStr,
			Class<? extends Exception> expected) {
		this.inStr = inStr;
		this.outStr = outStr;
		this.exStr = exStr;
		this.expected = expected;
	}

	@Parameterized.Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				/*{ "test1.csv", null, null, ShipmentFileParsingError.class },
				{ "test2.csv", "result2.csv", "expected2.csv", null },*/
				{ "test3.csv", "result3.csv", "expected3.csv", null },
				{ "test4.csv", "result4.csv", "expected4.cs", null } });
	}

	@Test
	public void fileTest() throws Throwable {
		try {
			SchedulerPolicy.Builder builder = new SchedulerPolicy.Builder(

			LocalDateTime.of(2015, 2, 1, 8, 0));
			builder.addRule(true, Optional.of(Period.ofDays(15)), Optional
					.of((new Range.Builder<BigDecimal>()).setUpperBound(
							BigDecimal.valueOf(15000L)).build()));
			builder.addRule(false, Optional.empty(), Optional
					.of((new Range.Builder<BigDecimal>()).setLowerBound(
							BigDecimal.valueOf(15000L)).build()));
			builder.addRule(false, Optional.empty(), Optional.empty());
			
			File in = new File(TEST_RESOURCE_FOLDER + inStr);
			File out = new File(TEST_RESOURCE_FOLDER + outStr);
			File exp = new File(TEST_RESOURCE_FOLDER+exStr);
			SchedulerPolicy policy;
			policy = builder.build();

			ShipmentScheduler.schedule(in, out, policy);
			
			assertTrue(FileUtils.contentEquals(exp, out));

		} catch (Throwable t) {
			if (expected == null || !expected.equals(t.getClass())) {
				t.printStackTrace();
				throw t;
			}
		}
	}

}
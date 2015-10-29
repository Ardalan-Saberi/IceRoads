package com.sandbox.iceroads;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.sandbox.iceroads.Range.IllegalRangeException;

@RunWith(Parameterized.class)
public class RangeCreationTest {
	private BigDecimal lowerBound;
	private BigDecimal upperBound;
	private String result;
	private Class<? extends Exception> expected;

	public RangeCreationTest(BigDecimal lowerBound, BigDecimal upperBound,
			String result, Class<? extends Exception> expected) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.result = result;
		this.expected = expected;
	}

	@Parameterized.Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{ null, null, null, IllegalRangeException.class },
				{ null, BigDecimal.valueOf(33.3), "Range[x < 33.3]", null },
				{ BigDecimal.valueOf(-27), null, "Range[-27 < x]", null },
				{ BigDecimal.valueOf(-27), BigDecimal.valueOf(33.3),
						"Range[-27 < x < 33.3]", null },
				{ BigDecimal.valueOf(33.3), BigDecimal.valueOf(-27), null,
						IllegalRangeException.class } });
	}

	@Test
	public void rangeTest() throws Exception {
		try {
			Range.Builder<BigDecimal> builder = new Range.Builder<BigDecimal>();
			if (upperBound != null) {
				builder.setUpperBound(upperBound);
			}
			if (lowerBound != null) {
				builder.setLowerBound(lowerBound);
			}

			Range<BigDecimal> range = builder.build();

			assertEquals("Range Test Failed", range.toString(), result);

		} catch (Throwable t) {
			if (expected == null || !expected.equals(t.getClass())) {
				t.printStackTrace();
				throw t;
			}
		}
	}
}
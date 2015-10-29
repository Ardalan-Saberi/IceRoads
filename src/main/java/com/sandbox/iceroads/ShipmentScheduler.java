package com.sandbox.iceroads;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.Policy;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShipmentScheduler {
	private static final String DELIMITER = ",";
	private static int idColumnOrder, unitColumnOrder, weightColumnOrder,
			priorityColumnOrder;
	public static final BigDecimal POUND_TO_KILOGRAM = new BigDecimal(
			0.45359237);
	public static final int NONE_PRIORITY = 4;
	public static final Duration DURATION_PER_STEP = Duration.ofHours(1L);
	public static final int SHIPMENT_PER_STEP = 7;
	private static Logger logger = (Logger) LoggerFactory
			.getLogger(ShipmentScheduler.class);

	public static class UnitNotSupprtedException extends RuntimeException {
		private static final long serialVersionUID = -6530637521303449892L;
	};

	public static class ShipmentFileParsingError extends RuntimeException {

		private static final long serialVersionUID = 6771491374354127870L;

		public ShipmentFileParsingError(String message, Throwable cause) {
			super(message, cause);
		}
	};

	public static class SchedulerOutputFileIOException extends RuntimeException {
		private static final long serialVersionUID = -6600544132823946919L;

		public SchedulerOutputFileIOException(String message, Throwable cause) {
			super(message, cause);
		}
	};

	private static void schedule(File in, File out, SchedulerPolicy policy) {

		List<Shipment> shipmentList = parseShipmentList(in);
		Instant time = policy.getStart();
		Integer timeSlot = 1;

		try (FileWriter writer = new FileWriter(out)) {

			for (SchedulerPolicy.PolicyRule rule : policy.getRules()) {
				shipmentList.sort(rule.getComparator());

				for (Shipment shipment : shipmentList) {
					if (rule.weightCheck(shipment.getWeight())) {

					}
				}
			}

		} catch (IOException ioe) {
			throw new SchedulerOutputFileIOException("Output File: "
					+ out.getPath(), ioe);
		}

	}

	private class TimeSlot {
		private int slot = 0;
		private Instant time;

		public TimeSlot(Instant start) {
			time = start;
		}

		public int getSlot() {
			return slot;
		}

		public Instant getTime() {
			return time;
		}

		public void nextSlot() {
			if (slot < SHIPMENT_PER_STEP) {
				slot++;
			} else {
				slot = 0;
				time = time.plus(DURATION_PER_STEP);
			}

		}
	}

	private static List<Shipment> parseShipmentList(File file) {

		List<Shipment> shipmentlist = new ArrayList<Shipment>();
		int lineNumber = 0;

		try (Scanner scanner = new Scanner(file);) {

			int columnPosition = 0;

			for (String columnName : scanner.next().split(DELIMITER)) {
				switch (columnName) {
				case "id":
					idColumnOrder = columnPosition;
				case "unit":
					unitColumnOrder = columnPosition;
				case "priority":
					priorityColumnOrder = columnPosition;
				case "weight":
					weightColumnOrder = columnPosition;
				}
				columnPosition++;
			}

			while (scanner.hasNext()) {
				lineNumber++;
				Shipment newShipment = parseShipment(scanner.next());
				shipmentlist.add(newShipment);

				logger.debug("Shipment Order Added (" + newShipment.toString()
						+ ")");
			}

			logger.info("Shipments File Parsed Successfully");
			return shipmentlist;

		} catch (FileNotFoundException fnfe) {
			throw new ShipmentFileParsingError("File Doesn't Exist", fnfe);
		} catch (NumberFormatException | UnitNotSupprtedException ae) {
			throw new ShipmentFileParsingError(
					"Shipment Parsing Error at line " + lineNumber, ae);
		}

	}

	private static Shipment parseShipment(String shipmentStr) {
		String[] args = shipmentStr.split(DELIMITER);
		int id, priority;
		BigDecimal weight;

		id = Integer.parseInt(args[idColumnOrder]);

		weight = parseWeight(args[weightColumnOrder], args[unitColumnOrder]);

		if (args.length >= priorityColumnOrder) {
			priority = Integer.parseInt(args[priorityColumnOrder]);
			return new Shipment(id, weight, priority);
		}

		return new Shipment(id, weight, NONE_PRIORITY);
	}

	private static BigDecimal parseWeight(String weightStr, String unitStr) {
		switch (unitStr) {
		case "ton":
			return new BigDecimal(weightStr).multiply(BigDecimal.valueOf(1000));
		case "kg":
			return new BigDecimal(weightStr);
		case "lbs":
			return new BigDecimal(weightStr).multiply(POUND_TO_KILOGRAM);
		default:
			throw new UnitNotSupprtedException();
		}
	}

}

package com.sandbox.iceroads;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sandbox.iceroads.SchedulerPolicy.PolicyRule;

public class ShipmentScheduler {
	private static final Duration DURATION_PER_STEP = Duration.ofHours(1L);
	private static final int SHIPMENT_PER_STEP = 7;
	private static final String DELIMITER = ",";
	private static int idColumnOrder, unitColumnOrder, weightColumnOrder,
			priorityColumnOrder;
	private static final BigDecimal POUND_TO_KILOGRAM = new BigDecimal(
			0.45359237);
	private static final int NONE_PRIORITY = 4;

	private static final DateTimeFormatter DATETIME_PATTERN = DateTimeFormatter
			.ofPattern("yyyy-MM-dd, HH:mm");
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

	public static void schedule(File in, File out, SchedulerPolicy policy)
			throws IOException {

		List<Shipment> shipmentList = parseShipmentList(in);
		TimeSlot ts = new TimeSlot(policy.getStart());
		LocalDateTime ruleStart = policy.getStart();
		Shipment shipment;
		String shipmentStr;

		try (FileWriter writer = new FileWriter(out)) {

			for (SchedulerPolicy.PolicyRule rule : policy.getRules()) {
				shipmentList.sort(rule.getComparator());
				Iterator<Shipment> it = shipmentList.iterator();
				ruleStart = ts.getDateTime();

				while (ts.isRuleInEffect(rule, ruleStart) && it.hasNext()) {
					shipment = it.next();

					if (rule.canShip(shipment.getWeight())) {
						shipmentStr = String.format("%s, %d, %d\n", ts
								.getDateTime().format(DATETIME_PATTERN), ts
								.getSlot(), shipment.getId());
						writer.write(shipmentStr);
						it.remove();

						logger.debug("Shipment Scheduled (" + shipmentStr + ")");
						ts.nextSlot();
					}
				}
				while (ts.isRuleInEffect(rule, ruleStart)) {
					ts.nextSlot();
				}

			}
		} catch (IOException ioe) {
			throw new SchedulerOutputFileIOException("Output File: "
					+ out.getPath(), ioe);
		}

	}

	private static List<Shipment> parseShipmentList(File file)
			throws IOException {

		List<Shipment> shipmentlist = new ArrayList<Shipment>();
		int lineNumber = 0;
		String line = null;

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(file)))) {

			int columnPosition = 0;
			line = reader.readLine();

			for (String columnName : line.split(DELIMITER)) {

				if ("id".equals(columnName)) {
					idColumnOrder = columnPosition;
				} else if ("unit".equals(columnName)) {
					unitColumnOrder = columnPosition;
				} else if ("priority".equals(columnName)) {
					priorityColumnOrder = columnPosition;
				} else if ("weight".equals(columnName)) {
					weightColumnOrder = columnPosition;
				}
				columnPosition++;
			}

			while ((line = reader.readLine()) != null) {
				lineNumber++;
				Shipment newShipment = parseShipment(line);
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

		if (args.length > priorityColumnOrder) {
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

	static final class TimeSlot {
		private int slot = 1;
		private LocalDateTime datetime;

		public TimeSlot(LocalDateTime init) {
			datetime = init;
		}

		public int getSlot() {
			return slot;
		}

		public LocalDateTime getDateTime() {
			return datetime;
		}

		public boolean isRuleInEffect(PolicyRule rule, LocalDateTime ruleStart) {
			return (ruleStart.compareTo(this.getDateTime()) <= 0 && (
						(rule.getPeriod().isPresent() && 
						 ruleStart.plus(rule.getPeriod().get()).compareTo(this.getDateTime()) > 0) 
						|| 
						!rule.getPeriod().isPresent()));
		}

		public void nextSlot() {
			if (slot < SHIPMENT_PER_STEP + 1) {
				slot++;
			} else {
				slot = 1;
				datetime = datetime.plus(DURATION_PER_STEP);
			}

		}
	}

}

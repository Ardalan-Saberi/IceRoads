package com.sandbox.iceroads;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShipmentScheduler {
	private static final String DELIMITER = ",";
	private static short idColumnOrder, unitColumnOrder, weightColumnOrder,
			priorityColumnOrder;
	public static final BigDecimal LB2KG = new BigDecimal(0.45359237);
	public static final short NONE_PRIORITY = 4;
	private static Logger logger = (Logger) LoggerFactory
			.getLogger(ShipmentScheduler.class);

	public static class UnitNotSupprtedException extends RuntimeException {
	};

	public static class ShipmentFileParsingError extends RuntimeException {
		public ShipmentFileParsingError(String message, Throwable cause) {
			super(message, cause);
		}
	};

	private static List<Shipment> parseShipmentLsit(File f) {

		List<Shipment> shipmentlist = new ArrayList<Shipment>();
		short lineNumber = 0;

		try {

			Scanner scanner = new Scanner(f);
			short columnPosition = 0;

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
				Shipment newShipment= parseShipment(scanner.next());
				shipmentlist.add(newShipment);
				
				logger.debug("Shipment Parsed: " + newShipment.toString());
			}
			
			logger.info("File Parsed Successfully");
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
		short id, priority;
		BigDecimal weight;

		id = Short.parseShort(args[idColumnOrder]);

		weight = parseWeight(args[weightColumnOrder], args[unitColumnOrder]);

		if (args.length >= priorityColumnOrder) {
			priority = Short.parseShort(args[priorityColumnOrder]);
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
			return new BigDecimal(weightStr).multiply(LB2KG);
		default:
			throw new UnitNotSupprtedException();
		}
	}

}

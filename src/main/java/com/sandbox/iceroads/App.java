package com.sandbox.iceroads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sandbox.iceroads.Range.IllegalRangeException;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.*;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class App {
	public static final String HELP = "Usage: iceroads INPUT_CSV_FILE [OUTPUT_CSV_FILE]";

	public static void main(String[] args) throws IOException,
			IllegalRangeException {

		Logger logger = LoggerFactory.getLogger(App.class);
		logger.info("IceRoads Scheduler Started (" + Instant.now() + ")");

		logger.debug(">iceroads "
				+ Arrays.asList(args).stream().collect(Collectors.joining(",")));

		File in = new File("");
		File out = new File("");
		String message;

		if (args.length > 0) {
			if (Paths.get(args[0]).toFile().isFile()) {
				in = Paths.get(args[0]).toFile();
			} else {
				message = "Input File Not Found.";
				logger.error(message + " (" + args[0] + ")");
				System.out.println(message);

			}
			if (args.length == 2) {
				if (Paths.get(args[1]).toFile().isFile()) {
					out = Paths.get(args[0]).toFile();
				} else {
					out = Files.createFile(
							Paths.get("schedule-" + System.currentTimeMillis()
									+ ".csv")).toFile();
					message = "Output File Not Found.";
					logger.error(message + " (" + args[0] + ")");
					System.out.println(message);
				}
			} else {
				System.out.println(HELP);
			}
		} else {
			System.out.println(HELP);
		}

		if (in.isFile() && out.isFile()) {

			SchedulerPolicy.Builder builder = new SchedulerPolicy.Builder(
					LocalDateTime.of(2015, 2, 1, 8, 0));
			builder.addRule(true, Optional.of(Period.ofDays(15)), Optional
					.of((new Range.Builder<BigDecimal>()).setUpperBound(
							BigDecimal.valueOf(15000L)).build()));
			builder.addRule(false, Optional.empty(), Optional
					.of((new Range.Builder<BigDecimal>()).setLowerBound(
							BigDecimal.valueOf(15000L)).build()));
			builder.addRule(false, Optional.empty(), Optional.empty());
			builder.build();
		}
		// ShipmentScheduler.schedule(null, null, null);

		logger.info("IceRoads Scheduler Finihsed (" + Instant.now() + ")");

	}
}

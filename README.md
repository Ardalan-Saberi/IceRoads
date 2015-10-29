Garner IceRoads Challange

Setup: 

1- git clone https://github.com/Ardalan-Saberi/IceRoads.git
2- mvn clean install
3- got to target directory
4- java -jar iceroads-jar-with-dependencies.jar sample.csv out.csv

Usage: Usage: iceroads INPUT_CSV_FILE [OUTPUT_CSV_FILE]


Project Structure:

	/src/main/java/org/sandbox/iceroads/
		- App.java : Jar's Main Class
		- Shipment.java : Object representation of a shipment request in input csv file
		- SchedulerPolicy.java: Build rules that regulates scheduler, by enforcing a weight ascending / descending order, of shipments weighing btween a arbitrary range (optionally), for certain number of days(optionally)
		- ShipmentScheduler: exposes only static "schedule()" function to take in a policy, input and output files and create a shipping schedule
		- Range: Representing a range of comparalbe

	/src/test/java/
		- RangeCreationTest: Unit test for Range;
		- ShipmentSchedulre: end to end test using /src/test/resources/(test cases and expected outcomes)

	


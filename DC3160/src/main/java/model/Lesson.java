package model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Lesson {


	protected String description;
	protected String startTime;
	protected String date;
	protected String endTime;
	protected int level;

	protected String ID;

	// There is no no-arguments constructor, so at the moment this class can't be instantiated directly from JSPX using 'useBean'

	public Lesson(String description, Timestamp startDateTime, Timestamp endDateTime, int level, String id) {

		this.description = description;
		this.level = level;
		this.ID = id;

		// Use the Calendar class to convert between a Date object and formatted strings.
		Calendar c = Calendar.getInstance();
		c.setTime(startDateTime);
		// Get the details from this Date object
		SimpleDateFormat dateformatter = new SimpleDateFormat("E, dd MMM, yyyy");
		this.date = dateformatter.format(c.getTime());

		dateformatter = new SimpleDateFormat("kk:mm");
		this.startTime = dateformatter.format(c.getTime());

		// Extract the details from the 'endDateTime' Date object
		c.setTime(endDateTime);
		dateformatter = new SimpleDateFormat("kk:mm");
		this.endTime = dateformatter.format(c.getTime());
	}

	// We can make a copy of a lesson object, for example to store in a session.
	// This is done by populating the parameters of the new lesson using the accessor (getter) methods of the original.
	public Lesson(Lesson l) {

		this.description = l.getDescription();
		this.ID = l.getId();
		this.level = l.level;
		this.date = l.getDate();
		this.startTime = l.getStartTime();
		this.endTime = l.getEndTime();
	}

	/* Getter methods for all the properties of the object.
	 * At the moment, this class has no setters.
	 */
	public String getId() {
		return ID;
	}

	public String getDescription() {
		return description;
	}

	public String getDate() {
		return this.date;
	}

	public String getStartTime() {
		return this.startTime;
	}

	public String getEndTime() {
		return this.endTime;
	}

	public int getLevel() {
		return this.level;
	}

}

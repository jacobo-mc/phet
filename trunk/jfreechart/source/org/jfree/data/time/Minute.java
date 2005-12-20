/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * -----------
 * Minute.java
 * -----------
 * (C) Copyright 2001-2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes
 * -------
 * 11-Oct-2001 : Version 1 (DG);
 * 18-Dec-2001 : Changed order of parameters in constructor (DG);
 * 19-Dec-2001 : Added a new constructor as suggested by Paul English (DG);
 * 14-Feb-2002 : Fixed bug in Minute(Date) constructor, and changed the range 
 *               to start from zero instead of one (DG);
 * 26-Feb-2002 : Changed getStart(), getMiddle() and getEnd() methods to 
 *               evaluate with reference to a particular time zone (DG);
 * 13-Mar-2002 : Added parseMinute() method (DG);
 * 19-Mar-2002 : Changed API, the minute is now defined in relation to an 
 *               Hour (DG);
 * 10-Sep-2002 : Added getSerialIndex() method (DG);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 10-Jan-2003 : Changed base class and method names (DG);
 * 13-Mar-2003 : Moved to com.jrefinery.data.time package and implemented 
 *               Serializable (DG);
 * 21-Oct-2003 : Added hashCode() method, and new constructor for 
 *               convenience (DG);
 * 30-Sep-2004 : Replaced getTime().getTime() with getTimeInMillis() (DG);
 * 04-Nov-2004 : Reverted change of 30-Sep-2004, because it won't work for 
 *               JDK 1.3 (DG);
 *
 */

package org.jfree.data.time;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Represents a minute.  This class is immutable, which is a requirement for 
 * all {@link RegularTimePeriod} subclasses.
 */
public class Minute extends RegularTimePeriod implements Serializable {

    /** For serialization. */
    private static final long serialVersionUID = 2144572840034842871L;
    
    /** Useful constant for the first minute in a day. */
    public static final int FIRST_MINUTE_IN_HOUR = 0;

    /** Useful constant for the last minute in a day. */
    public static final int LAST_MINUTE_IN_HOUR = 59;

    /** The hour in which the minute falls. */
    private Hour hour;

    /** The minute. */
    private int minute;

    /**
     * Constructs a new Minute, based on the system date/time.
     */
    public Minute() {
        this(new Date());
    }

    /**
     * Constructs a new Minute.
     *
     * @param minute  the minute (0 to 59).
     * @param hour  the hour (<code>null</code> not permitted).
     */
    public Minute(int minute, Hour hour) {
        if (hour == null) {
            throw new IllegalArgumentException("Null 'hour' argument.");
        }
        this.minute = minute;
        this.hour = hour;
    }

    /**
     * Constructs a new Minute, based on the supplied date/time.
     *
     * @param time  the time (<code>null</code> not permitted).
     */
    public Minute(Date time) {
        // defer argument checking
        this(time, RegularTimePeriod.DEFAULT_TIME_ZONE);
    }

    /**
     * Constructs a new Minute, based on the supplied date/time and timezone.
     *
     * @param time  the time (<code>null</code> not permitted).
     * @param zone  the time zone (<code>null</code> not permitted).
     */
    public Minute(Date time, TimeZone zone) {

        if (time == null) {
            throw new IllegalArgumentException("Null 'time' argument.");
        }
        if (zone == null) {
            throw new IllegalArgumentException("Null 'zone' argument.");
        }
        Calendar calendar = Calendar.getInstance(zone);
        calendar.setTime(time);
        int min = calendar.get(Calendar.MINUTE);
        this.minute = min;
        this.hour = new Hour(time, zone);

    }
    
    /**
     * Creates a new minute.
     * 
     * @param minute  the minute (0-59).
     * @param hour  the hour (0-23).
     * @param day  the day (1-31).
     * @param month  the month (1-12).
     * @param year  the year (1900-9999).
     */
    public Minute(int minute, 
                  int hour, 
                  int day, 
                  int month, 
                  int year) {
        this(minute, new Hour(hour, new Day(day, month, year)));
    }

    /**
     * Returns the hour.
     *
     * @return The hour (never <code>null</code>).
     */
    public Hour getHour() {
        return this.hour;
    }

    /**
     * Returns the minute.
     *
     * @return The minute.
     */
    public int getMinute() {
        return this.minute;
    }

    /**
     * Returns the minute preceding this one.
     *
     * @return The minute preceding this one.
     */
    public RegularTimePeriod previous() {

        Minute result;
        if (this.minute != FIRST_MINUTE_IN_HOUR) {
            result = new Minute(this.minute - 1, this.hour);
        }
        else { // we are at the first minute in the hour...
            Hour prevHour = (Hour) this.hour.previous();
            if (prevHour != null) {
                result = new Minute(LAST_MINUTE_IN_HOUR, prevHour);
            }
            else {
                result = null;
            }
        }
        return result;

    }

    /**
     * Returns the minute following this one.
     *
     * @return The minute following this one.
     */
    public RegularTimePeriod next() {

        Minute result;
        if (this.minute != LAST_MINUTE_IN_HOUR) {
            result = new Minute(this.minute + 1, this.hour);
        }
        else { // we are at the last minute in the hour...
            Hour nextHour = (Hour) this.hour.next();
            if (nextHour != null) {
                result = new Minute(FIRST_MINUTE_IN_HOUR, nextHour);
            }
            else {
                result = null;
            }
        }
        return result;

    }

    /**
     * Returns a serial index number for the minute.
     *
     * @return The serial index number.
     */
    public long getSerialIndex() {
        return this.hour.getSerialIndex() * 60L + this.minute;
    }

    /**
     * Returns the first millisecond of the minute.
     *
     * @param calendar  the calendar (which defines the timezone).
     *
     * @return The first millisecond.
     */
    public long getFirstMillisecond(Calendar calendar) {

        int year = this.hour.getDay().getYear();
        int month = this.hour.getDay().getMonth() - 1;
        int day = this.hour.getDay().getDayOfMonth();

        calendar.clear();
        calendar.set(year, month, day, this.hour.getHour(), this.minute, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        //return calendar.getTimeInMillis();  // this won't work for JDK 1.3
        return calendar.getTime().getTime();

    }

    /**
     * Returns the last millisecond of the minute.
     *
     * @param calendar  the calendar and timezone.
     *
     * @return The last millisecond.
     */
    public long getLastMillisecond(Calendar calendar) {

        int year = this.hour.getDay().getYear();
        int month = this.hour.getDay().getMonth() - 1;
        int day = this.hour.getDay().getDayOfMonth();

        calendar.clear();
        calendar.set(year, month, day, this.hour.getHour(), this.minute, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        //return calendar.getTimeInMillis();  // this won't work for JDK 1.3
        return calendar.getTime().getTime();

    }

    /**
     * Tests the equality of this object against an arbitrary Object.
     * <P>
     * This method will return true ONLY if the object is a Minute object
     * representing the same minute as this instance.
     *
     * @param obj  the object to compare (<code>null</code> permitted).
     *
     * @return <code>true</code> if the minute and hour value of this and the
     *      object are the same.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Minute)) {
            return false;
        }
        Minute that = (Minute) obj;
        if (this.minute != that.minute) {
            return false;
        }
        if (!this.hour.equals(that.hour)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a hash code for this object instance.  The approach described 
     * by Joshua Bloch in "Effective Java" has been used here:
     * <p>
     * <code>http://developer.java.sun.com/developer/Books/effectivejava
     * /Chapter3.pdf</code>
     * 
     * @return A hash code.
     */
    public int hashCode() {
        int result = 17;
        result = 37 * result + this.minute;
        result = 37 * result + this.hour.hashCode();
        return result;
    }

    /**
     * Returns an integer indicating the order of this Minute object relative
     * to the specified object:
     *
     * negative == before, zero == same, positive == after.
     *
     * @param o1  object to compare.
     *
     * @return negative == before, zero == same, positive == after.
     */
    public int compareTo(Object o1) {

        int result;

        // CASE 1 : Comparing to another Minute object
        // -------------------------------------------
        if (o1 instanceof Minute) {
            Minute m = (Minute) o1;
            result = getHour().compareTo(m.getHour());
            if (result == 0) {
                result = this.minute - m.getMinute();
            }
        }

        // CASE 2 : Comparing to another TimePeriod object
        // -----------------------------------------------
        else if (o1 instanceof RegularTimePeriod) {
            // more difficult case - evaluate later...
            result = 0;
        }

        // CASE 3 : Comparing to a non-TimePeriod object
        // ---------------------------------------------
        else {
            // consider time periods to be ordered after general objects
            result = 1;
        }

        return result;

    }

    /**
     * Creates a Minute instance by parsing a string.  The string is assumed to
     * be in the format "YYYY-MM-DD HH:MM", perhaps with leading or trailing
     * whitespace.
     *
     * @param s  the minute string to parse.
     *
     * @return <code>null</code>, if the string is not parseable, the minute
     *      otherwise.
     */
    public static Minute parseMinute(String s) {

        Minute result = null;
        s = s.trim();

        String daystr = s.substring(0, Math.min(10, s.length()));
        Day day = Day.parseDay(daystr);
        if (day != null) {
            String hmstr = s.substring(
                Math.min(daystr.length() + 1, s.length()), s.length()
            );
            hmstr = hmstr.trim();

            String hourstr = hmstr.substring(0, Math.min(2, hmstr.length()));
            int hour = Integer.parseInt(hourstr);

            if ((hour >= 0) && (hour <= 23)) {
                String minstr = hmstr.substring(
                    Math.min(hourstr.length() + 1, hmstr.length()), 
                    hmstr.length()
                );
                int minute = Integer.parseInt(minstr);
                if ((minute >= 0) && (minute <= 59)) {
                    result = new Minute(minute, new Hour(hour, day));
                }
            }
        }

        return result;

    }

}

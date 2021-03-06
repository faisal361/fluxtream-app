package com.fluxtream.connectors.bodymedia;

import com.fluxtream.utils.TimespanSegment;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import java.util.Iterator;
import java.util.TreeSet;
import java.util.TimeZone;


/**
 * <p>
 * <code>TimezoneMap</code> does something...
 * </p>
 *
 * @author Anne Wright (arwright@cmu.edu)
 */
public class TimezoneMap {
  public TreeSet<TimespanSegment<DateTimeZone>> spans = new TreeSet<TimespanSegment<DateTimeZone>>();

  public TimezoneMap() {
  }
  
    public boolean add(final long start, final long end, org.joda.time.DateTimeZone tz) {
	    TimespanSegment<DateTimeZone> newSpan = new TimespanSegment<DateTimeZone>(start,end,tz);

        return(spans.add(newSpan));
    }

    public TimespanSegment<DateTimeZone> queryPoint(long ts) {
        // Create a segment for retrieving the segment in spans which
        // starts at a time <= ts.  The returned segment will be one in
        // the map or null if ts < the start time of the first segment.
        // In that case, return the first item in the span instead.
        TimespanSegment<DateTimeZone> querySeg = new TimespanSegment(ts, ts);
        TimespanSegment<DateTimeZone> retSeg = spans.lower(querySeg);

        if(retSeg==null) {
            // This time is earlier than the earliest segment, return the first
            return spans.first();
        }
        return retSeg;
    }

    public DateTime getStartOfDate(LocalDate date)
    {
                // Get the milisecond time for the start of that date in UTC
        long utcStartMillis = date.toDateTimeAtStartOfDay(DateTimeZone.UTC).getMillis();
        // Lookup the timezone for that time - 12 and +12 hours since timezones range from
        // UTC-12h to UTC+12h so the real start time will be within that range
        long minStartMillis = utcStartMillis - DateTimeConstants.MILLIS_PER_DAY/2;
        long maxStartMillis = utcStartMillis + DateTimeConstants.MILLIS_PER_DAY/2;

        TimespanSegment<DateTimeZone> minTimespan = this.queryPoint(minStartMillis);
        TimespanSegment<DateTimeZone> maxTimespan = this.queryPoint(maxStartMillis);
        DateTimeZone realTz = null;
        DateTime realDateStart = null;

        // Check if they agree
        if(minTimespan==maxTimespan) {
            // Ok, they agree so we're good, just use the consensus timezone
            realTz = minTimespan.getValue();
            realDateStart = date.toDateTimeAtStartOfDay(realTz);
        }
        else {
            // The start and end timespans are different, compute the start time in each and see which if
            // either intersect
            DateTime minTzStartDT = date.toDateTimeAtStartOfDay(minTimespan.getValue());
            DateTime maxTzStartDT = date.toDateTimeAtStartOfDay(maxTimespan.getValue());
            // Does the earlier one fall within the timespan for the minTimezone
            long minTzStartMillis = minTzStartDT.getMillis();
            long maxTzStartMillis = maxTzStartDT.getMillis();
            if(minTimespan.isTimeInSpan(minTzStartMillis)) {
                // First one works, keep it
                realTz=minTimespan.getValue();
                realDateStart = minTzStartDT;
            }
            else if(maxTimespan.isTimeInSpan(maxStartMillis)) {
                // Last one works, keep it
                realTz=maxTimespan.getValue();
                realDateStart = maxTzStartDT;
            }
            else {
                // Something weird is going on here, complain and return GMT
                System.out.println("Cant figure out start of date "+date.toString()+", "+minTimespan + " does not contain " + minTzStartDT + " and "+ maxTimespan + " does not contain " + maxTzStartDT);
                return(date.toDateTimeAtStartOfDay(DateTimeZone.UTC));
             }
        }

        //System.out.println("Start of date "+date.toString()+", in "+realTz + ": " + realDateStart);
        return(realDateStart);
    }

    public static void main(final String[] args) {
        // Create test table
        TimezoneMap tzMap = new TimezoneMap();

        // Create sample tzMap based on Anne's BodyMedia map:
        // [ { "endDate" : "20110822T220048-0400", "startDate" : "20101221T000000-0500", "value" : "US/Eastern"},
        //   { "endDate" : "20110921T192919-0700", "startDate" : "20110822T190048-0700", "value" : "US/Pacific"},
        //   { "startDate" : "20110921T222919-0400","value" : "US/Eastern"
        //   } ]
        // Then extended to have a segment in Central time starting Wed, 22 May 2013 13:45:56 GMT and ending
        // Sat, 01 Jun 2013 00:00:00 GMT
        tzMap.add(1292907600000L,1314064848000L, DateTimeZone.forID("America/New_York"));
        tzMap.add(1314064848000L, 1316658559000L, DateTimeZone.forID("America/Los_Angeles"));
        tzMap.add(1316658559000L, 1369230356963L, DateTimeZone.forID("America/New_York"));
        tzMap.add(1369230356963L, 1370044800000L, DateTimeZone.forID("US/Central"));

        // This should be Eastern
        LocalDate d1 = new LocalDate(2011, 6, 15);
        tzMap.getStartOfDate(d1);

        // This should be Eastern
        LocalDate d2 = new LocalDate(2011, 8, 22);
        tzMap.getStartOfDate(d2);

        // This should be Pacific
        LocalDate d3 = new LocalDate(2011, 8, 23);
        tzMap.getStartOfDate(d3);

        // This should be Eastern
        LocalDate d3b = new LocalDate(2013, 5, 22);
         tzMap.getStartOfDate(d3b);

        // This should be Central
        LocalDate d3c = new LocalDate(2013, 5, 23);
        tzMap.getStartOfDate(d3c);

        // Get from before the start of the map.  This should default to using the first item in the map and return
        // in America/New_York
        LocalDate d4 = new LocalDate(2010,12,21);
        tzMap.getStartOfDate(d4);

        LocalDate d5 = new LocalDate(2010,1,1);
        tzMap.getStartOfDate(d5);

        // Get from past the end of the map.  This should be Central
        LocalDate d6 = new LocalDate(2013, 6, 2);
        tzMap.getStartOfDate(d6);

        LocalDate d7 = new LocalDate(2013, 8, 2);
        tzMap.getStartOfDate(d7);
    }
}
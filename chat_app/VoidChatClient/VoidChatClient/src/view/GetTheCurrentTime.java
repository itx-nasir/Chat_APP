package VoiceCall;

import java.util.Calendar;
import java.util.Date;

class GetTheCurrentTime {
	public Date getTime() {
		// This Class return the time of delivered picket on Second/ Day/ Month and year
		// one way
		long currentTimeInMillis = System.currentTimeMillis();
		Date today = new Date(currentTimeInMillis);
		// System.out.println( today );
		// another way
		Calendar cal = null;
		today = cal.getTime();
		return today;
		// System.out.println( today );
	}
}
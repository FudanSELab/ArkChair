package fudan.se.lab2.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    private DateUtil(){
        // no codes here
    }
    /**
     * transfer 2020-03-20T16:00:00.000Z to the format of 2019-03-21 00:00:00 and return it in Timestamp
     * @param dateTime time
     * @return newTime
     */
    public static Timestamp transferDateFormat(String dateTime) {
        Logger logger = LoggerFactory.getLogger(DateUtil.class);
        dateTime = dateTime.replace("Z", " UTC");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
        SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp newTime = null;
        try {
            Date time = format.parse(dateTime);
            String result = defaultFormat.format(time);
            newTime = Timestamp.valueOf(result);
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
        return newTime;
    }
}

package edu.cricket.api.cricketscores.utils;

import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DateUtils {

    public static Date getDateFromString(String dateStr){
        if(StringUtils.isNotBlank(dateStr)){
            String sDate = dateStr.split("T")[0];

            try {
                return  new SimpleDateFormat("yyyy-MM-dd").parse(sDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;

    }
}
package edu.cricket.api.cricketscores.utils;

import org.apache.commons.lang.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtils {
    public static Date getDateFromDateTimeString(String dateTimeString){
        Date date = null;
        if(StringUtils.isNotBlank(dateTimeString)){
            String dateString = dateTimeString.split("T")[0];
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            try {
                return dateFormat.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;

    }

    public static  int getIntegerFromString(String intValueStr){
        try {
            return  Integer.parseInt(intValueStr);
        }catch (Exception e){
            return 0;
        }
    }

    public static  float getFloatFromString(String floatValueStr){
        try {
            return  Float.parseFloat(floatValueStr);
        }catch (Exception e){
            return 0.0f;
        }
    }
}

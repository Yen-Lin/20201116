package com.koddev.authenticatorapp.Chat.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Define
{

    public static String ChatContent = "ChatContent";
    public static String Sentence = "Sentence";
    public static String IsMine = "IsMine";
    public static String UserUUID = "UserUUID";
    public static String UserName = "UserName";
    public static String Chats = "Chats";
    public static String isSeen = "isSeen";
    public static String message = "message";
    public static String receiver = "receiver";
    public static String sender = "sender";
    public static String timestamp = "timestamp";


    public static long getCurrentUnixTime()
    {
        return System.currentTimeMillis();
    }


    public static String convertUnixTimeToString(long unixTime, String pattern)
    {
        return convertUnixTimeToString(unixTime, pattern, TimeZone.getTimeZone("UTC"));
    }

    public static String convertUnixTimeToString(long unixTime, String pattern, TimeZone targetTimeZone)
    {
        return convertDateToString(new Date(unixTime), pattern, targetTimeZone);
    }

    public static String convertDateToString(Date date, String pattern, TimeZone targetTimeZone)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.US);
        dateFormat.setTimeZone(targetTimeZone);
        return dateFormat.format(date);
    }

    public static String changeDateFormat(String sourceString, String sourcePattern, TimeZone sourceTimeZone, String targetPattern, TimeZone targetTimeZone) throws ParseException
    {
        SimpleDateFormat sourceDateFormat = new SimpleDateFormat(sourcePattern, Locale.US);
        sourceDateFormat.setTimeZone(sourceTimeZone);
        SimpleDateFormat targetDateFormat = new SimpleDateFormat(targetPattern, Locale.US);
        targetDateFormat.setTimeZone(targetTimeZone);
        Date date = sourceDateFormat.parse(sourceString);
        return targetDateFormat.format(date);
    }
}

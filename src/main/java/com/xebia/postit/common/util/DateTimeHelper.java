package com.xebia.postit.common.util;

import static com.xebia.postit.infra.config.ApplicationDefaults.ZONE_ID_UTC;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xebia.postit.infra.config.ApplicationProperties;

import lombok.NonNull;

@Component
public class DateTimeHelper {

    public static final LocalTime LOCAL_TIME_MIN = LocalTime.MIN.truncatedTo(ChronoUnit.MINUTES);

    public static final LocalTime LOCAL_TIME_MAX = LocalTime.MAX.truncatedTo(ChronoUnit.MINUTES);

    @NonNull
    private static ApplicationProperties applicationProperties;

    @NonNull
    public static DateTimeFormatter DEFAULT_DATE_FORMATTER;

    @NonNull
    public static DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER;

    @NonNull
    public static DateTimeFormatter DEFAULT_ZONED_DATE_TIME_FORMATTER;

    @NonNull
    public static DateTimeFormatter DEFAULT_TIME_FORMATTER;

    @NonNull
    public static ZoneId DISPLAY_ZONE_ID;

    @Autowired
    public void setApplicationProperties(final ApplicationProperties applicationProperties) {
        DateTimeHelper.applicationProperties = applicationProperties;
        DateTimeHelper.DEFAULT_DATE_FORMATTER = DateTimeFormatter
                .ofPattern(applicationProperties.getDefaultDateFormat());
        DateTimeHelper.DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter
                .ofPattern(applicationProperties.getDefaultDatetimeFormat());
        DateTimeHelper.DEFAULT_ZONED_DATE_TIME_FORMATTER = DateTimeFormatter
                .ofPattern(applicationProperties.getDefaultZonedDatetimeFormat());
        DateTimeHelper.DEFAULT_TIME_FORMATTER = DateTimeFormatter
                .ofPattern(applicationProperties.getDefaultTimeFormat());
        DateTimeHelper.DISPLAY_ZONE_ID = applicationProperties.getDisplayZoneId();
    }

    public static String formatDate(final LocalDate date) {
        return date.format(DEFAULT_DATE_FORMATTER);
    }

    public static String formatDate(final LocalDate date, final String pattern) {
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String formatDate(final LocalDate date, final DateTimeFormatter formatter) {
        return date.format(formatter);
    }

    public static String formatDateTime(final LocalDateTime datetime) {
        return datetime.format(DEFAULT_DATE_TIME_FORMATTER);
    }

    public static String formatDateTime(final LocalDateTime datetime, final String pattern) {
        return datetime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String formatDateTime(final LocalDateTime datetime, final DateTimeFormatter formatter) {
        return datetime.format(formatter);
    }

    public static String formatZonedDateTime(final ZonedDateTime zonedDateTime) {
        return zonedDateTime.format(DEFAULT_ZONED_DATE_TIME_FORMATTER);
    }

    public static String formatZonedDateTime(final ZonedDateTime zonedDateTime, final String pattern) {
        return zonedDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String formatZonedDateTime(final ZonedDateTime zonedDateTime, final DateTimeFormatter formatter) {
        return zonedDateTime.format(formatter);
    }

    public static ZonedDateTime nowZonedDateTimeUTC() {
        return ZonedDateTime.now(ZONE_ID_UTC);
    }

    public static ZonedDateTime nowZonedDateTime(final ZoneId zoneId) {
        return ZonedDateTime.now(zoneId);
    }

    public static ZonedDateTime convertZonedDateTime(final ZonedDateTime sourceZoneDatetime,
            final ZoneId targetZoneId) {
        return sourceZoneDatetime.getZone() == targetZoneId ? sourceZoneDatetime
                : sourceZoneDatetime.withZoneSameInstant(targetZoneId);
    }

    public static String convertAndFormatZonedDateTime(final ZonedDateTime sourceZoneDatetime,
            final ZoneId targetZoneId) {
        return sourceZoneDatetime.getZone() == targetZoneId ? formatZonedDateTime(sourceZoneDatetime)
                : formatZonedDateTime(sourceZoneDatetime.withZoneSameInstant(targetZoneId));
    }

    public static String convertAndFormatZonedDateTime(final ZonedDateTime sourceZoneDatetime) {
        return sourceZoneDatetime.getZone() == DISPLAY_ZONE_ID ? formatZonedDateTime(sourceZoneDatetime)
                : formatZonedDateTime(sourceZoneDatetime.withZoneSameInstant(DISPLAY_ZONE_ID));
    }

    // Truncate to minutes also
    public static LocalTime covertLocalTimeHHmm(final LocalTime time, final ZoneId sourceZoneId,
            final ZoneId targetZoneId) {
        if (sourceZoneId == targetZoneId) {
            return time;
        }
        ZonedDateTime sourceTime = ZonedDateTime.of(LocalDate.now(), time, sourceZoneId);
        ZonedDateTime converted = sourceTime.withZoneSameInstant(targetZoneId);
        return LocalTime.of(converted.getHour(), converted.getMinute());
    }

    public static String formatTime(final LocalTime time, final DateTimeFormatter formatter) {
        return time.format(formatter);
    }

    public static String formatTime(final LocalTime time) {
        return time.format(DEFAULT_TIME_FORMATTER);
    }

    public static boolean timeLe(final LocalTime candidate, final LocalTime reference) {
        return !candidate.isAfter(reference);
    }

    public static boolean timeGe(final LocalTime candidate, final LocalTime reference) {
        return !candidate.isBefore(reference);
    }

    public static boolean timeBetween(final LocalTime candidate, final LocalTime from, final LocalTime till) {
        return timeGe(candidate, from) && timeLe(candidate, till);
    }

    public static LocalTime nowLocalTimeHHmm() {
        return LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
    }
}

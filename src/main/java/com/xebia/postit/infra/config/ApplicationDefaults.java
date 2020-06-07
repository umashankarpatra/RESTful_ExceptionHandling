package com.xebia.postit.infra.config;

import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

public interface ApplicationDefaults {

    public int DEFAULT_PAGE_SIZE = 12;

    public String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    public String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd hh:mm a";

    public String DEFAULT_ZONED_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public String DEFAULT_TIME_FORMAT = "HH:mm";

    public ZoneId ZONE_ID_UTC = ZoneId.of("UTC");

    // Indian Standard Time Zone
    public ZoneId ZONE_ID_IST = ZoneId.of("Asia/Kolkata");

    public TimeZone TIME_ZONE_UTC = TimeZone.getTimeZone(ZONE_ID_UTC);

    public TimeZone TIME_ZONE_IST = TimeZone.getTimeZone(ZONE_ID_IST);

    public Locale DEFAULT_LOCALE = Locale.getDefault();

    public ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();

    public TimeZone DEFAULT_TIME_ZONE = TIME_ZONE_UTC;

    public boolean PROBLEM_STACKTRACE = true;
    
    interface Swagger {

        String title = "Application API";

        String description = "API documentation";

        String version = "0.0.1";

        String termsOfServiceUrl = null;

        String contactName = null;

        String contactUrl = null;

        String contactEmail = null;

        String license = null;

        String licenseUrl = null;

        String defaultIncludePattern = "/api/.*";

        String host = null;

        String[] protocols = {};

        boolean useDefaultResponseMessages = true;
    }
}

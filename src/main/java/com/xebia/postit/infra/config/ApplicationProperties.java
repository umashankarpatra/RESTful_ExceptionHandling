package com.xebia.postit.infra.config;

import java.time.ZoneId;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Properties specific to pricing-service.
 * <p>
 * Properties are configured in the application.yml file.
 */
@Setter
@Getter
@NoArgsConstructor
@ToString
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    public static final String CONTROLLER_PACKAGE = "com.xebia.postit.controller";
    
    public static final String REPOSITORY_PACKAGE = "com.xebia.postit.repository";

    public static final String SLASH = "/";

    public static final String DOCS_DIR = SLASH + "docs";

    public static final String API = SLASH + "api";

    public static final String VERSION_v1 = "v1";

    public static final String VERSION_v2 = "v2";

    public static final String CURRENT_VERSION = VERSION_v1;

    public static final String POST_IT_API = API + SLASH + CURRENT_VERSION;
    
    private String defaultDateFormat = ApplicationDefaults.DEFAULT_DATE_FORMAT;

    private String defaultDatetimeFormat = ApplicationDefaults.DEFAULT_DATE_TIME_FORMAT;

    private String defaultZonedDatetimeFormat = ApplicationDefaults.DEFAULT_ZONED_DATE_TIME_FORMAT;

    private String defaultTimeFormat = ApplicationDefaults.DEFAULT_TIME_FORMAT;

    private ZoneId displayZoneId = ApplicationDefaults.ZONE_ID_UTC;

    private boolean problemStacktrace = ApplicationDefaults.PROBLEM_STACKTRACE;

    private final Swagger swagger = new Swagger();

    private final CorsConfiguration cors = new CorsConfiguration();

    @Getter
    @Setter
    public static class Swagger {

        private String title = ApplicationDefaults.Swagger.title;

        private String description = ApplicationDefaults.Swagger.description;

        private String version = ApplicationDefaults.Swagger.version;

        private String termsOfServiceUrl = ApplicationDefaults.Swagger.termsOfServiceUrl;

        private String contactName = ApplicationDefaults.Swagger.contactName;

        private String contactUrl = ApplicationDefaults.Swagger.contactUrl;

        private String contactEmail = ApplicationDefaults.Swagger.contactEmail;

        private String license = ApplicationDefaults.Swagger.license;

        private String licenseUrl = ApplicationDefaults.Swagger.licenseUrl;

        private String defaultIncludePattern = ApplicationDefaults.Swagger.defaultIncludePattern;

        private String host = ApplicationDefaults.Swagger.host;

        private String[] protocols = ApplicationDefaults.Swagger.protocols;

        private boolean useDefaultResponseMessages = ApplicationDefaults.Swagger.useDefaultResponseMessages;
    }
}

package com.xebia.postit;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.filter.ForwardedHeaderFilter;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.xebia.postit.infra.config.ApplicationDefaults;
import com.xebia.postit.infra.config.ApplicationProperties;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableConfigurationProperties({ ApplicationProperties.class })
@Slf4j
public class PostItApplication {

    private final Environment env;

    public PostItApplication(final Environment env) {
        this.env = env;
    }

	public static void main(String[] args) {
        SpringApplication app = new SpringApplication(PostItApplication.class);
        SpringProfiles.addDefaultProfile(app);
        TimeZone.setDefault(ApplicationDefaults.DEFAULT_TIME_ZONE);
        Environment env = app.run(args).getEnvironment();
        logApplicationStartup(env);
	}


    private static void logApplicationStartup(final Environment env) {
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        String serverPort = env.getProperty("server.port") == null ? "" + 8080 : env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path");
        if (StringUtils.isBlank(contextPath)) {
            contextPath = "/";
        }
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }
        String activeProfile = env.getProperty("spring.profiles.active");
        String features = Arrays.stream(env.getActiveProfiles()).filter(feature -> !feature.equals(activeProfile))
                .collect(Collectors.joining(", "));
        log.info(
                "\n--------------------------------------------------------------------\n\t"
                        + "Application '{}' is running! Access URLs:\n\t" + "Local: \t\t{}://localhost:{}{}\n\t"
                        + "External: \t{}://{}:{}{}\n\t" + "Environment: \t{}\n\t"
                        + "Features: \t{}\n--------------------------------------------------------------------",
                env.getProperty("spring.application.name"), protocol, serverPort, contextPath, protocol, hostAddress,
                serverPort, contextPath, activeProfile, features);
    }
    

    /**
     * Initializes sample.
     * <p>
     * Spring profiles can be configured with a program argument --spring.profiles.active=your-active-profile
     * <p>
     */
    @PostConstruct
    public void initApplication() {
        Set<String> activeProfiles = Sets.newHashSet(this.env.getActiveProfiles());
        Set<String> exclusiveProfiles = SpringProfiles.exclusiveProfiles();
        SetView<String> intersection = Sets.intersection(exclusiveProfiles, activeProfiles);

        if (intersection.isEmpty()) {
            log.error("You have misconfigured your application! It should run "
                    + "with exactly one of active profiles: " + exclusiveProfiles + ", depending on environment.");
        }
        else if (intersection.size() > 1) {
            log.error("You have misconfigured your application! It should not run with active profiles " + intersection
                    + " togeather. Exactly one of profiles: " + exclusiveProfiles
                    + " must be active, depending on environment.");
        }
    }

	@Bean
	ForwardedHeaderFilter forwardedHeaderFilter() {
	    return new ForwardedHeaderFilter();
	}
}

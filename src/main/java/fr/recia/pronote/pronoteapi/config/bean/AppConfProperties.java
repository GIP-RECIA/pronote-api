package fr.recia.pronote.pronoteapi.config.bean;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "app.conf")
@Data
@Validated
@Slf4j
public class AppConfProperties {

    Map<String, String> uaiReplacementMapRequest = new HashMap<>();

    Map<String, String> uaiReplacementMapProxyTicketFor = new HashMap<>();

    @PostConstruct
    void init() {
        log.debug("AppConfProperties {}", this);
    }
}

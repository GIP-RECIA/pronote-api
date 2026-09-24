/*
 * Copyright © 2026 GIP-RECIA (https://www.recia.fr/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.recia.pronote.pronoteapi.config;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.json.JsonMapper;
import fr.recia.pronote.pronoteapi.config.bean.AppConfProperties;
import fr.recia.pronote.pronoteapi.config.bean.CasProperties;
import fr.recia.pronote.pronoteapi.config.custom.impl.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.client.proxy.ProxyGrantingTicketStorage;
import org.apereo.cas.client.session.SingleSignOutFilter;
import org.apereo.cas.client.validation.Assertion;
import org.apereo.cas.client.validation.Cas20ProxyTicketValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {

    private final AppConfProperties appConfProperties;
    private final CasProperties casProperties;
    private final CorsConfigurationSource corsConfigurationSource;
    private final CustomSessionMappingStorage ticketSessionMappingStorage;
    private final CasSuccessHandler casSuccessHandler;
    private final SessionDebugFilter sessionDebugFilter;

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            Filter singleSignOutFilter,
            CasAuthenticationFilter casAuthenticationFilter,
            CustomAuthenticationProvider customAuthProvider,
            CasAuthenticationEntryPoint casAuthenticationEntryPoint
    ) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(singleSignOutFilter, CasAuthenticationFilter.class)
                .addFilterAfter(sessionDebugFilter, CasAuthenticationFilter.class)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authenticationProvider(customAuthProvider)
                .addFilterBefore(casAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e -> e.authenticationEntryPoint(casAuthenticationEntryPoint).accessDeniedHandler((req, res, ex) -> {
                    res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    res.getWriter().write(new JsonMapper().writeValueAsString(Map.of("message", ex.getMessage())));
                }))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/health-check").permitAll()
                        .requestMatchers("/api/config").permitAll()
                        .requestMatchers("/api/summary").authenticated()
                        .requestMatchers("/api/page").authenticated()
                        .requestMatchers(casProperties.getCasTicketCallback()).permitAll()
                        .requestMatchers(casProperties.getCasProxyReceptorUrl()).permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().denyAll()
                );
        return http.build();
    }

    /**
     * Filtre CAS pour le Single Logout (SLO).
     */
    @Bean
    public Filter singleSignOutFilter() {
        SingleSignOutFilter.setArtifactParameterName("ticket");
        SingleSignOutFilter.setLogoutParameterName("logoutRequest");
        return new SingleSignOutHandlerFilter(ticketSessionMappingStorage);
    }

    @Bean
    public CasAuthenticationEntryPoint casAuthenticationEntryPoint(ServiceProperties serviceProperties) {
        CasAuthenticationEntryPoint casAuthenticationEntryPoint = new CustomCasAuthenticationEntryPoint(casProperties);
        casAuthenticationEntryPoint.setLoginUrl(casProperties.getCasServerLoginUrl());
        casAuthenticationEntryPoint.setServiceProperties(serviceProperties);
        return casAuthenticationEntryPoint;
    }


    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setService(casProperties.getCasServiceId());
        serviceProperties.setSendRenew(false);
        return serviceProperties;
    }

    @Bean
    public ProxyGrantingTicketStorage pgtStorage() {
        return new ProxyGrantingTicketRedisImpl();
    }

    @Bean
    public AuthenticationUserDetailsService<CasAssertionAuthenticationToken> customUserDetailsService() {
        return (CasAssertionAuthenticationToken token) -> {
            Assertion assertion = token.getAssertion();
            Map<String, Object> attributes = assertion.getPrincipal().getAttributes();
            String username = assertion.getPrincipal().getName();
            return new UserCustomImplementation(username, "", List.of(new SimpleGrantedAuthority("ROLE_USER")), attributes);
        };
    }

    @Bean
    public CustomAuthenticationProvider customAuthProvider(ServiceProperties serviceProperties) {
        CustomAuthenticationProvider provider = new CustomAuthenticationProvider(casProperties);
        provider.setServiceProperties(serviceProperties);

        Cas20ProxyTicketValidator validator = new CustomCas20ProxyTicketValidator(casProperties.getCasServerUrl(), casProperties);
        validator.setProxyCallbackUrl(casProperties.getCasProxyTicketCallback());
        validator.setProxyGrantingTicketStorage(pgtStorage());

        provider.setTicketValidator(validator);
        provider.setAuthenticationUserDetailsService(customUserDetailsService());
        provider.setKey(casProperties.getCasProviderKey());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(CustomAuthenticationProvider customAuthProvider) {
        return new ProviderManager(customAuthProvider);
    }

    @Bean
    public CasAuthenticationFilter casAuthenticationFilter(AuthenticationManager authenticationManager) {
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager);
        filter.setFilterProcessesUrl(casProperties.getCasTicketCallback());
        filter.setProxyGrantingTicketStorage(pgtStorage());
        filter.setProxyReceptorUrl(casProperties.getCasProxyReceptorUrl());
        filter.setAuthenticationSuccessHandler(casSuccessHandler);
        return filter;
    }

}
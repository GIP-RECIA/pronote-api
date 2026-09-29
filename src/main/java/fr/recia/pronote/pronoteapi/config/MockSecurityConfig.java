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

import fr.recia.pronote.pronoteapi.config.bean.MockProperties;
import fr.recia.pronote.pronoteapi.config.bean.ProfilsProperties;
import fr.recia.pronote.pronoteapi.config.custom.impl.MockAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@Profile("mock-no-cas")
@ConditionalOnProperty(prefix = "app.mock", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class MockSecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;
    private final MockProperties mockProperties;
    private final ProfilsProperties profilsProperties;

    @Bean
    public SecurityFilterChain mockFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(new MockAuthenticationFilter(mockProperties, profilsProperties), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());
        return http.build();
    }
}
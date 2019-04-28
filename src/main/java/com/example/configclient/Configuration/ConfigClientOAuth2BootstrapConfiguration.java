/*
 * Copyright 2016 the original author or authors.
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
package com.example.configclient.Configuration;

import com.example.configclient.RequestResponseLoggingInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Collections;

@Configuration
@EnableConfigurationProperties(ConfigClientOAuth2ResourceDetails.class)
@ConditionalOnClass({ConfigServicePropertySourceLocator.class, OAuth2RestTemplate.class})
@ConditionalOnProperty(value = ConfigClientOAuth2ResourceDetails.PREFIX + ".oauth2.client-id")
public class ConfigClientOAuth2BootstrapConfiguration {

    @Bean
    @Primary
    @ConditionalOnMissingBean(ConfigClientOAuth2ResourceDetails.class)
    public ConfigClientOAuth2ResourceDetails configClientOAuth2ResourceDetails() {
        return new ConfigClientOAuth2ResourceDetails();
    }

    @Bean
    @Primary
    @ConditionalOnProperty(value = "spring.cloud.config.enabled", matchIfMissing = true)
    protected ConfigClientOAuth2Configurer configClientOAuth2Configurator(ConfigServicePropertySourceLocator locator,
                                                                          ConfigClientOAuth2ResourceDetails configClientOAuth2ResourceDetails) {
        return new ConfigClientOAuth2Configurer(locator, configClientOAuth2ResourceDetails);
    }

    protected static class ConfigClientOAuth2Configurer {

        private final ConfigServicePropertySourceLocator locator;

        private final ConfigClientOAuth2ResourceDetails configClientOAuth2ResourceDetails;

        public ConfigClientOAuth2Configurer(ConfigServicePropertySourceLocator locator,
                                            ConfigClientOAuth2ResourceDetails configClientOAuth2ResourceDetails) {
            this.locator = locator;
            this.configClientOAuth2ResourceDetails = configClientOAuth2ResourceDetails;
        }

        @PostConstruct
        public void init() {
            OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(this.configClientOAuth2ResourceDetails.getOauth2());
            restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
            restTemplate.setInterceptors( Collections.singletonList(new RequestResponseLoggingInterceptor()) );
            this.locator.setRestTemplate(restTemplate);
        }

    }

}

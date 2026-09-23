package com.vhre.base.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(JacksonConfig.class)
@ComponentScan(basePackages = "com.vhre.base")
public class BaseProjectAutoConfiguration {
}
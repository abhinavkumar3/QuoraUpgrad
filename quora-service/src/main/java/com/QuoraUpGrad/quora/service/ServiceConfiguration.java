package com.QuoraUpGrad.quora.service;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.QuoraUpGrad.quora.service")
@EntityScan("com.QuoraUpGrad.quora.service.entity")
public class ServiceConfiguration {
}
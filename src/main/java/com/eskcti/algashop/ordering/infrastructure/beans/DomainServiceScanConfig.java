package com.eskcti.algashop.ordering.infrastructure.beans;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import com.eskcti.algashop.ordering.domain.model.DomainService;

@Configuration
@ComponentScan(basePackages = "com.eskcti.algashop.ordering.domain.model", includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = DomainService.class))
public class DomainServiceScanConfig {

}

package com.eskcti.algashop.ordering.infrastructure.utility.modelmapper;

import java.time.LocalDate;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NamingConventions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.eskcti.algashop.ordering.application.customer.query.CustomerOutput;
import com.eskcti.algashop.ordering.application.utility.Mapper;
import com.eskcti.algashop.ordering.domain.model.commons.FullName;
import com.eskcti.algashop.ordering.domain.model.customer.BirthDate;
import com.eskcti.algashop.ordering.domain.model.customer.Customer;

@Configuration
public class ModelMapperConfig {

  private static final Converter<FullName, String> fullNameToFirstNameConverter = mappingContext -> {
    FullName fullName = mappingContext.getSource();
    if (fullName == null) {
      return null;
    }
    return fullName.firstName();
  };

  private static final Converter<FullName, String> fullNameToLastNameConverter = mappingContext -> {
    FullName fullName = mappingContext.getSource();
    if (fullName == null) {
      return null;
    }
    return fullName.lastName();
  };

  private static final Converter<BirthDate, LocalDate> birthDateToLocalDateConverter = mappingContext -> {
    BirthDate birthDate = mappingContext.getSource();
    if (birthDate == null) {
      return null;
    }
    return birthDate.value();
  };

  @Bean
  public Mapper mapper() {
    ModelMapper modelMapper = new ModelMapper();
    configuration(modelMapper);
    return modelMapper::map;
  }

  private void configuration(ModelMapper modelMapper) {
    modelMapper.getConfiguration()
        .setSourceNamingConvention(NamingConventions.NONE)
        .setDestinationNamingConvention(NamingConventions.NONE)
        .setMatchingStrategy(MatchingStrategies.STRICT);

    modelMapper.createTypeMap(Customer.class, CustomerOutput.class)
        .addMappings(mapping -> mapping.using(fullNameToFirstNameConverter)
            .map(Customer::fullName, CustomerOutput::setFirstName))
        .addMappings(mapping -> mapping.using(fullNameToLastNameConverter)
            .map(Customer::fullName, CustomerOutput::setLastName))
        .addMappings(mapping -> mapping.using(birthDateToLocalDateConverter)
            .map(Customer::birthDate, CustomerOutput::setBirthDate));
  }

}

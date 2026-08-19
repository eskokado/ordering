package com.eskcti.algashop.ordering.core.application.utility;

public interface Mapper {
  <T> T convert(Object object, Class<T> destinationType);
}
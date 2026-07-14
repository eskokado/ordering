package com.eskcti.algashop.ordering.domain.valueobject;

public final class ValueObjectTestFixtures {

  private ValueObjectTestFixtures() {
  }

  public static FullName validFullName() {
    return new FullName("John", "Doe");
  }

  public static Document validDocument() {
    return new Document("255-08-0578");
  }

  public static Phone validPhone() {
    return new Phone("478-256-2604");
  }

  public static Address validAddress() {
    return Address.builder()
        .street("Bourbon Street")
        .number("1134")
        .neighborhood("North Ville")
        .city("York")
        .state("South California")
        .zipCode(new ZipCode("12345"))
        .complement("Apt. 114")
        .build();
  }

  public static Email validEmail() {
    return new Email("john.doe@example.com");
  }
}

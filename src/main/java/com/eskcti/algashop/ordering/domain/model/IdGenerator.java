package com.eskcti.algashop.ordering.domain.model;

import java.util.UUID;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochRandomGenerator;
import io.hypersistence.tsid.TSID;

public class IdGenerator {

  private static final TimeBasedEpochRandomGenerator timeBasedEpochRandomGenerator = Generators
      .timeBasedEpochRandomGenerator();

  private static final TSID.Factory tsidFactroy = TSID.Factory.INSTANCE;

  private IdGenerator() {
  }

  public static UUID generateTimeBasedUUID() {
    return timeBasedEpochRandomGenerator.generate();
  }

  /*
   * TSID_NODE
   * TSID_NODE_COUNT
   */
  public static TSID generateTSID() {
    return tsidFactroy.generate();
  }

  public static TSID generateTSID(String value) {
    return TSID.from(value);
  }

  public static TSID generateTSID(Long value) {
    return TSID.from(value);
  }

}
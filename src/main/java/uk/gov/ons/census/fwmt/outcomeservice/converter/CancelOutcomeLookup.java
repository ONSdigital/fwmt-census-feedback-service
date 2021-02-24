package uk.gov.ons.census.fwmt.outcomeservice.converter;

import java.util.HashMap;
import java.util.Map;

public class CancelOutcomeLookup {

  private final Map<String, String> cancelOutcomeCodeMap = new HashMap<>();

  public String getLookup(String outcomeCode) {

    return cancelOutcomeCodeMap.get(outcomeCode);
  }

  public void add(String productCode) {
    cancelOutcomeCodeMap.put(productCode, productCode);
  }

  public boolean containsKey(String productCode) {
    return cancelOutcomeCodeMap.containsKey(productCode);
  }

}

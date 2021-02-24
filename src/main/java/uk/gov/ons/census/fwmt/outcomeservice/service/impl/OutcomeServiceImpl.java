package uk.gov.ons.census.fwmt.outcomeservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;
import uk.gov.ons.census.fwmt.outcomeservice.converter.CancelOutcomeLookup;
import uk.gov.ons.census.fwmt.outcomeservice.converter.OutcomeServiceProcessor;
import uk.gov.ons.census.fwmt.outcomeservice.converter.OutcomeLookup;
import uk.gov.ons.census.fwmt.outcomeservice.data.GatewayCache;
import uk.gov.ons.census.fwmt.outcomeservice.dto.OutcomeSuperSetDto;
import uk.gov.ons.census.fwmt.outcomeservice.service.OutcomeService;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import javax.transaction.Transactional;

import static uk.gov.ons.census.fwmt.outcomeservice.converter.OutcomeServiceLogConfig.*;

@Slf4j
@Service
public class OutcomeServiceImpl implements OutcomeService {

  public static final String PROCESSING_HH_OUTCOME = "PROCESSING_HH_OUTCOME";

  public static final String PROCESSING_SPG_OUTCOME = "PROCESSING_SPG_OUTCOME";

  public static final String PROCESSING_CE_OUTCOME = "PROCESSING_CE_OUTCOME";

  public static final String PROCESSING_CCS_PL_OUTCOME = "PROCESSING_CCS_PL_OUTCOME";

  public static final String PROCESSING_CCS_INT_OUTCOME = "PROCESSING_CCS_INT_OUTCOME";

  public static final String PROCESSING_NC_OUTCOME = "PROCESSING_NC_OUTCOME";

  public static final String FAILED_TO_LOOKUP_OUTCOME_CODE = "FAILED_TO_LOOKUP_OUTCOME_CODE";

  @Autowired
  private Map<String, OutcomeServiceProcessor> outcomeServiceProcessors;

  @Autowired
  private OutcomeLookup outcomeLookup;

  @Autowired
  private CancelOutcomeLookup cancelOutcomeLookup;

  @Autowired
  private GatewayEventManager gatewayEventManager;

  @Autowired
  private GatewayCacheService gatewayCacheService;

  @Override
  @Transactional
  public void createSpgOutcomeEvent(OutcomeSuperSetDto outcome) throws GatewayException {
    String[] operationsList = outcomeLookup.getLookup(outcome.getOutcomeCode());
    if (operationsList == null) {
      gatewayEventManager.triggerErrorEvent(this.getClass(), (Exception) null, "No outcome code found",
          String.valueOf(outcome.getCaseId()), FAILED_TO_LOOKUP_OUTCOME_CODE,
          "Survey type", "SPG",
          "Outcome code", outcome.getOutcomeCode(),
          "Secondary Outcome", outcome.getSecondaryOutcomeDescription());
      return;
    }
    UUID caseIdHolder = null;
    for (String operation : operationsList) {
      gatewayEventManager.triggerEvent(String.valueOf(outcome.getCaseId()), PROCESSING_SPG_OUTCOME,
          "Survey type", "SPG",
          "Secondary Outcome", outcome.getSecondaryOutcomeDescription(),
          "Held case id", (caseIdHolder != null) ? String.valueOf(caseIdHolder) : "N/A",
          "Operation", operation,
          "Operation list", Arrays.toString(operationsList));
      caseIdHolder = outcomeServiceProcessors.get(operation).process(outcome, caseIdHolder, "SPG");
    }
    if (cancelOutcomeLookup.containsKey(outcome.getOutcomeCode()))
      triggerCancelLog(String.valueOf(caseIdHolder),String.valueOf(outcome.getCaseId()),outcome.getOutcomeCode());
  }

  @Override
  @Transactional
  public void createCeOutcomeEvent(OutcomeSuperSetDto outcome) throws GatewayException {
    String[] operationsList = outcomeLookup.getLookup(outcome.getOutcomeCode());
    if (operationsList == null) {
      gatewayEventManager.triggerErrorEvent(this.getClass(), (Exception) null, "No outcome code found",
          String.valueOf(outcome.getCaseId()), FAILED_TO_LOOKUP_OUTCOME_CODE,
          "Survey type", "CE",
          "Outcome code", outcome.getOutcomeCode(),
          "Secondary Outcome", outcome.getSecondaryOutcomeDescription());
      return;
    }
    UUID caseIdHolder = null;
    for (String operation : operationsList) {
      gatewayEventManager.triggerEvent(String.valueOf(outcome.getCaseId()), PROCESSING_CE_OUTCOME,
          "Survey type", "CE",
          "Secondary Outcome", outcome.getSecondaryOutcomeDescription(),
          "Held case id", (caseIdHolder != null) ? String.valueOf(caseIdHolder) : "N/A",
          "Operation", operation,
          "Operation list", Arrays.toString(operationsList));
      caseIdHolder = outcomeServiceProcessors.get(operation).process(outcome, caseIdHolder, "CE");
    }
    if (cancelOutcomeLookup.containsKey(outcome.getOutcomeCode()))
      triggerCancelLog(String.valueOf(caseIdHolder),String.valueOf(outcome.getCaseId()),outcome.getOutcomeCode());
  }

  @Override
  @Transactional
  public void createHhOutcomeEvent(OutcomeSuperSetDto outcome) throws GatewayException {
    String[] operationsList = outcomeLookup.getLookup(outcome.getOutcomeCode());
    if (operationsList == null) {
      gatewayEventManager.triggerErrorEvent(this.getClass(), (Exception) null, "No outcome code found",
          String.valueOf(outcome.getCaseId()), FAILED_TO_LOOKUP_OUTCOME_CODE,
          "Survey type", "HH",
          "Outcome code", outcome.getOutcomeCode(),
          "Secondary Outcome", outcome.getSecondaryOutcomeDescription());
      return;
    }
    UUID caseIdHolder = null;
    for (String operation : operationsList) {
      gatewayEventManager.triggerEvent(String.valueOf(outcome.getCaseId()), PROCESSING_HH_OUTCOME,
          "Survey type", "HH",
          "Secondary Outcome", outcome.getSecondaryOutcomeDescription(),
          "Held case id", (caseIdHolder != null) ? String.valueOf(caseIdHolder) : "N/A",
          "Operation", operation,
          "Operation list", Arrays.toString(operationsList));
      caseIdHolder = outcomeServiceProcessors.get(operation).process(outcome, caseIdHolder, "HH");
    }
    if (cancelOutcomeLookup.containsKey(outcome.getOutcomeCode()))
      triggerCancelLog(String.valueOf(caseIdHolder),String.valueOf(outcome.getCaseId()),outcome.getOutcomeCode());
  }

  @Override
  @Transactional
  public void createCcsPropertyListingOutcomeEvent(OutcomeSuperSetDto outcome) throws GatewayException {
    String[] operationsList = outcomeLookup.getLookup(outcome.getOutcomeCode());
    if (operationsList == null) {
      gatewayEventManager.triggerErrorEvent(this.getClass(), (Exception) null, "No outcome code found",
          String.valueOf(outcome.getCaseId()), FAILED_TO_LOOKUP_OUTCOME_CODE,
          "Survey type", "CCS PL",
          "Outcome code", outcome.getOutcomeCode(),
          "Secondary Outcome", outcome.getSecondaryOutcomeDescription());
      return;
    }
    UUID caseIdHolder = null;
    for (String operation : operationsList) {
      gatewayEventManager.triggerEvent(String.valueOf(outcome.getCaseId()), PROCESSING_CCS_PL_OUTCOME,
          "Survey type", "CCS PL",
          "Secondary Outcome", outcome.getSecondaryOutcomeDescription(),
          "Held case id", (caseIdHolder != null) ? String.valueOf(caseIdHolder) : "N/A",
          "Operation", operation,
          "Operation list", Arrays.toString(operationsList));
      caseIdHolder = outcomeServiceProcessors.get(operation).process(outcome, caseIdHolder, "CCS PL");
    }
    if (cancelOutcomeLookup.containsKey(outcome.getOutcomeCode()))
      triggerCancelLog(String.valueOf(caseIdHolder),String.valueOf(outcome.getCaseId()),outcome.getOutcomeCode());
  }

  @Override
  @Transactional
  public void createCcsInterviewOutcomeEvent(OutcomeSuperSetDto outcome) throws GatewayException {
    String[] operationsList = outcomeLookup.getLookup(outcome.getOutcomeCode());
    if (operationsList == null) {
      gatewayEventManager.triggerErrorEvent(this.getClass(), (Exception) null, "No outcome code found",
          String.valueOf(outcome.getCaseId()), FAILED_TO_LOOKUP_OUTCOME_CODE,
          "Survey type", "CCS INT",
          "Outcome code", outcome.getOutcomeCode(),
          "Secondary Outcome", outcome.getSecondaryOutcomeDescription());
      return;
    }
    UUID caseIdHolder = null;
    for (String operation : operationsList) {
      gatewayEventManager.triggerEvent(String.valueOf(outcome.getCaseId()), PROCESSING_CCS_INT_OUTCOME,
          "Survey type", "CCS INT",
          "Secondary Outcome", outcome.getSecondaryOutcomeDescription(),
          "Held case id", (caseIdHolder != null) ? String.valueOf(caseIdHolder) : "N/A",
          "Operation", operation,
          "Operation list", Arrays.toString(operationsList));
      caseIdHolder = outcomeServiceProcessors.get(operation).process(outcome, caseIdHolder, "CCS INT");
    }
    if (cancelOutcomeLookup.containsKey(outcome.getOutcomeCode()))
      triggerCancelLog(String.valueOf(caseIdHolder),String.valueOf(outcome.getCaseId()),outcome.getOutcomeCode());
  }

  @Override
  @Transactional
  public void createNcOutcomeEvent(OutcomeSuperSetDto outcome) throws GatewayException {
    String[] operationsList = outcomeLookup.getLookup(outcome.getOutcomeCode());
    if (operationsList == null) {
      gatewayEventManager.triggerErrorEvent(this.getClass(), (Exception) null, "No outcome code found",
          String.valueOf(outcome.getCaseId()), FAILED_TO_LOOKUP_OUTCOME_CODE,
          "Survey type", "NC",
          "Outcome code", outcome.getOutcomeCode(),
          "Secondary Outcome", outcome.getSecondaryOutcomeDescription());
      return;
    }
    UUID caseIdHolder = null;
    for (String operation : operationsList) {
      gatewayEventManager.triggerEvent(String.valueOf(outcome.getCaseId()), PROCESSING_NC_OUTCOME,
          "Survey type", "NC",
          "Secondary Outcome", outcome.getSecondaryOutcomeDescription(),
          "Held case id", (caseIdHolder != null) ? String.valueOf(caseIdHolder) : "N/A",
          "Operation", operation,
          "Operation list", Arrays.toString(operationsList));
      caseIdHolder = outcomeServiceProcessors.get(operation).process(outcome, caseIdHolder, "NC");
    }
    if (cancelOutcomeLookup.containsKey(outcome.getOutcomeCode()))
      triggerCancelLog(String.valueOf(caseIdHolder),String.valueOf(outcome.getCaseId()),outcome.getOutcomeCode());
  }

  public void triggerCancelLog(String caseId, String originalCaseId, String outcomeCode) {
    GatewayCache gatewayCache = gatewayCacheService.getById(String.valueOf(caseId));
    gatewayEventManager.triggerEvent(caseId, BAU_PROCESS_CHECK_CANCEL_NEW_CASE,
        ORIGINAL_CASE_ID, originalCaseId,
        OUTCOME_CODE, outcomeCode,
        LAST_ACTION_TYPE, gatewayCache.getLastActionInstruction() != null ? gatewayCache.lastActionInstruction : "Case not returned yet",
        LAST_ACTION_TIME, gatewayCache.getLastActionTime() != null ? String.valueOf(gatewayCache.getLastActionTime()) : "Case not returned yet");
  }
}

package uk.gov.ons.census.fwmt.outcomeservice.converter.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.common.rm.dto.ActionInstructionType;
import uk.gov.ons.census.fwmt.common.rm.dto.FwmtCancelActionInstruction;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;
import uk.gov.ons.census.fwmt.outcomeservice.converter.OutcomeServiceProcessor;
import uk.gov.ons.census.fwmt.outcomeservice.data.GatewayCache;
import uk.gov.ons.census.fwmt.outcomeservice.dto.OutcomeSuperSetDto;
import uk.gov.ons.census.fwmt.outcomeservice.message.RmFieldRepublishProducer;
import uk.gov.ons.census.fwmt.outcomeservice.service.impl.GatewayCacheService;
import uk.gov.ons.census.fwmt.outcomeservice.service.impl.SwitchCaseIdService;

import java.util.UUID;

import static uk.gov.ons.census.fwmt.outcomeservice.converter.OutcomeServiceLogConfig.*;

@Slf4j
@Component("CANCEL_FEEDBACK")
public class CancelFeedbackProcessor implements OutcomeServiceProcessor {

  @Autowired
  private SwitchCaseIdService switchCaseIdService;

  @Autowired
  private RmFieldRepublishProducer rmFieldRepublishProducer;

  @Autowired
  private GatewayEventManager gatewayEventManager;

  @Override
  public UUID process(OutcomeSuperSetDto outcome, UUID caseIdHolder, String type) throws GatewayException {
    UUID caseId = (caseIdHolder != null) ? caseIdHolder : outcome.getCaseId();
    String ncCaseId = null;
    if (type.equals("NC")) {
      ncCaseId = switchCaseIdService.fromIdOriginalToNc(String.valueOf(caseId));
    }

    String loggedCaseId = ncCaseId != null ? ncCaseId : String.valueOf(caseId);
    gatewayEventManager.triggerEvent(loggedCaseId, PROCESSING_OUTCOME,
        SURVEY_TYPE, type,
        PROCESSOR, "CANCEL_FEEDBACK",
        ORIGINAL_CASE_ID, String.valueOf(outcome.getCaseId()),
        SITE_CASE_ID, (outcome.getSiteCaseId() != null ? String.valueOf(outcome.getSiteCaseId()) : "N/A"));

    FwmtCancelActionInstruction fieldworkFollowup = FwmtCancelActionInstruction
        .builder()
        .actionInstruction(ActionInstructionType.CANCEL)
        .surveyName("FEEDBACK")
        .addressType("FEEDBACK")
        .addressLevel("F")
        .caseId(loggedCaseId)
        .build();

    rmFieldRepublishProducer.republish(fieldworkFollowup);

    gatewayEventManager.triggerEvent(loggedCaseId, RM_FIELD_REPUBLISH,
        SURVEY_NAME, "CENSUS",
        ADDRESS_TYPE, type,
        ORIGINAL_CASE_ID, String.valueOf(outcome.getCaseId()),
        ACTION_INSTRUCTION_TYPE, ActionInstructionType.CANCEL.toString(),
        TRANSACTION_ID, outcome.getTransactionId().toString());
    return caseId;
  }
}

package uk.gov.ons.census.fwmt.outcomeservice.converter.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.common.rm.dto.ActionInstructionType;
import uk.gov.ons.census.fwmt.common.rm.dto.FwmtActionInstruction;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;
import uk.gov.ons.census.fwmt.outcomeservice.converter.OutcomeServiceProcessor;
import uk.gov.ons.census.fwmt.outcomeservice.dto.OutcomeSuperSetDto;
import uk.gov.ons.census.fwmt.outcomeservice.message.RmFieldRepublishProducer;

@Slf4j
@Component("DELIVERED_FEEDBACK")
public class DeliveredFeedbackProcessor implements OutcomeServiceProcessor {

  public static final String PROCESSING_OUTCOME = "PROCESSING_OUTCOME";

  public static final String RM_FIELD_REPUBLISH = "RM_FIELD_REPUBLISH";

  @Autowired
  private RmFieldRepublishProducer rmFieldRepublishProducer;

  @Autowired
  private GatewayEventManager gatewayEventManager;

  @Override
  public UUID process(OutcomeSuperSetDto outcome, UUID caseIdHolder, String type) throws GatewayException {
    UUID caseId = (caseIdHolder != null) ? caseIdHolder : outcome.getCaseId();

    gatewayEventManager.triggerEvent(String.valueOf(caseId), PROCESSING_OUTCOME,
    "survey type", type,
    "processor", "DELIVERED_FEEDBACK",
    "original caseId", String.valueOf(outcome.getCaseId()),
    "Site Case id", (outcome.getSiteCaseId() != null ? String.valueOf(outcome.getSiteCaseId()) : "N/A"));

    FwmtActionInstruction fieldworkFollowup = FwmtActionInstruction.builder()
        .actionInstruction(ActionInstructionType.UPDATE)
        .surveyName("CENSUS")
        .addressType(type)
        .addressLevel("U")
        .caseId(caseId.toString())
        .build();

    rmFieldRepublishProducer.republish(fieldworkFollowup);

    gatewayEventManager.triggerEvent(String.valueOf(caseId), RM_FIELD_REPUBLISH,
        "survey name", "CENSUS",
        "address type", type,
    "action instruction", ActionInstructionType.UPDATE.toString(),
    "transactionId", outcome.getTransactionId().toString());

    return caseId;
  }
}

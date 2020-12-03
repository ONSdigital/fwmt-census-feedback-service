package uk.gov.ons.census.fwmt.outcomeservice.converter.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;
import uk.gov.ons.census.fwmt.outcomeservice.config.GatewayOutcomeQueueConfig;
import uk.gov.ons.census.fwmt.outcomeservice.converter.OutcomeServiceProcessor;
import uk.gov.ons.census.fwmt.outcomeservice.data.GatewayCache;
import uk.gov.ons.census.fwmt.outcomeservice.dto.OutcomeSuperSetDto;
import uk.gov.ons.census.fwmt.outcomeservice.message.GatewayOutcomeProducer;
import uk.gov.ons.census.fwmt.outcomeservice.service.impl.GatewayCacheService;
import uk.gov.ons.census.fwmt.outcomeservice.template.TemplateCreator;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static uk.gov.ons.census.fwmt.outcomeservice.enums.EventType.INTERVIEW_REQUIRED;
import static uk.gov.ons.census.fwmt.outcomeservice.enums.EventType.CCS_ADDRESS_LISTED;

@Component("INTERVIEW_REQUIRED_HH")
public class InterviewRequiredHhProcessor implements OutcomeServiceProcessor {

  public static final String PROCESSING_OUTCOME = "PROCESSING_OUTCOME";

  public static final String OUTCOME_SENT = "OUTCOME_SENT";

  @Autowired
  private DateFormat dateFormat;

  @Autowired
  private GatewayOutcomeProducer gatewayOutcomeProducer;

  @Autowired
  private GatewayEventManager gatewayEventManager;

  @Autowired
  private GatewayCacheService gatewayCacheService;

  @Override
  public UUID process(OutcomeSuperSetDto outcome, UUID caseIdHolder, String type) throws GatewayException {
    UUID caseId = (caseIdHolder != null) ? caseIdHolder : outcome.getCaseId();
    UUID newCaseId = UUID.randomUUID();

    gatewayEventManager.triggerEvent(String.valueOf(caseId), PROCESSING_OUTCOME,
        "Survey type", type,
        "Processor", "INTERVIEW_REQUIRED",
        "Case ID", String.valueOf(outcome.getCaseId()),
        "Interview Case ID", String.valueOf(newCaseId),
        "AddressType", "HH");

    GatewayCache plCache = gatewayCacheService.getById(String.valueOf(outcome.getCaseId()));

    cacheData(outcome, newCaseId);

    String eventDateTime = dateFormat.format(outcome.getEventDate());
    Map<String, Object> root = new HashMap<>();
    root.put("outcome", outcome);
    root.put("address", outcome.getAddress());
    root.put("caseId", newCaseId);
    root.put("eventDate", eventDateTime);
    root.put("addressType", "HH");
    root.put("addressLevel", "U");
    root.put("interviewRequired", "True");
    root.put("oa", plCache.getOa());
    root.put("region",plCache.getOa().charAt(0));

    String outcomeEvent = TemplateCreator.createOutcomeMessage(CCS_ADDRESS_LISTED, root);

    gatewayOutcomeProducer.sendOutcome(outcomeEvent, String.valueOf(outcome.getTransactionId()),
        GatewayOutcomeQueueConfig.GATEWAY_CCS_PROPERTY_LISTING_ROUTING_KEY);

    gatewayEventManager.triggerEvent(String.valueOf(caseId), OUTCOME_SENT,
        "Survey type", type,
        "Type", INTERVIEW_REQUIRED.toString(),
        "Transaction ID", outcome.getTransactionId().toString(),
        "Routing key", GatewayOutcomeQueueConfig.GATEWAY_CCS_PROPERTY_LISTING_ROUTING_KEY);

    return newCaseId;
  }

  private void cacheData(OutcomeSuperSetDto outcome, UUID newCaseId) {
    gatewayCacheService.save(GatewayCache.builder()
        .caseId(newCaseId.toString())
        .existsInFwmt(false)
        .accessInfo(outcome.getAccessInfo())
        .careCodes(OutcomeSuperSetDto.careCodesToText(outcome.getCareCodes()))
        .type(50)
        .build());
  }
}

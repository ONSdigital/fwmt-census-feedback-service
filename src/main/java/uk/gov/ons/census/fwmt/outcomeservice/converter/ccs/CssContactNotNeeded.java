package uk.gov.ons.census.fwmt.outcomeservice.converter.ccs;

import static uk.gov.ons.census.fwmt.outcomeservice.config.GatewayEventsConfig.CCSPL_OUTCOME_SENT;
import static uk.gov.ons.census.fwmt.outcomeservice.enums.EventType.CONTACT_NOT_NEEDED;
import static uk.gov.ons.census.fwmt.outcomeservice.enums.SurveyType.ccs;
import static uk.gov.ons.census.fwmt.outcomeservice.util.CcsUtilityMethods.getAddressLevel;
import static uk.gov.ons.census.fwmt.outcomeservice.util.CcsUtilityMethods.getAddressType;
import static uk.gov.ons.census.fwmt.outcomeservice.util.CcsUtilityMethods.getOrganisationName;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.gov.ons.census.fwmt.common.data.ccs.CCSPropertyListingOutcome;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;
import uk.gov.ons.census.fwmt.outcomeservice.config.GatewayOutcomeQueueConfig;
import uk.gov.ons.census.fwmt.outcomeservice.converter.CcsOutcomeServiceProcessor;
import uk.gov.ons.census.fwmt.outcomeservice.message.GatewayOutcomeProducer;
import uk.gov.ons.census.fwmt.outcomeservice.service.JobCacheManager;
import uk.gov.ons.census.fwmt.outcomeservice.template.TemplateCreator;

@Component
public class CssContactNotNeeded implements CcsOutcomeServiceProcessor {

  @Autowired
  private GatewayOutcomeProducer gatewayOutcomeProducer;

  @Autowired
  private GatewayEventManager gatewayEventManager;

  @Autowired
  private JobCacheManager jobCacheManager;

  @Override
  public boolean isValid(CCSPropertyListingOutcome ccsPLOutcome) {
    List<String> validSecondaryOutcomes = Collections.singletonList("Contact not needed");
    return validSecondaryOutcomes.contains(ccsPLOutcome.getSecondaryOutcome());
  }

  @Override
  public void processMessage(CCSPropertyListingOutcome ccsPLOutcome) throws GatewayException {
    UUID newRandomUUID = UUID.randomUUID();
    jobCacheManager.cacheCCSOutcome(String.valueOf(newRandomUUID), ccsPLOutcome);

    String eventDateTime = ccsPLOutcome.getEventDate().toString();
    Map<String, Object> root = new HashMap<>();
    root.put("generatedUuid", newRandomUUID);
    root.put("ccsPropertyListingOutcome", ccsPLOutcome);
    root.put("addressType", getAddressType(ccsPLOutcome));
    root.put("addressLevel", getAddressLevel(ccsPLOutcome));
    root.put("organisationName", getOrganisationName(ccsPLOutcome));
    root.put("eventDate", eventDateTime + "Z");

    String outcomeEvent = TemplateCreator.createOutcomeMessage(CONTACT_NOT_NEEDED, root, ccs);

    gatewayOutcomeProducer.sendOutcome(outcomeEvent, String.valueOf(ccsPLOutcome.getTransactionId()), GatewayOutcomeQueueConfig.GATEWAY_CCS_PROPERTYLISTING_ROUTING_KEY);
    gatewayEventManager.triggerEvent(String.valueOf(newRandomUUID), CCSPL_OUTCOME_SENT,
        "type", "CCSPL_CONTACT_NOT_NEEDED_OUTCOME_SENT", "transactionId", ccsPLOutcome.getTransactionId().toString());
  }
}

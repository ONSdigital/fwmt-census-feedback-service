package uk.gov.ons.census.fwmt.outcomeservice.config;

import com.godaddy.logging.LoggingConfigs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;
import uk.gov.ons.census.fwmt.outcomeservice.Application;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.function.Function;

@Configuration
public class GatewayEventsConfig {

  public static final String COMET_HH_OUTCOME_RECEIVED = "COMET_HH_OUTCOME_RECEIVED";
  public static final String COMET_CCSSI_OUTCOME_RECEIVED = "COMET_CCSSI_OUTCOME_RECEIVED";
  public static final String COMET_CCSPL_OUTCOME_RECEIVED = "COMET_CCSPL_OUTCOME_RECEIVED";

  //PROPERTY_LISTING_SENT
  public static final String CCSPL_OUTCOME_SENT = "CCSPL_OUTCOME_SENT";
  public static final String CCSPL_CACHED_OUTCOME = "CCSPL_CACHED_OUTCOME";
  public static final String CCSPL_CACHED_FAILED = "CCSPL_CACHED_FAILED";

  //OUTCOME_SENT_RM
  public static final String HH_OUTCOME_SENT = "HH_OUTCOME_SENT";
  public static final String CCSI_OUTCOME_SENT = "CCSI_OUTCOME_SENT";
  public static final String FAILED_JSON_CONVERSION = "FAILED_JSON_CONVERSION";
  public static final String FAILED_FULFILMENT_REQUEST_IS_NULL = "FAILED_FULFILMENT_REQUEST_IS_NULL";
  public static final String CCS_FAILED_FULFILMENT_REQUEST_INVALID = "FAILED_FULFILMENT_REQUEST_SIZE_INVALID";
  public static final String FAILED_FULFILMENT_REQUEST_ADDITIONAL_QID_IN_PROPERTY_LISTING = "FAILED_FULFILMENT_REQUEST_ADDITIONAL_QID_IN_PROPERTY_LISTING";

  //Health logging
  public static final String RABBIT_QUEUE_UP = "RABBIT_QUEUE_UP";
  public static final String RABBIT_QUEUE_DOWN = "RABBIT_QUEUE_DOWN";
  public static final String REDIS_SERVICE_UP = "REDIS_SERVICE_UP";
  public static final String REDIS_SERVICE_DOWN = "REDIS_SERVICE_DOWN";

  @Bean
  public GatewayEventManager gatewayEventManager() {
    GatewayEventManager gatewayEventManager = new GatewayEventManager();
    gatewayEventManager.setSource(Application.APPLICATION_NAME);
    gatewayEventManager.addEventTypes(new String[] {COMET_HH_OUTCOME_RECEIVED, COMET_CCSSI_OUTCOME_RECEIVED,
        COMET_CCSPL_OUTCOME_RECEIVED, CCSPL_OUTCOME_SENT, HH_OUTCOME_SENT, CCSI_OUTCOME_SENT, RABBIT_QUEUE_UP,
        REDIS_SERVICE_UP, CCSPL_CACHED_OUTCOME});
    gatewayEventManager.addErrorEventTypes(new String[] {FAILED_JSON_CONVERSION, FAILED_FULFILMENT_REQUEST_IS_NULL,
        CCS_FAILED_FULFILMENT_REQUEST_INVALID, RABBIT_QUEUE_DOWN, REDIS_SERVICE_DOWN,
        FAILED_FULFILMENT_REQUEST_ADDITIONAL_QID_IN_PROPERTY_LISTING});
    
    return gatewayEventManager;
  }

  @Value("#{'${logging.profile}' == 'CLOUD'}")
  private boolean useJsonLogging;

  /**
   * This method needs to be called before the Gateway event Manager is used,
   * if not the logger will not be properly initialised
   */
  @PostConstruct
  public void initJsonLogging() {
    HashMap<Class<?>, Function<Object, String>> customMappers = new HashMap<>();
    customMappers.put(LocalTime.class, Object::toString);
    customMappers.put(LocalDateTime.class, Object::toString);

    LoggingConfigs configs;

    if (useJsonLogging) {
      configs = LoggingConfigs.builder().customMapper(customMappers).build().useJson();
    }
    else {
      configs = LoggingConfigs.builder().customMapper(customMappers).build();
    }
    LoggingConfigs.setCurrent(configs);
  }
}

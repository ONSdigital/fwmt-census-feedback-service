package uk.gov.ons.census.fwmt.outcomeservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.outcomeservice.converter.CancelOutcomeLookup;
import uk.gov.ons.census.fwmt.outcomeservice.converter.OutcomeLookup;
import uk.gov.ons.census.fwmt.outcomeservice.converter.ReasonCodeLookup;
import uk.gov.ons.census.fwmt.outcomeservice.converter.RefusalEncryptionLookup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.nio.charset.StandardCharsets.UTF_8;

@Configuration
public class OutcomeSetup {
  @Autowired
  private ResourceLoader resourceLoader;

  @Value(value = "${outcomeservice.outcomeCodeLookup.path}")
  private String outcomeCodeLookupPath;

  @Value(value = "${outcomeservice.reasonCodeLookup.path}")
  private String reasonCodeLookupPath;

  @Value(value = "${outcomeservice.cancelOutcomeCodeLookup.path}")
  private String cancelOutcomeCodeLookupPath;

  @Value(value = "${outcomeservice.processorsleep.milliseconds}")
  private int processorSleepMilliSeconds;

  @Value(value = "${outcomeservice.refusalEncryptionCodeLookup.path}")
  private String refusalEncryptionCodeLookupPath;

  @Bean
  public OutcomeLookup buildOutcomeLookup() throws GatewayException {
    String line;
    Resource resource = resourceLoader.getResource(outcomeCodeLookupPath);

    OutcomeLookup outcomeLookup = new OutcomeLookup();
    try (BufferedReader in = new BufferedReader(new InputStreamReader(resource.getInputStream(), UTF_8))) {
      while ((line = in.readLine()) != null) {
        String[] lookup = line.split("\\|");
        outcomeLookup.add(lookup[0], lookup[1].split(","));
      }
    } catch (IOException e) {
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Cannot process outcome lookup");
    }
    return outcomeLookup;
  }

  @Bean
  public CancelOutcomeLookup buildCancelOutcomeLookup() throws GatewayException {
    String line;
    Resource resource = resourceLoader.getResource(cancelOutcomeCodeLookupPath);

    CancelOutcomeLookup cancelOutcomeLookup = new CancelOutcomeLookup();
    try (BufferedReader in = new BufferedReader(new InputStreamReader(resource.getInputStream(), UTF_8))) {
      while ((line = in.readLine()) != null) {
        cancelOutcomeLookup.add(line);
      }
    } catch (IOException e) {
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Cannot process outcome lookup");
    }
    return cancelOutcomeLookup;
  }

  @Bean
  public ReasonCodeLookup buildReasonCodeLookup() throws GatewayException {
    String line;
    Resource resource = resourceLoader.getResource(reasonCodeLookupPath);
    ReasonCodeLookup reasonCodeLookup = new ReasonCodeLookup();
    try (BufferedReader in = new BufferedReader(new InputStreamReader(resource.getInputStream(), UTF_8))) {
      while ((line = in.readLine()) != null) {
        String[] lookup = line.split(",");
        reasonCodeLookup.add(lookup[0], lookup[1]);
      }
    } catch (IOException e) {
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Cannot process reason code lookup");
    }
    return reasonCodeLookup;
  }

  public int getMessageProcessorSleepTime(){
    return processorSleepMilliSeconds;
  }

  @Bean
  public RefusalEncryptionLookup buildRefusalEncryptionCodeLookup() throws GatewayException {
    String line;
    Resource resource = resourceLoader.getResource(refusalEncryptionCodeLookupPath);
    RefusalEncryptionLookup refusalEncryptionLookup = new RefusalEncryptionLookup();
    try (BufferedReader in = new BufferedReader(new InputStreamReader(resource.getInputStream(), UTF_8))) {
      while ((line = in.readLine()) != null) {
        refusalEncryptionLookup.add(line, line);
      }
    }catch (IOException e) {
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Cannot process transition rule lookup");
    }
    return refusalEncryptionLookup;
  }
}

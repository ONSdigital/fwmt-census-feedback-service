package uk.gov.ons.census.fwmt.outcomeservice.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.census.fwmt.common.data.comet.HouseholdOutcome;
import uk.gov.ons.census.fwmt.common.error.GatewayException;

@Api(value = "FWMT Census Outcome Service", description = "Operations pertaining to receiving outcomes from COMET")
@RestController
@RequestMapping("/OutcomeController")
public interface OutcomeApi {

  @ApiOperation(value = "Post an outcome to the FWMT Gateway", response = HouseholdOutcome.class)
  @ApiResponses(value = {
      @ApiResponse(code = 202, message = "Case Outcome received", response = HouseholdOutcome.class)})
  @RequestMapping(value = "/HouseholdOutcome",
      produces = {"application/json"},
      method = RequestMethod.POST)
  ResponseEntity<HouseholdOutcome> householdCaseOutcomeResponse(
      @RequestBody HouseholdOutcome householdOutcome) throws GatewayException;
}
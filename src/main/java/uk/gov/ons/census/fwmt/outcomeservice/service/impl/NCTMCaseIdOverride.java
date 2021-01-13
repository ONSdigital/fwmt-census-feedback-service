package uk.gov.ons.census.fwmt.outcomeservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.census.fwmt.common.data.nc.NCOutcome;
import uk.gov.ons.census.fwmt.outcomeservice.data.GatewayCache;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
public class NCTMCaseIdOverride {

    @Autowired
    private GatewayCacheService gatewayCacheService;

    @Transactional
    public void overrideTMCaseIdWithRMOriginalCaseId(String caseID, NCOutcome ncOutcome) {
        GatewayCache gatewayCache = gatewayCacheService.getById(caseID);
        ncOutcome.setCaseId(UUID.fromString(gatewayCache.getOriginalCaseId()));
    }
}
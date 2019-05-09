package uk.gov.ons.census.fwmt.outcomeservice.data.dto.rm;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "fulfilment",
    "productCode",
    "caseId",
    "address",
    "contact"
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Fulfilment {

  @JsonProperty("productCode")
  private String productCode;

  @JsonProperty("caseId")
  private UUID caseId;

  @JsonProperty("address")
  private Address address;

  @JsonProperty("contact")
  private Contact contact;

}

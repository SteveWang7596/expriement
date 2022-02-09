/*
 * Card Payment API
 *
 * # Overview This interface must be implemented to accepts and send Iso20022 financial transactions to and from TJ. 
 *
 * OpenAPI document version: 1.0.00
 * Maintained by: cardpaymnetapi-integration@switch.tj
 *
 * AUTO-GENERATED FILE, DO NOT MODIFY!
 */
package za.co.transactionjunction.cardpaymentapi.gen.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;





@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaUndertowServerCodegen", date = "2022-01-25T15:53:26.548+02:00[Africa/Harare]")
public class AcceptorAuthoisationRequestV09   {
  
  private String header;
  private String authorisationRequest;
  private String securityTrailer;

  /**
   */
  public AcceptorAuthoisationRequestV09 header(String header) {
    this.header = header;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("header")
  public String getHeader() {
    return header;
  }
  public void setHeader(String header) {
    this.header = header;
  }

  /**
   */
  public AcceptorAuthoisationRequestV09 authorisationRequest(String authorisationRequest) {
    this.authorisationRequest = authorisationRequest;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("authorisationRequest")
  public String getAuthorisationRequest() {
    return authorisationRequest;
  }
  public void setAuthorisationRequest(String authorisationRequest) {
    this.authorisationRequest = authorisationRequest;
  }

  /**
   */
  public AcceptorAuthoisationRequestV09 securityTrailer(String securityTrailer) {
    this.securityTrailer = securityTrailer;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("securityTrailer")
  public String getSecurityTrailer() {
    return securityTrailer;
  }
  public void setSecurityTrailer(String securityTrailer) {
    this.securityTrailer = securityTrailer;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AcceptorAuthoisationRequestV09 acceptorAuthoisationRequestV09 = (AcceptorAuthoisationRequestV09) o;
    return Objects.equals(header, acceptorAuthoisationRequestV09.header) &&
        Objects.equals(authorisationRequest, acceptorAuthoisationRequestV09.authorisationRequest) &&
        Objects.equals(securityTrailer, acceptorAuthoisationRequestV09.securityTrailer);
  }

  @Override
  public int hashCode() {
    return Objects.hash(header, authorisationRequest, securityTrailer);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AcceptorAuthoisationRequestV09 {\n");
    
    sb.append("    header: ").append(toIndentedString(header)).append("\n");
    sb.append("    authorisationRequest: ").append(toIndentedString(authorisationRequest)).append("\n");
    sb.append("    securityTrailer: ").append(toIndentedString(securityTrailer)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}


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
package gen.api;

import gen.model.AcceptorAuthoisationRequestV09;
import gen.model.AcceptorAuthorisationResponseV09;
import io.undertow.server.*;
import io.undertow.util.*;

import gen.model.*;

@SuppressWarnings("TooManyFunctions")
public interface PathHandlerInterface {

    /**
     * <p>The AcceptorAuthorisationRequest message is sent by an acceptor (or its agent) to the acquirer (or its agent), to check with the issuer (or its agent) that the account associated to the card has the resources to fund the payment. This checking will include validation of the card data and any additional transaction data provided.</p>
     *
     * <p><b>Endpoint</b>: {@link Methods#POST POST} "/v1/AcceptorAuthorisation" (<i>privileged: true</i>)</p>
     *
     * <p><b>Request parameters</b>:</p>
     * <ul>
     * </ul>
     *
     * <p><b>Consumes</b>: [{mediaType=application/json}, {mediaType=application/xml}]</p>
     * <p><b>Payload</b>: {@link AcceptorAuthoisationRequestV09} (<i>required: true</i>)</p>
     *
     * <p><b>Produces</b>: [{mediaType=application/json}, {mediaType=application/xml}]</p>
     * <p><b>Returns</b>: {@link AcceptorAuthorisationResponseV09}</p>
     *
     * <p><b>Responses</b>:</p>
     * <ul>
     * <li><b>200 (success)</b>: Transaction Reversal</li>
     * <li><b>Default</b>: Error information</li>
     * </ul>
     */
    @javax.annotation.Nonnull
    HttpHandler authorisation();
}

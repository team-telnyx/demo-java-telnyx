package com.telnyx.webhookverification;

import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import io.github.cdimascio.dotenv.Dotenv;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.Base64;

@RestController
public class WebhookController {

    private static final Dotenv dotenv = Dotenv.load();
    private final String TELNYX_PUBLIC_KEY = dotenv.get("TELNYX_PUBLIC_KEY");

    @PostMapping("/messaging/inbound")
    public ResponseEntity<Void> webhook(@RequestBody String payload, @RequestHeader HttpHeaders headers) {
        Security.addProvider(new BouncyCastleProvider());
        String signature, timestamp;
        try {
            signature = headers.get("telnyx-signature-ed25519").get(0);
            timestamp = headers.get("telnyx-timestamp").get(0);
        }
        catch (Exception e) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        String signedPayload = timestamp + "|" + payload;
        byte[] signedPayloadBytes = signedPayload.getBytes(StandardCharsets.UTF_8);

        Ed25519PublicKeyParameters ed25519PublicKeyParameters = new Ed25519PublicKeyParameters(Base64.getDecoder().decode(TELNYX_PUBLIC_KEY), 0);

        Signer verifier = new Ed25519Signer();
        verifier.init(false, ed25519PublicKeyParameters);
        verifier.update(signedPayloadBytes, 0, signedPayloadBytes.length);
        boolean isVerified = verifier.verifySignature(Base64.getDecoder().decode(signature));

        if(isVerified) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
    }
}

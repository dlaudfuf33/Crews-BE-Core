package org.baas.baascore.controller;

import lombok.RequiredArgsConstructor;
import org.baas.baascore.dto.request.CardReissuedRequest;
import org.baas.baascore.dto.request.CommonRequest;
import org.baas.baascore.dto.response.CardIssuedResponse;
import org.baas.baascore.dto.response.MessageResponse;
import org.baas.baascore.service.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/cards")
public class CardController {
    private final CardService cardService;

    @PostMapping
    public ResponseEntity<CardIssuedResponse> cardIssued(@RequestBody CommonRequest commonRequest){
        return ResponseEntity.ok().body(cardService.cardIssued(commonRequest));
    }

    @DeleteMapping
    public ResponseEntity<MessageResponse> cardRemove(@RequestBody CardReissuedRequest cardReissuedRequest){
        return ResponseEntity.ok().body(cardService.cardRemove(cardReissuedRequest));
    }

    @PostMapping("/reissue")
    public ResponseEntity<CardIssuedResponse> cardReissued(@RequestBody CardReissuedRequest cardReissuedRequest){
        return ResponseEntity.ok().body(cardService.cardReissued(cardReissuedRequest));
    }
}

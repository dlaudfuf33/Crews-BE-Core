package org.baas.baascore.controller;

import lombok.RequiredArgsConstructor;
import org.baas.baascore.dto.request.CIRequest;
import org.baas.baascore.dto.response.AccountIssuedResponse;
import org.baas.baascore.service.CIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class CiController {

    private final CIService ciService;

    @PostMapping("/ci")
    public ResponseEntity<AccountIssuedResponse> ciSave(@RequestBody CIRequest ci){
        return ResponseEntity.ok().body(ciService.ciSave(ci));
    }
}

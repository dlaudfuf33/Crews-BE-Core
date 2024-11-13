package org.baas.baascore.controller;

import lombok.RequiredArgsConstructor;
import org.baas.baascore.dto.CIRequest;
import org.baas.baascore.dto.IdentityRequest;
import org.baas.baascore.service.CIService;
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
    public String ciSave(@RequestBody CIRequest ci){
        return ciService.ciSave(ci);
    }
}

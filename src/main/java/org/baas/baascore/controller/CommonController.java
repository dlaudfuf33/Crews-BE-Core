package org.baas.baascore.controller;

import lombok.RequiredArgsConstructor;
import org.baas.baascore.dto.request.CIRequest;
import org.baas.baascore.dto.response.AccountIssuedResponse;
import org.baas.baascore.dto.response.ProductAllResponse;
import org.baas.baascore.dto.response.ProductResponse;
import org.baas.baascore.service.CommonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class CommonController {

    private final CommonService commonService;

    @PostMapping("/ci")
    public ResponseEntity<AccountIssuedResponse> ciSave(@RequestBody CIRequest ci){
        return ResponseEntity.ok().body(commonService.ciSave(ci));
    }

    @GetMapping("/products")
    public ResponseEntity<ProductAllResponse> getAllProductInfo() {
        return ResponseEntity.ok().body(commonService.getAllProductInfo());
    }
}

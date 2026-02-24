package com.payvance.erp_saas.erp.controller;

import com.payvance.erp_saas.erp.dto.HsnSacDTO;
import com.payvance.erp_saas.erp.service.HsnSacService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/hsn-sac")
@RequiredArgsConstructor
public class HsnSacController {

    private final HsnSacService hsnSacService;

    @GetMapping("/{code}")
    public ResponseEntity<String> getDescriptionByCode(@PathVariable String code) {
        return ResponseEntity.ok(hsnSacService.getDescriptionByCode(code));
    }

    @GetMapping
    public ResponseEntity<List<HsnSacDTO>> getAllHsnSac(@RequestParam(required = false) Integer type) {
        if (type != null) {
            return ResponseEntity.ok(hsnSacService.getHsnSacByType(type));
        }
        return ResponseEntity.ok(hsnSacService.getAllHsnSac());
    }
}

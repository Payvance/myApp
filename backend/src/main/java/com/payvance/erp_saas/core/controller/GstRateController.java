package com.payvance.erp_saas.core.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payvance.erp_saas.core.service.GstRateService;


@RestController
@RequestMapping("/api/gst")
public class GstRateController {
	
	 @Autowired
	    private GstRateService gstRateService;

	 
	 /*
	  * Get Latest GST Rate
	  */
	    @GetMapping("/rate/latest")
	    public ResponseEntity<Map<String, Object>> getLatestGstRate() {
	        return ResponseEntity.ok(gstRateService.getLatestGstRate());
	    }

}

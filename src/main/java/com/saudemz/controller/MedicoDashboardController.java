package com.saudemz.controller;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saudemz.service.MedicoDashboardService;

@RestController
@RequestMapping("/medico/dashboard")
@PreAuthorize("hasRole('MEDICO')")
public class MedicoDashboardController {

    private final MedicoDashboardService medicoDashboardService;

    public MedicoDashboardController(
            MedicoDashboardService medicoDashboardService) {
        this.medicoDashboardService = medicoDashboardService;
    }

    @GetMapping
    public Map<String, Object> dashboard() {
        return medicoDashboardService.gerarDashboard();
    }
}
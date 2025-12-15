package com.teekasarthi.teekasarthi.controller;


import com.teekasarthi.teekasarthi.dto.VerifyBeneficiary;
import com.teekasarthi.teekasarthi.service.WorkerService;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/worker")
public class WorkerController {
    @Autowired
    WorkerService workerService;
    @PostMapping("/verify/{id}")
    public ResponseEntity<?> verify(@PathVariable Long id,
                                    @RequestBody VerifyBeneficiary dto) {
        workerService.beneficiaryVerification(id, dto);
        return ResponseEntity.ok("Beneficiary verified successfully");
    }
    @PostMapping("/reject/{id}")
    public ResponseEntity<?> reject(@PathVariable Long id,
                                    @RequestBody VerifyBeneficiary dto) {
        workerService.beneficiaryRejection(id, dto);
        return ResponseEntity.ok("Beneficiary verified successfully");
    }

    @GetMapping("/pending-beneficiaries")
    public ResponseEntity<?> getPendingBeneficiaries() {
        return ResponseEntity.ok(workerService.getPendingBeneficiaries());
    }


}

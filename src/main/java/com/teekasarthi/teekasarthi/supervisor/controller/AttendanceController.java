package com.teekasarthi.teekasarthi.supervisor.controller;

import com.teekasarthi.teekasarthi.supervisor.services.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.*;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/submit")
    public ResponseEntity<?> submitBulkAttendance(
            MultipartHttpServletRequest request,
            @RequestParam("workerId") Long workerId,
            @RequestParam("centerId") Long centerId,
            @RequestParam("latitude") String latitude,
            @RequestParam("longitude") String longitude,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam("taskId") Long assignmentId
    ) {
        System.out.println("Processing Bulk Attendance for Assignment ID: " + assignmentId);

        try {
            // 1. Parse Dynamic Photos & Dates
            Map<Long, MultipartFile> beneficiaryPhotos = new HashMap<>();
            Map<Long, String> beneficiaryDates = new HashMap<>();

            Iterator<String> iterator = request.getFileNames();

            while (iterator.hasNext()) {
                String key = iterator.next();
                if (key.startsWith("photo_")) {
                    String idString = key.replace("photo_", "");
                    try {
                        Long beneficiaryId = Long.parseLong(idString);
                        MultipartFile file = request.getFile(key);
                        String date = request.getParameter("date_" + beneficiaryId);

                        if (file != null) {
                            beneficiaryPhotos.put(beneficiaryId, file);
                        }
                        if (date != null) {
                            beneficiaryDates.put(beneficiaryId, date);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid ID format in key: " + key);
                    }
                }
            }

            if (beneficiaryPhotos.isEmpty()) {
                return ResponseEntity.badRequest().body("No beneficiary photos provided.");
            }

            // 👇 UPDATED CALL: Added latitude and longitude
            attendanceService.processBulkAttendance(
                    workerId,
                    centerId,
                    location,
                    latitude,   // <--- Added
                    longitude,  // <--- Added
                    assignmentId,
                    beneficiaryPhotos,
                    beneficiaryDates
            );

            return ResponseEntity.ok("Attendance marked and tasks updated to SUCCESS.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
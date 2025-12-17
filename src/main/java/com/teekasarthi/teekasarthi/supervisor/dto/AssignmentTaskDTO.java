package com.teekasarthi.teekasarthi.supervisor.dto;

import com.teekasarthi.teekasarthi.supervisor.entity.AssignmentTask;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentTaskDTO {

    private Long id;
    private Long assignmentId; // Reference to parent ID only
    private String status;
    private String submissionLatitude;
    private String submissionLongitude;
    private String submissionAddress;

    // Flattened Beneficiary Details for easier Frontend display
    private Long beneficiaryId;
    private String beneficiaryName;
    private String beneficiaryPhone;
    private String beneficiaryAddress;
    private String beneficiaryDob; // Useful for calculating age if needed

    // Mapper Constructor (Optional but recommended for cleaner code)
    public static AssignmentTaskDTO fromEntity(AssignmentTask task) {
        return AssignmentTaskDTO.builder()
                .beneficiaryId(task.getBeneficiary().getId()) // ID goes to ID
                .beneficiaryDob(task.getBeneficiary().getDob().toString()) // Date converted to String
                .status(task.getStatus())
                .assignmentId(task.getAssignment().getId())
                .submissionLatitude(task.getSubmissionLatitude())
                .submissionLongitude(task.getSubmissionLongitude())
                .submissionAddress(task.getSubmissionAddress())
                // Safe handling in case beneficiary is somehow null (though nullable=false)
                .beneficiaryId(task.getBeneficiary() != null ? task.getBeneficiary().getId() : null)
                .beneficiaryName(task.getBeneficiary() != null ? task.getBeneficiary().getName() : "Unknown")
                .beneficiaryPhone(task.getBeneficiary() != null ? task.getBeneficiary().getPhoneNumber() : "N/A") // adjust 'getPhoneNumber' to your actual field name
                .beneficiaryAddress(task.getBeneficiary() != null ? task.getBeneficiary().getAddress() : "")
                .build();
    }
}
package com.teekasarthi.teekasarthi.supervisor.services;

import com.teekasarthi.teekasarthi.supervisor.dto.WorkerTaskDTO;
import com.teekasarthi.teekasarthi.supervisor.entity.AssignmentTask;
import com.teekasarthi.teekasarthi.supervisor.repository.AssignmentTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentTaskService {

    private final AssignmentTaskRepository assignmentTaskRepository;

    @Transactional(readOnly = true)
    public List<WorkerTaskDTO> getBeneficiariesForWorker(Long workerId, LocalDate date) {
        // Default to TODAY if date is null
        LocalDate targetDate = (date != null) ? date : LocalDate.now();

        List<AssignmentTask> tasks = assignmentTaskRepository.findTasksByWorkerAndDate(workerId, targetDate);

        return tasks.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateTaskStatus(Long taskId, String newStatus) {
        AssignmentTask task = assignmentTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setStatus(newStatus);

        assignmentTaskRepository.save(task);
    }

    private WorkerTaskDTO mapToDTO(AssignmentTask task) {
        return WorkerTaskDTO.builder()
                .taskId(task.getId())
                .taskStatus(task.getStatus())
                .beneficiaryId(task.getBeneficiary().getId())
                .beneficiaryName(task.getBeneficiary().getName())
                .beneficiaryPhone(task.getBeneficiary().getPhoneNumber())
                .build();
    }
}
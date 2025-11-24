package com.teekasarthi.teekasarthi.supervisor.services;

import com.teekasarthi.teekasarthi.supervisor.entity.Center;
import com.teekasarthi.teekasarthi.supervisor.entity.Worker;
import com.teekasarthi.teekasarthi.supervisor.repository.CenterRepository;
import com.teekasarthi.teekasarthi.supervisor.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupervisorService {

    private final CenterRepository centerRepository;
    private final WorkerRepository workerRepository;

    /**
     * Returns centers under a supervisor.
     * Pagination-ready; currently returns all centers (non-paged convenience).
     */
    public List<Center> getCentersForSupervisor(Long supervisorId) {
        log.debug("Fetching centers for supervisorId={}", supervisorId);
        return centerRepository.findBySupervisorId(supervisorId);
    }

    /**
     * Returns workers for a given center.
     */
    public List<Worker> getWorkersForCenter(Long centerId) {
        log.debug("Fetching workers for centerId={}", centerId);
        return workerRepository.findByCenterId(centerId);
    }

    /**
     * Example paged method (future-proof for large datasets).
     */
    public List<Center> getCentersForSupervisorPaged(Long supervisorId, int page, int size) {
        var pageable = PageRequest.of(page, size);
        return centerRepository.findBySupervisorId(supervisorId, pageable).getContent();
    }
}
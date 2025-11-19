package org.skypro.bank.star.recommendations_service.service;

import org.skypro.bank.star.recommendations_service.repository.CachedUserDataRepository;
import org.springframework.stereotype.Service;

@Service
public class CacheManagementService {

    CachedUserDataRepository cachedUserDataRepository;

    public CacheManagementService(CachedUserDataRepository cachedUserDataRepository) {
        this.cachedUserDataRepository = cachedUserDataRepository;
    }

    public void clearAllCaches() {
        cachedUserDataRepository.clearAllCaches();
    }

}

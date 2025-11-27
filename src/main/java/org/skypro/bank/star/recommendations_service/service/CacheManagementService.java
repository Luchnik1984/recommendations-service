package org.skypro.bank.star.recommendations_service.service;

import org.skypro.bank.star.recommendations_service.repository.CachedUserDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CacheManagementService {

    CachedUserDataRepository cachedUserDataRepository;
    private static final Logger logger = LoggerFactory.getLogger(CacheManagementService.class);

    public CacheManagementService(CachedUserDataRepository cachedUserDataRepository) {
        this.cachedUserDataRepository = cachedUserDataRepository;
    }

    /**
     * Очистка всех кешей сервиса
     */
    public void clearAllCaches()
    { logger.info("Clearing all caches");
        cachedUserDataRepository.clearAllCaches();
        logger.info("All caches cleared successfully");
    }

    /**
     * Очистка кеша поиска пользователей.
     * Вызывается автоматически при добавлении/изменении пользователей
     */
    public void clearUserSearchCache() {
        logger.info("Clearing user search cache");
        cachedUserDataRepository.clearAllCaches();
        logger.info("User search cache cleared");
    }

}

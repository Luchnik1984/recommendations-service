                   # Recommendation Service for Bank "Star"

Минимальный жизнеспособный продукт (MVP) рекомендательной системы для банка.

## Текущий статус

**СПРИНТ 1: Базовая инфраструктура - ВЫПОЛНЕН**

### Выполненные User Stories:

#### **US1: Настройка Spring Boot проекта с H2 БД**
- Настроен Spring Boot 3.5.7 с Java 17
- Подключена H2 Database (file-based, read-only)
- Конфигурация JdbcTemplate с ручным DataSource
- Read-only режим работы с основной БД

#### **US2: Создание структуры пакетов**
- `controller/` - REST API контроллеры
- `service/` - бизнес-логика сервиса
- `repository/` - слой доступа к данным
- `model/dto/` - DTO объекты для API
- `rule/` - бизнес-правила рекомендаций
- `enums/` - перечисления типов
- `configuration/` - конфигурационные классы

#### **US3: Реализация UserDataRepository с базовыми SQL-запросами**
- Интерфейс `UserDataRepository` с контрактом доступа к данным
- Реализация `UserDataRepositoryImpl` с оптимизированными SQL-запросами
- JOIN запросы между таблицами TRANSACTIONS и PRODUCTS
- Поддержка операций DEPOSIT/WITHDRAW

#### **US4: Интеграционные тесты для репозитория**
- `UserDataRepositoryIntegrationTest` - тестирование SQL-запросов
- `RecommendationControllerIntegrationTest` - тестирование REST API
- `UserStories1FinalTest` - проверка конфигурации БД
- Мultiple test profiles (`test`, `testConnectRealDB`)

## Архитектура

- **Spring Boot 3.5.x** + **Java 17**
- **H2 Database** (file-based, read-only для прода; in-memory для тестов)
- **JdbcTemplate** с ручной конфигурацией DataSource
- **Liquibase** для управления миграциями тестовой БД
- **REST API** с JSON форматом ответа

## База данных

### Продакшн база:
- Файл: `src/main/resources/transaction.mv.db`
- Пользователи: 1000
- Продукты: 20
- Транзакции: 10093
- Режим: только для чтения

### Тестовая база (Liquibase):
- In-memory H2 с предзаполненными тестовыми данными
- Автоматическое создание таблиц и данных для тестов

### База динамических правил (PostgreSQL)
- Тип: PostgreSQL
- Режим: Read-write
- Назначение: Хранение динамических правил рекомендаций
- Миграции: Liquibase

##  Запуск проекта
### 1. Настройка окружения
#### Автоматическая настройка (рекомендуется)
```bash
  ./scripts/setup-env.sh
```

После выполнения скрипта отредактируйте созданные файлы:

```bash

nano configuration.env
nano configuration.env.dev  
nano configuration.env.test
```


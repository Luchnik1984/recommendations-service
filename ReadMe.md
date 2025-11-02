                   # Recommendation Service for Bank "Star"

Минимальный жизнеспособный продукт (MVP) рекомендательной системы для банка.

## Текущий статус

**US1: Настройка Spring Boot проекта с H2 БД** -  ВЫПОЛНЕНО +

## Архитектура

- **Spring Boot 3.5.x**
- **H2 Database** (file-based, read-only)
- **JdbcTemplate** с ручной конфигурацией DataSource
- **REST API** (в разработке)

## База данных

Файл БД: `src/main/resources/transaction.mv.db`
- Пользователи: 1000
- Продукты: 20
- Транзакции: 10093

## Запуск проекта

```bash
  mvn spring-boot:run
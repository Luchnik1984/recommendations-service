package org.skypro.bank.star.recommendations_service.model.dynamic;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Сущность, представляющая динамическое правило рекомендаций.
 * Динамическое правило состоит из рекомендуемого продукта и набора условий (RuleQuery),
 * которые должны быть выполнены для пользователя, чтобы продукт был рекомендован.
 * Правило считается выполненным, если ВСЕ входящие в него RuleQuery возвращают true
 * (с учетом их флагов negate).
 */
@Entity
@Table(name = "dynamic_rules")
public class DynamicRule {

    /**
     * Уникальный идентификатор правила.
     * Генерируется автоматически при создании правила.
     */
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    /**
     * Название рекомендуемого продукта.
     * Отображается пользователю в рекомендациях.
     */
    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    /**
     * UUID рекомендуемого продукта.
     * Должен соответствовать реальному продукту в основной системе.
     */
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    /**
     * Текст описания продукта для рекомендации.
     * Содержит маркетинговое описание, которое видит пользователь.
     */
    @Column(name = "product_text", nullable = false, columnDefinition = "TEXT")
    private String productText;

    /**
     * Список запросов (условий), составляющих правило.
     * Все запросы должны быть выполнены для применения рекомендации.
     *
     * Запросы выполняются в порядке их следования в списке.
     * При удалении правила все связанные запросы также удаляются (CascadeType.ALL).
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "dynamic_rule_id")
    @OrderColumn(name = "query_order")
    private List<RuleQuery> rules = new ArrayList<>();

    /**
     * Дата и время создания правила.
     * Устанавливается автоматически при создании записи.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public DynamicRule() {
    }

    /**
     * Конструктор для создания правила с указанием основных параметров.
     * @param productName название рекомендуемого продукта
     * @param productId UUID рекомендуемого продукта
     * @param productText текст описания продукта
     */
    public DynamicRule(String productName, UUID productId, String productText) {
        this.productName = productName;
        this.productId = productId;
        this.productText = productText;
    }

    /**
     * Конструктор для создания правила со всеми параметрами.
     * @param productName название рекомендуемого продукта
     * @param productId UUID рекомендуемого продукта
     * @param productText текст описания продукта
     * @param rules список условий правила
     */
    public DynamicRule(String productName, UUID productId, String productText, List<RuleQuery> rules) {
        this.productName = productName;
        this.productId = productId;
        this.productText = productText;
        this.rules = rules != null ? new ArrayList<>(rules) : new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getProductText() {
        return productText;
    }

    public void setProductText(String productText) {
        this.productText = productText;
    }

    /**
     * Возвращает список условий правила.
     * Возвращаемый список является копией для обеспечения иммутабельности.
     * @return список условий правила
     */
    public List<RuleQuery> getRules() {
        return new ArrayList<>(rules);
    }

    /**
     * Устанавливает список условий правила.
     * Внутренний список заменяется копией переданного списка.
     * @param rules список условий правила
     */
    public void setRules(List<RuleQuery> rules) {
        this.rules =rules != null ? new ArrayList<>(rules) : new ArrayList<>();
    }

    /**
     * Добавляет условие в правило.
     * @param ruleQuery условие для добавления
     */
    public void addRule(RuleQuery ruleQuery) {
        this.rules.add(ruleQuery);
    }

    /**
     * Удаляет условие из правила.
     * @param ruleQuery условие для удаления
     * @return true если условие было удалено, false если условие не найдено
     */
    public boolean removeRule(RuleQuery ruleQuery) {
        return this.rules.remove(ruleQuery);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DynamicRule that = (DynamicRule) o;
        return Objects.equals(productName, that.productName) &&
                Objects.equals(productId, that.productId) &&
                Objects.equals(productText, that.productText) &&
                Objects.equals(rules, that.rules);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productName, productId, productText, rules);
    }

    @Override
    public String toString() {
        return "DynamicRule{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", productId=" + productId +
                ", productText='" + productText + '\'' +
                ", rules=" + rules +
                ", createdAt=" + createdAt +
                '}';
    }
}


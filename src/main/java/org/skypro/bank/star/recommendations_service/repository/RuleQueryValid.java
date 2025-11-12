package org.skypro.bank.star.recommendations_service.repository;

import jakarta.validation.Constraint;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RuleQueryValidator.class)
public @interface RuleQueryValid {
    String message() default "Некорректные аргументы в RuleQueryDTO";
}
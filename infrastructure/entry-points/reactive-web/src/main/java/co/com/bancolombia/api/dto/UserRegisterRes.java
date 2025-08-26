package co.com.bancolombia.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UserRegisterRes(Long userId,
                              String name,
                              String lastName,
                              LocalDate birthDate,
                              String address,
                              String email,
                              BigDecimal baseSalary,
                              String phoneNumber) {
}

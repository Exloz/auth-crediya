package co.com.bancolombia.api.dto;

import java.math.BigDecimal;
import java.util.Date;

public record UserRegisterRes(String userId,
                              String name,
                              String lastName,
                              Date birthDate,
                              String address,
                              String email,
                              BigDecimal baseSalary,
                              String phoneNumber) {
}

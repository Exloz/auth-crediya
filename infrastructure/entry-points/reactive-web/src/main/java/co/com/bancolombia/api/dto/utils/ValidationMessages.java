package co.com.bancolombia.api.dto.utils;

public final class ValidationMessages {

    private ValidationMessages() {
        throw new IllegalStateException("Utility class");
    }

    public static final String NAME_REQUIRED = "Name is required and cannot be blank";
    public static final String LASTNAME_REQUIRED = "Lastname is required and cannot be blank";
    public static final String EMAIL_REQUIRED = "Email is required and cannot be blank";
    public static final String ID_DOCUMENT_REQUIRED = "Document id is required and cannot be blank";
    public static final String PHONE_REQUIRED = "Phone number is required and cannot be blank";
    public static final String BIRTH_DATE_REQUIRED = "Birth date is required";
    public static final String BASE_SALARY_REQUIRED = "Base salary is required";

    public static final String EMAIL_INVALID_FORMAT = "Invalid email format";

    public static final String BASE_SALARY_MIN_VALUE = "Base salary must be greater than 0";
    public static final String BASE_SALARY_MAX_VALUE = "Base salary cannot exceed 15,000,000";
}

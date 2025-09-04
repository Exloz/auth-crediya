package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.register.UserRegisterReq;
import co.com.bancolombia.api.dto.register.UserRegisterRes;
import co.com.bancolombia.api.dto.login.LoginReq;
import co.com.bancolombia.api.dto.login.LoginRes;
import co.com.bancolombia.api.dto.user.UserInfoRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    @RouterOperations({
            @RouterOperation( path = "/api/v1/usuarios",
                    produces = { MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.POST, beanClass = Handler.class, beanMethod = "listenCreateUser",
                    operation = @Operation( operationId = "createUser",
                            summary = "Register user",
                            description = "Registers a new user. Role is optional and validated in the use case. If a valid role is provided (USER, ADMIN, ADVISOR), it will be applied. If omitted or blank, the role is inferred by business rules: emails ending with @crediya.com become ADMIN; otherwise USER.",
                            requestBody = @RequestBody(
                                description = "Data of the client to register",
                                required = true,
                                content = @Content(schema = @Schema(implementation = UserRegisterReq.class))
                            ),
                            responses = {
                                @ApiResponse(responseCode = "201", description = "User created successfully",
                                    content = @Content(schema = @Schema(implementation = UserRegisterRes.class))),
                                @ApiResponse(responseCode = "400", description = "Invalid input data"),
                                @ApiResponse(responseCode = "409", description = "The email is already registered"),
                                @ApiResponse(responseCode = "500", description = "Internal server error")
                            })
            ),
            @RouterOperation( path = "/api/v1/login",
                    produces = { MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.POST, beanClass = Handler.class, beanMethod = "listenLogin",
                    operation = @Operation( operationId = "login",
                            summary = "Login",
                            description = "Authenticates a user by email and password (any role)",
                            requestBody = @RequestBody(
                                description = "Login credentials",
                                required = true,
                                content = @Content(schema = @Schema(implementation = LoginReq.class))
                            ),
                             responses = {
                                 @ApiResponse(responseCode = "200", description = "Login successful",
                                     content = @Content(schema = @Schema(implementation = LoginRes.class))),
                                 @ApiResponse(responseCode = "400", description = "Invalid input data"),
                                 @ApiResponse(responseCode = "401", description = "Invalid credentials"),
                                 @ApiResponse(responseCode = "500", description = "Internal server error")
                             })
             ),
             @RouterOperation( path = "/api/v1/usuarios/{userId}",
                     produces = { MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.GET, beanClass = Handler.class, beanMethod = "listenGetUserByIdDocument",
                     operation = @Operation( operationId = "getUserById",
                             summary = "Get user information by ID",
                             description = "Retrieves complete user information by ID number. Requires ADMIN or ADVISOR role.",
                             parameters = {
                                 @Parameter(name = "userId", description = "User's ID number", required = true, example = "1234567890")
                             },
                             responses = {
                                 @ApiResponse(responseCode = "200", description = "User information retrieved successfully",
                                     content = @Content(schema = @Schema(implementation = UserInfoRes.class))),
                                 @ApiResponse(responseCode = "404", description = "User not found"),
                                 @ApiResponse(responseCode = "403", description = "Access denied - insufficient privileges"),
                                 @ApiResponse(responseCode = "500", description = "Internal server error")
                             })
             )})
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/usuarios"), handler::listenCreateUser)
                .andRoute(POST("/api/v1/login"), handler::listenLogin)
                .andRoute(GET("/api/v1/usuarios/{userId}"), handler::listenGetUserById);
    }
}

package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.UserRegisterReq;
import co.com.bancolombia.api.dto.AdminUserRegisterReq;
import co.com.bancolombia.api.dto.UserRegisterRes;
import io.swagger.v3.oas.annotations.Operation;
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

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    @RouterOperations({
            @RouterOperation( path = "/api/v1/usuarios",
                    produces = { MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.POST, beanClass = Handler.class, beanMethod = "listenCreateUser",
                    operation = @Operation( operationId = "createUser",
                            summary = "Register client user",
                            description = "Registers a new client user (role USER) in the system",
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
            @RouterOperation( path = "/api/v1/admin/usuarios",
                    produces = { MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.POST, beanClass = Handler.class, beanMethod = "listenRegisterPrivilegedUser",
                    operation = @Operation( operationId = "createPrivilegedUser",
                            summary = "Register privileged user",
                            description = "Registers a new privileged user (role ADMIN or ASESOR) in the system",
                            requestBody = @RequestBody(
                                description = "Data of the privileged user to register",
                                required = true,
                                content = @Content(schema = @Schema(implementation = AdminUserRegisterReq.class))
                            ),
                            responses = {
                                @ApiResponse(responseCode = "201", description = "User created successfully",
                                    content = @Content(schema = @Schema(implementation = UserRegisterRes.class))),
                                @ApiResponse(responseCode = "400", description = "Invalid input data or role"),
                                @ApiResponse(responseCode = "409", description = "The email is already registered"),
                                @ApiResponse(responseCode = "500", description = "Internal server error")
                            })
            )})
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/usuarios"), handler::listenCreateUser)
                .andRoute(POST("/api/v1/admin/usuarios"), handler::listenRegisterPrivilegedUser);
    }
}

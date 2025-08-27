package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.UserRegisterReq;
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
                            summary = "Registrar usuario",
                            description = "Registra un nuevo usuario en el sistema",
                            requestBody = @RequestBody(
                                description = "Datos del usuario a registrar",
                                required = true,
                                content = @Content(schema = @Schema(implementation = UserRegisterReq.class))
                            ),
                            responses = {
                                @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                                    content = @Content(schema = @Schema(implementation = UserRegisterRes.class))),
                                @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
                                @ApiResponse(responseCode = "409", description = "El email ya está registrado"),
                                @ApiResponse(responseCode = "500", description = "Error interno del servidor")
                            })
            )})
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/usuarios"), handler::listenCreateUser);
    }
}

package net.krusher.datalinks.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import net.krusher.datalinks.application.handler.user.ActivateUserCommandHandler;
import net.krusher.datalinks.application.handler.user.ChangePasswordCommand;
import net.krusher.datalinks.application.handler.user.ChangePasswordCommandHandler;
import net.krusher.datalinks.application.handler.user.GetUserByLoginTokenCommand;
import net.krusher.datalinks.application.handler.user.GetUserByLoginTokenCommandHandler;
import net.krusher.datalinks.application.handler.user.GetUserCommand;
import net.krusher.datalinks.application.handler.user.GetUserCommandHandler;
import net.krusher.datalinks.application.handler.user.LoginCommandHandler;
import net.krusher.datalinks.application.handler.user.RequestResetUserCommandHandler;
import net.krusher.datalinks.application.handler.user.ResetPasswordCommandHandler;
import net.krusher.datalinks.application.handler.user.SignupCommandHandler;
import net.krusher.datalinks.domain.exception.EngineException;
import net.krusher.datalinks.domain.model.user.LoginToken;
import net.krusher.datalinks.web.common.CaptchaHelper;
import net.krusher.datalinks.web.mapper.LoginCommandMapper;
import net.krusher.datalinks.web.mapper.RequestResetUserCommandMapper;
import net.krusher.datalinks.web.mapper.SignupCommandMapper;
import net.krusher.datalinks.web.model.LoginModel;
import net.krusher.datalinks.web.model.PasswordResetRequestModel;
import net.krusher.datalinks.web.model.SignupModel;

import java.util.Optional;
import java.util.UUID;

import static net.krusher.datalinks.web.common.ControllerUtil.AUTH_HEADER;
import static net.krusher.datalinks.web.common.ControllerUtil.remoteAddr;
import static net.krusher.datalinks.web.common.ControllerUtil.toLoginToken;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@AllArgsConstructor(onConstructor_ = @Inject)
public class UserController {

    private final GetUserCommandHandler getUserCommandHandler;
    private final GetUserByLoginTokenCommandHandler getUserByLoginTokenCommandHandler;
    private final LoginCommandHandler loginCommandHandler;
    private final SignupCommandHandler signupCommandHandler;
    private final SignupCommandMapper signupCommandMapper;
    private final LoginCommandMapper loginCommandMapper;
    private final RequestResetUserCommandMapper requestResetUserCommandMapper;
    private final ObjectMapper objectMapper;
    private final CaptchaHelper captchaHelper;
    private final ActivateUserCommandHandler activateUserCommandHandler;
    private final ResetPasswordCommandHandler resetPasswordCommandHandler;
    private final RequestResetUserCommandHandler requestResetUserCommandHandler;
    private final ChangePasswordCommandHandler changePasswordCommandHandler;
    @GET
    @Path("{name}/get")
    public Response get(@PathParam("name") String name, @HeaderParam(AUTH_HEADER) String userToken) {
        return getUserCommandHandler.handler(GetUserCommand.builder().username(name).loginToken(toLoginToken(userToken)).build())
                .map(u -> Response.ok(u).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("{loginToken}/byLoginToken")
    public Response getByLoginToken(@PathParam("loginToken") String loginToken) {
        return getUserByLoginTokenCommandHandler.handler(GetUserByLoginTokenCommand.builder().loginToken(toLoginToken(loginToken)).build())
                .map(u -> Response.ok(u).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("{token}/activate")
    public Response activate(@PathParam("token") String token) {
        return Try.run(() -> activateUserCommandHandler.handler(UUID.fromString(token)))
                .map(e -> Response.ok("OK").build())
                .recover(EngineException.class, e -> Response.status(Response.Status.BAD_REQUEST).entity("Request failed").build())
                .get();
    }

    @GET
    @Path("{token}/reset")
    public Response reset(@PathParam("token") String token) {
        return Try.run(() -> resetPasswordCommandHandler.handler(UUID.fromString(token)))
                .map(e -> Response.ok("OK").build())
                .recover(EngineException.class, e -> Response.status(Response.Status.BAD_REQUEST).entity("Request failed").build())
                .get();
    }

    @POST
    @Path("/passwordChange")
    @Consumes(MediaType.WILDCARD)
    public Response passwordChange(String body, @HeaderParam(AUTH_HEADER) String userToken) {
        return Try.run(() -> changePasswordCommandHandler.handler(ChangePasswordCommand.builder()
                .password(body)
                .loginToken(toLoginToken(userToken))
                .build()))
                .map(e -> Response.ok("OK").build())
                .recover(EngineException.class, e -> Response.status(Response.Status.BAD_REQUEST).entity("Request failed").build())
                .get();
    }

    @POST
    @Path("/requestReset")
    @Consumes(MediaType.WILDCARD)
    public Response requestReset(String body, @Context RoutingContext routingContext) throws JsonProcessingException {
        PasswordResetRequestModel passwordResetRequestModel = objectMapper.readValue(body, PasswordResetRequestModel.class);
        if (!captchaHelper.checkCaptcha(passwordResetRequestModel.getCaptcha(), remoteAddr(routingContext))) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Captcha is invalid").build();
        }
        return Try.run(() -> requestResetUserCommandHandler.handler(requestResetUserCommandMapper.toCommand(passwordResetRequestModel)))
                .map(_ -> Response.ok("OK").build())
                .recover(EngineException.class, e -> Response.status(Response.Status.BAD_REQUEST).entity("Request failed").build())
                .get();
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.WILDCARD)
    public Response login(String body) throws JsonProcessingException {
        LoginModel loginModel = objectMapper.readValue(body, LoginModel.class);
        Optional<LoginToken> loginToken = loginCommandHandler.handler(loginCommandMapper.toCommand(loginModel));
        return loginToken.map(token -> Response.ok(token.getLoginToken()).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Path("/signup")
    @Consumes(MediaType.WILDCARD)
    public Response signup(String body, @Context RoutingContext routingContext) throws JsonProcessingException {
        SignupModel signupModel = objectMapper.readValue(body, SignupModel.class);
        if (!captchaHelper.checkCaptcha(signupModel.getCaptcha(), remoteAddr(routingContext))) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Captcha is invalid").build();
        }
        return Try.run(() -> signupCommandHandler.handle(signupCommandMapper.toCommand(signupModel)))
                .map(e -> Response.ok("OK").build())
                .recover(EngineException.class, e -> Response.status(Response.Status.BAD_REQUEST).entity("Request failed").build())
                .get();
    }
}

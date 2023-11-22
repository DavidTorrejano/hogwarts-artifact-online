package com.edu.hogwartsartifactonline.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class CustomBasicAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final HandlerExceptionResolver resolver;

    /**
     *
     * @param resolver
     * @Qualifier esta sentencia es usada para especificar cuál bean en especial se va a usar, en situaciones donde
     * varios beans implementan la misma interfaz, de esta forma, se provee el nombre del bean necesitado.
     */
    public CustomBasicAuthenticationEntryPoint(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        response.addHeader("WWW-Authenticate", "Basic realm=\"Realm\"");
        resolver.resolveException(request, response, null, authException);
    }
}

package com.creatival;

import java.io.IOException;
import java.net.URLEncoder;

import org.springframework.security.core.AuthenticationException;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		String errorMessage = "아이디 또는 비밀번호가 잘못되었습니다.";

        if (exception instanceof DisabledException) {
        	errorMessage = "비활성화된 회원입니다.";
        	response.sendRedirect("/user/login?error=disabled&message=" +
        	URLEncoder.encode(errorMessage, "UTF-8"));
            return;
        }

        response.sendRedirect("/user/login?error=true&message=" +
        URLEncoder.encode(errorMessage, "UTF-8"));
	}
}

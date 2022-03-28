package ru.otus.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import ru.otus.services.TemplateProcessor;
import ru.otus.services.UserAuthService;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

public class LoginServlet extends HttpServlet {

    private static final String PARAM_LOGIN = "login";
    private static final String PARAM_PASSWORD = "password";
    private static final int MAX_INACTIVE_INTERVAL = 300;
    private static final String LOGIN_PAGE_TEMPLATE = "login.html";

    private final TemplateProcessor templateProcessor;
    private final UserAuthService userAuthService;

    public LoginServlet(TemplateProcessor templateProcessor, UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
        this.templateProcessor = templateProcessor;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        var paramsMap = getParamMapFromRequest(request);

        response.setContentType("text/html");
        response.getWriter().println(templateProcessor.getPage(LOGIN_PAGE_TEMPLATE, paramsMap));

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        String name = request.getParameter(PARAM_LOGIN);
        String password = request.getParameter(PARAM_PASSWORD);

        if (userAuthService.authenticate(name, password)) {
            HttpSession session = request.getSession();
            session.setMaxInactiveInterval(MAX_INACTIVE_INTERVAL);

            putIntoResponseAuthenticateCookie(response, true);

            response.sendRedirect("/clients");

        } else {

            putIntoResponseAuthenticateCookie(response, false);

            response.setStatus(SC_UNAUTHORIZED);
            response.sendRedirect("/login");

        }

    }

    //region Service

    private void putIntoResponseAuthenticateCookie(HttpServletResponse response, boolean success){

        var cookie = new Cookie("WRONG_PASSWORD", success? "": "WRONG_PASSWORD");
        cookie.setMaxAge( success? 0: 2);

        response.addCookie(cookie);

    }

    private Map<String, Object> getParamMapFromRequest(HttpServletRequest request) {

        Map<String, Object> paramsMap = new HashMap<>();

        var unsuccessfulAuthorized = false;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (var cookie : request.getCookies()) {
                if (cookie.getName().equals("WRONG_PASSWORD")) {
                    unsuccessfulAuthorized = true;
                }
            }
        }

        paramsMap.put("showPasswordWarning", unsuccessfulAuthorized);

        return paramsMap;
    }

    //endregion

}

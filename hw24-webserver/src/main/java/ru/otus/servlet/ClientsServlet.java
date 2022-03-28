package ru.otus.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.services.TemplateProcessor;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientsServlet extends HttpServlet {

    private static final String TEMPLATE_ATTR_CLIENTS = "clients";
    private static final String CLIENTS_PAGE_TEMPLATE = "clients.html";

    private final TemplateProcessor templateProcessor;
    private final DBServiceClient dbServiceClient;

    public ClientsServlet(TemplateProcessor templateProcessor, DBServiceClient dbServiceClient) {
        this.templateProcessor = templateProcessor;
        this.dbServiceClient = dbServiceClient;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        var paramsMap = getParamMapFromRequest(req);
        paramsMap.put(TEMPLATE_ATTR_CLIENTS, dbServiceClient.findAll());

        resp.setContentType("text/html");
        resp.getWriter().println(templateProcessor.getPage(CLIENTS_PAGE_TEMPLATE, paramsMap));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String clientName = req.getParameter("name");
            String clientAddress = req.getParameter("address");
            String clientPhone = req.getParameter("phone");

            if (clientName.isEmpty()){
                putIntoResponseErrorClientCreatingCookie(resp, false);
            } else {
                var findedClients = dbServiceClient.findByField("name", clientName);
                if (findedClients.size() != 0){
                    putIntoResponseErrorClientCreatingCookie(resp, false);
                } else {
                    addClient(clientName, clientAddress, clientPhone);
                    putIntoResponseErrorClientCreatingCookie(resp, true);
                }
            }
        } catch (Exception e){
            putIntoResponseErrorClientCreatingCookie(resp, false);
        }

        resp.sendRedirect("/clients");
    }

    //region Service

    private void addClient(String clientName, String clientAddress, String clientPhone) {

        var phone = new Phone();
        phone.setNumber(clientPhone);

        var client = new Client(clientName, new Address(clientAddress), List.of(phone));
        phone.setClient(client);

        dbServiceClient.saveClient(client);

    }

    private void putIntoResponseErrorClientCreatingCookie(HttpServletResponse response, boolean success){

        var cookie = new Cookie("CREATING_CLIENT_ERROR", success? "": "CREATING_CLIENT_ERROR");
        cookie.setMaxAge( success? 0: 2);

        response.addCookie(cookie);

    }

    private Map<String, Object> getParamMapFromRequest(HttpServletRequest request) {

        Map<String, Object> paramsMap = new HashMap<>();

        var unsuccessfulCreatingClient = false;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (var cookie : request.getCookies()) {
                if (cookie.getName().equals("CREATING_CLIENT_ERROR")) {
                    unsuccessfulCreatingClient = true;
                }
            }
        }

        paramsMap.put("showClientAddingError", unsuccessfulCreatingClient);

        return paramsMap;
    }

    //endregion

}

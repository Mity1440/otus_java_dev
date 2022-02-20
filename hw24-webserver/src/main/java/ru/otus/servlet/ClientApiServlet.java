package ru.otus.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.services.TemplateProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientApiServlet extends HttpServlet{

    private final DBServiceClient dbServiceClient;

    public ClientApiServlet(DBServiceClient dbServiceClient) {
        this.dbServiceClient = dbServiceClient;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            String clientName = req.getParameter("name");
            String clientAddress = req.getParameter("address");
            String clientPhone = req.getParameter("phone");

            if (clientName.isEmpty()){
                setResponseBodyAsPlainText(resp, "Не указано имя клиента");
                resp.setStatus(400);
            } else {
                var findedClients = dbServiceClient.findByField("name", clientName);
                if (findedClients.size() != 0){
                    setResponseBodyAsPlainText(resp, "Клиент с таким именем уже существует");
                    resp.setStatus(400);
                } else {
                    addClient(clientName, clientAddress, clientPhone);
                    resp.setStatus(201);
                }
            }
        } catch (Exception e){
            setResponseBodyAsPlainText(resp, "Возникла неисправимая ошибка при заведении клиента");
            resp.setStatus(500);
        }

    }

    private void addClient(String clientName, String clientAddress, String clientPhone) {

        var phone = new Phone();
        phone.setNumber(clientPhone);

        var client = new Client(clientName, new Address(clientAddress), List.of(phone));
        phone.setClient(client);

        dbServiceClient.saveClient(client);

    }

    private void setResponseBodyAsPlainText(HttpServletResponse resp, String message) throws IOException {

        resp.setContentType("application/text");
        resp.setCharacterEncoding("UTF-8");

        var respWriter = resp.getWriter();

        respWriter.println(message);

    }
}

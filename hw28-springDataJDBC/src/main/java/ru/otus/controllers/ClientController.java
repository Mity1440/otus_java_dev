package ru.otus.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.view.RedirectView;

import ru.otus.services.ClientService;
import ru.otus.services.view.ClientView;

@Controller
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping({"/", "/client/list"})
    public String clientsListView(Model model) {

        var clients = clientService.findAll();
        model.addAttribute("clients", clients);
        model.addAttribute("client", new ClientView());

        return "clientsList";
    }

    @PostMapping("/client/save")
    public RedirectView clientSave(@ModelAttribute ClientView client) {
        clientService.save(client);
        return new RedirectView("/", true);
    }

}

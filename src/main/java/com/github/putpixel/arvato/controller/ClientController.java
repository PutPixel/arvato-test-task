package com.github.putpixel.arvato.controller;

import java.time.YearMonth;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.putpixel.arvato.external.ParkingInfoProvider;
import com.github.putpixel.arvato.model.Client;
import com.github.putpixel.arvato.model.TypedId;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private static final Logger LOG = LoggerFactory.getLogger(ClientController.class);

    @Autowired
    private ParkingInfoProvider info;

    @RequestMapping(method = RequestMethod.GET)
    public List<Client> getClients() {
        LOG.debug("All clients retrived");
        return info.getClients();
    }

    @RequestMapping("/parkedAtMonthes/{clientId}")
    public List<YearMonth> parkedAtMonthes(@PathVariable String clientId) {
        LOG.debug("Retrived parking monthes for client '{}'", clientId);
        TypedId<Client> id = new TypedId<>(clientId, Client.class);
        return info.getParkingInfoMonthesForClient(id);
    }
}

package com.github.putpixel.arvato.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.putpixel.arvato.external.ParkingInfoProvider;
import com.github.putpixel.arvato.logic.ParkingInfoCalculator;
import com.github.putpixel.arvato.model.Car;
import com.github.putpixel.arvato.model.Client;
import com.github.putpixel.arvato.model.ParkingInfo;
import com.github.putpixel.arvato.model.TypedId;
import com.github.putpixel.arvato.templates.TemplateProcessor;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

@Controller
@RequestMapping("/parking-info")
public class ParkingInfoController {

    private static final Logger LOG = LoggerFactory.getLogger(ParkingInfoController.class);

    @Autowired
    private ParkingInfoProvider info;

    @Autowired
    private TemplateProcessor template;

    @SuppressWarnings("unused")
    private static class InvoiceDTO {
        int number = positive(new Random().nextInt());
        String monthFee;
        String reduction;
        String total;

        private int positive(int nextInt) {
            return nextInt > 0 ? nextInt : -nextInt;
        }
    }

    @RequestMapping(value = "/{clientId}/{year}/{month}/{invoice}")
    public @ResponseBody String parkingInvoicePage(
            @PathVariable String clientId, @PathVariable int year, @PathVariable int month, @PathVariable String invoice) {
        LOG.debug("Requested invoice for client '{}', period '{}-{}'", clientId, year, month);
        TypedId<Client> id = new TypedId<>(clientId, Client.class);
        Client client = info.getClient(id).get();
        YearMonth period = YearMonth.of(year, month);
        ParkingInfoCalculator calculator = new ParkingInfoCalculator(client);
        List<ParkingInfo> infosRaw = info.getParkingInfoForClient(id, period);

        String dtoAsJson = createInfoDTOs(id, calculator, infosRaw);
        String invoiceJson = invoiceAsJson(calculator, infosRaw);
        LOG.trace("Requested invoice for client '{}', period '{}-{}', returned json: \n\n{}\n\n{}", clientId, year, month, dtoAsJson, invoiceJson);

        return template.generateFromTemplate("templates/parking-info.html",
                ImmutableMap.of(
                        "clientName", client.getName(),
                        "period", period,
                        "invoice", invoiceJson,
                        "parkingInfoJson", dtoAsJson));
    }

    private String invoiceAsJson(ParkingInfoCalculator calculator, List<ParkingInfo> infosRaw) {
        BigDecimal fee = calculator.calculateMonthlyFee(infosRaw);
        BigDecimal reduction = calculator.calculateReduction(infosRaw);
        BigDecimal total = calculator.calculateTotal(infosRaw);

        InvoiceDTO invoice = new InvoiceDTO();
        invoice.monthFee = fee.toPlainString();
        invoice.reduction = reduction.toPlainString();
        invoice.total = total.toPlainString();

        return new Gson().toJson(invoice);
    }

    @RequestMapping(value = "/{clientId}/{year}/{month}/")
    public @ResponseBody String parkingInfoPage(@PathVariable String clientId, @PathVariable int year, @PathVariable int month) {
        LOG.debug("Requested parking info for client '{}', period '{}-{}'", clientId, year, month);
        TypedId<Client> id = new TypedId<>(clientId, Client.class);
        Client client = info.getClient(id).get();
        YearMonth period = YearMonth.of(year, month);
        ParkingInfoCalculator calculator = new ParkingInfoCalculator(client);
        List<ParkingInfo> infosRaw = info.getParkingInfoForClient(id, period);

        String dtoAsJson = createInfoDTOs(id, calculator, infosRaw);
        LOG.trace("Requested parking info for client '{}', period '{}-{}', returned json: \n\n{}", clientId, year, month, dtoAsJson);

        return template.generateFromTemplate("templates/parking-info.html",
                ImmutableMap.of(
                        "clientName", client.getName(),
                        "period", period,
                        "invoice", "null",
                        "parkingInfoJson", dtoAsJson));
    }

    @SuppressWarnings("unused")
    private static class ParkingInfoDTO {
        String alias;
        String start;
        String end;
        String cost;
    }

    private String createInfoDTOs(TypedId<Client> id, ParkingInfoCalculator calculator, List<ParkingInfo> infosRaw) {
        Map<ParkingInfo, BigDecimal> costs = calculator.calculateBreakdown(infosRaw);
        Map<String, Car> carIdToCar = info.getCars(id).stream().collect(Collectors.toMap(Car::getIdentifier, Function.identity()));

        List<ParkingInfoDTO> dtos = infosRaw.stream().map(raw -> {
            ParkingInfoDTO dto = new ParkingInfoDTO();
            dto.alias = carIdToCar.get(raw.getParkedCar().getIdentifier()).getAlias();
            dto.start = format(raw.getParkedAt());
            dto.end = format(raw.getLeftParkingAt());
            dto.cost = costs.get(raw).toPlainString();
            return dto;
        }).collect(Collectors.toList());

        return new Gson().toJson(dtos);
    }

    private String format(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}

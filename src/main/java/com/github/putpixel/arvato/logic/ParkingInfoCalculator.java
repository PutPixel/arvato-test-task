package com.github.putpixel.arvato.logic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.putpixel.arvato.model.Client;
import com.github.putpixel.arvato.model.ParkingInfo;
import com.google.common.base.Preconditions;

public class ParkingInfoCalculator {

    private static final Duration PRICING_INTERVAL = Duration.ofMinutes(30);

    private final Client client;

    private final BigDecimal dayFee;

    private final BigDecimal nightFee;

    private final BigDecimal maxInvoiceValue;

    public ParkingInfoCalculator(Client client) {
        this(client, null);
    }

    ParkingInfoCalculator(Client client, BigDecimal maxInvoiceValue) {
        this.client = client;
        if (client.isPremium()) {
            dayFee = new BigDecimal("1");
            nightFee = new BigDecimal("0.75");
            if (maxInvoiceValue == null) {
                maxInvoiceValue = new BigDecimal("300");
            }
        }
        else {
            dayFee = new BigDecimal("1.5");
            nightFee = new BigDecimal("1");
        }
        this.maxInvoiceValue = maxInvoiceValue;
    }

    public Map<ParkingInfo, BigDecimal> calculateBreakdown(List<ParkingInfo> infos) {
        return infos.stream().collect(Collectors.toMap(Function.identity(), this::calculateForSingleItem));
    }

    private BigDecimal calculateForSingleItem(ParkingInfo info) {
        LocalDateTime parkedAt = info.getParkedAt();
        LocalDateTime leftParkingAt = info.getLeftParkingAt();
        Preconditions.checkState(parkedAt.isBefore(leftParkingAt));

        LocalDateTime currentPeriod = parkedAt;
        BigDecimal amount = BigDecimal.ZERO;
        while (currentPeriod.isBefore(leftParkingAt)) {
            if (isDayTime(currentPeriod)) {
                amount = amount.add(dayFee);
            }
            else {
                amount = amount.add(nightFee);
            }
            currentPeriod = currentPeriod.plus(PRICING_INTERVAL);
        }

        return scale(amount);
    }

    private boolean isDayTime(LocalDateTime p) {
        LocalTime localTime = p.toLocalTime();
        return localTime.getHour() >= 7 && localTime.getHour() <= 18;
    }

    public BigDecimal calculateTotal(List<ParkingInfo> infos) {
        return scale(calculateTotalByElementsAndMonth(infos).subtract(calculateReduction(infos)));
    }

    public BigDecimal calculateReduction(List<ParkingInfo> infos) {
        if (client.isPremium()) {
            BigDecimal totalByElements = calculateTotalByElementsAndMonth(infos);
            if (totalByElements.compareTo(maxInvoiceValue) > 0) {
                return scale(totalByElements.subtract(maxInvoiceValue));
            }
        }
        return scale(BigDecimal.ZERO);
    }

    private BigDecimal calculateTotalByElementsAndMonth(List<ParkingInfo> infos) {
        return calculateBreakdown(infos).values().stream().reduce(BigDecimal.ZERO, (v1, v2) -> v1.add(v2)).add(calculateMonthlyFee(infos));
    }

    public BigDecimal calculateMonthlyFee(List<ParkingInfo> infos) {
        return client.isPremium() ? scale(new BigDecimal("20")) : scale(BigDecimal.ZERO);
    }

    private BigDecimal scale(BigDecimal v) {
        return v.setScale(2, RoundingMode.CEILING);
    }

}

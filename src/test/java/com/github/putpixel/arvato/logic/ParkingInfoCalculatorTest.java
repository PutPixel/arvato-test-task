package com.github.putpixel.arvato.logic;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.putpixel.arvato.Application;
import com.github.putpixel.arvato.external.ParkingInfoProvider;
import com.github.putpixel.arvato.logic.ParkingInfoCalculator;
import com.github.putpixel.arvato.model.Client;
import com.github.putpixel.arvato.model.ParkingInfo;
import com.github.putpixel.arvato.model.TypedId;
import com.google.common.collect.ImmutableList;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class ParkingInfoCalculatorTest extends Matchers {

    private static final YearMonth PERIOD = YearMonth.of(2016, 1);

    private static final BigDecimal MAX_INVOICE_VALUE = new BigDecimal("10");

    @Autowired
    private ParkingInfoProvider provider;

    @Test
    public void test_month_fee_not_premium() {
        BigDecimal monthlyFee = usualClientCalc().calculateMonthlyFee(ImmutableList.of());
        assertTrue(monthlyFee.compareTo(BigDecimal.ZERO) == 0);
    }

    private ParkingInfoCalculator usualClientCalc() {
        ParkingInfoCalculator calculatorNotPremium = new ParkingInfoCalculator(new Client(), MAX_INVOICE_VALUE);
        return calculatorNotPremium;
    }

    @Test
    public void test_month_fee_premium() {
        BigDecimal monthlyFee = premiumClientCalc().calculateMonthlyFee(ImmutableList.of());
        assertEqual(monthlyFee, new BigDecimal("20"));
    }

    private void assertEqual(BigDecimal v1, BigDecimal v2) {
        assertTrue(v1 + "!=" + v2, v1.compareTo(v2) == 0);
    }

    private ParkingInfoCalculator premiumClientCalc() {
        Client client = new Client();
        client.setPremium(true);
        ParkingInfoCalculator calculatorNotPremium = new ParkingInfoCalculator(client, MAX_INVOICE_VALUE);
        return calculatorNotPremium;
    }

    @Test
    public void calculate_not_premium_breakdown() {
        TypedId<Client> client = new TypedId<>("test2", Client.class);
        List<ParkingInfo> infos = provider.getParkingInfoForClient(client, PERIOD);
        assertThat(infos, hasSize(2));
        Map<ParkingInfo, BigDecimal> calculateBreakdown = usualClientCalc().calculateBreakdown(infos);
        assertThat(calculateBreakdown.values(), hasSize(2));
        assertNotNull(calculateBreakdown);
        assertEqual(calculateBreakdown.get(infos.get(0)), new BigDecimal("9.00"));
        assertEqual(calculateBreakdown.get(infos.get(1)), new BigDecimal("2.00"));
    }

    @Test
    public void calculate_not_premium_total() {
        TypedId<Client> client = new TypedId<>("test2", Client.class);
        List<ParkingInfo> infos = provider.getParkingInfoForClient(client, PERIOD);
        BigDecimal total = usualClientCalc().calculateTotal(infos);
        assertEqual(total, new BigDecimal("11.00"));
    }

    @Test
    public void calculate_not_premium_reduction() {
        TypedId<Client> client = new TypedId<>("test2", Client.class);
        List<ParkingInfo> infos = provider.getParkingInfoForClient(client, PERIOD);
        BigDecimal total = usualClientCalc().calculateReduction(infos);
        assertEqual(total, BigDecimal.ZERO);
    }

    @Test
    public void calculate_premium_breakdown() {
        TypedId<Client> client = new TypedId<>("test1", Client.class);
        List<ParkingInfo> infos = provider.getParkingInfoForClient(client, PERIOD);
        assertThat(infos, hasSize(4));

        Map<ParkingInfo, BigDecimal> calculateBreakdown = premiumClientCalc().calculateBreakdown(infos);
        assertNotNull(calculateBreakdown);

        assertEqual(calculateBreakdown.get(infos.get(0)), new BigDecimal("6.00"));
        assertEqual(calculateBreakdown.get(infos.get(1)), new BigDecimal("10.00"));
        assertEqual(calculateBreakdown.get(infos.get(2)), new BigDecimal("0.75"));
        assertEqual(calculateBreakdown.get(infos.get(3)), new BigDecimal("1.50"));
    }

    @Test
    public void calculate_premium_reduction() {
        TypedId<Client> client = new TypedId<>("test1", Client.class);
        List<ParkingInfo> infos = provider.getParkingInfoForClient(client, PERIOD);
        BigDecimal total = premiumClientCalc().calculateReduction(infos);
        assertEqual(total, new BigDecimal("28.25"));
    }

    @Test
    public void calculate_premium_total() {
        TypedId<Client> client = new TypedId<>("test1", Client.class);
        List<ParkingInfo> infos = provider.getParkingInfoForClient(client, PERIOD);
        BigDecimal total = premiumClientCalc().calculateTotal(infos);
        assertEqual(total, new BigDecimal("10.00"));
    }

}

package com.github.putpixel.arvato.external.mock;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.putpixel.arvato.Application;
import com.github.putpixel.arvato.external.mock.ParkingInfoProviderMock;
import com.github.putpixel.arvato.model.Car;
import com.github.putpixel.arvato.model.Client;
import com.github.putpixel.arvato.model.ParkingInfo;
import com.github.putpixel.arvato.model.TypedId;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class ParkingInfoProviderMockTest {

    @Autowired
    private ParkingInfoProviderMock mock;

    @Test
    public void test_all_clients() {
        List<Client> retriveAllClients = mock.getClients();
        assertNotNull(retriveAllClients);
        assertTrue(!retriveAllClients.isEmpty());
        retriveAllClients.forEach(this::assertHasNoEmptyFields);
    }

    private void assertHasNoEmptyFields(Client c) {
        assertNotNull(c.getIdentifier());
        assertNotNull(c.getName());
    }

    @Test
    public void test_cars_for_client_that_has_more_then_one() {
        List<Client> clients = mock.getClients();
        Client hasTwoCars = clients.get(0);
        List<Car> cars = mock.getCars(TypedId.of(hasTwoCars));
        assertNotNull(cars);
        assertTrue(cars.size() == 2);
    }

    @Test
    public void test_cars_has_correct_client_ids() {
        List<Client> clients = mock.getClients();

        for (Client client : clients) {
            TypedId<Client> clientId = TypedId.of(client);
            List<Car> cars = mock.getCars(clientId);
            assertNotNull(cars);
            for (Car car : cars) {
                assertNotNull(car.getAlias());
                assertNotNull(car.getPlate());
                assertEquals(clientId, car.getClientIdentifier());
            }
        }
    }

    @Test
    public void test_parking_info_monthes() {
        TypedId<Client> client = new TypedId<>("test1", Client.class);

        List<YearMonth> parkingInfoForClient = mock.getParkingInfoMonthesForClient(client);
        assertNotNull(parkingInfoForClient);
        assertFalse(parkingInfoForClient.isEmpty());
        assertThat(parkingInfoForClient, contains(YearMonth.of(2016, 1), YearMonth.of(2016, 2)));
    }

    @Test
    public void test_parking_info_range() {
        YearMonth period = YearMonth.of(2016, 1);
        LocalDate from = period.atDay(1);
        LocalDate to = period.atEndOfMonth();
        TypedId<Client> client = new TypedId<>("test1", Client.class);

        List<ParkingInfo> parkingInfoForClient = mock.getParkingInfoForClient(client, period);
        assertNotNull(parkingInfoForClient);
        assertFalse(parkingInfoForClient.isEmpty());

        Set<TypedId<Car>> carIds = mock.getCars(client).parallelStream().map(TypedId::of).collect(Collectors.toSet());

        for (ParkingInfo parkingInfo : parkingInfoForClient) {
            assertTrue(carIds.contains(parkingInfo.getParkedCar()));
            assertTrue(parkingInfo.getParkedAt().isAfter(from.atStartOfDay()));
            assertTrue(parkingInfo.getParkedAt().isBefore(to.atTime(LocalTime.MAX)));
        }
    }
}

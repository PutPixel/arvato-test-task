package com.github.putpixel.arvato.external;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import com.github.putpixel.arvato.model.Car;
import com.github.putpixel.arvato.model.Client;
import com.github.putpixel.arvato.model.ParkingInfo;
import com.github.putpixel.arvato.model.TypedId;

public interface ParkingInfoProvider {

    List<Client> getClients();

    List<ParkingInfo> getParkingInfoForClient(TypedId<Client> clientId, YearMonth period);

    List<YearMonth> getParkingInfoMonthesForClient(TypedId<Client> clientId);

    List<Car> getCars(TypedId<Client> client);

    Optional<Client> getClient(TypedId<Client> id);

}

package com.github.putpixel.arvato.external.mock;

import java.io.FileReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.github.putpixel.arvato.external.ParkingInfoProvider;
import com.github.putpixel.arvato.model.Car;
import com.github.putpixel.arvato.model.Client;
import com.github.putpixel.arvato.model.ParkingInfo;
import com.github.putpixel.arvato.model.TypedId;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

@Component
public class ParkingInfoProviderMock implements ParkingInfoProvider {

    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public List<Client> getClients() {
        try {
            Resource resource = resourceLoader.getResource("classpath:clients.json");
            FileReader json = new FileReader(resource.getFile());
            Type typeOfT = new TypeToken<List<Client>>() {
            }.getType();
            return createGson().fromJson(json, typeOfT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(TypedId.class, new TypedIdDeserializer());
        gsonBuilder.setPrettyPrinting();
        return gsonBuilder.create();
    }

    @SuppressWarnings("unchecked")
    private class TypedIdDeserializer implements JsonDeserializer<TypedId> {

        @Override
        public TypedId deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Type[] actualTypeArguments = ((ParameterizedType) typeOfT).getActualTypeArguments();
            Preconditions.checkState(actualTypeArguments.length == 1);
            Preconditions.checkState(actualTypeArguments[0] instanceof Class);
            return new TypedId(json.getAsString(), (Class) actualTypeArguments[0]);
        }

    }

    @Override
    public List<ParkingInfo> getParkingInfoForClient(TypedId<Client> clientId, YearMonth period) {
        LocalDateTime from = period.atDay(1).atStartOfDay();
        LocalDateTime to = period.atEndOfMonth().atTime(LocalTime.MAX);
        return parkingInfosOfClient(clientId)
                .filter(info -> info.getParkedAt().isAfter(from))
                .filter(info -> info.getParkedAt().isBefore(to))
                .sorted((e1, e2) -> e1.getParkedAt().compareTo(e2.getParkedAt()))
                .collect(Collectors.toList());
    }

    private Stream<ParkingInfo> parkingInfosOfClient(TypedId<Client> clientId) {
        Set<TypedId<Car>> carsOfClient = getCars(clientId).stream().map(TypedId::of).collect(Collectors.toSet());
        Stream<ParkingInfo> infosOfClient = getAllInfos().stream().filter(info -> carsOfClient.contains(info.getParkedCar()));
        return infosOfClient;
    }

    private List<ParkingInfo> getAllInfos() {
        try {
            Resource resource = resourceLoader.getResource("classpath:park_info.json");
            FileReader json = new FileReader(resource.getFile());
            Type typeOfT = new TypeToken<List<ParkingInfo>>() {
            }.getType();
            return createGson().fromJson(json, typeOfT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Car> getCars(TypedId<Client> client) {
        return getAllCars().stream().filter(car -> car.getClientIdentifier().equals(client)).collect(Collectors.toList());
    }

    private List<Car> getAllCars() {
        try {
            Resource resource = resourceLoader.getResource("classpath:cars.json");
            FileReader json = new FileReader(resource.getFile());
            Type typeOfT = new TypeToken<List<Car>>() {
            }.getType();
            return createGson().fromJson(json, typeOfT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<YearMonth> getParkingInfoMonthesForClient(TypedId<Client> clientId) {
        return parkingInfosOfClient(clientId)
                .map(it -> YearMonth.from(it.getParkedAt()))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Client> getClient(TypedId<Client> id) {
        return getClients().stream().filter(c -> c.getIdentifier().equals(id.getIdentifier())).findFirst();
    }

}

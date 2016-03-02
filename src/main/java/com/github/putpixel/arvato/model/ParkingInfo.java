package com.github.putpixel.arvato.model;

import java.time.LocalDateTime;

public class ParkingInfo extends ModelWithIdentifier {

    private static final long serialVersionUID = 1851188232527381447L;

    private TypedId<Car> parkedCar;

    private LocalDateTime parkedAt;

    private LocalDateTime leftParkingAt;

    public TypedId<Car> getParkedCar() {
        return parkedCar;
    }

    public void setParkedCar(TypedId<Car> parkedCar) {
        this.parkedCar = parkedCar;
    }

    public LocalDateTime getParkedAt() {
        return parkedAt;
    }

    public void setParkedAt(LocalDateTime parkedAt) {
        this.parkedAt = parkedAt;
    }

    public LocalDateTime getLeftParkingAt() {
        return leftParkingAt;
    }

    public void setLeftParkingAt(LocalDateTime leftParkingAt) {
        this.leftParkingAt = leftParkingAt;
    }

    @Override
    public String toString() {
        return "ParkingInfo [parkedCar=" + parkedCar + ", parkedAt=" + parkedAt + ", leftParkingAt=" + leftParkingAt + "]";
    }
}

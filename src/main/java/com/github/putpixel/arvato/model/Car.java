package com.github.putpixel.arvato.model;

public class Car extends ModelWithIdentifier {

    private static final long serialVersionUID = 479755213304954677L;

    private String alias;

    private String plate;

    private TypedId<Client> clientIdentifier;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public TypedId<Client> getClientIdentifier() {
        return clientIdentifier;
    }

    public void setClientIdentifier(TypedId<Client> clientIdentifier) {
        this.clientIdentifier = clientIdentifier;
    }

    @Override
    public String toString() {
        return "Car [alias=" + alias + ", plate=" + plate + ", clientIdentifier=" + clientIdentifier + "]";
    }

}

package com.github.putpixel.arvato.model;

public class Client extends ModelWithIdentifier {

    private static final long serialVersionUID = 3479755153304954677L;

    private String name;

    private boolean premium;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    @Override
    public String toString() {
        return "Client [name=" + name + ", premium=" + premium + "]";
    }

}

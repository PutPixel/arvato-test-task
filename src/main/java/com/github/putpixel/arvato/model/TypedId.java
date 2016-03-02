package com.github.putpixel.arvato.model;

@SuppressWarnings("unchecked")
public final class TypedId<T extends ModelWithIdentifier> {

    private String identifier;
    private Class<T> clazz;

    public TypedId(String identifier, Class<T> clazz) {
        this.identifier = identifier;
        this.clazz = clazz;
    }

    public static final TypedId<Client> of(Client c) {
        return new TypedId(c.getIdentifier(), c.getClass());
    }

    public static final TypedId<Car> of(Car c) {
        return new TypedId(c.getIdentifier(), c.getClass());
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public void setClazz(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
        result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            throw new RuntimeException("Tring to compare TypedId with " + obj.getClass());
        }
        TypedId other = (TypedId) obj;

        if (!clazz.equals(other.clazz)) {
            throw new RuntimeException("Tring to compare TypedId<" + clazz + "> with TypedId<" + other.clazz + ">");
        }
        if (identifier == null) {
            if (other.identifier != null) {
                return false;
            }
        }
        else if (!identifier.equals(other.identifier)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TypedId<" + clazz.getSimpleName() + ">[" + identifier + "]";
    }

}

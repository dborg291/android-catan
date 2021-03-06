package edu.up.cs.androidcatan.catan.gamestate;

import java.io.Serializable;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * https://github.com/alexweininger/android-catan
 **/

public class Hexagon implements Serializable {

    private static final long serialVersionUID = 675408522730573292L;
    // instance variables
    private int resourceId, chitValue, hexagonId;

    /**
     * Hexagon constructor
     *
     * @param resourceType - resourceType type of hexagon
     * @param chitValue - dice value of hexagon
     */
    public Hexagon(int resourceType, int chitValue, int hexagonId) {
        this.hexagonId = hexagonId;
        this.resourceId = resourceType;
        this.chitValue = chitValue;
    }

    /**
     * Copy constructor for a Hexagon object.
     *
     * @param h Hexagon object to create a copy from.
     */
    Hexagon(Hexagon h) {
        this.setHexagonId(h.getHexagonId());
        this.setChitValue(h.getChitValue());
        this.setResourceId(h.getResourceId());
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setHexagonId(int hexagonId) {
        this.hexagonId = hexagonId;
    }

    public int getChitValue() {
        return chitValue;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public void setChitValue(int chitValue) {
        this.chitValue = chitValue;
    }

    public int getHexagonId() {
        return this.hexagonId;
    }

    /**
     * @return String representing the Hexagon object.
     */
    @Override
    public String toString() {
        return "id=" + this.hexagonId + "\tresId=" + this.resourceId + "\tchit=" + this.chitValue;
    }
}

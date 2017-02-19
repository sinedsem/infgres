package com.github.sinedsem.infgres.datamodel.datamine;

public class Battery extends DatamineEntity implements Continuous {

    private int number;
    private int charge;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }
}

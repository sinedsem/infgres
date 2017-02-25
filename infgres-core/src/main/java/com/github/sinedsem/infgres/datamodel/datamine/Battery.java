package com.github.sinedsem.infgres.datamodel.datamine;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "battery")
public class Battery extends DatamineEntity implements Continuous {

    @Column(name = "f_number")
    private int number;

    @Column(name = "f_charge")
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

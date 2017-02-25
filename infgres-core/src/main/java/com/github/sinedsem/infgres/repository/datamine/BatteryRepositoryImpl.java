package com.github.sinedsem.infgres.repository.datamine;

import com.github.sinedsem.infgres.datamodel.datamine.Battery;

import javax.persistence.EntityManager;

public class BatteryRepositoryImpl extends DatamineRepositoryImpl<Battery> {
    public BatteryRepositoryImpl(EntityManager entityManager) {
        super(entityManager);
    }
}

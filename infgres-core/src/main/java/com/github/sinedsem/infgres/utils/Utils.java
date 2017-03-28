package com.github.sinedsem.infgres.utils;

import com.github.sinedsem.infgres.datamodel.datamine.ContinuousDatamineEntity;
import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class Utils {

    private Utils() {
    }

    public static void seizeEntities(Collection<DatamineEntity> entities) {

        Map<String, DatamineEntity> previous = new HashMap<>();

        for (Iterator<DatamineEntity> it = entities.iterator(); it.hasNext(); ) {
            DatamineEntity entity = it.next();
            if (entity instanceof ContinuousDatamineEntity) {
                String key = entity.getKey();
                DatamineEntity prev = previous.get(key);

                if (prev != null && prev.getEndTime() >= entity.getStartTime()) {
                    if (prev.equals(entity)) {
                        prev.setEndTime(entity.getEndTime());
                        prev.setRequestId(entity.getRequestId());
                        it.remove();
                    } else {
                        prev.setEndTime(entity.getStartTime() - 1);
                        previous.put(key, entity);
                    }
                } else {
                    previous.put(key, entity);
                }
            }

        }
    }
}

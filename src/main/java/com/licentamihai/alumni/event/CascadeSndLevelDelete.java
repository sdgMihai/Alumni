package com.licentamihai.alumni.event;

import com.licentamihai.alumni.annotation.CascadeDelete;
import com.licentamihai.alumni.annotation.CascadeSave;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;


@Getter
@Setter
public class CascadeSndLevelDelete implements ReflectionUtils.FieldCallback{
    private Object source;
    private MongoOperations mongoOperations;

    CascadeSndLevelDelete(final Object source, final MongoOperations mongoOperations) {
        this.source = source;
        this.mongoOperations = mongoOperations;
    }

    @Override
    public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
        ReflectionUtils.makeAccessible(field);

        if (field.isAnnotationPresent(DBRef.class) && field.isAnnotationPresent(CascadeDelete.class)) {
            final Object fieldValue = field.get(getSource());

            if (fieldValue != null) {
                final FieldCallback callback = new FieldCallback();

                ReflectionUtils.doWithFields(fieldValue.getClass(), callback);
                if (fieldValue instanceof List) {
                    ((List<?>) fieldValue).forEach( element -> getMongoOperations().remove(element));
                } else {
                    getMongoOperations().remove(fieldValue);
                }
            }
        }

    }
}

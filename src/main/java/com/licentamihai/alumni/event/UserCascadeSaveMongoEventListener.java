package com.licentamihai.alumni.event;

import com.licentamihai.alumni.model.Group;
import com.licentamihai.alumni.model.SimpleUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;

public class UserCascadeSaveMongoEventListener extends AbstractMongoEventListener<Object> {
    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public void onBeforeConvert(final BeforeConvertEvent<Object> event) {
        final Object source = event.getSource();
        // TODO : change this with a generic implementation by modyfing the link impl
        // to take into consideration lists, not simple elements
        // https://www.baeldung.com/cascading-with-dbref-and-lifecycle-events-in-spring-data-mongodb
        if ((source instanceof SimpleUser) && (((SimpleUser) source).getGroups() != null)) {
            ((SimpleUser) source).getGroups().forEach(
                    group -> mongoOperations.save(group, "groups")
            );
        }
        if ((source instanceof Group) && (((Group) source).getChannels() != null)) {
            ((Group) source).getChannels().forEach(
                    channel -> mongoOperations.save(channel, "channels")
            );
        }
    }
}

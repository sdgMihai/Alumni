package com.licentamihai.alumni.event;

import com.licentamihai.alumni.controller.UsersController;
import com.licentamihai.alumni.repository.ChannelRepository;
import com.licentamihai.alumni.repository.GroupRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.event.*;
import org.springframework.util.ReflectionUtils;
import java.util.List;

public class CascadeSaveMongoEventListener extends AbstractMongoEventListener<Object> {

    @Autowired
    private MongoOperations mongoOperations;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private ChannelRepository channelRepository;
    private static final Logger log = LogManager.getLogger(CascadeSaveMongoEventListener.class);
    private static Boolean onDelete = false;

    @Override
    public void onBeforeConvert(final BeforeConvertEvent<Object> event) {
        final Object source = event.getSource();
        log.debug("enter before convert {}", event.toString());
        ReflectionUtils.doWithFields(source.getClass(), new CascadeSaveCallback(source, mongoOperations));
    }

    @Override
    public void onBeforeDelete(BeforeDeleteEvent<Object> event) {
        super.onBeforeDelete(event);
        log.debug("before delete event {}", event.toString());
        final Object source = event.getSource();
        ReflectionUtils.doWithFields(source.getClass(),
                new CascadeDeleteCallback(source, mongoOperations));
    }

//    @Override
//    public void onAfterLoad(AfterLoadEvent<Object> event) {
//        super.onAfterLoad(event);
//        log.debug("after load event {}", event.toString());
//   }

//    @Override
//    public void onAfterConvert(AfterConvertEvent<Object> event) {
//        super.onAfterConvert(event);
//        log.debug("after convert event {}", event.toString());
//    }


}

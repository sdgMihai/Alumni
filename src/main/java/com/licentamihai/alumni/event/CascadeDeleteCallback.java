package com.licentamihai.alumni.event;

import com.licentamihai.alumni.annotation.CascadeDelete;
import com.licentamihai.alumni.model.Channel;
import com.licentamihai.alumni.model.Group;
import com.licentamihai.alumni.repository.ChannelRepository;
import com.licentamihai.alumni.repository.GroupRepository;
import com.licentamihai.alumni.repository.MongoConfig;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
public class CascadeDeleteCallback implements ReflectionUtils.FieldCallback{
    private Object source;
    private MongoOperations mongoOperations;
    private static final Logger log = LogManager.getLogger(CascadeDeleteCallback.class);

    public CascadeDeleteCallback(final Object source, final MongoOperations mongoOperations) {
        this.source = source;
        this.mongoOperations = mongoOperations;
    }

    @Override
    public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
        ReflectionUtils.makeAccessible(field);
        final Object fieldValue = field.get(getSource());
        final FieldCallback callback = new FieldCallback();
        ReflectionUtils.doWithFields(fieldValue.getClass(), callback);

        if (field.getName().equals("documentAsMap")) {
            evalDocumentAsMap((LinkedHashMap<String, Object>) fieldValue);
        }
    }

    private void evalDocumentAsMap(LinkedHashMap<String, Object> fieldValue) {
        // 2021 org.bson.Document field documentAsMap
        Optional<Object> value = Optional.ofNullable(fieldValue.get("name"));
        value.ifPresent(docName -> {
            String objectName = docName.toString();
            log.debug("the channel name to be deleted {}", objectName);
            Query query = new Query();
            query.addCriteria(Criteria.where("name").is(objectName));
            final ObjectType type = getType(objectName);

            switch (type) {
                case GROUP:
                    Group group = mongoOperations.find(query, Group.class).get(0);
                    ReflectionUtils.doWithFields(group.getClass(), new CascadeSndLevelDelete(group, mongoOperations));
                    break;
                default:
                    Channel channel = mongoOperations.find(query, Channel.class).get(0);
                    ReflectionUtils.doWithFields(channel.getClass(), new CascadeSndLevelDelete(channel, mongoOperations));
            }
        });
    }

    private ObjectType getType(String name) {
        String patternStr = "^[A-Za-z0-9]*[Gg]roup[A-Za-z0-9]*$";
        Pattern pattern = Pattern.compile(patternStr);

        Matcher matcher = pattern.matcher(name);
        boolean matchFound = matcher.matches();

        return (matchFound ? ObjectType.GROUP: ObjectType.CHANNEL);
    }
}

enum ObjectType {  // TODO: if other object may have cascade delete add them here & mod acc. method getType
    GROUP,
    CHANNEL
}

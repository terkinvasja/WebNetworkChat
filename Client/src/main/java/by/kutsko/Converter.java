package by.kutsko;

import by.kutsko.model.Message;
import by.kutsko.model.MessageToUser;
import by.kutsko.model.MessageType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;

import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

public class Converter {

    private static Logger LOG = getLogger(Converter.class);
    private static ObjectMapper mapper = new ObjectMapper();

    public static String toJSON(MessageToUser message) {
        try {
            return mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            LOG.debug("", e);
            return "";
        }
    }

    public static MessageToUser toJavaObject(String jsonString) {
        try {
            return mapper.readValue(jsonString, MessageToUser.class);
        } catch (IOException e) {
            LOG.debug("", e);
            return new MessageToUser(0, new Message());
        }
    }
}

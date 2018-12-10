package by.kutsko.console;

import by.kutsko.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;

import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

public class Converter {

    private static Logger LOG = getLogger(Converter.class);
    private static ObjectMapper mapper = new ObjectMapper();

    public static String toJSON(Message message) {
        try {
            return mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            LOG.debug("", e);
            return "";
        }
    }

    public static Message toJavaObject(String jsonString) {
        try {
            return mapper.readValue(jsonString, Message.class);
        } catch (IOException e) {
            LOG.debug("", e);
            return new Message();
        }
    }
}

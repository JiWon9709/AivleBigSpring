package kt.aivle.convert;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CustomObjectMapper extends ObjectMapper {

  private static final long serialVersionUID = -3027900196571617560L;

  public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(
      "yyy-MM-dd HH:mm:ss");

  public CustomObjectMapper() {
    SimpleModule simpleModule = new SimpleModule();
    JavaTimeModule javaTimeModule = new JavaTimeModule();
    javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer());
    javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
    getSerializerProvider().setNullValueSerializer(new NullSerializer());
    registerModule(simpleModule);
    registerModule(javaTimeModule);
    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // 없는 필드로 인한 오류 무시
  }

  public class LocalDateSerializer extends JsonSerializer<LocalDate> {

    @Override
    public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
      gen.writeString(value.format(FORMATTER));
    }
  }

  public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      return LocalDate.parse(p.getValueAsString(), FORMATTER);
    }
  }
}
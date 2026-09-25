package com.northstar.crm.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

/**
 * Timed-path producer: publish a keyed CRM event with acks=all + idempotence.
 */
public class CustomerEventProducer {
  private static final String DEFAULT_TOPIC = "crm.customer-events.v1";
  private static final ObjectMapper JSON = new ObjectMapper();

  public static void main(String[] args) throws Exception {
    boolean validateOnly = args.length > 0 && "--validate".equals(args[0]);
    if ((validateOnly && args.length > 2) || (!validateOnly && args.length > 1)) {
      throw new IllegalArgumentException("Pass one event JSON file, optionally after --validate");
    }
    String bootstrap = System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");
    String topic = System.getenv().getOrDefault("CRM_EVENTS_TOPIC", DEFAULT_TOPIC);
    Path payloadPath = args.length == 0 || (validateOnly && args.length == 1)
        ? Path.of("events/customer-created-amina.json") : Path.of(args[validateOnly ? 1 : 0]);
    Event event = parseEvent(Files.readString(payloadPath, StandardCharsets.UTF_8));
    if (validateOnly) {
      System.out.printf("validated topic=%s key=%s file=%s (not published)%n",
          topic, event.customerId(), payloadPath);
      return;
    }

    try (KafkaProducer<String, String> producer = new KafkaProducer<>(producerProperties(bootstrap))) {
      // Finished prompt: the record key is the customer ID from the versioned envelope.
      ProducerRecord<String, String> record = new ProducerRecord<>(topic, event.customerId(), event.json());
      var metadata = producer.send(record).get(30, TimeUnit.SECONDS);
      System.out.printf("topic=%s key=%s partition=%d offset=%d timestamp=%d%n",
          metadata.topic(), event.customerId(), metadata.partition(), metadata.offset(), metadata.timestamp());
    }
  }

  static Properties producerProperties(String bootstrap) {
    if (bootstrap == null || bootstrap.isBlank()) {
      throw new IllegalArgumentException("Kafka bootstrap servers are required");
    }
    Properties props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    // Finished prompt: wait for all in-sync replicas and deduplicate producer retries.
    props.put(ProducerConfig.ACKS_CONFIG, "all");
    props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
    props.put(ProducerConfig.CLIENT_ID_CONFIG, "lab30-customer-producer");
    return props;
  }

  static Event parseEvent(String json) throws Exception {
    JsonNode root = JSON.readTree(json);
    if (root == null || !root.isObject() || !root.path("eventVersion").isInt()
        || root.path("eventVersion").intValue() != 1) {
      throw new IllegalArgumentException("Expected a version 1 CRM event object");
    }
    String eventId = requiredText(root, "eventId");
    String eventType = requiredText(root, "eventType");
    String customerId = requiredText(root, "customerId");
    requiredText(root, "correlationId");
    requiredText(root, "source");
    Instant.parse(requiredText(root, "occurredAt"));
    UUID.fromString(eventId);
    if (!("CustomerCreated".equals(eventType) || "CustomerStatusChanged".equals(eventType))
        || !customerId.matches("CUS-[0-9]+") || !root.path("data").isObject()) {
      throw new IllegalArgumentException("Invalid CRM event type, customer ID, or data");
    }
    return new Event(customerId, json);
  }

  private static String requiredText(JsonNode root, String field) {
    JsonNode value = root.path(field);
    if (!value.isTextual() || value.asText().isBlank()) {
      throw new IllegalArgumentException("Missing or invalid event field: " + field);
    }
    return value.asText();
  }

  record Event(String customerId, String json) {}
}

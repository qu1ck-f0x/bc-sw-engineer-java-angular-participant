package com.northstar.crm.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomerEventProducerTest {
  private final ObjectMapper json = new ObjectMapper();

  @Test
  void producerRequiresAllAcksAndIdempotence() {
    var properties = CustomerEventProducer.producerProperties("localhost:9092");
    assertEquals("localhost:9092", properties.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
    assertEquals("all", properties.get(ProducerConfig.ACKS_CONFIG));
    assertEquals(true, properties.get(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG));
    assertEquals("lab30-customer-producer", properties.get(ProducerConfig.CLIENT_ID_CONFIG));
    assertThrows(IllegalArgumentException.class, () -> CustomerEventProducer.producerProperties(" "));
  }

  @Test
  void samplesHaveValidDistinctVersionedEnvelopes() throws Exception {
    Set<String> ids = new HashSet<>();
    for (String file : new String[] {
        "customer-created-amina.json", "customer-created-ravi.json", "customer-status-changed-amina.json"}) {
      String value = Files.readString(Path.of("events", file));
      var event = CustomerEventProducer.parseEvent(value);
      JsonNode envelope = json.readTree(value);
      assertEquals(1, envelope.get("eventVersion").asInt());
      assertEquals("lab-request-001", envelope.get("correlationId").asText());
      assertEquals(envelope.get("customerId").asText(), event.customerId());
      assertTrue(ids.add(envelope.get("eventId").asText()));
      ProducerRecord<String, String> record = new ProducerRecord<>("crm.customer-events.v1", event.customerId(), event.json());
      assertEquals(event.customerId(), record.key());
    }
    assertEquals(3, ids.size());
  }

  @Test
  void aminaEventsShareTheSameRecordKey() throws Exception {
    var created = CustomerEventProducer.parseEvent(
        Files.readString(Path.of("events", "customer-created-amina.json")));
    var changed = CustomerEventProducer.parseEvent(
        Files.readString(Path.of("events", "customer-status-changed-amina.json")));
    var ravi = CustomerEventProducer.parseEvent(
        Files.readString(Path.of("events", "customer-created-ravi.json")));
    assertEquals("CUS-1001", created.customerId());
    assertEquals(created.customerId(), changed.customerId());
    assertEquals("CUS-1002", ravi.customerId());
  }

  @Test
  void invalidEnvelopeIsRejectedBeforePublishing() throws Exception {
    ObjectNode envelope = (ObjectNode) json.readTree(
        Files.readString(Path.of("events", "customer-created-amina.json")));
    envelope.remove("customerId");
    assertThrows(IllegalArgumentException.class, () -> CustomerEventProducer.parseEvent(json.writeValueAsString(envelope)));
    envelope.put("customerId", "CUS-1001");
    envelope.put("eventVersion", 2);
    assertThrows(IllegalArgumentException.class, () -> CustomerEventProducer.parseEvent(json.writeValueAsString(envelope)));
  }

  @Test
  void validationModeRunsWithoutABroker() throws Exception {
    CustomerEventProducer.main(new String[] {"--validate", "events/customer-created-amina.json"});
    CustomerEventProducer.main(new String[] {"--validate", "events/customer-created-ravi.json"});
    CustomerEventProducer.main(new String[] {"--validate", "events/customer-status-changed-amina.json"});
  }
}

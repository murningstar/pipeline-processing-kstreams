package com.pipeline.processing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaStreamsCfg {
    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration getStreamsConfiguration() {
        Map<String, Object> result = new HashMap<>();
        result.put(StreamsConfig.APPLICATION_ID_CONFIG, "my-consumer-group");
        result.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        result.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        result.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, new JsonSerde<>(MessageDTO.class).getClass());
//        result.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, new JsonSerde<>(MessageDTO.class, new ObjectMapper()).getClass());
        return new KafkaStreamsConfiguration(result);
    }

    @Bean
    public Topology createTopology(StreamsBuilder streamsBuilder) {
        KStream<String, MessageDTO> sales =
                streamsBuilder
                        .stream("sales-topic", Consumed.with(Serdes.String(), new JsonSerde<>(MessageDTO.class)));

        var totalPerReceipt = sales.mapValues(messageDTO -> {
            var totalReceiptPrice = 0;
            for (int i = 0; i < messageDTO.receipt.size(); i++) {
                totalReceiptPrice += messageDTO.receipt.get(i).price;
            }
            return totalReceiptPrice;
        }).peek((k,v) -> System.out.println("[totalPerReceipt] "+v));

        totalPerReceipt.groupByKey().aggregate(
                        () -> 0,
                        (storeId, next, accum) -> accum + next,
                        Materialized.with(Serdes.String(), Serdes.Integer()))
                .toStream()
                .peek((k, v) -> System.out.println("[total-per-store-" + k + "]" + " " + v))
                .mapValues(v->v.toString())
                .to("total-per-store", Produced.with(Serdes.String(), Serdes.String()));

        totalPerReceipt.groupByKey().aggregate(
                        () -> 0,
                        (storeId, next, accum) -> accum > next ? accum : next,
                        Materialized.with(Serdes.String(), Serdes.Integer()))
                .toStream()
                .peek((k, v) -> System.out.println("[highest-receipt-per-store-" + k + "]" + " " + v))
                .mapValues(v->v.toString())
                .to("highest-receipt-per-store", Produced.with(Serdes.String(), Serdes.String()));

        return streamsBuilder.build();
    }

}

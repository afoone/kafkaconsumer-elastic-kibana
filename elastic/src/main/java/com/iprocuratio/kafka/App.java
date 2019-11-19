package com.iprocuratio.kafka;

import java.io.IOException;
import java.util.Properties;

import org.apache.http.HttpHost;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class App {


    public static KafkaConsumer<String, String> createConsumer() {
        String boostrapServers ="192.168.0.10:9092";
        String groupId = "kibana-logger";
        String topic = "create_process";

        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String,String>(properties);
        return consumer;

    }

    public static RestHighLevelClient createClient() {
        String hostName="localhost";
        RestClientBuilder restClientBuilder = RestClient.builder(new HttpHost(hostName,9200,"http"));
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(restClientBuilder);
        return restHighLevelClient;
    }

    public static void main(String[] args) throws IOException {
        RestHighLevelClient restHighLevelClient = createClient();
        // Necesitamos que esté creado previamente el índice twitter

        String jsonString = "{" + "\"user\":\"kimchy\"," + "\"postDate\":\"2013-01-30\","
                + "\"message\":\"trying out Elasticsearch\"" + "}";
        IndexRequest indexRequest = new IndexRequest("twitter");

        indexRequest.source(jsonString, XContentType.JSON);

        Logger logger = LoggerFactory.getLogger(App.class);

        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        String id = indexResponse.getId();
        logger.info("El id es " + id);
    }
}

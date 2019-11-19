# 1. GENERATE MAVEN PROJECT


```shell
mvn archetype:generate \
	-DgroupId=com.iprocuratio.kafka \
	-DartifactId=elastic \
	-DarchetypeArtifactId=maven-archetype-quickstart \
	-DinteractiveMode=false
```



# 2. COPIAR LAS DEPENDENCIAS DE ELASTIC SEARCH

Dentro del *pom.xml*

```xml
<dependency>
    <groupId>org.elasticsearch.client</groupId>
    <artifactId>elasticsearch-rest-high-level-client</artifactId>
    <version>7.4.2</version>
</dependency>
```

# 3. DEPENDENCIAS DE KAFKA

```xml
<!-- https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients -->
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
    <version>2.3.0</version>
</dependency>
```

# 4. DEPENDENCIAS DE slf4j

```xml
<!-- https://mvnrepository.com/artifact/org.slf4j/slf4j-simple -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-simple</artifactId>
    <version>1.7.29</version>
</dependency>
```

# Create Client

```java
    public static RestHighLevelClient createClient() {
        String hostName="localhost";
        RestClientBuilder restClientBuilder = RestClient.builder(new HttpHost(hostName,9200,"http"));
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(restClientBuilder)
        return restHighLevelClient;
    }
```

Creamos el cliente en el `main()`

```java
        RestHighLevelClient restHighLevelClient = createClient();
```

# Crear los índices

Hay que crear los índices con el POSTMAN:

```
PUT http://localhost:9200/twitter/
```

(*si lo ejecutamos dos veces nos dirá que ya existe, lo mismo que si ya estaba creado*)

Respuesta:
```json
{
    "acknowledged": true,
    "shards_acknowledged": true,
    "index": "twitter"
}
```

# Enviar datos

Necesitaremos datos:

```java
 String jsonString = "{" +
        "\"user\":\"kimchy\"," +
        "\"postDate\":\"2013-01-30\"," +
        "\"message\":\"trying out Elasticsearch\"" +
        "}";
```

Y los preparamos:

```java
 indexRequest.source(jsonString, XContentType.JSON);
```

Para enviarlos necesitamos un cliente:

```java
        Logger logger = LoggerFactory.getLogger(App.class);

        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        String id = indexResponse.getId();
        logger.info("El id es " + id);
```

Ejecuto (botón derecho en App.java -> run())

```
Hello World!
[main] INFO com.iprocuratio.kafka.App - El id es 6Elvg24BxHRO78YusUM6
```

Recuperamos el documento con:

```sh
GET http://localhost:9200/twitter/_doc/6Elvg24BxHRO78YusUM6
```


Podríamos, por supuesto, ejecutarlo varias veces para crear nuevos elementos.


# Escribiendo el consumidor

Crearemos un nuevo método estático:

```java
    public static KafkaConsumer<String, String> createConsumer() {
        
    }
```

Dentro de el parametrizamos el método:

```java
    String boostrapServers ="192.168.0.10:9092";
    String groupId = "kibana-logger";
    String topic = "create_process";

    Properties properties = new Properties();
    properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
    properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
```

Creamos el consumidor y lo devuelve el sistema:

```java
    KafkaConsumer<String, String> consumer = new KafkaConsumer<String,String>(properties);
    return consumer;
```


















package de.aero.common.kafka

enum class DltHeaders(
        val headerName: String) {
    APP_NAME("dlt.appName"),
    ORIGINAL_TOPIC("kafka_dlt-original-topic"),
    ORIGINAL_CONSUMER_GROUP("kafka_dlt-original-consumer-group"),
}

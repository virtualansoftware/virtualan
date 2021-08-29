package io.virtualan.message.core;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * The type Mqtt.
 */
public class Mqtt {
    private static IMqttClient instance;

    /**
     * Gets instance.
     *
     * @param MQTT_PUBLISHER_ID  the mqtt publisher id
     * @param MQTT_SERVER_ADDRES the mqtt server addres
     * @param options            the options
     * @return the instance
     */
    public static IMqttClient getInstance(String MQTT_PUBLISHER_ID, String MQTT_SERVER_ADDRES, MqttConnectOptions options) {
        try {
            if (instance == null) {
                instance = new MqttClient(MQTT_SERVER_ADDRES, MQTT_PUBLISHER_ID);
            }

            if (!instance.isConnected()) {
                instance.connect(options);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }

        return instance;
    }

    private Mqtt() {

    }
}
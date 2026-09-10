package com.xjteam4.envMgr.service;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public interface MqttClientService {

    void initClient();

    void reconnect();

    void closeClient();

    void sendDeviceCmd(String deviceId, String cmdContent, int qos);

    void sendDeviceCmd(String deviceId, String cmdContent);
}

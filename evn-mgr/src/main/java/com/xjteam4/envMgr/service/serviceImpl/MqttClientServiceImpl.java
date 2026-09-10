package com.xjteam4.envMgr.service.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xjteam4.envMgr.domain.EnvDataEntity;
import com.xjteam4.envMgr.mapper.EnvDataMapper;
import com.xjteam4.envMgr.service.MqttClientService;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Date;

@Service
public class MqttClientServiceImpl implements MqttClientService {

    @Autowired
    private EnvDataMapper envDataMapper;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${mqtt.client.broker-url}")
    private String brokerUrl;

    @Value("${mqtt.client.client-id}")
    private String clientId;

    @Value("${mqtt.client.topic}")
    private String topic;

    @Value("${mqtt.client.username:}")
    private String username;

    @Value("${mqtt.client.password:}")
    private String password;

    @Value("${mqtt.cmd.topic.prefix}")
    private String cmdTopicPrefix;

    private MqttClient mqttClient;

    @EventListener(ApplicationReadyEvent.class)
    public void initClient() {
        try {
            mqttClient = new MqttClient(brokerUrl, clientId, new MemoryPersistence());

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            if (username != null && !username.isEmpty()) {
                options.setUserName(username);
            }
            if (password != null && !password.isEmpty()) {
                options.setPassword(password.toCharArray());
            }
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(20);

            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("MQTT连接断开，尝试重连...");
                    reconnect();
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String payload = new String(message.getPayload());
                    System.out.println("================= 收到传感器数据: " + payload);

                    try {
                        // 解析 JSON
                        java.util.Map<String, Object> map = objectMapper.readValue(payload, java.util.Map.class);

                        // 提取字段
                        Integer devId = (Integer) map.get("devId");
                        String devName = (String) map.get("devName");
                        String devPosi = (String) map.get("devPosi");
                        Double temp = map.get("temp") != null ? ((Number) map.get("temp")).doubleValue() : null;
                        Double humi = map.get("humi") != null ? ((Number) map.get("humi")).doubleValue() : null;
                        Integer light = map.get("light") != null ? ((Number) map.get("light")).intValue() : null;

                        // 插入温度数据 (valType=1)
                        if (temp != null) {
                            EnvDataEntity envData = new EnvDataEntity();
                            envData.setDevId(devId);
                            envData.setDevName(devName);
                            envData.setDevPosi(devPosi);
                            envData.setDevVal(temp);
                            envData.setValType(1);
                            envData.setCollectTime(new Date().toString());
                            envData.setThFlag(0);
                            int result = envDataMapper.insert(envData);
                            System.out.println("温度数据入库成功，影响行数: " + result);
                        }

                        // 插入湿度数据 (valType=2)
                        if (humi != null) {
                            EnvDataEntity envData = new EnvDataEntity();
                            envData.setDevId(devId);
                            envData.setDevName(devName);
                            envData.setDevPosi(devPosi);
                            envData.setDevVal(humi);
                            envData.setValType(2);
                            envData.setCollectTime(new Date().toString());
                            envData.setThFlag(0);
                            int result = envDataMapper.insert(envData);
                            System.out.println("湿度数据入库成功，影响行数: " + result);
                        }

                        // 插入光照数据 (valType=3)
                        if (light != null) {
                            EnvDataEntity envData = new EnvDataEntity();
                            envData.setDevId(devId);
                            envData.setDevName(devName);
                            envData.setDevPosi(devPosi);
                            envData.setDevVal(light.doubleValue());
                            envData.setValType(3);
                            envData.setCollectTime(new Date().toString());
                            envData.setThFlag(0);
                            int result = envDataMapper.insert(envData);
                            System.out.println("光照数据入库成功，影响行数: " + result);
                        }

                    } catch (Exception e) {
                        System.out.println("数据解析或入库失败: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                }
            });

            mqttClient.connect(options);
            mqttClient.subscribe(topic, 1);
            System.out.println("===== MQTT订阅客户端启动成功，订阅主题：" + topic + " =====");

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reconnect() {
        try {
            if (!mqttClient.isConnected()) {
                mqttClient.connect();
                mqttClient.subscribe(topic, 1);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendDeviceCmd(String deviceId, String cmdContent, int qos) {
        if (mqttClient == null || !mqttClient.isConnected()) {
            throw new RuntimeException("MQTT客户端未连接，无法下发命令");
        }

        String cmdTopic = cmdTopicPrefix + deviceId;
        MqttMessage mqttMessage = new MqttMessage(cmdContent.getBytes());
        mqttMessage.setQos(qos);
        mqttMessage.setRetained(false);

        try {
            mqttClient.publish(cmdTopic, mqttMessage);
            System.out.println("向设备[" + deviceId + "]下发命令：" + cmdContent + " | topic:" + cmdTopic);
        } catch (MqttException e) {
            e.printStackTrace();
            throw new RuntimeException("命令下发失败");
        }
    }

    @Override
    public void sendDeviceCmd(String deviceId, String cmdContent) {
        sendDeviceCmd(deviceId, cmdContent, 1);
    }

    @PreDestroy
    public void closeClient() {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.unsubscribe(topic);
                mqttClient.disconnect();
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
/**
 * 定义在SpringBoot项目的工具包下
 */
package com.xjteam4.envMgr.util;

import io.moquette.broker.Server;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.MemoryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Properties;

@Component
public class MqttBrokerServer {

    @Value("${mqtt.broker.host}")
    private String brokerHost;
    @Value("${mqtt.broker.port}")
    private int brokerPort;

    private Server mqttServer;

    @PostConstruct
    public void startBroker() throws IOException {
        IConfig config = new MemoryConfig(new Properties());
        // 直接用字符串配置，不依赖任何常量类
        config.setProperty("host", brokerHost);
        config.setProperty("port", String.valueOf(brokerPort));
        config.setProperty("allow_anonymous", "true");

        mqttServer = new Server();
        mqttServer.startServer(config);
        System.out.println("===== 内嵌MQTT Broker启动成功，端口：" + brokerPort + " =====");
    }

    @PreDestroy
    public void stopBroker() {
        if (mqttServer != null) {
            mqttServer.stopServer();
            System.out.println("===== MQTT Broker 已关闭 =====");
        }
    }
}

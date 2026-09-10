package com.xjteam4.envMgr;

import com.xjteam4.envMgr.util.CheckUtil;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@MapperScan("com.xjteam4.envMgr.mapper")
public class SpbYqApplication {

    private static final Logger log = LoggerFactory.getLogger(SpbYqApplication.class);

    public static void main(String[] args) throws UnknownHostException {
        SpringApplication app = new SpringApplication(SpbYqApplication.class);
        ConfigurableApplicationContext applicationContext = app.run(args);
        Environment env = applicationContext.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port");
        String path = env.getProperty("server.servlet.context-path");
        if (CheckUtil.checkStringsEmpty(path)) {
            path = "";
        }
        log.info("Application is running on http://{}:{}{}", ip, port, path);
        log.info("Application is running on http://{}:{}{}", "localhost", port, path);
    }

}

# 基于鸿蒙的物联网环境检测系统

基于 Hi3861 硬件开发板 + Spring Boot + HarmonyOS 的三端全链路物联网环境监测系统，实现温湿度 / 光照实时采集、异常告警、远程设备控制与数据可视化。

## 系统架构


## 技术栈

- **硬件端**：C 语言、Hi3861、DHT11 温湿度传感器、ADC 光敏采集、SSD1306 OLED、WiFi、MQTT（Paho）、FreeRTOS
- **后端**：Spring Boot、MyBatis、MySQL、MQTT（Paho）
- **客户端**：HarmonyOS、ArkTS、ArkUI、ECharts

## 核心功能

- 双传感器每 10 秒同步采集，JSON 格式封装上报
- MQTT 双向通信：数据上行上报、命令下行控制（QoS 1、断线自动重连）
- OLED 本地实时显示 + 双按键亮屏 / 熄屏（低功耗）
- 远程控制设备上报状态，LED 指示灯反馈
- 24 小时数据趋势可视化、阈值异常告警、操作日志审计

## 目录结构


#include "pinctrl.h"
#include "soc_osal.h"
#include "adc.h"
#include "adc_porting.h"
#include "osal_debug.h"
#include "cmsis_os2.h"
#include "app_init.h"
#include "gpio.h"
#include "i2c.h"
#include "stdio.h"
#include "string.h"

#include "Led.h"
#include "DHT11.h"
#include "wifi_connect.h"
#include "MQTTClient.h"
#include "MQTTClientPersistence.h"
#include "cJSON.h"
#include "time.h"

#include "ssd1306_fonts.h"
#include "ssd1306.h"

#define I2C_BANDRATE            400000

#define QOS                     1
#define MQTT_TASK_PRIO          (osPriority_t)(17)
#define MQTT_TASK_STACK_SIZE    0x1000
#define REPORT_TASK_PRIORITY_LOW 30

#define SERVER_URI      "192.168.168.167:1883"
#define DEV_ID          107
#define DEV_ID_STR      "107"
#define DEV_NAME        "testDev107"
#define DEV_POSI        "teacher2"
#define USER_NAME       "6cLNyy47x2"
#define PASSWORD        "version=2018-10-31&res=products%2F6cLNyy47x2%2Fdevices%2FtempSensor&et=1956499200&method=md5&sign=hRkbMPUv5%2BhDHSbOgy5NCQ%3D%3D"
#define PUBLISH_TOPIC   "sensor/temp_hum"
#define SUBSCRIBE_TOPIC "cmd/device/107"

#define OLED_TASK_PRIO          (osPriority_t)(16)
#define OLED_TASK_STACK_SIZE    0x1000
#define DHT11_TASK_PRIO         (osPriority_t)(17)
#define DHT11_TASK_STACK_SIZE   0x1000
#define ADC_TASK_PRIO           (osPriority_t)(17)
#define ADC_TASK_STACK_SIZE     0x1000

// ========== 按键相关宏定义 ==========
#define KEY1_GPIO               GPIO_13
#define KEY2_GPIO               GPIO_14
#define KEY_TASK_PRIO           (osPriority_t)(18)
#define KEY_TASK_STACK_SIZE     0x800

MQTTClient_deliveryToken deliveredtoken;
MQTTClient handler;

volatile uint8_t  g_dht11_buf[5] = {0};
volatile uint8_t  g_dht11_valid = 0;
volatile uint32_t g_light_mv = 0;
volatile uint8_t  g_light_valid = 0;
volatile uint8_t  g_wifi_connected = 0;
volatile uint8_t  g_mqtt_connected = 0;
volatile uint8_t  g_report_enabled = 1;
char g_wifi_ssid[33] = {0};

// ========== 按键相关全局变量 ==========
volatile uint8_t  g_oled_display_on = 1;  // 1=亮屏, 0=熄屏

void MQTT_Deliverd(void *context, MQTTClient_deliveryToken dt)
{
    (void)context;
    osal_printk("Message with token value %d delivery confirmed\r\n", dt);
    deliveredtoken = dt;
}

int MQTT_MsgArrived(void *context, char *topicName, int topicLen, MQTTClient_message *message)
{
    int i;
    char *payloadptr = NULL;
    (void)context;
    (void)topicLen;
    osal_printk("Message arrived\r\n");
    osal_printk(" topic: %s\r\n", topicName);
    osal_printk(" message: ");

    payloadptr = message->payload;
    for (i = 0; i < message->payloadlen; i++)
    {
        osal_printk("%c", payloadptr[i]);
    }
    osal_printk("\r\n");

    if (strcmp(topicName, SUBSCRIBE_TOPIC) == 0)
    {
        cJSON *msg_cjson = cJSON_Parse(message->payload);
        if (msg_cjson == NULL)
        {
            const char *error_str = cJSON_GetErrorPtr();
            osal_printk("cJSON_Parse failed:: %s\n", error_str);
            return ERRCODE_INVALID_PARAM;
        }

        cJSON *cjson_param = cJSON_GetObjectItemCaseSensitive(msg_cjson, "cmdId");
        int cmdId = (int)cJSON_GetNumberValue(cjson_param);
        
        if(cmdId == 1){
            Led_Ctrl(LED_ON);
            g_report_enabled = 1;
            osal_printk("Report ENABLED\r\n");
        }
        else if(cmdId == 0)
        {
            Led_Ctrl(LED_OFF);
            Led2_Ctrl(LED_OFF);
            g_report_enabled = 0;
            osal_printk("Report DISABLED\r\n");
        }
        cJSON_Delete(msg_cjson);
    }

    MQTTClient_freeMessage(&message);
    MQTTClient_free(topicName);
    return 1;
}

void MQTT_ConnLost(void *context, char *cause)
{
    (void)context;
    osal_printk("\nConnection lost\r\n");
    osal_printk(" cause: %s\r\n", cause);
    g_mqtt_connected = 0;
}

extern int MQTTClient_init(void);

int MQTT_Client_Init(const char *serverURI, const char *dev_ID, const char *dev_name, const char *topic)
{
    MQTTClient_init();

    int rc = MQTTClient_create(&handler, serverURI, dev_ID, MQTTCLIENT_PERSISTENCE_NONE, NULL);
    if (rc != MQTTCLIENT_SUCCESS)
    {
        osal_printk("Client handler create failure\r\n");
        return rc;
    }

    MQTTClient_setCallbacks(handler, NULL, MQTT_ConnLost, MQTT_MsgArrived, MQTT_Deliverd);

    MQTTClient_connectOptions options = MQTTClient_connectOptions_initializer;
    options.keepAliveInterval = 20;
    options.cleansession = 1;

    rc = MQTTClient_connect(handler, &options);
    if (rc != MQTTCLIENT_SUCCESS)
    {
        osal_printk("Client connect failure\r\n");
        MQTTClient_destroy(&handler);
        return rc;
    }

    MQTTClient_subscribe(handler, topic, QOS);
    g_mqtt_connected = 1;
    return MQTTCLIENT_SUCCESS;
}

char *cJSON_MessageCreate(double temp_data, uint64_t humi_data)
{
    cJSON *root = cJSON_CreateObject();
    if (root == NULL)
    {
        osal_printk("cJSON_CreateObject fail\r\n");
        return NULL;
    }

    if (cJSON_AddNumberToObject(root, "devId", DEV_ID) == NULL)
    {
        osal_printk("add devId fail\r\n");
        cJSON_Delete(root);
        return NULL;
    }

    if (cJSON_AddStringToObject(root, "devName", DEV_NAME) == NULL)
    {
        osal_printk("add devName fail\r\n");
        cJSON_Delete(root);
        return NULL;
    }

    if (cJSON_AddStringToObject(root, "devPosi", DEV_POSI) == NULL)
    {
        osal_printk("add devPosi fail\r\n");
        cJSON_Delete(root);
        return NULL;
    }

    cJSON_AddNumberToObject(root, "temp", temp_data);
    cJSON_AddNumberToObject(root, "humi", humi_data);
    cJSON_AddNumberToObject(root, "light", g_light_valid ? g_light_mv : 0);

    char *json_string = cJSON_Print(root);
    cJSON_Delete(root);
    return json_string;
}

int Sensor_report_task(void* argument)
{
    unused(argument);

    while(1)
    {
        if (g_report_enabled == 0) {
            Led_Ctrl(LED_OFF);
            Led2_Ctrl(LED_OFF);
            osal_msleep(1000);
            continue;
        }

        uint8_t buf[5] = {0};
        SENSOR_Error_t err = DHT11_GetData(buf);
        if(err == DHT11_OK)
        {
            Led_Ctrl(LED_ON);
            
            osal_printk("Temp:%d.%d  Humi: %d.%d\r\n", buf[2], buf[3], buf[0], buf[1]);
            char* payload = cJSON_MessageCreate(buf[2] + buf[3] * 0.1, buf[0] + buf[1] * 0.1);
            osal_printk("%s\r\n", payload);

            MQTTClient_message pubmsg = MQTTClient_message_initializer;
            MQTTClient_deliveryToken token;
            pubmsg.payload = payload;
            pubmsg.payloadlen = (int)strlen(payload);
            pubmsg.qos = QOS;
            pubmsg.retained = 0;
            MQTTClient_publishMessage(handler, PUBLISH_TOPIC, &pubmsg, &token);

            free(payload);
            
            osal_msleep(500);
            Led_Ctrl(LED_OFF);
        }
        else
            osal_printk("error:%d\r\n", err);

        if (g_light_valid == 1) {
            Led2_Ctrl(LED_ON);
            
            osal_printk("Light:%dmV\r\n", g_light_mv);
            
            osal_msleep(500);
            Led2_Ctrl(LED_OFF);
        }

        osal_msleep(9000);
    }
}

void test_adc_callback(uint8_t ch, uint32_t *buffer, uint32_t length, bool *next)
{
    UNUSED(next);
    for (uint32_t i = 0; i < length; i++) {
        g_light_mv = buffer[i];
        g_light_valid = 1;
        osal_printk("channel: %d, light voltage: %dmv\r\n", ch, buffer[i]);
    }
}

void OLED_init_pin(void)
{
    uapi_pin_set_mode(GPIO_15, PIN_MODE_2);
    uapi_pin_set_mode(GPIO_16, PIN_MODE_2);
    uapi_pin_set_pull(GPIO_15, PIN_PULL_TYPE_UP);
    uapi_pin_set_pull(GPIO_16, PIN_PULL_TYPE_UP);
}

void DHT11_task(void* argment)
{
    UNUSED(argment);
    temp_sensor_Init();
    osal_msleep(1000);

    while (1) {
        uint8_t buf[5] = {0};
        SENSOR_Error_t err = DHT11_GetData(buf);
        if (err == DHT11_OK) {
            osal_printk("Temp:%d.%d  Humi: %d.%d\r\n", buf[2], buf[3], buf[0], buf[1]);
            g_dht11_buf[0] = buf[0];
            g_dht11_buf[1] = buf[1];
            g_dht11_buf[2] = buf[2];
            g_dht11_buf[3] = buf[3];
            g_dht11_valid = 1;
        } else {
            osal_printk("DHT11 error:%d\r\n", err);
            g_dht11_valid = 0;
        }
        osal_msleep(2000);
    }
}

void ADC_task(void* argment)
{
    UNUSED(argment);

    uapi_adc_init(ADC_CLOCK_125KHZ);
    uapi_adc_power_en(AFE_SCAN_MODE_MAX_NUM, true);

    adc_scan_config_t config = {
        .type = 0,
        .freq = 1,
    };

    while (1) {
        uapi_adc_auto_scan_ch_enable(ADC_CHANNEL_5, config, test_adc_callback);
        uapi_adc_auto_scan_ch_disable(ADC_CHANNEL_5);
        osal_msleep(2000);
    }
}

// ========== 按键初始化函数 ==========
void Key_Init(void)
{
    // 配置KEY1 (GPIO_13) 为输入模式，上拉电阻
    uapi_pin_set_mode(KEY1_GPIO, PIN_MODE_0);  // GPIO模式
    uapi_gpio_set_dir(KEY1_GPIO, GPIO_DIRECTION_INPUT);  // ✅ 修正：INPUT不是IN
    uapi_pin_set_pull(KEY1_GPIO, PIN_PULL_TYPE_UP);  // 上拉
    
    // 配置KEY2 (GPIO_14) 为输入模式，上拉电阻
    uapi_pin_set_mode(KEY2_GPIO, PIN_MODE_0);  // GPIO模式
    uapi_gpio_set_dir(KEY2_GPIO, GPIO_DIRECTION_INPUT);  // ✅ 修正：INPUT不是IN
    uapi_pin_set_pull(KEY2_GPIO, PIN_PULL_TYPE_UP);  // 上拉
    
    osal_printk("Key init OK: KEY1=GPIO_13, KEY2=GPIO_14\r\n");
}

// ========== 按键检测任务 ==========
void Key_task(void* argment)
{
    UNUSED(argment);
    
    Key_Init();
    osal_msleep(100);  // 等待初始化稳定
    
    uint8_t key1_last = 1;  // 上拉，默认高电平
    uint8_t key2_last = 1;
    uint8_t key1_now = 1;
    uint8_t key2_now = 1;
    
    while (1) {
        // 读取KEY1状态 (低电平有效)
        key1_now = uapi_gpio_get_val(KEY1_GPIO);
        if (key1_last == 1 && key1_now == 0) {
            // KEY1按下，亮屏
            osal_msleep(20);  // 消抖
            if (uapi_gpio_get_val(KEY1_GPIO) == 0) {
                g_oled_display_on = 1;
                ssd1306_SetDisplayOn(1);  // 亮屏
                osal_printk("KEY1 pressed: OLED ON\r\n");
            }
        }
        key1_last = key1_now;
        
        // 读取KEY2状态 (低电平有效)
        key2_now = uapi_gpio_get_val(KEY2_GPIO);
        if (key2_last == 1 && key2_now == 0) {
            // KEY2按下，熄屏
            osal_msleep(20);  // 消抖
            if (uapi_gpio_get_val(KEY2_GPIO) == 0) {
                g_oled_display_on = 0;
                ssd1306_SetDisplayOn(0);  // 熄屏
                osal_printk("KEY2 pressed: OLED OFF\r\n");
            }
        }
        key2_last = key2_now;
        
        osal_msleep(50);  // 50ms检测周期
    }
}

void OLED_task(void* argment)
{
    UNUSED(argment);

    uint32_t baudrate = I2C_BANDRATE;
    uint8_t hscode = 0x00;

    OLED_init_pin();
    uapi_i2c_master_init(I2C_BUS_1, baudrate, hscode);

    ssd1306_Init();
    ssd1306_Fill(Black);

    while (1) {
        // ========== 修改：根据屏幕状态决定是否刷新显示 ==========
        if (g_oled_display_on == 0) {
            // 熄屏状态，不刷新显示，只等待
            osal_msleep(500);
            continue;
        }
        
        ssd1306_Fill(Black);

        // 第1行：WiFi名称
        char line1[32];
        if (g_wifi_connected && strlen(g_wifi_ssid) > 0) {
            snprintf(line1, sizeof(line1), "WiFi:%s", g_wifi_ssid);
        } else {
            snprintf(line1, sizeof(line1), "WiFi:Connecting");
        }
        ssd1306_SetCursor(0, 0);
        ssd1306_DrawString(line1, Font_6x8, White);

        // 第2行：上报状态
        char line2[32];
        if (g_report_enabled) {
            snprintf(line2, sizeof(line2), "Status:ON");
        } else {
            snprintf(line2, sizeof(line2), "Status:OFF");
        }
        ssd1306_SetCursor(0, 12);
        ssd1306_DrawString(line2, Font_6x8, White);

        // 第3行：MQTT设备名
        char line3[32];
        snprintf(line3, sizeof(line3), "MQTT:%s", DEV_NAME);
        ssd1306_SetCursor(0, 24);
        ssd1306_DrawString(line3, Font_6x8, White);

        // 第4行：温度+湿度
        char line4[32];
        if (g_dht11_valid == 1) {
            snprintf(line4, sizeof(line4), "Temp:%d.%dC Humi:%d.%d%%",
                     g_dht11_buf[2], g_dht11_buf[3],
                     g_dht11_buf[0], g_dht11_buf[1]);
        } else {
            snprintf(line4, sizeof(line4), "Temp:--C Humi:--%%");
        }
        ssd1306_SetCursor(0, 36);
        ssd1306_DrawString(line4, Font_6x8, White);

        // 第5行：光照
        char line5[32];
        if (g_light_valid == 1) {
            snprintf(line5, sizeof(line5), "Light:%dmV", g_light_mv);
        } else {
            snprintf(line5, sizeof(line5), "Light:--mV");
        }
        ssd1306_SetCursor(0, 48);
        ssd1306_DrawString(line5, Font_6x8, White);

        ssd1306_UpdateScreen();
        osal_msleep(500);
    }
}

void MQTT_task(void *argment)
{
    UNUSED(argment);

    Led_Init();
    temp_sensor_Init();

    wifi_connect(CONFIG_WIFI_SSID, CONFIG_WIFI_PWD);

    strncpy(g_wifi_ssid, CONFIG_WIFI_SSID, sizeof(g_wifi_ssid) - 1);
    g_wifi_ssid[sizeof(g_wifi_ssid) - 1] = '\0';
    g_wifi_connected = 1;

    MQTT_Client_Init(SERVER_URI, DEV_ID_STR, DEV_NAME, SUBSCRIBE_TOPIC);

    osal_msleep(50);
    osal_kthread_lock();
    osal_task *value_report_task = osal_kthread_create((osal_kthread_handler)Sensor_report_task, 0, "Sensor_report_task", 0x1000);
    if (value_report_task != NULL) {
        osal_kthread_set_priority(value_report_task, REPORT_TASK_PRIORITY_LOW);
    } else {
        osal_printk("ERROR: Create value_report_task failed!\n");
    }
    osal_kthread_unlock();
}

void MQTT_entry(void)
{
    osal_printk("mqtt executed ......................");

    osThreadAttr_t attr;

    attr.attr_bits = 0U;
    attr.cb_mem = NULL;
    attr.cb_size = 0U;

    attr.name = "MQTTTask";
    attr.priority = MQTT_TASK_PRIO;
    attr.stack_mem = NULL;
    attr.stack_size = MQTT_TASK_STACK_SIZE;

    osThreadNew(MQTT_task, NULL, &attr);

    attr.name = "DHT11Task";
    attr.priority = DHT11_TASK_PRIO;
    attr.stack_size = DHT11_TASK_STACK_SIZE;
    osThreadNew(DHT11_task, NULL, &attr);

    attr.name = "ADCTask";
    attr.priority = ADC_TASK_PRIO;
    attr.stack_size = ADC_TASK_STACK_SIZE;
    osThreadNew(ADC_task, NULL, &attr);

    attr.name = "OLEDTask";
    attr.priority = OLED_TASK_PRIO;
    attr.stack_size = OLED_TASK_STACK_SIZE;
    osThreadNew(OLED_task, NULL, &attr);

    // ========== 新增：创建按键检测任务 ==========
    attr.name = "KeyTask";
    attr.priority = KEY_TASK_PRIO;
    attr.stack_size = KEY_TASK_STACK_SIZE;
    osThreadNew(Key_task, NULL, &attr);
}

app_run(MQTT_entry);
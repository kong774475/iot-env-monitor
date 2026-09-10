#ifndef __DHT11_H
#define __DHT11_H

typedef enum {
    DHT11_OK                = 0x00,
    DHT11_NO_RESPONCE_ERR   = 0x01,
    DHT11_RESPONCE_LOW_ERR  = 0x02,
    DHT11_RESPONCE_HIGH_ERR = 0x03,
    DHT11_DATA_CRC_ERR      = 0x04,
} SENSOR_Error_t;

#define  SENSOR_CTL_PIN       GPIO_04        // 你要的 GPIO04
#define  SENSOR_CTL_PIN_MODE  PIN_MODE_2     // 必须设为 GPIO 模式

void temp_sensor_Init(void);
SENSOR_Error_t DHT11_GetData(uint8_t *pbuf);

#endif



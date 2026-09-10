#ifndef __LED_H
#define __LED_H

typedef enum {
    LED_OFF = 0x00,
    LED_ON = 0x01,
} LED_Stauts_t;

#define LED_CTL_PIN       GPIO_02
#define LED_CTL_PIN_MODE  PIN_MODE_0

#define LED2_CTL_PIN      GPIO_03
#define LED2_CTL_PIN_MODE PIN_MODE_0

void Led_Init(void);
void Led_Ctrl(LED_Stauts_t onoff);
void Led2_Ctrl(LED_Stauts_t onoff);

#endif
#include "pinctrl.h"
#include "soc_osal.h"
#include "gpio.h"
#include "osal_debug.h"
#include "cmsis_os2.h"
#include "app_init.h"
#include "los_task.h"
#include "systick.h"

#include "Led.h"

void Led_Init(void)
{
    uapi_pin_set_mode(LED_CTL_PIN, LED_CTL_PIN_MODE);
    uapi_gpio_set_dir(LED_CTL_PIN, GPIO_DIRECTION_OUTPUT);
    uapi_gpio_set_val(LED_CTL_PIN, GPIO_LEVEL_LOW);

    uapi_pin_set_mode(LED2_CTL_PIN, LED2_CTL_PIN_MODE);
    uapi_gpio_set_dir(LED2_CTL_PIN, GPIO_DIRECTION_OUTPUT);
    uapi_gpio_set_val(LED2_CTL_PIN, GPIO_LEVEL_LOW);
}

void Led_Ctrl(LED_Stauts_t onoff)
{
    if (onoff == LED_OFF)
        uapi_gpio_set_val(LED_CTL_PIN, GPIO_LEVEL_LOW);
    else
        uapi_gpio_set_val(LED_CTL_PIN, GPIO_LEVEL_HIGH);
}

void Led2_Ctrl(LED_Stauts_t onoff)
{
    if (onoff == LED_OFF)
        uapi_gpio_set_val(LED2_CTL_PIN, GPIO_LEVEL_LOW);
    else
        uapi_gpio_set_val(LED2_CTL_PIN, GPIO_LEVEL_HIGH);
}
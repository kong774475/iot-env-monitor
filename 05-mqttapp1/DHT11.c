#include "pinctrl.h"
#include "soc_osal.h"
#include "gpio.h"
#include "osal_debug.h"
#include "cmsis_os2.h"
#include "app_init.h"
#include "los_task.h"
#include "systick.h"

#include "DHT11.h"


void temp_sensor_Init(void)
{
    // 引脚模式 = GPIO
    uapi_pin_set_mode(SENSOR_CTL_PIN, SENSOR_CTL_PIN_MODE);
    // 初始引脚方向
    uapi_gpio_set_dir(SENSOR_CTL_PIN, GPIO_DIRECTION_OUTPUT);    
    // 设置引脚上拉
    uapi_pin_set_pull(SENSOR_CTL_PIN, PIN_PULL_TYPE_UP);    
    // 初始引脚输出高电平
    uapi_gpio_set_val(SENSOR_CTL_PIN, GPIO_LEVEL_HIGH);    
}

void my_udelay(uint16_t us)
{
	for (volatile uint16_t i = 0; i < us; i++)
	{
		volatile uint16_t j = 45;
		while (j--)  ;
	}
}

static SENSOR_Error_t DHT11_Start(void)
{
    uint32_t t = 0;

    // 主机起始信号：拉低 18~20ms
    uapi_gpio_set_dir(SENSOR_CTL_PIN, GPIO_DIRECTION_OUTPUT);
    uapi_gpio_set_val(SENSOR_CTL_PIN, GPIO_LEVEL_LOW);
    osal_mdelay(20);

    // 拉高 20~40us
    uapi_gpio_set_val(SENSOR_CTL_PIN, GPIO_LEVEL_HIGH);
    my_udelay(30);   

     // 切换输入，等待DHT11响应
    uapi_gpio_set_dir(SENSOR_CTL_PIN, GPIO_DIRECTION_INPUT);

 
    // ===================== 等待低电平 =====================
    t = 0;
    while (uapi_gpio_get_val(SENSOR_CTL_PIN) == GPIO_LEVEL_HIGH) 
    {
        my_udelay(1);
        if (++t > 500)  // 超时合理值
            return DHT11_NO_RESPONCE_ERR;
    }

    // ===================== 等待 80us 低电平 =====================
    t = 0;
    while (uapi_gpio_get_val(SENSOR_CTL_PIN) == GPIO_LEVEL_LOW) 
    {
        my_udelay(1);
        if (++t > 200)
            return DHT11_RESPONCE_LOW_ERR;
    }

    // ===================== 等待 80us 高电平 =====================
    t = 0;
    while (uapi_gpio_get_val(SENSOR_CTL_PIN) == GPIO_LEVEL_HIGH) 
    {
        my_udelay(1);
        if (++t > 200)
            return DHT11_RESPONCE_HIGH_ERR;
    }

    return DHT11_OK;
}

static void DHT11_End(void)
{
    /*读取结束，引脚改为输出模式*/
	uapi_gpio_set_dir(SENSOR_CTL_PIN, GPIO_DIRECTION_OUTPUT);
	/*主机拉高*/
	uapi_gpio_set_val(SENSOR_CTL_PIN, GPIO_LEVEL_HIGH);

    osal_mdelay(50);
}

static uint8_t DHT11_ReadByte(void)
{
    uint8_t byte = 0;
    uint8_t i = 0;

    uint16_t timeout = 0;

    // 关闭任务调度，防止读取过程中被打断
	osal_kthread_lock();

    for (i = 0; i < 8; i++) 
    {
        /*每bit以50us低电平标置开始，轮询直到从机发出 的50us 低电平 结束*/
		while (uapi_gpio_get_val(SENSOR_CTL_PIN) == GPIO_LEVEL_LOW)			;

		/*DHT11 以26~28us的高电平表示“0”，以70us高电平表示“1”，
		 *通过检测 x us后的电平即可区别这两个状 ，x 即下面的延时
		 */
		my_udelay(35); // 延时x us 这个延时需要大于数据0持续的时间即可

        if (uapi_gpio_get_val(SENSOR_CTL_PIN) == GPIO_LEVEL_HIGH) /* x us后仍为高电平表示数据“1” */
		{
			/* 等待数据1的高电平结束 */
			timeout = 0;
			while (uapi_gpio_get_val(SENSOR_CTL_PIN) == GPIO_LEVEL_HIGH)
			{
				timeout++;
				if (timeout > 200)
				{
                    osal_kthread_unlock();
					return (uint8_t)ERRCODE_FAIL;
				}
				my_udelay(1);
			}

			byte |= (uint8_t)(0x01 << (7 - i)); // 把第7-i位置1，MSB先行
		}
    }
    // 开启任务调度
    osal_kthread_unlock();
    return byte;
}

SENSOR_Error_t DHT11_GetData(uint8_t *pbuf)
{
    uint8_t i;
    uint8_t checksum = 0;
    SENSOR_Error_t ret;

    ret = DHT11_Start();
    if (ret != DHT11_OK) 
    {
        DHT11_End();
        return ret;
    }
    // 读取 5 字节
    for (i = 0; i < 5; i++) 
    {
        pbuf[i] = DHT11_ReadByte();
    }
    
    DHT11_End();    
    // 校验
    checksum = pbuf[0] + pbuf[1] + pbuf[2] + pbuf[3];
    if (checksum != pbuf[4]) 
    {
        return DHT11_DATA_CRC_ERR;
    }

    return DHT11_OK;
}
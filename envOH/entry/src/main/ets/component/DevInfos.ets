import PrjConstant from "../model/PrjConstant";
import HTTPMgr from "../util/HTTPMgr";
import DevCard from "../model/DevCard";
import ReturnVO from "../util/ReturnVO";
import ThCard from "../model/ThCard";

/**
 * API返回的设备信息接口（兼容多种字段命名）
 */
interface ApiDevInfo {
  devId?: number;
  dev_id?: number;
  id?: number;
  devName?: string | number;
  dev_name?: string | number;
  devPosi?: string | number;
  dev_posi?: string | number;
  temp?: number;
  temperature?: number;
  humi?: number;
  humidity?: number;
  light?: number;
  lux?: number;
  illumination?: number;
  collectTime?: string | number;
  collect_time?: string | number;
}

/**
 * API返回的分页数据结构
 */
interface ApiPageData {
  content?: Object[];
}

@Component
export default struct DevInfos{
  @State devList: DevCard[] | undefined = [];
  @State devThList: ThCard[] | undefined = [];
  @State showThSection: boolean = false;
  private timerId: number = -1;

  // 页面创建时请求数据
  async aboutToAppear() {
    await this.showDevList();
    await this.showThList();
    // 每5秒刷新设备列表
    this.timerId = setInterval(() => {
      this.showDevList();
    }, 5000);
  }

  // 页面销毁时清除定时器
  aboutToDisappear() {
    if (this.timerId !== -1) {
      clearInterval(this.timerId);
      this.timerId = -1;
    }
  }

  build() {
    Column() {
      // 标题
      Text('设备监测列表')
        .fontSize(20)
        .fontWeight(FontWeight.Bold)
        .width('100%')
        .textAlign(TextAlign.Center)
        .padding(15)
        .backgroundColor('#f5f7fa')

      // 设备列表
      List({ space: 1 }) {
        ForEach(this.devList, (item: DevCard, index) => {
          ListItem() {
            this.DevItemComponent(item, index)
          }
          .backgroundColor('#f5f7fa')
        })
      }
      .width('100%')
      .layoutWeight(2)

      // 阈值设置卡片按钮（可点击展开/收起）
      Row({ space: 6 }) {
        Text(this.showThSection ? '▼' : '▶')
          .fontSize(15)
          .fontColor('#333333')
          .fontWeight(FontWeight.Medium)
        Text('阈值设置')
          .fontSize(15)
          .fontWeight(FontWeight.Medium)
          .fontColor('#333333')
      }
      .width('100%')
      .height(42)
      .justifyContent(FlexAlign.Center)
      .backgroundColor('#ffffff')
      .borderRadius(12)
      .shadow({ radius: 4, color: 'rgba(0, 0, 0, 0.04)', offsetX: 0, offsetY: 2 })
      .margin({ left: 12, right: 12, top: 6, bottom: 4 })
      .onClick(() => {
        this.showThSection = !this.showThSection;
        if (this.showThSection && this.devThList?.length === 0) {
          this.showThList();
        }
      })

      // 阈值列表（展开才显示）
      if (this.showThSection) {
        List({ space: 1 }) {
          ForEach(this.devThList, (item: ThCard, index) => {
            ListItem() {
              this.ThItemComponent(item, index)
            }
            .backgroundColor('#f5f7fa')
          })
        }
        .width('100%')
        .layoutWeight(2)
      }
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#f5f7fa')
  }

  /**
   * 从网络获取设备列表并展示
   */
  async showDevList(){
    const postURL = 'envData/queryDevInfos';

    try {
      // 调用你封装的session请求
      const result: ReturnVO = await HTTPMgr.post(postURL) as ReturnVO;

      if (result.code !== 1) {
        // 提示错误信息
        this.getUIContext().getPromptAction().showToast({
          message: result.msg,
          duration: 2000
        });
        return;
      }

      // 直接取 API 返回的数组，兼容多种字段命名
      const rawData = result.content;
      const list: DevCard[] = [];
      if (rawData instanceof Array) {
        const arr = rawData as Object[];
        for (const item of arr) {
          const row = item as ApiDevInfo;
          const devId = Number(row.devId ?? row.dev_id ?? row.id ?? 0);
          if (!devId) { continue; }
          list.push({
            devId: devId,
            devName: String(row.devName ?? row.dev_name ?? ''),
            devPosi: String(row.devPosi ?? row.dev_posi ?? ''),
            temp: Number(row.temp ?? row.temperature ?? 0),
            humi: Number(row.humi ?? row.humidity ?? 0),
            light: Number(row.light ?? row.lux ?? row.illumination ?? 0),
            collectTime: String(row.collectTime ?? row.collect_time ?? ''),
            switchStatus: true
          });
        }
      }
      this.devList = list;

    } catch (err) {
      this.getUIContext().getPromptAction().showToast({
        message: '网络请求异常',
        duration: 2000
      });
      console.error('请求设备列表失败:', JSON.stringify(err));
    }

  }

  /**
   * 从网络获取阈值列表并展示
   */
  async showThList(){
    const postURL = 'dataThreshold/queryByPage';

    try {
      // 调用你封装的session请求
      const result: ReturnVO = await HTTPMgr.post(postURL) as ReturnVO;

      if (result.code !== 1) {
        // 提示错误信息
        this.getUIContext().getPromptAction().showToast({
          message: result.msg,
          duration: 2000
        });
        return;
      }

      // 后端分页包装：content.content 为实际列表
      const pageData = result.content as ApiPageData | undefined;
      const innerList = pageData?.content;
      const list: ThCard[] = [];
      if (innerList instanceof Array) {
        const arr = innerList as Object[];
        for (const item of arr) {
          list.push(item as ThCard);
        }
      }
      this.devThList = list;

    } catch (err) {
      this.getUIContext().getPromptAction().showToast({
        message: '网络请求异常',
        duration: 2000
      });
      console.error('请求阈值列表失败:', JSON.stringify(err));
    }

  }

  /**
   * 阈值类型编号 → 中文名称
   */
  getThTypeName(thType: number | undefined): string {
    if (thType === 1) { return '温度'; }
    if (thType === 2) { return '湿度'; }
    if (thType === 3) { return '光照'; }
    return '未知';
  }

  /**
   * 阈值类型编号 → 对应色调
   */
  getThTypeColor(thType: number | undefined): string {
    if (thType === 1) { return '#F44336'; } // 红色（温度）
    if (thType === 2) { return '#2196F3'; } // 蓝色（湿度）
    if (thType === 3) { return '#FF9800'; } // 橙色（光照）
    return '#999';
  }

  /**
   * 阈值类型编号 → 对应图片
   */
  getThImage(thType: number | undefined): Resource {
    if (thType === 1) { return $r('app.media.wendu'); }
    if (thType === 2) { return $r('app.media.shidu'); }
    if (thType === 3) { return $r('app.media.guangzhao'); }
    return $r('app.media.dev_card');
  }

  /**
   * 列表项组件：设备信息 + Switch
   */
  @Builder
  DevItemComponent(item: DevCard, index: number) {
    Column() {
      Row({ space: 16 }) {
        // ========== 左侧：固定设备板卡图片 ==========
        Image($r('app.media.dev_card'))
          .width(48)
          .height(48)
          .borderRadius(12)
          .backgroundColor('#e8f4fd')

        // ========== 中间：设备信息 ==========
        Column({ space: 4 }) {
          Text(item.devName)
            .fontSize(16)
            .fontWeight(FontWeight.Medium)
            .fontColor('#1a1a1a')

          Text(item.devPosi)
            .fontSize(12)
            .fontColor('#888888')

          // 环境数据带颜色标签
          Row({ space: 8 }) {
            Text(`温度: ${(item.temp ?? 0).toFixed(1)}℃`)
              .fontSize(11)
              .fontColor('#F44336')
            Text(`湿度: ${(item.humi ?? 0).toFixed(1)}%`)
              .fontSize(11)
              .fontColor('#2196F3')
            Text(`光照: ${(item.light ?? 0).toFixed(0)}lux`)
              .fontSize(11)
              .fontColor('#FF9800')
          }
        }
        .layoutWeight(1)
        .alignItems(HorizontalAlign.Start)

        // ========== 右侧：开关 ==========
        Toggle({
          type: ToggleType.Switch,
          isOn: item.switchStatus ?? false
        })
          .onChange((isOn) => {
            item.switchStatus = isOn;

            let cmdNum:number = isOn ? 1 : 0;

            HTTPMgr.get(`${PrjConstant.APP_PATH}mqtt/sendCmd/${item.devId}/${cmdNum}`)

          })
          .width(46)
          .height(26)
          .selectedColor('#4CAF50')
          .switchPointColor('#ffffff')
      }
      .width('100%')
      .padding({ top: 16, bottom: 16, left: 16, right: 16 })
      .alignItems(VerticalAlign.Center)
    }
    .backgroundColor('#ffffff')
    .borderRadius(16)
    .shadow({ radius: 6, color: 'rgba(0, 0, 0, 0.04)', offsetX: 0, offsetY: 2 })
    .margin({ left: 12, right: 12, top: 8, bottom: 8 })
  }


  /**
   * 阈值卡片组件
   */
  @Builder
  ThItemComponent(item: ThCard, index: number) {
    Column() {
      Row({ space: 10 }) {
        // ========== 左侧：类型图标 ==========
        Image(this.getThImage(item.thType))
          .width(44)
          .height(44)
          .borderRadius(12)
          .backgroundColor('#f0f4f8')

        // ========== 中间：标题 + 限值输入 ==========
        Column({ space: 6 }) {
          // 第一行：指标名称标题（在下限输入框上方靠左）
          Row() {
            Text(this.getThTypeName(item.thType))
              .fontSize(16)
              .fontWeight(FontWeight.Bold)
              .fontColor(this.getThTypeColor(item.thType))
            Blank()
          }
          .width('100%')

          // 第二行：下限 + 上限 同行
          Row({ space: 8 }) {
            Text("下限:")
              .fontSize(13)
              .fontColor('#666666')

            TextInput({
              text: `${item.thMin}`,
              placeholder: "下限值"
            })
              .width(64)
              .height(30)
              .fontSize(13)
              .borderRadius(8)
              .backgroundColor('#f5f7fa')
              .onChange((text) => {
                item.thMin = Number(text);
              })

            Text("上限:")
              .fontSize(13)
              .fontColor('#666666')

            TextInput({
              text: `${item.thMax}`,
              placeholder: "上限值"
            })
              .width(64)
              .height(30)
              .fontSize(13)
              .borderRadius(8)
              .backgroundColor('#f5f7fa')
              .onChange((text) => {
                item.thMax = Number(text);
              })
          }
          .alignItems(VerticalAlign.Center)
        }
        .layoutWeight(1)
        .alignItems(HorizontalAlign.Start)

        // ========== 右侧：修改按钮 ==========
        Button("修改", { stateEffect: true })
          .width(58)
          .height(28)
          .fontSize(13)
          .borderRadius(8)
          .backgroundColor('#2E7D32')
          .fontColor('#ffffff')
          .margin({ left: 4 })
          .onClick(async () => {
            const postURL = 'dataThreshold/edit';
            const params: Record<string, object|number|string|undefined> = {
              "thId": item.thId,
              "thMin": item.thMin,
              "thMax": item.thMax
            };
            let rv: ReturnVO = await HTTPMgr.post(postURL, params);
            this.getUIContext().getPromptAction().showToast({
              message: rv.msg,
              duration: 2000
            });
          })
      }
      .width('100%')
      .padding({ top: 12, bottom: 12, left: 14, right: 14 })
      .alignItems(VerticalAlign.Center)
    }
    .backgroundColor('#ffffff')
    .borderRadius(16)
    .shadow({ radius: 6, color: 'rgba(0, 0, 0, 0.04)', offsetX: 0, offsetY: 2 })
    .margin({ left: 12, right: 12, top: 8, bottom: 8 })
  }
}

import { promptAction } from '@kit.ArkUI';
import HTTPMgr from "../util/HTTPMgr";
import PrjConstant from "../model/PrjConstant";
import SysUser from "../model/SysUser";
import ReturnVO from '../util/ReturnVO';

@Entry
@Component
struct Login {
  @State phoneNumber: string = '';
  @State password: string = '';
  @State autoLogin: boolean = false;
  @State showPassword: boolean = false;
  @State rememberPassword: boolean = false;

  // ===== 入场动画状态 =====
  @State bgBlur: number = 0;         // 背景模糊：0 → 8
  @State brandScale: number = 0;     // 品牌区域缩放：0 → 1
  @State brandOpacity: number = 0;   // 品牌区域透明度：0 → 1
  @State uiTranslateY: number = 500; // 表单从下方滑入：500 → 0
  @State uiOpacity: number = 0;      // 表单透明度：0 → 1

  async aboutToAppear() {
    // 阶段1：短暂停留，展示清晰背景
    await this.sleep(150);

    // 阶段2：背景模糊（历时1200ms），品牌和表单均隐藏
    animateTo({ duration: 1200, curve: Curve.EaseInOut }, () => {
      this.bgBlur = 8;
    });

    // 阶段3：模糊结束后，底部表单像窗帘一样向上滑入
    await this.sleep(1200);
    animateTo({ duration: 500, curve: Curve.EaseOut }, () => {
      this.uiTranslateY = 0;
      this.uiOpacity = 1.0;
    });

    // 阶段4：表单显示完成后，再弹出顶部Logo
    await this.sleep(400);
    animateTo({ duration: 400, curve: Curve.EaseOut }, () => {
      this.brandScale = 1.0;
      this.brandOpacity = 1.0;
    });
  }

  sleep(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  /**
   * 登录方法
   */
  async loginHandler() {
    const postURL = `sysUser/login`;
    const postContent: Record<string, object | string |boolean |number>= {
      "userPhone": this.phoneNumber,
      "userPwd": this.password
    }
    const result: ReturnVO = await HTTPMgr.post(postURL, postContent) as ReturnVO;

        if(result.code != 1){
          this.getUIContext().getPromptAction().showToast({
            message: result.msg,
            duration: 2000
          })
          return;
        }

        PrjConstant.logUser = result.content;

        this.getUIContext().getRouter().replaceUrl({
          url: "pages/Index"
        })
  }

  build() {
    Stack() {
      // ===== 背景图片：先清晰 → 渐模糊 =====
      Image($r('app.media.bg'))
        .width('100%')
        .height('100%')
        .objectFit(ImageFit.Cover)
        .blur(this.bgBlur)
        .animation({ duration: 1200, curve: Curve.EaseInOut })

      // 半透明暗色叠层
      Column()
        .width('100%')
        .height('100%')
        .backgroundColor('rgba(0, 0, 0, 0.08)')

      // ===== 整体UI容器 =====
      Column() {
        // ----- 品牌标识区域：双叶Logo -----
        Row() {
          Image($r('app.media.logo'))
            .width(80)
            .height(80)
            .border({ radius: 10 })
            .alignSelf(ItemAlign.Center)
        }
        .width('100%')
        .position({ top: 55 })
        .justifyContent(FlexAlign.Center)
        .scale({ x: this.brandScale, y: this.brandScale })
        .opacity(this.brandOpacity)
        .animation({ duration: 1500, curve: Curve.EaseInOut })

        // ----- 登录表单卡片 -----
        Column({ space: 20 }) {
          // 手机号输入行
          Row() {
            Image($r('app.media.zhanghao'))
              .width(20)
              .height(20)
              .margin({ left: 15, right: 10 })

            TextInput({ placeholder: '请输入手机号码', text: this.phoneNumber })
              .layoutWeight(1)
              .backgroundColor(Color.Transparent)
              .fontColor('#1a1a1a')
              .placeholderColor('#999999')
              .maxLength(11)
              .onChange((value: string) => {
                this.phoneNumber = value;
              })
          }
          .width('90%')
          .height(50)
          .backgroundColor('rgba(255, 255, 255, 0.75)')
          .borderRadius(16)
          .border({ width: 1, color: 'rgba(255, 255, 255, 0.9)' })

          // 密码输入行
          Row() {
            Image($r('app.media.mima'))
              .width(20)
              .height(20)
              .margin({ left: 15, right: 10 })

            TextInput({ placeholder: '请输入密码', text: this.password })
              .layoutWeight(1)
              .backgroundColor(Color.Transparent)
              .fontColor('#1a1a1a')
              .placeholderColor('#999999')
              .type(this.showPassword ? InputType.Normal : InputType.Password)
              .onChange((value: string) => {
                this.password = value;
              })
          }
          .width('90%')
          .height(50)
          .backgroundColor('rgba(255, 255, 255, 0.75)')
          .borderRadius(16)
          .border({ width: 1, color: 'rgba(255, 255, 255, 0.9)' })

          // 记住密码 / 忘记密码
          Row() {
            Checkbox({ name: 'remember', group: 'login' })
              .select(this.rememberPassword)
              .onChange((isChecked: boolean) => {
                this.rememberPassword = isChecked;
              })
              .margin({ right: 8 })
              .width(18)
              .height(18)

            Text('记住密码')
              .fontSize(14)
              .fontColor('#555555')
              .onClick(() => {
                this.rememberPassword = !this.rememberPassword;
              })

            Blank()

            Text('忘记密码？')
              .fontSize(14)
              .fontColor('#555555')
              .onClick(() => {
                promptAction.showToast({ message: '忘记密码功能' });
              })
          }
          .width('90%')
          .margin({ top: 10 })

          // 登录按钮（天蓝色渐变）
          Button('登录')
            .width('90%')
            .height(50)
            .linearGradient({
              direction: GradientDirection.Right,
              colors: [['#42A5F5', 0], ['#1E88E5', 1]]
            })
            .fontColor('#FFFFFF')
            .fontSize(18)
            .fontWeight(FontWeight.Bold)
            .borderRadius(16)
            .margin({ top: 30 })
            .shadow({ radius: 8, color: 'rgba(66, 165, 245, 0.3)', offsetX: 0, offsetY: 4 })
            .onClick(() => {
              this.loginHandler();
            })

          // 其他登录方式分隔线
          Row() {
            Divider()
              .strokeWidth(1)
              .color('#555555')
              .opacity(0.25)
              .layoutWeight(1)

            Text('其他登录方式')
              .fontSize(14)
              .fontColor('#555555')
              .margin({ left: 15, right: 15 })

            Divider()
              .strokeWidth(1)
              .color('#555555')
              .opacity(0.25)
              .layoutWeight(1)
          }
          .width('90%')
          .margin({ top: 20 })

          // 第三方登录图标
          Row({ space: 40 }) {
            Column() {
              Image($r('app.media.weixin'))
                .width(40)
                .height(40)
                .borderRadius(20)
                .backgroundColor('rgba(255, 255, 255, 0.85)')
                .shadow({ radius: 4, color: 'rgba(0, 0, 0, 0.06)', offsetX: 0, offsetY: 2 })

              Text('微信登录')
                .fontSize(12)
                .fontColor('#555555')
                .margin({ top: 8 })
            }
            .onClick(() => {
              promptAction.showToast({ message: '微信登录' });
            })

            Column() {
              Image($r('app.media.qq'))
                .width(40)
                .height(40)
                .borderRadius(20)
                .backgroundColor('rgba(255, 255, 255, 0.85)')
                .shadow({ radius: 4, color: 'rgba(0, 0, 0, 0.06)', offsetX: 0, offsetY: 2 })

              Text('QQ登录')
                .fontSize(12)
                .fontColor('#555555')
                .margin({ top: 8 })
            }
            .onClick(() => {
              promptAction.showToast({ message: 'QQ登录' });
            })

            Column() {
              Image($r('app.media.duanxin'))
                .width(40)
                .height(40)
                .borderRadius(20)
                .backgroundColor('rgba(255, 255, 255, 0.85)')
                .shadow({ radius: 4, color: 'rgba(0, 0, 0, 0.06)', offsetX: 0, offsetY: 2 })

              Text('短信登录')
                .fontSize(12)
                .fontColor('#555555')
                .margin({ top: 8 })
            }
            .onClick(() => {
              promptAction.showToast({ message: '短信登录' });
            })
          }
          .margin({ top: 10 })

          Blank()

          // 底部注册引导
          Row() {
            Text('还没有账号？')
              .fontSize(14)
              .fontColor('#555555')

            Text('立即注册')
              .fontSize(14)
              .fontColor('#2E7D32')
              .fontWeight(FontWeight.Bold)
              .onClick(() => {
                promptAction.showToast({ message: '跳转到注册页面' });
              })
          }
          .margin({ bottom: 30 })
        }
        .width('100%')
        .backgroundColor('rgba(255, 255, 255, 0.55)')
        .borderRadius(28)
        .position({ bottom: 0 })

        // ===== 表单从下方滑入（窗帘效果） =====
        .translate({ y: this.uiTranslateY })
        .opacity(this.uiOpacity)
        .animation({ duration: 500, curve: Curve.EaseOut })
      }
      .width('100%')
      .height('100%')
      .justifyContent(FlexAlign.SpaceBetween)
      .alignItems(HorizontalAlign.Center)
    }
    .width('100%')
    .height('100%')
  }
}

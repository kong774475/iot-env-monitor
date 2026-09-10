import PrjConstant from "../model/PrjConstant";
import HTTPMgr from "../util/HTTPMgr";
import ReturnVO from "../util/ReturnVO";
import UpdatePwdDialog from "./UpdatePwdDialog";

@Component
export default struct UserInfoPage {

  // 声明对话框控制器
  dialogController: CustomDialogController = new CustomDialogController({
    builder: UpdatePwdDialog(),
    alignment: DialogAlignment.Center,
    autoCancel: true,
    customStyle: true
  });

  // 当前登录用户信息（从PrjConstant.logUser回显）
  @State userId: number | undefined = PrjConstant.logUser?.userId;
  @State nickName: string | undefined = PrjConstant.logUser?.nickName;
  @State userPhone: string | undefined = PrjConstant.logUser?.userPhone;

  // 电话校验状态：0-未校验，1-校验通过（打钩），-1-校验失败（打叉）
  @State phoneCheckStatus: number = 0;

  build() {
    Column() {
      // 页面标题区域
      Text('个人信息修改')
        .fontSize(26)
        .fontWeight(FontWeight.Bold)
        .fontColor('#1a1a1a')
        .margin({ top: 12, bottom: 24 })

      // 表单卡片容器
      Column({ space: 24 }) {
        // 手机号输入行
        Row({ space: 12 }) {
          Text('手机号码')
            .fontSize(16)
            .fontColor('#333333')
            .width(80)
            .textAlign(TextAlign.Start)

          Row({ space: 10 }) {
            TextInput({ text: $$this.userPhone, placeholder: '请输入11位手机号' })
              .type(InputType.PhoneNumber)
              .layoutWeight(1)
              .height(46)
              .fontSize(16)
              .backgroundColor('#fafbfc')
              .border({
                width: 1,
                color: this.phoneCheckStatus === -1 ? '#ff4d4f' : '#e0e0e0',
                radius: 12
              })
              .padding({ left: 14, right: 14 })
              .onChange((value: string) => {
                // 电话内容变化时重置校验状态
                this.phoneCheckStatus = 0;
              })
              .onBlur(() => {
                // 失去焦点时校验电话是否存在
                this.checkUserPhone();
              })

            // 校验结果图标+提示
            if (this.phoneCheckStatus === 1) {
              Row({ space: 4 }) {
                Text('✓')
                  .fontSize(22)
                  .fontColor('#4CAF50')
              }
            } else if (this.phoneCheckStatus === -1) {
              Row({ space: 4 }) {
                Text('✗')
                  .fontSize(22)
                  .fontColor('#ff4d4f')
              }
            }
          }
          .layoutWeight(1)
          .alignItems(VerticalAlign.Center)
        }
        .width('100%')
        .alignItems(VerticalAlign.Center)

        // 昵称输入行
        Row({ space: 12 }) {
          Text('用户昵称')
            .fontSize(16)
            .fontColor('#333333')
            .width(80)
            .textAlign(TextAlign.Start)

          TextInput({ text: $$this.nickName, placeholder: '请设置你的昵称' })
            .type(InputType.Normal)
            .layoutWeight(1)
            .height(46)
            .fontSize(16)
            .backgroundColor('#fafbfc')
            .border({ width: 1, color: '#e0e0e0', radius: 12 })
            .padding({ left: 14, right: 14 })
        }
        .width('100%')
        .alignItems(VerticalAlign.Center)
      }
      .width('90%')
      .padding(24)
      .backgroundColor(Color.White)
      .borderRadius(16)
      .shadow({ radius: 12, color: 'rgba(0, 0, 0, 0.06)', offsetX: 0, offsetY: 4 })

      Blank()

      // 底部提交按钮
      Button('保存修改', { stateEffect: true })
        .width('90%')
        .height(52)
        .fontSize(18)
        .fontWeight(FontWeight.Medium)
        .fontColor(Color.White)
        .linearGradient({
          direction: GradientDirection.Right,
          colors: [['#42A5F5', 0], ['#1E88E5', 1]]
        })
        .borderRadius(14)
        .shadow({ radius: 8, color: 'rgba(66, 165, 245, 0.3)', offsetX: 0, offsetY: 4 })
        .opacity(this.phoneCheckStatus === 1 ? 1 : 0.65)
        .onClick(() => {
          this.editUserInfo();
        })
        .margin({ bottom: 20 })

      Button('修改密码')
        .width('90%')
        .height(52)
        .fontSize(18)
        .fontColor(Color.White)
        .fontWeight(FontWeight.Medium)
        .linearGradient({
          direction: GradientDirection.Right,
          colors: [['#42A5F5', 0], ['#1E88E5', 1]]
        })
        .borderRadius(14)
        .shadow({ radius: 8, color: 'rgba(66, 165, 245, 0.3)', offsetX: 0, offsetY: 4 })
        .onClick(() => {
          this.dialogController.open();
        })
    }
    .width('100%')
    .height('100%')
    .padding({ left: 16, right: 16 })
    .backgroundColor('#f5f7fa')
  }

  /**
   * 校验电话是否已被其他用户使用
   * 请求URL：sysUser/queryByUserPhone
   * 请求参数：{ userId, userPhone }
   * code=0 表示用户不存在（电话可用），code=1 表示用户存在（电话已被占用）
   */
  async checkUserPhone() {
    // 基本校验：电话不能为空
    if (!this.userPhone || this.userPhone.trim() === '') {
      this.phoneCheckStatus = -1;
      return;
    }

    const requestUrl = `${PrjConstant.APP_PATH}sysUser/queryByUserPhone`;
    const requestBody: Record<string, object | number | string | boolean | undefined> = {
      "userId": this.userId,
      "userPhone": this.userPhone
    };

    let rv: ReturnVO = await HTTPMgr.post(requestUrl, requestBody) as ReturnVO;

    if (rv.code === 0) {
      // 用户不存在，电话可用，打钩
      this.phoneCheckStatus = 1;
    } else {
      // 用户存在，电话已被占用，打叉
      this.phoneCheckStatus = -1;
    }
  }

  /**
   * 提交用户修改请求
   * 请求URL：sysUser/edit
   * 请求参数：{ userId, nickName, userPhone }
   */
  async editUserInfo() {
    // 校验：电话必须通过校验
    if (this.phoneCheckStatus !== 1) {
      this.getUIContext().getPromptAction().showToast({
        message: '请先确认电话号码可用',
        duration: 2000
      });
      return;
    }

    // 校验：昵称不能为空
    if (!this.nickName || this.nickName.trim() === '') {
      this.getUIContext().getPromptAction().showToast({
        message: '昵称不能为空',
        duration: 2000
      });
      return;
    }

    const requestUrl = `${PrjConstant.APP_PATH}sysUser/edit`;
    const requestBody: Record<string, object | number | string | boolean | undefined> = {
      "userId": this.userId,
      "nickName": this.nickName,
      "userPhone": this.userPhone
    };

    let rv: ReturnVO = await HTTPMgr.post(requestUrl, requestBody) as ReturnVO;

    if (rv.code === 1 && PrjConstant.logUser) {
      // 修改成功，更新PrjConstant中的登录用户信息
      PrjConstant.logUser.nickName = this.nickName;
      PrjConstant.logUser.userPhone = this.userPhone;

      this.getUIContext().getPromptAction().showToast({
        message: '修改成功',
        duration: 2000
      });
    } else {
      this.getUIContext().getPromptAction().showToast({
        message: rv.msg || '修改失败',
        duration: 2000
      });
    }
  }
}

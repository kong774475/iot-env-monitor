import HTTPMgr from '../util/HTTPMgr';
import PrjConstant from '../model/PrjConstant';
import ReturnVO from '../util/ReturnVO';

@CustomDialog
export default struct UpdatePwdDialog {
  // 对话框控制器
  controller?: CustomDialogController;

  // 当前登录用户ID（从PrjConstant获取）
  userId: number|undefined = PrjConstant.logUser?.userId;

  // 三个密码输入框的绑定变量
  @State oldPwd: string = '';
  @State newPwd: string = '';
  @State confirmPwd: string = '';

  // 校验提示信息
  @State errorMsg: string = '';

  build() {
    Column() {
      // 标题
      Text('修改密码')
        .fontSize(22)
        .fontWeight(FontWeight.Bold)
        .fontColor('#1a1a1a')
        .margin({ top: 20, bottom: 25 })

      // 旧密码输入框
      Row() {
        Text('旧密码')
          .fontSize(16)
          .fontColor('#333333')
          .width(90)
          .textAlign(TextAlign.Start)
        TextInput({ text: $$this.oldPwd, placeholder: '请输入旧密码' })
          .type(InputType.Password)
          .layoutWeight(1)
          .height(45)
          .fontSize(16)
          .border({ width: 1, color: '#e0e0e0', radius: 12 })
          .padding({ left: 12, right: 12 })
          .backgroundColor('#fafbfc')
      }
      .width('100%')
      .margin({ bottom: 15 })

      // 新密码输入框
      Row() {
        Text('新密码')
          .fontSize(16)
          .fontColor('#333333')
          .width(90)
          .textAlign(TextAlign.Start)
        TextInput({ text: $$this.newPwd, placeholder: '请输入新密码' })
          .type(InputType.Password)
          .layoutWeight(1)
          .height(45)
          .fontSize(16)
          .border({ width: 1, color: '#e0e0e0', radius: 12 })
          .padding({ left: 12, right: 12 })
          .backgroundColor('#fafbfc')
      }
      .width('100%')
      .margin({ bottom: 15 })

      // 确认新密码输入框
      Row() {
        Text('确认密码')
          .fontSize(16)
          .fontColor('#333333')
          .width(90)
          .textAlign(TextAlign.Start)
        TextInput({ text: $$this.confirmPwd, placeholder: '请再次输入新密码' })
          .type(InputType.Password)
          .layoutWeight(1)
          .height(45)
          .fontSize(16)
          .border({ width: 1, color: '#e0e0e0', radius: 12 })
          .padding({ left: 12, right: 12 })
          .backgroundColor('#fafbfc')
      }
      .width('100%')
      .margin({ bottom: 10 })

      // 错误提示信息
      if (this.errorMsg !== '') {
        Text(this.errorMsg)
          .fontSize(13)
          .fontColor('#F44336')
          .width('100%')
          .margin({ bottom: 10 })
      }

      // 按钮区域
      Row() {
        // 取消按钮
        Button('取消')
          .width('45%')
          .height(45)
          .fontSize(16)
          .fontColor('#666666')
          .backgroundColor('#f5f5f5')
          .borderRadius(12)
          .onClick(() => {
            this.resetForm();
            this.controller?.close();
          })

        // 确认修改按钮
        Button('确认')
          .width('45%')
          .height(45)
          .fontSize(16)
          .fontColor(Color.White)
          .linearGradient({
            direction: GradientDirection.Right,
            colors: [['#42A5F5', 0], ['#1E88E5', 1]]
          })
          .borderRadius(12)
          .shadow({ radius: 6, color: 'rgba(66, 165, 245, 0.3)', offsetX: 0, offsetY: 3 })
          .onClick(() => {
            this.updatePwd();
          })
      }
      .width('100%')
      .justifyContent(FlexAlign.SpaceBetween)
      .margin({ top: 5, bottom: 20 })
    }
    .padding({ left: 24, right: 24 })
    .backgroundColor(Color.White)
    .borderRadius(20)
    .shadow({ radius: 20, color: 'rgba(0, 0, 0, 0.1)', offsetX: 0, offsetY: 8 })
  }

  /**
   * 重置表单
   */
  resetForm() {
    this.oldPwd = '';
    this.newPwd = '';
    this.confirmPwd = '';
    this.errorMsg = '';
  }

  /**
   * 修改密码
   * URL：sysUser/updatePwd/{新密码}
   * 请求参数：{ userId, userPwd(旧密码) }
   */
  async updatePwd() {
    // 校验1：旧密码不能为空
    if (!this.oldPwd || this.oldPwd.trim() === '') {
      this.errorMsg = '请输入旧密码';
      return;
    }

    // 校验2：旧密码位数 >= 3
    if (this.oldPwd.trim().length < 3) {
      this.errorMsg = '旧密码位数不能少于3位';
      return;
    }

    // 校验3：新密码不能为空
    if (!this.newPwd || this.newPwd.trim() === '') {
      this.errorMsg = '请输入新密码';
      return;
    }

    // 校验4：新密码位数 >= 3
    if (this.newPwd.trim().length < 3) {
      this.errorMsg = '新密码位数不能少于3位';
      return;
    }

    // 校验5：确认密码不能为空
    if (!this.confirmPwd || this.confirmPwd.trim() === '') {
      this.errorMsg = '请输入确认密码';
      return;
    }

    // 校验6：确认密码位数 >= 3
    if (this.confirmPwd.trim().length < 3) {
      this.errorMsg = '确认密码位数不能少于3位';
      return;
    }

    // 校验7：新密码与确认密码是否一致
    if (this.newPwd !== this.confirmPwd) {
      this.errorMsg = '新密码与确认密码不一致';
      return;
    }

    // 校验8：新密码不能与旧密码相同
    if (this.oldPwd === this.newPwd) {
      this.errorMsg = '新密码不能与旧密码相同';
      return;
    }

    // 发起修改密码请求
    const requestUrl = `${PrjConstant.APP_PATH}sysUser/updatePwd/${this.newPwd}`;
    const requestBody: Record<string, object | number | string | boolean | undefined> = {
      "userId": this.userId,
      "userPwd": this.oldPwd
    };

    let rv: ReturnVO = await HTTPMgr.post(requestUrl, requestBody) as ReturnVO;

    if (rv.code === 1) {
      // 修改成功
      this.getUIContext().getPromptAction().showToast({
        message: '修改密码成功',
        duration: 2000
      });
      this.resetForm();
      this.controller?.close();
    } else {
      // 修改失败
      this.errorMsg = rv.msg || '修改密码失败';
    }
  }
}

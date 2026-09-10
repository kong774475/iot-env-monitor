import DashBoard from "../component/Dashboard";
import DevInfos from "../component/DevInfos";
import UserInfo from "../component/UserInfo";

@Entry
@Component
struct Index {
  build() {
    // 外层容器
    Stack() {
      // Tabs 组件，页签位于底部，默认选中索引为1（监视）
      Tabs({ barPosition: BarPosition.End, index: 1 }) {
        // 控制页签
        TabContent() {
          // 控制页签的内容区域
          Column() {
            DevInfos();
          }
          .width('100%')
          .height('100%')
          .justifyContent(FlexAlign.Center)
        }
        .tabBar('控制')

        // 监视页签（默认选中）
        TabContent() {
          // 监视页签的内容区域
          Column() {
            DashBoard();
          }
          .width('100%')
          .height('100%')
          .justifyContent(FlexAlign.Center)
        }
        .tabBar('监视')

        // 我的页签
        TabContent() {
          // 我的页签的内容区域
          Column() {
            UserInfo();
          }
          .width('100%')
          .height('100%')
          .justifyContent(FlexAlign.Center)
        }
        .tabBar('我的')
      }
      .width('100%')
      .height('100%')
      .barMode(BarMode.Fixed)         // 页签固定模式，均分底部宽度
      .scrollable(false)              // 禁止左右滑动切换
      .barBackgroundColor('#ffffff')
    }
    .width('100%')
    .height('100%')
  }
}

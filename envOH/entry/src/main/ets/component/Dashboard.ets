import PrjConstant from "../model/PrjConstant";

@Component
export default struct DashBoard{
  webCtl: WebController = new WebController();

  build() {
    Web({
      src: `${PrjConstant.APP_PATH}dashboard.html`,
      controller: this.webCtl
    })
      .width("100%")
      .height("100%")
      .javaScriptAccess(true)    // 必开：允许JS运行，图表才能渲染
      .domStorageAccess(true)   // 必开：支持页面本地存储
      .cacheMode(CacheMode.None); // 开发关闭缓存，避免旧页面缓存卡死
  }
}
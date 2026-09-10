import { rcp } from '@kit.RemoteCommunicationKit';
import { BusinessError } from '@kit.BasicServicesKit';
import PrjConstant from "../model/PrjConstant";
import ReturnVO2 from "./ReturnVO";

export default class HTTPMgr {
  //定义一个静态的session对象
  static session: rcp.Session = rcp.createSession({
    baseAddress: PrjConstant.APP_PATH,
  });

  /**
   * 封装get请求
   * @param url
   * @returns
   */
  static async get(url: string, params?: Record<string, object|number|string|boolean|undefined>): Promise<ReturnVO2> {
    try {
      let requestUrl = url;
      if (params) {
        const queryArr: string[] = [];
        // 获取键值对数组
        const entries = Object.entries(params);
        // 用普通for下标遍历，取消解构
        for (let i = 0; i < entries.length; i++) {
          const item = entries[i];
          const key = item[0];
          const val = item[1];
          queryArr.push(`${encodeURIComponent(key)}=${encodeURIComponent(String(val))}`);
        }
        if (queryArr.length > 0) {
          requestUrl += "?" + queryArr.join("&");
        }
      }
      //使用session对象发起get请求
      const response = await HTTPMgr.session.get(requestUrl);
      //强转返回对象为ReturnVO2类型;
      // const rv : ReturnVO2 = JSON.parse(JSON.stringify(response)) as ReturnVO2;
      const rv : ReturnVO2 = JSON.parse(JSON.stringify(response)) as ReturnVO2;
      return rv;
    } catch (error) {
      const err = error as BusinessError;
      console.error("clrm GET请求异常：", err.code, err.message);

      // 异常场景返回错误的统一返回体，满足返回值类型约束
      const errRv: ReturnVO2 = {
        code: err.code,
        msg: err.message
      }
      return errRv;
    }
  }

  /**
   * 封装post请求
   * @param url
   * @param params
   * @returns
   */
  static async post(url: string, params?: Record<string, object|number|string|boolean|undefined>): Promise<ReturnVO2> {
    try {
      //使用session对象发起post请求
      const response = await HTTPMgr.session.post(url, params);
      //强转返回对象为ReturnVO2类型;
      const rv : ReturnVO2 = JSON.parse(JSON.stringify(response)) as ReturnVO2;

      return rv;
    } catch (error) {
      const err = error as BusinessError;
      console.error("clrm POST请求异常：", err.code, err.message);

      // 异常场景返回错误的统一返回体，满足返回值类型约束
      const errRv: ReturnVO2 = {
        code: err.code,
        msg: err.message
      }
      return errRv;
    }
  }
}
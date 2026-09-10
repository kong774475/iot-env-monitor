import SysUser from "../model/SysUser";

export default class PrjConstant {
    static readonly APP_PATH: string = "http://192.168.237.94:8081/envMgr/";

    static logUser: SysUser|undefined = {};

}
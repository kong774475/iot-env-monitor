export default interface DevCard {
  devId: number;
  devName: string;
  devPosi: string;
  temp: number;
  humi: number;
  light: number;
  collectTime: string;
  // 开关状态（本地状态，不来自接口）
  switchStatus?: boolean;
}

export default interface ThCard {
  // primary key
  thId?:number;
  //指标类型：1温度 2湿度 3光照
  thType?:number;
  //最小值
  thMin?:number;
  //最大值
  thMax?:number;
}
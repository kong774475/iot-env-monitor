export default interface ReturnVO {
  //返回编码 1：成功  0:失败  < 0 特殊标记位
  code: number;

  //消息
  msg: string;

  //返回内容
  content?: Record<string, object>;
}
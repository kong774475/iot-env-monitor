export default interface  UserAccount{
  userId        ?:number;
  userPhone     ?:string;
  userPassword  ?:string;
  nickName      ?:string;
  avatarId      ?:string;
  createTime    ?:string;
  lastLoginTime ?:string;
  status        ?:number;
}
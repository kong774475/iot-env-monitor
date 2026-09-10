export default interface SecretRealm{
    realmId?: number;// primary key
    realmName?: string;
    unlockLevel?: number;
    totalLayers?: number;
    description?: string;
    realmPic?: string;
    pageUrl?: string;
}
/*    */ package flex.management.jmx;
/*    */ 
/*    */ import javax.management.MalformedObjectNameException;
/*    */ 
/*    */ public class ObjectInstance
/*    */ {
/*    */   public ObjectName objectName;
/*    */   public String className;
/*    */ 
/*    */   public ObjectInstance()
/*    */   {
/*    */   }
/*    */ 
/*    */   public ObjectInstance(javax.management.ObjectInstance objectInstance)
/*    */   {
/* 55 */     this.objectName = new ObjectName(objectInstance.getObjectName());
/* 56 */     this.className = objectInstance.getClassName();
/*    */   }
/*    */ 
/*    */   public javax.management.ObjectInstance toObjectInstance()
/*    */     throws MalformedObjectNameException
/*    */   {
/* 67 */     return new javax.management.ObjectInstance(this.objectName.toObjectName(), this.className);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.jmx.ObjectInstance
 * JD-Core Version:    0.6.0
 */
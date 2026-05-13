/*    */ package flex.management.jmx;
/*    */ 
/*    */ public class MBeanParameterInfo
/*    */ {
/*    */   public String name;
/*    */   public String type;
/*    */   public String description;
/*    */ 
/*    */   public MBeanParameterInfo()
/*    */   {
/*    */   }
/*    */ 
/*    */   public MBeanParameterInfo(javax.management.MBeanParameterInfo mbeanParameterInfo)
/*    */   {
/* 57 */     this.name = mbeanParameterInfo.getName();
/* 58 */     this.type = mbeanParameterInfo.getType();
/* 59 */     this.description = mbeanParameterInfo.getDescription();
/*    */   }
/*    */ 
/*    */   public javax.management.MBeanParameterInfo toMBeanParameterInfo()
/*    */   {
/* 70 */     return new javax.management.MBeanParameterInfo(this.name, this.type, this.description);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.jmx.MBeanParameterInfo
 * JD-Core Version:    0.6.0
 */
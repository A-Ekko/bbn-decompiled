/*    */ package flex.management.jmx;
/*    */ 
/*    */ public class MBeanAttributeInfo
/*    */ {
/*    */   public String name;
/*    */   public String type;
/*    */   public String description;
/*    */   public boolean readable;
/*    */   public boolean writable;
/*    */   public boolean isIs;
/*    */ 
/*    */   public MBeanAttributeInfo()
/*    */   {
/*    */   }
/*    */ 
/*    */   public MBeanAttributeInfo(javax.management.MBeanAttributeInfo mbeanAttributeInfo)
/*    */   {
/* 74 */     this.name = mbeanAttributeInfo.getName();
/* 75 */     this.type = mbeanAttributeInfo.getType();
/* 76 */     this.description = mbeanAttributeInfo.getDescription();
/* 77 */     this.readable = mbeanAttributeInfo.isReadable();
/* 78 */     this.writable = mbeanAttributeInfo.isWritable();
/* 79 */     this.isIs = mbeanAttributeInfo.isIs();
/*    */   }
/*    */ 
/*    */   public javax.management.MBeanAttributeInfo toMBeanAttributeInfo()
/*    */   {
/* 90 */     return new javax.management.MBeanAttributeInfo(this.name, this.type, this.description, this.readable, this.writable, this.isIs);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.jmx.MBeanAttributeInfo
 * JD-Core Version:    0.6.0
 */
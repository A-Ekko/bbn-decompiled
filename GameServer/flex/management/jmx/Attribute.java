/*    */ package flex.management.jmx;
/*    */ 
/*    */ public class Attribute
/*    */ {
/*    */   public String name;
/*    */   public Object value;
/*    */ 
/*    */   public Attribute()
/*    */   {
/*    */   }
/*    */ 
/*    */   public Attribute(javax.management.Attribute attribute)
/*    */   {
/* 52 */     this.name = attribute.getName();
/* 53 */     this.value = attribute.getValue();
/*    */   }
/*    */ 
/*    */   public javax.management.Attribute toAttribute()
/*    */   {
/* 63 */     return new javax.management.Attribute(this.name, this.value);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.jmx.Attribute
 * JD-Core Version:    0.6.0
 */
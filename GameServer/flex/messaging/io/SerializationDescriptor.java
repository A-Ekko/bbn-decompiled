/*    */ package flex.messaging.io;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ 
/*    */ public class SerializationDescriptor extends HashMap
/*    */ {
/*    */   static final long serialVersionUID = 1828426777611186569L;
/*    */   private List excludes;
/*    */ 
/*    */   public List getExcludesForInstance(Object instance)
/*    */   {
/* 52 */     return this.excludes;
/*    */   }
/*    */ 
/*    */   public List getExcludes()
/*    */   {
/* 60 */     return this.excludes;
/*    */   }
/*    */ 
/*    */   public void setExcludes(List excludes)
/*    */   {
/* 65 */     this.excludes = excludes;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 70 */     return "[ excludes: " + this.excludes + "]";
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.SerializationDescriptor
 * JD-Core Version:    0.6.0
 */
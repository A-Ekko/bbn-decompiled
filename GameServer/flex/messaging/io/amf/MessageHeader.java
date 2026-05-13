/*    */ package flex.messaging.io.amf;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class MessageHeader
/*    */   implements Serializable
/*    */ {
/*    */   static final long serialVersionUID = -4535655145953153945L;
/*    */   private String name;
/*    */   private boolean mustUnderstand;
/*    */   private Object data;
/*    */ 
/*    */   public MessageHeader()
/*    */   {
/*    */   }
/*    */ 
/*    */   public MessageHeader(String name, boolean mustUnderstand, Object data)
/*    */   {
/* 49 */     this.name = name;
/* 50 */     this.mustUnderstand = mustUnderstand;
/* 51 */     this.data = data;
/*    */   }
/*    */ 
/*    */   public String getName()
/*    */   {
/* 57 */     return this.name;
/*    */   }
/*    */ 
/*    */   public void setName(String name)
/*    */   {
/* 63 */     this.name = name;
/*    */   }
/*    */ 
/*    */   public boolean getMustUnderstand()
/*    */   {
/* 69 */     return this.mustUnderstand;
/*    */   }
/*    */ 
/*    */   public void setMustUnderstand(boolean mustUnderstand)
/*    */   {
/* 75 */     this.mustUnderstand = mustUnderstand;
/*    */   }
/*    */ 
/*    */   public Object getData()
/*    */   {
/* 81 */     return this.data;
/*    */   }
/*    */ 
/*    */   public void setData(Object data)
/*    */   {
/* 86 */     this.data = data;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.MessageHeader
 * JD-Core Version:    0.6.0
 */
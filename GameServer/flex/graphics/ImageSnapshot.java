/*    */ package flex.graphics;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class ImageSnapshot extends HashMap
/*    */ {
/*    */   private static final long serialVersionUID = 7914317354403674061L;
/*    */   private Map properties;
/*    */   private String contentType;
/*    */   private byte[] data;
/*    */   private int height;
/*    */   private int width;
/*    */ 
/*    */   public String getContentType()
/*    */   {
/* 26 */     return this.contentType;
/*    */   }
/*    */ 
/*    */   public void setContentType(String value)
/*    */   {
/* 31 */     this.contentType = value;
/*    */   }
/*    */ 
/*    */   public byte[] getData()
/*    */   {
/* 39 */     return this.data;
/*    */   }
/*    */ 
/*    */   public void setData(byte[] value)
/*    */   {
/* 44 */     this.data = value;
/*    */   }
/*    */ 
/*    */   public int getHeight()
/*    */   {
/* 52 */     return this.height;
/*    */   }
/*    */ 
/*    */   public void setHeight(int value)
/*    */   {
/* 57 */     this.height = value;
/*    */   }
/*    */ 
/*    */   public Map getProperties()
/*    */   {
/* 65 */     return this.properties;
/*    */   }
/*    */ 
/*    */   public void setProperties(Map value)
/*    */   {
/* 70 */     this.properties = value;
/*    */   }
/*    */ 
/*    */   public int getWidth()
/*    */   {
/* 78 */     return this.width;
/*    */   }
/*    */ 
/*    */   public void setWidth(int value)
/*    */   {
/* 83 */     this.width = value;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.graphics.ImageSnapshot
 * JD-Core Version:    0.6.0
 */
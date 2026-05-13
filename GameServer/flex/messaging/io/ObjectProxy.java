/*    */ package flex.messaging.io;
/*    */ 
/*    */ import java.io.Externalizable;
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectInput;
/*    */ import java.io.ObjectOutput;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class ObjectProxy extends HashMap
/*    */   implements Externalizable
/*    */ {
/*    */   static final long serialVersionUID = 6978936573135117900L;
/*    */ 
/*    */   public ObjectProxy()
/*    */   {
/*    */   }
/*    */ 
/*    */   public ObjectProxy(int initialCapacity)
/*    */   {
/* 48 */     super(initialCapacity);
/*    */   }
/*    */ 
/*    */   public ObjectProxy(int initialCapacity, float loadFactor)
/*    */   {
/* 53 */     super(initialCapacity, loadFactor);
/*    */   }
/*    */ 
/*    */   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
/*    */   {
/* 58 */     Object value = in.readObject();
/* 59 */     if ((value instanceof Map))
/*    */     {
/* 61 */       putAll((Map)value);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void writeExternal(ObjectOutput out)
/*    */     throws IOException
/*    */   {
/* 71 */     Map map = new HashMap();
/* 72 */     map.putAll(this);
/* 73 */     out.writeObject(map);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.ObjectProxy
 * JD-Core Version:    0.6.0
 */
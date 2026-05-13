/*    */ package flex.messaging.io;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectInput;
/*    */ import java.io.ObjectOutput;
/*    */ import java.util.Iterator;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class ManagedObjectProxy extends ObjectProxy
/*    */ {
/*    */   static final long serialVersionUID = 255140415084514484L;
/*    */ 
/*    */   public ManagedObjectProxy()
/*    */   {
/*    */   }
/*    */ 
/*    */   public ManagedObjectProxy(int initialCapacity)
/*    */   {
/* 37 */     super(initialCapacity);
/*    */   }
/*    */ 
/*    */   public ManagedObjectProxy(int initialCapacity, float loadFactor)
/*    */   {
/* 42 */     super(initialCapacity, loadFactor);
/*    */   }
/*    */ 
/*    */   public void writeExternal(ObjectOutput out) throws IOException
/*    */   {
/* 47 */     int count = size();
/* 48 */     out.writeInt(count);
/*    */ 
/* 53 */     Iterator it = keySet().iterator();
/* 54 */     while (it.hasNext())
/*    */     {
/* 56 */       Object key = it.next();
/* 57 */       out.writeObject(key);
/* 58 */       out.writeObject(get(key));
/*    */     }
/*    */   }
/*    */ 
/*    */   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
/*    */   {
/* 64 */     int count = in.readInt();
/*    */ 
/* 66 */     for (int i = 0; i < count; i++)
/*    */     {
/* 68 */       Object key = in.readObject();
/* 69 */       Object value = in.readObject();
/* 70 */       put(key, value);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.ManagedObjectProxy
 * JD-Core Version:    0.6.0
 */
/*    */ package flex.messaging.io;
/*    */ 
/*    */ import java.io.Externalizable;
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectInput;
/*    */ import java.io.ObjectOutput;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ 
/*    */ public class ArrayCollection extends ArrayList
/*    */   implements Externalizable
/*    */ {
/*    */   private static final long serialVersionUID = 8037277879661457358L;
/* 35 */   private SerializationDescriptor descriptor = null;
/*    */ 
/*    */   public ArrayCollection()
/*    */   {
/*    */   }
/*    */ 
/*    */   public ArrayCollection(Collection c)
/*    */   {
/* 44 */     super(c);
/*    */   }
/*    */ 
/*    */   public ArrayCollection(int initialCapacity)
/*    */   {
/* 49 */     super(initialCapacity);
/*    */   }
/*    */ 
/*    */   public Object[] getSource()
/*    */   {
/* 54 */     return toArray();
/*    */   }
/*    */ 
/*    */   public void setDescriptor(SerializationDescriptor desc)
/*    */   {
/* 59 */     this.descriptor = desc;
/*    */   }
/*    */ 
/*    */   public void setSource(Object[] s)
/*    */   {
/* 64 */     if (s != null)
/*    */     {
/* 66 */       if (size() > 0) {
/* 67 */         clear();
/*    */       }
/* 69 */       for (int i = 0; i < s.length; i++)
/*    */       {
/* 71 */         add(s[i]);
/*    */       }
/*    */     }
/*    */     else
/*    */     {
/* 76 */       clear();
/*    */     }
/*    */   }
/*    */ 
/*    */   public void setSource(Collection s)
/*    */   {
/* 82 */     addAll(s);
/*    */   }
/*    */ 
/*    */   public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException
/*    */   {
/* 87 */     Object s = input.readObject();
/* 88 */     if ((s instanceof Collection))
/* 89 */       s = ((Collection)s).toArray();
/* 90 */     Object[] source = (Object[])(Object[])s;
/* 91 */     setSource(source);
/*    */   }
/*    */ 
/*    */   public void writeExternal(ObjectOutput output) throws IOException
/*    */   {
/* 96 */     if (this.descriptor == null) {
/* 97 */       output.writeObject(getSource());
/*    */     }
/*    */     else {
/* 100 */       Object[] source = getSource();
/* 101 */       if (source == null) {
/* 102 */         output.writeObject(null);
/*    */       }
/*    */       else {
/* 105 */         for (int i = 0; i < source.length; i++)
/*    */         {
/* 107 */           Object item = source[i];
/* 108 */           if (item == null) {
/* 109 */             source[i] = null;
/*    */           }
/*    */           else {
/* 112 */             PropertyProxy proxy = PropertyProxyRegistry.getProxy(item);
/* 113 */             proxy = (PropertyProxy)proxy.clone();
/* 114 */             proxy.setDescriptor(this.descriptor);
/* 115 */             proxy.setDefaultInstance(item);
/* 116 */             source[i] = proxy;
/*    */           }
/*    */         }
/* 119 */         output.writeObject(source);
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.ArrayCollection
 * JD-Core Version:    0.6.0
 */
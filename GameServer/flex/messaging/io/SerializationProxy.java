/*     */ package flex.messaging.io;
/*     */ 
/*     */ import flex.messaging.io.amf.Amf3Input;
/*     */ import java.io.Externalizable;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInput;
/*     */ import java.io.ObjectOutput;
/*     */ 
/*     */ public class SerializationProxy extends MapProxy
/*     */   implements Externalizable
/*     */ {
/*     */   static final long serialVersionUID = -2544463435731984479L;
/*     */ 
/*     */   public SerializationProxy()
/*     */   {
/*  51 */     super(null);
/*  52 */     this.externalizable = false;
/*     */   }
/*     */ 
/*     */   public SerializationProxy(Object defaultInstance)
/*     */   {
/*  57 */     super(defaultInstance);
/*  58 */     this.externalizable = false;
/*     */   }
/*     */ 
/*     */   public boolean isExternalizable()
/*     */   {
/*  63 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isExternalizable(Object instance)
/*     */   {
/*  68 */     return false;
/*     */   }
/*     */ 
/*     */   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
/*     */   {
/*  73 */     Object saveObjectTable = null;
/*  74 */     Object saveTraitsTable = null;
/*  75 */     Object saveStringTable = null;
/*  76 */     Amf3Input in3 = null;
/*     */ 
/*  78 */     if ((in instanceof Amf3Input)) {
/*  79 */       in3 = (Amf3Input)in;
/*     */     }
/*     */     try
/*     */     {
/*  83 */       if (in3 != null)
/*     */       {
/*  85 */         saveObjectTable = in3.saveObjectTable();
/*  86 */         saveTraitsTable = in3.saveTraitsTable();
/*  87 */         saveStringTable = in3.saveStringTable();
/*     */       }
/*     */ 
/*  90 */       this.defaultInstance = in.readObject();
/*     */     }
/*     */     finally
/*     */     {
/*  94 */       if (in3 != null)
/*     */       {
/*  96 */         in3.restoreObjectTable(saveObjectTable);
/*  97 */         in3.restoreTraitsTable(saveTraitsTable);
/*  98 */         in3.restoreStringTable(saveStringTable);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeExternal(ObjectOutput out)
/*     */     throws IOException
/*     */   {
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 110 */     return "[Proxy(" + this.defaultInstance + ") descriptor=" + this.descriptor + "]";
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/* 115 */     SerializationProxy proxy = new SerializationProxy();
/* 116 */     proxy.setCloneFieldsFrom(proxy);
/* 117 */     return proxy;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.SerializationProxy
 * JD-Core Version:    0.6.0
 */
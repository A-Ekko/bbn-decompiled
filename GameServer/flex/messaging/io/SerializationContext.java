/*     */ package flex.messaging.io;
/*     */ 
/*     */ import flex.messaging.util.ClassUtil;
/*     */ import java.io.Serializable;
/*     */ 
/*     */ public class SerializationContext
/*     */   implements Serializable
/*     */ {
/*     */   static final long serialVersionUID = -8260273707611027509L;
/*     */   public boolean legacyXMLDocument;
/*     */   public boolean legacyXMLNamespaces;
/*     */   public boolean legacyCollection;
/*     */   public boolean legacyMap;
/*     */   public boolean legacyThrowable;
/*     */   public boolean legacyBigNumbers;
/*     */   public boolean restoreReferences;
/*     */   public boolean supportRemoteClass;
/*     */   public boolean supportDatesByReference;
/*  53 */   public boolean enableSmallMessages = true;
/*     */ 
/*  65 */   public boolean instantiateTypes = true;
/*  66 */   public boolean ignorePropertyErrors = true;
/*  67 */   public boolean logPropertyErrors = false;
/*     */   private Class deserializer;
/*     */   private Class serializer;
/* 128 */   private static ThreadLocal contexts = new ThreadLocal();
/*     */ 
/*     */   public Class getDeserializerClass()
/*     */   {
/*  78 */     return this.deserializer;
/*     */   }
/*     */ 
/*     */   public void setDeserializerClass(Class c)
/*     */   {
/*  83 */     this.deserializer = c;
/*     */   }
/*     */ 
/*     */   public Class getSerializerClass()
/*     */   {
/*  88 */     return this.serializer;
/*     */   }
/*     */ 
/*     */   public void setSerializerClass(Class c)
/*     */   {
/*  93 */     this.serializer = c;
/*     */   }
/*     */ 
/*     */   public MessageDeserializer newMessageDeserializer()
/*     */   {
/*  98 */     MessageDeserializer deserializer = (MessageDeserializer)ClassUtil.createDefaultInstance(getDeserializerClass(), MessageDeserializer.class);
/*  99 */     return deserializer;
/*     */   }
/*     */ 
/*     */   public MessageSerializer newMessageSerializer()
/*     */   {
/* 104 */     MessageSerializer serializer = (MessageSerializer)ClassUtil.createDefaultInstance(getSerializerClass(), MessageSerializer.class);
/* 105 */     return serializer;
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/* 110 */     SerializationContext context = new SerializationContext();
/* 111 */     context.legacyXMLDocument = this.legacyXMLDocument;
/* 112 */     context.legacyXMLNamespaces = this.legacyXMLNamespaces;
/* 113 */     context.legacyCollection = this.legacyCollection;
/* 114 */     context.legacyMap = this.legacyMap;
/* 115 */     context.legacyThrowable = this.legacyThrowable;
/* 116 */     context.legacyBigNumbers = this.legacyBigNumbers;
/* 117 */     context.restoreReferences = this.restoreReferences;
/* 118 */     context.supportRemoteClass = this.supportRemoteClass;
/* 119 */     context.supportDatesByReference = this.supportDatesByReference;
/* 120 */     context.instantiateTypes = this.instantiateTypes;
/* 121 */     context.ignorePropertyErrors = this.ignorePropertyErrors;
/* 122 */     context.logPropertyErrors = this.logPropertyErrors;
/* 123 */     context.deserializer = this.deserializer;
/* 124 */     context.serializer = this.serializer;
/* 125 */     return context;
/*     */   }
/*     */ 
/*     */   public static void setSerializationContext(SerializationContext context)
/*     */   {
/* 137 */     contexts.set(context);
/*     */   }
/*     */ 
/*     */   public static SerializationContext getSerializationContext()
/*     */   {
/* 145 */     SerializationContext sc = (SerializationContext)contexts.get();
/* 146 */     if (sc == null)
/*     */     {
/* 148 */       sc = new SerializationContext();
/* 149 */       setSerializationContext(sc);
/*     */     }
/* 151 */     return sc;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.SerializationContext
 * JD-Core Version:    0.6.0
 */
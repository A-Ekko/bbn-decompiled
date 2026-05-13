/*     */ package flex.messaging.io;
/*     */ 
/*     */ import flex.messaging.FlexContext;
/*     */ import flex.messaging.MessageBroker;
/*     */ import flex.messaging.io.amf.ASObject;
/*     */ import flex.messaging.io.amf.translator.ASTranslator;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class TypeMarshallingContext
/*     */ {
/*  33 */   private static ThreadLocal contexts = new ThreadLocal();
/*  34 */   private static ThreadLocal marshallers = new ThreadLocal();
/*     */   private IdentityHashMap knownObjects;
/*     */   private ClassLoader classLoader;
/*     */ 
/*     */   public static void setTypeMarshallingContext(TypeMarshallingContext context)
/*     */   {
/*  52 */     contexts.set(context);
/*     */   }
/*     */ 
/*     */   public static TypeMarshallingContext getTypeMarshallingContext()
/*     */   {
/*  60 */     TypeMarshallingContext context = (TypeMarshallingContext)contexts.get();
/*  61 */     if (context == null)
/*     */     {
/*  63 */       context = new TypeMarshallingContext();
/*  64 */       setTypeMarshallingContext(context);
/*     */     }
/*  66 */     return context;
/*     */   }
/*     */ 
/*     */   public static void setTypeMarshaller(TypeMarshaller marshaller)
/*     */   {
/*  77 */     marshallers.set(marshaller);
/*     */   }
/*     */ 
/*     */   public static TypeMarshaller getTypeMarshaller()
/*     */   {
/*  85 */     TypeMarshaller marshaller = (TypeMarshaller)marshallers.get();
/*  86 */     if (marshaller == null)
/*     */     {
/*  88 */       marshaller = new ASTranslator();
/*  89 */       setTypeMarshaller(marshaller);
/*     */     }
/*     */ 
/*  92 */     return marshaller;
/*     */   }
/*     */ 
/*     */   public ClassLoader getClassLoader()
/*     */   {
/* 101 */     if (this.classLoader != null) {
/* 102 */       return this.classLoader;
/*     */     }
/* 104 */     return FlexContext.getMessageBroker() == null ? null : FlexContext.getMessageBroker().getClassLoader();
/*     */   }
/*     */ 
/*     */   public void setClassLoader(ClassLoader loader)
/*     */   {
/* 114 */     this.classLoader = loader;
/*     */   }
/*     */ 
/*     */   public IdentityHashMap getKnownObjects()
/*     */   {
/* 123 */     if (this.knownObjects == null) {
/* 124 */       this.knownObjects = new IdentityHashMap(64);
/*     */     }
/* 126 */     return this.knownObjects;
/*     */   }
/*     */ 
/*     */   public void setKnownObjects(IdentityHashMap knownObjects)
/*     */   {
/* 135 */     this.knownObjects = knownObjects;
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/* 143 */     if (this.knownObjects != null)
/* 144 */       this.knownObjects.clear();
/*     */   }
/*     */ 
/*     */   public static String getType(Object obj)
/*     */   {
/* 156 */     String type = null;
/*     */ 
/* 158 */     if ((obj != null) && ((obj instanceof Map)))
/*     */     {
/* 160 */       Map map = (Map)obj;
/*     */ 
/* 163 */       if ((map instanceof ASObject))
/*     */       {
/* 165 */         ASObject aso = (ASObject)map;
/* 166 */         type = aso.getType();
/*     */       }
/*     */ 
/* 169 */       SerializationContext sc = SerializationContext.getSerializationContext();
/*     */ 
/* 171 */       if ((type == null) && (sc.supportRemoteClass))
/*     */       {
/* 173 */         Object registerClass = map.get("_remoteClass");
/* 174 */         if ((registerClass != null) && ((registerClass instanceof String)))
/*     */         {
/* 176 */           type = (String)registerClass;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 181 */     return type;
/*     */   }
/*     */ 
/*     */   public static void clearThreadLocalObjects()
/*     */   {
/* 189 */     contexts.set(null);
/* 190 */     marshallers.set(null);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.TypeMarshallingContext
 * JD-Core Version:    0.6.0
 */
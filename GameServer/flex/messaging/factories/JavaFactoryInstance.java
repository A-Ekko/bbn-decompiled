/*     */ package flex.messaging.factories;
/*     */ 
/*     */ import flex.messaging.FactoryInstance;
/*     */ import flex.messaging.FlexConfigurable;
/*     */ import flex.messaging.FlexContext;
/*     */ import flex.messaging.FlexSession;
/*     */ import flex.messaging.MessageBroker;
/*     */ import flex.messaging.config.ConfigMap;
/*     */ import flex.messaging.util.ClassUtil;
/*     */ 
/*     */ public class JavaFactoryInstance extends FactoryInstance
/*     */ {
/*  21 */   Object applicationInstance = null;
/*  22 */   Class javaClass = null;
/*     */   String attributeId;
/*     */ 
/*     */   public JavaFactoryInstance(JavaFactory factory, String id, ConfigMap properties)
/*     */   {
/*  35 */     super(factory, id, properties);
/*     */   }
/*     */ 
/*     */   public void setAttributeId(String attributeId)
/*     */   {
/*  45 */     this.attributeId = attributeId;
/*     */   }
/*     */ 
/*     */   public String getAttributeId()
/*     */   {
/*  55 */     return this.attributeId;
/*     */   }
/*     */ 
/*     */   public void setSource(String source)
/*     */   {
/*  64 */     super.setSource(source);
/*  65 */     if (this.javaClass != null)
/*  66 */       this.javaClass = null;
/*     */   }
/*     */ 
/*     */   public Object createInstance()
/*     */   {
/*  77 */     Object inst = ClassUtil.createDefaultInstance(getInstanceClass(), null);
/*     */ 
/*  79 */     if ((inst instanceof FlexConfigurable)) {
/*  80 */       ((FlexConfigurable)inst).initialize(getId(), getProperties());
/*     */     }
/*  82 */     return inst;
/*     */   }
/*     */ 
/*     */   public Class getInstanceClass()
/*     */   {
/*  90 */     if (this.javaClass == null) {
/*  91 */       this.javaClass = ClassUtil.createClass(getSource(), FlexContext.getMessageBroker() == null ? getClass().getClassLoader() : FlexContext.getMessageBroker().getClassLoader());
/*     */     }
/*     */ 
/*  95 */     return this.javaClass;
/*     */   }
/*     */ 
/*     */   public void operationComplete(Object instance)
/*     */   {
/* 104 */     if (getScope().equalsIgnoreCase("session"))
/*     */     {
/* 106 */       FlexSession session = FlexContext.getFlexSession();
/* 107 */       if ((session != null) && (session.isValid()))
/*     */       {
/* 109 */         session.setAttribute(getAttributeId(), instance);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 117 */     return "JavaFactory instance for id=" + getId() + " source=" + getSource() + " scope=" + getScope();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.factories.JavaFactoryInstance
 * JD-Core Version:    0.6.0
 */
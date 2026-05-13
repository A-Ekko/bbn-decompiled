/*    */ package org.logicalcobwebs.cglib.proxy;
/*    */ 
/*    */ import org.logicalcobwebs.cglib.asm.Type;
/*    */ 
/*    */ class CallbackInfo
/*    */ {
/*    */   private Class cls;
/*    */   private CallbackGenerator generator;
/*    */   private Type type;
/* 52 */   private static final CallbackInfo[] CALLBACKS = { new CallbackInfo(NoOp.class, NoOpGenerator.INSTANCE), new CallbackInfo(MethodInterceptor.class, MethodInterceptorGenerator.INSTANCE), new CallbackInfo(InvocationHandler.class, InvocationHandlerGenerator.INSTANCE), new CallbackInfo(LazyLoader.class, LazyLoaderGenerator.INSTANCE), new CallbackInfo(Dispatcher.class, DispatcherGenerator.INSTANCE), new CallbackInfo(FixedValue.class, FixedValueGenerator.INSTANCE), new CallbackInfo(ProxyRefDispatcher.class, DispatcherGenerator.PROXY_REF_INSTANCE) };
/*    */ 
/*    */   public static Type[] determineTypes(Class[] callbackTypes)
/*    */   {
/* 23 */     Type[] types = new Type[callbackTypes.length];
/* 24 */     for (int i = 0; i < types.length; i++) {
/* 25 */       types[i] = determineType(callbackTypes[i]);
/*    */     }
/* 27 */     return types;
/*    */   }
/*    */ 
/*    */   public static Type[] determineTypes(Callback[] callbacks) {
/* 31 */     Type[] types = new Type[callbacks.length];
/* 32 */     for (int i = 0; i < types.length; i++) {
/* 33 */       types[i] = determineType(callbacks[i]);
/*    */     }
/* 35 */     return types;
/*    */   }
/*    */ 
/*    */   public static CallbackGenerator[] getGenerators(Type[] callbackTypes) {
/* 39 */     CallbackGenerator[] generators = new CallbackGenerator[callbackTypes.length];
/* 40 */     for (int i = 0; i < generators.length; i++) {
/* 41 */       generators[i] = getGenerator(callbackTypes[i]);
/*    */     }
/* 43 */     return generators;
/*    */   }
/*    */ 
/*    */   private CallbackInfo(Class cls, CallbackGenerator generator)
/*    */   {
/* 63 */     this.cls = cls;
/* 64 */     this.generator = generator;
/* 65 */     this.type = Type.getType(cls);
/*    */   }
/*    */ 
/*    */   private static Type determineType(Callback callback) {
/* 69 */     if (callback == null) {
/* 70 */       throw new IllegalStateException("Callback is null");
/*    */     }
/* 72 */     return determineType(callback.getClass());
/*    */   }
/*    */ 
/*    */   private static Type determineType(Class callbackType) {
/* 76 */     Class cur = null;
/* 77 */     for (int i = 0; i < CALLBACKS.length; i++) {
/* 78 */       CallbackInfo info = CALLBACKS[i];
/* 79 */       if (info.cls.isAssignableFrom(callbackType)) {
/* 80 */         if (cur != null) {
/* 81 */           throw new IllegalStateException("Callback implements both " + cur + " and " + info.cls);
/*    */         }
/* 83 */         cur = info.cls;
/*    */       }
/*    */     }
/* 86 */     if (cur == null) {
/* 87 */       throw new IllegalStateException("Unknown callback type " + callbackType);
/*    */     }
/* 89 */     return Type.getType(cur);
/*    */   }
/*    */ 
/*    */   private static CallbackGenerator getGenerator(Type callbackType) {
/* 93 */     for (int i = 0; i < CALLBACKS.length; i++) {
/* 94 */       CallbackInfo info = CALLBACKS[i];
/* 95 */       if (info.type.equals(callbackType)) {
/* 96 */         return info.generator;
/*    */       }
/*    */     }
/* 99 */     throw new IllegalStateException("Unknown callback type " + callbackType);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.proxy.CallbackInfo
 * JD-Core Version:    0.6.0
 */
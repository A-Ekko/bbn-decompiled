/*    */ package org.logicalcobwebs.cglib.proxy;
/*    */ 
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.ArrayList;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import org.logicalcobwebs.cglib.core.ReflectUtils;
/*    */ 
/*    */ public abstract class CallbackHelper
/*    */   implements CallbackFilter
/*    */ {
/* 28 */   private Map methodMap = new HashMap();
/* 29 */   private List callbacks = new ArrayList();
/*    */ 
/*    */   public CallbackHelper(Class superclass, Class[] interfaces)
/*    */   {
/* 33 */     List methods = new ArrayList();
/* 34 */     Enhancer.getMethods(superclass, interfaces, methods);
/* 35 */     Map indexes = new HashMap();
/* 36 */     int i = 0; for (int size = methods.size(); i < size; i++) {
/* 37 */       Method method = (Method)methods.get(i);
/* 38 */       Object callback = getCallback(method);
/* 39 */       if (callback == null)
/* 40 */         throw new IllegalStateException("getCallback cannot return null");
/* 41 */       boolean isCallback = callback instanceof Callback;
/* 42 */       if ((!isCallback) && (!(callback instanceof Class)))
/* 43 */         throw new IllegalStateException("getCallback must return a Callback or a Class");
/* 44 */       if ((i > 0) && ((this.callbacks.get(i - 1) instanceof Callback ^ isCallback)))
/* 45 */         throw new IllegalStateException("getCallback must return a Callback or a Class consistently for every Method");
/* 46 */       Integer index = (Integer)indexes.get(callback);
/* 47 */       if (index == null) {
/* 48 */         index = new Integer(this.callbacks.size());
/* 49 */         indexes.put(callback, index);
/*    */       }
/* 51 */       this.methodMap.put(method, index);
/* 52 */       this.callbacks.add(callback);
/*    */     }
/*    */   }
/*    */ 
/*    */   protected abstract Object getCallback(Method paramMethod);
/*    */ 
/*    */   public Callback[] getCallbacks() {
/* 60 */     if (this.callbacks.size() == 0)
/* 61 */       return new Callback[0];
/* 62 */     if ((this.callbacks.get(0) instanceof Callback)) {
/* 63 */       return (Callback[])(Callback[])this.callbacks.toArray(new Callback[this.callbacks.size()]);
/*    */     }
/* 65 */     throw new IllegalStateException("getCallback returned classes, not callbacks; call getCallbackTypes instead");
/*    */   }
/*    */ 
/*    */   public Class[] getCallbackTypes()
/*    */   {
/* 71 */     if (this.callbacks.size() == 0)
/* 72 */       return new Class[0];
/* 73 */     if ((this.callbacks.get(0) instanceof Callback)) {
/* 74 */       return ReflectUtils.getClasses(getCallbacks());
/*    */     }
/* 76 */     return (Class[])(Class[])this.callbacks.toArray(new Class[this.callbacks.size()]);
/*    */   }
/*    */ 
/*    */   public int accept(Method method)
/*    */   {
/* 82 */     return ((Integer)this.methodMap.get(method)).intValue();
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 87 */     return this.methodMap.hashCode();
/*    */   }
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 92 */     if (o == null)
/* 93 */       return false;
/* 94 */     if (!(o instanceof CallbackHelper))
/* 95 */       return false;
/* 96 */     return this.methodMap.equals(((CallbackHelper)o).methodMap);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.proxy.CallbackHelper
 * JD-Core Version:    0.6.0
 */
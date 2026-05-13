/*    */ package org.logicalcobwebs.proxool.proxy;
/*    */ 
/*    */ import java.lang.reflect.Method;
/*    */ import java.lang.reflect.Modifier;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.logicalcobwebs.proxool.ProxoolException;
/*    */ 
/*    */ public class InvokerFacade
/*    */ {
/* 24 */   private static Map methodMappers = new HashMap();
/*    */ 
/*    */   public static Method getConcreteMethod(Class concreteClass, Method injectableMethod)
/*    */     throws ProxoolException
/*    */   {
/* 36 */     if (Modifier.isPublic(concreteClass.getModifiers())) {
/* 37 */       Object key = concreteClass.getName() + ":" + injectableMethod.getName();
/* 38 */       MethodMapper methodMapper = (MethodMapper)methodMappers.get(key);
/* 39 */       if (methodMapper == null) {
/* 40 */         methodMapper = new MethodMapper(concreteClass);
/* 41 */         methodMappers.put(key, methodMapper);
/*    */       }
/* 43 */       return methodMapper.getConcreteMethod(injectableMethod);
/*    */     }
/* 45 */     return injectableMethod;
/*    */   }
/*    */ 
/*    */   public static void overrideConcreteMethod(Class concreteClass, Method injectableMethod, Method overridenMethod)
/*    */   {
/* 58 */     Object key = concreteClass.getName() + ":" + injectableMethod.getName();
/* 59 */     MethodMapper methodMapper = (MethodMapper)methodMappers.get(key);
/* 60 */     if (methodMapper == null) {
/* 61 */       methodMapper = new MethodMapper(concreteClass);
/* 62 */       methodMappers.put(key, methodMapper);
/*    */     }
/* 64 */     methodMapper.overrideConcreteMethod(injectableMethod, overridenMethod);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.proxy.InvokerFacade
 * JD-Core Version:    0.6.0
 */
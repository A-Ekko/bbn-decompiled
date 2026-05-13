/*    */ package org.logicalcobwebs.proxool;
/*    */ 
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.lang.reflect.Method;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.apache.commons.logging.LogFactory;
/*    */ 
/*    */ class ShutdownHook
/*    */   implements Runnable
/*    */ {
/* 24 */   private static final Log LOG = LogFactory.getLog(ShutdownHook.class);
/*    */   private static boolean registered;
/*    */ 
/*    */   protected static void init()
/*    */   {
/* 29 */     if (!registered) {
/* 30 */       registered = true;
/* 31 */       new ShutdownHook();
/*    */     }
/*    */   }
/*    */ 
/*    */   protected static void remove(Thread t) {
/* 36 */     Runtime runtime = Runtime.getRuntime();
/*    */     try {
/* 38 */       Method removeShutdownHookMethod = Runtime.class.getMethod("removeShutdownHook", new Class[] { Thread.class });
/* 39 */       removeShutdownHookMethod.invoke(runtime, new Object[] { t });
/* 40 */       if (LOG.isDebugEnabled())
/* 41 */         LOG.debug("Removed shutdownHook");
/*    */     }
/*    */     catch (NoSuchMethodException e) {
/* 44 */       LOG.warn("Proxool will have to be shutdown manually with ProxoolFacade.shutdown() because this version of the JDK does not support Runtime.getRuntime().addShutdownHook()");
/*    */     } catch (SecurityException e) {
/* 46 */       LOG.error("Problem removing shutdownHook", e);
/*    */     } catch (IllegalAccessException e) {
/* 48 */       LOG.error("Problem removing shutdownHook", e);
/*    */     }
/*    */     catch (InvocationTargetException e) {
/* 51 */       Throwable cause = e.getTargetException();
/* 52 */       if (!(cause instanceof IllegalStateException))
/*    */       {
/* 56 */         LOG.error("Problem removing shutdownHook", e);
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   private ShutdownHook()
/*    */   {
/* 65 */     Thread t = new Thread(this);
/* 66 */     t.setName("ShutdownHook");
/* 67 */     Runtime runtime = Runtime.getRuntime();
/*    */     try {
/* 69 */       Method addShutdownHookMethod = Runtime.class.getMethod("addShutdownHook", new Class[] { Thread.class });
/* 70 */       addShutdownHookMethod.invoke(runtime, new Object[] { t });
/* 71 */       ProxoolFacade.setShutdownHook(t);
/* 72 */       if (LOG.isDebugEnabled())
/* 73 */         LOG.debug("Registered shutdownHook");
/*    */     }
/*    */     catch (NoSuchMethodException e) {
/* 76 */       LOG.warn("Proxool will have to be shutdown manually with ProxoolFacade.shutdown() because this version of the JDK does not support Runtime.getRuntime().addShutdownHook()");
/*    */     } catch (SecurityException e) {
/* 78 */       LOG.error("Problem registering shutdownHook", e);
/*    */     } catch (IllegalAccessException e) {
/* 80 */       LOG.error("Problem registering shutdownHook", e);
/*    */     } catch (InvocationTargetException e) {
/* 82 */       LOG.error("Problem registering shutdownHook", e);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/* 92 */     if (ProxoolFacade.isShutdownHookEnabled()) {
/* 93 */       LOG.debug("Running ShutdownHook");
/* 94 */       Thread.currentThread().setName("Shutdown Hook");
/* 95 */       ProxoolFacade.shutdown(0);
/*    */     } else {
/* 97 */       LOG.debug("Skipping ShutdownHook because it's been disabled");
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.ShutdownHook
 * JD-Core Version:    0.6.0
 */
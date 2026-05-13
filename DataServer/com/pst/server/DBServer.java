/*    */ package com.pst.server;
/*    */ 
/*    */ import com.pst.config.SystemConfig;
/*    */ import com.pst.core.Console;
/*    */ import com.pst.core.InitSystem;
/*    */ import com.pst.core.shutdown.ShutdownHook;
/*    */ import java.util.Properties;
/*    */ import org.apache.log4j.PropertyConfigurator;
/*    */ 
/*    */ public class DBServer
/*    */ {
/*    */   public static void main(String[] args)
/*    */   {
/* 12 */     if ((args != null) && (args.length > 0) && ("stop".equalsIgnoreCase(args[0].trim()))) {
/* 13 */       SystemConfig.systemStop();
/* 14 */       return;
/*    */     }
/* 16 */     PropertyConfigurator.configure(System.getProperties().getProperty("user.dir") + "/resource/log4j.properties");
/*    */ 
/* 18 */     if (new InitSystem().action()) {
/* 19 */       Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHook()));
/* 20 */       new Console().console();
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.pst.server.DBServer
 * JD-Core Version:    0.6.0
 */
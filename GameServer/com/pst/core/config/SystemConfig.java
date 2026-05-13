/*    */ package com.pst.core.config;
/*    */ 
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import java.util.Properties;
/*    */ import org.apache.mina.transport.socket.SocketAcceptor;
/*    */ 
/*    */ public class SystemConfig
/*    */ {
/* 18 */   public static int port = 0;
/*    */ 
/* 23 */   public static int good = 0;
/*    */ 
/* 28 */   public static int bad = 0;
/*    */ 
/* 35 */   public static int totalRole = 0;
/*    */   public static SocketAcceptor acceptor;
/*    */ 
/*    */   public static void writePId(String PID)
/*    */   {
/* 44 */     Properties prop = new Properties();
/* 45 */     String filePath = System.getProperties().getProperty("user.dir") + "/resource/run.properties";
/*    */     try {
/* 47 */       InputStream fis = new FileInputStream(filePath);
/*    */ 
/* 49 */       prop.load(fis);
/*    */ 
/* 52 */       OutputStream fos = new FileOutputStream(filePath);
/* 53 */       prop.setProperty("PID", PID);
/*    */ 
/* 56 */       prop.store(fos, "save 'PID' value");
/*    */     } catch (IOException e) {
/* 58 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ 
/*    */   public static void systemStop() {
/* 63 */     Properties prop = new Properties();
/* 64 */     String filePath = System.getProperties().getProperty("user.dir") + "/resource/run.properties";
/*    */     try {
/* 66 */       InputStream fis = new FileInputStream(filePath);
/* 67 */       prop.load(fis);
/* 68 */       String pid = prop.getProperty("PID");
/* 69 */       if (pid != null) {
/* 70 */         String cmd = "kill -15 " + pid;
/* 71 */         Runtime.getRuntime().exec(cmd);
/*    */       }
/*    */     } catch (Exception e) {
/* 74 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.core.config.SystemConfig
 * JD-Core Version:    0.6.0
 */
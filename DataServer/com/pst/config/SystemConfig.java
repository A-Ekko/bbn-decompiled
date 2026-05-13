/*    */ package com.pst.config;
/*    */ 
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.Properties;
/*    */ import org.apache.mina.transport.socket.SocketAcceptor;
/*    */ 
/*    */ public class SystemConfig
/*    */ {
/*    */   public static int port;
/*    */   public static int account;
/*    */   public static int powertoken;
/*    */   public static int batchMax;
/* 39 */   public static long runRate = 5000L;
/*    */   public static SocketAcceptor acceptor;
/* 49 */   public static final List<String> concentipList = new ArrayList();
/*    */ 
/*    */   public static void writePId(String PID)
/*    */   {
/* 54 */     Properties prop = new Properties();
/* 55 */     String filePath = System.getProperties().getProperty("user.dir") + "/resource/run.properties";
/*    */     try {
/* 57 */       InputStream fis = new FileInputStream(filePath);
/*    */ 
/* 59 */       prop.load(fis);
/*    */ 
/* 62 */       OutputStream fos = new FileOutputStream(filePath);
/* 63 */       prop.setProperty("PID", PID);
/*    */ 
/* 66 */       prop.store(fos, "save 'PID' value");
/*    */     } catch (IOException e) {
/* 68 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ 
/*    */   public static void systemStop() {
/* 73 */     Properties prop = new Properties();
/* 74 */     String filePath = System.getProperties().getProperty("user.dir") + "/resource/run.properties";
/*    */     try {
/* 76 */       InputStream fis = new FileInputStream(filePath);
/* 77 */       prop.load(fis);
/* 78 */       String pid = prop.getProperty("PID");
/* 79 */       if (pid != null) {
/* 80 */         String cmd = "kill -15 " + pid;
/* 81 */         Runtime.getRuntime().exec(cmd);
/*    */       }
/*    */     } catch (Exception e) {
/* 84 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.pst.config.SystemConfig
 * JD-Core Version:    0.6.0
 */
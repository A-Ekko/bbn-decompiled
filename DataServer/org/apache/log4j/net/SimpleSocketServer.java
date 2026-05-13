/*    */ package org.apache.log4j.net;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.net.ServerSocket;
/*    */ import java.net.Socket;
/*    */ import org.apache.log4j.Category;
/*    */ import org.apache.log4j.LogManager;
/*    */ import org.apache.log4j.PropertyConfigurator;
/*    */ import org.apache.log4j.xml.DOMConfigurator;
/*    */ 
/*    */ public class SimpleSocketServer
/*    */ {
/* 36 */   static Category cat = Category.getInstance(SimpleSocketServer.class.getName());
/*    */   static int port;
/*    */ 
/*    */   public static void main(String[] argv)
/*    */   {
/* 43 */     if (argv.length == 2)
/* 44 */       init(argv[0], argv[1]);
/*    */     else {
/* 46 */       usage("Wrong number of arguments.");
/*    */     }
/*    */     try
/*    */     {
/* 50 */       cat.info("Listening on port " + port);
/* 51 */       ServerSocket serverSocket = new ServerSocket(port);
/*    */       while (true) {
/* 53 */         cat.info("Waiting to accept a new client.");
/* 54 */         Socket socket = serverSocket.accept();
/* 55 */         cat.info("Connected to client at " + socket.getInetAddress());
/* 56 */         cat.info("Starting new socket node.");
/* 57 */         new Thread(new SocketNode(socket, LogManager.getLoggerRepository())).start();
/*    */       }
/*    */     }
/*    */     catch (Exception e) {
/* 61 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ 
/*    */   static void usage(String msg)
/*    */   {
/* 67 */     System.err.println(msg);
/* 68 */     System.err.println("Usage: java " + SimpleSocketServer.class.getName() + " port configFile");
/*    */ 
/* 70 */     System.exit(1);
/*    */   }
/*    */ 
/*    */   static void init(String portStr, String configFile) {
/*    */     try {
/* 75 */       port = Integer.parseInt(portStr);
/*    */     } catch (NumberFormatException e) {
/* 77 */       e.printStackTrace();
/* 78 */       usage("Could not interpret port number [" + portStr + "].");
/*    */     }
/*    */ 
/* 81 */     if (configFile.endsWith(".xml")) {
/* 82 */       new DOMConfigurator(); DOMConfigurator.configure(configFile);
/*    */     } else {
/* 84 */       new PropertyConfigurator(); PropertyConfigurator.configure(configFile);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.net.SimpleSocketServer
 * JD-Core Version:    0.6.0
 */
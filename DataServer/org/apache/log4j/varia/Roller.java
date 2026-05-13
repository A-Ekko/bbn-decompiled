/*     */ package org.apache.log4j.varia;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.net.Socket;
/*     */ import org.apache.log4j.BasicConfigurator;
/*     */ import org.apache.log4j.Category;
/*     */ 
/*     */ public class Roller
/*     */ {
/*  32 */   static Category cat = Category.getInstance(Roller.class.getName());
/*     */   static String host;
/*     */   static int port;
/*     */ 
/*     */   public static void main(String[] argv)
/*     */   {
/*  52 */     BasicConfigurator.configure();
/*     */ 
/*  54 */     if (argv.length == 2)
/*  55 */       init(argv[0], argv[1]);
/*     */     else {
/*  57 */       usage("Wrong number of arguments.");
/*     */     }
/*  59 */     roll();
/*     */   }
/*     */ 
/*     */   static void usage(String msg)
/*     */   {
/*  64 */     System.err.println(msg);
/*  65 */     System.err.println("Usage: java " + Roller.class.getName() + "host_name port_number");
/*     */ 
/*  67 */     System.exit(1);
/*     */   }
/*     */ 
/*     */   static void init(String hostArg, String portArg)
/*     */   {
/*  72 */     host = hostArg;
/*     */     try {
/*  74 */       port = Integer.parseInt(portArg);
/*     */     }
/*     */     catch (NumberFormatException e) {
/*  77 */       usage("Second argument " + portArg + " is not a valid integer.");
/*     */     }
/*     */   }
/*     */ 
/*     */   static void roll()
/*     */   {
/*     */     try {
/*  84 */       Socket socket = new Socket(host, port);
/*  85 */       DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
/*  86 */       DataInputStream dis = new DataInputStream(socket.getInputStream());
/*  87 */       dos.writeUTF("RollOver");
/*  88 */       String rc = dis.readUTF();
/*  89 */       if ("OK".equals(rc)) {
/*  90 */         cat.info("Roll over signal acknowledged by remote appender.");
/*     */       } else {
/*  92 */         cat.warn("Unexpected return code " + rc + " from remote entity.");
/*  93 */         System.exit(2);
/*     */       }
/*     */     } catch (IOException e) {
/*  96 */       cat.error("Could not send roll signal on host " + host + " port " + port + " .", e);
/*     */ 
/*  98 */       System.exit(2);
/*     */     }
/* 100 */     System.exit(0);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.varia.Roller
 * JD-Core Version:    0.6.0
 */
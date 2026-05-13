/*    */ package org.apache.log4j.helpers;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.Writer;
/*    */ import java.net.DatagramPacket;
/*    */ import java.net.DatagramSocket;
/*    */ import java.net.InetAddress;
/*    */ import java.net.SocketException;
/*    */ import java.net.UnknownHostException;
/*    */ 
/*    */ public class SyslogWriter extends Writer
/*    */ {
/* 28 */   final int SYSLOG_PORT = 514;
/*    */   static String syslogHost;
/*    */   private InetAddress address;
/*    */   private DatagramSocket ds;
/*    */ 
/*    */   public SyslogWriter(String syslogHost)
/*    */   {
/* 36 */     syslogHost = syslogHost;
/*    */     try
/*    */     {
/* 39 */       this.address = InetAddress.getByName(syslogHost);
/*    */     }
/*    */     catch (UnknownHostException e) {
/* 42 */       LogLog.error("Could not find " + syslogHost + ". All logging will FAIL.", e);
/*    */     }
/*    */ 
/*    */     try
/*    */     {
/* 47 */       this.ds = new DatagramSocket();
/*    */     }
/*    */     catch (SocketException e) {
/* 50 */       e.printStackTrace();
/* 51 */       LogLog.error("Could not instantiate DatagramSocket to " + syslogHost + ". All logging will FAIL.", e);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void write(char[] buf, int off, int len)
/*    */     throws IOException
/*    */   {
/* 59 */     write(new String(buf, off, len));
/*    */   }
/*    */ 
/*    */   public void write(String string) throws IOException
/*    */   {
/* 64 */     byte[] bytes = string.getBytes();
/* 65 */     DatagramPacket packet = new DatagramPacket(bytes, bytes.length, this.address, 514);
/*    */ 
/* 68 */     if (this.ds != null)
/* 69 */       this.ds.send(packet);
/*    */   }
/*    */ 
/*    */   public void flush()
/*    */   {
/*    */   }
/*    */ 
/*    */   public void close()
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.helpers.SyslogWriter
 * JD-Core Version:    0.6.0
 */
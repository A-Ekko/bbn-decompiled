/*     */ package org.apache.log4j.net;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Vector;
/*     */ import org.apache.log4j.AppenderSkeleton;
/*     */ import org.apache.log4j.Layout;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class TelnetAppender extends AppenderSkeleton
/*     */ {
/*     */   private SocketHandler sh;
/*  49 */   private int port = 23;
/*     */ 
/*     */   public boolean requiresLayout()
/*     */   {
/*  55 */     return true;
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/*     */     try
/*     */     {
/*  62 */       this.sh = new SocketHandler(this.port);
/*  63 */       this.sh.start();
/*     */     }
/*     */     catch (Exception e) {
/*  66 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getPort()
/*     */   {
/*  72 */     return this.port;
/*     */   }
/*     */ 
/*     */   public void setPort(int port)
/*     */   {
/*  77 */     this.port = port;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/*  83 */     this.sh.finalize();
/*     */   }
/*     */ 
/*     */   protected void append(LoggingEvent event)
/*     */   {
/*  89 */     this.sh.send(this.layout.format(event));
/*  90 */     if (this.layout.ignoresThrowable()) {
/*  91 */       String[] s = event.getThrowableStrRep();
/*  92 */       if (s != null) {
/*  93 */         int len = s.length;
/*  94 */         for (int i = 0; i < len; i++) {
/*  95 */           this.sh.send(s[i]);
/*  96 */           this.sh.send(Layout.LINE_SEP);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected class SocketHandler extends Thread
/*     */   {
/* 109 */     private boolean done = false;
/* 110 */     private Vector writers = new Vector();
/* 111 */     private Vector connections = new Vector();
/*     */     private ServerSocket serverSocket;
/* 113 */     private int MAX_CONNECTIONS = 20;
/*     */ 
/*     */     public void finalize()
/*     */     {
/* 117 */       for (Enumeration e = this.connections.elements(); e.hasMoreElements(); )
/*     */         try {
/* 119 */           ((Socket)e.nextElement()).close();
/*     */         }
/*     */         catch (Exception ex) {
/*     */         }
/*     */       try {
/* 124 */         this.serverSocket.close();
/*     */       } catch (Exception ex) {
/*     */       }
/* 127 */       this.done = true;
/*     */     }
/*     */ 
/*     */     public void send(String message)
/*     */     {
/* 132 */       Enumeration ce = this.connections.elements();
/* 133 */       for (Enumeration e = this.writers.elements(); e.hasMoreElements(); ) {
/* 134 */         Socket sock = (Socket)ce.nextElement();
/* 135 */         PrintWriter writer = (PrintWriter)e.nextElement();
/* 136 */         writer.print(message);
/* 137 */         if (!writer.checkError())
/*     */           continue;
/* 139 */         this.connections.remove(sock);
/* 140 */         this.writers.remove(writer);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/* 150 */       while (!this.done)
/*     */         try {
/* 152 */           Socket newClient = this.serverSocket.accept();
/* 153 */           PrintWriter pw = new PrintWriter(newClient.getOutputStream());
/* 154 */           if (this.connections.size() < this.MAX_CONNECTIONS) {
/* 155 */             this.connections.addElement(newClient);
/* 156 */             this.writers.addElement(pw);
/* 157 */             pw.print("TelnetAppender v1.0 (" + this.connections.size() + " active connections)\r\n\r\n");
/*     */ 
/* 159 */             pw.flush();
/*     */           } else {
/* 161 */             pw.print("Too many connections.\r\n");
/* 162 */             pw.flush();
/* 163 */             newClient.close();
/*     */           }
/*     */         } catch (Exception e) {
/* 166 */           LogLog.error("Encountered error while in SocketHandler loop.", e);
/*     */         }
/*     */     }
/*     */ 
/*     */     public SocketHandler(int port) throws IOException
/*     */     {
/* 172 */       this.serverSocket = new ServerSocket(port);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.net.TelnetAppender
 * JD-Core Version:    0.6.0
 */
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
/*     */   private int port;
/*     */ 
/*     */   public TelnetAppender()
/*     */   {
/*  59 */     this.port = 23;
/*     */   }
/*     */ 
/*     */   public boolean requiresLayout()
/*     */   {
/*  65 */     return true;
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/*     */     try
/*     */     {
/*  72 */       this.sh = new SocketHandler(this.port);
/*  73 */       this.sh.start();
/*     */     }
/*     */     catch (Exception e) {
/*  76 */       e.printStackTrace();
/*     */     }
/*  78 */     super.activateOptions();
/*     */   }
/*     */ 
/*     */   public int getPort()
/*     */   {
/*  83 */     return this.port;
/*     */   }
/*     */ 
/*     */   public void setPort(int port)
/*     */   {
/*  88 */     this.port = port;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/*  94 */     if (this.sh != null) {
/*  95 */       this.sh.close();
/*     */       try {
/*  97 */         this.sh.join();
/*     */       }
/*     */       catch (InterruptedException ex)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void append(LoggingEvent event) {
/* 106 */     this.sh.send(this.layout.format(event));
/* 107 */     if (this.layout.ignoresThrowable()) {
/* 108 */       String[] s = event.getThrowableStrRep();
/* 109 */       if (s != null) {
/* 110 */         int len = s.length;
/* 111 */         for (int i = 0; i < len; i++) {
/* 112 */           this.sh.send(s[i]);
/* 113 */           this.sh.send(Layout.LINE_SEP);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected class SocketHandler extends Thread
/*     */   {
/* 126 */     private Vector writers = new Vector();
/* 127 */     private Vector connections = new Vector();
/*     */     private ServerSocket serverSocket;
/* 129 */     private int MAX_CONNECTIONS = 20;
/*     */ 
/*     */     public void finalize() {
/* 132 */       close();
/*     */     }
/*     */ 
/*     */     public void close()
/*     */     {
/* 137 */       for (Enumeration e = this.connections.elements(); e.hasMoreElements(); )
/*     */         try {
/* 139 */           ((Socket)e.nextElement()).close();
/*     */         }
/*     */         catch (Exception ex)
/*     */         {
/*     */         }
/*     */       try {
/* 145 */         this.serverSocket.close();
/*     */       }
/*     */       catch (Exception ex) {
/*     */       }
/*     */     }
/*     */ 
/*     */     public void send(String message) {
/* 152 */       Enumeration ce = this.connections.elements();
/* 153 */       for (Enumeration e = this.writers.elements(); e.hasMoreElements(); ) {
/* 154 */         Socket sock = (Socket)ce.nextElement();
/* 155 */         PrintWriter writer = (PrintWriter)e.nextElement();
/* 156 */         writer.print(message);
/* 157 */         if (writer.checkError())
/*     */         {
/* 159 */           this.connections.remove(sock);
/* 160 */           this.writers.remove(writer);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/*     */       while (true) {
/* 170 */         if (!this.serverSocket.isClosed()) {
/*     */           try {
/* 172 */             Socket newClient = this.serverSocket.accept();
/* 173 */             PrintWriter pw = new PrintWriter(newClient.getOutputStream());
/* 174 */             if (this.connections.size() < this.MAX_CONNECTIONS) {
/* 175 */               this.connections.addElement(newClient);
/* 176 */               this.writers.addElement(pw);
/* 177 */               pw.print("TelnetAppender v1.0 (" + this.connections.size() + " active connections)\r\n\r\n");
/*     */ 
/* 179 */               pw.flush();
/*     */             } else {
/* 181 */               pw.print("Too many connections.\r\n");
/* 182 */               pw.flush();
/* 183 */               newClient.close();
/*     */             }
/*     */           } catch (Exception e) {
/* 186 */             if (!this.serverSocket.isClosed()) {
/* 187 */               LogLog.error("Encountered error while in SocketHandler loop.", e);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       try
/*     */       {
/* 194 */         this.serverSocket.close();
/*     */       } catch (IOException ex) {
/*     */       }
/*     */     }
/*     */ 
/*     */     public SocketHandler(int port) throws IOException {
/* 200 */       this.serverSocket = new ServerSocket(port);
/* 201 */       setName("TelnetAppender-" + getName() + "-" + port);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.net.TelnetAppender
 * JD-Core Version:    0.6.0
 */
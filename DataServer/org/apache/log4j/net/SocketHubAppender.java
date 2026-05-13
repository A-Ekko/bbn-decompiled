/*     */ package org.apache.log4j.net;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketException;
/*     */ import java.util.Vector;
/*     */ import org.apache.log4j.AppenderSkeleton;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class SocketHubAppender extends AppenderSkeleton
/*     */ {
/*     */   static final int DEFAULT_PORT = 4560;
/* 103 */   private int port = 4560;
/* 104 */   private Vector oosList = new Vector();
/* 105 */   private ServerMonitor serverMonitor = null;
/* 106 */   private boolean locationInfo = false;
/*     */ 
/*     */   public SocketHubAppender()
/*     */   {
/*     */   }
/*     */ 
/*     */   public SocketHubAppender(int _port)
/*     */   {
/* 114 */     this.port = _port;
/* 115 */     startServer();
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/* 122 */     startServer();
/*     */   }
/*     */ 
/*     */   public synchronized void close()
/*     */   {
/* 132 */     if (this.closed) {
/* 133 */       return;
/*     */     }
/* 135 */     LogLog.debug("closing SocketHubAppender " + getName());
/* 136 */     this.closed = true;
/* 137 */     cleanUp();
/* 138 */     LogLog.debug("SocketHubAppender " + getName() + " closed");
/*     */   }
/*     */ 
/*     */   public void cleanUp()
/*     */   {
/* 147 */     LogLog.debug("stopping ServerSocket");
/* 148 */     this.serverMonitor.stopMonitor();
/* 149 */     this.serverMonitor = null;
/*     */ 
/* 152 */     LogLog.debug("closing client connections");
/* 153 */     while (this.oosList.size() != 0) {
/* 154 */       ObjectOutputStream oos = (ObjectOutputStream)this.oosList.elementAt(0);
/* 155 */       if (oos == null) continue;
/*     */       try {
/* 157 */         oos.close();
/*     */       }
/*     */       catch (IOException e) {
/* 160 */         LogLog.error("could not close oos.", e);
/*     */       }
/*     */ 
/* 163 */       this.oosList.removeElementAt(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void append(LoggingEvent event)
/*     */   {
/* 173 */     if ((event == null) || (this.oosList.size() == 0)) {
/* 174 */       return;
/*     */     }
/*     */ 
/* 177 */     if (this.locationInfo) {
/* 178 */       event.getLocationInformation();
/*     */     }
/*     */ 
/* 182 */     for (int streamCount = 0; streamCount < this.oosList.size(); streamCount++)
/*     */     {
/* 184 */       ObjectOutputStream oos = null;
/*     */       try {
/* 186 */         oos = (ObjectOutputStream)this.oosList.elementAt(streamCount);
/*     */       }
/*     */       catch (ArrayIndexOutOfBoundsException e)
/*     */       {
/*     */       }
/*     */ 
/* 195 */       if (oos == null)
/*     */         break;
/*     */       try
/*     */       {
/* 199 */         oos.writeObject(event);
/* 200 */         oos.flush();
/*     */ 
/* 204 */         oos.reset();
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/* 208 */         this.oosList.removeElementAt(streamCount);
/* 209 */         LogLog.debug("dropped connection");
/*     */ 
/* 212 */         streamCount--;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean requiresLayout()
/*     */   {
/* 222 */     return false;
/*     */   }
/*     */ 
/*     */   public void setPort(int _port)
/*     */   {
/* 230 */     this.port = _port;
/*     */   }
/*     */ 
/*     */   public int getPort()
/*     */   {
/* 237 */     return this.port;
/*     */   }
/*     */ 
/*     */   public void setLocationInfo(boolean _locationInfo)
/*     */   {
/* 246 */     this.locationInfo = _locationInfo;
/*     */   }
/*     */ 
/*     */   public boolean getLocationInfo()
/*     */   {
/* 253 */     return this.locationInfo;
/*     */   }
/*     */ 
/*     */   private void startServer()
/*     */   {
/* 260 */     this.serverMonitor = new ServerMonitor(this.port, this.oosList);
/*     */   }
/*     */ 
/*     */   private class ServerMonitor
/*     */     implements Runnable
/*     */   {
/*     */     private int port;
/*     */     private Vector oosList;
/*     */     private boolean keepRunning;
/*     */     private Thread monitorThread;
/*     */ 
/*     */     public ServerMonitor(int _port, Vector _oosList)
/*     */     {
/* 278 */       this.port = _port;
/* 279 */       this.oosList = _oosList;
/* 280 */       this.keepRunning = true;
/* 281 */       this.monitorThread = new Thread(this);
/* 282 */       this.monitorThread.setDaemon(true);
/* 283 */       this.monitorThread.start();
/*     */     }
/*     */ 
/*     */     public synchronized void stopMonitor()
/*     */     {
/* 292 */       if (this.keepRunning) {
/* 293 */         LogLog.debug("server monitor thread shutting down");
/* 294 */         this.keepRunning = false;
/*     */         try {
/* 296 */           this.monitorThread.join();
/*     */         }
/*     */         catch (InterruptedException e)
/*     */         {
/*     */         }
/*     */ 
/* 303 */         this.monitorThread = null;
/* 304 */         LogLog.debug("server monitor thread shut down");
/*     */       }
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/* 313 */       ServerSocket serverSocket = null;
/*     */       try {
/* 315 */         serverSocket = new ServerSocket(this.port);
/* 316 */         serverSocket.setSoTimeout(1000);
/*     */       }
/*     */       catch (Exception e) {
/* 319 */         LogLog.error("exception setting timeout, shutting down server socket.", e);
/* 320 */         this.keepRunning = false;
/* 321 */         return;
/*     */       }
/*     */       try
/*     */       {
/*     */         try {
/* 326 */           serverSocket.setSoTimeout(1000);
/*     */         }
/*     */         catch (SocketException e) {
/* 329 */           LogLog.error("exception setting timeout, shutting down server socket.", e);
/* 330 */           jsr 151;
/*     */         }
/*     */ 
/* 333 */         while (this.keepRunning) {
/* 334 */           Socket socket = null;
/*     */           try {
/* 336 */             socket = serverSocket.accept();
/*     */           }
/*     */           catch (InterruptedIOException e)
/*     */           {
/*     */           }
/*     */           catch (SocketException e) {
/* 342 */             LogLog.error("exception accepting socket, shutting down server socket.", e);
/* 343 */             this.keepRunning = false;
/*     */           }
/*     */           catch (IOException e) {
/* 346 */             LogLog.error("exception accepting socket.", e);
/*     */           }
/*     */ 
/* 350 */           if (socket == null) continue;
/*     */           try {
/* 352 */             InetAddress remoteAddress = socket.getInetAddress();
/* 353 */             LogLog.debug("accepting connection from " + remoteAddress.getHostName() + " (" + remoteAddress.getHostAddress() + ")");
/*     */ 
/* 357 */             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
/*     */ 
/* 360 */             this.oosList.addElement(oos);
/*     */           }
/*     */           catch (IOException e) {
/* 363 */             LogLog.error("exception creating output stream on socket.", e);
/*     */           }
/*     */         }
/*     */       }
/*     */       finally
/*     */       {
/*     */         try
/*     */         {
/* 371 */           serverSocket.close();
/*     */         }
/*     */         catch (IOException e)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.net.SocketHubAppender
 * JD-Core Version:    0.6.0
 */
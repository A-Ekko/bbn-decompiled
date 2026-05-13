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
/* 113 */   private int port = 4560;
/* 114 */   private Vector oosList = new Vector();
/* 115 */   private ServerMonitor serverMonitor = null;
/* 116 */   private boolean locationInfo = false;
/*     */ 
/*     */   public SocketHubAppender()
/*     */   {
/*     */   }
/*     */ 
/*     */   public SocketHubAppender(int _port)
/*     */   {
/* 124 */     this.port = _port;
/* 125 */     startServer();
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/* 132 */     startServer();
/*     */   }
/*     */ 
/*     */   public synchronized void close()
/*     */   {
/* 142 */     if (this.closed) {
/* 143 */       return;
/*     */     }
/* 145 */     LogLog.debug("closing SocketHubAppender " + getName());
/* 146 */     this.closed = true;
/* 147 */     cleanUp();
/* 148 */     LogLog.debug("SocketHubAppender " + getName() + " closed");
/*     */   }
/*     */ 
/*     */   public void cleanUp()
/*     */   {
/* 157 */     LogLog.debug("stopping ServerSocket");
/* 158 */     this.serverMonitor.stopMonitor();
/* 159 */     this.serverMonitor = null;
/*     */ 
/* 162 */     LogLog.debug("closing client connections");
/* 163 */     while (this.oosList.size() != 0) {
/* 164 */       ObjectOutputStream oos = (ObjectOutputStream)this.oosList.elementAt(0);
/* 165 */       if (oos != null) {
/*     */         try {
/* 167 */           oos.close();
/*     */         }
/*     */         catch (IOException e) {
/* 170 */           LogLog.error("could not close oos.", e);
/*     */         }
/*     */ 
/* 173 */         this.oosList.removeElementAt(0);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void append(LoggingEvent event)
/*     */   {
/* 183 */     if ((event == null) || (this.oosList.size() == 0)) {
/* 184 */       return;
/*     */     }
/*     */ 
/* 187 */     if (this.locationInfo) {
/* 188 */       event.getLocationInformation();
/*     */     }
/*     */ 
/* 192 */     for (int streamCount = 0; streamCount < this.oosList.size(); streamCount++)
/*     */     {
/* 194 */       ObjectOutputStream oos = null;
/*     */       try {
/* 196 */         oos = (ObjectOutputStream)this.oosList.elementAt(streamCount);
/*     */       }
/*     */       catch (ArrayIndexOutOfBoundsException e)
/*     */       {
/*     */       }
/*     */ 
/* 205 */       if (oos == null)
/*     */         break;
/*     */       try
/*     */       {
/* 209 */         oos.writeObject(event);
/* 210 */         oos.flush();
/*     */ 
/* 214 */         oos.reset();
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/* 218 */         this.oosList.removeElementAt(streamCount);
/* 219 */         LogLog.debug("dropped connection");
/*     */ 
/* 222 */         streamCount--;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean requiresLayout()
/*     */   {
/* 232 */     return false;
/*     */   }
/*     */ 
/*     */   public void setPort(int _port)
/*     */   {
/* 240 */     this.port = _port;
/*     */   }
/*     */ 
/*     */   public int getPort()
/*     */   {
/* 247 */     return this.port;
/*     */   }
/*     */ 
/*     */   public void setLocationInfo(boolean _locationInfo)
/*     */   {
/* 256 */     this.locationInfo = _locationInfo;
/*     */   }
/*     */ 
/*     */   public boolean getLocationInfo()
/*     */   {
/* 263 */     return this.locationInfo;
/*     */   }
/*     */ 
/*     */   private void startServer()
/*     */   {
/* 270 */     this.serverMonitor = new ServerMonitor(this.port, this.oosList);
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
/* 288 */       this.port = _port;
/* 289 */       this.oosList = _oosList;
/* 290 */       this.keepRunning = true;
/* 291 */       this.monitorThread = new Thread(this);
/* 292 */       this.monitorThread.setDaemon(true);
/* 293 */       this.monitorThread.start();
/*     */     }
/*     */ 
/*     */     public synchronized void stopMonitor()
/*     */     {
/* 302 */       if (this.keepRunning) {
/* 303 */         LogLog.debug("server monitor thread shutting down");
/* 304 */         this.keepRunning = false;
/*     */         try {
/* 306 */           this.monitorThread.join();
/*     */         }
/*     */         catch (InterruptedException e)
/*     */         {
/*     */         }
/*     */ 
/* 313 */         this.monitorThread = null;
/* 314 */         LogLog.debug("server monitor thread shut down");
/*     */       }
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/* 323 */       ServerSocket serverSocket = null;
/*     */       try {
/* 325 */         serverSocket = new ServerSocket(this.port);
/* 326 */         serverSocket.setSoTimeout(1000);
/*     */       }
/*     */       catch (Exception e) {
/* 329 */         LogLog.error("exception setting timeout, shutting down server socket.", e);
/* 330 */         this.keepRunning = false;
/* 331 */         return;
/*     */       }
/*     */       try
/*     */       {
/*     */         try {
/* 336 */           serverSocket.setSoTimeout(1000);
/*     */         }
/*     */         catch (SocketException e) {
/* 339 */           LogLog.error("exception setting timeout, shutting down server socket.", e);
/*     */           try
/*     */           {
/* 381 */             serverSocket.close();
/*     */           }
/*     */           catch (IOException e) {
/*     */           }
/* 385 */           return;
/*     */         }
/* 343 */         while (this.keepRunning) {
/* 344 */           Socket socket = null;
/*     */           try {
/* 346 */             socket = serverSocket.accept();
/*     */           }
/*     */           catch (InterruptedIOException e)
/*     */           {
/*     */           }
/*     */           catch (SocketException e) {
/* 352 */             LogLog.error("exception accepting socket, shutting down server socket.", e);
/* 353 */             this.keepRunning = false;
/*     */           }
/*     */           catch (IOException e) {
/* 356 */             LogLog.error("exception accepting socket.", e);
/*     */           }
/*     */ 
/* 360 */           if (socket != null)
/*     */             try {
/* 362 */               InetAddress remoteAddress = socket.getInetAddress();
/* 363 */               LogLog.debug("accepting connection from " + remoteAddress.getHostName() + " (" + remoteAddress.getHostAddress() + ")");
/*     */ 
/* 367 */               ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
/*     */ 
/* 370 */               this.oosList.addElement(oos);
/*     */             }
/*     */             catch (IOException e) {
/* 373 */               LogLog.error("exception creating output stream on socket.", e);
/*     */             }
/*     */         }
/*     */       }
/*     */       finally
/*     */       {
/*     */         try
/*     */         {
/* 381 */           serverSocket.close();
/*     */         }
/*     */         catch (IOException e)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.net.SocketHubAppender
 * JD-Core Version:    0.6.0
 */
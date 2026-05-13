/*     */ package org.apache.log4j.net;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.net.ConnectException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.Socket;
/*     */ import org.apache.log4j.AppenderSkeleton;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.spi.ErrorHandler;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class SocketAppender extends AppenderSkeleton
/*     */ {
/*     */   public static final int DEFAULT_PORT = 4560;
/*     */   static final int DEFAULT_RECONNECTION_DELAY = 30000;
/*     */   String remoteHost;
/*     */   InetAddress address;
/* 121 */   int port = 4560;
/*     */   ObjectOutputStream oos;
/* 123 */   int reconnectionDelay = 30000;
/* 124 */   boolean locationInfo = false;
/*     */   private String application;
/*     */   private Connector connector;
/* 129 */   int counter = 0;
/*     */   private static final int RESET_FREQUENCY = 1;
/*     */ 
/*     */   public SocketAppender()
/*     */   {
/*     */   }
/*     */ 
/*     */   public SocketAppender(InetAddress address, int port)
/*     */   {
/* 142 */     this.address = address;
/* 143 */     this.remoteHost = address.getHostName();
/* 144 */     this.port = port;
/* 145 */     connect(address, port);
/*     */   }
/*     */ 
/*     */   public SocketAppender(String host, int port)
/*     */   {
/* 152 */     this.port = port;
/* 153 */     this.address = getAddressByName(host);
/* 154 */     this.remoteHost = host;
/* 155 */     connect(this.address, port);
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/* 162 */     connect(this.address, this.port);
/*     */   }
/*     */ 
/*     */   public synchronized void close()
/*     */   {
/* 172 */     if (this.closed) {
/* 173 */       return;
/*     */     }
/* 175 */     this.closed = true;
/* 176 */     cleanUp();
/*     */   }
/*     */ 
/*     */   public void cleanUp()
/*     */   {
/* 184 */     if (this.oos != null) {
/*     */       try {
/* 186 */         this.oos.close();
/*     */       } catch (IOException e) {
/* 188 */         LogLog.error("Could not close oos.", e);
/*     */       }
/* 190 */       this.oos = null;
/*     */     }
/* 192 */     if (this.connector != null)
/*     */     {
/* 194 */       this.connector.interrupted = true;
/* 195 */       this.connector = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   void connect(InetAddress address, int port) {
/* 200 */     if (this.address == null)
/* 201 */       return;
/*     */     try
/*     */     {
/* 204 */       cleanUp();
/* 205 */       this.oos = new ObjectOutputStream(new Socket(address, port).getOutputStream());
/*     */     }
/*     */     catch (IOException e) {
/* 208 */       String msg = "Could not connect to remote log4j server at [" + address.getHostName() + "].";
/*     */ 
/* 210 */       if (this.reconnectionDelay > 0) {
/* 211 */         msg = msg + " We will try again later.";
/* 212 */         fireConnector();
/*     */       } else {
/* 214 */         msg = msg + " We are not retrying.";
/* 215 */         this.errorHandler.error(msg, e, 0);
/*     */       }
/* 217 */       LogLog.error(msg);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void append(LoggingEvent event)
/*     */   {
/* 223 */     if (event == null) {
/* 224 */       return;
/*     */     }
/* 226 */     if (this.address == null) {
/* 227 */       this.errorHandler.error("No remote host is set for SocketAppender named \"" + this.name + "\".");
/*     */ 
/* 229 */       return;
/*     */     }
/*     */ 
/* 232 */     if (this.oos != null)
/*     */       try
/*     */       {
/* 235 */         if (this.locationInfo) {
/* 236 */           event.getLocationInformation();
/*     */         }
/* 238 */         if (this.application != null) {
/* 239 */           event.setProperty("application", this.application);
/*     */         }
/* 241 */         this.oos.writeObject(event);
/*     */ 
/* 243 */         this.oos.flush();
/* 244 */         if (++this.counter >= 1) {
/* 245 */           this.counter = 0;
/*     */ 
/* 249 */           this.oos.reset();
/*     */         }
/*     */       } catch (IOException e) {
/* 252 */         this.oos = null;
/* 253 */         LogLog.warn("Detected problem with connection: " + e);
/* 254 */         if (this.reconnectionDelay > 0)
/* 255 */           fireConnector();
/*     */         else
/* 257 */           this.errorHandler.error("Detected problem with connection, not reconnecting.", e, 0);
/*     */       }
/*     */   }
/*     */ 
/*     */   void fireConnector()
/*     */   {
/* 265 */     if (this.connector == null) {
/* 266 */       LogLog.debug("Starting a new connector thread.");
/* 267 */       this.connector = new Connector();
/* 268 */       this.connector.setDaemon(true);
/* 269 */       this.connector.setPriority(1);
/* 270 */       this.connector.start();
/*     */     }
/*     */   }
/*     */ 
/*     */   static InetAddress getAddressByName(String host)
/*     */   {
/*     */     try {
/* 277 */       return InetAddress.getByName(host);
/*     */     } catch (Exception e) {
/* 279 */       LogLog.error("Could not find address of [" + host + "].", e);
/* 280 */     }return null;
/*     */   }
/*     */ 
/*     */   public boolean requiresLayout()
/*     */   {
/* 289 */     return false;
/*     */   }
/*     */ 
/*     */   public void setRemoteHost(String host)
/*     */   {
/* 298 */     this.address = getAddressByName(host);
/* 299 */     this.remoteHost = host;
/*     */   }
/*     */ 
/*     */   public String getRemoteHost()
/*     */   {
/* 306 */     return this.remoteHost;
/*     */   }
/*     */ 
/*     */   public void setPort(int port)
/*     */   {
/* 314 */     this.port = port;
/*     */   }
/*     */ 
/*     */   public int getPort()
/*     */   {
/* 321 */     return this.port;
/*     */   }
/*     */ 
/*     */   public void setLocationInfo(boolean locationInfo)
/*     */   {
/* 330 */     this.locationInfo = locationInfo;
/*     */   }
/*     */ 
/*     */   public boolean getLocationInfo()
/*     */   {
/* 337 */     return this.locationInfo;
/*     */   }
/*     */ 
/*     */   public void setApplication(String lapp)
/*     */   {
/* 346 */     this.application = lapp;
/*     */   }
/*     */ 
/*     */   public String getApplication()
/*     */   {
/* 353 */     return this.application;
/*     */   }
/*     */ 
/*     */   public void setReconnectionDelay(int delay)
/*     */   {
/* 366 */     this.reconnectionDelay = delay;
/*     */   }
/*     */ 
/*     */   public int getReconnectionDelay()
/*     */   {
/* 373 */     return this.reconnectionDelay;
/*     */   }
/*     */ 
/*     */   class Connector extends Thread
/*     */   {
/* 390 */     boolean interrupted = false;
/*     */ 
/*     */     Connector() {
/*     */     }
/*     */     public void run() {
/* 395 */       while (!this.interrupted)
/*     */         try {
/* 397 */           Thread.sleep(SocketAppender.this.reconnectionDelay);
/* 398 */           LogLog.debug("Attempting connection to " + SocketAppender.this.address.getHostName());
/* 399 */           Socket socket = new Socket(SocketAppender.this.address, SocketAppender.this.port);
/* 400 */           synchronized (this) {
/* 401 */             SocketAppender.this.oos = new ObjectOutputStream(socket.getOutputStream());
/* 402 */             SocketAppender.access$002(SocketAppender.this, null);
/* 403 */             LogLog.debug("Connection established. Exiting connector thread.");
/*     */           }
/*     */         }
/*     */         catch (InterruptedException e) {
/* 407 */           LogLog.debug("Connector interrupted. Leaving loop.");
/* 408 */           return;
/*     */         } catch (ConnectException e) {
/* 410 */           LogLog.debug("Remote host " + SocketAppender.this.address.getHostName() + " refused connection.");
/*     */         }
/*     */         catch (IOException e) {
/* 413 */           LogLog.debug("Could not connect to " + SocketAppender.this.address.getHostName() + ". Exception is " + e);
/*     */         }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.net.SocketAppender
 * JD-Core Version:    0.6.0
 */
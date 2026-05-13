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
/*     */   static final int DEFAULT_PORT = 4560;
/*     */   static final int DEFAULT_RECONNECTION_DELAY = 30000;
/*     */   String remoteHost;
/*     */   InetAddress address;
/* 111 */   int port = 4560;
/*     */   ObjectOutputStream oos;
/* 113 */   int reconnectionDelay = 30000;
/* 114 */   boolean locationInfo = false;
/*     */   private Connector connector;
/* 118 */   int counter = 0;
/*     */   private static final int RESET_FREQUENCY = 1;
/*     */ 
/*     */   public SocketAppender()
/*     */   {
/*     */   }
/*     */ 
/*     */   public SocketAppender(InetAddress address, int port)
/*     */   {
/* 132 */     this.address = address;
/* 133 */     this.remoteHost = address.getHostName();
/* 134 */     this.port = port;
/* 135 */     connect(address, port);
/*     */   }
/*     */ 
/*     */   public SocketAppender(String host, int port)
/*     */   {
/* 142 */     this.port = port;
/* 143 */     this.address = getAddressByName(host);
/* 144 */     this.remoteHost = host;
/* 145 */     connect(this.address, port);
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/* 152 */     connect(this.address, this.port);
/*     */   }
/*     */ 
/*     */   public synchronized void close()
/*     */   {
/* 162 */     if (this.closed) {
/* 163 */       return;
/*     */     }
/* 165 */     this.closed = true;
/* 166 */     cleanUp();
/*     */   }
/*     */ 
/*     */   public void cleanUp()
/*     */   {
/* 174 */     if (this.oos != null) {
/*     */       try {
/* 176 */         this.oos.close();
/*     */       } catch (IOException e) {
/* 178 */         LogLog.error("Could not close oos.", e);
/*     */       }
/* 180 */       this.oos = null;
/*     */     }
/* 182 */     if (this.connector != null)
/*     */     {
/* 184 */       this.connector.interrupted = true;
/* 185 */       this.connector = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   void connect(InetAddress address, int port) {
/* 190 */     if (this.address == null)
/* 191 */       return;
/*     */     try
/*     */     {
/* 194 */       cleanUp();
/* 195 */       this.oos = new ObjectOutputStream(new Socket(address, port).getOutputStream());
/*     */     } catch (IOException e) {
/* 197 */       LogLog.error("Could not connect to remote log4j server at [" + address.getHostName() + "]. We will try again later.", e);
/*     */ 
/* 199 */       fireConnector();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void append(LoggingEvent event)
/*     */   {
/* 205 */     if (event == null) {
/* 206 */       return;
/*     */     }
/* 208 */     if (this.address == null) {
/* 209 */       this.errorHandler.error("No remote host is set for SocketAppender named \"" + this.name + "\".");
/*     */ 
/* 211 */       return;
/*     */     }
/*     */ 
/* 214 */     if (this.oos != null)
/*     */       try {
/* 216 */         if (this.locationInfo) {
/* 217 */           event.getLocationInformation();
/*     */         }
/* 219 */         this.oos.writeObject(event);
/*     */ 
/* 221 */         this.oos.flush();
/* 222 */         if (++this.counter >= 1) {
/* 223 */           this.counter = 0;
/*     */ 
/* 227 */           this.oos.reset();
/*     */         }
/*     */       } catch (IOException e) {
/* 230 */         this.oos = null;
/* 231 */         LogLog.warn("Detected problem with connection: " + e);
/* 232 */         if (this.reconnectionDelay > 0)
/* 233 */           fireConnector();
/*     */       }
/*     */   }
/*     */ 
/*     */   void fireConnector()
/*     */   {
/* 240 */     if (this.connector == null) {
/* 241 */       LogLog.debug("Starting a new connector thread.");
/* 242 */       this.connector = new Connector();
/* 243 */       this.connector.setDaemon(true);
/* 244 */       this.connector.setPriority(1);
/* 245 */       this.connector.start();
/*     */     }
/*     */   }
/*     */ 
/*     */   static InetAddress getAddressByName(String host)
/*     */   {
/*     */     try {
/* 252 */       return InetAddress.getByName(host);
/*     */     } catch (Exception e) {
/* 254 */       LogLog.error("Could not find address of [" + host + "].", e);
/* 255 */     }return null;
/*     */   }
/*     */ 
/*     */   public boolean requiresLayout()
/*     */   {
/* 264 */     return false;
/*     */   }
/*     */ 
/*     */   public void setRemoteHost(String host)
/*     */   {
/* 273 */     this.address = getAddressByName(host);
/* 274 */     this.remoteHost = host;
/*     */   }
/*     */ 
/*     */   public String getRemoteHost()
/*     */   {
/* 281 */     return this.remoteHost;
/*     */   }
/*     */ 
/*     */   public void setPort(int port)
/*     */   {
/* 289 */     this.port = port;
/*     */   }
/*     */ 
/*     */   public int getPort()
/*     */   {
/* 296 */     return this.port;
/*     */   }
/*     */ 
/*     */   public void setLocationInfo(boolean locationInfo)
/*     */   {
/* 305 */     this.locationInfo = locationInfo;
/*     */   }
/*     */ 
/*     */   public boolean getLocationInfo()
/*     */   {
/* 312 */     return this.locationInfo;
/*     */   }
/*     */ 
/*     */   public void setReconnectionDelay(int delay)
/*     */   {
/* 325 */     this.reconnectionDelay = delay;
/*     */   }
/*     */ 
/*     */   public int getReconnectionDelay()
/*     */   {
/* 332 */     return this.reconnectionDelay;
/*     */   }
/*     */ 
/*     */   class Connector extends Thread
/*     */   {
/* 349 */     boolean interrupted = false;
/*     */ 
/*     */     Connector() {
/*     */     }
/*     */     public void run() {
/* 354 */       while (!this.interrupted)
/*     */         try {
/* 356 */           Thread.sleep(SocketAppender.this.reconnectionDelay);
/* 357 */           LogLog.debug("Attempting connection to " + SocketAppender.this.address.getHostName());
/* 358 */           Socket socket = new Socket(SocketAppender.this.address, SocketAppender.this.port);
/* 359 */           synchronized (this) {
/* 360 */             SocketAppender.this.oos = new ObjectOutputStream(socket.getOutputStream());
/* 361 */             SocketAppender.access$002(SocketAppender.this, null);
/* 362 */             LogLog.debug("Connection established. Exiting connector thread.");
/*     */           }
/*     */         }
/*     */         catch (InterruptedException e) {
/* 366 */           LogLog.debug("Connector interrupted. Leaving loop.");
/* 367 */           return;
/*     */         } catch (ConnectException e) {
/* 369 */           LogLog.debug("Remote host " + SocketAppender.this.address.getHostName() + " refused connection.");
/*     */         }
/*     */         catch (IOException e) {
/* 372 */           LogLog.debug("Could not connect to " + SocketAppender.this.address.getHostName() + ". Exception is " + e);
/*     */         }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.net.SocketAppender
 * JD-Core Version:    0.6.0
 */
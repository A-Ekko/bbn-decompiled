/*     */ package org.apache.mina.filter.logging;
/*     */ 
/*     */ import org.apache.mina.core.filterchain.IoFilter.NextFilter;
/*     */ import org.apache.mina.core.filterchain.IoFilterAdapter;
/*     */ import org.apache.mina.core.session.IdleStatus;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.core.write.WriteRequest;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class LoggingFilter extends IoFilterAdapter
/*     */ {
/*     */   private final String name;
/*     */   private final Logger logger;
/*  53 */   private LogLevel exceptionCaughtLevel = LogLevel.WARN;
/*     */ 
/*  56 */   private LogLevel messageSentLevel = LogLevel.INFO;
/*     */ 
/*  59 */   private LogLevel messageReceivedLevel = LogLevel.INFO;
/*     */ 
/*  62 */   private LogLevel sessionCreatedLevel = LogLevel.INFO;
/*     */ 
/*  65 */   private LogLevel sessionOpenedLevel = LogLevel.INFO;
/*     */ 
/*  68 */   private LogLevel sessionIdleLevel = LogLevel.INFO;
/*     */ 
/*  71 */   private LogLevel sessionClosedLevel = LogLevel.INFO;
/*     */ 
/*     */   public LoggingFilter()
/*     */   {
/*  77 */     this(LoggingFilter.class.getName());
/*     */   }
/*     */ 
/*     */   public LoggingFilter(Class<?> clazz)
/*     */   {
/*  86 */     this(clazz.getName());
/*     */   }
/*     */ 
/*     */   public LoggingFilter(String name)
/*     */   {
/*  95 */     if (name == null)
/*  96 */       this.name = LoggingFilter.class.getName();
/*     */     else {
/*  98 */       this.name = name;
/*     */     }
/*     */ 
/* 101 */     this.logger = LoggerFactory.getLogger(name);
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 108 */     return this.name;
/*     */   }
/*     */ 
/*     */   private void log(LogLevel eventLevel, String message, Throwable cause)
/*     */   {
/* 120 */     switch (1.$SwitchMap$org$apache$mina$filter$logging$LogLevel[eventLevel.ordinal()]) { case 1:
/* 121 */       this.logger.trace(message, cause); return;
/*     */     case 2:
/* 122 */       this.logger.debug(message, cause); return;
/*     */     case 3:
/* 123 */       this.logger.info(message, cause); return;
/*     */     case 4:
/* 124 */       this.logger.warn(message, cause); return;
/*     */     case 5:
/* 125 */       this.logger.error(message, cause); return;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void log(LogLevel eventLevel, String message, Object param)
/*     */   {
/* 139 */     switch (1.$SwitchMap$org$apache$mina$filter$logging$LogLevel[eventLevel.ordinal()]) { case 1:
/* 140 */       this.logger.trace(message, param); return;
/*     */     case 2:
/* 141 */       this.logger.debug(message, param); return;
/*     */     case 3:
/* 142 */       this.logger.info(message, param); return;
/*     */     case 4:
/* 143 */       this.logger.warn(message, param); return;
/*     */     case 5:
/* 144 */       this.logger.error(message, param); return;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void log(LogLevel eventLevel, String message)
/*     */   {
/* 157 */     switch (1.$SwitchMap$org$apache$mina$filter$logging$LogLevel[eventLevel.ordinal()]) { case 1:
/* 158 */       this.logger.trace(message); return;
/*     */     case 2:
/* 159 */       this.logger.debug(message); return;
/*     */     case 3:
/* 160 */       this.logger.info(message); return;
/*     */     case 4:
/* 161 */       this.logger.warn(message); return;
/*     */     case 5:
/* 162 */       this.logger.error(message); return;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void exceptionCaught(IoFilter.NextFilter nextFilter, IoSession session, Throwable cause)
/*     */     throws Exception
/*     */   {
/* 170 */     log(this.exceptionCaughtLevel, "EXCEPTION :", cause);
/* 171 */     nextFilter.exceptionCaught(session, cause);
/*     */   }
/*     */ 
/*     */   public void messageReceived(IoFilter.NextFilter nextFilter, IoSession session, Object message)
/*     */     throws Exception
/*     */   {
/* 177 */     log(this.messageReceivedLevel, "RECEIVED: {}", message);
/* 178 */     nextFilter.messageReceived(session, message);
/*     */   }
/*     */ 
/*     */   public void messageSent(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest)
/*     */     throws Exception
/*     */   {
/* 184 */     log(this.messageSentLevel, "SENT: {}", writeRequest.getMessage());
/* 185 */     nextFilter.messageSent(session, writeRequest);
/*     */   }
/*     */ 
/*     */   public void sessionCreated(IoFilter.NextFilter nextFilter, IoSession session)
/*     */     throws Exception
/*     */   {
/* 191 */     log(this.sessionCreatedLevel, "CREATED");
/* 192 */     nextFilter.sessionCreated(session);
/*     */   }
/*     */ 
/*     */   public void sessionOpened(IoFilter.NextFilter nextFilter, IoSession session)
/*     */     throws Exception
/*     */   {
/* 198 */     log(this.sessionOpenedLevel, "OPENED");
/* 199 */     nextFilter.sessionOpened(session);
/*     */   }
/*     */ 
/*     */   public void sessionIdle(IoFilter.NextFilter nextFilter, IoSession session, IdleStatus status)
/*     */     throws Exception
/*     */   {
/* 205 */     log(this.sessionIdleLevel, "IDLE");
/* 206 */     nextFilter.sessionIdle(session, status);
/*     */   }
/*     */ 
/*     */   public void sessionClosed(IoFilter.NextFilter nextFilter, IoSession session) throws Exception
/*     */   {
/* 211 */     log(this.sessionClosedLevel, "CLOSED");
/* 212 */     nextFilter.sessionClosed(session);
/*     */   }
/*     */ 
/*     */   public void setExceptionCaughtLogLevel(LogLevel level)
/*     */   {
/* 221 */     this.exceptionCaughtLevel = level;
/*     */   }
/*     */ 
/*     */   public LogLevel getExceptionCaughtLoglevel()
/*     */   {
/* 230 */     return this.exceptionCaughtLevel;
/*     */   }
/*     */ 
/*     */   public void setMessageReceivedLogLevel(LogLevel level)
/*     */   {
/* 239 */     this.messageReceivedLevel = level;
/*     */   }
/*     */ 
/*     */   public LogLevel getMessageReceivedLoglevel()
/*     */   {
/* 248 */     return this.messageReceivedLevel;
/*     */   }
/*     */ 
/*     */   public void setMessageSentLoglevel(LogLevel level)
/*     */   {
/* 257 */     this.messageSentLevel = level;
/*     */   }
/*     */ 
/*     */   public LogLevel getMessageSentLoglevel()
/*     */   {
/* 266 */     return this.messageSentLevel;
/*     */   }
/*     */ 
/*     */   public void setSessionCreatedLoglevel(LogLevel level)
/*     */   {
/* 275 */     this.sessionCreatedLevel = level;
/*     */   }
/*     */ 
/*     */   public LogLevel getSessionCreatedLoglevel()
/*     */   {
/* 284 */     return this.sessionCreatedLevel;
/*     */   }
/*     */ 
/*     */   public void setSessionOpenedLoglevel(LogLevel level)
/*     */   {
/* 293 */     this.sessionOpenedLevel = level;
/*     */   }
/*     */ 
/*     */   public LogLevel getSessionOpenedLoglevel()
/*     */   {
/* 302 */     return this.sessionOpenedLevel;
/*     */   }
/*     */ 
/*     */   public void setSessionIdleLoglevel(LogLevel level)
/*     */   {
/* 311 */     this.sessionIdleLevel = level;
/*     */   }
/*     */ 
/*     */   public LogLevel getSessionIdleLoglevel()
/*     */   {
/* 320 */     return this.sessionIdleLevel;
/*     */   }
/*     */ 
/*     */   public void setSessionClosedLoglevel(LogLevel level)
/*     */   {
/* 329 */     this.sessionClosedLevel = level;
/*     */   }
/*     */ 
/*     */   public LogLevel getSessionClosedLoglevel()
/*     */   {
/* 338 */     return this.sessionClosedLevel;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.logging.LoggingFilter
 * JD-Core Version:    0.6.0
 */
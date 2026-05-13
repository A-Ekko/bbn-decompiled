/*     */ package flex.messaging.log;
/*     */ 
/*     */ public class LogEvent
/*     */ {
/*     */   public static final short NONE = 2000;
/*     */   public static final short FATAL = 1000;
/*     */   public static final short ERROR = 8;
/*     */   public static final short WARN = 6;
/*     */   public static final short INFO = 4;
/*     */   public static final short DEBUG = 2;
/*     */   public static final short ALL = 0;
/*     */   public short level;
/*     */   public String message;
/*     */   public Logger logger;
/*     */   public Throwable throwable;
/*     */ 
/*     */   public LogEvent(Logger lgr, String msg, short lvl, Throwable t)
/*     */   {
/*  74 */     this.logger = lgr;
/*  75 */     this.message = msg;
/*  76 */     this.level = lvl;
/*  77 */     this.throwable = t;
/*     */   }
/*     */ 
/*     */   public static String getLevelString(short value)
/*     */   {
/*  88 */     switch (value)
/*     */     {
/*     */     case 2000:
/*  91 */       return "NONE";
/*     */     case 1000:
/*  93 */       return "FATAL";
/*     */     case 8:
/*  95 */       return "ERROR";
/*     */     case 6:
/*  97 */       return "WARN";
/*     */     case 4:
/*  99 */       return "INFO";
/*     */     case 2:
/* 101 */       return "DEBUG";
/*     */     case 0:
/* 103 */       return "ALL";
/*     */     }
/* 105 */     return "UNKNOWN";
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.log.LogEvent
 * JD-Core Version:    0.6.0
 */
/*     */ package flex.messaging.services.messaging;
/*     */ 
/*     */ import flex.management.ManageableComponent;
/*     */ import flex.management.runtime.messaging.services.messaging.ThrottleManagerControl;
/*     */ import flex.messaging.MessageException;
/*     */ import flex.messaging.config.ThrottleSettings;
/*     */ import flex.messaging.messages.Message;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class ThrottleManager extends ManageableComponent
/*     */ {
/*     */   public static final String LOG_CATEGORY = "Service.Message";
/*     */   public static final String TYPE = "ThrottleManager";
/*  49 */   private static final Object classMutex = new Object();
/*  50 */   private static int instanceCount = 0;
/*     */   private Map inboundClientMarks;
/*     */   private Map outboundClientMarks;
/*     */   private ThrottleMark inboundDestinationMark;
/*     */   private ThrottleMark outboundDestinationMark;
/*     */   private ThrottleSettings settings;
/*     */   public static final int RESULT_OK = 0;
/*     */   public static final int RESULT_IGNORE = 1;
/*     */   public static final int RESULT_REPLACE = 2;
/*     */   public static final int RESULT_ERROR = 3;
/*     */   static final int MESSAGE_HISTORY_SIZE = 15;
/*     */ 
/*     */   public ThrottleManager()
/*     */   {
/*  68 */     this(false);
/*     */   }
/*     */ 
/*     */   public ThrottleManager(boolean enableManagement)
/*     */   {
/*  73 */     super(enableManagement);
/*  74 */     synchronized (classMutex)
/*     */     {
/*  76 */       super.setId("ThrottleManager" + ++instanceCount);
/*     */     }
/*     */ 
/*  79 */     this.settings = new ThrottleSettings();
/*     */   }
/*     */ 
/*     */   public void setId(String id)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setThrottleSettings(ThrottleSettings throttleSettings)
/*     */   {
/*  90 */     this.settings = throttleSettings;
/*  91 */     if (this.settings.isDestinationThrottleEnabled())
/*     */     {
/*  93 */       this.inboundDestinationMark = new ThrottleMark(this.settings.getDestinationName());
/*  94 */       this.outboundDestinationMark = new ThrottleMark(this.settings.getDestinationName());
/*     */     }
/*  96 */     if (this.settings.isClientThrottleEnabled())
/*     */     {
/*  98 */       this.inboundClientMarks = new HashMap();
/*  99 */       this.outboundClientMarks = new HashMap();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int throttleIncomingMessage(Message msg)
/*     */   {
/* 108 */     int n = 0;
/*     */ 
/* 110 */     if (this.settings.getInboundPolicy() != 0)
/*     */     {
/* 112 */       n = throttleDestinationLevel(msg, true);
/* 113 */       if (n == 0)
/*     */       {
/* 118 */         n = throttleClientLevel(msg, msg.getClientId(), true);
/*     */       }
/*     */     }
/* 121 */     return n;
/*     */   }
/*     */ 
/*     */   public int throttleOutgoingMessage(Message msg, Object clientId)
/*     */   {
/* 126 */     int n = 0;
/* 127 */     if (this.settings.getOutboundPolicy() != 0)
/*     */     {
/* 129 */       if (clientId == null)
/* 130 */         n = throttleDestinationLevel(msg, false);
/*     */       else
/* 132 */         n = throttleClientLevel(msg, clientId, false);
/*     */     }
/* 134 */     return n;
/*     */   }
/*     */ 
/*     */   private int throttleDestinationLevel(Message msg, boolean incoming)
/*     */   {
/* 139 */     int throttleResult = 0;
/* 140 */     if (this.settings.isDestinationThrottleEnabled())
/*     */     {
/* 142 */       if (incoming)
/*     */       {
/*     */         try
/*     */         {
/* 146 */           this.inboundDestinationMark.assertValid(msg, this.settings.getIncomingDestinationFrequency());
/*     */         }
/*     */         catch (RuntimeException e)
/*     */         {
/* 150 */           String s = "Message throttled: Too many messages sent to destination " + this.inboundDestinationMark.id + " in too small of a time interval.  " + e.getMessage();
/*     */ 
/* 152 */           MessageException me = new MessageException(s);
/*     */           try
/*     */           {
/* 155 */             throttleResult = handleError(this.settings.getInboundPolicy(), me);
/*     */           }
/*     */           catch (MessageException m)
/*     */           {
/* 159 */             throttleResult = 3;
/* 160 */             throw m;
/*     */           }
/*     */           finally
/*     */           {
/* 164 */             if ((throttleResult != 0) && (isManaged())) {
/* 165 */               ((ThrottleManagerControl)getControl()).incrementDestinationIncomingMessageThrottleCount();
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       else {
/*     */         try
/*     */         {
/* 173 */           this.outboundDestinationMark.assertValid(msg, this.settings.getOutgoingDestinationFrequency());
/*     */         }
/*     */         catch (RuntimeException e)
/*     */         {
/* 177 */           String s = "Message throttled: Too many messages routed by destination " + this.outboundDestinationMark.id + " in too small of a time interval";
/*     */ 
/* 179 */           MessageException me = new MessageException(s);
/*     */           try
/*     */           {
/* 182 */             throttleResult = handleError(this.settings.getOutboundPolicy(), me);
/*     */           }
/*     */           catch (MessageException m)
/*     */           {
/* 186 */             throttleResult = 3;
/* 187 */             throw m;
/*     */           }
/*     */           finally
/*     */           {
/* 191 */             if ((throttleResult != 0) && (isManaged()))
/* 192 */               ((ThrottleManagerControl)getControl()).incrementDestinationOutgoingMessageThrottleCount();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 197 */     return throttleResult;
/*     */   }
/*     */ 
/*     */   private int throttleClientLevel(Message msg, Object clientId, boolean incoming)
/*     */   {
/* 202 */     int throttleResult = 0;
/* 203 */     if (this.settings.isClientThrottleEnabled())
/*     */     {
/* 205 */       ThrottleMark clientLevelMark = null;
/* 206 */       if (incoming)
/*     */       {
/* 208 */         if (this.inboundClientMarks.get(clientId) != null)
/* 209 */           clientLevelMark = (ThrottleMark)this.inboundClientMarks.get(clientId);
/*     */         else
/* 211 */           clientLevelMark = new ThrottleMark(clientId);
/*     */         try
/*     */         {
/* 214 */           clientLevelMark.assertValid(msg, this.settings.getIncomingClientFrequency());
/*     */         }
/*     */         catch (RuntimeException e)
/*     */         {
/* 218 */           String s = "Message throttled: Too many messages sent by client " + clientId + " in too small of a time interval";
/*     */ 
/* 220 */           MessageException me = new MessageException(s);
/*     */           try
/*     */           {
/* 223 */             throttleResult = handleError(this.settings.getInboundPolicy(), me);
/*     */           }
/*     */           catch (MessageException m)
/*     */           {
/* 227 */             throttleResult = 3;
/* 228 */             throw m;
/*     */           }
/*     */           finally
/*     */           {
/* 232 */             if ((throttleResult != 0) && (isManaged()))
/* 233 */               ((ThrottleManagerControl)getControl()).incrementClientIncomingMessageThrottleCount();
/*     */           }
/*     */         }
/*     */         finally
/*     */         {
/* 238 */           this.inboundClientMarks.put(clientId, clientLevelMark);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 243 */         if (this.outboundClientMarks.get(clientId) != null)
/* 244 */           clientLevelMark = (ThrottleMark)this.outboundClientMarks.get(clientId);
/*     */         else
/* 246 */           clientLevelMark = new ThrottleMark(clientId);
/*     */         try
/*     */         {
/* 249 */           clientLevelMark.assertValid(msg, this.settings.getOutgoingClientFrequency());
/*     */         }
/*     */         catch (RuntimeException e)
/*     */         {
/* 253 */           String s = "Message throttled: Too many messages sent to client " + clientId + " in too small of a time interval";
/*     */ 
/* 255 */           MessageException me = new MessageException(s);
/*     */           try
/*     */           {
/* 258 */             throttleResult = handleError(this.settings.getOutboundPolicy(), me);
/*     */           }
/*     */           catch (MessageException m)
/*     */           {
/* 262 */             throttleResult = 3;
/* 263 */             throw m;
/*     */           }
/*     */           finally
/*     */           {
/* 267 */             if ((throttleResult != 0) && (isManaged()))
/* 268 */               ((ThrottleManagerControl)getControl()).incrementClientOutgoingMessageThrottleCount();
/*     */           }
/*     */         }
/*     */         finally
/*     */         {
/* 273 */           this.outboundClientMarks.put(clientId, clientLevelMark);
/*     */         }
/*     */       }
/*     */     }
/* 277 */     return throttleResult;
/*     */   }
/*     */ 
/*     */   private int handleError(int policy, MessageException e)
/*     */   {
/* 282 */     int n = 0;
/* 283 */     switch (policy)
/*     */     {
/*     */     case 2:
/* 286 */       n = 1;
/* 287 */       break;
/*     */     case 3:
/* 291 */       n = 2;
/* 292 */       break;
/*     */     case 1:
/* 294 */       throw e;
/*     */     }
/*     */ 
/* 298 */     return n;
/*     */   }
/*     */ 
/*     */   public void removeClientThrottleMark(Object clientId)
/*     */   {
/* 303 */     if (this.inboundClientMarks != null)
/*     */     {
/* 305 */       this.inboundClientMarks.remove(clientId);
/*     */     }
/* 307 */     if (this.outboundClientMarks != null)
/*     */     {
/* 309 */       this.outboundClientMarks.remove(clientId);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected String getLogCategory()
/*     */   {
/* 315 */     return "Service.Message";
/*     */   }
/*     */   class ThrottleMark {
/*     */     Object id;
/*     */     int messageCount;
/*     */     String lastMessageId;
/*     */     long[] lastMessageTimes;
/*     */ 
/* 327 */     ThrottleMark(Object identifier) { this.id = identifier;
/* 328 */       this.messageCount = 0;
/* 329 */       this.lastMessageId = "-1";
/* 330 */       this.lastMessageTimes = new long[15];
/*     */     }
/*     */ 
/*     */     void assertValid(Message msg, int frequency)
/*     */     {
/* 335 */       if (frequency > 0)
/*     */       {
/* 337 */         int len = this.lastMessageTimes.length;
/*     */ 
/* 339 */         if (this.messageCount >= len)
/*     */         {
/* 342 */           long interval = msg.getTimestamp() - this.lastMessageTimes[((this.messageCount - len) % len)];
/*     */ 
/* 344 */           long rate = 1000 * len / interval;
/*     */ 
/* 348 */           if (rate > frequency)
/*     */           {
/* 350 */             throw new RuntimeException("actual frequency=" + rate + " max frequency=" + frequency);
/*     */           }
/*     */         }
/* 353 */         this.lastMessageId = msg.getMessageId();
/* 354 */         this.lastMessageTimes[(this.messageCount++ % len)] = msg.getTimestamp();
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.messaging.ThrottleManager
 * JD-Core Version:    0.6.0
 */
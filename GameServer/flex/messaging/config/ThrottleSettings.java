/*     */ package flex.messaging.config;
/*     */ 
/*     */ public class ThrottleSettings
/*     */ {
/*     */   public static final int POLICY_NONE = 0;
/*     */   public static final int POLICY_ERROR = 1;
/*     */   public static final int POLICY_IGNORE = 2;
/*     */   public static final int POLICY_REPLACE = 3;
/*     */   public static final String POLICY_NONE_STRING = "NONE";
/*     */   public static final String POLICY_ERROR_STRING = "ERROR";
/*     */   public static final String POLICY_IGNORE_STRING = "IGNORE";
/*     */   public static final String POLICY_REPLACE_STRING = "REPLACE";
/*     */   public static final String ELEMENT_INBOUND = "throttle-inbound";
/*     */   public static final String ELEMENT_OUTBOUND = "throttle-outbound";
/*     */   public static final String ELEMENT_POLICY = "policy";
/*     */   public static final String ELEMENT_DEST_FREQ = "max-frequency";
/*     */   public static final String ELEMENT_CLIENT_FREQ = "max-client-frequency";
/*     */   private String destinationName;
/*     */   private int inClientMessagesPerSec;
/*     */   private int inDestinationMessagesPerSec;
/*     */   private int outClientMessagesPerSec;
/*     */   private int outDestinationMessagesPerSec;
/*     */   private int inPolicy;
/*     */   private int outPolicy;
/*     */ 
/*     */   public ThrottleSettings()
/*     */   {
/*  73 */     this.inPolicy = 0;
/*  74 */     this.outPolicy = 0;
/*  75 */     this.inClientMessagesPerSec = 0;
/*  76 */     this.inDestinationMessagesPerSec = 0;
/*  77 */     this.outDestinationMessagesPerSec = 0;
/*     */   }
/*     */ 
/*     */   public int parsePolicy(String s)
/*     */   {
/*     */     int n;
/*  84 */     if (s.equalsIgnoreCase("IGNORE"))
/*     */     {
/*  86 */       n = 2;
/*     */     }
/*     */     else
/*     */     {
/*     */       int n;
/*  88 */       if (s.equalsIgnoreCase("ERROR"))
/*     */       {
/*  90 */         n = 1;
/*     */       }
/*     */       else
/*     */       {
/*     */         int n;
/*  92 */         if (s.equalsIgnoreCase("REPLACE"))
/*     */         {
/*  94 */           n = 3;
/*     */         }
/*     */         else
/*     */         {
/*  98 */           ConfigurationException ex = new ConfigurationException();
/*  99 */           ex.setMessage("Unsupported throttle policy '" + s + "'");
/* 100 */           throw ex;
/*     */         }
/*     */       }
/*     */     }
/*     */     int n;
/* 102 */     return n;
/*     */   }
/*     */ 
/*     */   public boolean isClientThrottleEnabled()
/*     */   {
/* 115 */     return (getIncomingClientFrequency() > 0) || (getOutgoingClientFrequency() > 0);
/*     */   }
/*     */ 
/*     */   public boolean isDestinationThrottleEnabled()
/*     */   {
/* 130 */     return (getIncomingDestinationFrequency() > 0) || (getOutgoingDestinationFrequency() > 0);
/*     */   }
/*     */ 
/*     */   public int getInboundPolicy()
/*     */   {
/* 142 */     return this.inPolicy;
/*     */   }
/*     */ 
/*     */   public void setInboundPolicy(int inPolicy)
/*     */   {
/* 152 */     if (inPolicy == 3)
/*     */     {
/* 154 */       ConfigurationException ex = new ConfigurationException();
/* 155 */       ex.setMessage("The REPLACE throttle policy applies to outbound throttling only");
/* 156 */       throw ex;
/*     */     }
/* 158 */     this.inPolicy = inPolicy;
/*     */   }
/*     */ 
/*     */   public int getOutboundPolicy()
/*     */   {
/* 168 */     return this.outPolicy;
/*     */   }
/*     */ 
/*     */   public void setOutboundPolicy(int outPolicy)
/*     */   {
/* 179 */     this.outPolicy = outPolicy;
/*     */   }
/*     */ 
/*     */   public String getDestinationName()
/*     */   {
/* 189 */     return this.destinationName;
/*     */   }
/*     */ 
/*     */   public void setDestinationName(String destinationName)
/*     */   {
/* 200 */     this.destinationName = destinationName;
/*     */   }
/*     */ 
/*     */   public int getIncomingClientFrequency()
/*     */   {
/* 210 */     return this.inClientMessagesPerSec;
/*     */   }
/*     */ 
/*     */   public void setIncomingClientFrequency(int n)
/*     */   {
/* 221 */     this.inClientMessagesPerSec = n;
/*     */   }
/*     */ 
/*     */   public int getIncomingDestinationFrequency()
/*     */   {
/* 231 */     return this.inDestinationMessagesPerSec;
/*     */   }
/*     */ 
/*     */   public void setIncomingDestinationFrequency(int n)
/*     */   {
/* 242 */     this.inDestinationMessagesPerSec = n;
/*     */   }
/*     */ 
/*     */   public int getOutgoingClientFrequency()
/*     */   {
/* 252 */     return this.outClientMessagesPerSec;
/*     */   }
/*     */ 
/*     */   public void setOutgoingClientFrequency(int n)
/*     */   {
/* 263 */     this.outClientMessagesPerSec = n;
/*     */   }
/*     */ 
/*     */   public int getOutgoingDestinationFrequency()
/*     */   {
/* 273 */     return this.outDestinationMessagesPerSec;
/*     */   }
/*     */ 
/*     */   public void setOutgoingDestinationFrequency(int n)
/*     */   {
/* 284 */     this.outDestinationMessagesPerSec = n;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.ThrottleSettings
 * JD-Core Version:    0.6.0
 */
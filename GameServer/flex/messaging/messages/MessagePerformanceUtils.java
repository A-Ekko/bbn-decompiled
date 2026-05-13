/*     */ package flex.messaging.messages;
/*     */ 
/*     */ import flex.messaging.io.amf.ActionContext;
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.log.Logger;
/*     */ 
/*     */ public class MessagePerformanceUtils
/*     */ {
/*     */   static final String LOG_CATEGORY = "Message.General";
/*     */   public static final String MPI_HEADER_IN = "DSMPII";
/*     */   public static final String MPI_HEADER_OUT = "DSMPIO";
/*     */   public static final String MPI_HEADER_PUSH = "DSMPIP";
/*  47 */   public static int MPI_NONE = 0;
/*  48 */   public static int MPI_TIMING = 1;
/*  49 */   public static int MPI_TIMING_AND_SIZING = 2;
/*     */ 
/*     */   public static void propogateMPIDownBatch(Message message)
/*     */   {
/*  62 */     long overhead = System.currentTimeMillis();
/*  63 */     if ((message instanceof BatchableMessage))
/*     */     {
/*  65 */       BatchableMessage dm = (BatchableMessage)message;
/*  66 */       if (dm.isBatched())
/*     */       {
/*  68 */         Object[] batchedMessages = (Object[])(Object[])message.getBody();
/*  69 */         int batchedLength = batchedMessages.length;
/*  70 */         for (int a = 0; a < batchedLength; a++)
/*     */         {
/*  72 */           Message currentMess = (Message)batchedMessages[a];
/*  73 */           MessagePerformanceInfo mpi = getMPII(message);
/*  74 */           setMPII(currentMess, (MessagePerformanceInfo)mpi.clone());
/*  75 */           propogateMPIDownBatch(currentMess);
/*     */         }
/*     */       }
/*     */     }
/*  79 */     overhead = System.currentTimeMillis() - overhead;
/*  80 */     getMPII(message).addToOverhead(overhead);
/*     */   }
/*     */ 
/*     */   public static void setupMPII(ActionContext context, Message inMessage)
/*     */   {
/*     */     try
/*     */     {
/* 100 */       MessagePerformanceInfo mpii = getMPII(inMessage);
/*     */ 
/* 103 */       MessagePerformanceInfo contextMPI = context.getMPII();
/* 104 */       if ((contextMPI != null) && (mpii != null)) {
/* 105 */         contextMPI.sendTime = mpii.sendTime;
/* 106 */         setMPII(inMessage, (MessagePerformanceInfo)contextMPI.clone());
/* 107 */         propogateMPIDownBatch(inMessage);
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 112 */       if (Log.isDebug())
/* 113 */         Log.getLogger("Message.General").error("MPI error: setting up response MPI : " + e.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void updateOutgoingMPI(ActionContext context, Message inMessage, Object outMessage)
/*     */   {
/*     */     try
/*     */     {
/* 133 */       MessagePerformanceInfo mpio = null;
/* 134 */       if (context != null)
/*     */       {
/* 136 */         mpio = context.getMPIO();
/* 137 */         if (mpio == null)
/*     */         {
/* 139 */           mpio = new MessagePerformanceInfo();
/* 140 */           if ((getMPII(inMessage) != null) && (getMPII(inMessage).sendTime != 0L))
/* 141 */             mpio.infoType = "OUT";
/*     */         }
/* 143 */         Message mess = (Message)outMessage;
/* 144 */         if (getMPII(inMessage) != null)
/* 145 */           setMPII(mess, (MessagePerformanceInfo)getMPII(inMessage).clone());
/* 146 */         setMPIO(mess, mpio);
/* 147 */         context.setMPIO(mpio);
/*     */       }
/*     */ 
/* 150 */       if (((outMessage instanceof CommandMessage)) && (((CommandMessage)outMessage).getOperation() == 4))
/*     */       {
/* 154 */         CommandMessage cmd = (CommandMessage)outMessage;
/* 155 */         Object[] cmdBody = (Object[])(Object[])cmd.getBody();
/*     */ 
/* 159 */         int batchedLength = cmdBody.length;
/* 160 */         for (int i = 0; i < batchedLength; i++)
/*     */         {
/* 162 */           Message currentMess = (Message)cmdBody[i];
/* 163 */           MessagePerformanceInfo origMPII = getMPII(currentMess);
/*     */ 
/* 165 */           if ((origMPII == null) || (getMPII(inMessage) == null))
/*     */           {
/* 170 */             if (Log.isError())
/*     */             {
/* 172 */               Log.getLogger("Message.General").error("MPI is enabled but could not get message performance information for incoming MPI instance from client message.  The client might have created a new channel not configured to send message performance after declaring a different destination which does.  The client channel should either be configured to add MPI properties, or a server destion which has the same MPI properties as the client channel should be used.");
/*     */             }
/*     */ 
/* 182 */             return;
/*     */           }
/*     */ 
/* 185 */           setMPIP(currentMess, (MessagePerformanceInfo)origMPII.clone());
/* 186 */           MessagePerformanceInfo newMPII = (MessagePerformanceInfo)getMPII(inMessage).clone();
/* 187 */           mpio.pushedFlag = true;
/* 188 */           setMPII(currentMess, newMPII);
/* 189 */           setMPIO(currentMess, mpio);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 195 */       if (Log.isDebug())
/* 196 */         Log.getLogger("Message.General").error("MPI error: setting up response MPI : " + e.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void setMPII(Message message, MessagePerformanceInfo mpi)
/*     */   {
/* 211 */     message.setHeader("DSMPII", mpi);
/*     */   }
/*     */ 
/*     */   public static void setMPIO(Message message, MessagePerformanceInfo mpi)
/*     */   {
/* 224 */     message.setHeader("DSMPIO", mpi);
/*     */   }
/*     */ 
/*     */   public static void setMPIP(Message message, MessagePerformanceInfo mpi)
/*     */   {
/* 238 */     message.setHeader("DSMPIP", mpi);
/*     */   }
/*     */ 
/*     */   public static MessagePerformanceInfo getMPII(Message message)
/*     */   {
/* 251 */     return (MessagePerformanceInfo)message.getHeader("DSMPII");
/*     */   }
/*     */ 
/*     */   public static MessagePerformanceInfo getMPIO(Message message)
/*     */   {
/* 264 */     return (MessagePerformanceInfo)message.getHeader("DSMPIO");
/*     */   }
/*     */ 
/*     */   public static MessagePerformanceInfo getMPIP(Message message)
/*     */   {
/* 278 */     return (MessagePerformanceInfo)message.getHeader("DSMPIP");
/*     */   }
/*     */ 
/*     */   public static void markServerPrePushTime(Message message)
/*     */   {
/* 293 */     if ((getMPII(message) == null) || (getMPII(message).sendTime == 0L)) {
/* 294 */       return;
/*     */     }
/* 296 */     MessagePerformanceInfo mpi = getMPII(message);
/* 297 */     mpi.serverPrePushTime = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   public static void markServerPreAdapterTime(Message message)
/*     */   {
/* 312 */     if ((getMPII(message) == null) || (getMPII(message).sendTime == 0L)) {
/* 313 */       return;
/*     */     }
/* 315 */     MessagePerformanceInfo mpi = getMPII(message);
/*     */ 
/* 319 */     if (mpi.serverPreAdapterTime != 0L) {
/* 320 */       return;
/*     */     }
/* 322 */     mpi.serverPreAdapterTime = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   public static void markServerPostAdapterTime(Message message)
/*     */   {
/* 337 */     if ((getMPII(message) == null) || (getMPII(message).sendTime == 0L) || (getMPII(message).serverPostAdapterTime != 0L)) {
/* 338 */       return;
/*     */     }
/* 340 */     MessagePerformanceInfo mpi = getMPII(message);
/* 341 */     mpi.serverPostAdapterTime = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   public static void markServerPreAdapterExternalTime(Message message)
/*     */   {
/* 363 */     if ((getMPII(message) == null) || (getMPII(message).sendTime == 0L)) {
/* 364 */       return;
/*     */     }
/* 366 */     MessagePerformanceInfo mpi = getMPII(message);
/* 367 */     mpi.serverPreAdapterExternalTime = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   public static void markServerPostAdapterExternalTime(Message message)
/*     */   {
/* 389 */     if ((getMPII(message) == null) || (getMPII(message).sendTime == 0L) || (getMPII(message).serverPostAdapterExternalTime != 0L)) {
/* 390 */       return;
/*     */     }
/* 392 */     MessagePerformanceInfo mpi = getMPII(message);
/* 393 */     mpi.serverPostAdapterExternalTime = System.currentTimeMillis();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.messages.MessagePerformanceUtils
 * JD-Core Version:    0.6.0
 */
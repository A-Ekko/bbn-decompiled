/*     */ package flex.messaging.messages;
/*     */ 
/*     */ import flex.messaging.util.UUIDUtils;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInput;
/*     */ import java.io.ObjectOutput;
/*     */ 
/*     */ public class CommandMessage extends AsyncMessage
/*     */ {
/*     */   public static final String LOG_CATEGORY = "Message.Command";
/*     */   public static final int SUBSCRIBE_OPERATION = 0;
/*     */   public static final int UNSUBSCRIBE_OPERATION = 1;
/*     */   public static final int POLL_OPERATION = 2;
/*     */   public static final int CLIENT_SYNC_OPERATION = 4;
/*     */   public static final int CLIENT_PING_OPERATION = 5;
/*     */   public static final int CLUSTER_REQUEST_OPERATION = 7;
/*     */   public static final int LOGIN_OPERATION = 8;
/*     */   public static final int LOGOUT_OPERATION = 9;
/*     */   public static final int SUBSCRIPTION_INVALIDATE_OPERATION = 10;
/*     */   public static final int MULTI_SUBSCRIBE_OPERATION = 11;
/*     */   public static final int DISCONNECT_OPERATION = 12;
/*     */   public static final int UNKNOWN_OPERATION = 10000;
/*     */   public static final String MESSAGING_VERSION = "DSMessagingVersion";
/*     */   public static final String SELECTOR_HEADER = "DSSelector";
/*     */   public static final String SUBSCRIPTION_INVALIDATED_HEADER = "DSSubscriptionInvalidated";
/*     */   public static final String PRESERVE_DURABLE_HEADER = "DSPreserveDurable";
/*     */   public static final String NEEDS_CONFIG_HEADER = "DSNeedsConfig";
/*     */   public static final String ADD_SUBSCRIPTIONS = "DSAddSub";
/*     */   public static final String REMOVE_SUBSCRIPTIONS = "DSRemSub";
/*     */   public static final String SUBTOPIC_SEPARATOR = "_;_";
/*     */   public static final String POLL_WAIT_HEADER = "DSPollWait";
/*     */   public static final String NO_OP_POLL_HEADER = "DSNoOpPoll";
/*     */   public static final String SUPPRESS_POLL_WAIT_HEADER = "DSSuppressPollWait";
/*     */   public static final String CREDENTIALS_CHARSET_HEADER = "DSCredentialsCharset";
/* 184 */   private static byte OPERATION_FLAG = 1;
/*     */   private static final long serialVersionUID = -4026438615587526303L;
/* 194 */   static final String[] operationNames = { "subscribe", "unsubscribe", "poll", "unused3", "client_sync", "client_ping", "unused6", "cluster_request", "login", "logout", "subscription_invalidate", "multi_subscribe", "disconnect" };
/*     */ 
/* 203 */   private int operation = 10000;
/*     */ 
/*     */   public CommandMessage()
/*     */   {
/* 213 */     this.messageId = UUIDUtils.createUUID();
/* 214 */     this.timestamp = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   public CommandMessage(int operation)
/*     */   {
/* 226 */     this();
/* 227 */     this.operation = operation;
/*     */   }
/*     */ 
/*     */   public int getOperation()
/*     */   {
/* 237 */     return this.operation;
/*     */   }
/*     */ 
/*     */   public void setOperation(int operation)
/*     */   {
/* 247 */     this.operation = operation;
/*     */   }
/*     */ 
/*     */   public Message getSmallMessage()
/*     */   {
/* 258 */     if (this.operation == 2)
/*     */     {
/* 260 */       return new CommandMessageExt(this);
/*     */     }
/*     */ 
/* 263 */     return null;
/*     */   }
/*     */ 
/*     */   public static String operationToString(int operation)
/*     */   {
/* 273 */     if ((operation < 0) || (operation >= operationNames.length))
/* 274 */       return "invalid." + operation + "";
/* 275 */     return operationNames[operation];
/*     */   }
/*     */ 
/*     */   public void readExternal(ObjectInput input)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 283 */     super.readExternal(input);
/*     */ 
/* 285 */     short[] flagsArray = readFlags(input);
/* 286 */     for (int i = 0; i < flagsArray.length; i++)
/*     */     {
/* 288 */       short flags = flagsArray[i];
/* 289 */       short reservedPosition = 0;
/*     */ 
/* 291 */       if (i == 0)
/*     */       {
/* 293 */         if ((flags & OPERATION_FLAG) != 0) {
/* 294 */           this.operation = ((Number)input.readObject()).intValue();
/*     */         }
/* 296 */         reservedPosition = 1;
/*     */       }
/*     */ 
/* 301 */       if (flags >> reservedPosition == 0)
/*     */         continue;
/* 303 */       for (short j = reservedPosition; j < 6; j = (short)(j + 1))
/*     */       {
/* 305 */         if ((flags >> j & 0x1) == 0)
/*     */           continue;
/* 307 */         input.readObject();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected String toStringFields(int indentLevel)
/*     */   {
/* 323 */     String sep = getFieldSeparator(indentLevel);
/* 324 */     String s = sep + "operation = " + operationToString(this.operation);
/* 325 */     if (this.operation == 0)
/* 326 */       s = s + sep + "selector = " + getHeader("DSSelector");
/* 327 */     if (this.operation != 8)
/*     */     {
/* 329 */       s = s + super.toStringFields(indentLevel);
/*     */     }
/*     */     else
/*     */     {
/* 333 */       s = s + sep + "clientId =  " + this.clientId;
/* 334 */       s = s + sep + "destination =  " + this.destination;
/* 335 */       s = s + sep + "messageId =  " + this.messageId;
/* 336 */       s = s + sep + "timestamp =  " + this.timestamp;
/* 337 */       s = s + sep + "timeToLive =  " + this.timeToLive;
/* 338 */       s = s + sep + "***not printing credentials***";
/*     */     }
/* 340 */     return s;
/*     */   }
/*     */ 
/*     */   public void writeExternal(ObjectOutput output)
/*     */     throws IOException
/*     */   {
/* 348 */     super.writeExternal(output);
/*     */ 
/* 350 */     short flags = 0;
/*     */ 
/* 352 */     if (this.operation != 0) {
/* 353 */       flags = (short)(flags | OPERATION_FLAG);
/*     */     }
/* 355 */     output.writeByte(flags);
/*     */ 
/* 357 */     if (this.operation != 0)
/* 358 */       output.writeObject(new Integer(this.operation));
/*     */   }
/*     */ 
/*     */   public String logCategory()
/*     */   {
/* 367 */     return "Message.Command." + operationToString(this.operation);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.messages.CommandMessage
 * JD-Core Version:    0.6.0
 */
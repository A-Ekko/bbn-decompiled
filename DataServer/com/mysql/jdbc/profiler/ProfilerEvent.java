/*     */ package com.mysql.jdbc.profiler;
/*     */ 
/*     */ import com.mysql.jdbc.Util;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class ProfilerEvent
/*     */ {
/*     */   public static final byte TYPE_WARN = 0;
/*     */   public static final byte TYPE_OBJECT_CREATION = 1;
/*     */   public static final byte TYPE_PREPARE = 2;
/*     */   public static final byte TYPE_QUERY = 3;
/*     */   public static final byte TYPE_EXECUTE = 4;
/*     */   public static final byte TYPE_FETCH = 5;
/*     */   protected byte eventType;
/*     */   protected int connectionId;
/*     */   protected int statementId;
/*     */   protected int resultSetId;
/*     */   protected long eventCreationTime;
/*     */   protected int eventDurationMillis;
/*     */   protected int hostNameIndex;
/*     */   protected String hostName;
/*     */   protected int catalogIndex;
/*     */   protected String catalog;
/*     */   protected int eventCreationPointIndex;
/*     */   protected Throwable eventCreationPoint;
/*     */   protected String eventCreationPointDesc;
/*     */   protected String message;
/*     */ 
/*     */   public ProfilerEvent(byte eventType, String hostName, String catalog, int connectionId, int statementId, int resultSetId, long eventCreationTime, int eventDurationMillis, String eventCreationPointDesc, Throwable eventCreationPoint, String message)
/*     */   {
/* 171 */     this.eventType = eventType;
/* 172 */     this.connectionId = connectionId;
/* 173 */     this.statementId = statementId;
/* 174 */     this.resultSetId = resultSetId;
/* 175 */     this.eventCreationTime = eventCreationTime;
/* 176 */     this.eventDurationMillis = eventDurationMillis;
/* 177 */     this.eventCreationPoint = eventCreationPoint;
/* 178 */     this.eventCreationPointDesc = eventCreationPointDesc;
/* 179 */     this.message = message;
/*     */   }
/*     */ 
/*     */   public String getEventCreationPointAsString()
/*     */   {
/* 188 */     if (this.eventCreationPointDesc == null) {
/* 189 */       this.eventCreationPointDesc = Util.stackTraceToString(this.eventCreationPoint);
/*     */     }
/*     */ 
/* 193 */     return this.eventCreationPointDesc;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 202 */     StringBuffer buf = new StringBuffer(32);
/*     */ 
/* 204 */     switch (this.eventType) {
/*     */     case 4:
/* 206 */       buf.append("EXECUTE");
/* 207 */       break;
/*     */     case 5:
/* 210 */       buf.append("FETCH");
/* 211 */       break;
/*     */     case 1:
/* 214 */       buf.append("CONSTRUCT");
/* 215 */       break;
/*     */     case 2:
/* 218 */       buf.append("PREPARE");
/* 219 */       break;
/*     */     case 3:
/* 222 */       buf.append("QUERY");
/* 223 */       break;
/*     */     case 0:
/* 226 */       buf.append("WARN");
/* 227 */       break;
/*     */     default:
/* 229 */       buf.append("UNKNOWN");
/*     */     }
/*     */ 
/* 232 */     buf.append(" created: ");
/* 233 */     buf.append(new Date(this.eventCreationTime));
/* 234 */     buf.append(" duration: ");
/* 235 */     buf.append(this.eventDurationMillis);
/* 236 */     buf.append(" connection: ");
/* 237 */     buf.append(this.connectionId);
/* 238 */     buf.append(" statement: ");
/* 239 */     buf.append(this.statementId);
/* 240 */     buf.append(" resultset: ");
/* 241 */     buf.append(this.resultSetId);
/*     */ 
/* 243 */     if (this.message != null) {
/* 244 */       buf.append(" message: ");
/* 245 */       buf.append(this.message);
/*     */     }
/*     */ 
/* 249 */     if (this.eventCreationPointDesc != null) {
/* 250 */       buf.append("\n\nEvent Created at:\n");
/* 251 */       buf.append(this.eventCreationPointDesc);
/*     */     }
/*     */ 
/* 254 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   public static ProfilerEvent unpack(byte[] buf)
/*     */     throws Exception
/*     */   {
/* 267 */     int pos = 0;
/*     */ 
/* 269 */     byte eventType = buf[(pos++)];
/* 270 */     int connectionId = readInt(buf, pos);
/* 271 */     pos += 4;
/* 272 */     int statementId = readInt(buf, pos);
/* 273 */     pos += 4;
/* 274 */     int resultSetId = readInt(buf, pos);
/* 275 */     pos += 4;
/* 276 */     long eventCreationTime = readLong(buf, pos);
/* 277 */     pos += 8;
/* 278 */     int eventDurationMillis = readInt(buf, pos);
/* 279 */     pos += 4;
/* 280 */     int eventCreationPointIndex = readInt(buf, pos);
/* 281 */     pos += 4;
/* 282 */     byte[] eventCreationAsBytes = readBytes(buf, pos);
/* 283 */     pos += 4;
/*     */ 
/* 285 */     if (eventCreationAsBytes != null) {
/* 286 */       pos += eventCreationAsBytes.length;
/*     */     }
/*     */ 
/* 289 */     byte[] message = readBytes(buf, pos);
/* 290 */     pos += 4;
/*     */ 
/* 292 */     if (message != null) {
/* 293 */       pos += message.length;
/*     */     }
/*     */ 
/* 296 */     return new ProfilerEvent(eventType, "", "", connectionId, statementId, resultSetId, eventCreationTime, eventDurationMillis, new String(eventCreationAsBytes, "ISO8859_1"), null, new String(message, "ISO8859_1"));
/*     */   }
/*     */ 
/*     */   public byte[] pack()
/*     */     throws Exception
/*     */   {
/* 311 */     int len = 29;
/*     */ 
/* 313 */     byte[] eventCreationAsBytes = null;
/*     */ 
/* 315 */     getEventCreationPointAsString();
/*     */ 
/* 317 */     if (this.eventCreationPointDesc != null) {
/* 318 */       eventCreationAsBytes = this.eventCreationPointDesc.getBytes("ISO8859_1");
/*     */ 
/* 320 */       len += 4 + eventCreationAsBytes.length;
/*     */     } else {
/* 322 */       len += 4;
/*     */     }
/*     */ 
/* 325 */     byte[] messageAsBytes = null;
/*     */ 
/* 327 */     if (messageAsBytes != null) {
/* 328 */       messageAsBytes = this.message.getBytes("ISO8859_1");
/* 329 */       len += 4 + messageAsBytes.length;
/*     */     } else {
/* 331 */       len += 4;
/*     */     }
/*     */ 
/* 334 */     byte[] buf = new byte[len];
/*     */ 
/* 336 */     int pos = 0;
/*     */ 
/* 338 */     buf[(pos++)] = this.eventType;
/* 339 */     pos = writeInt(this.connectionId, buf, pos);
/* 340 */     pos = writeInt(this.statementId, buf, pos);
/* 341 */     pos = writeInt(this.resultSetId, buf, pos);
/* 342 */     pos = writeLong(this.eventCreationTime, buf, pos);
/* 343 */     pos = writeInt(this.eventDurationMillis, buf, pos);
/* 344 */     pos = writeInt(this.eventCreationPointIndex, buf, pos);
/*     */ 
/* 346 */     if (eventCreationAsBytes != null)
/* 347 */       pos = writeBytes(eventCreationAsBytes, buf, pos);
/*     */     else {
/* 349 */       pos = writeInt(0, buf, pos);
/*     */     }
/*     */ 
/* 352 */     if (messageAsBytes != null)
/* 353 */       pos = writeBytes(messageAsBytes, buf, pos);
/*     */     else {
/* 355 */       pos = writeInt(0, buf, pos);
/*     */     }
/*     */ 
/* 358 */     return buf;
/*     */   }
/*     */ 
/*     */   private static int writeInt(int i, byte[] buf, int pos)
/*     */   {
/* 363 */     buf[(pos++)] = (byte)(i & 0xFF);
/* 364 */     buf[(pos++)] = (byte)(i >>> 8);
/* 365 */     buf[(pos++)] = (byte)(i >>> 16);
/* 366 */     buf[(pos++)] = (byte)(i >>> 24);
/*     */ 
/* 368 */     return pos;
/*     */   }
/*     */ 
/*     */   private static int writeLong(long l, byte[] buf, int pos) {
/* 372 */     buf[(pos++)] = (byte)(int)(l & 0xFF);
/* 373 */     buf[(pos++)] = (byte)(int)(l >>> 8);
/* 374 */     buf[(pos++)] = (byte)(int)(l >>> 16);
/* 375 */     buf[(pos++)] = (byte)(int)(l >>> 24);
/* 376 */     buf[(pos++)] = (byte)(int)(l >>> 32);
/* 377 */     buf[(pos++)] = (byte)(int)(l >>> 40);
/* 378 */     buf[(pos++)] = (byte)(int)(l >>> 48);
/* 379 */     buf[(pos++)] = (byte)(int)(l >>> 56);
/*     */ 
/* 381 */     return pos;
/*     */   }
/*     */ 
/*     */   private static int writeBytes(byte[] msg, byte[] buf, int pos) {
/* 385 */     pos = writeInt(msg.length, buf, pos);
/*     */ 
/* 387 */     System.arraycopy(msg, 0, buf, pos, msg.length);
/*     */ 
/* 389 */     return pos + msg.length;
/*     */   }
/*     */ 
/*     */   private static int readInt(byte[] buf, int pos) {
/* 393 */     return buf[(pos++)] & 0xFF | (buf[(pos++)] & 0xFF) << 8 | (buf[(pos++)] & 0xFF) << 16 | (buf[(pos++)] & 0xFF) << 24;
/*     */   }
/*     */ 
/*     */   private static long readLong(byte[] buf, int pos)
/*     */   {
/* 399 */     return buf[(pos++)] & 0xFF | (buf[(pos++)] & 0xFF) << 8 | (buf[(pos++)] & 0xFF) << 16 | (buf[(pos++)] & 0xFF) << 24 | (buf[(pos++)] & 0xFF) << 32 | (buf[(pos++)] & 0xFF) << 40 | (buf[(pos++)] & 0xFF) << 48 | (buf[(pos++)] & 0xFF) << 56;
/*     */   }
/*     */ 
/*     */   private static byte[] readBytes(byte[] buf, int pos)
/*     */   {
/* 409 */     int length = readInt(buf, pos);
/*     */ 
/* 411 */     pos += 4;
/*     */ 
/* 413 */     byte[] msg = new byte[length];
/* 414 */     System.arraycopy(buf, pos, msg, 0, length);
/*     */ 
/* 416 */     return msg;
/*     */   }
/*     */ 
/*     */   public String getCatalog()
/*     */   {
/* 425 */     return this.catalog;
/*     */   }
/*     */ 
/*     */   public int getConnectionId()
/*     */   {
/* 434 */     return this.connectionId;
/*     */   }
/*     */ 
/*     */   public Throwable getEventCreationPoint()
/*     */   {
/* 444 */     return this.eventCreationPoint;
/*     */   }
/*     */ 
/*     */   public long getEventCreationTime()
/*     */   {
/* 454 */     return this.eventCreationTime;
/*     */   }
/*     */ 
/*     */   public int getEventDurationMillis()
/*     */   {
/* 463 */     return this.eventDurationMillis;
/*     */   }
/*     */ 
/*     */   public byte getEventType()
/*     */   {
/* 472 */     return this.eventType;
/*     */   }
/*     */ 
/*     */   public int getResultSetId()
/*     */   {
/* 481 */     return this.resultSetId;
/*     */   }
/*     */ 
/*     */   public int getStatementId()
/*     */   {
/* 490 */     return this.statementId;
/*     */   }
/*     */ 
/*     */   public String getMessage()
/*     */   {
/* 499 */     return this.message;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.mysql.jdbc.profiler.ProfilerEvent
 * JD-Core Version:    0.6.0
 */
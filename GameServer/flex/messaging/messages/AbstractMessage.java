/*     */ package flex.messaging.messages;
/*     */ 
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.util.ExceptionUtil;
/*     */ import flex.messaging.util.StringUtils;
/*     */ import flex.messaging.util.UUIDUtils;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInput;
/*     */ import java.io.ObjectOutput;
/*     */ import java.util.HashMap;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ public abstract class AbstractMessage
/*     */   implements Message, Cloneable
/*     */ {
/*     */   private static final long serialVersionUID = -834697863344344313L;
/*     */   private static final short HAS_NEXT_FLAG = 128;
/*     */   private static final short BODY_FLAG = 1;
/*     */   private static final short CLIENT_ID_FLAG = 2;
/*     */   private static final short DESTINATION_FLAG = 4;
/*     */   private static final short HEADERS_FLAG = 8;
/*     */   private static final short MESSAGE_ID_FLAG = 16;
/*     */   private static final short TIMESTAMP_FLAG = 32;
/*     */   private static final short TIME_TO_LIVE_FLAG = 64;
/*     */   private static final short CLIENT_ID_BYTES_FLAG = 1;
/*     */   private static final short MESSAGE_ID_BYTES_FLAG = 2;
/*     */   protected Object clientId;
/*     */   protected String destination;
/*     */   protected String messageId;
/*     */   protected long timestamp;
/*     */   protected long timeToLive;
/*     */   protected Map headers;
/*     */   protected Object body;
/*     */   private byte[] clientIdBytes;
/*     */   private byte[] messageIdBytes;
/* 397 */   static final String[] indentLevels = { "", "  ", "    ", "      ", "        ", "          " };
/*     */ 
/*     */   public Object getClientId()
/*     */   {
/*  77 */     return this.clientId;
/*     */   }
/*     */ 
/*     */   public void setClientId(Object clientId)
/*     */   {
/*  82 */     this.clientId = clientId;
/*  83 */     this.clientIdBytes = null;
/*     */   }
/*     */ 
/*     */   public String getMessageId()
/*     */   {
/*  88 */     return this.messageId;
/*     */   }
/*     */ 
/*     */   public void setMessageId(String messageId)
/*     */   {
/*  93 */     this.messageId = messageId;
/*  94 */     this.messageIdBytes = null;
/*     */   }
/*     */ 
/*     */   public long getTimestamp()
/*     */   {
/*  99 */     return this.timestamp;
/*     */   }
/*     */ 
/*     */   public void setTimestamp(long timestamp)
/*     */   {
/* 104 */     this.timestamp = timestamp;
/*     */   }
/*     */ 
/*     */   public long getTimeToLive()
/*     */   {
/* 109 */     return this.timeToLive;
/*     */   }
/*     */ 
/*     */   public void setTimeToLive(long timeToLive)
/*     */   {
/* 114 */     this.timeToLive = timeToLive;
/*     */   }
/*     */ 
/*     */   public Object getBody()
/*     */   {
/* 119 */     return this.body;
/*     */   }
/*     */ 
/*     */   public void setBody(Object body)
/*     */   {
/* 124 */     this.body = body;
/*     */   }
/*     */ 
/*     */   public String getDestination()
/*     */   {
/* 129 */     return this.destination;
/*     */   }
/*     */ 
/*     */   public void setDestination(String destination)
/*     */   {
/* 134 */     this.destination = destination;
/*     */   }
/*     */ 
/*     */   public Map getHeaders()
/*     */   {
/* 139 */     if (this.headers == null)
/*     */     {
/* 141 */       this.headers = new HashMap();
/*     */     }
/* 143 */     return this.headers;
/*     */   }
/*     */ 
/*     */   public void setHeaders(Map newHeaders)
/*     */   {
/* 148 */     for (Iterator iter = newHeaders.entrySet().iterator(); iter.hasNext(); )
/*     */     {
/* 150 */       Map.Entry entry = (Map.Entry)iter.next();
/* 151 */       String propName = (String)entry.getKey();
/* 152 */       setHeader(propName, entry.getValue());
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object getHeader(String headerName)
/*     */   {
/* 158 */     if (this.headers == null)
/*     */     {
/* 160 */       return null;
/*     */     }
/* 162 */     return this.headers.get(headerName);
/*     */   }
/*     */ 
/*     */   public void setHeader(String headerName, Object value)
/*     */   {
/* 167 */     if (this.headers == null)
/*     */     {
/* 169 */       this.headers = new HashMap();
/*     */     }
/*     */ 
/* 172 */     if (value == null)
/*     */     {
/* 174 */       this.headers.remove(headerName);
/*     */     }
/*     */     else
/*     */     {
/* 178 */       this.headers.put(headerName, value);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean headerExists(String headerName)
/*     */   {
/* 184 */     return (this.headers != null) && (this.headers.containsKey(headerName));
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 189 */     if ((o instanceof Cloneable))
/*     */     {
/* 191 */       if (this.messageId == null) {
/* 192 */         return this == o;
/*     */       }
/* 194 */       Message m = (Cloneable)o;
/* 195 */       if (m.getMessageId().equals(getMessageId()))
/*     */       {
/* 197 */         return true;
/*     */       }
/*     */     }
/* 200 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 205 */     if (this.messageId == null)
/* 206 */       return super.hashCode();
/* 207 */     return this.messageId.hashCode();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 212 */     return toString(1);
/*     */   }
/*     */ 
/*     */   public String toString(int indent)
/*     */   {
/* 217 */     return toStringHeader(indent) + toStringFields(indent + 1);
/*     */   }
/*     */ 
/*     */   public void readExternal(ObjectInput input)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 231 */     short[] flagsArray = readFlags(input);
/*     */ 
/* 233 */     for (int i = 0; i < flagsArray.length; i++)
/*     */     {
/* 235 */       short flags = flagsArray[i];
/* 236 */       short reservedPosition = 0;
/*     */ 
/* 238 */       if (i == 0)
/*     */       {
/* 240 */         if ((flags & 0x1) != 0) {
/* 241 */           this.body = input.readObject();
/*     */         }
/* 243 */         if ((flags & 0x2) != 0) {
/* 244 */           this.clientId = input.readObject();
/*     */         }
/* 246 */         if ((flags & 0x4) != 0) {
/* 247 */           this.destination = ((String)input.readObject());
/*     */         }
/* 249 */         if ((flags & 0x8) != 0) {
/* 250 */           this.headers = ((Map)input.readObject());
/*     */         }
/* 252 */         if ((flags & 0x10) != 0) {
/* 253 */           this.messageId = ((String)input.readObject());
/*     */         }
/* 255 */         if ((flags & 0x20) != 0) {
/* 256 */           this.timestamp = ((Number)input.readObject()).longValue();
/*     */         }
/* 258 */         if ((flags & 0x40) != 0) {
/* 259 */           this.timeToLive = ((Number)input.readObject()).longValue();
/*     */         }
/* 261 */         reservedPosition = 7;
/*     */       }
/* 263 */       else if (i == 1)
/*     */       {
/* 265 */         if ((flags & 0x1) != 0)
/*     */         {
/* 267 */           this.clientIdBytes = ((byte[])(byte[])input.readObject());
/* 268 */           this.clientId = UUIDUtils.fromByteArray(this.clientIdBytes);
/*     */         }
/*     */ 
/* 271 */         if ((flags & 0x2) != 0)
/*     */         {
/* 273 */           this.messageIdBytes = ((byte[])(byte[])input.readObject());
/* 274 */           this.messageId = UUIDUtils.fromByteArray(this.messageIdBytes);
/*     */         }
/*     */ 
/* 277 */         reservedPosition = 2;
/*     */       }
/*     */ 
/* 282 */       if (flags >> reservedPosition == 0)
/*     */         continue;
/* 284 */       for (short j = reservedPosition; j < 6; j = (short)(j + 1))
/*     */       {
/* 286 */         if ((flags >> j & 0x1) == 0)
/*     */           continue;
/* 288 */         input.readObject();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeExternal(ObjectOutput output)
/*     */     throws IOException
/*     */   {
/* 306 */     short flags = 0;
/*     */ 
/* 308 */     if ((this.clientIdBytes == null) && ((this.clientId instanceof String))) {
/* 309 */       this.clientIdBytes = UUIDUtils.toByteArray((String)this.clientId);
/*     */     }
/* 311 */     if (this.messageIdBytes == null) {
/* 312 */       this.messageIdBytes = UUIDUtils.toByteArray(this.messageId);
/*     */     }
/* 314 */     if (this.body != null) {
/* 315 */       flags = (short)(flags | 0x1);
/*     */     }
/* 317 */     if ((this.clientId != null) && (this.clientIdBytes == null)) {
/* 318 */       flags = (short)(flags | 0x2);
/*     */     }
/* 320 */     if (this.destination != null) {
/* 321 */       flags = (short)(flags | 0x4);
/*     */     }
/* 323 */     if (this.headers != null) {
/* 324 */       flags = (short)(flags | 0x8);
/*     */     }
/* 326 */     if ((this.messageId != null) && (this.messageIdBytes == null)) {
/* 327 */       flags = (short)(flags | 0x10);
/*     */     }
/* 329 */     if (this.timestamp != 0L) {
/* 330 */       flags = (short)(flags | 0x20);
/*     */     }
/* 332 */     if (this.timeToLive != 0L) {
/* 333 */       flags = (short)(flags | 0x40);
/*     */     }
/* 335 */     if ((this.clientIdBytes != null) || (this.messageIdBytes != null)) {
/* 336 */       flags = (short)(flags | 0x80);
/*     */     }
/* 338 */     output.writeByte(flags);
/*     */ 
/* 340 */     flags = 0;
/*     */ 
/* 342 */     if (this.clientIdBytes != null) {
/* 343 */       flags = (short)(flags | 0x1);
/*     */     }
/* 345 */     if (this.messageIdBytes != null) {
/* 346 */       flags = (short)(flags | 0x2);
/*     */     }
/* 348 */     if (flags != 0) {
/* 349 */       output.writeByte(flags);
/*     */     }
/* 351 */     if (this.body != null) {
/* 352 */       output.writeObject(this.body);
/*     */     }
/* 354 */     if ((this.clientId != null) && (this.clientIdBytes == null)) {
/* 355 */       output.writeObject(this.clientId);
/*     */     }
/* 357 */     if (this.destination != null) {
/* 358 */       output.writeObject(this.destination);
/*     */     }
/* 360 */     if (this.headers != null) {
/* 361 */       output.writeObject(this.headers);
/*     */     }
/* 363 */     if ((this.messageId != null) && (this.messageIdBytes == null)) {
/* 364 */       output.writeObject(this.messageId);
/*     */     }
/* 366 */     if (this.timestamp != 0L) {
/* 367 */       output.writeObject(new Long(this.timestamp));
/*     */     }
/* 369 */     if (this.timeToLive != 0L) {
/* 370 */       output.writeObject(new Long(this.timeToLive));
/*     */     }
/* 372 */     if (this.clientIdBytes != null) {
/* 373 */       output.writeObject(this.clientIdBytes);
/*     */     }
/* 375 */     if (this.messageIdBytes != null)
/* 376 */       output.writeObject(this.messageIdBytes);
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/* 381 */     AbstractMessage m = null;
/*     */     try
/*     */     {
/* 384 */       m = (AbstractMessage)super.clone();
/*     */ 
/* 387 */       if (this.headers != null) {
/* 388 */         m.headers = ((HashMap)((HashMap)this.headers).clone());
/*     */       }
/*     */     }
/*     */     catch (CloneNotSupportedException exc)
/*     */     {
/*     */     }
/* 394 */     return m;
/*     */   }
/*     */ 
/*     */   protected String getIndent(int indentLevel)
/*     */   {
/* 402 */     if (indentLevel < indentLevels.length) return indentLevels[indentLevel];
/* 403 */     StringBuffer sb = new StringBuffer();
/* 404 */     sb.append(indentLevels[(indentLevels.length - 1)]);
/* 405 */     indentLevel -= indentLevels.length - 1;
/* 406 */     for (int i = 0; i < indentLevel; i++)
/* 407 */       sb.append("  ");
/* 408 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   protected String getFieldSeparator(int indentLevel)
/*     */   {
/* 413 */     String indStr = getIndent(indentLevel);
/* 414 */     if (indentLevel > 0)
/* 415 */       indStr = StringUtils.NEWLINE + indStr;
/*     */     else
/* 417 */       indStr = " ";
/* 418 */     return indStr;
/*     */   }
/*     */ 
/*     */   protected String toStringHeader(int indentLevel)
/*     */   {
/* 423 */     String s = "Flex Message";
/* 424 */     s = s + " (" + getClass().getName() + ") ";
/* 425 */     return s;
/*     */   }
/*     */ 
/*     */   protected String toStringFields(int indentLevel)
/*     */   {
/* 430 */     if (this.headers != null)
/*     */     {
/* 432 */       String sep = getFieldSeparator(indentLevel);
/* 433 */       String s = "";
/* 434 */       for (Iterator i = this.headers.entrySet().iterator(); i.hasNext(); )
/*     */       {
/* 436 */         Map.Entry e = (Map.Entry)i.next();
/* 437 */         String key = e.getKey().toString();
/* 438 */         s = s + sep + "hdr(" + key + ") = ";
/* 439 */         if (Log.isExcludedProperty(key))
/* 440 */           s = s + "** [Value Suppressed] **";
/*     */         else
/* 442 */           s = s + bodyToString(e.getValue(), indentLevel + 1);
/*     */       }
/* 444 */       return s;
/*     */     }
/* 446 */     return "";
/*     */   }
/*     */ 
/*     */   protected final String bodyToString(Object body, int indentLevel)
/*     */   {
/* 455 */     return bodyToString(body, indentLevel, null);
/*     */   }
/*     */ 
/*     */   protected final String bodyToString(Object body, int indentLevel, Map visited)
/*     */   {
/*     */     try
/*     */     {
/* 466 */       indentLevel += 1;
/* 467 */       if ((visited == null) && (indentLevel > 18))
/* 468 */         return StringUtils.NEWLINE + getFieldSeparator(indentLevel) + "<..max-depth-reached..>";
/* 469 */       return internalBodyToString(body, indentLevel, visited);
/*     */     }
/*     */     catch (RuntimeException exc) {
/*     */     }
/* 473 */     return "Exception in body toString: " + ExceptionUtil.toString(exc);
/*     */   }
/*     */ 
/*     */   protected String internalBodyToString(Object body, int indentLevel)
/*     */   {
/* 479 */     return internalBodyToString(body, indentLevel, null);
/*     */   }
/*     */ 
/*     */   protected String internalBodyToString(Object body, int indentLevel, Map visited)
/*     */   {
/* 484 */     if ((body instanceof Object[]))
/*     */     {
/* 486 */       if ((visited = checkVisited(visited, body)) == null) {
/* 487 */         return "<--";
/*     */       }
/* 489 */       String sep = getFieldSeparator(indentLevel);
/* 490 */       StringBuffer sb = new StringBuffer();
/* 491 */       Object[] arr = (Object[])(Object[])body;
/* 492 */       sb.append(getFieldSeparator(indentLevel - 1));
/* 493 */       sb.append("[");
/* 494 */       sb.append(sep);
/* 495 */       for (int i = 0; i < arr.length; i++)
/*     */       {
/* 497 */         if (i != 0)
/*     */         {
/* 499 */           sb.append(",");
/* 500 */           sb.append(sep);
/*     */         }
/* 502 */         sb.append(bodyToString(arr[i], indentLevel, visited));
/*     */       }
/* 504 */       sb.append(getFieldSeparator(indentLevel - 1));
/* 505 */       sb.append("]");
/* 506 */       return sb.toString();
/*     */     }
/*     */ 
/* 510 */     if ((body instanceof Map))
/*     */     {
/* 512 */       Map bodyMap = (Map)body;
/* 513 */       StringBuffer buf = new StringBuffer();
/* 514 */       buf.append("{");
/* 515 */       Iterator it = bodyMap.entrySet().iterator();
/* 516 */       while (it.hasNext())
/*     */       {
/* 518 */         Map.Entry e = (Map.Entry)it.next();
/* 519 */         Object key = e.getKey();
/* 520 */         Object value = e.getValue();
/* 521 */         buf.append(key == this ? "(recursive Map as key)" : key);
/* 522 */         buf.append("=");
/* 523 */         if (value == this)
/* 524 */           buf.append("(recursive Map as value)");
/* 525 */         else if (Log.isExcludedProperty(key.toString()))
/* 526 */           buf.append("** [Value Suppressed] **");
/*     */         else {
/* 528 */           buf.append(bodyToString(value, indentLevel + 1, visited));
/*     */         }
/* 530 */         if (it.hasNext())
/* 531 */           buf.append(", ");
/*     */       }
/* 533 */       buf.append("}");
/* 534 */       return buf.toString();
/*     */     }
/* 536 */     if ((body instanceof AbstractMessage))
/*     */     {
/* 538 */       return ((AbstractMessage)body).toString(indentLevel);
/*     */     }
/* 540 */     if (body != null)
/* 541 */       return body.toString();
/* 542 */     return "null";
/*     */   }
/*     */ 
/*     */   protected short[] readFlags(ObjectInput input)
/*     */     throws IOException
/*     */   {
/* 557 */     boolean hasNextFlag = true;
/* 558 */     short[] flagsArray = new short[2];
/* 559 */     int i = 0;
/*     */ 
/* 561 */     while (hasNextFlag)
/*     */     {
/* 563 */       short flags = (short)input.readUnsignedByte();
/* 564 */       if (i == flagsArray.length)
/*     */       {
/* 566 */         short[] tempArray = new short[i * 2];
/* 567 */         System.arraycopy(flagsArray, 0, tempArray, 0, flagsArray.length);
/* 568 */         flagsArray = tempArray;
/*     */       }
/*     */ 
/* 571 */       flagsArray[i] = flags;
/*     */ 
/* 573 */       if ((flags & 0x80) != 0)
/* 574 */         hasNextFlag = true;
/*     */       else {
/* 576 */         hasNextFlag = false;
/*     */       }
/* 578 */       i++;
/*     */     }
/*     */ 
/* 581 */     return flagsArray;
/*     */   }
/*     */ 
/*     */   public String logCategory()
/*     */   {
/* 589 */     return "Message.General";
/*     */   }
/*     */ 
/*     */   private Map checkVisited(Map visited, Object obj)
/*     */   {
/* 594 */     if (visited == null)
/* 595 */       visited = new IdentityHashMap();
/* 596 */     else if (visited.get(obj) != null) {
/* 597 */       return null;
/*     */     }
/* 599 */     visited.put(obj, Boolean.TRUE);
/*     */ 
/* 601 */     return visited;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.messages.AbstractMessage
 * JD-Core Version:    0.6.0
 */
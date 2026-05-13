/*     */ package flex.messaging.io.amf;
/*     */ 
/*     */ import flex.messaging.util.ObjectTrace;
/*     */ 
/*     */ public class AmfTrace extends ObjectTrace
/*     */ {
/*     */   public AmfTrace()
/*     */   {
/*  39 */     this.buffer = new StringBuffer(4096);
/*     */   }
/*     */ 
/*     */   public void startRequest(String message)
/*     */   {
/*  49 */     this.m_indent = 0;
/*  50 */     this.buffer.append(message);
/*  51 */     this.m_indent += 1;
/*     */   }
/*     */ 
/*     */   public void startResponse(String message)
/*     */   {
/*  61 */     this.m_indent = 0;
/*  62 */     this.buffer.append(message);
/*  63 */     this.m_indent += 1;
/*     */   }
/*     */ 
/*     */   public void version(int version)
/*     */   {
/*  73 */     newLine();
/*  74 */     this.buffer.append("Version: ").append(version);
/*     */   }
/*     */ 
/*     */   public void startHeader(String name, boolean mustUnderstand, int index)
/*     */   {
/*  86 */     newLine();
/*  87 */     this.buffer.append(indentString());
/*  88 */     this.buffer.append("(Header #").append(index).append(" name=").append(name);
/*  89 */     this.buffer.append(", mustUnderstand=").append(mustUnderstand).append(")");
/*  90 */     newLine();
/*  91 */     this.m_indent += 1;
/*     */   }
/*     */ 
/*     */   public void endHeader()
/*     */   {
/*  99 */     this.m_indent -= 1;
/*     */   }
/*     */ 
/*     */   public void startCommand(Object cmd, int iCmd, Object trxId)
/*     */   {
/* 111 */     newLine();
/* 112 */     this.buffer.append(indentString());
/* 113 */     this.buffer.append("(Command method=").append(cmd).append(" (").append(iCmd).append(")");
/* 114 */     this.buffer.append(" trxId=").append(trxId).append(")");
/* 115 */     newLine();
/* 116 */     this.m_indent += 1;
/*     */   }
/*     */ 
/*     */   public void endCommand()
/*     */   {
/* 124 */     this.m_indent -= 1;
/*     */   }
/*     */ 
/*     */   public void startMessage(String targetURI, String responseURI, int index)
/*     */   {
/* 136 */     newLine();
/* 137 */     this.buffer.append(indentString());
/* 138 */     this.buffer.append("(Message #").append(index).append(" targetURI=").append(targetURI);
/* 139 */     this.buffer.append(", responseURI=").append(responseURI).append(")");
/* 140 */     newLine();
/* 141 */     this.m_indent += 1;
/*     */   }
/*     */ 
/*     */   public void endMessage()
/*     */   {
/* 149 */     this.m_indent -= 1;
/*     */   }
/*     */ 
/*     */   private boolean isExcluded()
/*     */   {
/* 157 */     if (this.nextElementExclude)
/*     */     {
/* 159 */       this.nextElementExclude = false;
/* 160 */       this.buffer.append("** [Value Suppressed] **");
/* 161 */       newLine();
/* 162 */       return true;
/*     */     }
/*     */ 
/* 166 */     return false;
/*     */   }
/*     */ 
/*     */   public void write(Object o)
/*     */   {
/* 175 */     if (isExcluded()) {
/* 176 */       return;
/*     */     }
/* 178 */     if (this.m_nested <= 0) {
/* 179 */       this.buffer.append(indentString());
/*     */     }
/* 181 */     this.buffer.append(String.valueOf(o));
/* 182 */     newLine();
/*     */   }
/*     */ 
/*     */   public void write(boolean b)
/*     */   {
/* 190 */     if (isExcluded()) {
/* 191 */       return;
/*     */     }
/* 193 */     if (this.m_nested <= 0) {
/* 194 */       this.buffer.append(indentString());
/*     */     }
/* 196 */     this.buffer.append(b);
/* 197 */     newLine();
/*     */   }
/*     */ 
/*     */   public void write(double d)
/*     */   {
/* 205 */     if (isExcluded()) {
/* 206 */       return;
/*     */     }
/* 208 */     if (this.m_nested <= 0) {
/* 209 */       this.buffer.append(indentString());
/*     */     }
/* 211 */     this.buffer.append(d);
/* 212 */     newLine();
/*     */   }
/*     */ 
/*     */   public void write(float f)
/*     */   {
/* 220 */     if (isExcluded()) {
/* 221 */       return;
/*     */     }
/* 223 */     if (this.m_nested <= 0) {
/* 224 */       this.buffer.append(indentString());
/*     */     }
/* 226 */     this.buffer.append(f);
/* 227 */     newLine();
/*     */   }
/*     */ 
/*     */   public void write(int i)
/*     */   {
/* 235 */     if (isExcluded()) {
/* 236 */       return;
/*     */     }
/* 238 */     if (this.m_nested <= 0) {
/* 239 */       this.buffer.append(indentString());
/*     */     }
/* 241 */     this.buffer.append(i);
/* 242 */     newLine();
/*     */   }
/*     */ 
/*     */   public void write(long l)
/*     */   {
/* 250 */     if (isExcluded()) {
/* 251 */       return;
/*     */     }
/* 253 */     if (this.m_nested <= 0) {
/* 254 */       this.buffer.append(indentString());
/*     */     }
/* 256 */     this.buffer.append(l);
/* 257 */     newLine();
/*     */   }
/*     */ 
/*     */   public void write(short s)
/*     */   {
/* 265 */     if (isExcluded()) {
/* 266 */       return;
/*     */     }
/* 268 */     if (this.m_nested <= 0) {
/* 269 */       this.buffer.append(indentString());
/*     */     }
/* 271 */     this.buffer.append(s);
/* 272 */     newLine();
/*     */   }
/*     */ 
/*     */   public void write(byte b)
/*     */   {
/* 280 */     if (isExcluded()) {
/* 281 */       return;
/*     */     }
/* 283 */     if (this.m_nested <= 0) {
/* 284 */       this.buffer.append(indentString());
/*     */     }
/* 286 */     this.buffer.append(b);
/* 287 */     newLine();
/*     */   }
/*     */ 
/*     */   public void writeNull()
/*     */   {
/* 295 */     if (isExcluded()) {
/* 296 */       return;
/*     */     }
/* 298 */     if (this.m_nested <= 0) {
/* 299 */       this.buffer.append(indentString());
/*     */     }
/* 301 */     this.buffer.append("null");
/* 302 */     newLine();
/*     */   }
/*     */ 
/*     */   public void writeRef(int ref)
/*     */   {
/* 310 */     if (isExcluded()) {
/* 311 */       return;
/*     */     }
/* 313 */     if (this.m_nested <= 0) {
/* 314 */       this.buffer.append(indentString());
/*     */     }
/* 316 */     this.buffer.append("(Ref #").append(ref).append(")");
/* 317 */     newLine();
/*     */   }
/*     */ 
/*     */   public void writeString(String s)
/*     */   {
/* 325 */     if (isExcluded()) {
/* 326 */       return;
/*     */     }
/* 328 */     if (this.m_nested <= 0) {
/* 329 */       this.buffer.append(indentString());
/*     */     }
/* 331 */     this.buffer.append("\"").append(s).append("\"");
/*     */ 
/* 333 */     newLine();
/*     */   }
/*     */ 
/*     */   public void writeStringRef(int ref)
/*     */   {
/* 341 */     if (isExcluded()) {
/* 342 */       return;
/*     */     }
/* 344 */     this.buffer.append(" (String Ref #").append(ref).append(")");
/*     */   }
/*     */ 
/*     */   public void writeTraitsInfoRef(int ref)
/*     */   {
/* 352 */     if (isExcluded()) {
/* 353 */       return;
/*     */     }
/* 355 */     this.buffer.append(" (Traits Ref #").append(ref).append(")");
/*     */   }
/*     */ 
/*     */   public void writeUndefined()
/*     */   {
/* 363 */     if (isExcluded()) {
/* 364 */       return;
/*     */     }
/* 366 */     if (this.m_nested <= 0) {
/* 367 */       this.buffer.append(indentString());
/*     */     }
/* 369 */     this.buffer.append("undefined");
/* 370 */     newLine();
/*     */   }
/*     */ 
/*     */   public void startAMFArray(int ref)
/*     */   {
/* 380 */     if (isExcluded()) {
/* 381 */       return;
/*     */     }
/* 383 */     if (this.m_nested <= 0) {
/* 384 */       this.buffer.append(indentString());
/*     */     }
/* 386 */     this.buffer.append("(Array #").append(ref).append(")").append(newLine);
/* 387 */     this.m_indent += 1;
/* 388 */     this.m_nested += 1;
/*     */   }
/*     */ 
/*     */   public void startECMAArray(int ref)
/*     */   {
/* 398 */     if (isExcluded()) {
/* 399 */       return;
/*     */     }
/* 401 */     if (this.m_nested <= 0) {
/* 402 */       this.buffer.append(indentString());
/*     */     }
/* 404 */     this.buffer.append("(ECMA Array #").append(ref).append(")").append(newLine);
/* 405 */     this.m_indent += 1;
/* 406 */     this.m_nested += 1;
/*     */   }
/*     */ 
/*     */   public void startByteArray(int ref, int length)
/*     */   {
/* 417 */     if (isExcluded()) {
/* 418 */       return;
/*     */     }
/* 420 */     if (this.m_nested <= 0) {
/* 421 */       this.buffer.append(indentString());
/*     */     }
/* 423 */     this.buffer.append("(Byte Array #").append(ref).append(", Length " + length + ")").append(newLine);
/*     */   }
/*     */ 
/*     */   public void endAMFArray()
/*     */   {
/* 431 */     this.m_indent -= 1;
/* 432 */     this.m_nested -= 1;
/*     */   }
/*     */ 
/*     */   public void startExternalizableObject(String type, int ref)
/*     */   {
/* 443 */     if (isExcluded()) {
/* 444 */       return;
/*     */     }
/* 446 */     if (this.m_nested <= 0) {
/* 447 */       this.buffer.append(indentString());
/*     */     }
/* 449 */     this.buffer.append("(Externalizable Object #").append(ref).append(" '").append(type).append("')").append(newLine);
/*     */ 
/* 451 */     this.m_indent += 1;
/* 452 */     this.m_nested += 1;
/*     */ 
/* 454 */     this.buffer.append(indentString());
/*     */   }
/*     */ 
/*     */   public void startAMFObject(String type, int ref)
/*     */   {
/* 465 */     if (isExcluded()) {
/* 466 */       return;
/*     */     }
/* 468 */     if (this.m_nested <= 0) {
/* 469 */       this.buffer.append(indentString());
/*     */     }
/* 471 */     if ((type != null) && (type.length() > 0))
/* 472 */       this.buffer.append("(Typed Object #").append(ref).append(" '").append(type).append("')").append(newLine);
/*     */     else {
/* 474 */       this.buffer.append("(Object #").append(ref).append(")").append(newLine);
/*     */     }
/* 476 */     this.m_indent += 1;
/* 477 */     this.m_nested += 1;
/*     */   }
/*     */ 
/*     */   public void endAMFObject()
/*     */   {
/* 485 */     this.m_indent -= 1;
/* 486 */     this.m_nested -= 1;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.AmfTrace
 * JD-Core Version:    0.6.0
 */
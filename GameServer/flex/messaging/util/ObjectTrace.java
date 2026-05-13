/*     */ package flex.messaging.util;
/*     */ 
/*     */ import flex.messaging.log.Log;
/*     */ 
/*     */ public class ObjectTrace
/*     */ {
/*     */   public boolean nextElementExclude;
/*     */   protected StringBuffer buffer;
/*     */   protected int m_indent;
/*     */   protected int m_nested;
/* 172 */   public static String newLine = StringUtils.NEWLINE;
/*     */ 
/*     */   public ObjectTrace()
/*     */   {
/*  40 */     this.buffer = new StringBuffer(4096);
/*     */   }
/*     */ 
/*     */   public ObjectTrace(int bufferSize)
/*     */   {
/*  45 */     this.buffer = new StringBuffer(bufferSize);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  50 */     return this.buffer.toString();
/*     */   }
/*     */ 
/*     */   public void write(Object o)
/*     */   {
/*  55 */     if (this.m_nested <= 0) {
/*  56 */       this.buffer.append(indentString());
/*     */     }
/*  58 */     this.buffer.append(String.valueOf(o));
/*     */   }
/*     */ 
/*     */   public void writeNull()
/*     */   {
/*  63 */     if (this.m_nested <= 0) {
/*  64 */       this.buffer.append(indentString());
/*     */     }
/*  66 */     this.buffer.append("null");
/*     */   }
/*     */ 
/*     */   public void writeRef(int ref)
/*     */   {
/*  71 */     if (this.m_nested <= 0) {
/*  72 */       this.buffer.append(indentString());
/*     */     }
/*  74 */     this.buffer.append("(Ref #").append(ref).append(")");
/*     */   }
/*     */ 
/*     */   public void writeString(String s)
/*     */   {
/*  79 */     if (this.m_nested <= 0) {
/*  80 */       this.buffer.append(indentString());
/*     */     }
/*  82 */     this.buffer.append("\"").append(s).append("\"");
/*     */   }
/*     */ 
/*     */   public void startArray(String header)
/*     */   {
/*  87 */     if ((header != null) && (header.length() > 0))
/*     */     {
/*  89 */       if (this.m_nested <= 0) {
/*  90 */         this.buffer.append(indentString());
/*     */       }
/*  92 */       this.buffer.append(header).append(newLine);
/*     */     }
/*     */ 
/*  95 */     this.m_indent += 1;
/*  96 */     this.m_nested += 1;
/*     */   }
/*     */ 
/*     */   public void arrayElement(int index)
/*     */   {
/* 101 */     this.buffer.append(indentString()).append("[").append(index).append("] = ");
/*     */   }
/*     */ 
/*     */   public void endArray()
/*     */   {
/* 106 */     this.m_indent -= 1;
/* 107 */     this.m_nested -= 1;
/*     */   }
/*     */ 
/*     */   public void startObject(String header)
/*     */   {
/* 112 */     if ((header != null) && (header.length() > 0))
/*     */     {
/* 114 */       if (this.m_nested <= 0) {
/* 115 */         this.buffer.append(indentString());
/*     */       }
/* 117 */       this.buffer.append(header).append(newLine);
/*     */     }
/*     */ 
/* 120 */     this.m_indent += 1;
/* 121 */     this.m_nested += 1;
/*     */   }
/*     */ 
/*     */   public void namedElement(String name)
/*     */   {
/* 126 */     if (Log.isExcludedProperty(name))
/*     */     {
/* 128 */       this.nextElementExclude = true;
/*     */     }
/*     */ 
/* 131 */     this.buffer.append(indentString()).append(name).append(" = ");
/*     */   }
/*     */ 
/*     */   public void endObject()
/*     */   {
/* 136 */     this.m_indent -= 1;
/* 137 */     this.m_nested -= 1;
/*     */   }
/*     */ 
/*     */   public void newLine()
/*     */   {
/* 142 */     boolean alreadyPadded = false;
/* 143 */     int length = this.buffer.length();
/*     */ 
/* 145 */     if (length > 3)
/*     */     {
/* 147 */       String tail = this.buffer.substring(length - 3, length - 1);
/* 148 */       alreadyPadded = tail.equals(newLine);
/*     */     }
/*     */ 
/* 151 */     if (!alreadyPadded)
/* 152 */       this.buffer.append(newLine);
/*     */   }
/*     */ 
/*     */   protected String indentString()
/*     */   {
/* 161 */     StringBuffer sb = new StringBuffer();
/* 162 */     for (int i = 0; i < this.m_indent; i++)
/*     */     {
/* 164 */       sb.append("  ");
/*     */     }
/* 166 */     return sb.toString();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.util.ObjectTrace
 * JD-Core Version:    0.6.0
 */
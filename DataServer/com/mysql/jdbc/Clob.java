/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
/*     */ import java.io.Writer;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class Clob
/*     */   implements java.sql.Clob, OutputStreamWatcher, WriterWatcher
/*     */ {
/*     */   private String charData;
/*     */ 
/*     */   Clob(String charDataInit)
/*     */   {
/*  46 */     this.charData = charDataInit;
/*     */   }
/*     */ 
/*     */   public InputStream getAsciiStream()
/*     */     throws SQLException
/*     */   {
/*  53 */     if (this.charData != null) {
/*  54 */       return new ByteArrayInputStream(this.charData.getBytes());
/*     */     }
/*     */ 
/*  57 */     return null;
/*     */   }
/*     */ 
/*     */   public Reader getCharacterStream()
/*     */     throws SQLException
/*     */   {
/*  64 */     if (this.charData != null) {
/*  65 */       return new StringReader(this.charData);
/*     */     }
/*     */ 
/*  68 */     return null;
/*     */   }
/*     */ 
/*     */   public String getSubString(long startPos, int length)
/*     */     throws SQLException
/*     */   {
/*  75 */     if (startPos < 1L) {
/*  76 */       throw new SQLException(Messages.getString("Clob.6"), "S1009");
/*     */     }
/*     */ 
/*  80 */     if (this.charData != null) {
/*  81 */       if (startPos - 1L + length > this.charData.length()) {
/*  82 */         throw new SQLException(Messages.getString("Clob.7"), "S1009");
/*     */       }
/*     */ 
/*  86 */       return this.charData.substring((int)(startPos - 1L), length);
/*     */     }
/*     */ 
/*  89 */     return null;
/*     */   }
/*     */ 
/*     */   public long length()
/*     */     throws SQLException
/*     */   {
/*  96 */     if (this.charData != null) {
/*  97 */       return this.charData.length();
/*     */     }
/*     */ 
/* 100 */     return 0L;
/*     */   }
/*     */ 
/*     */   public long position(java.sql.Clob arg0, long arg1)
/*     */     throws SQLException
/*     */   {
/* 107 */     return position(arg0.getSubString(0L, (int)arg0.length()), arg1);
/*     */   }
/*     */ 
/*     */   public long position(String stringToFind, long startPos)
/*     */     throws SQLException
/*     */   {
/* 115 */     if (startPos < 1L) {
/* 116 */       throw new SQLException(Messages.getString("Clob.8") + startPos + Messages.getString("Clob.9"), "S1009");
/*     */     }
/*     */ 
/* 121 */     if (this.charData != null) {
/* 122 */       if (startPos - 1L > this.charData.length()) {
/* 123 */         throw new SQLException(Messages.getString("Clob.10"), "S1009");
/*     */       }
/*     */ 
/* 127 */       int pos = this.charData.indexOf(stringToFind, (int)(startPos - 1L));
/*     */ 
/* 129 */       return pos + 1;
/*     */     }
/*     */ 
/* 132 */     return -1L;
/*     */   }
/*     */ 
/*     */   public OutputStream setAsciiStream(long indexToWriteAt)
/*     */     throws SQLException
/*     */   {
/* 139 */     if (indexToWriteAt < 1L) {
/* 140 */       throw new SQLException(Messages.getString("Clob.0"), "S1009");
/*     */     }
/*     */ 
/* 144 */     WatchableOutputStream bytesOut = new WatchableOutputStream();
/* 145 */     bytesOut.setWatcher(this);
/*     */ 
/* 147 */     if (indexToWriteAt > 0L) {
/* 148 */       bytesOut.write(this.charData.getBytes(), 0, (int)(indexToWriteAt - 1L));
/*     */     }
/*     */ 
/* 152 */     return bytesOut;
/*     */   }
/*     */ 
/*     */   public Writer setCharacterStream(long indexToWriteAt)
/*     */     throws SQLException
/*     */   {
/* 159 */     if (indexToWriteAt < 1L) {
/* 160 */       throw new SQLException(Messages.getString("Clob.1"), "S1009");
/*     */     }
/*     */ 
/* 164 */     WatchableWriter writer = new WatchableWriter();
/* 165 */     writer.setWatcher(this);
/*     */ 
/* 170 */     if (indexToWriteAt > 1L) {
/* 171 */       writer.write(this.charData, 0, (int)(indexToWriteAt - 1L));
/*     */     }
/*     */ 
/* 174 */     return writer;
/*     */   }
/*     */ 
/*     */   public int setString(long pos, String str)
/*     */     throws SQLException
/*     */   {
/* 181 */     if (pos < 1L) {
/* 182 */       throw new SQLException(Messages.getString("Clob.2"), "S1009");
/*     */     }
/*     */ 
/* 186 */     if (str == null) {
/* 187 */       throw new SQLException(Messages.getString("Clob.3"), "S1009");
/*     */     }
/*     */ 
/* 191 */     StringBuffer charBuf = new StringBuffer(this.charData);
/*     */ 
/* 193 */     pos -= 1L;
/*     */ 
/* 195 */     int strLength = str.length();
/*     */ 
/* 197 */     charBuf.replace((int)pos, (int)(pos + strLength), str);
/*     */ 
/* 199 */     this.charData = charBuf.toString();
/*     */ 
/* 201 */     return strLength;
/*     */   }
/*     */ 
/*     */   public int setString(long pos, String str, int offset, int len)
/*     */     throws SQLException
/*     */   {
/* 209 */     if (pos < 1L) {
/* 210 */       throw new SQLException(Messages.getString("Clob.4"), "S1009");
/*     */     }
/*     */ 
/* 214 */     if (str == null) {
/* 215 */       throw new SQLException(Messages.getString("Clob.5"), "S1009");
/*     */     }
/*     */ 
/* 219 */     StringBuffer charBuf = new StringBuffer(this.charData);
/*     */ 
/* 221 */     pos -= 1L;
/*     */ 
/* 223 */     String replaceString = str.substring(offset, len);
/*     */ 
/* 225 */     charBuf.replace((int)pos, (int)(pos + replaceString.length()), replaceString);
/*     */ 
/* 228 */     this.charData = charBuf.toString();
/*     */ 
/* 230 */     return len;
/*     */   }
/*     */ 
/*     */   public void streamClosed(WatchableOutputStream out)
/*     */   {
/* 237 */     int streamSize = out.size();
/*     */ 
/* 239 */     if (streamSize < this.charData.length()) {
/*     */       try {
/* 241 */         out.write(StringUtils.getBytes(this.charData, null, null, false), streamSize, this.charData.length() - streamSize);
/*     */       }
/*     */       catch (SQLException ex)
/*     */       {
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 249 */     this.charData = StringUtils.toAsciiString(out.toByteArray());
/*     */   }
/*     */ 
/*     */   public void truncate(long length)
/*     */     throws SQLException
/*     */   {
/* 256 */     if (length > this.charData.length()) {
/* 257 */       throw new SQLException(Messages.getString("Clob.11") + this.charData.length() + Messages.getString("Clob.12") + length + Messages.getString("Clob.13"));
/*     */     }
/*     */ 
/* 263 */     this.charData = this.charData.substring(0, (int)length);
/*     */   }
/*     */ 
/*     */   public void writerClosed(char[] charDataBeingWritten)
/*     */   {
/* 270 */     this.charData = new String(charDataBeingWritten);
/*     */   }
/*     */ 
/*     */   public void writerClosed(WatchableWriter out)
/*     */   {
/* 277 */     int dataLength = out.size();
/*     */ 
/* 279 */     if (dataLength < this.charData.length()) {
/* 280 */       out.write(this.charData, dataLength, this.charData.length() - dataLength);
/*     */     }
/*     */ 
/* 284 */     this.charData = out.toString();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.mysql.jdbc.Clob
 * JD-Core Version:    0.6.0
 */
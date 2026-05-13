/*     */ package com.pst.core.protocol;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ 
/*     */ public class Protocol
/*     */ {
/*  14 */   public static String PROTOCAL_ENCODEDEFAULT = "utf-8";
/*     */ 
/*     */   protected int ToUnsignByteInt(byte _thebyte)
/*     */   {
/*  24 */     if (_thebyte < 0) {
/*  25 */       return 256 + _thebyte;
/*     */     }
/*  27 */     return _thebyte;
/*     */   }
/*     */ 
/*     */   public byte UnsignByteToByte(int _num)
/*     */   {
/*  38 */     byte targetbyte = 0;
/*  39 */     if ((_num < 0) || (_num > 255)) {
/*  40 */       return targetbyte;
/*     */     }
/*     */ 
/*  43 */     if (_num > 127)
/*  44 */       targetbyte = (byte)(_num - 256);
/*     */     else {
/*  46 */       targetbyte = (byte)_num;
/*     */     }
/*     */ 
/*  49 */     return targetbyte;
/*     */   }
/*     */ 
/*     */   public String debugReciveByte(byte[] getbytes, boolean UnsignMode)
/*     */   {
/*  60 */     if (getbytes.length == 0) {
/*  61 */       return "";
/*     */     }
/*  63 */     int colnum = 30; int i = 0; int j = 0; int posoffset = 0;
/*  64 */     int rownum = getbytes.length / colnum;
/*  65 */     if (getbytes.length % colnum > 0) {
/*  66 */       rownum++;
/*     */     }
/*     */ 
/*  70 */     StringBuffer debugstr = new StringBuffer();
/*  71 */     debugstr.append("==============debugstart=================\r\n");
/*  72 */     for (i = 0; i < rownum; i++)
/*     */     {
/*  74 */       debugstr.append("row:").append(i).append(":");
/*  75 */       for (j = 0; j < colnum; j++) {
/*  76 */         posoffset = i * colnum + j;
/*  77 */         if (posoffset >= getbytes.length) {
/*     */           break;
/*     */         }
/*  80 */         if (UnsignMode)
/*  81 */           debugstr.append("[").append(
/*  82 */             String.valueOf(i * colnum + j)).append("]")
/*  83 */             .append(
/*  84 */             String.valueOf(
/*  85 */             ToUnsignByteInt(getbytes[
/*  86 */             (i * 
/*  86 */             colnum + j)])))
/*  87 */             .append(",");
/*     */         else {
/*  89 */           debugstr.append("[").append(
/*  90 */             String.valueOf(i * colnum + j)).append("]")
/*  91 */             .append(String.valueOf(getbytes[(i * colnum + j)]))
/*  92 */             .append(",");
/*     */         }
/*     */       }
/*     */ 
/*  96 */       debugstr.append("\r\n");
/*     */     }
/*     */ 
/* 100 */     debugstr.append("===============debugend================\r\n");
/*     */ 
/* 102 */     return debugstr.toString();
/*     */   }
/*     */ 
/*     */   public String ByteToHexLen(byte[] _bytes)
/*     */   {
/* 112 */     StringBuffer HexNum = new StringBuffer();
/* 113 */     int i = 0; int unsignbyte = 0;
/* 114 */     for (i = 0; i < _bytes.length; i++) {
/* 115 */       unsignbyte = 0;
/* 116 */       unsignbyte = ToUnsignByteInt(_bytes[i]);
/* 117 */       if (unsignbyte < 16) {
/* 118 */         HexNum.append("0");
/*     */       }
/* 120 */       HexNum.append(Integer.toHexString(unsignbyte));
/*     */     }
/*     */ 
/* 123 */     return HexNum.toString();
/*     */   }
/*     */ 
/*     */   public byte[] ToUnsignBLenByte(int _len)
/*     */   {
/* 133 */     byte[] returnbytes = (byte[])null;
/* 134 */     int i = 0;
/* 135 */     int bytenum = 0;
/* 136 */     StringBuffer HexLen = new StringBuffer();
/* 137 */     HexLen.append(Integer.toHexString(_len));
/*     */ 
/* 139 */     if (HexLen.length() % 2 > 0) {
/* 140 */       HexLen.insert(0, "0");
/*     */     }
/*     */ 
/* 143 */     if (HexLen.length() == 2) {
/* 144 */       HexLen.insert(0, "00");
/*     */     }
/*     */ 
/* 147 */     returnbytes = new byte[HexLen.length() / 2];
/* 148 */     for (i = 0; i < HexLen.length() / 2; i++) {
/* 149 */       bytenum = Integer.parseInt(HexLen.substring(i * 2, i * 2 + 2), 16);
/* 150 */       returnbytes[i] = UnsignByteToByte(bytenum);
/*     */     }
/*     */ 
/* 153 */     return returnbytes;
/*     */   }
/*     */ 
/*     */   public byte[] FillBytes(byte[] _cmdbytes, int _targetlen)
/*     */   {
/* 164 */     byte[] returnbytes = new byte[_targetlen];
/*     */ 
/* 166 */     if (_cmdbytes.length > _targetlen)
/*     */     {
/* 168 */       System.arraycopy(_cmdbytes, 0, returnbytes, 0, _targetlen);
/*     */     }
/* 170 */     else System.arraycopy(_cmdbytes, 0, returnbytes, 
/* 171 */         _targetlen - _cmdbytes.length, _cmdbytes.length);
/*     */ 
/* 174 */     return returnbytes;
/*     */   }
/*     */ 
/*     */   public byte[] StringIconv(String _msg)
/*     */   {
/* 179 */     return StringIconv(_msg, PROTOCAL_ENCODEDEFAULT);
/*     */   }
/*     */ 
/*     */   public byte[] StringIconv(String _msg, String _chatset)
/*     */   {
/* 189 */     ByteArrayOutputStream sbaos = new ByteArrayOutputStream();
/* 190 */     OutputStreamWriter sosw = null;
/* 191 */     byte[] MSGBYTE = new byte[0];
/*     */     try {
/* 193 */       sosw = new OutputStreamWriter(sbaos, _chatset);
/* 194 */       sosw.write(_msg.toString());
/* 195 */       sosw.flush();
/*     */ 
/* 197 */       MSGBYTE = sbaos.toByteArray();
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/*     */     try {
/* 203 */       sosw.close();
/* 204 */       sbaos.close();
/*     */     }
/*     */     catch (Exception localException1) {
/*     */     }
/* 208 */     return MSGBYTE;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.core.protocol.Protocol
 * JD-Core Version:    0.6.0
 */
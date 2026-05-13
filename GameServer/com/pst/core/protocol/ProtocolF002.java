/*     */ package com.pst.core.protocol;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.Vector;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ public class ProtocolF002 extends Protocol
/*     */ {
/*  13 */   private byte[] PROTOCALHEAD = null;
/*  14 */   private int PROTOCAL_VERIFY_BYTELEN = 1;
/*  15 */   private int PROTOCAL_PACKLEN_BYTELEN = 2;
/*  16 */   private Logger loger = Logger.getLogger("ProtocalF002");
/*     */ 
/*     */   public ProtocolF002() {
/*  19 */     this.PROTOCALHEAD = new byte[] { -16, 2 };
/*     */   }
/*     */ 
/*     */   public Vector _parseBytes(byte[] _getbytes) {
/*  23 */     Vector CmdBytes = new Vector();
/*  24 */     HashMap singleCmd = null;
/*     */ 
/*  29 */     byte[] headtest = new byte[this.PROTOCALHEAD.length];
/*  30 */     byte[] packlenbyte = new byte[this.PROTOCAL_PACKLEN_BYTELEN];
/*  31 */     byte[] packbytes = (byte[])null;
/*  32 */     byte[] databytes = (byte[])null;
/*  33 */     int i = 0; int j = 0; int k = 0;
/*  34 */     String verifynumstr = null;
/*  35 */     byte packlenvertifybyte = 0;
/*  36 */     int packlenverifyint = 0;
/*  37 */     int packlen = 0;
/*     */ 
/*  39 */     databytes = _getbytes;
/*     */ 
/*  41 */     i = 0;
/*  42 */     while (i < databytes.length - this.PROTOCALHEAD.length - 
/*  42 */       this.PROTOCAL_VERIFY_BYTELEN - this.PROTOCAL_PACKLEN_BYTELEN) {
/*  43 */       System.arraycopy(databytes, i, headtest, 0, 
/*  44 */         this.PROTOCALHEAD.length);
/*  45 */       if (!Arrays.equals(this.PROTOCALHEAD, headtest))
/*     */       {
/*  47 */         i++;
/*     */       }
/*     */       else
/*     */       {
/*  52 */         packlenvertifybyte = 0;
/*  53 */         packlenvertifybyte = databytes[(i + this.PROTOCALHEAD.length)];
/*     */ 
/*  55 */         System.arraycopy(databytes, i + this.PROTOCALHEAD.length + 
/*  56 */           this.PROTOCAL_VERIFY_BYTELEN, packlenbyte, 0, 
/*  57 */           packlenbyte.length);
/*     */ 
/*  59 */         packlenverifyint = getProtocolPackLenVertify(packlenbyte);
/*     */ 
/*  61 */         if (packlenverifyint != packlenvertifybyte)
/*     */         {
/*  63 */           i++;
/*  64 */           this.loger
/*  65 */             .debug("BYTE VERIFY NOT THE SAME:(byte:" + 
/*  66 */             packlenvertifybyte + ",int:" + 
/*  67 */             packlenverifyint + ")");
/*  68 */           this.loger.debug("DECEIVE BYTES:\r\n" + 
/*  69 */             debugReciveByte(_getbytes, true) + 
/*  70 */             "DECEIVE BYTES END\r\n");
/*     */         }
/*     */         else
/*     */         {
/*  74 */           packlen = Integer.parseInt(ByteToHexLen(packlenbyte), 16);
/*  75 */           packbytes = (byte[])null;
/*     */ 
/*  77 */           if (packlen == 0)
/*     */           {
/*  79 */             this.loger.debug("PackageLen(0):" + 
/*  80 */               debugReciveByte(databytes, true));
/*  81 */             i++;
/*     */           }
/*     */           else
/*     */           {
/*  85 */             singleCmd = new HashMap();
/*     */ 
/*  88 */             if (databytes.length - i < packlen + this.PROTOCALHEAD.length + 
/*  88 */               packlenbyte.length + this.PROTOCAL_VERIFY_BYTELEN)
/*     */             {
/*  90 */               this.loger.debug("{ProtocalF001->ParseBytes_cachedata:");
/*  91 */               packbytes = new byte[databytes.length - i];
/*  92 */               System.arraycopy(databytes, i, packbytes, 0, databytes.length - 
/*  93 */                 i);
/*  94 */               singleCmd.put("DataComplete", "0");
/*  95 */               singleCmd.put("CmdData", packbytes);
/*  96 */               CmdBytes.add(singleCmd);
/*  97 */               return CmdBytes;
/*     */             }
/*     */ 
/* 100 */             packbytes = new byte[packlen];
/* 101 */             System.arraycopy(databytes, i + this.PROTOCALHEAD.length + 
/* 102 */               packlenbyte.length + this.PROTOCAL_VERIFY_BYTELEN, 
/* 103 */               packbytes, 0, packlen);
/* 104 */             singleCmd.put("DataComplete", "1");
/* 105 */             singleCmd.put("CmdData", packbytes);
/* 106 */             CmdBytes.add(singleCmd);
/*     */ 
/* 109 */             i = i + (this.PROTOCALHEAD.length + packlenbyte.length + packlen + 
/* 109 */               this.PROTOCAL_VERIFY_BYTELEN);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 113 */     if (_getbytes.length - i > 0) {
/* 114 */       byte[] _remainBytes = new byte[_getbytes.length - i];
/* 115 */       k = 0;
/* 116 */       for (j = i; j < _getbytes.length; j++) {
/* 117 */         _remainBytes[k] = _getbytes[j];
/* 118 */         k++;
/*     */       }
/*     */     }
/*     */ 
/* 122 */     return CmdBytes;
/*     */   }
/*     */ 
/*     */   public Vector parseBytes(byte[] _getbytes)
/*     */   {
/* 131 */     Vector CmdBytes = new Vector();
/* 132 */     byte[] headtest = new byte[this.PROTOCALHEAD.length];
/* 133 */     byte[] packlenbyte = new byte[this.PROTOCAL_PACKLEN_BYTELEN];
/* 134 */     byte[] packbytes = (byte[])null;
/* 135 */     byte[] databytes = (byte[])null;
/* 136 */     int i = 0; int j = 0; int k = 0;
/* 137 */     String verifynumstr = null;
/* 138 */     byte packlenvertifybyte = 0;
/* 139 */     int packlenverifyint = 0;
/* 140 */     int packlen = 0;
/*     */ 
/* 142 */     databytes = _getbytes;
/*     */ 
/* 144 */     i = 0;
/* 145 */     while (i < databytes.length - this.PROTOCALHEAD.length - 
/* 145 */       this.PROTOCAL_VERIFY_BYTELEN - this.PROTOCAL_PACKLEN_BYTELEN) {
/* 146 */       System.arraycopy(databytes, i, headtest, 0, 
/* 147 */         this.PROTOCALHEAD.length);
/* 148 */       if (!Arrays.equals(this.PROTOCALHEAD, headtest))
/*     */       {
/* 150 */         i++;
/*     */       }
/*     */       else
/*     */       {
/* 155 */         packlenvertifybyte = 0;
/* 156 */         packlenvertifybyte = databytes[(i + this.PROTOCALHEAD.length)];
/*     */ 
/* 158 */         System.arraycopy(databytes, i + this.PROTOCALHEAD.length + 
/* 159 */           this.PROTOCAL_VERIFY_BYTELEN, packlenbyte, 0, 
/* 160 */           packlenbyte.length);
/*     */ 
/* 162 */         packlenverifyint = getProtocolPackLenVertify(packlenbyte);
/*     */ 
/* 164 */         if (packlenverifyint != packlenvertifybyte)
/*     */         {
/* 166 */           i++;
/* 167 */           this.loger
/* 168 */             .debug("BYTE VERIFY NOT THE SAME:(byte:" + 
/* 169 */             packlenvertifybyte + ",int:" + 
/* 170 */             packlenverifyint + ")");
/* 171 */           this.loger.debug("DECEIVE BYTES:\r\n" + 
/* 172 */             debugReciveByte(_getbytes, true) + 
/* 173 */             "DECEIVE BYTES END\r\n");
/*     */         }
/*     */         else
/*     */         {
/* 177 */           packlen = Integer.parseInt(ByteToHexLen(packlenbyte), 16);
/* 178 */           packbytes = (byte[])null;
/*     */ 
/* 180 */           if (packlen == 0)
/*     */           {
/* 182 */             this.loger.debug("PackageLen(0):" + 
/* 183 */               debugReciveByte(databytes, true));
/* 184 */             i++;
/*     */           }
/*     */           else
/*     */           {
/* 189 */             if (databytes.length - i < packlen + this.PROTOCALHEAD.length + 
/* 189 */               packlenbyte.length + this.PROTOCAL_VERIFY_BYTELEN)
/*     */             {
/* 191 */               this.loger.debug("{ProtocalF001->ParseBytes_cachedata:");
/* 192 */               packbytes = new byte[databytes.length - i];
/* 193 */               System.arraycopy(databytes, i, packbytes, 0, databytes.length - 
/* 194 */                 i);
/* 195 */               return CmdBytes;
/*     */             }
/* 197 */             packbytes = new byte[packlen];
/* 198 */             System.arraycopy(databytes, i + this.PROTOCALHEAD.length + 
/* 199 */               packlenbyte.length + this.PROTOCAL_VERIFY_BYTELEN, 
/* 200 */               packbytes, 0, packlen);
/* 201 */             CmdBytes.add(packbytes);
/*     */ 
/* 204 */             i = i + (this.PROTOCALHEAD.length + packlenbyte.length + packlen + 
/* 204 */               this.PROTOCAL_VERIFY_BYTELEN);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 208 */     return CmdBytes;
/*     */   }
/*     */ 
/*     */   public int getProtocolPackLenVertify(byte[] _packlen)
/*     */   {
/* 218 */     int cmdlenvertify = 0;
/* 219 */     int i = 0; int j = 0;
/* 220 */     String verifystr = null;
/* 221 */     for (i = 0; i < _packlen.length; i++) {
/* 222 */       verifystr = String.valueOf(ToUnsignByteInt(_packlen[i]));
/* 223 */       for (j = 0; j < verifystr.length(); j++)
/*     */       {
/* 225 */         cmdlenvertify += Integer.parseInt(verifystr.substring(j, j + 1));
/*     */       }
/*     */     }
/*     */ 
/* 229 */     return cmdlenvertify;
/*     */   }
/*     */ 
/*     */   public byte[] packProtocolBytes(byte[] cmdBytes, byte[] actionBytes)
/*     */   {
/* 242 */     byte[] cmdlenbytes = FillBytes(ToUnsignBLenByte(cmdBytes.length + actionBytes.length), 2);
/*     */ 
/* 245 */     byte[] returnbytes = new byte[cmdBytes.length + this.PROTOCALHEAD.length + this.PROTOCAL_VERIFY_BYTELEN + cmdlenbytes.length + actionBytes.length];
/*     */ 
/* 247 */     int writepos = 0;
/*     */ 
/* 249 */     System.arraycopy(this.PROTOCALHEAD, writepos, returnbytes, 0, this.PROTOCALHEAD.length);
/* 250 */     writepos += this.PROTOCALHEAD.length;
/*     */ 
/* 252 */     returnbytes[this.PROTOCALHEAD.length] = (byte)getProtocolPackLenVertify(cmdlenbytes);
/* 253 */     writepos++;
/*     */ 
/* 255 */     System.arraycopy(cmdlenbytes, 0, returnbytes, writepos, cmdlenbytes.length);
/* 256 */     writepos += cmdlenbytes.length;
/*     */ 
/* 258 */     System.arraycopy(actionBytes, 0, returnbytes, writepos, actionBytes.length);
/* 259 */     writepos += actionBytes.length;
/*     */ 
/* 261 */     if (cmdBytes.length > 0) {
/* 262 */       System.arraycopy(cmdBytes, 0, returnbytes, writepos, cmdBytes.length);
/*     */     }
/*     */ 
/* 265 */     this.loger.debug(debugReciveByte(returnbytes, true));
/*     */ 
/* 267 */     return returnbytes;
/*     */   }
/*     */ 
/*     */   public byte[] packToServerProtocolBytes(byte[] cmdbytes, byte[] actionBytes, int powertoken, int accountid)
/*     */   {
/* 280 */     byte[] byte_powertoken = FillBytes(ToUnsignBLenByte(powertoken), 2);
/* 281 */     byte[] byte_accountid = FillBytes(ToUnsignBLenByte(accountid), 4);
/*     */ 
/* 283 */     byte[] cmdlenbytes = FillBytes(ToUnsignBLenByte(cmdbytes.length + actionBytes.length + byte_powertoken.length + byte_accountid.length), 2);
/*     */ 
/* 287 */     byte[] returnbytes = new byte[this.PROTOCALHEAD.length + this.PROTOCAL_VERIFY_BYTELEN + cmdlenbytes.length + byte_powertoken.length + byte_accountid.length + actionBytes.length + cmdbytes.length];
/* 288 */     int writepos = 0;
/*     */ 
/* 290 */     System.arraycopy(this.PROTOCALHEAD, 0, returnbytes, writepos, this.PROTOCALHEAD.length);
/* 291 */     writepos += this.PROTOCALHEAD.length;
/*     */ 
/* 293 */     returnbytes[writepos] = (byte)getProtocolPackLenVertify(cmdlenbytes);
/* 294 */     writepos++;
/*     */ 
/* 296 */     System.arraycopy(cmdlenbytes, 0, returnbytes, writepos, cmdlenbytes.length);
/* 297 */     writepos += cmdlenbytes.length;
/*     */ 
/* 299 */     System.arraycopy(byte_powertoken, 0, returnbytes, writepos, byte_powertoken.length);
/* 300 */     writepos += byte_powertoken.length;
/*     */ 
/* 303 */     System.arraycopy(byte_accountid, 0, returnbytes, writepos, byte_accountid.length);
/* 304 */     writepos += byte_accountid.length;
/*     */ 
/* 306 */     System.arraycopy(actionBytes, 0, returnbytes, writepos, actionBytes.length);
/* 307 */     writepos += actionBytes.length;
/*     */ 
/* 309 */     if (cmdbytes.length > 0)
/*     */     {
/* 311 */       System.arraycopy(cmdbytes, 0, returnbytes, writepos, cmdbytes.length);
/*     */     }
/*     */ 
/* 316 */     return returnbytes;
/*     */   }
/*     */ 
/*     */   public Information parseCmdBytes(byte[] cmdbody)
/*     */   {
/* 363 */     if (cmdbody.length < 8)
/*     */     {
/* 365 */       this.loger.info("{ParseCmdBytes}Package Len Too Short");
/* 366 */       return null;
/*     */     }
/*     */ 
/* 369 */     byte[] byte_pt = new byte[2];
/*     */ 
/* 371 */     byte[] byte_uid = new byte[4];
/* 372 */     byte[] byte_action = new byte[2];
/*     */ 
/* 374 */     byte[] byte_cmdbody = new byte[cmdbody.length - (byte_pt.length + byte_uid.length + byte_action.length)];
/*     */ 
/* 376 */     int readpos = 0;
/*     */ 
/* 378 */     System.arraycopy(cmdbody, readpos, byte_pt, 0, byte_pt.length);
/* 379 */     readpos += byte_pt.length;
/*     */ 
/* 386 */     System.arraycopy(cmdbody, readpos, byte_uid, 0, byte_uid.length);
/* 387 */     readpos += byte_uid.length;
/*     */ 
/* 389 */     System.arraycopy(cmdbody, readpos, byte_action, 0, byte_action.length);
/* 390 */     readpos += byte_action.length;
/*     */ 
/* 392 */     if (cmdbody.length > readpos) {
/* 393 */       System.arraycopy(cmdbody, readpos, byte_cmdbody, 0, byte_cmdbody.length);
/*     */     }
/*     */ 
/* 396 */     int pt = Integer.parseInt(ByteToHexLen(byte_pt), 16);
/* 397 */     int userId = Integer.parseInt(ByteToHexLen(byte_uid), 16);
/* 398 */     String type = ByteToHexLen(byte_action);
/*     */ 
/* 400 */     return new Information(pt, userId, type, byte_cmdbody);
/*     */   }
/*     */ 
/*     */   public byte[] getLoginFalse(byte[] msgbytes)
/*     */   {
/* 442 */     this.loger.debug("LOGINFALSE:" + new String(msgbytes));
/* 443 */     byte[] returnbyte = (byte[])null;
/* 444 */     byte[] dealbyte = new byte[msgbytes.length + 2];
/* 445 */     dealbyte[0] = 1;
/* 446 */     dealbyte[1] = 0;
/* 447 */     System.arraycopy(msgbytes, 0, dealbyte, 2, msgbytes.length);
/*     */ 
/* 450 */     return returnbyte;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.core.protocol.ProtocolF002
 * JD-Core Version:    0.6.0
 */
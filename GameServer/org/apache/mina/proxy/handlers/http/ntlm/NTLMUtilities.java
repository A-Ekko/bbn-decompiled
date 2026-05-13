/*     */ package org.apache.mina.proxy.handlers.http.ntlm;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.StringTokenizer;
/*     */ import org.apache.mina.proxy.utils.ByteUtilities;
/*     */ 
/*     */ public class NTLMUtilities
/*     */   implements NTLMConstants
/*     */ {
/*     */   public static final byte[] writeSecurityBuffer(short length, int bufferOffset)
/*     */   {
/*  45 */     byte[] b = new byte[8];
/*  46 */     writeSecurityBuffer(length, length, bufferOffset, b, 0);
/*  47 */     return b;
/*     */   }
/*     */ 
/*     */   public static final void writeSecurityBuffer(short length, short allocated, int bufferOffset, byte[] b, int offset)
/*     */   {
/*  64 */     ByteUtilities.writeShort(length, b, offset);
/*  65 */     ByteUtilities.writeShort(allocated, b, offset + 2);
/*  66 */     ByteUtilities.writeInt(bufferOffset, b, offset + 4);
/*     */   }
/*     */ 
/*     */   public static final void writeOSVersion(byte majorVersion, byte minorVersion, short buildNumber, byte[] b, int offset)
/*     */   {
/*  71 */     b[offset] = majorVersion;
/*  72 */     b[(offset + 1)] = minorVersion;
/*  73 */     b[(offset + 2)] = (byte)buildNumber;
/*  74 */     b[(offset + 3)] = (byte)(buildNumber >> 8);
/*  75 */     b[(offset + 4)] = 0;
/*  76 */     b[(offset + 5)] = 0;
/*  77 */     b[(offset + 6)] = 0;
/*  78 */     b[(offset + 7)] = 15;
/*     */   }
/*     */ 
/*     */   public static final byte[] getOsVersion()
/*     */   {
/*  85 */     String os = System.getProperty("os.name");
/*  86 */     if ((os == null) || (!os.toUpperCase().contains("WINDOWS"))) {
/*  87 */       return DEFAULT_OS_VERSION;
/*     */     }
/*  89 */     byte[] osVer = new byte[8];
/*     */     try {
/*  91 */       Process pr = Runtime.getRuntime().exec("cmd /C ver");
/*  92 */       BufferedReader reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
/*     */ 
/*  94 */       pr.waitFor();
/*     */       do
/*     */       {
/*  98 */         line = reader.readLine();
/*  99 */       }while ((line != null) && (line.length() != 0));
/*     */ 
/* 101 */       int pos = line.toLowerCase().indexOf("version");
/*     */ 
/* 103 */       if (pos == -1) {
/* 104 */         throw new NullPointerException();
/*     */       }
/*     */ 
/* 107 */       pos += 8;
/* 108 */       String line = line.substring(pos, line.indexOf(']'));
/* 109 */       StringTokenizer tk = new StringTokenizer(line, ".");
/* 110 */       if (tk.countTokens() != 3) {
/* 111 */         throw new NullPointerException();
/*     */       }
/*     */ 
/* 114 */       writeOSVersion(Byte.parseByte(tk.nextToken()), Byte.parseByte(tk.nextToken()), Short.parseShort(tk.nextToken()), osVer, 0);
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/*     */       try {
/* 119 */         String version = System.getProperty("os.version");
/* 120 */         writeOSVersion(Byte.parseByte(version.substring(0, 1)), Byte.parseByte(version.substring(2, 3)), 0, osVer, 0);
/*     */       }
/*     */       catch (Exception ex2)
/*     */       {
/* 124 */         return DEFAULT_OS_VERSION;
/*     */       }
/*     */     }
/* 127 */     return osVer;
/*     */   }
/*     */ 
/*     */   public static final byte[] createType1Message(String workStation, String domain, Integer customFlags, byte[] osVersion)
/*     */   {
/* 144 */     byte[] msg = null;
/*     */ 
/* 146 */     if ((osVersion != null) && (osVersion.length != 8)) {
/* 147 */       throw new IllegalArgumentException("osVersion parameter should be a 8 byte wide array");
/*     */     }
/*     */ 
/* 151 */     if ((workStation == null) || (domain == null)) {
/* 152 */       throw new NullPointerException("workStation and domain must be non null");
/*     */     }
/*     */ 
/* 156 */     int flags = customFlags != null ? customFlags.intValue() | 0x2000 | 0x1000 : 12291;
/*     */ 
/* 160 */     ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*     */     try
/*     */     {
/* 163 */       baos.write(NTLM_SIGNATURE);
/* 164 */       baos.write(ByteUtilities.writeInt(1));
/* 165 */       baos.write(ByteUtilities.writeInt(flags));
/*     */ 
/* 167 */       byte[] domainData = ByteUtilities.getOEMStringAsByteArray(domain);
/* 168 */       byte[] workStationData = ByteUtilities.getOEMStringAsByteArray(workStation);
/*     */ 
/* 171 */       int pos = osVersion != null ? 40 : 32;
/* 172 */       baos.write(writeSecurityBuffer((short)domainData.length, pos + workStationData.length));
/*     */ 
/* 174 */       baos.write(writeSecurityBuffer((short)workStationData.length, pos));
/*     */ 
/* 178 */       if (osVersion != null) {
/* 179 */         baos.write(osVersion);
/*     */       }
/*     */ 
/* 183 */       baos.write(workStationData);
/* 184 */       baos.write(domainData);
/*     */ 
/* 186 */       msg = baos.toByteArray();
/* 187 */       baos.close();
/*     */     } catch (IOException e) {
/* 189 */       return null;
/*     */     }
/*     */ 
/* 192 */     return msg;
/*     */   }
/*     */ 
/*     */   public static final int writeSecurityBufferAndUpdatePointer(ByteArrayOutputStream baos, short len, int pointer)
/*     */     throws IOException
/*     */   {
/* 208 */     baos.write(writeSecurityBuffer(len, pointer));
/* 209 */     return pointer + len;
/*     */   }
/*     */ 
/*     */   public static final byte[] extractChallengeFromType2Message(byte[] msg) {
/* 213 */     byte[] challenge = new byte[8];
/* 214 */     System.arraycopy(msg, 24, challenge, 0, 8);
/* 215 */     return challenge;
/*     */   }
/*     */ 
/*     */   public static final int extractFlagsFromType2Message(byte[] msg) {
/* 219 */     byte[] flagsBytes = new byte[4];
/*     */ 
/* 221 */     System.arraycopy(msg, 20, flagsBytes, 0, 4);
/* 222 */     ByteUtilities.changeWordEndianess(flagsBytes, 0, 4);
/*     */ 
/* 224 */     return ByteUtilities.makeIntFromByte4(flagsBytes);
/*     */   }
/*     */ 
/*     */   public static final String extractTargetNameFromType2Message(byte[] msg, Integer msgFlags) throws UnsupportedEncodingException
/*     */   {
/* 229 */     byte[] targetName = null;
/*     */ 
/* 232 */     byte[] securityBuffer = new byte[8];
/*     */ 
/* 234 */     System.arraycopy(msg, 12, securityBuffer, 0, 8);
/* 235 */     ByteUtilities.changeWordEndianess(securityBuffer, 0, 8);
/* 236 */     int length = ByteUtilities.makeIntFromByte2(securityBuffer);
/* 237 */     int offset = ByteUtilities.makeIntFromByte4(securityBuffer, 4);
/*     */ 
/* 239 */     targetName = new byte[length];
/* 240 */     System.arraycopy(msg, offset, targetName, 0, length);
/*     */ 
/* 242 */     int flags = msgFlags == null ? extractFlagsFromType2Message(msg) : msgFlags.intValue();
/*     */ 
/* 244 */     if (ByteUtilities.isFlagSet(flags, 1)) {
/* 245 */       return new String(targetName, "UTF-16LE");
/*     */     }
/* 247 */     return new String(targetName, "ASCII");
/*     */   }
/*     */ 
/*     */   public static final byte[] extractTargetInfoFromType2Message(byte[] msg, Integer msgFlags)
/*     */   {
/* 253 */     int flags = msgFlags == null ? extractFlagsFromType2Message(msg) : msgFlags.intValue();
/*     */ 
/* 255 */     byte[] targetInformationBlock = null;
/*     */ 
/* 257 */     if (!ByteUtilities.isFlagSet(flags, 8388608)) {
/* 258 */       return null;
/*     */     }
/* 260 */     int pos = 40;
/*     */ 
/* 263 */     byte[] securityBuffer = new byte[8];
/*     */ 
/* 265 */     System.arraycopy(msg, pos, securityBuffer, 0, 8);
/* 266 */     ByteUtilities.changeWordEndianess(securityBuffer, 0, 8);
/* 267 */     int length = ByteUtilities.makeIntFromByte2(securityBuffer);
/* 268 */     int offset = ByteUtilities.makeIntFromByte4(securityBuffer, 4);
/*     */ 
/* 270 */     targetInformationBlock = new byte[length];
/* 271 */     System.arraycopy(msg, offset, targetInformationBlock, 0, length);
/*     */ 
/* 273 */     return targetInformationBlock;
/*     */   }
/*     */ 
/*     */   public static final void printTargetInformationBlockFromType2Message(byte[] msg, Integer msgFlags, PrintWriter out)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 279 */     int flags = msgFlags == null ? extractFlagsFromType2Message(msg) : msgFlags.intValue();
/*     */ 
/* 282 */     byte[] infoBlock = extractTargetInfoFromType2Message(msg, Integer.valueOf(flags));
/* 283 */     if (infoBlock == null) {
/* 284 */       out.println("No target information block found !");
/*     */     } else {
/* 286 */       int pos = 0;
/* 287 */       while (infoBlock[pos] != 0) {
/* 288 */         out.print("---\nType " + infoBlock[pos] + ": ");
/* 289 */         switch (infoBlock[pos]) {
/*     */         case 1:
/* 291 */           out.println("Server name");
/* 292 */           break;
/*     */         case 2:
/* 294 */           out.println("Domain name");
/* 295 */           break;
/*     */         case 3:
/* 297 */           out.println("Fully qualified DNS hostname");
/* 298 */           break;
/*     */         case 4:
/* 300 */           out.println("DNS domain name");
/* 301 */           break;
/*     */         case 5:
/* 303 */           out.println("Parent DNS domain name");
/*     */         }
/*     */ 
/* 306 */         byte[] len = new byte[2];
/* 307 */         System.arraycopy(infoBlock, pos + 2, len, 0, 2);
/* 308 */         ByteUtilities.changeByteEndianess(len, 0, 2);
/*     */ 
/* 310 */         int length = ByteUtilities.makeIntFromByte2(len, 0);
/* 311 */         out.println("Length: " + length + " bytes");
/* 312 */         out.print("Data: ");
/* 313 */         if (ByteUtilities.isFlagSet(flags, 1)) {
/* 314 */           out.println(new String(infoBlock, pos + 4, length, "UTF-16LE"));
/*     */         }
/*     */         else {
/* 317 */           out.println(new String(infoBlock, pos + 4, length, "ASCII"));
/*     */         }
/*     */ 
/* 321 */         pos += 4 + length;
/* 322 */         out.flush();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final byte[] createType3Message(String user, String password, byte[] challenge, String target, String workstation, Integer serverFlags, byte[] osVersion)
/*     */   {
/* 333 */     byte[] msg = null;
/*     */ 
/* 335 */     if ((challenge == null) || (challenge.length != 8)) {
/* 336 */       throw new IllegalArgumentException("challenge[] should be a 8 byte wide array");
/*     */     }
/*     */ 
/* 340 */     if ((osVersion != null) && (osVersion.length != 8)) {
/* 341 */       throw new IllegalArgumentException("osVersion should be a 8 byte wide array");
/*     */     }
/*     */ 
/* 349 */     int flags = serverFlags != null ? serverFlags.intValue() : 12291;
/*     */ 
/* 351 */     ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*     */     try
/*     */     {
/* 354 */       baos.write(NTLM_SIGNATURE);
/* 355 */       baos.write(ByteUtilities.writeInt(3));
/*     */ 
/* 357 */       byte[] dataLMResponse = NTLMResponses.getLMResponse(password, challenge);
/*     */ 
/* 359 */       byte[] dataNTLMResponse = NTLMResponses.getNTLMResponse(password, challenge);
/*     */ 
/* 362 */       boolean useUnicode = ByteUtilities.isFlagSet(flags, 1);
/*     */ 
/* 364 */       byte[] targetName = ByteUtilities.encodeString(target, useUnicode);
/* 365 */       byte[] userName = ByteUtilities.encodeString(user, useUnicode);
/* 366 */       byte[] workstationName = ByteUtilities.encodeString(workstation, useUnicode);
/*     */ 
/* 369 */       int pos = osVersion != null ? 72 : 64;
/* 370 */       int responsePos = pos + targetName.length + userName.length + workstationName.length;
/*     */ 
/* 372 */       responsePos = writeSecurityBufferAndUpdatePointer(baos, (short)dataLMResponse.length, responsePos);
/*     */ 
/* 374 */       writeSecurityBufferAndUpdatePointer(baos, (short)dataNTLMResponse.length, responsePos);
/*     */ 
/* 376 */       pos = writeSecurityBufferAndUpdatePointer(baos, (short)targetName.length, pos);
/*     */ 
/* 378 */       pos = writeSecurityBufferAndUpdatePointer(baos, (short)userName.length, pos);
/*     */ 
/* 380 */       writeSecurityBufferAndUpdatePointer(baos, (short)workstationName.length, pos);
/*     */ 
/* 394 */       baos.write(new byte[] { 0, 0, 0, 0, -102, 0, 0, 0 });
/* 395 */       baos.write(ByteUtilities.writeInt(flags));
/*     */ 
/* 397 */       if (osVersion != null) {
/* 398 */         baos.write(osVersion);
/*     */       }
/*     */ 
/* 404 */       baos.write(targetName);
/* 405 */       baos.write(userName);
/* 406 */       baos.write(workstationName);
/*     */ 
/* 408 */       baos.write(dataLMResponse);
/* 409 */       baos.write(dataNTLMResponse);
/*     */ 
/* 411 */       msg = baos.toByteArray();
/* 412 */       baos.close();
/*     */     } catch (Exception e) {
/* 414 */       e.printStackTrace();
/* 415 */       return null;
/*     */     }
/*     */ 
/* 418 */     return msg;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.proxy.handlers.http.ntlm.NTLMUtilities
 * JD-Core Version:    0.6.0
 */
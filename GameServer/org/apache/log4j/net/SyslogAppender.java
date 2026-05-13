/*     */ package org.apache.log4j.net;
/*     */ 
/*     */ import java.io.FilterWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.Locale;
/*     */ import org.apache.log4j.AppenderSkeleton;
/*     */ import org.apache.log4j.Layout;
/*     */ import org.apache.log4j.Priority;
/*     */ import org.apache.log4j.helpers.SyslogQuietWriter;
/*     */ import org.apache.log4j.helpers.SyslogWriter;
/*     */ import org.apache.log4j.spi.ErrorHandler;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class SyslogAppender extends AppenderSkeleton
/*     */ {
/*     */   public static final int LOG_KERN = 0;
/*     */   public static final int LOG_USER = 8;
/*     */   public static final int LOG_MAIL = 16;
/*     */   public static final int LOG_DAEMON = 24;
/*     */   public static final int LOG_AUTH = 32;
/*     */   public static final int LOG_SYSLOG = 40;
/*     */   public static final int LOG_LPR = 48;
/*     */   public static final int LOG_NEWS = 56;
/*     */   public static final int LOG_UUCP = 64;
/*     */   public static final int LOG_CRON = 72;
/*     */   public static final int LOG_AUTHPRIV = 80;
/*     */   public static final int LOG_FTP = 88;
/*     */   public static final int LOG_LOCAL0 = 128;
/*     */   public static final int LOG_LOCAL1 = 136;
/*     */   public static final int LOG_LOCAL2 = 144;
/*     */   public static final int LOG_LOCAL3 = 152;
/*     */   public static final int LOG_LOCAL4 = 160;
/*     */   public static final int LOG_LOCAL5 = 168;
/*     */   public static final int LOG_LOCAL6 = 176;
/*     */   public static final int LOG_LOCAL7 = 184;
/*     */   protected static final int SYSLOG_HOST_OI = 0;
/*     */   protected static final int FACILITY_OI = 1;
/*     */   static final String TAB = "    ";
/*  96 */   int syslogFacility = 8;
/*     */   String facilityStr;
/*  98 */   boolean facilityPrinting = false;
/*     */   SyslogQuietWriter sqw;
/*     */   String syslogHost;
/* 109 */   private boolean header = false;
/*     */ 
/* 114 */   private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd HH:mm:ss ", Locale.ENGLISH);
/*     */   private String localHostname;
/* 124 */   private boolean layoutHeaderChecked = false;
/*     */ 
/*     */   public SyslogAppender()
/*     */   {
/* 128 */     initSyslogFacilityStr();
/*     */   }
/*     */ 
/*     */   public SyslogAppender(Layout layout, int syslogFacility)
/*     */   {
/* 133 */     this.layout = layout;
/* 134 */     this.syslogFacility = syslogFacility;
/* 135 */     initSyslogFacilityStr();
/*     */   }
/*     */ 
/*     */   public SyslogAppender(Layout layout, String syslogHost, int syslogFacility)
/*     */   {
/* 140 */     this(layout, syslogFacility);
/* 141 */     setSyslogHost(syslogHost);
/*     */   }
/*     */ 
/*     */   public synchronized void close()
/*     */   {
/* 152 */     this.closed = true;
/* 153 */     if (this.sqw != null)
/*     */       try {
/* 155 */         if ((this.layoutHeaderChecked) && (this.layout != null) && (this.layout.getFooter() != null)) {
/* 156 */           sendLayoutMessage(this.layout.getFooter());
/*     */         }
/* 158 */         this.sqw.close();
/* 159 */         this.sqw = null;
/*     */       } catch (IOException ex) {
/* 161 */         this.sqw = null;
/*     */       }
/*     */   }
/*     */ 
/*     */   private void initSyslogFacilityStr()
/*     */   {
/* 168 */     this.facilityStr = getFacilityString(this.syslogFacility);
/*     */ 
/* 170 */     if (this.facilityStr == null) {
/* 171 */       System.err.println("\"" + this.syslogFacility + "\" is an unknown syslog facility. Defaulting to \"USER\".");
/*     */ 
/* 173 */       this.syslogFacility = 8;
/* 174 */       this.facilityStr = "user:";
/*     */     } else {
/* 176 */       this.facilityStr += ":";
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String getFacilityString(int syslogFacility)
/*     */   {
/* 187 */     switch (syslogFacility) { case 0:
/* 188 */       return "kern";
/*     */     case 8:
/* 189 */       return "user";
/*     */     case 16:
/* 190 */       return "mail";
/*     */     case 24:
/* 191 */       return "daemon";
/*     */     case 32:
/* 192 */       return "auth";
/*     */     case 40:
/* 193 */       return "syslog";
/*     */     case 48:
/* 194 */       return "lpr";
/*     */     case 56:
/* 195 */       return "news";
/*     */     case 64:
/* 196 */       return "uucp";
/*     */     case 72:
/* 197 */       return "cron";
/*     */     case 80:
/* 198 */       return "authpriv";
/*     */     case 88:
/* 199 */       return "ftp";
/*     */     case 128:
/* 200 */       return "local0";
/*     */     case 136:
/* 201 */       return "local1";
/*     */     case 144:
/* 202 */       return "local2";
/*     */     case 152:
/* 203 */       return "local3";
/*     */     case 160:
/* 204 */       return "local4";
/*     */     case 168:
/* 205 */       return "local5";
/*     */     case 176:
/* 206 */       return "local6";
/*     */     case 184:
/* 207 */       return "local7"; }
/* 208 */     return null;
/*     */   }
/*     */ 
/*     */   public static int getFacility(String facilityName)
/*     */   {
/* 226 */     if (facilityName != null) {
/* 227 */       facilityName = facilityName.trim();
/*     */     }
/* 229 */     if ("KERN".equalsIgnoreCase(facilityName))
/* 230 */       return 0;
/* 231 */     if ("USER".equalsIgnoreCase(facilityName))
/* 232 */       return 8;
/* 233 */     if ("MAIL".equalsIgnoreCase(facilityName))
/* 234 */       return 16;
/* 235 */     if ("DAEMON".equalsIgnoreCase(facilityName))
/* 236 */       return 24;
/* 237 */     if ("AUTH".equalsIgnoreCase(facilityName))
/* 238 */       return 32;
/* 239 */     if ("SYSLOG".equalsIgnoreCase(facilityName))
/* 240 */       return 40;
/* 241 */     if ("LPR".equalsIgnoreCase(facilityName))
/* 242 */       return 48;
/* 243 */     if ("NEWS".equalsIgnoreCase(facilityName))
/* 244 */       return 56;
/* 245 */     if ("UUCP".equalsIgnoreCase(facilityName))
/* 246 */       return 64;
/* 247 */     if ("CRON".equalsIgnoreCase(facilityName))
/* 248 */       return 72;
/* 249 */     if ("AUTHPRIV".equalsIgnoreCase(facilityName))
/* 250 */       return 80;
/* 251 */     if ("FTP".equalsIgnoreCase(facilityName))
/* 252 */       return 88;
/* 253 */     if ("LOCAL0".equalsIgnoreCase(facilityName))
/* 254 */       return 128;
/* 255 */     if ("LOCAL1".equalsIgnoreCase(facilityName))
/* 256 */       return 136;
/* 257 */     if ("LOCAL2".equalsIgnoreCase(facilityName))
/* 258 */       return 144;
/* 259 */     if ("LOCAL3".equalsIgnoreCase(facilityName))
/* 260 */       return 152;
/* 261 */     if ("LOCAL4".equalsIgnoreCase(facilityName))
/* 262 */       return 160;
/* 263 */     if ("LOCAL5".equalsIgnoreCase(facilityName))
/* 264 */       return 168;
/* 265 */     if ("LOCAL6".equalsIgnoreCase(facilityName))
/* 266 */       return 176;
/* 267 */     if ("LOCAL7".equalsIgnoreCase(facilityName)) {
/* 268 */       return 184;
/*     */     }
/* 270 */     return -1;
/*     */   }
/*     */ 
/*     */   private void splitPacket(String header, String packet)
/*     */   {
/* 276 */     int byteCount = packet.getBytes().length;
/*     */ 
/* 282 */     if (byteCount <= 1019) {
/* 283 */       this.sqw.write(packet);
/*     */     } else {
/* 285 */       int split = header.length() + (packet.length() - header.length()) / 2;
/* 286 */       splitPacket(header, packet.substring(0, split) + "...");
/* 287 */       splitPacket(header, header + "..." + packet.substring(split));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void append(LoggingEvent event)
/*     */   {
/* 294 */     if (!isAsSevereAsThreshold(event.getLevel())) {
/* 295 */       return;
/*     */     }
/*     */ 
/* 298 */     if (this.sqw == null) {
/* 299 */       this.errorHandler.error("No syslog host is set for SyslogAppedender named \"" + this.name + "\".");
/*     */ 
/* 301 */       return;
/*     */     }
/*     */ 
/* 304 */     if (!this.layoutHeaderChecked) {
/* 305 */       if ((this.layout != null) && (this.layout.getHeader() != null)) {
/* 306 */         sendLayoutMessage(this.layout.getHeader());
/*     */       }
/* 308 */       this.layoutHeaderChecked = true;
/*     */     }
/*     */ 
/* 311 */     String hdr = getPacketHeader(event.timeStamp);
/* 312 */     String packet = this.layout.format(event);
/* 313 */     if ((this.facilityPrinting) || (hdr.length() > 0)) {
/* 314 */       StringBuffer buf = new StringBuffer(hdr);
/* 315 */       if (this.facilityPrinting) {
/* 316 */         buf.append(this.facilityStr);
/*     */       }
/* 318 */       buf.append(packet);
/* 319 */       packet = buf.toString();
/*     */     }
/*     */ 
/* 322 */     this.sqw.setLevel(event.getLevel().getSyslogEquivalent());
/*     */ 
/* 326 */     if (packet.length() > 256)
/* 327 */       splitPacket(hdr, packet);
/*     */     else {
/* 329 */       this.sqw.write(packet);
/*     */     }
/*     */ 
/* 332 */     if (this.layout.ignoresThrowable()) {
/* 333 */       String[] s = event.getThrowableStrRep();
/* 334 */       if (s != null)
/* 335 */         for (int i = 0; i < s.length; i++)
/* 336 */           if (s[i].startsWith("\t"))
/* 337 */             this.sqw.write(hdr + "    " + s[i].substring(1));
/*     */           else
/* 339 */             this.sqw.write(hdr + s[i]);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/* 352 */     if (this.header) {
/* 353 */       getLocalHostname();
/*     */     }
/* 355 */     if ((this.layout != null) && (this.layout.getHeader() != null)) {
/* 356 */       sendLayoutMessage(this.layout.getHeader());
/*     */     }
/* 358 */     this.layoutHeaderChecked = true;
/*     */   }
/*     */ 
/*     */   public boolean requiresLayout()
/*     */   {
/* 368 */     return true;
/*     */   }
/*     */ 
/*     */   public void setSyslogHost(String syslogHost)
/*     */   {
/* 382 */     this.sqw = new SyslogQuietWriter(new SyslogWriter(syslogHost), this.syslogFacility, this.errorHandler);
/*     */ 
/* 385 */     this.syslogHost = syslogHost;
/*     */   }
/*     */ 
/*     */   public String getSyslogHost()
/*     */   {
/* 393 */     return this.syslogHost;
/*     */   }
/*     */ 
/*     */   public void setFacility(String facilityName)
/*     */   {
/* 407 */     if (facilityName == null) {
/* 408 */       return;
/*     */     }
/* 410 */     this.syslogFacility = getFacility(facilityName);
/* 411 */     if (this.syslogFacility == -1) {
/* 412 */       System.err.println("[" + facilityName + "] is an unknown syslog facility. Defaulting to [USER].");
/*     */ 
/* 414 */       this.syslogFacility = 8;
/*     */     }
/*     */ 
/* 417 */     initSyslogFacilityStr();
/*     */ 
/* 420 */     if (this.sqw != null)
/* 421 */       this.sqw.setSyslogFacility(this.syslogFacility);
/*     */   }
/*     */ 
/*     */   public String getFacility()
/*     */   {
/* 430 */     return getFacilityString(this.syslogFacility);
/*     */   }
/*     */ 
/*     */   public void setFacilityPrinting(boolean on)
/*     */   {
/* 440 */     this.facilityPrinting = on;
/*     */   }
/*     */ 
/*     */   public boolean getFacilityPrinting()
/*     */   {
/* 448 */     return this.facilityPrinting;
/*     */   }
/*     */ 
/*     */   public final boolean getHeader()
/*     */   {
/* 458 */     return this.header;
/*     */   }
/*     */ 
/*     */   public final void setHeader(boolean val)
/*     */   {
/* 467 */     this.header = val;
/*     */   }
/*     */ 
/*     */   private String getLocalHostname()
/*     */   {
/* 476 */     if (this.localHostname == null) {
/*     */       try {
/* 478 */         InetAddress addr = InetAddress.getLocalHost();
/* 479 */         this.localHostname = addr.getHostName();
/*     */       } catch (UnknownHostException uhe) {
/* 481 */         this.localHostname = "UNKNOWN_HOST";
/*     */       }
/*     */     }
/* 484 */     return this.localHostname;
/*     */   }
/*     */ 
/*     */   private String getPacketHeader(long timeStamp)
/*     */   {
/* 494 */     if (this.header) {
/* 495 */       StringBuffer buf = new StringBuffer(this.dateFormat.format(new Date(timeStamp)));
/*     */ 
/* 497 */       if (buf.charAt(4) == '0') {
/* 498 */         buf.setCharAt(4, ' ');
/*     */       }
/* 500 */       buf.append(getLocalHostname());
/* 501 */       buf.append(' ');
/* 502 */       return buf.toString();
/*     */     }
/* 504 */     return "";
/*     */   }
/*     */ 
/*     */   private void sendLayoutMessage(String msg)
/*     */   {
/* 512 */     if (this.sqw != null) {
/* 513 */       String packet = msg;
/* 514 */       String hdr = getPacketHeader(new Date().getTime());
/* 515 */       if ((this.facilityPrinting) || (hdr.length() > 0)) {
/* 516 */         StringBuffer buf = new StringBuffer(hdr);
/* 517 */         if (this.facilityPrinting) {
/* 518 */           buf.append(this.facilityStr);
/*     */         }
/* 520 */         buf.append(msg);
/* 521 */         packet = buf.toString();
/*     */       }
/* 523 */       this.sqw.setLevel(6);
/* 524 */       this.sqw.write(packet);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.net.SyslogAppender
 * JD-Core Version:    0.6.0
 */
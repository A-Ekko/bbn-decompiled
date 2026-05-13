/*     */ package org.apache.log4j.net;
/*     */ 
/*     */ import java.io.PrintStream;
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
/*  80 */   int syslogFacility = 8;
/*     */   String facilityStr;
/*  82 */   boolean facilityPrinting = false;
/*     */   SyslogQuietWriter sqw;
/*     */   String syslogHost;
/*     */ 
/*     */   public SyslogAppender()
/*     */   {
/*  90 */     initSyslogFacilityStr();
/*     */   }
/*     */ 
/*     */   public SyslogAppender(Layout layout, int syslogFacility)
/*     */   {
/*  95 */     this.layout = layout;
/*  96 */     this.syslogFacility = syslogFacility;
/*  97 */     initSyslogFacilityStr();
/*     */   }
/*     */ 
/*     */   public SyslogAppender(Layout layout, String syslogHost, int syslogFacility)
/*     */   {
/* 102 */     this(layout, syslogFacility);
/* 103 */     setSyslogHost(syslogHost);
/*     */   }
/*     */ 
/*     */   public synchronized void close()
/*     */   {
/* 114 */     this.closed = true;
/*     */ 
/* 117 */     this.sqw = null;
/*     */   }
/*     */ 
/*     */   private void initSyslogFacilityStr()
/*     */   {
/* 122 */     this.facilityStr = getFacilityString(this.syslogFacility);
/*     */ 
/* 124 */     if (this.facilityStr == null) {
/* 125 */       System.err.println("\"" + this.syslogFacility + "\" is an unknown syslog facility. Defaulting to \"USER\".");
/*     */ 
/* 127 */       this.syslogFacility = 8;
/* 128 */       this.facilityStr = "user:";
/*     */     } else {
/* 130 */       this.facilityStr += ":";
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String getFacilityString(int syslogFacility)
/*     */   {
/* 141 */     switch (syslogFacility) { case 0:
/* 142 */       return "kern";
/*     */     case 8:
/* 143 */       return "user";
/*     */     case 16:
/* 144 */       return "mail";
/*     */     case 24:
/* 145 */       return "daemon";
/*     */     case 32:
/* 146 */       return "auth";
/*     */     case 40:
/* 147 */       return "syslog";
/*     */     case 48:
/* 148 */       return "lpr";
/*     */     case 56:
/* 149 */       return "news";
/*     */     case 64:
/* 150 */       return "uucp";
/*     */     case 72:
/* 151 */       return "cron";
/*     */     case 80:
/* 152 */       return "authpriv";
/*     */     case 88:
/* 153 */       return "ftp";
/*     */     case 128:
/* 154 */       return "local0";
/*     */     case 136:
/* 155 */       return "local1";
/*     */     case 144:
/* 156 */       return "local2";
/*     */     case 152:
/* 157 */       return "local3";
/*     */     case 160:
/* 158 */       return "local4";
/*     */     case 168:
/* 159 */       return "local5";
/*     */     case 176:
/* 160 */       return "local6";
/*     */     case 184:
/* 161 */       return "local7"; }
/* 162 */     return null;
/*     */   }
/*     */ 
/*     */   public static int getFacility(String facilityName)
/*     */   {
/* 180 */     if (facilityName != null) {
/* 181 */       facilityName = facilityName.trim();
/*     */     }
/* 183 */     if ("KERN".equalsIgnoreCase(facilityName))
/* 184 */       return 0;
/* 185 */     if ("USER".equalsIgnoreCase(facilityName))
/* 186 */       return 8;
/* 187 */     if ("MAIL".equalsIgnoreCase(facilityName))
/* 188 */       return 16;
/* 189 */     if ("DAEMON".equalsIgnoreCase(facilityName))
/* 190 */       return 24;
/* 191 */     if ("AUTH".equalsIgnoreCase(facilityName))
/* 192 */       return 32;
/* 193 */     if ("SYSLOG".equalsIgnoreCase(facilityName))
/* 194 */       return 40;
/* 195 */     if ("LPR".equalsIgnoreCase(facilityName))
/* 196 */       return 48;
/* 197 */     if ("NEWS".equalsIgnoreCase(facilityName))
/* 198 */       return 56;
/* 199 */     if ("UUCP".equalsIgnoreCase(facilityName))
/* 200 */       return 64;
/* 201 */     if ("CRON".equalsIgnoreCase(facilityName))
/* 202 */       return 72;
/* 203 */     if ("AUTHPRIV".equalsIgnoreCase(facilityName))
/* 204 */       return 80;
/* 205 */     if ("FTP".equalsIgnoreCase(facilityName))
/* 206 */       return 88;
/* 207 */     if ("LOCAL0".equalsIgnoreCase(facilityName))
/* 208 */       return 128;
/* 209 */     if ("LOCAL1".equalsIgnoreCase(facilityName))
/* 210 */       return 136;
/* 211 */     if ("LOCAL2".equalsIgnoreCase(facilityName))
/* 212 */       return 144;
/* 213 */     if ("LOCAL3".equalsIgnoreCase(facilityName))
/* 214 */       return 152;
/* 215 */     if ("LOCAL4".equalsIgnoreCase(facilityName))
/* 216 */       return 160;
/* 217 */     if ("LOCAL5".equalsIgnoreCase(facilityName))
/* 218 */       return 168;
/* 219 */     if ("LOCAL6".equalsIgnoreCase(facilityName))
/* 220 */       return 176;
/* 221 */     if ("LOCAL7".equalsIgnoreCase(facilityName)) {
/* 222 */       return 184;
/*     */     }
/* 224 */     return -1;
/*     */   }
/*     */ 
/*     */   public void append(LoggingEvent event)
/*     */   {
/* 231 */     if (!isAsSevereAsThreshold(event.getLevel())) {
/* 232 */       return;
/*     */     }
/*     */ 
/* 235 */     if (this.sqw == null) {
/* 236 */       this.errorHandler.error("No syslog host is set for SyslogAppedender named \"" + this.name + "\".");
/*     */ 
/* 238 */       return;
/*     */     }
/*     */ 
/* 241 */     String buffer = (this.facilityPrinting ? this.facilityStr : "") + this.layout.format(event);
/*     */ 
/* 244 */     this.sqw.setLevel(event.getLevel().getSyslogEquivalent());
/* 245 */     this.sqw.write(buffer);
/*     */ 
/* 247 */     String[] s = event.getThrowableStrRep();
/* 248 */     if (s != null) {
/* 249 */       int len = s.length;
/* 250 */       if (len > 0) {
/* 251 */         this.sqw.write(s[0]);
/*     */ 
/* 253 */         for (int i = 1; i < len; i++)
/* 254 */           this.sqw.write("    " + s[i].substring(1));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean requiresLayout()
/*     */   {
/* 276 */     return true;
/*     */   }
/*     */ 
/*     */   public void setSyslogHost(String syslogHost)
/*     */   {
/* 288 */     this.sqw = new SyslogQuietWriter(new SyslogWriter(syslogHost), this.syslogFacility, this.errorHandler);
/*     */ 
/* 291 */     this.syslogHost = syslogHost;
/*     */   }
/*     */ 
/*     */   public String getSyslogHost()
/*     */   {
/* 299 */     return this.syslogHost;
/*     */   }
/*     */ 
/*     */   public void setFacility(String facilityName)
/*     */   {
/* 313 */     if (facilityName == null) {
/* 314 */       return;
/*     */     }
/* 316 */     this.syslogFacility = getFacility(facilityName);
/* 317 */     if (this.syslogFacility == -1) {
/* 318 */       System.err.println("[" + facilityName + "] is an unknown syslog facility. Defaulting to [USER].");
/*     */ 
/* 320 */       this.syslogFacility = 8;
/*     */     }
/*     */ 
/* 323 */     initSyslogFacilityStr();
/*     */ 
/* 326 */     if (this.sqw != null)
/* 327 */       this.sqw.setSyslogFacility(this.syslogFacility);
/*     */   }
/*     */ 
/*     */   public String getFacility()
/*     */   {
/* 336 */     return getFacilityString(this.syslogFacility);
/*     */   }
/*     */ 
/*     */   public void setFacilityPrinting(boolean on)
/*     */   {
/* 346 */     this.facilityPrinting = on;
/*     */   }
/*     */ 
/*     */   public boolean getFacilityPrinting()
/*     */   {
/* 354 */     return this.facilityPrinting;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.net.SyslogAppender
 * JD-Core Version:    0.6.0
 */
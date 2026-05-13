/*     */ package org.apache.log4j;
/*     */ 
/*     */ import java.util.Date;
/*     */ import org.apache.log4j.helpers.Transform;
/*     */ import org.apache.log4j.spi.LocationInfo;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class HTMLLayout extends Layout
/*     */ {
/*  21 */   protected final int BUF_SIZE = 256;
/*  22 */   protected final int MAX_CAPACITY = 1024;
/*     */ 
/*  24 */   static String TRACE_PREFIX = "<br>&nbsp;&nbsp;&nbsp;&nbsp;";
/*     */ 
/*  27 */   private StringBuffer sbuf = new StringBuffer(256);
/*     */ 
/*     */   /** @deprecated */
/*     */   public static final String LOCATION_INFO_OPTION = "LocationInfo";
/*     */   public static final String TITLE_OPTION = "Title";
/*  51 */   boolean locationInfo = false;
/*     */ 
/*  53 */   String title = "Log4J Log Messages";
/*     */ 
/*     */   public void setLocationInfo(boolean flag)
/*     */   {
/*  68 */     this.locationInfo = flag;
/*     */   }
/*     */ 
/*     */   public boolean getLocationInfo()
/*     */   {
/*  76 */     return this.locationInfo;
/*     */   }
/*     */ 
/*     */   public void setTitle(String title)
/*     */   {
/*  87 */     this.title = title;
/*     */   }
/*     */ 
/*     */   public String getTitle()
/*     */   {
/*  95 */     return this.title;
/*     */   }
/*     */ 
/*     */   public String getContentType()
/*     */   {
/* 103 */     return "text/html";
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/*     */   }
/*     */ 
/*     */   public String format(LoggingEvent event)
/*     */   {
/* 116 */     if (this.sbuf.capacity() > 1024)
/* 117 */       this.sbuf = new StringBuffer(256);
/*     */     else {
/* 119 */       this.sbuf.setLength(0);
/*     */     }
/*     */ 
/* 122 */     this.sbuf.append(Layout.LINE_SEP + "<tr>" + Layout.LINE_SEP);
/*     */ 
/* 124 */     this.sbuf.append("<td>");
/* 125 */     this.sbuf.append(event.timeStamp - LoggingEvent.getStartTime());
/* 126 */     this.sbuf.append("</td>" + Layout.LINE_SEP);
/*     */ 
/* 128 */     this.sbuf.append("<td title=\"" + event.getThreadName() + " thread\">");
/* 129 */     this.sbuf.append(Transform.escapeTags(event.getThreadName()));
/* 130 */     this.sbuf.append("</td>" + Layout.LINE_SEP);
/*     */ 
/* 132 */     this.sbuf.append("<td title=\"Level\">");
/* 133 */     if (event.getLevel().equals(Level.DEBUG)) {
/* 134 */       this.sbuf.append("<font color=\"#339933\">");
/* 135 */       this.sbuf.append(event.getLevel());
/* 136 */       this.sbuf.append("</font>");
/*     */     }
/* 138 */     else if (event.getLevel().isGreaterOrEqual(Level.WARN)) {
/* 139 */       this.sbuf.append("<font color=\"#993300\"><strong>");
/* 140 */       this.sbuf.append(event.getLevel());
/* 141 */       this.sbuf.append("</strong></font>");
/*     */     } else {
/* 143 */       this.sbuf.append(event.getLevel());
/*     */     }
/* 145 */     this.sbuf.append("</td>" + Layout.LINE_SEP);
/*     */ 
/* 147 */     this.sbuf.append("<td title=\"" + event.getLoggerName() + " category\">");
/* 148 */     this.sbuf.append(Transform.escapeTags(event.getLoggerName()));
/* 149 */     this.sbuf.append("</td>" + Layout.LINE_SEP);
/*     */ 
/* 151 */     if (this.locationInfo) {
/* 152 */       LocationInfo locInfo = event.getLocationInformation();
/* 153 */       this.sbuf.append("<td>");
/* 154 */       this.sbuf.append(Transform.escapeTags(locInfo.getFileName()));
/* 155 */       this.sbuf.append(':');
/* 156 */       this.sbuf.append(locInfo.getLineNumber());
/* 157 */       this.sbuf.append("</td>" + Layout.LINE_SEP);
/*     */     }
/*     */ 
/* 160 */     this.sbuf.append("<td title=\"Message\">");
/* 161 */     this.sbuf.append(Transform.escapeTags(event.getRenderedMessage()));
/* 162 */     this.sbuf.append("</td>" + Layout.LINE_SEP);
/* 163 */     this.sbuf.append("</tr>" + Layout.LINE_SEP);
/*     */ 
/* 165 */     if (event.getNDC() != null) {
/* 166 */       this.sbuf.append("<tr><td bgcolor=\"#EEEEEE\" style=\"font-size : xx-small;\" colspan=\"6\" title=\"Nested Diagnostic Context\">");
/* 167 */       this.sbuf.append("NDC: " + Transform.escapeTags(event.getNDC()));
/* 168 */       this.sbuf.append("</td></tr>" + Layout.LINE_SEP);
/*     */     }
/*     */ 
/* 171 */     String[] s = event.getThrowableStrRep();
/* 172 */     if (s != null) {
/* 173 */       this.sbuf.append("<tr><td bgcolor=\"#993300\" style=\"color:White; font-size : xx-small;\" colspan=\"6\">");
/* 174 */       appendThrowableAsHTML(s, this.sbuf);
/* 175 */       this.sbuf.append("</td></tr>" + Layout.LINE_SEP);
/*     */     }
/*     */ 
/* 178 */     return this.sbuf.toString();
/*     */   }
/*     */ 
/*     */   void appendThrowableAsHTML(String[] s, StringBuffer sbuf) {
/* 182 */     if (s != null) {
/* 183 */       int len = s.length;
/* 184 */       if (len == 0)
/* 185 */         return;
/* 186 */       sbuf.append(Transform.escapeTags(s[0]));
/* 187 */       sbuf.append(Layout.LINE_SEP);
/* 188 */       for (int i = 1; i < len; i++) {
/* 189 */         sbuf.append(TRACE_PREFIX);
/* 190 */         sbuf.append(Transform.escapeTags(s[i]));
/* 191 */         sbuf.append(Layout.LINE_SEP);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getHeader()
/*     */   {
/* 201 */     StringBuffer sbuf = new StringBuffer();
/* 202 */     sbuf.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">" + Layout.LINE_SEP);
/* 203 */     sbuf.append("<html>" + Layout.LINE_SEP);
/* 204 */     sbuf.append("<head>" + Layout.LINE_SEP);
/* 205 */     sbuf.append("<title>" + this.title + "</title>" + Layout.LINE_SEP);
/* 206 */     sbuf.append("<style type=\"text/css\">" + Layout.LINE_SEP);
/* 207 */     sbuf.append("<!--" + Layout.LINE_SEP);
/* 208 */     sbuf.append("body, table {font-family: arial,sans-serif; font-size: x-small;}" + Layout.LINE_SEP);
/* 209 */     sbuf.append("th {background: #336699; color: #FFFFFF; text-align: left;}" + Layout.LINE_SEP);
/* 210 */     sbuf.append("-->" + Layout.LINE_SEP);
/* 211 */     sbuf.append("</style>" + Layout.LINE_SEP);
/* 212 */     sbuf.append("</head>" + Layout.LINE_SEP);
/* 213 */     sbuf.append("<body bgcolor=\"#FFFFFF\" topmargin=\"6\" leftmargin=\"6\">" + Layout.LINE_SEP);
/* 214 */     sbuf.append("<hr size=\"1\" noshade>" + Layout.LINE_SEP);
/* 215 */     sbuf.append("Log session start time " + new Date() + "<br>" + Layout.LINE_SEP);
/* 216 */     sbuf.append("<br>" + Layout.LINE_SEP);
/* 217 */     sbuf.append("<table cellspacing=\"0\" cellpadding=\"4\" border=\"1\" bordercolor=\"#224466\" width=\"100%\">" + Layout.LINE_SEP);
/* 218 */     sbuf.append("<tr>" + Layout.LINE_SEP);
/* 219 */     sbuf.append("<th>Time</th>" + Layout.LINE_SEP);
/* 220 */     sbuf.append("<th>Thread</th>" + Layout.LINE_SEP);
/* 221 */     sbuf.append("<th>Level</th>" + Layout.LINE_SEP);
/* 222 */     sbuf.append("<th>Category</th>" + Layout.LINE_SEP);
/* 223 */     if (this.locationInfo) {
/* 224 */       sbuf.append("<th>File:Line</th>" + Layout.LINE_SEP);
/*     */     }
/* 226 */     sbuf.append("<th>Message</th>" + Layout.LINE_SEP);
/* 227 */     sbuf.append("</tr>" + Layout.LINE_SEP);
/* 228 */     return sbuf.toString();
/*     */   }
/*     */ 
/*     */   public String getFooter()
/*     */   {
/* 236 */     StringBuffer sbuf = new StringBuffer();
/* 237 */     sbuf.append("</table>" + Layout.LINE_SEP);
/* 238 */     sbuf.append("<br>" + Layout.LINE_SEP);
/* 239 */     sbuf.append("</body></html>");
/* 240 */     return sbuf.toString();
/*     */   }
/*     */ 
/*     */   public boolean ignoresThrowable()
/*     */   {
/* 248 */     return false;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.HTMLLayout
 * JD-Core Version:    0.6.0
 */
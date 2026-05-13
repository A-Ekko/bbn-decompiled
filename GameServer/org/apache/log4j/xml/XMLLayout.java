/*     */ package org.apache.log4j.xml;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Set;
/*     */ import org.apache.log4j.Layout;
/*     */ import org.apache.log4j.helpers.Transform;
/*     */ import org.apache.log4j.spi.LocationInfo;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class XMLLayout extends Layout
/*     */ {
/*  70 */   private final int DEFAULT_SIZE = 256;
/*  71 */   private final int UPPER_LIMIT = 2048;
/*     */ 
/*  73 */   private StringBuffer buf = new StringBuffer(256);
/*  74 */   private boolean locationInfo = false;
/*  75 */   private boolean properties = false;
/*     */ 
/*     */   public void setLocationInfo(boolean flag)
/*     */   {
/*  89 */     this.locationInfo = flag;
/*     */   }
/*     */ 
/*     */   public boolean getLocationInfo()
/*     */   {
/*  96 */     return this.locationInfo;
/*     */   }
/*     */ 
/*     */   public void setProperties(boolean flag)
/*     */   {
/* 104 */     this.properties = flag;
/*     */   }
/*     */ 
/*     */   public boolean getProperties()
/*     */   {
/* 112 */     return this.properties;
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/*     */   }
/*     */ 
/*     */   public String format(LoggingEvent event)
/*     */   {
/* 127 */     if (this.buf.capacity() > 2048)
/* 128 */       this.buf = new StringBuffer(256);
/*     */     else {
/* 130 */       this.buf.setLength(0);
/*     */     }
/*     */ 
/* 135 */     this.buf.append("<log4j:event logger=\"");
/* 136 */     this.buf.append(Transform.escapeTags(event.getLoggerName()));
/* 137 */     this.buf.append("\" timestamp=\"");
/* 138 */     this.buf.append(event.timeStamp);
/* 139 */     this.buf.append("\" level=\"");
/* 140 */     this.buf.append(Transform.escapeTags(String.valueOf(event.getLevel())));
/* 141 */     this.buf.append("\" thread=\"");
/* 142 */     this.buf.append(Transform.escapeTags(event.getThreadName()));
/* 143 */     this.buf.append("\">\r\n");
/*     */ 
/* 145 */     this.buf.append("<log4j:message><![CDATA[");
/*     */ 
/* 148 */     Transform.appendEscapingCDATA(this.buf, event.getRenderedMessage());
/* 149 */     this.buf.append("]]></log4j:message>\r\n");
/*     */ 
/* 151 */     String ndc = event.getNDC();
/* 152 */     if (ndc != null) {
/* 153 */       this.buf.append("<log4j:NDC><![CDATA[");
/* 154 */       Transform.appendEscapingCDATA(this.buf, ndc);
/* 155 */       this.buf.append("]]></log4j:NDC>\r\n");
/*     */     }
/*     */ 
/* 158 */     String[] s = event.getThrowableStrRep();
/* 159 */     if (s != null) {
/* 160 */       this.buf.append("<log4j:throwable><![CDATA[");
/* 161 */       for (int i = 0; i < s.length; i++) {
/* 162 */         Transform.appendEscapingCDATA(this.buf, s[i]);
/* 163 */         this.buf.append("\r\n");
/*     */       }
/* 165 */       this.buf.append("]]></log4j:throwable>\r\n");
/*     */     }
/*     */ 
/* 168 */     if (this.locationInfo) {
/* 169 */       LocationInfo locationInfo = event.getLocationInformation();
/* 170 */       this.buf.append("<log4j:locationInfo class=\"");
/* 171 */       this.buf.append(Transform.escapeTags(locationInfo.getClassName()));
/* 172 */       this.buf.append("\" method=\"");
/* 173 */       this.buf.append(Transform.escapeTags(locationInfo.getMethodName()));
/* 174 */       this.buf.append("\" file=\"");
/* 175 */       this.buf.append(Transform.escapeTags(locationInfo.getFileName()));
/* 176 */       this.buf.append("\" line=\"");
/* 177 */       this.buf.append(locationInfo.getLineNumber());
/* 178 */       this.buf.append("\"/>\r\n");
/*     */     }
/*     */ 
/* 181 */     if (this.properties) {
/* 182 */       Set keySet = event.getPropertyKeySet();
/* 183 */       if (keySet.size() > 0) {
/* 184 */         this.buf.append("<log4j:properties>\r\n");
/* 185 */         Object[] keys = keySet.toArray();
/* 186 */         Arrays.sort(keys);
/* 187 */         for (int i = 0; i < keys.length; i++) {
/* 188 */           String key = keys[i].toString();
/* 189 */           Object val = event.getMDC(key);
/* 190 */           if (val != null) {
/* 191 */             this.buf.append("<log4j:data name=\"");
/* 192 */             this.buf.append(Transform.escapeTags(key));
/* 193 */             this.buf.append("\" value=\"");
/* 194 */             this.buf.append(Transform.escapeTags(String.valueOf(val)));
/* 195 */             this.buf.append("\"/>\r\n");
/*     */           }
/*     */         }
/* 198 */         this.buf.append("</log4j:properties>\r\n");
/*     */       }
/*     */     }
/*     */ 
/* 202 */     this.buf.append("</log4j:event>\r\n\r\n");
/*     */ 
/* 204 */     return this.buf.toString();
/*     */   }
/*     */ 
/*     */   public boolean ignoresThrowable()
/*     */   {
/* 212 */     return false;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.xml.XMLLayout
 * JD-Core Version:    0.6.0
 */
/*     */ package org.apache.mina.util;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Set;
/*     */ import java.util.logging.Formatter;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.LogRecord;
/*     */ import org.slf4j.MDC;
/*     */ import org.slf4j.helpers.BasicMDCAdapter;
/*     */ 
/*     */ public class Log4jXmlFormatter extends Formatter
/*     */ {
/*  50 */   private final int DEFAULT_SIZE = 256;
/*  51 */   private final int UPPER_LIMIT = 2048;
/*     */ 
/*  53 */   private StringBuffer buf = new StringBuffer(256);
/*  54 */   private boolean locationInfo = false;
/*  55 */   private boolean properties = false;
/*     */ 
/*     */   public void setLocationInfo(boolean flag)
/*     */   {
/*  67 */     this.locationInfo = flag;
/*     */   }
/*     */ 
/*     */   public boolean getLocationInfo()
/*     */   {
/*  76 */     return this.locationInfo;
/*     */   }
/*     */ 
/*     */   public void setProperties(boolean flag)
/*     */   {
/*  85 */     this.properties = flag;
/*     */   }
/*     */ 
/*     */   public boolean getProperties()
/*     */   {
/*  94 */     return this.properties;
/*     */   }
/*     */ 
/*     */   public String format(LogRecord record)
/*     */   {
/* 101 */     if (this.buf.capacity() > 2048)
/* 102 */       this.buf = new StringBuffer(256);
/*     */     else {
/* 104 */       this.buf.setLength(0);
/*     */     }
/* 106 */     this.buf.append("<log4j:event logger=\"");
/* 107 */     this.buf.append(Transform.escapeTags(record.getLoggerName()));
/* 108 */     this.buf.append("\" timestamp=\"");
/* 109 */     this.buf.append(record.getMillis());
/* 110 */     this.buf.append("\" level=\"");
/*     */ 
/* 112 */     this.buf.append(Transform.escapeTags(record.getLevel().getName()));
/* 113 */     this.buf.append("\" thread=\"");
/* 114 */     this.buf.append(String.valueOf(record.getThreadID()));
/* 115 */     this.buf.append("\">\r\n");
/*     */ 
/* 117 */     this.buf.append("<log4j:message><![CDATA[");
/*     */ 
/* 120 */     Transform.appendEscapingCDATA(this.buf, record.getMessage());
/* 121 */     this.buf.append("]]></log4j:message>\r\n");
/*     */ 
/* 123 */     if (record.getThrown() != null) {
/* 124 */       String[] s = Transform.getThrowableStrRep(record.getThrown());
/* 125 */       if (s != null) {
/* 126 */         this.buf.append("<log4j:throwable><![CDATA[");
/* 127 */         for (String value : s) {
/* 128 */           Transform.appendEscapingCDATA(this.buf, value);
/* 129 */           this.buf.append("\r\n");
/*     */         }
/* 131 */         this.buf.append("]]></log4j:throwable>\r\n");
/*     */       }
/*     */     }
/*     */ 
/* 135 */     if (this.locationInfo) {
/* 136 */       this.buf.append("<log4j:locationInfo class=\"");
/* 137 */       this.buf.append(Transform.escapeTags(record.getSourceClassName()));
/* 138 */       this.buf.append("\" method=\"");
/* 139 */       this.buf.append(Transform.escapeTags(record.getSourceMethodName()));
/* 140 */       this.buf.append("\" file=\"?\" line=\"?\"/>\r\n");
/*     */     }
/*     */ 
/* 143 */     if ((this.properties) && 
/* 144 */       ((MDC.getMDCAdapter() instanceof BasicMDCAdapter))) {
/* 145 */       BasicMDCAdapter mdcAdapter = (BasicMDCAdapter)MDC.getMDCAdapter();
/* 146 */       Set keySet = mdcAdapter.getKeys();
/* 147 */       if ((keySet != null) && (keySet.size() > 0)) {
/* 148 */         this.buf.append("<log4j:properties>\r\n");
/* 149 */         Object[] keys = keySet.toArray();
/* 150 */         Arrays.sort(keys);
/* 151 */         for (Object key1 : keys) {
/* 152 */           String key = key1.toString();
/* 153 */           Object val = mdcAdapter.get(key);
/* 154 */           if (val != null) {
/* 155 */             this.buf.append("<log4j:data name=\"");
/* 156 */             this.buf.append(Transform.escapeTags(key));
/* 157 */             this.buf.append("\" value=\"");
/* 158 */             this.buf.append(Transform.escapeTags(String.valueOf(val)));
/* 159 */             this.buf.append("\"/>\r\n");
/*     */           }
/*     */         }
/* 162 */         this.buf.append("</log4j:properties>\r\n");
/*     */       }
/*     */     }
/*     */ 
/* 166 */     this.buf.append("</log4j:event>\r\n\r\n");
/*     */ 
/* 168 */     return this.buf.toString();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.util.Log4jXmlFormatter
 * JD-Core Version:    0.6.0
 */
/*     */ package org.apache.log4j.spi;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Serializable;
/*     */ import java.io.StringWriter;
/*     */ import org.apache.log4j.Layout;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ 
/*     */ public class LocationInfo
/*     */   implements Serializable
/*     */ {
/*     */   transient String lineNumber;
/*     */   transient String fileName;
/*     */   transient String className;
/*     */   transient String methodName;
/*     */   public String fullInfo;
/*  46 */   private static StringWriter sw = new StringWriter();
/*  47 */   private static PrintWriter pw = new PrintWriter(sw);
/*     */   public static final String NA = "?";
/*     */   static final long serialVersionUID = -1325822038990805636L;
/*  59 */   static boolean inVisualAge = false;
/*     */ 
/*     */   public LocationInfo(Throwable t, String fqnOfCallingClass)
/*     */   {
/*  90 */     if (t == null)
/*  91 */       return;
/*     */     String s;
/*  95 */     synchronized (sw) {
/*  96 */       t.printStackTrace(pw);
/*  97 */       s = sw.toString();
/*  98 */       sw.getBuffer().setLength(0);
/*     */     }
/*     */ 
/* 110 */     int ibegin = s.lastIndexOf(fqnOfCallingClass);
/* 111 */     if (ibegin == -1) {
/* 112 */       return;
/*     */     }
/*     */ 
/* 115 */     ibegin = s.indexOf(Layout.LINE_SEP, ibegin);
/* 116 */     if (ibegin == -1)
/* 117 */       return;
/* 118 */     ibegin += Layout.LINE_SEP_LEN;
/*     */ 
/* 121 */     int iend = s.indexOf(Layout.LINE_SEP, ibegin);
/* 122 */     if (iend == -1) {
/* 123 */       return;
/*     */     }
/*     */ 
/* 127 */     if (!inVisualAge)
/*     */     {
/* 129 */       ibegin = s.lastIndexOf("at ", iend);
/* 130 */       if (ibegin == -1) {
/* 131 */         return;
/*     */       }
/* 133 */       ibegin += 3;
/*     */     }
/*     */ 
/* 136 */     this.fullInfo = s.substring(ibegin, iend);
/*     */   }
/*     */ 
/*     */   public String getClassName()
/*     */   {
/* 145 */     if (this.fullInfo == null) return "?";
/* 146 */     if (this.className == null)
/*     */     {
/* 149 */       int iend = this.fullInfo.lastIndexOf('(');
/* 150 */       if (iend == -1) {
/* 151 */         this.className = "?";
/*     */       } else {
/* 153 */         iend = this.fullInfo.lastIndexOf('.', iend);
/*     */ 
/* 164 */         int ibegin = 0;
/* 165 */         if (inVisualAge) {
/* 166 */           ibegin = this.fullInfo.lastIndexOf(' ', iend) + 1;
/*     */         }
/*     */ 
/* 169 */         if (iend == -1)
/* 170 */           this.className = "?";
/*     */         else
/* 172 */           this.className = this.fullInfo.substring(ibegin, iend);
/*     */       }
/*     */     }
/* 175 */     return this.className;
/*     */   }
/*     */ 
/*     */   public String getFileName()
/*     */   {
/* 185 */     if (this.fullInfo == null) return "?";
/*     */ 
/* 187 */     if (this.fileName == null) {
/* 188 */       int iend = this.fullInfo.lastIndexOf(':');
/* 189 */       if (iend == -1) {
/* 190 */         this.fileName = "?";
/*     */       } else {
/* 192 */         int ibegin = this.fullInfo.lastIndexOf('(', iend - 1);
/* 193 */         this.fileName = this.fullInfo.substring(ibegin + 1, iend);
/*     */       }
/*     */     }
/* 196 */     return this.fileName;
/*     */   }
/*     */ 
/*     */   public String getLineNumber()
/*     */   {
/* 206 */     if (this.fullInfo == null) return "?";
/*     */ 
/* 208 */     if (this.lineNumber == null) {
/* 209 */       int iend = this.fullInfo.lastIndexOf(')');
/* 210 */       int ibegin = this.fullInfo.lastIndexOf(':', iend - 1);
/* 211 */       if (ibegin == -1)
/* 212 */         this.lineNumber = "?";
/*     */       else
/* 214 */         this.lineNumber = this.fullInfo.substring(ibegin + 1, iend);
/*     */     }
/* 216 */     return this.lineNumber;
/*     */   }
/*     */ 
/*     */   public String getMethodName()
/*     */   {
/* 224 */     if (this.fullInfo == null) return "?";
/* 225 */     if (this.methodName == null) {
/* 226 */       int iend = this.fullInfo.lastIndexOf('(');
/* 227 */       int ibegin = this.fullInfo.lastIndexOf('.', iend);
/* 228 */       if (ibegin == -1)
/* 229 */         this.methodName = "?";
/*     */       else
/* 231 */         this.methodName = this.fullInfo.substring(ibegin + 1, iend);
/*     */     }
/* 233 */     return this.methodName;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  62 */       Class dummy = Class.forName("com.ibm.uvm.tools.DebugSupport");
/*  63 */       inVisualAge = true;
/*  64 */       LogLog.debug("Detected IBM VisualAge environment.");
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.spi.LocationInfo
 * JD-Core Version:    0.6.0
 */
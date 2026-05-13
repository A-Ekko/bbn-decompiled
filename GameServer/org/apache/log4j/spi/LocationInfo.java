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
/*  56 */   private static StringWriter sw = new StringWriter();
/*  57 */   private static PrintWriter pw = new PrintWriter(sw);
/*     */   public static final String NA = "?";
/*     */   static final long serialVersionUID = -1325822038990805636L;
/*  71 */   public static final LocationInfo NA_LOCATION_INFO = new LocationInfo("?", "?", "?", "?");
/*     */ 
/*  77 */   static boolean inVisualAge = false;
/*     */ 
/*     */   public LocationInfo(Throwable t, String fqnOfCallingClass)
/*     */   {
/* 107 */     if ((t == null) || (fqnOfCallingClass == null))
/* 108 */       return;
/*     */     String s;
/* 112 */     synchronized (sw) {
/* 113 */       t.printStackTrace(pw);
/* 114 */       s = sw.toString();
/* 115 */       sw.getBuffer().setLength(0);
/*     */     }
/*     */ 
/* 127 */     int ibegin = s.lastIndexOf(fqnOfCallingClass);
/* 128 */     if (ibegin == -1) {
/* 129 */       return;
/*     */     }
/*     */ 
/* 132 */     ibegin = s.indexOf(Layout.LINE_SEP, ibegin);
/* 133 */     if (ibegin == -1)
/* 134 */       return;
/* 135 */     ibegin += Layout.LINE_SEP_LEN;
/*     */ 
/* 138 */     int iend = s.indexOf(Layout.LINE_SEP, ibegin);
/* 139 */     if (iend == -1) {
/* 140 */       return;
/*     */     }
/*     */ 
/* 144 */     if (!inVisualAge)
/*     */     {
/* 146 */       ibegin = s.lastIndexOf("at ", iend);
/* 147 */       if (ibegin == -1) {
/* 148 */         return;
/*     */       }
/* 150 */       ibegin += 3;
/*     */     }
/*     */ 
/* 153 */     this.fullInfo = s.substring(ibegin, iend);
/*     */   }
/*     */ 
/*     */   private static final void appendFragment(StringBuffer buf, String fragment)
/*     */   {
/* 166 */     if (fragment == null)
/* 167 */       buf.append("?");
/*     */     else
/* 169 */       buf.append(fragment);
/*     */   }
/*     */ 
/*     */   public LocationInfo(String file, String classname, String method, String line)
/*     */   {
/* 187 */     this.fileName = file;
/* 188 */     this.className = classname;
/* 189 */     this.methodName = method;
/* 190 */     this.lineNumber = line;
/* 191 */     StringBuffer buf = new StringBuffer();
/* 192 */     appendFragment(buf, classname);
/* 193 */     buf.append(".");
/* 194 */     appendFragment(buf, method);
/* 195 */     buf.append("(");
/* 196 */     appendFragment(buf, file);
/* 197 */     buf.append(":");
/* 198 */     appendFragment(buf, line);
/* 199 */     buf.append(")");
/* 200 */     this.fullInfo = buf.toString();
/*     */   }
/*     */ 
/*     */   public String getClassName()
/*     */   {
/* 209 */     if (this.fullInfo == null) return "?";
/* 210 */     if (this.className == null)
/*     */     {
/* 213 */       int iend = this.fullInfo.lastIndexOf('(');
/* 214 */       if (iend == -1) {
/* 215 */         this.className = "?";
/*     */       } else {
/* 217 */         iend = this.fullInfo.lastIndexOf('.', iend);
/*     */ 
/* 228 */         int ibegin = 0;
/* 229 */         if (inVisualAge) {
/* 230 */           ibegin = this.fullInfo.lastIndexOf(' ', iend) + 1;
/*     */         }
/*     */ 
/* 233 */         if (iend == -1)
/* 234 */           this.className = "?";
/*     */         else
/* 236 */           this.className = this.fullInfo.substring(ibegin, iend);
/*     */       }
/*     */     }
/* 239 */     return this.className;
/*     */   }
/*     */ 
/*     */   public String getFileName()
/*     */   {
/* 249 */     if (this.fullInfo == null) return "?";
/*     */ 
/* 251 */     if (this.fileName == null) {
/* 252 */       int iend = this.fullInfo.lastIndexOf(':');
/* 253 */       if (iend == -1) {
/* 254 */         this.fileName = "?";
/*     */       } else {
/* 256 */         int ibegin = this.fullInfo.lastIndexOf('(', iend - 1);
/* 257 */         this.fileName = this.fullInfo.substring(ibegin + 1, iend);
/*     */       }
/*     */     }
/* 260 */     return this.fileName;
/*     */   }
/*     */ 
/*     */   public String getLineNumber()
/*     */   {
/* 270 */     if (this.fullInfo == null) return "?";
/*     */ 
/* 272 */     if (this.lineNumber == null) {
/* 273 */       int iend = this.fullInfo.lastIndexOf(')');
/* 274 */       int ibegin = this.fullInfo.lastIndexOf(':', iend - 1);
/* 275 */       if (ibegin == -1)
/* 276 */         this.lineNumber = "?";
/*     */       else
/* 278 */         this.lineNumber = this.fullInfo.substring(ibegin + 1, iend);
/*     */     }
/* 280 */     return this.lineNumber;
/*     */   }
/*     */ 
/*     */   public String getMethodName()
/*     */   {
/* 288 */     if (this.fullInfo == null) return "?";
/* 289 */     if (this.methodName == null) {
/* 290 */       int iend = this.fullInfo.lastIndexOf('(');
/* 291 */       int ibegin = this.fullInfo.lastIndexOf('.', iend);
/* 292 */       if (ibegin == -1)
/* 293 */         this.methodName = "?";
/*     */       else
/* 295 */         this.methodName = this.fullInfo.substring(ibegin + 1, iend);
/*     */     }
/* 297 */     return this.methodName;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  80 */       inVisualAge = Class.forName("com.ibm.uvm.tools.DebugSupport") != null;
/*  81 */       LogLog.debug("Detected IBM VisualAge environment.");
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.spi.LocationInfo
 * JD-Core Version:    0.6.0
 */
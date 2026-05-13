/*     */ package com.mysql.jdbc.log;
/*     */ 
/*     */ import com.mysql.jdbc.Util;
/*     */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*     */ 
/*     */ public class LogUtils
/*     */ {
/*  30 */   private static final String LINE_SEPARATOR = System.getProperty("line.separator");
/*     */ 
/*  33 */   private static final int LINE_SEPARATOR_LENGTH = LINE_SEPARATOR.length();
/*     */ 
/*     */   public static Object expandProfilerEventIfNecessary(Object possibleProfilerEvent)
/*     */   {
/*  38 */     if ((possibleProfilerEvent instanceof ProfilerEvent)) {
/*  39 */       StringBuffer msgBuf = new StringBuffer();
/*     */ 
/*  41 */       ProfilerEvent evt = (ProfilerEvent)possibleProfilerEvent;
/*     */ 
/*  43 */       Throwable locationException = evt.getEventCreationPoint();
/*     */ 
/*  45 */       if (locationException == null) {
/*  46 */         locationException = new Throwable();
/*     */       }
/*     */ 
/*  49 */       msgBuf.append("Profiler Event: [");
/*     */ 
/*  51 */       boolean appendLocationInfo = false;
/*     */ 
/*  53 */       switch (evt.getEventType()) {
/*     */       case 4:
/*  55 */         msgBuf.append("EXECUTE");
/*     */ 
/*  57 */         break;
/*     */       case 5:
/*  60 */         msgBuf.append("FETCH");
/*     */ 
/*  62 */         break;
/*     */       case 1:
/*  65 */         msgBuf.append("CONSTRUCT");
/*     */ 
/*  67 */         break;
/*     */       case 2:
/*  70 */         msgBuf.append("PREPARE");
/*     */ 
/*  72 */         break;
/*     */       case 3:
/*  75 */         msgBuf.append("QUERY");
/*     */ 
/*  77 */         break;
/*     */       case 0:
/*  80 */         msgBuf.append("WARN");
/*  81 */         appendLocationInfo = true;
/*     */ 
/*  83 */         break;
/*     */       default:
/*  86 */         msgBuf.append("UNKNOWN");
/*     */       }
/*     */ 
/*  89 */       msgBuf.append("] ");
/*  90 */       msgBuf.append(findCallingClassAndMethod(locationException));
/*  91 */       msgBuf.append(" duration: ");
/*  92 */       msgBuf.append(evt.getEventDurationMillis());
/*  93 */       msgBuf.append(" ms, connection-id: ");
/*  94 */       msgBuf.append(evt.getConnectionId());
/*  95 */       msgBuf.append(", statement-id: ");
/*  96 */       msgBuf.append(evt.getStatementId());
/*  97 */       msgBuf.append(", resultset-id: ");
/*  98 */       msgBuf.append(evt.getResultSetId());
/*     */ 
/* 100 */       String evtMessage = evt.getMessage();
/*     */ 
/* 102 */       if (evtMessage != null) {
/* 103 */         msgBuf.append(", message: ");
/* 104 */         msgBuf.append(evtMessage);
/*     */       }
/*     */ 
/* 107 */       if (appendLocationInfo) {
/* 108 */         msgBuf.append("\n\nFull stack trace of location where event occurred:\n\n");
/*     */ 
/* 110 */         msgBuf.append(Util.stackTraceToString(locationException));
/* 111 */         msgBuf.append("\n");
/*     */       }
/*     */ 
/* 114 */       return msgBuf;
/*     */     }
/*     */ 
/* 117 */     return possibleProfilerEvent;
/*     */   }
/*     */ 
/*     */   public static String findCallingClassAndMethod(Throwable t)
/*     */   {
/* 122 */     String stackTraceAsString = Util.stackTraceToString(t);
/*     */ 
/* 124 */     String callingClassAndMethod = "Caller information not available";
/*     */ 
/* 126 */     int endInternalMethods = stackTraceAsString.lastIndexOf("com.mysql.jdbc");
/*     */ 
/* 129 */     if (endInternalMethods != -1) {
/* 130 */       int endOfLine = -1;
/* 131 */       int compliancePackage = stackTraceAsString.indexOf("com.mysql.jdbc.compliance", endInternalMethods);
/*     */ 
/* 134 */       if (compliancePackage != -1)
/* 135 */         endOfLine = compliancePackage - LINE_SEPARATOR_LENGTH;
/*     */       else {
/* 137 */         endOfLine = stackTraceAsString.indexOf(LINE_SEPARATOR, endInternalMethods);
/*     */       }
/*     */ 
/* 141 */       if (endOfLine != -1) {
/* 142 */         int nextEndOfLine = stackTraceAsString.indexOf(LINE_SEPARATOR, endOfLine + LINE_SEPARATOR_LENGTH);
/*     */ 
/* 145 */         if (nextEndOfLine != -1) {
/* 146 */           callingClassAndMethod = stackTraceAsString.substring(endOfLine + LINE_SEPARATOR_LENGTH, nextEndOfLine);
/*     */         }
/*     */         else {
/* 149 */           callingClassAndMethod = stackTraceAsString.substring(endOfLine + LINE_SEPARATOR_LENGTH);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 155 */     if (!callingClassAndMethod.startsWith("at ")) {
/* 156 */       return "at " + callingClassAndMethod;
/*     */     }
/*     */ 
/* 159 */     return callingClassAndMethod;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.mysql.jdbc.log.LogUtils
 * JD-Core Version:    0.6.0
 */
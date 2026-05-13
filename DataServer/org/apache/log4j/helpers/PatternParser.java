/*     */ package org.apache.log4j.helpers;
/*     */ 
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import org.apache.log4j.Layout;
/*     */ import org.apache.log4j.Priority;
/*     */ import org.apache.log4j.spi.LocationInfo;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class PatternParser
/*     */ {
/*     */   private static final char ESCAPE_CHAR = '%';
/*     */   private static final int LITERAL_STATE = 0;
/*     */   private static final int CONVERTER_STATE = 1;
/*     */   private static final int MINUS_STATE = 2;
/*     */   private static final int DOT_STATE = 3;
/*     */   private static final int MIN_STATE = 4;
/*     */   private static final int MAX_STATE = 5;
/*     */   static final int FULL_LOCATION_CONVERTER = 1000;
/*     */   static final int METHOD_LOCATION_CONVERTER = 1001;
/*     */   static final int CLASS_LOCATION_CONVERTER = 1002;
/*     */   static final int LINE_LOCATION_CONVERTER = 1003;
/*     */   static final int FILE_LOCATION_CONVERTER = 1004;
/*     */   static final int RELATIVE_TIME_CONVERTER = 2000;
/*     */   static final int THREAD_CONVERTER = 2001;
/*     */   static final int LEVEL_CONVERTER = 2002;
/*     */   static final int NDC_CONVERTER = 2003;
/*     */   static final int MESSAGE_CONVERTER = 2004;
/*     */   int state;
/*  61 */   protected StringBuffer currentLiteral = new StringBuffer(32);
/*     */   protected int patternLength;
/*     */   protected int i;
/*     */   PatternConverter head;
/*     */   PatternConverter tail;
/*  66 */   protected FormattingInfo formattingInfo = new FormattingInfo();
/*     */   protected String pattern;
/*     */ 
/*     */   public PatternParser(String pattern)
/*     */   {
/*  71 */     this.pattern = pattern;
/*  72 */     this.patternLength = pattern.length();
/*  73 */     this.state = 0;
/*     */   }
/*     */ 
/*     */   private void addToList(PatternConverter pc)
/*     */   {
/*  78 */     if (this.head == null) {
/*  79 */       this.head = (this.tail = pc);
/*     */     } else {
/*  81 */       this.tail.next = pc;
/*  82 */       this.tail = pc;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected String extractOption()
/*     */   {
/*  88 */     if ((this.i < this.patternLength) && (this.pattern.charAt(this.i) == '{')) {
/*  89 */       int end = this.pattern.indexOf('}', this.i);
/*  90 */       if (end > this.i) {
/*  91 */         String r = this.pattern.substring(this.i + 1, end);
/*  92 */         this.i = (end + 1);
/*  93 */         return r;
/*     */       }
/*     */     }
/*  96 */     return null;
/*     */   }
/*     */ 
/*     */   protected int extractPrecisionOption()
/*     */   {
/* 105 */     String opt = extractOption();
/* 106 */     int r = 0;
/* 107 */     if (opt != null) {
/*     */       try {
/* 109 */         r = Integer.parseInt(opt);
/* 110 */         if (r <= 0) {
/* 111 */           LogLog.error("Precision option (" + opt + ") isn't a positive integer.");
/*     */ 
/* 113 */           r = 0;
/*     */         }
/*     */       }
/*     */       catch (NumberFormatException e) {
/* 117 */         LogLog.error("Category option \"" + opt + "\" not a decimal integer.", e);
/*     */       }
/*     */     }
/* 120 */     return r;
/*     */   }
/*     */ 
/*     */   public PatternConverter parse()
/*     */   {
/* 126 */     this.i = 0;
/* 127 */     while (this.i < this.patternLength) {
/* 128 */       char c = this.pattern.charAt(this.i++);
/* 129 */       switch (this.state)
/*     */       {
/*     */       case 0:
/* 132 */         if (this.i == this.patternLength) {
/* 133 */           this.currentLiteral.append(c);
/*     */         }
/* 136 */         else if (c == '%')
/*     */         {
/* 138 */           switch (this.pattern.charAt(this.i)) {
/*     */           case '%':
/* 140 */             this.currentLiteral.append(c);
/* 141 */             this.i += 1;
/* 142 */             break;
/*     */           case 'n':
/* 144 */             this.currentLiteral.append(Layout.LINE_SEP);
/* 145 */             this.i += 1;
/* 146 */             break;
/*     */           default:
/* 148 */             if (this.currentLiteral.length() != 0) {
/* 149 */               addToList(new LiteralPatternConverter(this.currentLiteral.toString()));
/*     */             }
/*     */ 
/* 154 */             this.currentLiteral.setLength(0);
/* 155 */             this.currentLiteral.append(c);
/* 156 */             this.state = 1;
/* 157 */             this.formattingInfo.reset(); break;
/*     */           }
/*     */         }
/*     */         else {
/* 161 */           this.currentLiteral.append(c);
/*     */         }
/* 163 */         break;
/*     */       case 1:
/* 165 */         this.currentLiteral.append(c);
/* 166 */         switch (c) {
/*     */         case '-':
/* 168 */           this.formattingInfo.leftAlign = true;
/* 169 */           break;
/*     */         case '.':
/* 171 */           this.state = 3;
/* 172 */           break;
/*     */         default:
/* 174 */           if ((c >= '0') && (c <= '9')) {
/* 175 */             this.formattingInfo.min = (c - '0');
/* 176 */             this.state = 4;
/*     */           }
/*     */           else {
/* 179 */             finalizeConverter(c);
/*     */           }
/*     */         }
/* 181 */         break;
/*     */       case 4:
/* 183 */         this.currentLiteral.append(c);
/* 184 */         if ((c >= '0') && (c <= '9'))
/* 185 */           this.formattingInfo.min = (this.formattingInfo.min * 10 + (c - '0'));
/* 186 */         else if (c == '.')
/* 187 */           this.state = 3;
/*     */         else {
/* 189 */           finalizeConverter(c);
/*     */         }
/* 191 */         break;
/*     */       case 3:
/* 193 */         this.currentLiteral.append(c);
/* 194 */         if ((c >= '0') && (c <= '9')) {
/* 195 */           this.formattingInfo.max = (c - '0');
/* 196 */           this.state = 5;
/*     */         }
/*     */         else {
/* 199 */           LogLog.error("Error occured in position " + this.i + ".\n Was expecting digit, instead got char \"" + c + "\".");
/*     */ 
/* 201 */           this.state = 0;
/*     */         }
/* 203 */         break;
/*     */       case 5:
/* 205 */         this.currentLiteral.append(c);
/* 206 */         if ((c >= '0') && (c <= '9')) {
/* 207 */           this.formattingInfo.max = (this.formattingInfo.max * 10 + (c - '0'));
/*     */         } else {
/* 209 */           finalizeConverter(c);
/* 210 */           this.state = 0;
/*     */         }
/*     */       case 2:
/*     */       }
/*     */     }
/* 215 */     if (this.currentLiteral.length() != 0) {
/* 216 */       addToList(new LiteralPatternConverter(this.currentLiteral.toString()));
/*     */     }
/*     */ 
/* 219 */     return this.head;
/*     */   }
/*     */ 
/*     */   protected void finalizeConverter(char c)
/*     */   {
/* 224 */     PatternConverter pc = null;
/* 225 */     switch (c) {
/*     */     case 'c':
/* 227 */       pc = new CategoryPatternConverter(this.formattingInfo, extractPrecisionOption());
/*     */ 
/* 231 */       this.currentLiteral.setLength(0);
/* 232 */       break;
/*     */     case 'C':
/* 234 */       pc = new ClassNamePatternConverter(this.formattingInfo, extractPrecisionOption());
/*     */ 
/* 238 */       this.currentLiteral.setLength(0);
/* 239 */       break;
/*     */     case 'd':
/* 241 */       String dateFormatStr = "ISO8601";
/*     */ 
/* 243 */       String dOpt = extractOption();
/* 244 */       if (dOpt != null)
/* 245 */         dateFormatStr = dOpt;
/*     */       DateFormat df;
/* 247 */       if (dateFormatStr.equalsIgnoreCase("ISO8601"))
/*     */       {
/* 249 */         df = new ISO8601DateFormat();
/* 250 */       } else if (dateFormatStr.equalsIgnoreCase("ABSOLUTE"))
/*     */       {
/* 252 */         df = new AbsoluteTimeDateFormat();
/* 253 */       } else if (dateFormatStr.equalsIgnoreCase("DATE"))
/*     */       {
/* 255 */         df = new DateTimeDateFormat();
/*     */       }
/*     */       else try {
/* 258 */           df = new SimpleDateFormat(dateFormatStr);
/*     */         }
/*     */         catch (IllegalArgumentException e) {
/* 261 */           LogLog.error("Could not instantiate SimpleDateFormat with " + dateFormatStr, e);
/*     */ 
/* 263 */           df = (DateFormat)OptionConverter.instantiateByClassName("org.apache.log4j.helpers.ISO8601DateFormat", DateFormat.class, null);
/*     */         }
/*     */ 
/*     */ 
/* 268 */       pc = new DatePatternConverter(this.formattingInfo, df);
/*     */ 
/* 271 */       this.currentLiteral.setLength(0);
/* 272 */       break;
/*     */     case 'F':
/* 274 */       pc = new LocationPatternConverter(this.formattingInfo, 1004);
/*     */ 
/* 278 */       this.currentLiteral.setLength(0);
/* 279 */       break;
/*     */     case 'l':
/* 281 */       pc = new LocationPatternConverter(this.formattingInfo, 1000);
/*     */ 
/* 285 */       this.currentLiteral.setLength(0);
/* 286 */       break;
/*     */     case 'L':
/* 288 */       pc = new LocationPatternConverter(this.formattingInfo, 1003);
/*     */ 
/* 292 */       this.currentLiteral.setLength(0);
/* 293 */       break;
/*     */     case 'm':
/* 295 */       pc = new BasicPatternConverter(this.formattingInfo, 2004);
/*     */ 
/* 298 */       this.currentLiteral.setLength(0);
/* 299 */       break;
/*     */     case 'M':
/* 301 */       pc = new LocationPatternConverter(this.formattingInfo, 1001);
/*     */ 
/* 305 */       this.currentLiteral.setLength(0);
/* 306 */       break;
/*     */     case 'p':
/* 308 */       pc = new BasicPatternConverter(this.formattingInfo, 2002);
/*     */ 
/* 311 */       this.currentLiteral.setLength(0);
/* 312 */       break;
/*     */     case 'r':
/* 314 */       pc = new BasicPatternConverter(this.formattingInfo, 2000);
/*     */ 
/* 318 */       this.currentLiteral.setLength(0);
/* 319 */       break;
/*     */     case 't':
/* 321 */       pc = new BasicPatternConverter(this.formattingInfo, 2001);
/*     */ 
/* 324 */       this.currentLiteral.setLength(0);
/* 325 */       break;
/*     */     case 'x':
/* 341 */       pc = new BasicPatternConverter(this.formattingInfo, 2003);
/*     */ 
/* 343 */       this.currentLiteral.setLength(0);
/* 344 */       break;
/*     */     case 'X':
/* 346 */       String xOpt = extractOption();
/* 347 */       pc = new MDCPatternConverter(this.formattingInfo, xOpt);
/* 348 */       this.currentLiteral.setLength(0);
/* 349 */       break;
/*     */     case 'D':
/*     */     case 'E':
/*     */     case 'G':
/*     */     case 'H':
/*     */     case 'I':
/*     */     case 'J':
/*     */     case 'K':
/*     */     case 'N':
/*     */     case 'O':
/*     */     case 'P':
/*     */     case 'Q':
/*     */     case 'R':
/*     */     case 'S':
/*     */     case 'T':
/*     */     case 'U':
/*     */     case 'V':
/*     */     case 'W':
/*     */     case 'Y':
/*     */     case 'Z':
/*     */     case '[':
/*     */     case '\\':
/*     */     case ']':
/*     */     case '^':
/*     */     case '_':
/*     */     case '`':
/*     */     case 'a':
/*     */     case 'b':
/*     */     case 'e':
/*     */     case 'f':
/*     */     case 'g':
/*     */     case 'h':
/*     */     case 'i':
/*     */     case 'j':
/*     */     case 'k':
/*     */     case 'n':
/*     */     case 'o':
/*     */     case 'q':
/*     */     case 's':
/*     */     case 'u':
/*     */     case 'v':
/*     */     case 'w':
/*     */     default:
/* 351 */       LogLog.error("Unexpected char [" + c + "] at position " + this.i + " in conversion patterrn.");
/*     */ 
/* 353 */       pc = new LiteralPatternConverter(this.currentLiteral.toString());
/* 354 */       this.currentLiteral.setLength(0);
/*     */     }
/*     */ 
/* 357 */     addConverter(pc);
/*     */   }
/*     */ 
/*     */   protected void addConverter(PatternConverter pc)
/*     */   {
/* 362 */     this.currentLiteral.setLength(0);
/*     */ 
/* 364 */     addToList(pc);
/*     */ 
/* 366 */     this.state = 0;
/*     */ 
/* 368 */     this.formattingInfo.reset();
/*     */   }
/*     */ 
/*     */   private class CategoryPatternConverter extends PatternParser.NamedPatternConverter
/*     */   {
/*     */     CategoryPatternConverter(FormattingInfo formattingInfo, int precision)
/*     */     {
/* 537 */       super(precision);
/*     */     }
/*     */ 
/*     */     String getFullyQualifiedName(LoggingEvent event) {
/* 541 */       return event.getLoggerName();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ClassNamePatternConverter extends PatternParser.NamedPatternConverter
/*     */   {
/*     */     ClassNamePatternConverter(FormattingInfo formattingInfo, int precision)
/*     */     {
/* 526 */       super(precision);
/*     */     }
/*     */ 
/*     */     String getFullyQualifiedName(LoggingEvent event) {
/* 530 */       return event.getLocationInformation().getClassName();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static abstract class NamedPatternConverter extends PatternConverter
/*     */   {
/*     */     int precision;
/*     */ 
/*     */     NamedPatternConverter(FormattingInfo formattingInfo, int precision)
/*     */     {
/* 494 */       super();
/* 495 */       this.precision = precision;
/*     */     }
/*     */ 
/*     */     abstract String getFullyQualifiedName(LoggingEvent paramLoggingEvent);
/*     */ 
/*     */     public String convert(LoggingEvent event)
/*     */     {
/* 503 */       String n = getFullyQualifiedName(event);
/* 504 */       if (this.precision <= 0) {
/* 505 */         return n;
/*     */       }
/* 507 */       int len = n.length();
/*     */ 
/* 512 */       int end = len - 1;
/* 513 */       for (int i = this.precision; i > 0; i--) {
/* 514 */         end = n.lastIndexOf('.', end - 1);
/* 515 */         if (end == -1)
/* 516 */           return n;
/*     */       }
/* 518 */       return n.substring(end + 1, len);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class LocationPatternConverter extends PatternConverter
/*     */   {
/*     */     int type;
/*     */ 
/*     */     LocationPatternConverter(FormattingInfo formattingInfo, int type)
/*     */     {
/* 469 */       super();
/* 470 */       this.type = type;
/*     */     }
/*     */ 
/*     */     public String convert(LoggingEvent event)
/*     */     {
/* 475 */       LocationInfo locationInfo = event.getLocationInformation();
/* 476 */       switch (this.type) {
/*     */       case 1000:
/* 478 */         return locationInfo.fullInfo;
/*     */       case 1001:
/* 480 */         return locationInfo.getMethodName();
/*     */       case 1003:
/* 482 */         return locationInfo.getLineNumber();
/*     */       case 1004:
/* 484 */         return locationInfo.getFileName();
/* 485 */       case 1002: } return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class MDCPatternConverter extends PatternConverter
/*     */   {
/*     */     private String key;
/*     */ 
/*     */     MDCPatternConverter(FormattingInfo formattingInfo, String key)
/*     */     {
/* 449 */       super();
/* 450 */       this.key = key;
/*     */     }
/*     */ 
/*     */     public String convert(LoggingEvent event)
/*     */     {
/* 455 */       Object val = event.getMDC(this.key);
/* 456 */       if (val == null) {
/* 457 */         return null;
/*     */       }
/* 459 */       return val.toString();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class DatePatternConverter extends PatternConverter
/*     */   {
/*     */     private DateFormat df;
/*     */     private Date date;
/*     */ 
/*     */     DatePatternConverter(FormattingInfo formattingInfo, DateFormat df)
/*     */     {
/* 426 */       super();
/* 427 */       this.date = new Date();
/* 428 */       this.df = df;
/*     */     }
/*     */ 
/*     */     public String convert(LoggingEvent event)
/*     */     {
/* 433 */       this.date.setTime(event.timeStamp);
/* 434 */       String converted = null;
/*     */       try {
/* 436 */         converted = this.df.format(this.date);
/*     */       }
/*     */       catch (Exception ex) {
/* 439 */         LogLog.error("Error occured while converting date.", ex);
/*     */       }
/* 441 */       return converted;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class LiteralPatternConverter extends PatternConverter
/*     */   {
/*     */     private String literal;
/*     */ 
/*     */     LiteralPatternConverter(String value)
/*     */     {
/* 406 */       this.literal = value;
/*     */     }
/*     */ 
/*     */     public final void format(StringBuffer sbuf, LoggingEvent event)
/*     */     {
/* 412 */       sbuf.append(this.literal);
/*     */     }
/*     */ 
/*     */     public String convert(LoggingEvent event)
/*     */     {
/* 417 */       return this.literal;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class BasicPatternConverter extends PatternConverter
/*     */   {
/*     */     int type;
/*     */ 
/*     */     BasicPatternConverter(FormattingInfo formattingInfo, int type)
/*     */     {
/* 379 */       super();
/* 380 */       this.type = type;
/*     */     }
/*     */ 
/*     */     public String convert(LoggingEvent event)
/*     */     {
/* 385 */       switch (this.type) {
/*     */       case 2000:
/* 387 */         return Long.toString(event.timeStamp - LoggingEvent.getStartTime());
/*     */       case 2001:
/* 389 */         return event.getThreadName();
/*     */       case 2002:
/* 391 */         return event.getLevel().toString();
/*     */       case 2003:
/* 393 */         return event.getNDC();
/*     */       case 2004:
/* 395 */         return event.getRenderedMessage();
/*     */       }
/* 397 */       return null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.helpers.PatternParser
 * JD-Core Version:    0.6.0
 */
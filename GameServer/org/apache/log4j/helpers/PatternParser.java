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
/*  69 */   protected StringBuffer currentLiteral = new StringBuffer(32);
/*     */   protected int patternLength;
/*     */   protected int i;
/*     */   PatternConverter head;
/*     */   PatternConverter tail;
/*  74 */   protected FormattingInfo formattingInfo = new FormattingInfo();
/*     */   protected String pattern;
/*     */ 
/*     */   public PatternParser(String pattern)
/*     */   {
/*  79 */     this.pattern = pattern;
/*  80 */     this.patternLength = pattern.length();
/*  81 */     this.state = 0;
/*     */   }
/*     */ 
/*     */   private void addToList(PatternConverter pc)
/*     */   {
/*  86 */     if (this.head == null) {
/*  87 */       this.head = (this.tail = pc);
/*     */     } else {
/*  89 */       this.tail.next = pc;
/*  90 */       this.tail = pc;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected String extractOption()
/*     */   {
/*  96 */     if ((this.i < this.patternLength) && (this.pattern.charAt(this.i) == '{')) {
/*  97 */       int end = this.pattern.indexOf('}', this.i);
/*  98 */       if (end > this.i) {
/*  99 */         String r = this.pattern.substring(this.i + 1, end);
/* 100 */         this.i = (end + 1);
/* 101 */         return r;
/*     */       }
/*     */     }
/* 104 */     return null;
/*     */   }
/*     */ 
/*     */   protected int extractPrecisionOption()
/*     */   {
/* 113 */     String opt = extractOption();
/* 114 */     int r = 0;
/* 115 */     if (opt != null) {
/*     */       try {
/* 117 */         r = Integer.parseInt(opt);
/* 118 */         if (r <= 0) {
/* 119 */           LogLog.error("Precision option (" + opt + ") isn't a positive integer.");
/*     */ 
/* 121 */           r = 0;
/*     */         }
/*     */       }
/*     */       catch (NumberFormatException e) {
/* 125 */         LogLog.error("Category option \"" + opt + "\" not a decimal integer.", e);
/*     */       }
/*     */     }
/* 128 */     return r;
/*     */   }
/*     */ 
/*     */   public PatternConverter parse()
/*     */   {
/* 134 */     this.i = 0;
/* 135 */     while (this.i < this.patternLength) {
/* 136 */       char c = this.pattern.charAt(this.i++);
/* 137 */       switch (this.state)
/*     */       {
/*     */       case 0:
/* 140 */         if (this.i == this.patternLength) {
/* 141 */           this.currentLiteral.append(c);
/* 142 */           continue;
/*     */         }
/* 144 */         if (c == '%')
/*     */         {
/* 146 */           switch (this.pattern.charAt(this.i)) {
/*     */           case '%':
/* 148 */             this.currentLiteral.append(c);
/* 149 */             this.i += 1;
/* 150 */             break;
/*     */           case 'n':
/* 152 */             this.currentLiteral.append(Layout.LINE_SEP);
/* 153 */             this.i += 1;
/* 154 */             break;
/*     */           default:
/* 156 */             if (this.currentLiteral.length() != 0) {
/* 157 */               addToList(new LiteralPatternConverter(this.currentLiteral.toString()));
/*     */             }
/*     */ 
/* 162 */             this.currentLiteral.setLength(0);
/* 163 */             this.currentLiteral.append(c);
/* 164 */             this.state = 1;
/* 165 */             this.formattingInfo.reset(); break;
/*     */           }
/*     */         }
/*     */         else {
/* 169 */           this.currentLiteral.append(c);
/*     */         }
/* 171 */         break;
/*     */       case 1:
/* 173 */         this.currentLiteral.append(c);
/* 174 */         switch (c) {
/*     */         case '-':
/* 176 */           this.formattingInfo.leftAlign = true;
/* 177 */           break;
/*     */         case '.':
/* 179 */           this.state = 3;
/* 180 */           break;
/*     */         default:
/* 182 */           if ((c >= '0') && (c <= '9')) {
/* 183 */             this.formattingInfo.min = (c - '0');
/* 184 */             this.state = 4;
/*     */           }
/*     */           else {
/* 187 */             finalizeConverter(c);
/*     */           }
/*     */         }
/* 189 */         break;
/*     */       case 4:
/* 191 */         this.currentLiteral.append(c);
/* 192 */         if ((c >= '0') && (c <= '9'))
/* 193 */           this.formattingInfo.min = (this.formattingInfo.min * 10 + (c - '0'));
/* 194 */         else if (c == '.')
/* 195 */           this.state = 3;
/*     */         else {
/* 197 */           finalizeConverter(c);
/*     */         }
/* 199 */         break;
/*     */       case 3:
/* 201 */         this.currentLiteral.append(c);
/* 202 */         if ((c >= '0') && (c <= '9')) {
/* 203 */           this.formattingInfo.max = (c - '0');
/* 204 */           this.state = 5;
/*     */         }
/*     */         else {
/* 207 */           LogLog.error("Error occured in position " + this.i + ".\n Was expecting digit, instead got char \"" + c + "\".");
/*     */ 
/* 209 */           this.state = 0;
/*     */         }
/* 211 */         break;
/*     */       case 5:
/* 213 */         this.currentLiteral.append(c);
/* 214 */         if ((c >= '0') && (c <= '9')) {
/* 215 */           this.formattingInfo.max = (this.formattingInfo.max * 10 + (c - '0'));
/*     */         } else {
/* 217 */           finalizeConverter(c);
/* 218 */           this.state = 0;
/*     */         }
/*     */       case 2:
/*     */       }
/*     */     }
/* 223 */     if (this.currentLiteral.length() != 0) {
/* 224 */       addToList(new LiteralPatternConverter(this.currentLiteral.toString()));
/*     */     }
/*     */ 
/* 227 */     return this.head;
/*     */   }
/*     */ 
/*     */   protected void finalizeConverter(char c)
/*     */   {
/* 232 */     PatternConverter pc = null;
/* 233 */     switch (c) {
/*     */     case 'c':
/* 235 */       pc = new CategoryPatternConverter(this.formattingInfo, extractPrecisionOption());
/*     */ 
/* 239 */       this.currentLiteral.setLength(0);
/* 240 */       break;
/*     */     case 'C':
/* 242 */       pc = new ClassNamePatternConverter(this.formattingInfo, extractPrecisionOption());
/*     */ 
/* 246 */       this.currentLiteral.setLength(0);
/* 247 */       break;
/*     */     case 'd':
/* 249 */       String dateFormatStr = "ISO8601";
/*     */ 
/* 251 */       String dOpt = extractOption();
/* 252 */       if (dOpt != null)
/* 253 */         dateFormatStr = dOpt;
/*     */       DateFormat df;
/*     */       DateFormat df;
/* 255 */       if (dateFormatStr.equalsIgnoreCase("ISO8601"))
/*     */       {
/* 257 */         df = new ISO8601DateFormat();
/*     */       }
/*     */       else
/*     */       {
/*     */         DateFormat df;
/* 258 */         if (dateFormatStr.equalsIgnoreCase("ABSOLUTE"))
/*     */         {
/* 260 */           df = new AbsoluteTimeDateFormat();
/*     */         }
/*     */         else
/*     */         {
/*     */           DateFormat df;
/* 261 */           if (dateFormatStr.equalsIgnoreCase("DATE"))
/*     */           {
/* 263 */             df = new DateTimeDateFormat();
/*     */           }
/*     */           else try {
/* 266 */               df = new SimpleDateFormat(dateFormatStr);
/*     */             }
/*     */             catch (IllegalArgumentException e) {
/* 269 */               LogLog.error("Could not instantiate SimpleDateFormat with " + dateFormatStr, e);
/*     */ 
/* 271 */               df = (DateFormat)OptionConverter.instantiateByClassName("org.apache.log4j.helpers.ISO8601DateFormat", DateFormat.class, null);
/*     */             }
/*     */         }
/*     */       }
/*     */ 
/* 276 */       pc = new DatePatternConverter(this.formattingInfo, df);
/*     */ 
/* 279 */       this.currentLiteral.setLength(0);
/* 280 */       break;
/*     */     case 'F':
/* 282 */       pc = new LocationPatternConverter(this.formattingInfo, 1004);
/*     */ 
/* 286 */       this.currentLiteral.setLength(0);
/* 287 */       break;
/*     */     case 'l':
/* 289 */       pc = new LocationPatternConverter(this.formattingInfo, 1000);
/*     */ 
/* 293 */       this.currentLiteral.setLength(0);
/* 294 */       break;
/*     */     case 'L':
/* 296 */       pc = new LocationPatternConverter(this.formattingInfo, 1003);
/*     */ 
/* 300 */       this.currentLiteral.setLength(0);
/* 301 */       break;
/*     */     case 'm':
/* 303 */       pc = new BasicPatternConverter(this.formattingInfo, 2004);
/*     */ 
/* 306 */       this.currentLiteral.setLength(0);
/* 307 */       break;
/*     */     case 'M':
/* 309 */       pc = new LocationPatternConverter(this.formattingInfo, 1001);
/*     */ 
/* 313 */       this.currentLiteral.setLength(0);
/* 314 */       break;
/*     */     case 'p':
/* 316 */       pc = new BasicPatternConverter(this.formattingInfo, 2002);
/*     */ 
/* 319 */       this.currentLiteral.setLength(0);
/* 320 */       break;
/*     */     case 'r':
/* 322 */       pc = new BasicPatternConverter(this.formattingInfo, 2000);
/*     */ 
/* 326 */       this.currentLiteral.setLength(0);
/* 327 */       break;
/*     */     case 't':
/* 329 */       pc = new BasicPatternConverter(this.formattingInfo, 2001);
/*     */ 
/* 332 */       this.currentLiteral.setLength(0);
/* 333 */       break;
/*     */     case 'x':
/* 349 */       pc = new BasicPatternConverter(this.formattingInfo, 2003);
/*     */ 
/* 351 */       this.currentLiteral.setLength(0);
/* 352 */       break;
/*     */     case 'X':
/* 354 */       String xOpt = extractOption();
/* 355 */       pc = new MDCPatternConverter(this.formattingInfo, xOpt);
/* 356 */       this.currentLiteral.setLength(0);
/* 357 */       break;
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
/* 359 */       LogLog.error("Unexpected char [" + c + "] at position " + this.i + " in conversion patterrn.");
/*     */ 
/* 361 */       pc = new LiteralPatternConverter(this.currentLiteral.toString());
/* 362 */       this.currentLiteral.setLength(0);
/*     */     }
/*     */ 
/* 365 */     addConverter(pc);
/*     */   }
/*     */ 
/*     */   protected void addConverter(PatternConverter pc)
/*     */   {
/* 370 */     this.currentLiteral.setLength(0);
/*     */ 
/* 372 */     addToList(pc);
/*     */ 
/* 374 */     this.state = 0;
/*     */ 
/* 376 */     this.formattingInfo.reset();
/*     */   }
/*     */ 
/*     */   private class CategoryPatternConverter extends PatternParser.NamedPatternConverter
/*     */   {
/*     */     CategoryPatternConverter(FormattingInfo formattingInfo, int precision)
/*     */     {
/* 545 */       super(precision);
/*     */     }
/*     */ 
/*     */     String getFullyQualifiedName(LoggingEvent event) {
/* 549 */       return event.getLoggerName();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ClassNamePatternConverter extends PatternParser.NamedPatternConverter
/*     */   {
/*     */     ClassNamePatternConverter(FormattingInfo formattingInfo, int precision)
/*     */     {
/* 534 */       super(precision);
/*     */     }
/*     */ 
/*     */     String getFullyQualifiedName(LoggingEvent event) {
/* 538 */       return event.getLocationInformation().getClassName();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static abstract class NamedPatternConverter extends PatternConverter
/*     */   {
/*     */     int precision;
/*     */ 
/*     */     NamedPatternConverter(FormattingInfo formattingInfo, int precision)
/*     */     {
/* 502 */       super();
/* 503 */       this.precision = precision;
/*     */     }
/*     */ 
/*     */     abstract String getFullyQualifiedName(LoggingEvent paramLoggingEvent);
/*     */ 
/*     */     public String convert(LoggingEvent event)
/*     */     {
/* 511 */       String n = getFullyQualifiedName(event);
/* 512 */       if (this.precision <= 0) {
/* 513 */         return n;
/*     */       }
/* 515 */       int len = n.length();
/*     */ 
/* 520 */       int end = len - 1;
/* 521 */       for (int i = this.precision; i > 0; i--) {
/* 522 */         end = n.lastIndexOf('.', end - 1);
/* 523 */         if (end == -1)
/* 524 */           return n;
/*     */       }
/* 526 */       return n.substring(end + 1, len);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class LocationPatternConverter extends PatternConverter
/*     */   {
/*     */     int type;
/*     */ 
/*     */     LocationPatternConverter(FormattingInfo formattingInfo, int type)
/*     */     {
/* 477 */       super();
/* 478 */       this.type = type;
/*     */     }
/*     */ 
/*     */     public String convert(LoggingEvent event)
/*     */     {
/* 483 */       LocationInfo locationInfo = event.getLocationInformation();
/* 484 */       switch (this.type) {
/*     */       case 1000:
/* 486 */         return locationInfo.fullInfo;
/*     */       case 1001:
/* 488 */         return locationInfo.getMethodName();
/*     */       case 1003:
/* 490 */         return locationInfo.getLineNumber();
/*     */       case 1004:
/* 492 */         return locationInfo.getFileName();
/* 493 */       case 1002: } return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class MDCPatternConverter extends PatternConverter
/*     */   {
/*     */     private String key;
/*     */ 
/*     */     MDCPatternConverter(FormattingInfo formattingInfo, String key)
/*     */     {
/* 457 */       super();
/* 458 */       this.key = key;
/*     */     }
/*     */ 
/*     */     public String convert(LoggingEvent event)
/*     */     {
/* 463 */       Object val = event.getMDC(this.key);
/* 464 */       if (val == null) {
/* 465 */         return null;
/*     */       }
/* 467 */       return val.toString();
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
/* 434 */       super();
/* 435 */       this.date = new Date();
/* 436 */       this.df = df;
/*     */     }
/*     */ 
/*     */     public String convert(LoggingEvent event)
/*     */     {
/* 441 */       this.date.setTime(event.timeStamp);
/* 442 */       String converted = null;
/*     */       try {
/* 444 */         converted = this.df.format(this.date);
/*     */       }
/*     */       catch (Exception ex) {
/* 447 */         LogLog.error("Error occured while converting date.", ex);
/*     */       }
/* 449 */       return converted;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class LiteralPatternConverter extends PatternConverter
/*     */   {
/*     */     private String literal;
/*     */ 
/*     */     LiteralPatternConverter(String value)
/*     */     {
/* 414 */       this.literal = value;
/*     */     }
/*     */ 
/*     */     public final void format(StringBuffer sbuf, LoggingEvent event)
/*     */     {
/* 420 */       sbuf.append(this.literal);
/*     */     }
/*     */ 
/*     */     public String convert(LoggingEvent event)
/*     */     {
/* 425 */       return this.literal;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class BasicPatternConverter extends PatternConverter
/*     */   {
/*     */     int type;
/*     */ 
/*     */     BasicPatternConverter(FormattingInfo formattingInfo, int type)
/*     */     {
/* 387 */       super();
/* 388 */       this.type = type;
/*     */     }
/*     */ 
/*     */     public String convert(LoggingEvent event)
/*     */     {
/* 393 */       switch (this.type) {
/*     */       case 2000:
/* 395 */         return Long.toString(event.timeStamp - LoggingEvent.getStartTime());
/*     */       case 2001:
/* 397 */         return event.getThreadName();
/*     */       case 2002:
/* 399 */         return event.getLevel().toString();
/*     */       case 2003:
/* 401 */         return event.getNDC();
/*     */       case 2004:
/* 403 */         return event.getRenderedMessage();
/*     */       }
/* 405 */       return null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.helpers.PatternParser
 * JD-Core Version:    0.6.0
 */
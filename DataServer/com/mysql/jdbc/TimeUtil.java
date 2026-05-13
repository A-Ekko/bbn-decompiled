/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.Map;
/*      */ import java.util.TimeZone;
/*      */ 
/*      */ public class TimeUtil
/*      */ {
/*      */   static final Map ABBREVIATED_TIMEZONES;
/*   46 */   static final TimeZone GMT_TIMEZONE = TimeZone.getTimeZone("GMT");
/*      */   static final Map TIMEZONE_MAPPINGS;
/*      */ 
/*      */   public static Time changeTimezone(Connection conn, Time t, TimeZone fromTz, TimeZone toTz, boolean rollForward)
/*      */   {
/*  812 */     if ((conn != null) && (conn.getUseTimezone()))
/*      */     {
/*  814 */       Calendar fromCal = Calendar.getInstance(fromTz);
/*  815 */       fromCal.setTime(t);
/*      */ 
/*  817 */       int fromOffset = fromCal.get(15) + fromCal.get(16);
/*      */ 
/*  819 */       Calendar toCal = Calendar.getInstance(toTz);
/*  820 */       toCal.setTime(t);
/*      */ 
/*  822 */       int toOffset = toCal.get(15) + toCal.get(16);
/*      */ 
/*  824 */       int offsetDiff = fromOffset - toOffset;
/*  825 */       long toTime = toCal.getTime().getTime();
/*      */ 
/*  827 */       if ((rollForward) || ((conn.isServerTzUTC()) && (!conn.isClientTzUTC())))
/*  828 */         toTime += offsetDiff;
/*      */       else {
/*  830 */         toTime -= offsetDiff;
/*      */       }
/*      */ 
/*  833 */       Time changedTime = new Time(toTime);
/*      */ 
/*  835 */       return changedTime;
/*      */     }
/*      */ 
/*  838 */     return t;
/*      */   }
/*      */ 
/*      */   public static Timestamp changeTimezone(Connection conn, Timestamp tstamp, TimeZone fromTz, TimeZone toTz, boolean rollForward)
/*      */   {
/*  857 */     if ((conn != null) && (conn.getUseTimezone()))
/*      */     {
/*  859 */       Calendar fromCal = Calendar.getInstance(fromTz);
/*  860 */       fromCal.setTime(tstamp);
/*      */ 
/*  862 */       int fromOffset = fromCal.get(15) + fromCal.get(16);
/*      */ 
/*  864 */       Calendar toCal = Calendar.getInstance(toTz);
/*  865 */       toCal.setTime(tstamp);
/*      */ 
/*  867 */       int toOffset = toCal.get(15) + toCal.get(16);
/*      */ 
/*  869 */       int offsetDiff = fromOffset - toOffset;
/*  870 */       long toTime = toCal.getTime().getTime();
/*      */ 
/*  872 */       if ((rollForward) || ((conn.isServerTzUTC()) && (!conn.isClientTzUTC())))
/*  873 */         toTime += offsetDiff;
/*      */       else {
/*  875 */         toTime -= offsetDiff;
/*      */       }
/*      */ 
/*  878 */       Timestamp changedTimestamp = new Timestamp(toTime);
/*      */ 
/*  880 */       return changedTimestamp;
/*      */     }
/*      */ 
/*  883 */     return tstamp;
/*      */   }
/*      */ 
/*      */   static final java.sql.Date fastDateCreate(Calendar cal, int year, int month, int day)
/*      */   {
/*  891 */     cal.clear();
/*      */ 
/*  896 */     cal.set(year, month - 1, day, 0, 0, 0);
/*      */ 
/*  898 */     long dateAsMillis = 0L;
/*      */     try
/*      */     {
/*  901 */       dateAsMillis = cal.getTimeInMillis();
/*      */     }
/*      */     catch (IllegalAccessError iae) {
/*  904 */       dateAsMillis = cal.getTime().getTime();
/*      */     }
/*      */ 
/*  907 */     return new java.sql.Date(dateAsMillis);
/*      */   }
/*      */ 
/*      */   static final Time fastTimeCreate(Calendar cal, int hour, int minute, int second)
/*      */   {
/*  912 */     cal.clear();
/*      */ 
/*  915 */     cal.set(1970, 0, 1, hour, minute, second);
/*      */ 
/*  917 */     long timeAsMillis = 0L;
/*      */     try
/*      */     {
/*  920 */       timeAsMillis = cal.getTimeInMillis();
/*      */     }
/*      */     catch (IllegalAccessError iae) {
/*  923 */       timeAsMillis = cal.getTime().getTime();
/*      */     }
/*      */ 
/*  926 */     return new Time(timeAsMillis);
/*      */   }
/*      */ 
/*      */   static final Timestamp fastTimestampCreate(Calendar cal, int year, int month, int day, int hour, int minute, int seconds, int secondsPart)
/*      */   {
/*  932 */     cal.clear();
/*      */ 
/*  937 */     cal.set(year, month - 1, day, hour, minute, seconds);
/*      */ 
/*  939 */     long tsAsMillis = 0L;
/*      */     try
/*      */     {
/*  942 */       tsAsMillis = cal.getTimeInMillis();
/*      */     }
/*      */     catch (IllegalAccessError iae) {
/*  945 */       tsAsMillis = cal.getTime().getTime();
/*      */     }
/*      */ 
/*  948 */     Timestamp ts = new Timestamp(tsAsMillis);
/*  949 */     ts.setNanos(secondsPart);
/*      */ 
/*  951 */     return ts;
/*      */   }
/*      */ 
/*      */   public static String getCanoncialTimezone(String timezoneStr)
/*      */   {
/*  966 */     if (timezoneStr == null) {
/*  967 */       return null;
/*      */     }
/*      */ 
/*  970 */     timezoneStr = timezoneStr.trim();
/*      */ 
/*  974 */     int daylightIndex = StringUtils.indexOfIgnoreCase(timezoneStr, "DAYLIGHT");
/*      */ 
/*  977 */     if (daylightIndex != -1) {
/*  978 */       StringBuffer timezoneBuf = new StringBuffer();
/*  979 */       timezoneBuf.append(timezoneStr.substring(0, daylightIndex));
/*  980 */       timezoneBuf.append("Standard");
/*  981 */       timezoneBuf.append(timezoneStr.substring(daylightIndex + "DAYLIGHT".length(), timezoneStr.length()));
/*      */ 
/*  983 */       timezoneStr = timezoneBuf.toString();
/*      */     }
/*      */ 
/*  986 */     String canonicalTz = (String)TIMEZONE_MAPPINGS.get(timezoneStr);
/*      */ 
/*  989 */     if (canonicalTz == null) {
/*  990 */       String[] abbreviatedTimezone = (String[])ABBREVIATED_TIMEZONES.get(timezoneStr);
/*      */ 
/*  993 */       if (abbreviatedTimezone != null)
/*      */       {
/*  995 */         if (abbreviatedTimezone.length == 1) {
/*  996 */           canonicalTz = abbreviatedTimezone[0];
/*      */         } else {
/*  998 */           StringBuffer errorMsg = new StringBuffer("The server timezone value '");
/*      */ 
/* 1000 */           errorMsg.append(timezoneStr);
/* 1001 */           errorMsg.append("' represents more than one timezone. You must ");
/*      */ 
/* 1003 */           errorMsg.append("configure either the server or client to use a ");
/*      */ 
/* 1005 */           errorMsg.append("more specifc timezone value if you want to enable ");
/*      */ 
/* 1007 */           errorMsg.append("timezone support. The timezones that '");
/* 1008 */           errorMsg.append(timezoneStr);
/* 1009 */           errorMsg.append("' maps to are: ");
/* 1010 */           errorMsg.append(abbreviatedTimezone[0]);
/*      */ 
/* 1012 */           for (int i = 1; i < abbreviatedTimezone.length; i++) {
/* 1013 */             errorMsg.append(", ");
/* 1014 */             errorMsg.append(abbreviatedTimezone[i]);
/*      */           }
/*      */ 
/* 1017 */           throw new IllegalArgumentException(errorMsg.toString());
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1022 */     return canonicalTz;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   51 */     HashMap tempMap = new HashMap();
/*      */ 
/*   56 */     tempMap.put("Romance", "Europe/Paris");
/*   57 */     tempMap.put("Romance Standard Time", "Europe/Paris");
/*   58 */     tempMap.put("Warsaw", "Europe/Warsaw");
/*   59 */     tempMap.put("Central Europe", "Europe/Prague");
/*   60 */     tempMap.put("Central Europe Standard Time", "Europe/Prague");
/*   61 */     tempMap.put("Prague Bratislava", "Europe/Prague");
/*   62 */     tempMap.put("W. Central Africa Standard Time", "Africa/Luanda");
/*   63 */     tempMap.put("FLE", "Europe/Helsinki");
/*   64 */     tempMap.put("FLE Standard Time", "Europe/Helsinki");
/*   65 */     tempMap.put("GFT", "Europe/Athens");
/*   66 */     tempMap.put("GFT Standard Time", "Europe/Athens");
/*   67 */     tempMap.put("GTB", "Europe/Athens");
/*   68 */     tempMap.put("GTB Standard Time", "Europe/Athens");
/*   69 */     tempMap.put("Israel", "Asia/Jerusalem");
/*   70 */     tempMap.put("Israel Standard Time", "Asia/Jerusalem");
/*   71 */     tempMap.put("Arab", "Asia/Riyadh");
/*   72 */     tempMap.put("Arab Standard Time", "Asia/Riyadh");
/*   73 */     tempMap.put("Arabic Standard Time", "Asia/Baghdad");
/*   74 */     tempMap.put("E. Africa", "Africa/Nairobi");
/*   75 */     tempMap.put("E. Africa Standard Time", "Africa/Nairobi");
/*   76 */     tempMap.put("Saudi Arabia", "Asia/Riyadh");
/*   77 */     tempMap.put("Saudi Arabia Standard Time", "Asia/Riyadh");
/*   78 */     tempMap.put("Iran", "Asia/Tehran");
/*   79 */     tempMap.put("Iran Standard Time", "Asia/Tehran");
/*   80 */     tempMap.put("Afghanistan", "Asia/Kabul");
/*   81 */     tempMap.put("Afghanistan Standard Time", "Asia/Kabul");
/*   82 */     tempMap.put("India", "Asia/Calcutta");
/*   83 */     tempMap.put("India Standard Time", "Asia/Calcutta");
/*   84 */     tempMap.put("Myanmar Standard Time", "Asia/Rangoon");
/*   85 */     tempMap.put("Nepal Standard Time", "Asia/Katmandu");
/*   86 */     tempMap.put("Sri Lanka", "Asia/Colombo");
/*   87 */     tempMap.put("Sri Lanka Standard Time", "Asia/Colombo");
/*   88 */     tempMap.put("Beijing", "Asia/Shanghai");
/*   89 */     tempMap.put("China", "Asia/Shanghai");
/*   90 */     tempMap.put("China Standard Time", "Asia/Shanghai");
/*   91 */     tempMap.put("AUS Central", "Australia/Darwin");
/*   92 */     tempMap.put("AUS Central Standard Time", "Australia/Darwin");
/*   93 */     tempMap.put("Cen. Australia", "Australia/Adelaide");
/*   94 */     tempMap.put("Cen. Australia Standard Time", "Australia/Adelaide");
/*   95 */     tempMap.put("Vladivostok", "Asia/Vladivostok");
/*   96 */     tempMap.put("Vladivostok Standard Time", "Asia/Vladivostok");
/*   97 */     tempMap.put("West Pacific", "Pacific/Guam");
/*   98 */     tempMap.put("West Pacific Standard Time", "Pacific/Guam");
/*   99 */     tempMap.put("E. South America", "America/Sao_Paulo");
/*  100 */     tempMap.put("E. South America Standard Time", "America/Sao_Paulo");
/*  101 */     tempMap.put("Greenland Standard Time", "America/Godthab");
/*  102 */     tempMap.put("Newfoundland", "America/St_Johns");
/*  103 */     tempMap.put("Newfoundland Standard Time", "America/St_Johns");
/*  104 */     tempMap.put("Pacific SA", "America/Caracas");
/*  105 */     tempMap.put("Pacific SA Standard Time", "America/Caracas");
/*  106 */     tempMap.put("SA Western", "America/Caracas");
/*  107 */     tempMap.put("SA Western Standard Time", "America/Caracas");
/*  108 */     tempMap.put("SA Pacific", "America/Bogota");
/*  109 */     tempMap.put("SA Pacific Standard Time", "America/Bogota");
/*  110 */     tempMap.put("US Eastern", "America/Indianapolis");
/*  111 */     tempMap.put("US Eastern Standard Time", "America/Indianapolis");
/*  112 */     tempMap.put("Central America Standard Time", "America/Regina");
/*  113 */     tempMap.put("Mexico", "America/Mexico_City");
/*  114 */     tempMap.put("Mexico Standard Time", "America/Mexico_City");
/*  115 */     tempMap.put("Canada Central", "America/Regina");
/*  116 */     tempMap.put("Canada Central Standard Time", "America/Regina");
/*  117 */     tempMap.put("US Mountain", "America/Phoenix");
/*  118 */     tempMap.put("US Mountain Standard Time", "America/Phoenix");
/*  119 */     tempMap.put("GMT", "Europe/London");
/*  120 */     tempMap.put("GMT Standard Time", "Europe/London");
/*  121 */     tempMap.put("Ekaterinburg", "Asia/Yekaterinburg");
/*  122 */     tempMap.put("Ekaterinburg Standard Time", "Asia/Yekaterinburg");
/*  123 */     tempMap.put("West Asia", "Asia/Karachi");
/*  124 */     tempMap.put("West Asia Standard Time", "Asia/Karachi");
/*  125 */     tempMap.put("Central Asia", "Asia/Dhaka");
/*  126 */     tempMap.put("Central Asia Standard Time", "Asia/Dhaka");
/*  127 */     tempMap.put("N. Central Asia Standard Time", "Asia/Novosibirsk");
/*  128 */     tempMap.put("Bangkok", "Asia/Bangkok");
/*  129 */     tempMap.put("Bangkok Standard Time", "Asia/Bangkok");
/*  130 */     tempMap.put("North Asia Standard Time", "Asia/Krasnoyarsk");
/*  131 */     tempMap.put("SE Asia", "Asia/Bangkok");
/*  132 */     tempMap.put("SE Asia Standard Time", "Asia/Bangkok");
/*  133 */     tempMap.put("North Asia East Standard Time", "Asia/Ulaanbaatar");
/*  134 */     tempMap.put("Singapore", "Asia/Singapore");
/*  135 */     tempMap.put("Singapore Standard Time", "Asia/Singapore");
/*  136 */     tempMap.put("Taipei", "Asia/Taipei");
/*  137 */     tempMap.put("Taipei Standard Time", "Asia/Taipei");
/*  138 */     tempMap.put("W. Australia", "Australia/Perth");
/*  139 */     tempMap.put("W. Australia Standard Time", "Australia/Perth");
/*  140 */     tempMap.put("Korea", "Asia/Seoul");
/*  141 */     tempMap.put("Korea Standard Time", "Asia/Seoul");
/*  142 */     tempMap.put("Tokyo", "Asia/Tokyo");
/*  143 */     tempMap.put("Tokyo Standard Time", "Asia/Tokyo");
/*  144 */     tempMap.put("Yakutsk", "Asia/Yakutsk");
/*  145 */     tempMap.put("Yakutsk Standard Time", "Asia/Yakutsk");
/*  146 */     tempMap.put("Central European", "Europe/Belgrade");
/*  147 */     tempMap.put("Central European Standard Time", "Europe/Belgrade");
/*  148 */     tempMap.put("W. Europe", "Europe/Berlin");
/*  149 */     tempMap.put("W. Europe Standard Time", "Europe/Berlin");
/*  150 */     tempMap.put("Tasmania", "Australia/Hobart");
/*  151 */     tempMap.put("Tasmania Standard Time", "Australia/Hobart");
/*  152 */     tempMap.put("AUS Eastern", "Australia/Sydney");
/*  153 */     tempMap.put("AUS Eastern Standard Time", "Australia/Sydney");
/*  154 */     tempMap.put("E. Australia", "Australia/Brisbane");
/*  155 */     tempMap.put("E. Australia Standard Time", "Australia/Brisbane");
/*  156 */     tempMap.put("Sydney Standard Time", "Australia/Sydney");
/*  157 */     tempMap.put("Central Pacific", "Pacific/Guadalcanal");
/*  158 */     tempMap.put("Central Pacific Standard Time", "Pacific/Guadalcanal");
/*  159 */     tempMap.put("Dateline", "Pacific/Majuro");
/*  160 */     tempMap.put("Dateline Standard Time", "Pacific/Majuro");
/*  161 */     tempMap.put("Fiji", "Pacific/Fiji");
/*  162 */     tempMap.put("Fiji Standard Time", "Pacific/Fiji");
/*  163 */     tempMap.put("Samoa", "Pacific/Apia");
/*  164 */     tempMap.put("Samoa Standard Time", "Pacific/Apia");
/*  165 */     tempMap.put("Hawaiian", "Pacific/Honolulu");
/*  166 */     tempMap.put("Hawaiian Standard Time", "Pacific/Honolulu");
/*  167 */     tempMap.put("Alaskan", "America/Anchorage");
/*  168 */     tempMap.put("Alaskan Standard Time", "America/Anchorage");
/*  169 */     tempMap.put("Pacific", "America/Los_Angeles");
/*  170 */     tempMap.put("Pacific Standard Time", "America/Los_Angeles");
/*  171 */     tempMap.put("Mexico Standard Time 2", "America/Chihuahua");
/*  172 */     tempMap.put("Mountain", "America/Denver");
/*  173 */     tempMap.put("Mountain Standard Time", "America/Denver");
/*  174 */     tempMap.put("Central", "America/Chicago");
/*  175 */     tempMap.put("Central Standard Time", "America/Chicago");
/*  176 */     tempMap.put("Eastern", "America/New_York");
/*  177 */     tempMap.put("Eastern Standard Time", "America/New_York");
/*  178 */     tempMap.put("E. Europe", "Europe/Bucharest");
/*  179 */     tempMap.put("E. Europe Standard Time", "Europe/Bucharest");
/*  180 */     tempMap.put("Egypt", "Africa/Cairo");
/*  181 */     tempMap.put("Egypt Standard Time", "Africa/Cairo");
/*  182 */     tempMap.put("South Africa", "Africa/Harare");
/*  183 */     tempMap.put("South Africa Standard Time", "Africa/Harare");
/*  184 */     tempMap.put("Atlantic", "America/Halifax");
/*  185 */     tempMap.put("Atlantic Standard Time", "America/Halifax");
/*  186 */     tempMap.put("SA Eastern", "America/Buenos_Aires");
/*  187 */     tempMap.put("SA Eastern Standard Time", "America/Buenos_Aires");
/*  188 */     tempMap.put("Mid-Atlantic", "Atlantic/South_Georgia");
/*  189 */     tempMap.put("Mid-Atlantic Standard Time", "Atlantic/South_Georgia");
/*  190 */     tempMap.put("Azores", "Atlantic/Azores");
/*  191 */     tempMap.put("Azores Standard Time", "Atlantic/Azores");
/*  192 */     tempMap.put("Cape Verde Standard Time", "Atlantic/Cape_Verde");
/*  193 */     tempMap.put("Russian", "Europe/Moscow");
/*  194 */     tempMap.put("Russian Standard Time", "Europe/Moscow");
/*  195 */     tempMap.put("New Zealand", "Pacific/Auckland");
/*  196 */     tempMap.put("New Zealand Standard Time", "Pacific/Auckland");
/*  197 */     tempMap.put("Tonga Standard Time", "Pacific/Tongatapu");
/*  198 */     tempMap.put("Arabian", "Asia/Muscat");
/*  199 */     tempMap.put("Arabian Standard Time", "Asia/Muscat");
/*  200 */     tempMap.put("Caucasus", "Asia/Tbilisi");
/*  201 */     tempMap.put("Caucasus Standard Time", "Asia/Tbilisi");
/*  202 */     tempMap.put("GMT Standard Time", "GMT");
/*  203 */     tempMap.put("Greenwich", "GMT");
/*  204 */     tempMap.put("Greenwich Standard Time", "GMT");
/*  205 */     tempMap.put("UTC", "GMT");
/*      */ 
/*  207 */     TIMEZONE_MAPPINGS = Collections.unmodifiableMap(tempMap);
/*      */ 
/*  212 */     tempMap = new HashMap();
/*      */ 
/*  214 */     tempMap.put("ACST", new String[] { "America/Porto_Acre" });
/*  215 */     tempMap.put("ACT", new String[] { "America/Porto_Acre" });
/*  216 */     tempMap.put("ADDT", new String[] { "America/Pangnirtung" });
/*  217 */     tempMap.put("ADMT", new String[] { "Africa/Asmera", "Africa/Addis_Ababa" });
/*      */ 
/*  219 */     tempMap.put("ADT", new String[] { "Atlantic/Bermuda", "Asia/Baghdad", "America/Thule", "America/Goose_Bay", "America/Halifax", "America/Glace_Bay", "America/Pangnirtung", "America/Barbados", "America/Martinique" });
/*      */ 
/*  223 */     tempMap.put("AFT", new String[] { "Asia/Kabul" });
/*  224 */     tempMap.put("AHDT", new String[] { "America/Anchorage" });
/*  225 */     tempMap.put("AHST", new String[] { "America/Anchorage" });
/*  226 */     tempMap.put("AHWT", new String[] { "America/Anchorage" });
/*  227 */     tempMap.put("AKDT", new String[] { "America/Juneau", "America/Yakutat", "America/Anchorage", "America/Nome" });
/*      */ 
/*  229 */     tempMap.put("AKST", new String[] { "Asia/Aqtobe", "America/Juneau", "America/Yakutat", "America/Anchorage", "America/Nome" });
/*      */ 
/*  231 */     tempMap.put("AKT", new String[] { "Asia/Aqtobe" });
/*  232 */     tempMap.put("AKTST", new String[] { "Asia/Aqtobe" });
/*  233 */     tempMap.put("AKWT", new String[] { "America/Juneau", "America/Yakutat", "America/Anchorage", "America/Nome" });
/*      */ 
/*  235 */     tempMap.put("ALMST", new String[] { "Asia/Almaty" });
/*  236 */     tempMap.put("ALMT", new String[] { "Asia/Almaty" });
/*  237 */     tempMap.put("AMST", new String[] { "Asia/Yerevan", "America/Cuiaba", "America/Porto_Velho", "America/Boa_Vista", "America/Manaus" });
/*      */ 
/*  239 */     tempMap.put("AMT", new String[] { "Europe/Athens", "Europe/Amsterdam", "Asia/Yerevan", "Africa/Asmera", "America/Cuiaba", "America/Porto_Velho", "America/Boa_Vista", "America/Manaus", "America/Asuncion" });
/*      */ 
/*  243 */     tempMap.put("ANAMT", new String[] { "Asia/Anadyr" });
/*  244 */     tempMap.put("ANAST", new String[] { "Asia/Anadyr" });
/*  245 */     tempMap.put("ANAT", new String[] { "Asia/Anadyr" });
/*  246 */     tempMap.put("ANT", new String[] { "America/Aruba", "America/Curacao" });
/*  247 */     tempMap.put("AQTST", new String[] { "Asia/Aqtobe", "Asia/Aqtau" });
/*  248 */     tempMap.put("AQTT", new String[] { "Asia/Aqtobe", "Asia/Aqtau" });
/*  249 */     tempMap.put("ARST", new String[] { "Antarctica/Palmer", "America/Buenos_Aires", "America/Rosario", "America/Cordoba", "America/Jujuy", "America/Catamarca", "America/Mendoza" });
/*      */ 
/*  252 */     tempMap.put("ART", new String[] { "Antarctica/Palmer", "America/Buenos_Aires", "America/Rosario", "America/Cordoba", "America/Jujuy", "America/Catamarca", "America/Mendoza" });
/*      */ 
/*  255 */     tempMap.put("ASHST", new String[] { "Asia/Ashkhabad" });
/*  256 */     tempMap.put("ASHT", new String[] { "Asia/Ashkhabad" });
/*  257 */     tempMap.put("AST", new String[] { "Atlantic/Bermuda", "Asia/Bahrain", "Asia/Baghdad", "Asia/Kuwait", "Asia/Qatar", "Asia/Riyadh", "Asia/Aden", "America/Thule", "America/Goose_Bay", "America/Halifax", "America/Glace_Bay", "America/Pangnirtung", "America/Anguilla", "America/Antigua", "America/Barbados", "America/Dominica", "America/Santo_Domingo", "America/Grenada", "America/Guadeloupe", "America/Martinique", "America/Montserrat", "America/Puerto_Rico", "America/St_Kitts", "America/St_Lucia", "America/Miquelon", "America/St_Vincent", "America/Tortola", "America/St_Thomas", "America/Aruba", "America/Curacao", "America/Port_of_Spain" });
/*      */ 
/*  268 */     tempMap.put("AWT", new String[] { "America/Puerto_Rico" });
/*  269 */     tempMap.put("AZOST", new String[] { "Atlantic/Azores" });
/*  270 */     tempMap.put("AZOT", new String[] { "Atlantic/Azores" });
/*  271 */     tempMap.put("AZST", new String[] { "Asia/Baku" });
/*  272 */     tempMap.put("AZT", new String[] { "Asia/Baku" });
/*  273 */     tempMap.put("BAKST", new String[] { "Asia/Baku" });
/*  274 */     tempMap.put("BAKT", new String[] { "Asia/Baku" });
/*  275 */     tempMap.put("BDT", new String[] { "Asia/Dacca", "America/Nome", "America/Adak" });
/*      */ 
/*  277 */     tempMap.put("BEAT", new String[] { "Africa/Nairobi", "Africa/Mogadishu", "Africa/Kampala" });
/*      */ 
/*  279 */     tempMap.put("BEAUT", new String[] { "Africa/Nairobi", "Africa/Dar_es_Salaam", "Africa/Kampala" });
/*      */ 
/*  281 */     tempMap.put("BMT", new String[] { "Europe/Brussels", "Europe/Chisinau", "Europe/Tiraspol", "Europe/Bucharest", "Europe/Zurich", "Asia/Baghdad", "Asia/Bangkok", "Africa/Banjul", "America/Barbados", "America/Bogota" });
/*      */ 
/*  285 */     tempMap.put("BNT", new String[] { "Asia/Brunei" });
/*  286 */     tempMap.put("BORT", new String[] { "Asia/Ujung_Pandang", "Asia/Kuching" });
/*      */ 
/*  288 */     tempMap.put("BOST", new String[] { "America/La_Paz" });
/*  289 */     tempMap.put("BOT", new String[] { "America/La_Paz" });
/*  290 */     tempMap.put("BRST", new String[] { "America/Belem", "America/Fortaleza", "America/Araguaina", "America/Maceio", "America/Sao_Paulo" });
/*      */ 
/*  293 */     tempMap.put("BRT", new String[] { "America/Belem", "America/Fortaleza", "America/Araguaina", "America/Maceio", "America/Sao_Paulo" });
/*      */ 
/*  295 */     tempMap.put("BST", new String[] { "Europe/London", "Europe/Belfast", "Europe/Dublin", "Europe/Gibraltar", "Pacific/Pago_Pago", "Pacific/Midway", "America/Nome", "America/Adak" });
/*      */ 
/*  298 */     tempMap.put("BTT", new String[] { "Asia/Thimbu" });
/*  299 */     tempMap.put("BURT", new String[] { "Asia/Dacca", "Asia/Rangoon", "Asia/Calcutta" });
/*      */ 
/*  301 */     tempMap.put("BWT", new String[] { "America/Nome", "America/Adak" });
/*  302 */     tempMap.put("CANT", new String[] { "Atlantic/Canary" });
/*  303 */     tempMap.put("CAST", new String[] { "Africa/Gaborone", "Africa/Khartoum" });
/*      */ 
/*  305 */     tempMap.put("CAT", new String[] { "Africa/Gaborone", "Africa/Bujumbura", "Africa/Lubumbashi", "Africa/Blantyre", "Africa/Maputo", "Africa/Windhoek", "Africa/Kigali", "Africa/Khartoum", "Africa/Lusaka", "Africa/Harare", "America/Anchorage" });
/*      */ 
/*  310 */     tempMap.put("CCT", new String[] { "Indian/Cocos" });
/*  311 */     tempMap.put("CDDT", new String[] { "America/Rankin_Inlet" });
/*  312 */     tempMap.put("CDT", new String[] { "Asia/Harbin", "Asia/Shanghai", "Asia/Chungking", "Asia/Urumqi", "Asia/Kashgar", "Asia/Taipei", "Asia/Macao", "America/Chicago", "America/Indianapolis", "America/Indiana/Marengo", "America/Indiana/Knox", "America/Indiana/Vevay", "America/Louisville", "America/Menominee", "America/Rainy_River", "America/Winnipeg", "America/Pangnirtung", "America/Iqaluit", "America/Rankin_Inlet", "America/Cambridge_Bay", "America/Cancun", "America/Mexico_City", "America/Chihuahua", "America/Belize", "America/Costa_Rica", "America/Havana", "America/El_Salvador", "America/Guatemala", "America/Tegucigalpa", "America/Managua" });
/*      */ 
/*  324 */     tempMap.put("CEST", new String[] { "Europe/Tirane", "Europe/Andorra", "Europe/Vienna", "Europe/Minsk", "Europe/Brussels", "Europe/Sofia", "Europe/Prague", "Europe/Copenhagen", "Europe/Tallinn", "Europe/Berlin", "Europe/Gibraltar", "Europe/Athens", "Europe/Budapest", "Europe/Rome", "Europe/Riga", "Europe/Vaduz", "Europe/Vilnius", "Europe/Luxembourg", "Europe/Malta", "Europe/Chisinau", "Europe/Tiraspol", "Europe/Monaco", "Europe/Amsterdam", "Europe/Oslo", "Europe/Warsaw", "Europe/Lisbon", "Europe/Kaliningrad", "Europe/Madrid", "Europe/Stockholm", "Europe/Zurich", "Europe/Kiev", "Europe/Uzhgorod", "Europe/Zaporozhye", "Europe/Simferopol", "Europe/Belgrade", "Africa/Algiers", "Africa/Tripoli", "Africa/Tunis", "Africa/Ceuta" });
/*      */ 
/*  338 */     tempMap.put("CET", new String[] { "Europe/Tirane", "Europe/Andorra", "Europe/Vienna", "Europe/Minsk", "Europe/Brussels", "Europe/Sofia", "Europe/Prague", "Europe/Copenhagen", "Europe/Tallinn", "Europe/Berlin", "Europe/Gibraltar", "Europe/Athens", "Europe/Budapest", "Europe/Rome", "Europe/Riga", "Europe/Vaduz", "Europe/Vilnius", "Europe/Luxembourg", "Europe/Malta", "Europe/Chisinau", "Europe/Tiraspol", "Europe/Monaco", "Europe/Amsterdam", "Europe/Oslo", "Europe/Warsaw", "Europe/Lisbon", "Europe/Kaliningrad", "Europe/Madrid", "Europe/Stockholm", "Europe/Zurich", "Europe/Kiev", "Europe/Uzhgorod", "Europe/Zaporozhye", "Europe/Simferopol", "Europe/Belgrade", "Africa/Algiers", "Africa/Tripoli", "Africa/Casablanca", "Africa/Tunis", "Africa/Ceuta" });
/*      */ 
/*  352 */     tempMap.put("CGST", new String[] { "America/Scoresbysund" });
/*  353 */     tempMap.put("CGT", new String[] { "America/Scoresbysund" });
/*  354 */     tempMap.put("CHDT", new String[] { "America/Belize" });
/*  355 */     tempMap.put("CHUT", new String[] { "Asia/Chungking" });
/*  356 */     tempMap.put("CJT", new String[] { "Asia/Tokyo" });
/*  357 */     tempMap.put("CKHST", new String[] { "Pacific/Rarotonga" });
/*  358 */     tempMap.put("CKT", new String[] { "Pacific/Rarotonga" });
/*  359 */     tempMap.put("CLST", new String[] { "Antarctica/Palmer", "America/Santiago" });
/*      */ 
/*  361 */     tempMap.put("CLT", new String[] { "Antarctica/Palmer", "America/Santiago" });
/*      */ 
/*  363 */     tempMap.put("CMT", new String[] { "Europe/Copenhagen", "Europe/Chisinau", "Europe/Tiraspol", "America/St_Lucia", "America/Buenos_Aires", "America/Rosario", "America/Cordoba", "America/Jujuy", "America/Catamarca", "America/Mendoza", "America/Caracas" });
/*      */ 
/*  368 */     tempMap.put("COST", new String[] { "America/Bogota" });
/*  369 */     tempMap.put("COT", new String[] { "America/Bogota" });
/*  370 */     tempMap.put("CST", new String[] { "Asia/Harbin", "Asia/Shanghai", "Asia/Chungking", "Asia/Urumqi", "Asia/Kashgar", "Asia/Taipei", "Asia/Macao", "Asia/Jayapura", "Australia/Darwin", "Australia/Adelaide", "Australia/Broken_Hill", "America/Chicago", "America/Indianapolis", "America/Indiana/Marengo", "America/Indiana/Knox", "America/Indiana/Vevay", "America/Louisville", "America/Detroit", "America/Menominee", "America/Rainy_River", "America/Winnipeg", "America/Regina", "America/Swift_Current", "America/Pangnirtung", "America/Iqaluit", "America/Rankin_Inlet", "America/Cambridge_Bay", "America/Cancun", "America/Mexico_City", "America/Chihuahua", "America/Hermosillo", "America/Mazatlan", "America/Belize", "America/Costa_Rica", "America/Havana", "America/El_Salvador", "America/Guatemala", "America/Tegucigalpa", "America/Managua" });
/*      */ 
/*  390 */     tempMap.put("CUT", new String[] { "Europe/Zaporozhye" });
/*  391 */     tempMap.put("CVST", new String[] { "Atlantic/Cape_Verde" });
/*  392 */     tempMap.put("CVT", new String[] { "Atlantic/Cape_Verde" });
/*  393 */     tempMap.put("CWT", new String[] { "America/Chicago", "America/Indianapolis", "America/Indiana/Marengo", "America/Indiana/Knox", "America/Indiana/Vevay", "America/Louisville", "America/Menominee" });
/*      */ 
/*  397 */     tempMap.put("CXT", new String[] { "Indian/Christmas" });
/*  398 */     tempMap.put("DACT", new String[] { "Asia/Dacca" });
/*  399 */     tempMap.put("DAVT", new String[] { "Antarctica/Davis" });
/*  400 */     tempMap.put("DDUT", new String[] { "Antarctica/DumontDUrville" });
/*  401 */     tempMap.put("DFT", new String[] { "Europe/Oslo", "Europe/Paris" });
/*  402 */     tempMap.put("DMT", new String[] { "Europe/Belfast", "Europe/Dublin" });
/*  403 */     tempMap.put("DUSST", new String[] { "Asia/Dushanbe" });
/*  404 */     tempMap.put("DUST", new String[] { "Asia/Dushanbe" });
/*  405 */     tempMap.put("EASST", new String[] { "Pacific/Easter" });
/*  406 */     tempMap.put("EAST", new String[] { "Indian/Antananarivo", "Pacific/Easter" });
/*      */ 
/*  408 */     tempMap.put("EAT", new String[] { "Indian/Comoro", "Indian/Antananarivo", "Indian/Mayotte", "Africa/Djibouti", "Africa/Asmera", "Africa/Addis_Ababa", "Africa/Nairobi", "Africa/Mogadishu", "Africa/Khartoum", "Africa/Dar_es_Salaam", "Africa/Kampala" });
/*      */ 
/*  413 */     tempMap.put("ECT", new String[] { "Pacific/Galapagos", "America/Guayaquil" });
/*      */ 
/*  415 */     tempMap.put("EDDT", new String[] { "America/Iqaluit" });
/*  416 */     tempMap.put("EDT", new String[] { "America/New_York", "America/Indianapolis", "America/Indiana/Marengo", "America/Indiana/Vevay", "America/Louisville", "America/Detroit", "America/Montreal", "America/Thunder_Bay", "America/Nipigon", "America/Pangnirtung", "America/Iqaluit", "America/Cancun", "America/Nassau", "America/Santo_Domingo", "America/Port-au-Prince", "America/Jamaica", "America/Grand_Turk" });
/*      */ 
/*  424 */     tempMap.put("EEMT", new String[] { "Europe/Minsk", "Europe/Chisinau", "Europe/Tiraspol", "Europe/Kaliningrad", "Europe/Moscow" });
/*      */ 
/*  426 */     tempMap.put("EEST", new String[] { "Europe/Minsk", "Europe/Sofia", "Europe/Tallinn", "Europe/Helsinki", "Europe/Athens", "Europe/Riga", "Europe/Vilnius", "Europe/Chisinau", "Europe/Tiraspol", "Europe/Warsaw", "Europe/Bucharest", "Europe/Kaliningrad", "Europe/Moscow", "Europe/Istanbul", "Europe/Kiev", "Europe/Uzhgorod", "Europe/Zaporozhye", "Asia/Nicosia", "Asia/Amman", "Asia/Beirut", "Asia/Gaza", "Asia/Damascus", "Africa/Cairo" });
/*      */ 
/*  434 */     tempMap.put("EET", new String[] { "Europe/Minsk", "Europe/Sofia", "Europe/Tallinn", "Europe/Helsinki", "Europe/Athens", "Europe/Riga", "Europe/Vilnius", "Europe/Chisinau", "Europe/Tiraspol", "Europe/Warsaw", "Europe/Bucharest", "Europe/Kaliningrad", "Europe/Moscow", "Europe/Istanbul", "Europe/Kiev", "Europe/Uzhgorod", "Europe/Zaporozhye", "Europe/Simferopol", "Asia/Nicosia", "Asia/Amman", "Asia/Beirut", "Asia/Gaza", "Asia/Damascus", "Africa/Cairo", "Africa/Tripoli" });
/*      */ 
/*  443 */     tempMap.put("EGST", new String[] { "America/Scoresbysund" });
/*  444 */     tempMap.put("EGT", new String[] { "Atlantic/Jan_Mayen", "America/Scoresbysund" });
/*      */ 
/*  446 */     tempMap.put("EHDT", new String[] { "America/Santo_Domingo" });
/*  447 */     tempMap.put("EST", new String[] { "Australia/Brisbane", "Australia/Lindeman", "Australia/Hobart", "Australia/Melbourne", "Australia/Sydney", "Australia/Broken_Hill", "Australia/Lord_Howe", "America/New_York", "America/Chicago", "America/Indianapolis", "America/Indiana/Marengo", "America/Indiana/Knox", "America/Indiana/Vevay", "America/Louisville", "America/Detroit", "America/Menominee", "America/Montreal", "America/Thunder_Bay", "America/Nipigon", "America/Pangnirtung", "America/Iqaluit", "America/Cancun", "America/Antigua", "America/Nassau", "America/Cayman", "America/Santo_Domingo", "America/Port-au-Prince", "America/Jamaica", "America/Managua", "America/Panama", "America/Grand_Turk" });
/*      */ 
/*  461 */     tempMap.put("EWT", new String[] { "America/New_York", "America/Indianapolis", "America/Indiana/Marengo", "America/Indiana/Vevay", "America/Louisville", "America/Detroit", "America/Jamaica" });
/*      */ 
/*  465 */     tempMap.put("FFMT", new String[] { "America/Martinique" });
/*  466 */     tempMap.put("FJST", new String[] { "Pacific/Fiji" });
/*  467 */     tempMap.put("FJT", new String[] { "Pacific/Fiji" });
/*  468 */     tempMap.put("FKST", new String[] { "Atlantic/Stanley" });
/*  469 */     tempMap.put("FKT", new String[] { "Atlantic/Stanley" });
/*  470 */     tempMap.put("FMT", new String[] { "Atlantic/Madeira", "Africa/Freetown" });
/*      */ 
/*  472 */     tempMap.put("FNST", new String[] { "America/Noronha" });
/*  473 */     tempMap.put("FNT", new String[] { "America/Noronha" });
/*  474 */     tempMap.put("FRUST", new String[] { "Asia/Bishkek" });
/*  475 */     tempMap.put("FRUT", new String[] { "Asia/Bishkek" });
/*  476 */     tempMap.put("GALT", new String[] { "Pacific/Galapagos" });
/*  477 */     tempMap.put("GAMT", new String[] { "Pacific/Gambier" });
/*  478 */     tempMap.put("GBGT", new String[] { "America/Guyana" });
/*  479 */     tempMap.put("GEST", new String[] { "Asia/Tbilisi" });
/*  480 */     tempMap.put("GET", new String[] { "Asia/Tbilisi" });
/*  481 */     tempMap.put("GFT", new String[] { "America/Cayenne" });
/*  482 */     tempMap.put("GHST", new String[] { "Africa/Accra" });
/*  483 */     tempMap.put("GILT", new String[] { "Pacific/Tarawa" });
/*  484 */     tempMap.put("GMT", new String[] { "Atlantic/St_Helena", "Atlantic/Reykjavik", "Europe/London", "Europe/Belfast", "Europe/Dublin", "Europe/Gibraltar", "Africa/Porto-Novo", "Africa/Ouagadougou", "Africa/Abidjan", "Africa/Malabo", "Africa/Banjul", "Africa/Accra", "Africa/Conakry", "Africa/Bissau", "Africa/Monrovia", "Africa/Bamako", "Africa/Timbuktu", "Africa/Nouakchott", "Africa/Niamey", "Africa/Sao_Tome", "Africa/Dakar", "Africa/Freetown", "Africa/Lome" });
/*      */ 
/*  493 */     tempMap.put("GST", new String[] { "Atlantic/South_Georgia", "Asia/Bahrain", "Asia/Muscat", "Asia/Qatar", "Asia/Dubai", "Pacific/Guam" });
/*      */ 
/*  496 */     tempMap.put("GYT", new String[] { "America/Guyana" });
/*  497 */     tempMap.put("HADT", new String[] { "America/Adak" });
/*  498 */     tempMap.put("HART", new String[] { "Asia/Harbin" });
/*  499 */     tempMap.put("HAST", new String[] { "America/Adak" });
/*  500 */     tempMap.put("HAWT", new String[] { "America/Adak" });
/*  501 */     tempMap.put("HDT", new String[] { "Pacific/Honolulu" });
/*  502 */     tempMap.put("HKST", new String[] { "Asia/Hong_Kong" });
/*  503 */     tempMap.put("HKT", new String[] { "Asia/Hong_Kong" });
/*  504 */     tempMap.put("HMT", new String[] { "Atlantic/Azores", "Europe/Helsinki", "Asia/Dacca", "Asia/Calcutta", "America/Havana" });
/*      */ 
/*  506 */     tempMap.put("HOVST", new String[] { "Asia/Hovd" });
/*  507 */     tempMap.put("HOVT", new String[] { "Asia/Hovd" });
/*  508 */     tempMap.put("HST", new String[] { "Pacific/Johnston", "Pacific/Honolulu" });
/*      */ 
/*  510 */     tempMap.put("HWT", new String[] { "Pacific/Honolulu" });
/*  511 */     tempMap.put("ICT", new String[] { "Asia/Phnom_Penh", "Asia/Vientiane", "Asia/Bangkok", "Asia/Saigon" });
/*      */ 
/*  513 */     tempMap.put("IDDT", new String[] { "Asia/Jerusalem", "Asia/Gaza" });
/*  514 */     tempMap.put("IDT", new String[] { "Asia/Jerusalem", "Asia/Gaza" });
/*  515 */     tempMap.put("IHST", new String[] { "Asia/Colombo" });
/*  516 */     tempMap.put("IMT", new String[] { "Europe/Sofia", "Europe/Istanbul", "Asia/Irkutsk" });
/*      */ 
/*  518 */     tempMap.put("IOT", new String[] { "Indian/Chagos" });
/*  519 */     tempMap.put("IRKMT", new String[] { "Asia/Irkutsk" });
/*  520 */     tempMap.put("IRKST", new String[] { "Asia/Irkutsk" });
/*  521 */     tempMap.put("IRKT", new String[] { "Asia/Irkutsk" });
/*  522 */     tempMap.put("IRST", new String[] { "Asia/Tehran" });
/*  523 */     tempMap.put("IRT", new String[] { "Asia/Tehran" });
/*  524 */     tempMap.put("ISST", new String[] { "Atlantic/Reykjavik" });
/*  525 */     tempMap.put("IST", new String[] { "Atlantic/Reykjavik", "Europe/Belfast", "Europe/Dublin", "Asia/Dacca", "Asia/Thimbu", "Asia/Calcutta", "Asia/Jerusalem", "Asia/Katmandu", "Asia/Karachi", "Asia/Gaza", "Asia/Colombo" });
/*      */ 
/*  529 */     tempMap.put("JAYT", new String[] { "Asia/Jayapura" });
/*  530 */     tempMap.put("JMT", new String[] { "Atlantic/St_Helena", "Asia/Jerusalem" });
/*      */ 
/*  532 */     tempMap.put("JST", new String[] { "Asia/Rangoon", "Asia/Dili", "Asia/Ujung_Pandang", "Asia/Tokyo", "Asia/Kuala_Lumpur", "Asia/Kuching", "Asia/Manila", "Asia/Singapore", "Pacific/Nauru" });
/*      */ 
/*  536 */     tempMap.put("KART", new String[] { "Asia/Karachi" });
/*  537 */     tempMap.put("KAST", new String[] { "Asia/Kashgar" });
/*  538 */     tempMap.put("KDT", new String[] { "Asia/Seoul" });
/*  539 */     tempMap.put("KGST", new String[] { "Asia/Bishkek" });
/*  540 */     tempMap.put("KGT", new String[] { "Asia/Bishkek" });
/*  541 */     tempMap.put("KMT", new String[] { "Europe/Vilnius", "Europe/Kiev", "America/Cayman", "America/Jamaica", "America/St_Vincent", "America/Grand_Turk" });
/*      */ 
/*  544 */     tempMap.put("KOST", new String[] { "Pacific/Kosrae" });
/*  545 */     tempMap.put("KRAMT", new String[] { "Asia/Krasnoyarsk" });
/*  546 */     tempMap.put("KRAST", new String[] { "Asia/Krasnoyarsk" });
/*  547 */     tempMap.put("KRAT", new String[] { "Asia/Krasnoyarsk" });
/*  548 */     tempMap.put("KST", new String[] { "Asia/Seoul", "Asia/Pyongyang" });
/*  549 */     tempMap.put("KUYMT", new String[] { "Europe/Samara" });
/*  550 */     tempMap.put("KUYST", new String[] { "Europe/Samara" });
/*  551 */     tempMap.put("KUYT", new String[] { "Europe/Samara" });
/*  552 */     tempMap.put("KWAT", new String[] { "Pacific/Kwajalein" });
/*  553 */     tempMap.put("LHST", new String[] { "Australia/Lord_Howe" });
/*  554 */     tempMap.put("LINT", new String[] { "Pacific/Kiritimati" });
/*  555 */     tempMap.put("LKT", new String[] { "Asia/Colombo" });
/*  556 */     tempMap.put("LPMT", new String[] { "America/La_Paz" });
/*  557 */     tempMap.put("LRT", new String[] { "Africa/Monrovia" });
/*  558 */     tempMap.put("LST", new String[] { "Europe/Riga" });
/*  559 */     tempMap.put("M", new String[] { "Europe/Moscow" });
/*  560 */     tempMap.put("MADST", new String[] { "Atlantic/Madeira" });
/*  561 */     tempMap.put("MAGMT", new String[] { "Asia/Magadan" });
/*  562 */     tempMap.put("MAGST", new String[] { "Asia/Magadan" });
/*  563 */     tempMap.put("MAGT", new String[] { "Asia/Magadan" });
/*  564 */     tempMap.put("MALT", new String[] { "Asia/Kuala_Lumpur", "Asia/Singapore" });
/*      */ 
/*  566 */     tempMap.put("MART", new String[] { "Pacific/Marquesas" });
/*  567 */     tempMap.put("MAWT", new String[] { "Antarctica/Mawson" });
/*  568 */     tempMap.put("MDDT", new String[] { "America/Cambridge_Bay", "America/Yellowknife", "America/Inuvik" });
/*      */ 
/*  570 */     tempMap.put("MDST", new String[] { "Europe/Moscow" });
/*  571 */     tempMap.put("MDT", new String[] { "America/Denver", "America/Phoenix", "America/Boise", "America/Regina", "America/Swift_Current", "America/Edmonton", "America/Cambridge_Bay", "America/Yellowknife", "America/Inuvik", "America/Chihuahua", "America/Hermosillo", "America/Mazatlan" });
/*      */ 
/*  576 */     tempMap.put("MET", new String[] { "Europe/Tirane", "Europe/Andorra", "Europe/Vienna", "Europe/Minsk", "Europe/Brussels", "Europe/Sofia", "Europe/Prague", "Europe/Copenhagen", "Europe/Tallinn", "Europe/Berlin", "Europe/Gibraltar", "Europe/Athens", "Europe/Budapest", "Europe/Rome", "Europe/Riga", "Europe/Vaduz", "Europe/Vilnius", "Europe/Luxembourg", "Europe/Malta", "Europe/Chisinau", "Europe/Tiraspol", "Europe/Monaco", "Europe/Amsterdam", "Europe/Oslo", "Europe/Warsaw", "Europe/Lisbon", "Europe/Kaliningrad", "Europe/Madrid", "Europe/Stockholm", "Europe/Zurich", "Europe/Kiev", "Europe/Uzhgorod", "Europe/Zaporozhye", "Europe/Simferopol", "Europe/Belgrade", "Africa/Algiers", "Africa/Tripoli", "Africa/Casablanca", "Africa/Tunis", "Africa/Ceuta" });
/*      */ 
/*  590 */     tempMap.put("MHT", new String[] { "Pacific/Majuro", "Pacific/Kwajalein" });
/*      */ 
/*  592 */     tempMap.put("MMT", new String[] { "Indian/Maldives", "Europe/Minsk", "Europe/Moscow", "Asia/Rangoon", "Asia/Ujung_Pandang", "Asia/Colombo", "Pacific/Easter", "Africa/Monrovia", "America/Managua", "America/Montevideo" });
/*      */ 
/*  596 */     tempMap.put("MOST", new String[] { "Asia/Macao" });
/*  597 */     tempMap.put("MOT", new String[] { "Asia/Macao" });
/*  598 */     tempMap.put("MPT", new String[] { "Pacific/Saipan" });
/*  599 */     tempMap.put("MSK", new String[] { "Europe/Minsk", "Europe/Tallinn", "Europe/Riga", "Europe/Vilnius", "Europe/Chisinau", "Europe/Kiev", "Europe/Uzhgorod", "Europe/Zaporozhye", "Europe/Simferopol" });
/*      */ 
/*  603 */     tempMap.put("MST", new String[] { "Europe/Moscow", "America/Denver", "America/Phoenix", "America/Boise", "America/Regina", "America/Swift_Current", "America/Edmonton", "America/Dawson_Creek", "America/Cambridge_Bay", "America/Yellowknife", "America/Inuvik", "America/Mexico_City", "America/Chihuahua", "America/Hermosillo", "America/Mazatlan", "America/Tijuana" });
/*      */ 
/*  610 */     tempMap.put("MUT", new String[] { "Indian/Mauritius" });
/*  611 */     tempMap.put("MVT", new String[] { "Indian/Maldives" });
/*  612 */     tempMap.put("MWT", new String[] { "America/Denver", "America/Phoenix", "America/Boise" });
/*      */ 
/*  614 */     tempMap.put("MYT", new String[] { "Asia/Kuala_Lumpur", "Asia/Kuching" });
/*      */ 
/*  617 */     tempMap.put("NCST", new String[] { "Pacific/Noumea" });
/*  618 */     tempMap.put("NCT", new String[] { "Pacific/Noumea" });
/*  619 */     tempMap.put("NDT", new String[] { "America/Nome", "America/Adak", "America/St_Johns", "America/Goose_Bay" });
/*      */ 
/*  621 */     tempMap.put("NEGT", new String[] { "America/Paramaribo" });
/*  622 */     tempMap.put("NFT", new String[] { "Europe/Paris", "Europe/Oslo", "Pacific/Norfolk" });
/*      */ 
/*  624 */     tempMap.put("NMT", new String[] { "Pacific/Norfolk" });
/*  625 */     tempMap.put("NOVMT", new String[] { "Asia/Novosibirsk" });
/*  626 */     tempMap.put("NOVST", new String[] { "Asia/Novosibirsk" });
/*  627 */     tempMap.put("NOVT", new String[] { "Asia/Novosibirsk" });
/*  628 */     tempMap.put("NPT", new String[] { "Asia/Katmandu" });
/*  629 */     tempMap.put("NRT", new String[] { "Pacific/Nauru" });
/*  630 */     tempMap.put("NST", new String[] { "Europe/Amsterdam", "Pacific/Pago_Pago", "Pacific/Midway", "America/Nome", "America/Adak", "America/St_Johns", "America/Goose_Bay" });
/*      */ 
/*  633 */     tempMap.put("NUT", new String[] { "Pacific/Niue" });
/*  634 */     tempMap.put("NWT", new String[] { "America/Nome", "America/Adak" });
/*  635 */     tempMap.put("NZDT", new String[] { "Antarctica/McMurdo" });
/*  636 */     tempMap.put("NZHDT", new String[] { "Pacific/Auckland" });
/*  637 */     tempMap.put("NZST", new String[] { "Antarctica/McMurdo", "Pacific/Auckland" });
/*      */ 
/*  639 */     tempMap.put("OMSMT", new String[] { "Asia/Omsk" });
/*  640 */     tempMap.put("OMSST", new String[] { "Asia/Omsk" });
/*  641 */     tempMap.put("OMST", new String[] { "Asia/Omsk" });
/*  642 */     tempMap.put("PDDT", new String[] { "America/Inuvik", "America/Whitehorse", "America/Dawson" });
/*      */ 
/*  644 */     tempMap.put("PDT", new String[] { "America/Los_Angeles", "America/Juneau", "America/Boise", "America/Vancouver", "America/Dawson_Creek", "America/Inuvik", "America/Whitehorse", "America/Dawson", "America/Tijuana" });
/*      */ 
/*  648 */     tempMap.put("PEST", new String[] { "America/Lima" });
/*  649 */     tempMap.put("PET", new String[] { "America/Lima" });
/*  650 */     tempMap.put("PETMT", new String[] { "Asia/Kamchatka" });
/*  651 */     tempMap.put("PETST", new String[] { "Asia/Kamchatka" });
/*  652 */     tempMap.put("PETT", new String[] { "Asia/Kamchatka" });
/*  653 */     tempMap.put("PGT", new String[] { "Pacific/Port_Moresby" });
/*  654 */     tempMap.put("PHOT", new String[] { "Pacific/Enderbury" });
/*  655 */     tempMap.put("PHST", new String[] { "Asia/Manila" });
/*  656 */     tempMap.put("PHT", new String[] { "Asia/Manila" });
/*  657 */     tempMap.put("PKT", new String[] { "Asia/Karachi" });
/*  658 */     tempMap.put("PMDT", new String[] { "America/Miquelon" });
/*  659 */     tempMap.put("PMMT", new String[] { "Pacific/Port_Moresby" });
/*  660 */     tempMap.put("PMST", new String[] { "America/Miquelon" });
/*  661 */     tempMap.put("PMT", new String[] { "Antarctica/DumontDUrville", "Europe/Prague", "Europe/Paris", "Europe/Monaco", "Africa/Algiers", "Africa/Tunis", "America/Panama", "America/Paramaribo" });
/*      */ 
/*  665 */     tempMap.put("PNT", new String[] { "Pacific/Pitcairn" });
/*  666 */     tempMap.put("PONT", new String[] { "Pacific/Ponape" });
/*  667 */     tempMap.put("PPMT", new String[] { "America/Port-au-Prince" });
/*  668 */     tempMap.put("PST", new String[] { "Pacific/Pitcairn", "America/Los_Angeles", "America/Juneau", "America/Boise", "America/Vancouver", "America/Dawson_Creek", "America/Inuvik", "America/Whitehorse", "America/Dawson", "America/Hermosillo", "America/Mazatlan", "America/Tijuana" });
/*      */ 
/*  673 */     tempMap.put("PWT", new String[] { "Pacific/Palau", "America/Los_Angeles", "America/Juneau", "America/Boise", "America/Tijuana" });
/*      */ 
/*  676 */     tempMap.put("PYST", new String[] { "America/Asuncion" });
/*  677 */     tempMap.put("PYT", new String[] { "America/Asuncion" });
/*  678 */     tempMap.put("QMT", new String[] { "America/Guayaquil" });
/*  679 */     tempMap.put("RET", new String[] { "Indian/Reunion" });
/*  680 */     tempMap.put("RMT", new String[] { "Atlantic/Reykjavik", "Europe/Rome", "Europe/Riga", "Asia/Rangoon" });
/*      */ 
/*  682 */     tempMap.put("S", new String[] { "Europe/Moscow" });
/*  683 */     tempMap.put("SAMMT", new String[] { "Europe/Samara" });
/*  684 */     tempMap.put("SAMST", new String[] { "Europe/Samara", "Asia/Samarkand" });
/*      */ 
/*  687 */     tempMap.put("SAMT", new String[] { "Europe/Samara", "Asia/Samarkand", "Pacific/Pago_Pago", "Pacific/Apia" });
/*      */ 
/*  689 */     tempMap.put("SAST", new String[] { "Africa/Maseru", "Africa/Windhoek", "Africa/Johannesburg", "Africa/Mbabane" });
/*      */ 
/*  691 */     tempMap.put("SBT", new String[] { "Pacific/Guadalcanal" });
/*  692 */     tempMap.put("SCT", new String[] { "Indian/Mahe" });
/*  693 */     tempMap.put("SDMT", new String[] { "America/Santo_Domingo" });
/*  694 */     tempMap.put("SGT", new String[] { "Asia/Singapore" });
/*  695 */     tempMap.put("SHEST", new String[] { "Asia/Aqtau" });
/*  696 */     tempMap.put("SHET", new String[] { "Asia/Aqtau" });
/*  697 */     tempMap.put("SJMT", new String[] { "America/Costa_Rica" });
/*  698 */     tempMap.put("SLST", new String[] { "Africa/Freetown" });
/*  699 */     tempMap.put("SMT", new String[] { "Atlantic/Stanley", "Europe/Stockholm", "Europe/Simferopol", "Asia/Phnom_Penh", "Asia/Vientiane", "Asia/Kuala_Lumpur", "Asia/Singapore", "Asia/Saigon", "America/Santiago" });
/*      */ 
/*  703 */     tempMap.put("SRT", new String[] { "America/Paramaribo" });
/*  704 */     tempMap.put("SST", new String[] { "Pacific/Pago_Pago", "Pacific/Midway" });
/*      */ 
/*  706 */     tempMap.put("SVEMT", new String[] { "Asia/Yekaterinburg" });
/*  707 */     tempMap.put("SVEST", new String[] { "Asia/Yekaterinburg" });
/*  708 */     tempMap.put("SVET", new String[] { "Asia/Yekaterinburg" });
/*  709 */     tempMap.put("SWAT", new String[] { "Africa/Windhoek" });
/*  710 */     tempMap.put("SYOT", new String[] { "Antarctica/Syowa" });
/*  711 */     tempMap.put("TAHT", new String[] { "Pacific/Tahiti" });
/*  712 */     tempMap.put("TASST", new String[] { "Asia/Samarkand", "Asia/Tashkent" });
/*      */ 
/*  715 */     tempMap.put("TAST", new String[] { "Asia/Samarkand", "Asia/Tashkent" });
/*  716 */     tempMap.put("TBIST", new String[] { "Asia/Tbilisi" });
/*  717 */     tempMap.put("TBIT", new String[] { "Asia/Tbilisi" });
/*  718 */     tempMap.put("TBMT", new String[] { "Asia/Tbilisi" });
/*  719 */     tempMap.put("TFT", new String[] { "Indian/Kerguelen" });
/*  720 */     tempMap.put("TJT", new String[] { "Asia/Dushanbe" });
/*  721 */     tempMap.put("TKT", new String[] { "Pacific/Fakaofo" });
/*  722 */     tempMap.put("TMST", new String[] { "Asia/Ashkhabad" });
/*  723 */     tempMap.put("TMT", new String[] { "Europe/Tallinn", "Asia/Tehran", "Asia/Ashkhabad" });
/*      */ 
/*  725 */     tempMap.put("TOST", new String[] { "Pacific/Tongatapu" });
/*  726 */     tempMap.put("TOT", new String[] { "Pacific/Tongatapu" });
/*  727 */     tempMap.put("TPT", new String[] { "Asia/Dili" });
/*  728 */     tempMap.put("TRST", new String[] { "Europe/Istanbul" });
/*  729 */     tempMap.put("TRT", new String[] { "Europe/Istanbul" });
/*  730 */     tempMap.put("TRUT", new String[] { "Pacific/Truk" });
/*  731 */     tempMap.put("TVT", new String[] { "Pacific/Funafuti" });
/*  732 */     tempMap.put("ULAST", new String[] { "Asia/Ulaanbaatar" });
/*  733 */     tempMap.put("ULAT", new String[] { "Asia/Ulaanbaatar" });
/*  734 */     tempMap.put("URUT", new String[] { "Asia/Urumqi" });
/*  735 */     tempMap.put("UYHST", new String[] { "America/Montevideo" });
/*  736 */     tempMap.put("UYT", new String[] { "America/Montevideo" });
/*  737 */     tempMap.put("UZST", new String[] { "Asia/Samarkand", "Asia/Tashkent" });
/*  738 */     tempMap.put("UZT", new String[] { "Asia/Samarkand", "Asia/Tashkent" });
/*  739 */     tempMap.put("VET", new String[] { "America/Caracas" });
/*  740 */     tempMap.put("VLAMT", new String[] { "Asia/Vladivostok" });
/*  741 */     tempMap.put("VLAST", new String[] { "Asia/Vladivostok" });
/*  742 */     tempMap.put("VLAT", new String[] { "Asia/Vladivostok" });
/*  743 */     tempMap.put("VUST", new String[] { "Pacific/Efate" });
/*  744 */     tempMap.put("VUT", new String[] { "Pacific/Efate" });
/*  745 */     tempMap.put("WAKT", new String[] { "Pacific/Wake" });
/*  746 */     tempMap.put("WARST", new String[] { "America/Jujuy", "America/Mendoza" });
/*      */ 
/*  748 */     tempMap.put("WART", new String[] { "America/Jujuy", "America/Mendoza" });
/*      */ 
/*  751 */     tempMap.put("WAST", new String[] { "Africa/Ndjamena", "Africa/Windhoek" });
/*      */ 
/*  753 */     tempMap.put("WAT", new String[] { "Africa/Luanda", "Africa/Porto-Novo", "Africa/Douala", "Africa/Bangui", "Africa/Ndjamena", "Africa/Kinshasa", "Africa/Brazzaville", "Africa/Malabo", "Africa/Libreville", "Africa/Banjul", "Africa/Conakry", "Africa/Bissau", "Africa/Bamako", "Africa/Nouakchott", "Africa/El_Aaiun", "Africa/Windhoek", "Africa/Niamey", "Africa/Lagos", "Africa/Dakar", "Africa/Freetown" });
/*      */ 
/*  760 */     tempMap.put("WEST", new String[] { "Atlantic/Faeroe", "Atlantic/Azores", "Atlantic/Madeira", "Atlantic/Canary", "Europe/Brussels", "Europe/Luxembourg", "Europe/Monaco", "Europe/Lisbon", "Europe/Madrid", "Africa/Algiers", "Africa/Casablanca", "Africa/Ceuta" });
/*      */ 
/*  765 */     tempMap.put("WET", new String[] { "Atlantic/Faeroe", "Atlantic/Azores", "Atlantic/Madeira", "Atlantic/Canary", "Europe/Andorra", "Europe/Brussels", "Europe/Luxembourg", "Europe/Monaco", "Europe/Lisbon", "Europe/Madrid", "Africa/Algiers", "Africa/Casablanca", "Africa/El_Aaiun", "Africa/Ceuta" });
/*      */ 
/*  770 */     tempMap.put("WFT", new String[] { "Pacific/Wallis" });
/*  771 */     tempMap.put("WGST", new String[] { "America/Godthab" });
/*  772 */     tempMap.put("WGT", new String[] { "America/Godthab" });
/*  773 */     tempMap.put("WMT", new String[] { "Europe/Vilnius", "Europe/Warsaw" });
/*  774 */     tempMap.put("WST", new String[] { "Antarctica/Casey", "Pacific/Apia", "Australia/Perth" });
/*      */ 
/*  776 */     tempMap.put("YAKMT", new String[] { "Asia/Yakutsk" });
/*  777 */     tempMap.put("YAKST", new String[] { "Asia/Yakutsk" });
/*  778 */     tempMap.put("YAKT", new String[] { "Asia/Yakutsk" });
/*  779 */     tempMap.put("YAPT", new String[] { "Pacific/Yap" });
/*  780 */     tempMap.put("YDDT", new String[] { "America/Whitehorse", "America/Dawson" });
/*      */ 
/*  782 */     tempMap.put("YDT", new String[] { "America/Yakutat", "America/Whitehorse", "America/Dawson" });
/*      */ 
/*  784 */     tempMap.put("YEKMT", new String[] { "Asia/Yekaterinburg" });
/*  785 */     tempMap.put("YEKST", new String[] { "Asia/Yekaterinburg" });
/*  786 */     tempMap.put("YEKT", new String[] { "Asia/Yekaterinburg" });
/*  787 */     tempMap.put("YERST", new String[] { "Asia/Yerevan" });
/*  788 */     tempMap.put("YERT", new String[] { "Asia/Yerevan" });
/*  789 */     tempMap.put("YST", new String[] { "America/Yakutat", "America/Whitehorse", "America/Dawson" });
/*      */ 
/*  791 */     tempMap.put("YWT", new String[] { "America/Yakutat" });
/*      */ 
/*  793 */     ABBREVIATED_TIMEZONES = Collections.unmodifiableMap(tempMap);
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.mysql.jdbc.TimeUtil
 * JD-Core Version:    0.6.0
 */
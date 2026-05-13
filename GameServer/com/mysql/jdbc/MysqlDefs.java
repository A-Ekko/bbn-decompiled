/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ final class MysqlDefs
/*     */ {
/*     */   static final int COM_BINLOG_DUMP = 18;
/*     */   static final int COM_CHANGE_USER = 17;
/*     */   static final int COM_CLOSE_STATEMENT = 25;
/*     */   static final int COM_CONNECT_OUT = 20;
/*     */   static final int COM_END = 28;
/*     */   static final int COM_EXECUTE = 23;
/*     */   static final int COM_LONG_DATA = 24;
/*     */   static final int COM_PREPARE = 22;
/*     */   static final int COM_REGISTER_SLAVE = 21;
/*     */   static final int COM_RESET_STMT = 26;
/*     */   static final int COM_SET_OPTION = 27;
/*     */   static final int COM_TABLE_DUMP = 19;
/*     */   static final int CONNECT = 11;
/*     */   static final int CREATE_DB = 5;
/*     */   static final int DEBUG = 13;
/*     */   static final int DELAYED_INSERT = 16;
/*     */   static final int DROP_DB = 6;
/*     */   static final int FIELD_LIST = 4;
/*     */   static final int FIELD_TYPE_BIT = 16;
/*     */   static final int FIELD_TYPE_BLOB = 252;
/*     */   static final int FIELD_TYPE_DATE = 10;
/*     */   static final int FIELD_TYPE_DATETIME = 12;
/*     */   static final int FIELD_TYPE_DECIMAL = 0;
/*     */   static final int FIELD_TYPE_DOUBLE = 5;
/*     */   static final int FIELD_TYPE_ENUM = 247;
/*     */   static final int FIELD_TYPE_FLOAT = 4;
/*     */   static final int FIELD_TYPE_GEOMETRY = 255;
/*     */   static final int FIELD_TYPE_INT24 = 9;
/*     */   static final int FIELD_TYPE_LONG = 3;
/*     */   static final int FIELD_TYPE_LONG_BLOB = 251;
/*     */   static final int FIELD_TYPE_LONGLONG = 8;
/*     */   static final int FIELD_TYPE_MEDIUM_BLOB = 250;
/*     */   static final int FIELD_TYPE_NEW_DECIMAL = 246;
/*     */   static final int FIELD_TYPE_NEWDATE = 14;
/*     */   static final int FIELD_TYPE_NULL = 6;
/*     */   static final int FIELD_TYPE_SET = 248;
/*     */   static final int FIELD_TYPE_SHORT = 2;
/*     */   static final int FIELD_TYPE_STRING = 254;
/*     */   static final int FIELD_TYPE_TIME = 11;
/*     */   static final int FIELD_TYPE_TIMESTAMP = 7;
/*     */   static final int FIELD_TYPE_TINY = 1;
/*     */   static final int FIELD_TYPE_TINY_BLOB = 249;
/*     */   static final int FIELD_TYPE_VAR_STRING = 253;
/*     */   static final int FIELD_TYPE_VARCHAR = 15;
/*     */   static final int FIELD_TYPE_YEAR = 13;
/*     */   static final int INIT_DB = 2;
/*     */   static final long LENGTH_BLOB = 65535L;
/*     */   static final long LENGTH_LONGBLOB = 4294967295L;
/*     */   static final long LENGTH_MEDIUMBLOB = 16777215L;
/*     */   static final long LENGTH_TINYBLOB = 255L;
/*     */   static final int MAX_ROWS = 50000000;
/*     */   public static final int NO_CHARSET_INFO = -1;
/*     */   static final int PING = 14;
/*     */   static final int PROCESS_INFO = 10;
/*     */   static final int PROCESS_KILL = 12;
/*     */   static final int QUERY = 3;
/*     */   static final int QUIT = 1;
/*     */   static final int RELOAD = 7;
/*     */   static final int SHUTDOWN = 8;
/*     */   static final int SLEEP = 0;
/*     */   static final int STATISTICS = 9;
/*     */   static final int TIME = 15;
/*     */ 
/*     */   static int mysqlToJavaType(int mysqlType)
/*     */   {
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/*     */     int jdbcType;
/* 186 */     switch (mysqlType) {
/*     */     case 0:
/*     */     case 246:
/* 189 */       jdbcType = 3;
/*     */ 
/* 191 */       break;
/*     */     case 1:
/* 194 */       jdbcType = -6;
/*     */ 
/* 196 */       break;
/*     */     case 2:
/* 199 */       jdbcType = 5;
/*     */ 
/* 201 */       break;
/*     */     case 3:
/* 204 */       jdbcType = 4;
/*     */ 
/* 206 */       break;
/*     */     case 4:
/* 209 */       jdbcType = 7;
/*     */ 
/* 211 */       break;
/*     */     case 5:
/* 214 */       jdbcType = 8;
/*     */ 
/* 216 */       break;
/*     */     case 6:
/* 219 */       jdbcType = 0;
/*     */ 
/* 221 */       break;
/*     */     case 7:
/* 224 */       jdbcType = 93;
/*     */ 
/* 226 */       break;
/*     */     case 8:
/* 229 */       jdbcType = -5;
/*     */ 
/* 231 */       break;
/*     */     case 9:
/* 234 */       jdbcType = 4;
/*     */ 
/* 236 */       break;
/*     */     case 10:
/* 239 */       jdbcType = 91;
/*     */ 
/* 241 */       break;
/*     */     case 11:
/* 244 */       jdbcType = 92;
/*     */ 
/* 246 */       break;
/*     */     case 12:
/* 249 */       jdbcType = 93;
/*     */ 
/* 251 */       break;
/*     */     case 13:
/* 254 */       jdbcType = 91;
/*     */ 
/* 256 */       break;
/*     */     case 14:
/* 259 */       jdbcType = 91;
/*     */ 
/* 261 */       break;
/*     */     case 247:
/* 264 */       jdbcType = 1;
/*     */ 
/* 266 */       break;
/*     */     case 248:
/* 269 */       jdbcType = 1;
/*     */ 
/* 271 */       break;
/*     */     case 249:
/* 274 */       jdbcType = -3;
/*     */ 
/* 276 */       break;
/*     */     case 250:
/* 279 */       jdbcType = -4;
/*     */ 
/* 281 */       break;
/*     */     case 251:
/* 284 */       jdbcType = -4;
/*     */ 
/* 286 */       break;
/*     */     case 252:
/* 289 */       jdbcType = -4;
/*     */ 
/* 291 */       break;
/*     */     case 15:
/*     */     case 253:
/* 295 */       jdbcType = 12;
/*     */ 
/* 297 */       break;
/*     */     case 254:
/* 300 */       jdbcType = 1;
/*     */ 
/* 302 */       break;
/*     */     case 255:
/* 304 */       jdbcType = -2;
/*     */ 
/* 306 */       break;
/*     */     case 16:
/* 308 */       jdbcType = -7;
/*     */ 
/* 310 */       break;
/*     */     default:
/* 312 */       jdbcType = 12;
/*     */     }
/*     */ 
/* 315 */     return jdbcType;
/*     */   }
/*     */ 
/*     */   static int mysqlToJavaType(String mysqlType)
/*     */   {
/* 322 */     if (mysqlType.equalsIgnoreCase("TINYINT"))
/* 323 */       return mysqlToJavaType(1);
/* 324 */     if (mysqlType.equalsIgnoreCase("SMALLINT"))
/* 325 */       return mysqlToJavaType(2);
/* 326 */     if (mysqlType.equalsIgnoreCase("MEDIUMINT"))
/* 327 */       return mysqlToJavaType(9);
/* 328 */     if ((mysqlType.equalsIgnoreCase("INT")) || (mysqlType.equalsIgnoreCase("INTEGER")))
/* 329 */       return mysqlToJavaType(3);
/* 330 */     if (mysqlType.equalsIgnoreCase("BIGINT"))
/* 331 */       return mysqlToJavaType(8);
/* 332 */     if (mysqlType.equalsIgnoreCase("INT24"))
/* 333 */       return mysqlToJavaType(9);
/* 334 */     if (mysqlType.equalsIgnoreCase("REAL"))
/* 335 */       return mysqlToJavaType(5);
/* 336 */     if (mysqlType.equalsIgnoreCase("FLOAT"))
/* 337 */       return mysqlToJavaType(4);
/* 338 */     if (mysqlType.equalsIgnoreCase("DECIMAL"))
/* 339 */       return mysqlToJavaType(0);
/* 340 */     if (mysqlType.equalsIgnoreCase("NUMERIC"))
/* 341 */       return mysqlToJavaType(0);
/* 342 */     if (mysqlType.equalsIgnoreCase("DOUBLE"))
/* 343 */       return mysqlToJavaType(5);
/* 344 */     if (mysqlType.equalsIgnoreCase("CHAR"))
/* 345 */       return mysqlToJavaType(254);
/* 346 */     if (mysqlType.equalsIgnoreCase("VARCHAR"))
/* 347 */       return mysqlToJavaType(253);
/* 348 */     if (mysqlType.equalsIgnoreCase("DATE"))
/* 349 */       return mysqlToJavaType(10);
/* 350 */     if (mysqlType.equalsIgnoreCase("TIME"))
/* 351 */       return mysqlToJavaType(11);
/* 352 */     if (mysqlType.equalsIgnoreCase("YEAR"))
/* 353 */       return mysqlToJavaType(13);
/* 354 */     if (mysqlType.equalsIgnoreCase("TIMESTAMP"))
/* 355 */       return mysqlToJavaType(7);
/* 356 */     if (mysqlType.equalsIgnoreCase("DATETIME"))
/* 357 */       return mysqlToJavaType(12);
/* 358 */     if (mysqlType.equalsIgnoreCase("TINYBLOB"))
/* 359 */       return -2;
/* 360 */     if (mysqlType.equalsIgnoreCase("BLOB"))
/* 361 */       return -4;
/* 362 */     if (mysqlType.equalsIgnoreCase("MEDIUMBLOB"))
/* 363 */       return -4;
/* 364 */     if (mysqlType.equalsIgnoreCase("LONGBLOB"))
/* 365 */       return -4;
/* 366 */     if (mysqlType.equalsIgnoreCase("TINYTEXT"))
/* 367 */       return 12;
/* 368 */     if (mysqlType.equalsIgnoreCase("TEXT"))
/* 369 */       return -1;
/* 370 */     if (mysqlType.equalsIgnoreCase("MEDIUMTEXT"))
/* 371 */       return -1;
/* 372 */     if (mysqlType.equalsIgnoreCase("LONGTEXT"))
/* 373 */       return -1;
/* 374 */     if (mysqlType.equalsIgnoreCase("ENUM"))
/* 375 */       return mysqlToJavaType(247);
/* 376 */     if (mysqlType.equalsIgnoreCase("SET"))
/* 377 */       return mysqlToJavaType(248);
/* 378 */     if (mysqlType.equalsIgnoreCase("GEOMETRY"))
/* 379 */       return mysqlToJavaType(255);
/* 380 */     if (mysqlType.equalsIgnoreCase("BINARY"))
/* 381 */       return -2;
/* 382 */     if (mysqlType.equalsIgnoreCase("VARBINARY")) {
/* 383 */       return -3;
/*     */     }
/*     */ 
/* 387 */     return 1111;
/*     */   }
/*     */ 
/*     */   public static String typeToName(int mysqlType)
/*     */   {
/* 395 */     switch (mysqlType) {
/*     */     case 0:
/* 397 */       return "FIELD_TYPE_DECIMAL";
/*     */     case 1:
/* 400 */       return "FIELD_TYPE_TINY";
/*     */     case 2:
/* 403 */       return "FIELD_TYPE_SHORT";
/*     */     case 3:
/* 406 */       return "FIELD_TYPE_LONG";
/*     */     case 4:
/* 409 */       return "FIELD_TYPE_FLOAT";
/*     */     case 5:
/* 412 */       return "FIELD_TYPE_DOUBLE";
/*     */     case 6:
/* 415 */       return "FIELD_TYPE_NULL";
/*     */     case 7:
/* 418 */       return "FIELD_TYPE_TIMESTAMP";
/*     */     case 8:
/* 421 */       return "FIELD_TYPE_LONGLONG";
/*     */     case 9:
/* 424 */       return "FIELD_TYPE_INT24";
/*     */     case 10:
/* 427 */       return "FIELD_TYPE_DATE";
/*     */     case 11:
/* 430 */       return "FIELD_TYPE_TIME";
/*     */     case 12:
/* 433 */       return "FIELD_TYPE_DATETIME";
/*     */     case 13:
/* 436 */       return "FIELD_TYPE_YEAR";
/*     */     case 14:
/* 439 */       return "FIELD_TYPE_NEWDATE";
/*     */     case 247:
/* 442 */       return "FIELD_TYPE_ENUM";
/*     */     case 248:
/* 445 */       return "FIELD_TYPE_SET";
/*     */     case 249:
/* 448 */       return "FIELD_TYPE_TINY_BLOB";
/*     */     case 250:
/* 451 */       return "FIELD_TYPE_MEDIUM_BLOB";
/*     */     case 251:
/* 454 */       return "FIELD_TYPE_LONG_BLOB";
/*     */     case 252:
/* 457 */       return "FIELD_TYPE_BLOB";
/*     */     case 253:
/* 460 */       return "FIELD_TYPE_VAR_STRING";
/*     */     case 254:
/* 463 */       return "FIELD_TYPE_STRING";
/*     */     case 15:
/* 466 */       return "FIELD_TYPE_VARCHAR";
/*     */     case 255:
/* 469 */       return "FIELD_TYPE_GEOMETRY";
/*     */     }
/*     */ 
/* 472 */     return " Unknown MySQL Type # " + mysqlType;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.mysql.jdbc.MysqlDefs
 * JD-Core Version:    0.6.0
 */
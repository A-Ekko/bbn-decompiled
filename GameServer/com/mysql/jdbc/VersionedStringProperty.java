/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.List;
/*     */ 
/*     */ class VersionedStringProperty
/*     */ {
/*     */   int majorVersion;
/*     */   int minorVersion;
/*     */   int subminorVersion;
/* 476 */   boolean preferredValue = false;
/*     */   String propertyInfo;
/*     */ 
/*     */   VersionedStringProperty(String property)
/*     */   {
/* 481 */     property = property.trim();
/*     */ 
/* 483 */     if (property.startsWith("*")) {
/* 484 */       property = property.substring(1);
/* 485 */       this.preferredValue = true;
/*     */     }
/*     */ 
/* 488 */     if (property.startsWith(">")) {
/* 489 */       property = property.substring(1);
/*     */ 
/* 491 */       int charPos = 0;
/*     */ 
/* 493 */       for (charPos = 0; charPos < property.length(); charPos++) {
/* 494 */         char c = property.charAt(charPos);
/*     */ 
/* 496 */         if ((!Character.isWhitespace(c)) && (!Character.isDigit(c)) && (c != '.'))
/*     */         {
/*     */           break;
/*     */         }
/*     */       }
/*     */ 
/* 502 */       String versionInfo = property.substring(0, charPos);
/* 503 */       List versionParts = StringUtils.split(versionInfo, ".", true);
/*     */ 
/* 505 */       this.majorVersion = Integer.parseInt(versionParts.get(0).toString());
/*     */ 
/* 507 */       if (versionParts.size() > 1)
/* 508 */         this.minorVersion = Integer.parseInt(versionParts.get(1).toString());
/*     */       else {
/* 510 */         this.minorVersion = 0;
/*     */       }
/*     */ 
/* 513 */       if (versionParts.size() > 2) {
/* 514 */         this.subminorVersion = Integer.parseInt(versionParts.get(2).toString());
/*     */       }
/*     */       else {
/* 517 */         this.subminorVersion = 0;
/*     */       }
/*     */ 
/* 520 */       this.propertyInfo = property.substring(charPos);
/*     */     } else {
/* 522 */       this.majorVersion = (this.minorVersion = this.subminorVersion = 0);
/* 523 */       this.propertyInfo = property;
/*     */     }
/*     */   }
/*     */ 
/*     */   VersionedStringProperty(String property, int major, int minor, int subminor) {
/* 528 */     this.propertyInfo = property;
/* 529 */     this.majorVersion = major;
/* 530 */     this.minorVersion = minor;
/* 531 */     this.subminorVersion = subminor;
/*     */   }
/*     */ 
/*     */   boolean isOkayForVersion(Connection conn) throws SQLException {
/* 535 */     return conn.versionMeetsMinimum(this.majorVersion, this.minorVersion, this.subminorVersion);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 540 */     return this.propertyInfo;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.mysql.jdbc.VersionedStringProperty
 * JD-Core Version:    0.6.0
 */
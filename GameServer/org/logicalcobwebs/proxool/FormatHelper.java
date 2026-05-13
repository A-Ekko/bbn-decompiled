/*    */ package org.logicalcobwebs.proxool;
/*    */ 
/*    */ import java.text.DecimalFormat;
/*    */ 
/*    */ public class FormatHelper
/*    */ {
/* 20 */   private static DecimalFormat smallNumberFormat = new DecimalFormat("00");
/*    */ 
/* 22 */   private static DecimalFormat mediumNumberFormat = new DecimalFormat("0000");
/*    */ 
/* 24 */   private static DecimalFormat bigCountFormat = new DecimalFormat("###000000");
/*    */ 
/*    */   public static String formatSmallNumber(long value)
/*    */   {
/* 32 */     return smallNumberFormat.format(value);
/*    */   }
/*    */ 
/*    */   public static String formatMediumNumber(long value)
/*    */   {
/* 41 */     return mediumNumberFormat.format(value);
/*    */   }
/*    */ 
/*    */   public static String formatBigNumber(long value)
/*    */   {
/* 50 */     return bigCountFormat.format(value);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.FormatHelper
 * JD-Core Version:    0.6.0
 */
/*     */ package flex.messaging;
/*     */ 
/*     */ public class VersionInfo
/*     */ {
/*     */   public static String BUILD_MESSAGE;
/*     */   public static String BUILD_NUMBER_STRING;
/*     */   public static String BUILD_TITLE;
/*     */   public static long BUILD_NUMBER;
/*     */   private static final String LCDS_CLASS = "flex.data.DataService";
/*     */ 
/*     */   public static String buildMessage()
/*     */   {
/*  33 */     if (BUILD_MESSAGE == null)
/*     */     {
/*     */       try
/*     */       {
/*  38 */         getBuild();
/*     */ 
/*  40 */         if ((BUILD_NUMBER_STRING == null) || (BUILD_NUMBER_STRING == ""))
/*     */         {
/*  42 */           BUILD_MESSAGE = BUILD_TITLE;
/*     */         }
/*     */         else
/*     */         {
/*  46 */           BUILD_MESSAGE = BUILD_TITLE + ": " + BUILD_NUMBER_STRING;
/*     */         }
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/*  51 */         BUILD_MESSAGE = BUILD_TITLE + ": information unavailable";
/*     */       }
/*     */     }
/*     */ 
/*  55 */     return BUILD_MESSAGE;
/*     */   }
/*     */ 
/*     */   public static long getBuildAsLong()
/*     */   {
/*  60 */     if (BUILD_NUMBER == 0L)
/*     */     {
/*  62 */       getBuild();
/*     */ 
/*  64 */       if ((BUILD_NUMBER_STRING != null) && (!BUILD_NUMBER_STRING.equals("")))
/*     */       {
/*     */         try
/*     */         {
/*  68 */           BUILD_NUMBER = Long.parseLong(BUILD_NUMBER_STRING);
/*     */         }
/*     */         catch (NumberFormatException nfe)
/*     */         {
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  77 */     return BUILD_NUMBER;
/*     */   }
/*     */ 
/*     */   public static String getBuild()
/*     */   {
/*  82 */     if (BUILD_NUMBER_STRING == null)
/*     */     {
/*     */       Class classToUseForManifest;
/*     */       try
/*     */       {
/*  88 */         classToUseForManifest = Class.forName("flex.data.DataService");
/*     */       }
/*     */       catch (ClassNotFoundException e)
/*     */       {
/*  92 */         classToUseForManifest = VersionInfo.class;
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/*  97 */         BUILD_NUMBER_STRING = "";
/*  98 */         Package pack = classToUseForManifest.getPackage();
/*  99 */         BUILD_NUMBER_STRING = pack.getImplementationVersion();
/* 100 */         BUILD_TITLE = pack.getImplementationTitle();
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 108 */     return BUILD_NUMBER_STRING;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.VersionInfo
 * JD-Core Version:    0.6.0
 */
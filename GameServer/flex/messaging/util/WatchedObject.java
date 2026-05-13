/*    */ package flex.messaging.util;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.FileNotFoundException;
/*    */ 
/*    */ public class WatchedObject
/*    */ {
/*    */   private String filename;
/*    */   private long modified;
/*    */ 
/*    */   public WatchedObject(String filename)
/*    */     throws FileNotFoundException
/*    */   {
/* 34 */     this.filename = filename;
/* 35 */     File file = new File(filename);
/*    */ 
/* 37 */     if ((!file.isFile()) && (!file.isDirectory()))
/*    */     {
/* 39 */       throw new FileNotFoundException();
/*    */     }
/* 41 */     this.modified = file.lastModified();
/*    */   }
/*    */ 
/*    */   public boolean isUptodate()
/*    */   {
/* 46 */     boolean uptodate = true;
/*    */ 
/* 48 */     long current = new File(this.filename).lastModified();
/*    */ 
/* 50 */     if (Math.abs(current - this.modified) > 1000L)
/*    */     {
/* 52 */       uptodate = false;
/*    */     }
/*    */ 
/* 55 */     this.modified = current;
/*    */ 
/* 57 */     return uptodate;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.util.WatchedObject
 * JD-Core Version:    0.6.0
 */
/*    */ package org.logicalcobwebs.concurrent;
/*    */ 
/*    */ public class ReaderPreferenceReadWriteLock extends WriterPreferenceReadWriteLock
/*    */ {
/*    */   protected boolean allowReader()
/*    */   {
/* 27 */     return this.activeWriter_ == null;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.concurrent.ReaderPreferenceReadWriteLock
 * JD-Core Version:    0.6.0
 */
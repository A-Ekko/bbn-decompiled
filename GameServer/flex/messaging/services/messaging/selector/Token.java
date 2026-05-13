/*    */ package flex.messaging.services.messaging.selector;
/*    */ 
/*    */ public class Token
/*    */ {
/*    */   public int kind;
/*    */   public int beginLine;
/*    */   public int beginColumn;
/*    */   public int endLine;
/*    */   public int endColumn;
/*    */   public String image;
/*    */   public Token next;
/*    */   public Token specialToken;
/*    */ 
/*    */   public String toString()
/*    */   {
/* 58 */     return this.image;
/*    */   }
/*    */ 
/*    */   public static final Token newToken(int ofKind)
/*    */   {
/* 75 */     switch (ofKind) {
/*    */     }
/* 77 */     return new Token();
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.messaging.selector.Token
 * JD-Core Version:    0.6.0
 */
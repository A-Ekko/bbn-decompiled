/*    */ package flex.messaging.messages;
/*    */ 
/*    */ import flex.messaging.util.UUIDUtils;
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectInput;
/*    */ import java.io.ObjectOutput;
/*    */ 
/*    */ public class AcknowledgeMessage extends AsyncMessage
/*    */ {
/*    */   private static final long serialVersionUID = 228072709981643313L;
/*    */ 
/*    */   public AcknowledgeMessage()
/*    */   {
/* 45 */     this.messageId = UUIDUtils.createUUID(false);
/* 46 */     this.timestamp = System.currentTimeMillis();
/*    */   }
/*    */ 
/*    */   public void readExternal(ObjectInput input)
/*    */     throws IOException, ClassNotFoundException
/*    */   {
/* 54 */     super.readExternal(input);
/*    */ 
/* 56 */     short[] flagsArray = readFlags(input);
/* 57 */     for (int i = 0; i < flagsArray.length; i++)
/*    */     {
/* 59 */       short flags = flagsArray[i];
/* 60 */       short reservedPosition = 0;
/*    */ 
/* 64 */       if (flags >> reservedPosition == 0)
/*    */         continue;
/* 66 */       for (short j = reservedPosition; j < 6; j = (short)(j + 1))
/*    */       {
/* 68 */         if ((flags >> j & 0x1) == 0)
/*    */           continue;
/* 70 */         input.readObject();
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public Message getSmallMessage()
/*    */   {
/* 82 */     if (getClass() == AcknowledgeMessage.class)
/* 83 */       return new AcknowledgeMessageExt(this);
/* 84 */     return null;
/*    */   }
/*    */ 
/*    */   public void writeExternal(ObjectOutput output)
/*    */     throws IOException
/*    */   {
/* 92 */     super.writeExternal(output);
/*    */ 
/* 94 */     short flags = 0;
/* 95 */     output.writeByte(flags);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.messages.AcknowledgeMessage
 * JD-Core Version:    0.6.0
 */
/*    */ package flex.messaging.io;
/*    */ 
/*    */ import flex.messaging.MessageException;
/*    */ 
/*    */ public class SerializationException extends MessageException
/*    */ {
/*    */   static final long serialVersionUID = -5723542920189973518L;
/*    */   public static final String CLIENT_PACKET_ENCODING = "Client.Packet.Encoding";
/*    */ 
/*    */   public SerializationException()
/*    */   {
/* 44 */     setCode("Client.Packet.Encoding");
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.SerializationException
 * JD-Core Version:    0.6.0
 */
/*    */ package flex.messaging.io.amf;
/*    */ 
/*    */ import flex.messaging.io.SerializationContext;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class Java15AmfMessageSerializer extends AmfMessageSerializer
/*    */ {
/*    */   public void initialize(SerializationContext context, OutputStream out, AmfTrace trace)
/*    */   {
/* 11 */     this.amfOut = new Java15Amf0Output(context);
/* 12 */     this.amfOut.setAvmPlus(this.version > 0);
/* 13 */     this.amfOut.setOutputStream(out);
/*    */ 
/* 15 */     this.debugTrace = trace;
/* 16 */     this.isDebug = (trace != null);
/* 17 */     this.amfOut.setDebugTrace(this.debugTrace);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.Java15AmfMessageSerializer
 * JD-Core Version:    0.6.0
 */
/*    */ package flex.messaging.io.amfx;
/*    */ 
/*    */ import flex.messaging.io.SerializationContext;
/*    */ import flex.messaging.io.amf.AmfTrace;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class Java15AmfxMessageSerializer extends AmfxMessageSerializer
/*    */ {
/*    */   public void initialize(SerializationContext context, OutputStream out, AmfTrace trace)
/*    */   {
/* 12 */     this.amfxOut = new Java15AmfxOutput(context);
/* 13 */     this.amfxOut.setOutputStream(out);
/* 14 */     this.debugTrace = trace;
/* 15 */     this.isDebug = (this.debugTrace != null);
/* 16 */     this.amfxOut.setDebugTrace(trace);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amfx.Java15AmfxMessageSerializer
 * JD-Core Version:    0.6.0
 */
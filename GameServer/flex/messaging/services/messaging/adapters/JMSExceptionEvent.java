/*    */ package flex.messaging.services.messaging.adapters;
/*    */ 
/*    */ import java.util.EventObject;
/*    */ import javax.jms.JMSException;
/*    */ 
/*    */ public class JMSExceptionEvent extends EventObject
/*    */ {
/*    */   private JMSException jmsException;
/*    */ 
/*    */   JMSExceptionEvent(JMSConsumer source, JMSException jmsException)
/*    */   {
/* 24 */     super(source);
/* 25 */     this.jmsException = jmsException;
/*    */   }
/*    */ 
/*    */   public JMSException getJMSException()
/*    */   {
/* 35 */     return this.jmsException;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.messaging.adapters.JMSExceptionEvent
 * JD-Core Version:    0.6.0
 */
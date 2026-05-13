/*     */ package flex.management.runtime.messaging.services.messaging.adapters;
/*     */ 
/*     */ import flex.management.BaseControl;
/*     */ import flex.management.runtime.messaging.services.ServiceAdapterControl;
/*     */ import flex.messaging.services.messaging.adapters.JMSAdapter;
/*     */ 
/*     */ public class JMSAdapterControl extends ServiceAdapterControl
/*     */   implements JMSAdapterControlMBean
/*     */ {
/*     */   private static final String TYPE = "JMSAdapter";
/*     */   private JMSAdapter jmsAdapter;
/*     */ 
/*     */   public JMSAdapterControl(JMSAdapter serviceAdapter, BaseControl parent)
/*     */   {
/*  46 */     super(serviceAdapter, parent);
/*  47 */     this.jmsAdapter = serviceAdapter;
/*     */   }
/*     */ 
/*     */   public String getType()
/*     */   {
/*  56 */     return "JMSAdapter";
/*     */   }
/*     */ 
/*     */   public Integer getTopicProducerCount()
/*     */   {
/*  65 */     return new Integer(this.jmsAdapter.getTopicProducerCount());
/*     */   }
/*     */ 
/*     */   public Integer getTopicConsumerCount()
/*     */   {
/*  74 */     return new Integer(this.jmsAdapter.getTopicConsumerCount());
/*     */   }
/*     */ 
/*     */   public String[] getTopicConsumerIds()
/*     */   {
/*  83 */     return this.jmsAdapter.getTopicConsumerIds();
/*     */   }
/*     */ 
/*     */   public Integer getQueueProducerCount()
/*     */   {
/*  92 */     return new Integer(this.jmsAdapter.getQueueProducerCount());
/*     */   }
/*     */ 
/*     */   public Integer getQueueConsumerCount()
/*     */   {
/* 101 */     return new Integer(this.jmsAdapter.getQueueConsumerCount());
/*     */   }
/*     */ 
/*     */   public String[] getQueueConsumerIds()
/*     */   {
/* 110 */     return this.jmsAdapter.getQueueConsumerIds();
/*     */   }
/*     */ 
/*     */   public void removeConsumer(String consumerId)
/*     */   {
/* 119 */     this.jmsAdapter.removeConsumer(consumerId);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.runtime.messaging.services.messaging.adapters.JMSAdapterControl
 * JD-Core Version:    0.6.0
 */
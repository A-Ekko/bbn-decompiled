/*     */ package flex.messaging.services.messaging.adapters;
/*     */ 
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.CopyOnWriteArrayList;
/*     */ import flex.messaging.MessageException;
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.log.Logger;
/*     */ import java.util.Iterator;
/*     */ import javax.jms.Connection;
/*     */ import javax.jms.ExceptionListener;
/*     */ import javax.jms.JMSException;
/*     */ import javax.jms.Message;
/*     */ import javax.jms.MessageConsumer;
/*     */ import javax.jms.MessageListener;
/*     */ import javax.naming.NamingException;
/*     */ 
/*     */ public abstract class JMSConsumer extends JMSProxy
/*     */   implements ExceptionListener
/*     */ {
/*     */   protected MessageConsumer consumer;
/*     */   protected MessageReceiver messageReceiver;
/*     */   protected String selectorExpression;
/*  50 */   private boolean messageReceiverManuallySet = false;
/*     */ 
/*  55 */   protected Object lock = new Object();
/*     */ 
/*  60 */   private final CopyOnWriteArrayList jmsMessageListeners = new CopyOnWriteArrayList();
/*     */ 
/*  65 */   private final CopyOnWriteArrayList jmsExceptionListeners = new CopyOnWriteArrayList();
/*     */ 
/*     */   public void start()
/*     */     throws NamingException, JMSException
/*     */   {
/*  78 */     super.start();
/*     */ 
/*  80 */     if (Log.isInfo())
/*  81 */       Log.getLogger("Service.Message.JMS").info("JMS consumer for JMS destination '" + this.destinationJndiName + "' is starting.");
/*     */   }
/*     */ 
/*     */   public void stop()
/*     */   {
/*  92 */     if (Log.isInfo()) {
/*  93 */       Log.getLogger("Service.Message.JMS").info("JMS consumer for JMS destination '" + this.destinationJndiName + "' is stopping.");
/*     */     }
/*     */ 
/*  96 */     stopMessageReceiver();
/*     */     try
/*     */     {
/* 100 */       if (this.consumer != null)
/* 101 */         this.consumer.close();
/*     */     }
/*     */     catch (JMSException e)
/*     */     {
/* 105 */       if (Log.isWarn()) {
/* 106 */         Log.getLogger("Service.Message.JMS").warn("JMS consumer for JMS destination '" + this.destinationJndiName + "' received an error while closing its underlying MessageConsumer: " + e.getMessage());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 111 */     super.stop();
/*     */   }
/*     */ 
/*     */   public void stop(boolean unsubscribe)
/*     */   {
/* 124 */     stop();
/*     */   }
/*     */ 
/*     */   public void addJMSMessageListener(JMSMessageListener listener)
/*     */   {
/* 142 */     if (listener != null)
/* 143 */       this.jmsMessageListeners.addIfAbsent(listener);
/*     */   }
/*     */ 
/*     */   public void removeJMSMessageListener(JMSMessageListener listener)
/*     */   {
/* 155 */     if (listener != null)
/* 156 */       this.jmsMessageListeners.remove(listener);
/*     */   }
/*     */ 
/*     */   public void addJMSExceptionListener(JMSExceptionListener listener)
/*     */   {
/* 168 */     if (listener != null)
/* 169 */       this.jmsExceptionListeners.addIfAbsent(listener);
/*     */   }
/*     */ 
/*     */   public void removeJMSExceptionListener(JMSExceptionListener listener)
/*     */   {
/* 181 */     if (listener != null)
/* 182 */       this.jmsExceptionListeners.remove(listener);
/*     */   }
/*     */ 
/*     */   public MessageListener setMessageListener(MessageListener listener)
/*     */     throws JMSException
/*     */   {
/* 196 */     MessageListener oldListener = this.consumer.getMessageListener();
/* 197 */     this.consumer.setMessageListener(listener);
/* 198 */     return oldListener;
/*     */   }
/*     */ 
/*     */   public MessageReceiver getMessageReceiver()
/*     */   {
/* 209 */     return this.messageReceiver;
/*     */   }
/*     */ 
/*     */   public void setMessageReceiver(MessageReceiver messageReceiver)
/*     */   {
/* 220 */     this.messageReceiver = messageReceiver;
/* 221 */     this.messageReceiverManuallySet = true;
/*     */   }
/*     */ 
/*     */   public String getSelectorExpression()
/*     */   {
/* 232 */     return this.selectorExpression;
/*     */   }
/*     */ 
/*     */   public void setSelectorExpression(String selectorExpression)
/*     */   {
/* 244 */     this.selectorExpression = selectorExpression;
/*     */   }
/*     */ 
/*     */   public void onException(JMSException exception)
/*     */   {
/*     */     Iterator iter;
/* 253 */     if (!this.jmsExceptionListeners.isEmpty())
/*     */     {
/* 256 */       for (iter = this.jmsExceptionListeners.iterator(); iter.hasNext(); )
/* 257 */         ((JMSExceptionListener)iter.next()).exceptionThrown(new JMSExceptionEvent(this, exception));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onMessage(Message jmsMessage)
/*     */   {
/* 269 */     acknowledgeMessage(jmsMessage);
/*     */     Iterator iter;
/* 271 */     if (!this.jmsMessageListeners.isEmpty())
/*     */     {
/* 274 */       for (iter = this.jmsMessageListeners.iterator(); iter.hasNext(); )
/* 275 */         ((JMSMessageListener)iter.next()).messageReceived(new JMSMessageEvent(this, jmsMessage));
/*     */     }
/*     */   }
/*     */ 
/*     */   public Message receive()
/*     */     throws JMSException
/*     */   {
/* 285 */     if (Log.isInfo()) {
/* 286 */       Log.getLogger("Service.Message.JMS").info(Thread.currentThread() + " JMS consumer for JMS destination '" + this.destinationJndiName + "' is waiting forever until a new message arrives.");
/*     */     }
/*     */ 
/* 290 */     return this.consumer.receive();
/*     */   }
/*     */ 
/*     */   public Message receive(long timeout)
/*     */     throws JMSException
/*     */   {
/* 301 */     if (Log.isInfo()) {
/* 302 */       Log.getLogger("Service.Message.JMS").info(Thread.currentThread() + " JMS consumer for JMS destination '" + this.destinationJndiName + "' is waiting " + timeout + " ms for new message to arrive");
/*     */     }
/*     */ 
/* 306 */     return this.consumer.receive(timeout);
/*     */   }
/*     */ 
/*     */   public Message receiveNoWait()
/*     */     throws JMSException
/*     */   {
/* 314 */     return this.consumer.receiveNoWait();
/*     */   }
/*     */ 
/*     */   void startMessageReceiver()
/*     */     throws JMSException
/*     */   {
/* 330 */     initializeMessageReceiver();
/* 331 */     this.messageReceiver.startReceive();
/* 332 */     this.connection.start();
/*     */   }
/*     */ 
/*     */   void stopMessageReceiver()
/*     */   {
/* 340 */     if (this.messageReceiver != null)
/* 341 */       this.messageReceiver.stopReceive();
/*     */   }
/*     */ 
/*     */   protected void acknowledgeMessage(Message message)
/*     */   {
/* 349 */     if (getAcknowledgeMode() == 2)
/*     */     {
/*     */       try
/*     */       {
/* 353 */         message.acknowledge();
/*     */       }
/*     */       catch (JMSException e)
/*     */       {
/* 357 */         if (Log.isInfo())
/* 358 */           Log.getLogger("Service.Message.JMS").info("JMS consumer for JMS destination '" + this.destinationJndiName + "' received an error in message acknowledgement: " + e.getMessage());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void initializeMessageReceiver()
/*     */   {
/* 377 */     if ((this.messageReceiverManuallySet) && (this.messageReceiver != null))
/*     */     {
/* 379 */       if ((this.messageReceiver instanceof AsyncMessageReceiver))
/*     */       {
/* 381 */         String restrictedMethod = null;
/*     */         try
/*     */         {
/* 385 */           restrictedMethod = "javax.jms.MessageConsumer.setMessageListener";
/* 386 */           this.consumer.getMessageListener();
/*     */ 
/* 389 */           restrictedMethod = "javax.jms.Connection.setExceptionListener";
/* 390 */           this.connection.setExceptionListener((AsyncMessageReceiver)this.messageReceiver);
/*     */ 
/* 392 */           if (Log.isInfo()) {
/* 393 */             Log.getLogger("Service.Message.JMS").info("JMS consumer for JMS destination '" + this.destinationJndiName + "' is using async message receiver.");
/*     */           }
/*     */ 
/*     */         }
/*     */         catch (JMSException jmsEx)
/*     */         {
/* 399 */           MessageException me = new MessageException();
/* 400 */           me.setMessage(10818, new Object[] { this.destinationJndiName, restrictedMethod });
/* 401 */           throw me;
/*     */         }
/*     */       }
/* 404 */       else if ((this.messageReceiver instanceof SyncMessageReceiver))
/*     */       {
/* 406 */         SyncMessageReceiver smr = (SyncMessageReceiver)this.messageReceiver;
/* 407 */         if (Log.isInfo())
/*     */         {
/* 409 */           Log.getLogger("Service.Message.JMS").info("JMS consumer for JMS destination '" + this.destinationJndiName + "' is using sync message receiver" + " with sync-receive-interval-millis: " + smr.getSyncReceiveIntervalMillis() + ", sync-receive-wait-millis: " + smr.getSyncReceiveWaitMillis());
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */       try
/*     */       {
/* 423 */         this.messageReceiver = new AsyncMessageReceiver(this);
/*     */ 
/* 426 */         this.consumer.getMessageListener();
/*     */ 
/* 428 */         this.connection.setExceptionListener((AsyncMessageReceiver)this.messageReceiver);
/*     */ 
/* 430 */         if (Log.isInfo()) {
/* 431 */           Log.getLogger("Service.Message.JMS").info("JMS consumer for JMS destination '" + this.destinationJndiName + "' is using async message receiver.");
/*     */         }
/*     */       }
/*     */       catch (JMSException e)
/*     */       {
/* 436 */         SyncMessageReceiver smr = new SyncMessageReceiver(this);
/* 437 */         smr.setSyncReceiveIntervalMillis(1L);
/* 438 */         smr.setSyncReceiveWaitMillis(-1L);
/* 439 */         this.messageReceiver = smr;
/*     */ 
/* 441 */         if (Log.isInfo())
/*     */         {
/* 443 */           Log.getLogger("Service.Message.JMS").info("JMS consumer for JMS destination '" + this.destinationJndiName + "' is using sync message receiver" + " with sync-receive-interval-millis: " + smr.getSyncReceiveIntervalMillis() + ", sync-receive-wait-millis: " + smr.getSyncReceiveWaitMillis());
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.messaging.adapters.JMSConsumer
 * JD-Core Version:    0.6.0
 */
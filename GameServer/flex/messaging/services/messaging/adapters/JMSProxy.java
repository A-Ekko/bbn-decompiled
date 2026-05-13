/*     */ package flex.messaging.services.messaging.adapters;
/*     */ 
/*     */ import flex.messaging.config.ConfigurationException;
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.log.Logger;
/*     */ import java.util.Hashtable;
/*     */ import javax.jms.Connection;
/*     */ import javax.jms.ConnectionFactory;
/*     */ import javax.jms.Destination;
/*     */ import javax.jms.JMSException;
/*     */ import javax.jms.Session;
/*     */ import javax.naming.Context;
/*     */ import javax.naming.InitialContext;
/*     */ import javax.naming.NamingException;
/*     */ 
/*     */ public abstract class JMSProxy
/*     */ {
/*     */   protected Connection connection;
/*     */   protected ConnectionCredentials connectionCredentials;
/*     */   protected ConnectionFactory connectionFactory;
/*     */   protected Session session;
/*     */   protected Destination destination;
/*     */   protected Context jndiContext;
/*     */   protected int acknowledgeMode;
/*     */   protected String connectionFactoryName;
/*     */   protected String destinationJndiName;
/*     */   protected Hashtable initialContextEnvironment;
/*     */ 
/*     */   public JMSProxy()
/*     */   {
/*  66 */     this.acknowledgeMode = 1;
/*     */   }
/*     */ 
/*     */   public void initialize(JMSSettings settings)
/*     */   {
/*  82 */     String ackString = settings.getAcknowledgeMode();
/*  83 */     if (ackString.equals("auto_acknowledge"))
/*  84 */       this.acknowledgeMode = 1;
/*  85 */     else if (ackString.equals("client_acknowledge"))
/*  86 */       this.acknowledgeMode = 2;
/*  87 */     else if (ackString.equals("dups_ok_acknowledge")) {
/*  88 */       this.acknowledgeMode = 3;
/*     */     }
/*  90 */     this.connectionFactoryName = settings.getConnectionFactory();
/*  91 */     String username = settings.getConnectionUsername();
/*  92 */     String password = settings.getConnectionPassword();
/*  93 */     if ((username != null) || (password != null))
/*     */     {
/*  95 */       this.connectionCredentials = new ConnectionCredentials(username, password);
/*     */     }
/*  97 */     this.destinationJndiName = settings.getDestinationJNDIName();
/*  98 */     this.initialContextEnvironment = settings.getInitialContextEnvironment();
/*     */   }
/*     */ 
/*     */   protected void validate()
/*     */   {
/* 108 */     if (this.connectionFactoryName == null)
/*     */     {
/* 111 */       ConfigurationException ce = new ConfigurationException();
/* 112 */       ce.setMessage(10804);
/* 113 */       throw ce;
/*     */     }
/*     */ 
/* 116 */     if (this.destinationJndiName == null)
/*     */     {
/* 119 */       ConfigurationException ce = new ConfigurationException();
/* 120 */       ce.setMessage(10807);
/* 121 */       throw ce;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void start()
/*     */     throws NamingException, JMSException
/*     */   {
/* 136 */     validate();
/* 137 */     initializeJndiContext();
/* 138 */     initializeConnectionFactory();
/* 139 */     initializeDestination();
/*     */   }
/*     */ 
/*     */   public void stop()
/*     */   {
/*     */     try
/*     */     {
/* 150 */       if (this.session != null)
/* 151 */         this.session.close();
/*     */     }
/*     */     catch (JMSException e)
/*     */     {
/* 155 */       if (Log.isWarn()) {
/* 156 */         Log.getLogger("Service.Message.JMS").warn("JMS proxy for JMS destination '" + this.destinationJndiName + "' received an error while closing" + " its underlying Session: " + e.getMessage());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 163 */       if (this.connection != null)
/* 164 */         this.connection.close();
/*     */     }
/*     */     catch (JMSException e)
/*     */     {
/* 168 */       if (Log.isWarn()) {
/* 169 */         Log.getLogger("Service.Message.JMS").warn("JMS proxy for JMS destination '" + this.destinationJndiName + "' received an error while closing" + " its underlying Connection: " + e.getMessage());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 176 */       if (this.jndiContext != null)
/* 177 */         this.jndiContext.close();
/*     */     }
/*     */     catch (NamingException e)
/*     */     {
/* 181 */       if (Log.isWarn())
/* 182 */         Log.getLogger("Service.Message.JMS").warn("JMS proxy for JMS destination '" + this.destinationJndiName + "' received an error while closing" + " its underlying JNDI context: " + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getAcknowledgeMode()
/*     */   {
/* 201 */     return this.acknowledgeMode;
/*     */   }
/*     */ 
/*     */   public void setAcknowledgeMode(int acknowledgeMode)
/*     */   {
/* 214 */     if ((acknowledgeMode == 1) || (acknowledgeMode == 2) || (acknowledgeMode == 3))
/*     */     {
/* 217 */       this.acknowledgeMode = acknowledgeMode;
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getConnectionFactoryName()
/*     */   {
/* 227 */     return this.connectionFactoryName;
/*     */   }
/*     */ 
/*     */   public void setConnectionFactoryName(String connectionFactoryName)
/*     */   {
/* 238 */     this.connectionFactoryName = connectionFactoryName;
/*     */   }
/*     */ 
/*     */   public ConnectionCredentials getConnectionCredentials()
/*     */   {
/* 248 */     return this.connectionCredentials;
/*     */   }
/*     */ 
/*     */   public void setConnectionCredentials(ConnectionCredentials connectionCredentials)
/*     */   {
/* 259 */     this.connectionCredentials = connectionCredentials;
/*     */   }
/*     */ 
/*     */   public String getDestinationJndiName()
/*     */   {
/* 269 */     return this.destinationJndiName;
/*     */   }
/*     */ 
/*     */   public void setDestinationJndiName(String destinationJndiName)
/*     */   {
/* 279 */     this.destinationJndiName = destinationJndiName;
/*     */   }
/*     */ 
/*     */   public Hashtable getInitialContextEnvironment()
/*     */   {
/* 289 */     return this.initialContextEnvironment;
/*     */   }
/*     */ 
/*     */   public void setInitialContextEnvironment(Hashtable env)
/*     */   {
/* 300 */     this.initialContextEnvironment = env;
/*     */   }
/*     */ 
/*     */   protected ConnectionFactory initializeConnectionFactory()
/*     */     throws NamingException
/*     */   {
/* 314 */     if (this.connectionFactory == null) {
/* 315 */       this.connectionFactory = ((ConnectionFactory)this.jndiContext.lookup(this.connectionFactoryName));
/*     */     }
/* 317 */     return this.connectionFactory;
/*     */   }
/*     */ 
/*     */   protected Destination initializeDestination()
/*     */     throws NamingException
/*     */   {
/* 325 */     if (this.destination == null) {
/* 326 */       this.destination = ((Destination)this.jndiContext.lookup(this.destinationJndiName));
/*     */     }
/* 328 */     return this.destination;
/*     */   }
/*     */ 
/*     */   protected Context initializeJndiContext()
/*     */     throws NamingException
/*     */   {
/* 337 */     if (this.jndiContext != null) {
/* 338 */       stop();
/*     */     }
/* 340 */     if (this.initialContextEnvironment != null)
/* 341 */       this.jndiContext = new InitialContext(this.initialContextEnvironment);
/*     */     else {
/* 343 */       this.jndiContext = new InitialContext();
/*     */     }
/* 345 */     return this.jndiContext;
/*     */   }
/*     */ 
/*     */   public static class ConnectionCredentials
/*     */   {
/*     */     private String username;
/*     */     private String password;
/*     */ 
/*     */     public ConnectionCredentials(String username, String password)
/*     */     {
/* 372 */       this.username = username;
/* 373 */       this.password = password;
/*     */     }
/*     */ 
/*     */     public String getUsername()
/*     */     {
/* 383 */       return this.username;
/*     */     }
/*     */ 
/*     */     public String getPassword()
/*     */     {
/* 393 */       return this.password;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.messaging.adapters.JMSProxy
 * JD-Core Version:    0.6.0
 */
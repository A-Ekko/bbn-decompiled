/*     */ package org.apache.log4j.net;
/*     */ 
/*     */ import java.util.Hashtable;
/*     */ import java.util.Properties;
/*     */ import javax.jms.Connection;
/*     */ import javax.jms.ObjectMessage;
/*     */ import javax.jms.Session;
/*     */ import javax.jms.Topic;
/*     */ import javax.jms.TopicConnection;
/*     */ import javax.jms.TopicConnectionFactory;
/*     */ import javax.jms.TopicPublisher;
/*     */ import javax.jms.TopicSession;
/*     */ import javax.naming.Context;
/*     */ import javax.naming.InitialContext;
/*     */ import javax.naming.NameNotFoundException;
/*     */ import javax.naming.NamingException;
/*     */ import org.apache.log4j.AppenderSkeleton;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.spi.ErrorHandler;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class JMSAppender extends AppenderSkeleton
/*     */ {
/*     */   String securityPrincipalName;
/*     */   String securityCredentials;
/*     */   String initialContextFactoryName;
/*     */   String urlPkgPrefixes;
/*     */   String providerURL;
/*     */   String topicBindingName;
/*     */   String tcfBindingName;
/*     */   String userName;
/*     */   String password;
/*     */   boolean locationInfo;
/*     */   TopicConnection topicConnection;
/*     */   TopicSession topicSession;
/*     */   TopicPublisher topicPublisher;
/*     */ 
/*     */   public void setTopicConnectionFactoryBindingName(String tcfBindingName)
/*     */   {
/* 129 */     this.tcfBindingName = tcfBindingName;
/*     */   }
/*     */ 
/*     */   public String getTopicConnectionFactoryBindingName()
/*     */   {
/* 137 */     return this.tcfBindingName;
/*     */   }
/*     */ 
/*     */   public void setTopicBindingName(String topicBindingName)
/*     */   {
/* 147 */     this.topicBindingName = topicBindingName;
/*     */   }
/*     */ 
/*     */   public String getTopicBindingName()
/*     */   {
/* 155 */     return this.topicBindingName;
/*     */   }
/*     */ 
/*     */   public boolean getLocationInfo()
/*     */   {
/* 165 */     return this.locationInfo;
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/*     */     try
/*     */     {
/* 177 */       LogLog.debug("Getting initial context.");
/*     */       Context jndi;
/*     */       Context jndi;
/* 178 */       if (this.initialContextFactoryName != null) {
/* 179 */         Properties env = new Properties();
/* 180 */         env.put("java.naming.factory.initial", this.initialContextFactoryName);
/* 181 */         if (this.providerURL != null)
/* 182 */           env.put("java.naming.provider.url", this.providerURL);
/*     */         else {
/* 184 */           LogLog.warn("You have set InitialContextFactoryName option but not the ProviderURL. This is likely to cause problems.");
/*     */         }
/*     */ 
/* 187 */         if (this.urlPkgPrefixes != null) {
/* 188 */           env.put("java.naming.factory.url.pkgs", this.urlPkgPrefixes);
/*     */         }
/*     */ 
/* 191 */         if (this.securityPrincipalName != null) {
/* 192 */           env.put("java.naming.security.principal", this.securityPrincipalName);
/* 193 */           if (this.securityCredentials != null)
/* 194 */             env.put("java.naming.security.credentials", this.securityCredentials);
/*     */           else {
/* 196 */             LogLog.warn("You have set SecurityPrincipalName option but not the SecurityCredentials. This is likely to cause problems.");
/*     */           }
/*     */         }
/*     */ 
/* 200 */         jndi = new InitialContext(env);
/*     */       } else {
/* 202 */         jndi = new InitialContext();
/*     */       }
/*     */ 
/* 205 */       LogLog.debug("Looking up [" + this.tcfBindingName + "]");
/* 206 */       TopicConnectionFactory topicConnectionFactory = (TopicConnectionFactory)lookup(jndi, this.tcfBindingName);
/* 207 */       LogLog.debug("About to create TopicConnection.");
/* 208 */       if (this.userName != null) {
/* 209 */         this.topicConnection = topicConnectionFactory.createTopicConnection(this.userName, this.password);
/*     */       }
/*     */       else {
/* 212 */         this.topicConnection = topicConnectionFactory.createTopicConnection();
/*     */       }
/*     */ 
/* 215 */       LogLog.debug("Creating TopicSession, non-transactional, in AUTO_ACKNOWLEDGE mode.");
/*     */ 
/* 217 */       this.topicSession = this.topicConnection.createTopicSession(false, 1);
/*     */ 
/* 220 */       LogLog.debug("Looking up topic name [" + this.topicBindingName + "].");
/* 221 */       Topic topic = (Topic)lookup(jndi, this.topicBindingName);
/*     */ 
/* 223 */       LogLog.debug("Creating TopicPublisher.");
/* 224 */       this.topicPublisher = this.topicSession.createPublisher(topic);
/*     */ 
/* 226 */       LogLog.debug("Starting TopicConnection.");
/* 227 */       this.topicConnection.start();
/*     */ 
/* 229 */       jndi.close();
/*     */     } catch (Exception e) {
/* 231 */       this.errorHandler.error("Error while activating options for appender named [" + this.name + "].", e, 0);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Object lookup(Context ctx, String name) throws NamingException
/*     */   {
/*     */     try {
/* 238 */       return ctx.lookup(name);
/*     */     } catch (NameNotFoundException e) {
/* 240 */       LogLog.error("Could not find name [" + name + "].");
/* 241 */     }throw e;
/*     */   }
/*     */ 
/*     */   protected boolean checkEntryConditions()
/*     */   {
/* 246 */     String fail = null;
/*     */ 
/* 248 */     if (this.topicConnection == null)
/* 249 */       fail = "No TopicConnection";
/* 250 */     else if (this.topicSession == null)
/* 251 */       fail = "No TopicSession";
/* 252 */     else if (this.topicPublisher == null) {
/* 253 */       fail = "No TopicPublisher";
/*     */     }
/*     */ 
/* 256 */     if (fail != null) {
/* 257 */       this.errorHandler.error(fail + " for JMSAppender named [" + this.name + "].");
/* 258 */       return false;
/*     */     }
/* 260 */     return true;
/*     */   }
/*     */ 
/*     */   public synchronized void close()
/*     */   {
/* 270 */     if (this.closed) {
/* 271 */       return;
/*     */     }
/* 273 */     LogLog.debug("Closing appender [" + this.name + "].");
/* 274 */     this.closed = true;
/*     */     try
/*     */     {
/* 277 */       if (this.topicSession != null)
/* 278 */         this.topicSession.close();
/* 279 */       if (this.topicConnection != null)
/* 280 */         this.topicConnection.close();
/*     */     } catch (Exception e) {
/* 282 */       LogLog.error("Error while closing JMSAppender [" + this.name + "].", e);
/*     */     }
/*     */ 
/* 285 */     this.topicPublisher = null;
/* 286 */     this.topicSession = null;
/* 287 */     this.topicConnection = null;
/*     */   }
/*     */ 
/*     */   public void append(LoggingEvent event)
/*     */   {
/* 294 */     if (!checkEntryConditions()) {
/* 295 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 299 */       ObjectMessage msg = this.topicSession.createObjectMessage();
/* 300 */       if (this.locationInfo) {
/* 301 */         event.getLocationInformation();
/*     */       }
/* 303 */       msg.setObject(event);
/* 304 */       this.topicPublisher.publish(msg);
/*     */     } catch (Exception e) {
/* 306 */       this.errorHandler.error("Could not publish message in JMSAppender [" + this.name + "].", e, 0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getInitialContextFactoryName()
/*     */   {
/* 317 */     return this.initialContextFactoryName;
/*     */   }
/*     */ 
/*     */   public void setInitialContextFactoryName(String initialContextFactoryName)
/*     */   {
/* 330 */     this.initialContextFactoryName = initialContextFactoryName;
/*     */   }
/*     */ 
/*     */   public String getProviderURL() {
/* 334 */     return this.providerURL;
/*     */   }
/*     */ 
/*     */   public void setProviderURL(String providerURL) {
/* 338 */     this.providerURL = providerURL;
/*     */   }
/*     */ 
/*     */   String getURLPkgPrefixes() {
/* 342 */     return this.urlPkgPrefixes;
/*     */   }
/*     */ 
/*     */   public void setURLPkgPrefixes(String urlPkgPrefixes) {
/* 346 */     this.urlPkgPrefixes = urlPkgPrefixes;
/*     */   }
/*     */ 
/*     */   public String getSecurityCredentials() {
/* 350 */     return this.securityCredentials;
/*     */   }
/*     */ 
/*     */   public void setSecurityCredentials(String securityCredentials) {
/* 354 */     this.securityCredentials = securityCredentials;
/*     */   }
/*     */ 
/*     */   public String getSecurityPrincipalName()
/*     */   {
/* 359 */     return this.securityPrincipalName;
/*     */   }
/*     */ 
/*     */   public void setSecurityPrincipalName(String securityPrincipalName) {
/* 363 */     this.securityPrincipalName = securityPrincipalName;
/*     */   }
/*     */ 
/*     */   public String getUserName() {
/* 367 */     return this.userName;
/*     */   }
/*     */ 
/*     */   public void setUserName(String userName)
/*     */   {
/* 378 */     this.userName = userName;
/*     */   }
/*     */ 
/*     */   public String getPassword() {
/* 382 */     return this.password;
/*     */   }
/*     */ 
/*     */   public void setPassword(String password)
/*     */   {
/* 389 */     this.password = password;
/*     */   }
/*     */ 
/*     */   public void setLocationInfo(boolean locationInfo)
/*     */   {
/* 398 */     this.locationInfo = locationInfo;
/*     */   }
/*     */ 
/*     */   protected TopicConnection getTopicConnection()
/*     */   {
/* 406 */     return this.topicConnection;
/*     */   }
/*     */ 
/*     */   protected TopicSession getTopicSession()
/*     */   {
/* 414 */     return this.topicSession;
/*     */   }
/*     */ 
/*     */   protected TopicPublisher getTopicPublisher()
/*     */   {
/* 422 */     return this.topicPublisher;
/*     */   }
/*     */ 
/*     */   public boolean requiresLayout()
/*     */   {
/* 430 */     return false;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.net.JMSAppender
 * JD-Core Version:    0.6.0
 */
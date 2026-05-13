/*     */ package org.apache.log4j.net;
/*     */ 
/*     */ import java.util.Date;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Properties;
/*     */ import javax.mail.Authenticator;
/*     */ import javax.mail.Message;
/*     */ import javax.mail.Message.RecipientType;
/*     */ import javax.mail.MessagingException;
/*     */ import javax.mail.Multipart;
/*     */ import javax.mail.Part;
/*     */ import javax.mail.PasswordAuthentication;
/*     */ import javax.mail.Session;
/*     */ import javax.mail.Transport;
/*     */ import javax.mail.internet.AddressException;
/*     */ import javax.mail.internet.InternetAddress;
/*     */ import javax.mail.internet.MimeBodyPart;
/*     */ import javax.mail.internet.MimeMessage;
/*     */ import javax.mail.internet.MimeMultipart;
/*     */ import org.apache.log4j.AppenderSkeleton;
/*     */ import org.apache.log4j.Layout;
/*     */ import org.apache.log4j.helpers.CyclicBuffer;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.helpers.OptionConverter;
/*     */ import org.apache.log4j.spi.ErrorHandler;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ import org.apache.log4j.spi.OptionHandler;
/*     */ import org.apache.log4j.spi.TriggeringEventEvaluator;
/*     */ import org.apache.log4j.xml.DOMConfigurator;
/*     */ import org.apache.log4j.xml.UnrecognizedElementHandler;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ 
/*     */ public class SMTPAppender extends AppenderSkeleton
/*     */   implements UnrecognizedElementHandler
/*     */ {
/*     */   private String to;
/*     */   private String cc;
/*     */   private String bcc;
/*     */   private String from;
/*     */   private String subject;
/*     */   private String smtpHost;
/*     */   private String smtpUsername;
/*     */   private String smtpPassword;
/*  86 */   private boolean smtpDebug = false;
/*  87 */   private int bufferSize = 512;
/*  88 */   private boolean locationInfo = false;
/*     */ 
/*  90 */   protected CyclicBuffer cb = new CyclicBuffer(this.bufferSize);
/*     */   protected Message msg;
/*     */   protected TriggeringEventEvaluator evaluator;
/*     */ 
/*     */   public SMTPAppender()
/*     */   {
/* 103 */     this(new DefaultEvaluator());
/*     */   }
/*     */ 
/*     */   public SMTPAppender(TriggeringEventEvaluator evaluator)
/*     */   {
/* 112 */     this.evaluator = evaluator;
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/* 121 */     Session session = createSession();
/* 122 */     this.msg = new MimeMessage(session);
/*     */     try
/*     */     {
/* 125 */       addressMessage(this.msg);
/* 126 */       if (this.subject != null)
/* 127 */         this.msg.setSubject(this.subject);
/*     */     }
/*     */     catch (MessagingException e) {
/* 130 */       LogLog.error("Could not activate SMTPAppender options.", e);
/*     */     }
/*     */ 
/* 133 */     if ((this.evaluator instanceof OptionHandler))
/* 134 */       ((OptionHandler)this.evaluator).activateOptions();
/*     */   }
/*     */ 
/*     */   protected void addressMessage(Message msg)
/*     */     throws MessagingException
/*     */   {
/* 144 */     if (this.from != null)
/* 145 */       msg.setFrom(getAddress(this.from));
/*     */     else {
/* 147 */       msg.setFrom();
/*     */     }
/*     */ 
/* 150 */     if ((this.to != null) && (this.to.length() > 0)) {
/* 151 */       msg.setRecipients(Message.RecipientType.TO, parseAddress(this.to));
/*     */     }
/*     */ 
/* 155 */     if ((this.cc != null) && (this.cc.length() > 0)) {
/* 156 */       msg.setRecipients(Message.RecipientType.CC, parseAddress(this.cc));
/*     */     }
/*     */ 
/* 160 */     if ((this.bcc != null) && (this.bcc.length() > 0))
/* 161 */       msg.setRecipients(Message.RecipientType.BCC, parseAddress(this.bcc));
/*     */   }
/*     */ 
/*     */   protected Session createSession()
/*     */   {
/* 170 */     Properties props = null;
/*     */     try {
/* 172 */       props = new Properties(System.getProperties());
/*     */     } catch (SecurityException ex) {
/* 174 */       props = new Properties();
/*     */     }
/* 176 */     if (this.smtpHost != null) {
/* 177 */       props.put("mail.smtp.host", this.smtpHost);
/*     */     }
/*     */ 
/* 180 */     Authenticator auth = null;
/* 181 */     if ((this.smtpPassword != null) && (this.smtpUsername != null)) {
/* 182 */       props.put("mail.smtp.auth", "true");
/* 183 */       auth = new Authenticator() {
/*     */         protected PasswordAuthentication getPasswordAuthentication() {
/* 185 */           return new PasswordAuthentication(SMTPAppender.this.smtpUsername, SMTPAppender.this.smtpPassword);
/*     */         } } ;
/*     */     }
/* 189 */     Session session = Session.getInstance(props, auth);
/* 190 */     if (this.smtpDebug) {
/* 191 */       session.setDebug(this.smtpDebug);
/*     */     }
/* 193 */     return session;
/*     */   }
/*     */ 
/*     */   public void append(LoggingEvent event)
/*     */   {
/* 203 */     if (!checkEntryConditions()) {
/* 204 */       return;
/*     */     }
/*     */ 
/* 207 */     event.getThreadName();
/* 208 */     event.getNDC();
/* 209 */     event.getMDCCopy();
/* 210 */     if (this.locationInfo) {
/* 211 */       event.getLocationInformation();
/*     */     }
/* 213 */     this.cb.add(event);
/* 214 */     if (this.evaluator.isTriggeringEvent(event))
/* 215 */       sendBuffer();
/*     */   }
/*     */ 
/*     */   protected boolean checkEntryConditions()
/*     */   {
/* 227 */     if (this.msg == null) {
/* 228 */       this.errorHandler.error("Message object not configured.");
/* 229 */       return false;
/*     */     }
/*     */ 
/* 232 */     if (this.evaluator == null) {
/* 233 */       this.errorHandler.error("No TriggeringEventEvaluator is set for appender [" + this.name + "].");
/*     */ 
/* 235 */       return false;
/*     */     }
/*     */ 
/* 239 */     if (this.layout == null) {
/* 240 */       this.errorHandler.error("No layout set for appender named [" + this.name + "].");
/* 241 */       return false;
/*     */     }
/* 243 */     return true;
/*     */   }
/*     */ 
/*     */   public synchronized void close()
/*     */   {
/* 250 */     this.closed = true;
/*     */   }
/*     */ 
/*     */   InternetAddress getAddress(String addressStr) {
/*     */     try {
/* 255 */       return new InternetAddress(addressStr);
/*     */     } catch (AddressException e) {
/* 257 */       this.errorHandler.error("Could not parse address [" + addressStr + "].", e, 6);
/*     */     }
/* 259 */     return null;
/*     */   }
/*     */ 
/*     */   InternetAddress[] parseAddress(String addressStr)
/*     */   {
/*     */     try {
/* 265 */       return InternetAddress.parse(addressStr, true);
/*     */     } catch (AddressException e) {
/* 267 */       this.errorHandler.error("Could not parse address [" + addressStr + "].", e, 6);
/*     */     }
/* 269 */     return null;
/*     */   }
/*     */ 
/*     */   public String getTo()
/*     */   {
/* 278 */     return this.to;
/*     */   }
/*     */ 
/*     */   public boolean requiresLayout()
/*     */   {
/* 287 */     return true;
/*     */   }
/*     */ 
/*     */   protected void sendBuffer()
/*     */   {
/*     */     try
/*     */     {
/* 299 */       MimeBodyPart part = new MimeBodyPart();
/*     */ 
/* 301 */       StringBuffer sbuf = new StringBuffer();
/* 302 */       String t = this.layout.getHeader();
/* 303 */       if (t != null)
/* 304 */         sbuf.append(t);
/* 305 */       int len = this.cb.length();
/* 306 */       for (int i = 0; i < len; i++)
/*     */       {
/* 308 */         LoggingEvent event = this.cb.get();
/* 309 */         sbuf.append(this.layout.format(event));
/* 310 */         if (this.layout.ignoresThrowable()) {
/* 311 */           String[] s = event.getThrowableStrRep();
/* 312 */           if (s != null) {
/* 313 */             for (int j = 0; j < s.length; j++) {
/* 314 */               sbuf.append(s[j]);
/* 315 */               sbuf.append(Layout.LINE_SEP);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 320 */       t = this.layout.getFooter();
/* 321 */       if (t != null)
/* 322 */         sbuf.append(t);
/* 323 */       part.setContent(sbuf.toString(), this.layout.getContentType());
/*     */ 
/* 325 */       Multipart mp = new MimeMultipart();
/* 326 */       mp.addBodyPart(part);
/* 327 */       this.msg.setContent(mp);
/*     */ 
/* 329 */       this.msg.setSentDate(new Date());
/* 330 */       Transport.send(this.msg);
/*     */     } catch (Exception e) {
/* 332 */       LogLog.error("Error occured while sending e-mail notification.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getEvaluatorClass()
/*     */   {
/* 343 */     return this.evaluator == null ? null : this.evaluator.getClass().getName();
/*     */   }
/*     */ 
/*     */   public String getFrom()
/*     */   {
/* 351 */     return this.from;
/*     */   }
/*     */ 
/*     */   public String getSubject()
/*     */   {
/* 359 */     return this.subject;
/*     */   }
/*     */ 
/*     */   public void setFrom(String from)
/*     */   {
/* 368 */     this.from = from;
/*     */   }
/*     */ 
/*     */   public void setSubject(String subject)
/*     */   {
/* 377 */     this.subject = subject;
/*     */   }
/*     */ 
/*     */   public void setBufferSize(int bufferSize)
/*     */   {
/* 390 */     this.bufferSize = bufferSize;
/* 391 */     this.cb.resize(bufferSize);
/*     */   }
/*     */ 
/*     */   public void setSMTPHost(String smtpHost)
/*     */   {
/* 400 */     this.smtpHost = smtpHost;
/*     */   }
/*     */ 
/*     */   public String getSMTPHost()
/*     */   {
/* 408 */     return this.smtpHost;
/*     */   }
/*     */ 
/*     */   public void setTo(String to)
/*     */   {
/* 417 */     this.to = to;
/*     */   }
/*     */ 
/*     */   public int getBufferSize()
/*     */   {
/* 427 */     return this.bufferSize;
/*     */   }
/*     */ 
/*     */   public void setEvaluatorClass(String value)
/*     */   {
/* 439 */     this.evaluator = ((TriggeringEventEvaluator)OptionConverter.instantiateByClassName(value, TriggeringEventEvaluator.class, this.evaluator));
/*     */   }
/*     */ 
/*     */   public void setLocationInfo(boolean locationInfo)
/*     */   {
/* 459 */     this.locationInfo = locationInfo;
/*     */   }
/*     */ 
/*     */   public boolean getLocationInfo()
/*     */   {
/* 467 */     return this.locationInfo;
/*     */   }
/*     */ 
/*     */   public void setCc(String addresses)
/*     */   {
/* 475 */     this.cc = addresses;
/*     */   }
/*     */ 
/*     */   public String getCc()
/*     */   {
/* 483 */     return this.cc;
/*     */   }
/*     */ 
/*     */   public void setBcc(String addresses)
/*     */   {
/* 491 */     this.bcc = addresses;
/*     */   }
/*     */ 
/*     */   public String getBcc()
/*     */   {
/* 499 */     return this.bcc;
/*     */   }
/*     */ 
/*     */   public void setSMTPPassword(String password)
/*     */   {
/* 508 */     this.smtpPassword = password;
/*     */   }
/*     */ 
/*     */   public void setSMTPUsername(String username)
/*     */   {
/* 517 */     this.smtpUsername = username;
/*     */   }
/*     */ 
/*     */   public void setSMTPDebug(boolean debug)
/*     */   {
/* 527 */     this.smtpDebug = debug;
/*     */   }
/*     */ 
/*     */   public String getSMTPPassword()
/*     */   {
/* 535 */     return this.smtpPassword;
/*     */   }
/*     */ 
/*     */   public String getSMTPUsername()
/*     */   {
/* 543 */     return this.smtpUsername;
/*     */   }
/*     */ 
/*     */   public boolean getSMTPDebug()
/*     */   {
/* 551 */     return this.smtpDebug;
/*     */   }
/*     */ 
/*     */   public final void setEvaluator(TriggeringEventEvaluator trigger)
/*     */   {
/* 560 */     if (trigger == null) {
/* 561 */       throw new NullPointerException("trigger");
/*     */     }
/* 563 */     this.evaluator = trigger;
/*     */   }
/*     */ 
/*     */   public final TriggeringEventEvaluator getEvaluator()
/*     */   {
/* 572 */     return this.evaluator;
/*     */   }
/*     */ 
/*     */   public boolean parseUnrecognizedElement(Element element, Properties props)
/*     */     throws Exception
/*     */   {
/* 578 */     if ("triggeringPolicy".equals(element.getNodeName())) {
/* 579 */       Object triggerPolicy = DOMConfigurator.parseElement(element, props, TriggeringEventEvaluator.class);
/*     */ 
/* 582 */       if ((triggerPolicy instanceof TriggeringEventEvaluator)) {
/* 583 */         setEvaluator((TriggeringEventEvaluator)triggerPolicy);
/*     */       }
/* 585 */       return true;
/*     */     }
/*     */ 
/* 588 */     return false;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.net.SMTPAppender
 * JD-Core Version:    0.6.0
 */
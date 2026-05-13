/*     */ package flex.messaging;
/*     */ 
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap;
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.log.Logger;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.security.Principal;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Map;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpSession;
/*     */ import javax.servlet.http.HttpSessionAttributeListener;
/*     */ import javax.servlet.http.HttpSessionBindingEvent;
/*     */ import javax.servlet.http.HttpSessionBindingListener;
/*     */ import javax.servlet.http.HttpSessionEvent;
/*     */ import javax.servlet.http.HttpSessionListener;
/*     */ 
/*     */ public class HttpFlexSession extends FlexSession
/*     */   implements HttpSessionBindingListener, HttpSessionListener, HttpSessionAttributeListener, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -1260409488935306147L;
/*     */   private static final String SESSION_ATTRIBUTE = "__flexSession";
/*     */   private static final String INVALIDATED_REQUEST = "__flexInvalidatedRequest";
/*     */   public static final String SESSION_MAP = "LCDS_HTTP_TO_FLEX_SESSION_MAP";
/*     */   private static volatile boolean isHttpSessionListener;
/*     */   private static volatile boolean warnedNoEventRedispatch;
/* 110 */   private static String WARN_LOG_CATEGORY = "Configuration";
/*     */   private HttpSession httpSession;
/* 127 */   public static final Object mapLock = new Object();
/*     */ 
/*     */   public void attributeAdded(HttpSessionBindingEvent event)
/*     */   {
/* 144 */     if (!event.getName().equals("__flexSession"))
/*     */     {
/* 147 */       Map httpSessionToFlexSessionMap = getHttpSessionToFlexSessionMap(event.getSession());
/* 148 */       HttpFlexSession flexSession = (HttpFlexSession)httpSessionToFlexSessionMap.get(event.getSession().getId());
/* 149 */       if (flexSession != null)
/*     */       {
/* 151 */         String name = event.getName();
/* 152 */         Object value = event.getValue();
/* 153 */         flexSession.notifyAttributeBound(name, value);
/* 154 */         flexSession.notifyAttributeAdded(name, value);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void attributeRemoved(HttpSessionBindingEvent event)
/*     */   {
/* 167 */     if (!event.getName().equals("__flexSession"))
/*     */     {
/* 170 */       Map httpSessionToFlexSessionMap = getHttpSessionToFlexSessionMap(event.getSession());
/* 171 */       HttpFlexSession flexSession = (HttpFlexSession)httpSessionToFlexSessionMap.get(event.getSession().getId());
/* 172 */       if (flexSession != null)
/*     */       {
/* 174 */         String name = event.getName();
/* 175 */         Object value = event.getValue();
/* 176 */         flexSession.notifyAttributeUnbound(name, value);
/* 177 */         flexSession.notifyAttributeRemoved(name, value);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void attributeReplaced(HttpSessionBindingEvent event)
/*     */   {
/* 190 */     if (!event.getName().equals("__flexSession"))
/*     */     {
/* 193 */       Map httpSessionToFlexSessionMap = getHttpSessionToFlexSessionMap(event.getSession());
/* 194 */       HttpFlexSession flexSession = (HttpFlexSession)httpSessionToFlexSessionMap.get(event.getSession().getId());
/* 195 */       if (flexSession != null)
/*     */       {
/* 197 */         String name = event.getName();
/* 198 */         Object value = event.getValue();
/* 199 */         Object newValue = flexSession.getAttribute(name);
/* 200 */         flexSession.notifyAttributeUnbound(name, value);
/* 201 */         flexSession.notifyAttributeReplaced(name, value);
/* 202 */         flexSession.notifyAttributeBound(name, newValue);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static HttpFlexSession getFlexSession(HttpServletRequest req)
/*     */   {
/* 218 */     HttpSession httpSession = req.getSession(true);
/*     */ 
/* 220 */     if ((!isHttpSessionListener) && (!warnedNoEventRedispatch))
/*     */     {
/* 222 */       warnedNoEventRedispatch = true;
/* 223 */       if (Log.isWarn()) {
/* 224 */         Log.getLogger(WARN_LOG_CATEGORY).warn("HttpFlexSession has not been registered as a listener in web.xml for this application so no events will be dispatched to FlexSessionAttributeListeners or FlexSessionBindingListeners. To correct this, register flex.messaging.HttpFlexSession as a listener in web.xml.");
/*     */       }
/*     */     }
/* 227 */     boolean isNew = false;
/*     */     HttpFlexSession flexSession;
/* 228 */     synchronized (httpSession)
/*     */     {
/* 230 */       flexSession = (HttpFlexSession)httpSession.getAttribute("__flexSession");
/* 231 */       if (flexSession == null)
/*     */       {
/* 233 */         flexSession = new HttpFlexSession();
/*     */ 
/* 235 */         FlexContext.setThreadLocalSession(flexSession);
/* 236 */         httpSession.setAttribute("__flexSession", flexSession);
/* 237 */         flexSession.setHttpSession(httpSession);
/* 238 */         isNew = true;
/*     */       }
/*     */       else
/*     */       {
/* 242 */         FlexContext.setThreadLocalSession(flexSession);
/* 243 */         if (flexSession.httpSession == null)
/*     */         {
/* 247 */           flexSession.setHttpSession(httpSession);
/* 248 */           isNew = true;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 253 */     if (isNew)
/*     */     {
/* 255 */       flexSession.notifyCreated();
/*     */ 
/* 257 */       if (Log.isDebug()) {
/* 258 */         Log.getLogger("Endpoint.FlexSession").debug("FlexSession created with id '" + flexSession.getId() + "' for an Http-based client connection.");
/*     */       }
/*     */     }
/* 261 */     return flexSession;
/*     */   }
/*     */ 
/*     */   public Principal getUserPrincipal()
/*     */   {
/* 272 */     Principal p = super.getUserPrincipal();
/* 273 */     if (p == null)
/*     */     {
/* 275 */       HttpServletRequest req = FlexContext.getHttpRequest();
/* 276 */       if ((req != null) && (req.getAttribute("INVALIDATED_REQUEST") == null))
/* 277 */         p = req.getUserPrincipal();
/*     */     }
/* 279 */     return p;
/*     */   }
/*     */ 
/*     */   public void invalidate()
/*     */   {
/* 290 */     boolean recreate = FlexContext.getFlexSession() == this;
/* 291 */     invalidate(recreate);
/*     */   }
/*     */ 
/*     */   public void invalidate(boolean recreate)
/*     */   {
/* 303 */     synchronized (this.httpSession)
/*     */     {
/* 305 */       this.httpSession.invalidate();
/*     */     }
/*     */ 
/* 310 */     if (recreate)
/*     */     {
/* 312 */       HttpServletRequest req = FlexContext.getHttpRequest();
/*     */ 
/* 314 */       if (req != null)
/*     */       {
/* 318 */         req.setAttribute("INVALIDATED_REQUEST", "true");
/*     */ 
/* 320 */         FlexContext.setThreadLocalObjects(FlexContext.getFlexClient(), getFlexSession(req), FlexContext.getMessageBroker(), req, FlexContext.getHttpResponse(), FlexContext.getServletConfig());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object getAttribute(String name)
/*     */   {
/* 337 */     return this.httpSession.getAttribute(name);
/*     */   }
/*     */ 
/*     */   public Enumeration getAttributeNames()
/*     */   {
/* 347 */     return this.httpSession.getAttributeNames();
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/* 357 */     return this.httpSession.getId();
/*     */   }
/*     */ 
/*     */   public boolean isPushSupported()
/*     */   {
/* 369 */     return false;
/*     */   }
/*     */ 
/*     */   public void removeAttribute(String name)
/*     */   {
/* 379 */     this.httpSession.removeAttribute(name);
/*     */   }
/*     */ 
/*     */   public void sessionCreated(HttpSessionEvent event)
/*     */   {
/* 392 */     isHttpSessionListener = true;
/*     */   }
/*     */ 
/*     */   public void sessionDestroyed(HttpSessionEvent event)
/*     */   {
/* 403 */     HttpSession session = event.getSession();
/* 404 */     Map httpSessionToFlexSessionMap = getHttpSessionToFlexSessionMap(session);
/* 405 */     HttpFlexSession flexSession = (HttpFlexSession)httpSessionToFlexSessionMap.remove(session.getId());
/*     */     Enumeration e;
/* 406 */     if (flexSession != null)
/*     */     {
/* 409 */       flexSession.superInvalidate();
/*     */ 
/* 414 */       for (e = session.getAttributeNames(); e.hasMoreElements(); )
/*     */       {
/* 416 */         String name = (String)e.nextElement();
/* 417 */         if (name.equals("__flexSession"))
/*     */           continue;
/* 419 */         Object value = session.getAttribute(name);
/* 420 */         if (value != null)
/*     */         {
/* 422 */           flexSession.notifyAttributeUnbound(name, value);
/* 423 */           flexSession.notifyAttributeRemoved(name, value);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setAttribute(String name, Object value)
/*     */   {
/* 439 */     this.httpSession.setAttribute(name, value);
/*     */   }
/*     */ 
/*     */   public void valueBound(HttpSessionBindingEvent event)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void valueUnbound(HttpSessionBindingEvent event)
/*     */   {
/* 463 */     if (!isHttpSessionListener)
/*     */     {
/* 465 */       Map httpSessionToFlexSessionMap = getHttpSessionToFlexSessionMap(event.getSession());
/* 466 */       HttpFlexSession flexSession = (HttpFlexSession)httpSessionToFlexSessionMap.remove(event.getSession().getId());
/* 467 */       if (flexSession != null)
/* 468 */         flexSession.superInvalidate();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void internalInvalidate()
/*     */   {
/* 483 */     if (Log.isDebug())
/* 484 */       Log.getLogger("Endpoint.FlexSession").debug("FlexSession with id '" + getId() + "' for an Http-based client connection has been invalidated.");
/*     */   }
/*     */ 
/*     */   private void setHttpSession(HttpSession httpSession)
/*     */   {
/* 500 */     synchronized (this.lock)
/*     */     {
/* 502 */       this.httpSession = httpSession;
/*     */ 
/* 504 */       Map httpSessionToFlexSessionMap = getHttpSessionToFlexSessionMap(httpSession);
/* 505 */       httpSessionToFlexSessionMap.put(httpSession.getId(), this);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void superInvalidate()
/*     */   {
/* 516 */     super.invalidate();
/*     */   }
/*     */ 
/*     */   private void writeObject(ObjectOutputStream stream)
/*     */   {
/*     */     try
/*     */     {
/* 529 */       Principal principal = super.getUserPrincipal();
/* 530 */       if ((principal != null) && ((principal instanceof Serializable)))
/* 531 */         stream.writeObject(principal);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/*     */     }
/*     */     catch (LocalizedException ignore)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream stream)
/*     */   {
/*     */     try
/*     */     {
/* 557 */       setUserPrincipal((Principal)stream.readObject());
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   private Map getHttpSessionToFlexSessionMap(HttpSession session)
/*     */   {
/*     */     try
/*     */     {
/* 586 */       ServletContext context = session.getServletContext();
/* 587 */       Map map = (Map)context.getAttribute("LCDS_HTTP_TO_FLEX_SESSION_MAP");
/*     */ 
/* 589 */       if (map == null)
/*     */       {
/* 591 */         if (Log.isError()) {
/* 592 */           Log.getLogger("Endpoint.FlexSession").error("HttpSession to FlexSession map not created in message broker for " + session.getId());
/*     */         }
/* 594 */         MessageException me = new MessageException();
/* 595 */         me.setMessage(10032, new Object[] { session.getId() });
/* 596 */         throw me;
/*     */       }
/* 598 */       return map;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 602 */       if (Log.isDebug())
/* 603 */         Log.getLogger("Endpoint.FlexSession").debug("Unable to get HttpSession to FlexSession map for " + session.getId() + " " + e.toString());
/*     */     }
/* 605 */     return new ConcurrentHashMap();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.HttpFlexSession
 * JD-Core Version:    0.6.0
 */
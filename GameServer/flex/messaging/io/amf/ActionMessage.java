/*     */ package flex.messaging.io.amf;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ public class ActionMessage
/*     */   implements Serializable
/*     */ {
/*     */   static final long serialVersionUID = 7970778672727624188L;
/*     */   public static final int CURRENT_VERSION = 3;
/*     */   private int version;
/*  49 */   private ArrayList headers = null;
/*     */ 
/*  54 */   private ArrayList bodies = null;
/*     */ 
/*     */   public ActionMessage()
/*     */   {
/*  63 */     this.version = 3;
/*  64 */     this.headers = new ArrayList();
/*  65 */     this.bodies = new ArrayList();
/*     */   }
/*     */ 
/*     */   public ActionMessage(int version)
/*     */   {
/*  76 */     this.version = version;
/*  77 */     this.headers = new ArrayList();
/*  78 */     this.bodies = new ArrayList();
/*     */   }
/*     */ 
/*     */   public int getVersion()
/*     */   {
/*  89 */     return this.version;
/*     */   }
/*     */ 
/*     */   public void setVersion(int version)
/*     */   {
/*  99 */     this.version = version;
/*     */   }
/*     */ 
/*     */   public int getHeaderCount()
/*     */   {
/* 110 */     return this.headers.size();
/*     */   }
/*     */ 
/*     */   public MessageHeader getHeader(int pos)
/*     */   {
/* 122 */     return (MessageHeader)this.headers.get(pos);
/*     */   }
/*     */ 
/*     */   public ArrayList getHeaders()
/*     */   {
/* 133 */     return this.headers;
/*     */   }
/*     */ 
/*     */   public void addHeader(MessageHeader h)
/*     */   {
/* 143 */     this.headers.add(h);
/*     */   }
/*     */ 
/*     */   public int getBodyCount()
/*     */   {
/* 154 */     return this.bodies.size();
/*     */   }
/*     */ 
/*     */   public MessageBody getBody(int pos)
/*     */   {
/* 166 */     return (MessageBody)this.bodies.get(pos);
/*     */   }
/*     */ 
/*     */   public ArrayList getBodies()
/*     */   {
/* 178 */     return this.bodies;
/*     */   }
/*     */ 
/*     */   public void addBody(MessageBody b)
/*     */   {
/* 188 */     this.bodies.add(b);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.ActionMessage
 * JD-Core Version:    0.6.0
 */
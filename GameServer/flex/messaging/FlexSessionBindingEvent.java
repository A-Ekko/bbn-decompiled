/*     */ package flex.messaging;
/*     */ 
/*     */ public class FlexSessionBindingEvent
/*     */ {
/*     */   private FlexSession session;
/*     */   private String name;
/*     */   private Object value;
/*     */ 
/*     */   public FlexSessionBindingEvent(FlexSession session, String name)
/*     */   {
/*  41 */     this.session = session;
/*  42 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public FlexSessionBindingEvent(FlexSession session, String name, Object value)
/*     */   {
/*  55 */     this.session = session;
/*  56 */     this.name = name;
/*  57 */     this.value = value;
/*     */   }
/*     */ 
/*     */   public FlexSession getSession()
/*     */   {
/*  94 */     return this.session;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 104 */     return this.name;
/*     */   }
/*     */ 
/*     */   public Object getValue()
/*     */   {
/* 114 */     return this.value;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.FlexSessionBindingEvent
 * JD-Core Version:    0.6.0
 */
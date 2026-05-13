/*     */ package flex.messaging.client;
/*     */ 
/*     */ public class FlexClientBindingEvent
/*     */ {
/*     */   private FlexClient client;
/*     */   private String name;
/*     */   private Object value;
/*     */ 
/*     */   public FlexClientBindingEvent(FlexClient client, String name)
/*     */   {
/*  41 */     this.client = client;
/*  42 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public FlexClientBindingEvent(FlexClient client, String name, Object value)
/*     */   {
/*  56 */     this.client = client;
/*  57 */     this.name = name;
/*  58 */     this.value = value;
/*     */   }
/*     */ 
/*     */   public FlexClient getClient()
/*     */   {
/*  95 */     return this.client;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 105 */     return this.name;
/*     */   }
/*     */ 
/*     */   public Object getValue()
/*     */   {
/* 115 */     return this.value;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.client.FlexClientBindingEvent
 * JD-Core Version:    0.6.0
 */
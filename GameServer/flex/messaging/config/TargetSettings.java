/*    */ package flex.messaging.config;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class TargetSettings extends PropertiesSettings
/*    */ {
/*    */   private String className;
/*    */   private String level;
/*    */   private List filters;
/*    */ 
/*    */   public TargetSettings(String className)
/*    */   {
/* 43 */     this.className = className;
/*    */   }
/*    */ 
/*    */   public String getClassName()
/*    */   {
/* 48 */     return this.className;
/*    */   }
/*    */ 
/*    */   public String getLevel()
/*    */   {
/* 53 */     return this.level;
/*    */   }
/*    */ 
/*    */   public void setLevel(String level)
/*    */   {
/* 58 */     this.level = level;
/*    */   }
/*    */ 
/*    */   public List getFilters()
/*    */   {
/* 63 */     return this.filters;
/*    */   }
/*    */ 
/*    */   public void addFilter(String filter)
/*    */   {
/* 68 */     if (this.filters == null) {
/* 69 */       this.filters = new ArrayList();
/*    */     }
/*    */ 
/* 73 */     if ((filter.startsWith("DataService")) && (!filter.equals("DataService.coldfusion"))) {
/* 74 */       filter = filter.replaceFirst("DataService", "Service.Data");
/*    */     }
/* 76 */     this.filters.add(filter);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.TargetSettings
 * JD-Core Version:    0.6.0
 */
/*    */ package flex.messaging.config;
/*    */ 
/*    */ import java.util.List;
/*    */ 
/*    */ public abstract class PropertiesSettings
/*    */ {
/*    */   protected final ConfigMap properties;
/*    */ 
/*    */   public PropertiesSettings()
/*    */   {
/* 44 */     this.properties = new ConfigMap();
/*    */   }
/*    */ 
/*    */   public final void addProperties(ConfigMap p)
/*    */   {
/* 49 */     this.properties.addProperties(p);
/*    */   }
/*    */ 
/*    */   public ConfigMap getProperties()
/*    */   {
/* 54 */     return this.properties;
/*    */   }
/*    */ 
/*    */   public final String getProperty(String name)
/*    */   {
/* 59 */     return getPropertyAsString(name, null);
/*    */   }
/*    */ 
/*    */   public final void addProperty(String name, String value)
/*    */   {
/* 64 */     this.properties.addProperty(name, value);
/*    */   }
/*    */ 
/*    */   public final void addProperty(String name, ConfigMap value)
/*    */   {
/* 69 */     this.properties.addProperty(name, value);
/*    */   }
/*    */ 
/*    */   public final ConfigMap getPropertyAsMap(String name, ConfigMap defaultValue)
/*    */   {
/* 74 */     return this.properties.getPropertyAsMap(name, defaultValue);
/*    */   }
/*    */ 
/*    */   public final String getPropertyAsString(String name, String defaultValue)
/*    */   {
/* 79 */     return this.properties.getPropertyAsString(name, defaultValue);
/*    */   }
/*    */ 
/*    */   public final List getPropertyAsList(String name, List defaultValue)
/*    */   {
/* 84 */     return this.properties.getPropertyAsList(name, defaultValue);
/*    */   }
/*    */ 
/*    */   public final int getPropertyAsInt(String name, int defaultValue)
/*    */   {
/* 89 */     return this.properties.getPropertyAsInt(name, defaultValue);
/*    */   }
/*    */ 
/*    */   public final boolean getPropertyAsBoolean(String name, boolean defaultValue)
/*    */   {
/* 94 */     return this.properties.getPropertyAsBoolean(name, defaultValue);
/*    */   }
/*    */ 
/*    */   public final long getPropertyAsLong(String name, long defaultValue)
/*    */   {
/* 99 */     return this.properties.getPropertyAsLong(name, defaultValue);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.PropertiesSettings
 * JD-Core Version:    0.6.0
 */
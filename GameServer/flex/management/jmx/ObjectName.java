/*    */ package flex.management.jmx;
/*    */ 
/*    */ import java.util.Hashtable;
/*    */ import javax.management.MalformedObjectNameException;
/*    */ 
/*    */ public class ObjectName
/*    */ {
/*    */   public String canonicalKeyPropertyListString;
/*    */   public String canonicalName;
/*    */   public String domain;
/*    */   public Hashtable keyPropertyList;
/*    */   public String keyPropertyListString;
/*    */   public boolean pattern;
/*    */   public boolean propertyPattern;
/*    */ 
/*    */   public ObjectName()
/*    */   {
/*    */   }
/*    */ 
/*    */   public ObjectName(javax.management.ObjectName objectName)
/*    */   {
/* 81 */     this.canonicalKeyPropertyListString = objectName.getCanonicalKeyPropertyListString();
/* 82 */     this.canonicalName = objectName.getCanonicalName();
/* 83 */     this.domain = objectName.getDomain();
/* 84 */     this.keyPropertyList = objectName.getKeyPropertyList();
/* 85 */     this.keyPropertyListString = objectName.getKeyPropertyListString();
/* 86 */     this.pattern = objectName.isPattern();
/* 87 */     this.propertyPattern = objectName.isPropertyPattern();
/*    */   }
/*    */ 
/*    */   public javax.management.ObjectName toObjectName()
/*    */     throws MalformedObjectNameException
/*    */   {
/* 98 */     return new javax.management.ObjectName(this.domain, this.keyPropertyList);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.jmx.ObjectName
 * JD-Core Version:    0.6.0
 */
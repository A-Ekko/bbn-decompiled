/*    */ package flex.management.runtime;
/*    */ 
/*    */ import flex.management.BaseControl;
/*    */ import java.io.IOException;
/*    */ import java.util.ArrayList;
/*    */ import java.util.HashMap;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class AdminConsoleDisplayRegistrar extends BaseControl
/*    */   implements AdminConsoleDisplayRegistrarMBean
/*    */ {
/*    */   public static final String ID = "AdminConsoleDisplay";
/*    */   private HashMap registeredExposedObjects;
/*    */ 
/*    */   public AdminConsoleDisplayRegistrar(BaseControl parent)
/*    */   {
/* 17 */     super(parent);
/* 18 */     this.registeredExposedObjects = new HashMap();
/* 19 */     register();
/*    */   }
/*    */ 
/*    */   public void registerObject(int type, String beanName, String propertyName)
/*    */   {
/* 24 */     Object objects = this.registeredExposedObjects.get(new Integer(type));
/* 25 */     if (objects != null)
/*    */     {
/* 27 */       ((ArrayList)objects).add(beanName + ":" + propertyName);
/*    */     }
/*    */     else
/*    */     {
/* 31 */       if (type < 1) {
/* 32 */         return;
/*    */       }
/* 34 */       objects = new ArrayList();
/* 35 */       ((ArrayList)objects).add(beanName + ":" + propertyName);
/* 36 */       this.registeredExposedObjects.put(new Integer(type), objects);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void registerObjects(int type, String beanName, String[] propertyNames)
/*    */   {
/* 42 */     for (int i = 0; i < propertyNames.length; i++)
/*    */     {
/* 44 */       registerObject(type, beanName, propertyNames[i]);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void registerObjects(int[] types, String beanName, String[] propertyNames)
/*    */   {
/* 50 */     for (int j = 0; j < types.length; j++)
/*    */     {
/* 52 */       registerObjects(types[j], beanName, propertyNames);
/*    */     }
/*    */   }
/*    */ 
/*    */   public Integer[] getSupportedTypes() throws IOException
/*    */   {
/* 58 */     Object[] array = this.registeredExposedObjects.keySet().toArray();
/* 59 */     Integer[] types = new Integer[array.length];
/* 60 */     for (int i = 0; i < array.length; i++)
/*    */     {
/* 62 */       types[i] = ((Integer)array[i]);
/*    */     }
/* 64 */     return types;
/*    */   }
/*    */ 
/*    */   public String[] listForType(int type) throws IOException
/*    */   {
/* 69 */     Object list = this.registeredExposedObjects.get(new Integer(type));
/*    */ 
/* 71 */     return list != null ? (String[])(String[])((ArrayList)list).toArray(new String[0]) : new String[0];
/*    */   }
/*    */ 
/*    */   public String getId()
/*    */   {
/* 76 */     return "AdminConsoleDisplay";
/*    */   }
/*    */ 
/*    */   public String getType()
/*    */   {
/* 81 */     return "AdminConsoleDisplay";
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.runtime.AdminConsoleDisplayRegistrar
 * JD-Core Version:    0.6.0
 */
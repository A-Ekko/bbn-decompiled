/*     */ package flex.messaging.config;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class ConfigMap extends LinkedHashMap
/*     */ {
/*     */   private static final long serialVersionUID = 8913604659150919550L;
/*     */   private static final int UNEXPECTED_MULTIPLE_VALUES = 10169;
/*  51 */   private HashSet accessedKeys = new HashSet();
/*     */ 
/*     */   public ConfigMap()
/*     */   {
/*     */   }
/*     */ 
/*     */   public ConfigMap(int initialCapacity)
/*     */   {
/*  70 */     super(initialCapacity);
/*     */   }
/*     */ 
/*     */   public ConfigMap(ConfigMap m)
/*     */   {
/*  82 */     this();
/*  83 */     addProperties(m);
/*     */   }
/*     */ 
/*     */   public void addProperties(ConfigMap p)
/*     */   {
/*  94 */     Iterator it = p.entrySet().iterator();
/*  95 */     while (it.hasNext())
/*     */     {
/*  97 */       Map.Entry entry = (Map.Entry)it.next();
/*  98 */       Object key = entry.getKey();
/*  99 */       Object value = entry.getValue();
/* 100 */       if ((value instanceof ValueList))
/*     */       {
/* 102 */         addProperties(key, (ValueList)value);
/*     */       }
/*     */       else
/*     */       {
/* 106 */         addPropertyLogic(key, value);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void addProperties(Object key, ValueList values)
/*     */   {
/* 113 */     ValueList list = getValueList(key);
/* 114 */     if (list == null)
/*     */     {
/* 116 */       put(key, values.clone());
/*     */     }
/*     */     else
/*     */     {
/* 120 */       list.addAll(values);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void addPropertyLogic(Object key, Object value)
/*     */   {
/* 126 */     ValueList list = getValueList(key);
/* 127 */     if (list == null)
/*     */     {
/* 129 */       put(key, value);
/*     */     }
/*     */     else
/*     */     {
/* 133 */       list.add(value);
/*     */     }
/*     */   }
/*     */ 
/*     */   private ValueList getValueList(Object key)
/*     */   {
/* 145 */     Object old = super.get(key);
/*     */     ValueList list;
/*     */     ValueList list;
/* 146 */     if ((old instanceof ValueList))
/*     */     {
/* 148 */       list = (ValueList)old;
/*     */     }
/* 150 */     else if (old != null)
/*     */     {
/* 152 */       ValueList list = new ValueList(null);
/* 153 */       list.add(old);
/* 154 */       put(key, list);
/*     */     }
/*     */     else
/*     */     {
/* 158 */       list = null;
/*     */     }
/* 160 */     return list;
/*     */   }
/*     */ 
/*     */   public void addProperty(String name, String value)
/*     */   {
/* 172 */     addPropertyLogic(name, value);
/*     */   }
/*     */ 
/*     */   public void addProperty(String name, ConfigMap value)
/*     */   {
/* 184 */     addPropertyLogic(name, value);
/*     */   }
/*     */ 
/*     */   public Set propertyNames()
/*     */   {
/* 194 */     return keySet();
/*     */   }
/*     */ 
/*     */   public void allowProperty(String name)
/*     */   {
/* 205 */     this.accessedKeys.add(name);
/*     */   }
/*     */ 
/*     */   public Object get(Object name)
/*     */   {
/* 218 */     this.accessedKeys.add(name);
/* 219 */     return super.get(name);
/*     */   }
/*     */ 
/*     */   private Object getSinglePropertyOrFail(Object name)
/*     */   {
/* 224 */     Object result = get(name);
/* 225 */     if ((result instanceof ValueList))
/*     */     {
/* 227 */       ConfigurationException exception = new ConfigurationException();
/* 228 */       exception.setMessage(10169, new Object[] { name });
/*     */ 
/* 230 */       throw exception;
/*     */     }
/* 232 */     return result;
/*     */   }
/*     */ 
/*     */   public String getProperty(String name)
/*     */   {
/* 243 */     return getPropertyAsString(name, null);
/*     */   }
/*     */ 
/*     */   public ConfigMap getPropertyAsMap(String name, ConfigMap defaultValue)
/*     */   {
/* 255 */     Object prop = getSinglePropertyOrFail(name);
/* 256 */     if ((prop instanceof ConfigMap))
/*     */     {
/* 258 */       return (ConfigMap)prop;
/*     */     }
/* 260 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public String getPropertyAsString(String name, String defaultValue)
/*     */   {
/* 272 */     Object prop = getSinglePropertyOrFail(name);
/* 273 */     if ((prop instanceof String))
/*     */     {
/* 275 */       return (String)prop;
/*     */     }
/* 277 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public List getPropertyAsList(String name, List defaultValue)
/*     */   {
/* 291 */     Object prop = get(name);
/* 292 */     if (prop != null)
/*     */     {
/* 294 */       if ((prop instanceof List))
/*     */       {
/* 296 */         return (List)prop;
/*     */       }
/*     */ 
/* 300 */       List list = new ArrayList();
/* 301 */       list.add(prop);
/* 302 */       return list;
/*     */     }
/*     */ 
/* 305 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public boolean getPropertyAsBoolean(String name, boolean defaultValue)
/*     */   {
/* 317 */     Object prop = getSinglePropertyOrFail(name);
/* 318 */     if ((prop instanceof String))
/*     */     {
/* 320 */       return Boolean.valueOf((String)prop).booleanValue();
/*     */     }
/* 322 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public int getPropertyAsInt(String name, int defaultValue)
/*     */   {
/* 334 */     Object prop = getSinglePropertyOrFail(name);
/* 335 */     if ((prop instanceof String))
/*     */     {
/*     */       try
/*     */       {
/* 339 */         return Integer.parseInt((String)prop);
/*     */       }
/*     */       catch (NumberFormatException ex)
/*     */       {
/*     */       }
/*     */     }
/* 345 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public long getPropertyAsLong(String name, long defaultValue)
/*     */   {
/* 357 */     Object prop = getSinglePropertyOrFail(name);
/* 358 */     if ((prop instanceof String))
/*     */     {
/*     */       try
/*     */       {
/* 362 */         return Long.parseLong((String)prop);
/*     */       }
/*     */       catch (NumberFormatException ex)
/*     */       {
/*     */       }
/*     */     }
/* 368 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public List findAllUnusedProperties()
/*     */   {
/* 377 */     List result = new ArrayList();
/* 378 */     findUnusedProperties("", true, result);
/* 379 */     return result;
/*     */   }
/*     */ 
/*     */   public void findUnusedProperties(String parentPath, boolean recurse, Collection result)
/*     */   {
/* 396 */     Iterator itr = entrySet().iterator();
/* 397 */     while (itr.hasNext())
/*     */     {
/* 399 */       Map.Entry entry = (Map.Entry)itr.next();
/* 400 */       Object key = entry.getKey();
/* 401 */       String currentPath = parentPath + '/' + String.valueOf(key);
/* 402 */       if (!this.accessedKeys.contains(key))
/*     */       {
/* 404 */         result.add(currentPath);
/*     */       }
/* 406 */       else if (recurse)
/*     */       {
/* 408 */         Object value = entry.getValue();
/* 409 */         List values = (value instanceof List) ? (List)value : Collections.singletonList(value);
/*     */ 
/* 411 */         for (int i = 0; i < values.size(); i++)
/*     */         {
/* 413 */           Object child = values.get(i);
/* 414 */           if (!(child instanceof ConfigMap))
/*     */             continue;
/* 416 */           ((ConfigMap)child).findUnusedProperties(currentPath, recurse, result);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class ValueList extends ArrayList
/*     */   {
/*     */     static final long serialVersionUID = -5637755312744414675L;
/*     */ 
/*     */     private ValueList()
/*     */     {
/*     */     }
/*     */ 
/*     */     ValueList(ConfigMap.1 x0)
/*     */     {
/* 137 */       this();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.ConfigMap
 * JD-Core Version:    0.6.0
 */
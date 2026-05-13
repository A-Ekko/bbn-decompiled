/*     */ package flex.messaging.util;
/*     */ 
/*     */ import flex.messaging.MessageException;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Modifier;
/*     */ 
/*     */ public class ClassUtil
/*     */ {
/*     */   private static final int TYPE_NOT_FOUND = 10008;
/*     */   private static final int UNEXPECTED_TYPE = 10009;
/*     */   private static final int CANNOT_CREATE_TYPE = 10010;
/*     */   private static final int SECURITY_ERROR = 10011;
/*     */   private static final int UNKNOWN_ERROR = 10012;
/*     */ 
/*     */   public static Class createClass(String type)
/*     */   {
/*  47 */     return createClass(type, null);
/*     */   }
/*     */ 
/*     */   public static Class createClass(String type, ClassLoader loader) {
/*     */     MessageException ex;
/*     */     try {
/*  54 */       if (type != null) {
/*  55 */         type = type.trim();
/*     */       }
/*  57 */       if (loader == null) {
/*  58 */         return Class.forName(type);
/*     */       }
/*  60 */       return Class.forName(type, true, loader);
/*     */     }
/*     */     catch (ClassNotFoundException cnf)
/*     */     {
/*  65 */       ex = new MessageException();
/*  66 */       ex.setMessage(10008, new Object[] { type });
/*  67 */       ex.setDetails(10008, "0", new Object[] { type });
/*  68 */       ex.setCode("Server.ResourceUnavailable");
/*  69 */     }throw ex;
/*     */   }
/*     */ 
/*     */   public static Object createDefaultInstance(Class cls, Class expectedInstance) {
/*  75 */     String type = cls.getName();
/*     */     MessageException ex;
/*     */     try {
/*  79 */       Object instance = cls.newInstance();
/*     */ 
/*  81 */       if ((expectedInstance != null) && (!expectedInstance.isInstance(instance)))
/*     */       {
/*  84 */         MessageException ex = new MessageException();
/*  85 */         ex.setMessage(10009, new Object[] { instance.getClass().getName(), expectedInstance.getName() });
/*  86 */         ex.setCode("Server.ResourceUnavailable");
/*  87 */         throw ex;
/*     */       }
/*     */ 
/*  90 */       return instance;
/*     */     }
/*     */     catch (IllegalAccessException ia)
/*     */     {
/*  94 */       boolean details = false;
/*  95 */       StringBuffer message = new StringBuffer("Unable to create a new instance of type ");
/*  96 */       message.append(type);
/*     */ 
/* 101 */       if (!hasValidDefaultConstructor(cls))
/*     */       {
/* 103 */         details = true;
/*     */       }
/*     */ 
/* 107 */       MessageException ex = new MessageException();
/* 108 */       ex.setMessage(10010, new Object[] { type });
/* 109 */       if (details)
/*     */       {
/* 112 */         ex.setDetails(10010, "0");
/*     */       }
/* 114 */       ex.setCode("Server.ResourceUnavailable");
/* 115 */       throw ex;
/*     */     }
/*     */     catch (InstantiationException ine)
/*     */     {
/* 119 */       String variant = null;
/*     */ 
/* 122 */       if (cls != null)
/*     */       {
/* 125 */         if (cls.isInterface())
/*     */         {
/* 128 */           variant = "1";
/*     */         }
/* 130 */         else if (isAbstract(cls))
/*     */         {
/* 133 */           variant = "2";
/*     */         }
/* 136 */         else if (!hasValidDefaultConstructor(cls))
/*     */         {
/* 139 */           variant = "3";
/*     */         }
/*     */       }
/*     */ 
/* 143 */       MessageException ex = new MessageException();
/* 144 */       ex.setMessage(10010, new Object[] { type });
/* 145 */       if (variant != null)
/* 146 */         ex.setDetails(10010, variant);
/* 147 */       ex.setCode("Server.ResourceUnavailable");
/* 148 */       throw ex;
/*     */     }
/*     */     catch (SecurityException se)
/*     */     {
/* 152 */       MessageException ex = new MessageException();
/* 153 */       ex.setMessage(10011, new Object[] { type });
/* 154 */       ex.setCode("Server.ResourceUnavailable");
/* 155 */       ex.setRootCause(se);
/* 156 */       throw ex;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 160 */       ex = new MessageException();
/* 161 */       ex.setMessage(10012, new Object[] { type });
/* 162 */       ex.setCode("Server.ResourceUnavailable");
/* 163 */       ex.setRootCause(e);
/* 164 */     }throw ex;
/*     */   }
/*     */ 
/*     */   public static boolean isAbstract(Class cls)
/*     */   {
/* 170 */     boolean abs = false;
/*     */     try
/*     */     {
/* 174 */       if (cls != null)
/*     */       {
/* 176 */         int mod = cls.getModifiers();
/* 177 */         abs = Modifier.isAbstract(mod);
/*     */       }
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*     */     }
/*     */ 
/* 184 */     return abs;
/*     */   }
/*     */ 
/*     */   public static boolean hasValidDefaultConstructor(Class cls)
/*     */   {
/* 189 */     boolean valid = false;
/*     */     try
/*     */     {
/* 193 */       if (cls != null)
/*     */       {
/* 195 */         Constructor c = cls.getConstructor(new Class[0]);
/* 196 */         int mod = c.getModifiers();
/* 197 */         valid = Modifier.isPublic(mod);
/*     */       }
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*     */     }
/*     */ 
/* 204 */     return valid;
/*     */   }
/*     */ 
/*     */   public static String classLoaderToString(ClassLoader cl)
/*     */   {
/* 209 */     if (cl == null) {
/* 210 */       return "null";
/*     */     }
/* 212 */     if (cl == ClassLoader.getSystemClassLoader()) {
/* 213 */       return "system";
/*     */     }
/* 215 */     StringBuffer sb = new StringBuffer();
/* 216 */     sb.append("hashCode: " + System.identityHashCode(cl) + " (parent " + classLoaderToString(cl.getParent()) + ")");
/* 217 */     return sb.toString();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.util.ClassUtil
 * JD-Core Version:    0.6.0
 */
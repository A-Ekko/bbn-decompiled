/*     */ package org.apache.log4j.config;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import org.apache.log4j.Appender;
/*     */ import org.apache.log4j.Category;
/*     */ import org.apache.log4j.Level;
/*     */ import org.apache.log4j.Priority;
/*     */ 
/*     */ public class PropertyPrinter
/*     */   implements PropertyGetter.PropertyCallback
/*     */ {
/*  23 */   protected int numAppenders = 0;
/*  24 */   protected Hashtable appenderNames = new Hashtable();
/*  25 */   protected Hashtable layoutNames = new Hashtable();
/*     */   protected PrintWriter out;
/*     */   protected boolean doCapitalize;
/*     */ 
/*     */   public PropertyPrinter(PrintWriter out)
/*     */   {
/*  31 */     this(out, false);
/*     */   }
/*     */ 
/*     */   public PropertyPrinter(PrintWriter out, boolean doCapitalize)
/*     */   {
/*  36 */     this.out = out;
/*  37 */     this.doCapitalize = doCapitalize;
/*     */ 
/*  39 */     print(out);
/*  40 */     out.flush();
/*     */   }
/*     */ 
/*     */   protected String genAppName()
/*     */   {
/*  45 */     return "A" + this.numAppenders++;
/*     */   }
/*     */ 
/*     */   protected boolean isGenAppName(String name)
/*     */   {
/*  54 */     if ((name.length() < 2) || (name.charAt(0) != 'A')) return false;
/*     */ 
/*  56 */     for (int i = 0; i < name.length(); i++) {
/*  57 */       if ((name.charAt(i) < '0') || (name.charAt(i) > '9')) return false;
/*     */     }
/*  59 */     return true;
/*     */   }
/*     */ 
/*     */   public void print(PrintWriter out)
/*     */   {
/*  70 */     printOptions(out, Category.getRoot());
/*     */ 
/*  72 */     Enumeration cats = Category.getCurrentCategories();
/*  73 */     while (cats.hasMoreElements())
/*  74 */       printOptions(out, (Category)cats.nextElement());
/*     */   }
/*     */ 
/*     */   protected void printOptions(PrintWriter out, Category cat)
/*     */   {
/*  80 */     Enumeration appenders = cat.getAllAppenders();
/*  81 */     Level prio = cat.getLevel();
/*  82 */     String appenderString = prio == null ? "" : prio.toString();
/*     */ 
/*  84 */     while (appenders.hasMoreElements()) {
/*  85 */       Appender app = (Appender)appenders.nextElement();
/*     */       String name;
/*  88 */       if ((name = (String)this.appenderNames.get(app)) == null)
/*     */       {
/*  91 */         if (((name = app.getName()) == null) || (isGenAppName(name))) {
/*  92 */           name = genAppName();
/*     */         }
/*  94 */         this.appenderNames.put(app, name);
/*     */ 
/*  96 */         printOptions(out, app, "log4j.appender." + name);
/*  97 */         if (app.getLayout() != null) {
/*  98 */           printOptions(out, app.getLayout(), "log4j.appender." + name + ".layout");
/*     */         }
/*     */       }
/* 101 */       appenderString = appenderString + ", " + name;
/*     */     }
/* 103 */     String catKey = "log4j.category." + cat.getName();
/*     */ 
/* 106 */     if (appenderString != "")
/* 107 */       out.println(catKey + "=" + appenderString);
/*     */   }
/*     */ 
/*     */   protected void printOptions(PrintWriter out, Object obj, String fullname)
/*     */   {
/* 113 */     out.println(fullname + "=" + obj.getClass().getName());
/* 114 */     PropertyGetter.getProperties(obj, this, fullname + ".");
/*     */   }
/*     */ 
/*     */   public void foundProperty(Object obj, String prefix, String name, Object value)
/*     */   {
/* 119 */     if (((obj instanceof Appender)) && ("name".equals(name))) {
/* 120 */       return;
/*     */     }
/* 122 */     if (this.doCapitalize) {
/* 123 */       name = capitalize(name);
/*     */     }
/* 125 */     this.out.println(prefix + name + "=" + value.toString());
/*     */   }
/*     */ 
/*     */   public static String capitalize(String name) {
/* 129 */     if ((Character.isLowerCase(name.charAt(0))) && (
/* 130 */       (name.length() == 1) || (Character.isLowerCase(name.charAt(1))))) {
/* 131 */       StringBuffer newname = new StringBuffer(name);
/* 132 */       newname.setCharAt(0, Character.toUpperCase(name.charAt(0)));
/* 133 */       return newname.toString();
/*     */     }
/*     */ 
/* 136 */     return name;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 141 */     new PropertyPrinter(new PrintWriter(System.out));
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.config.PropertyPrinter
 * JD-Core Version:    0.6.0
 */
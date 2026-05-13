/*     */ package org.apache.log4j.config;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import org.apache.log4j.Appender;
/*     */ import org.apache.log4j.Category;
/*     */ import org.apache.log4j.Level;
/*     */ import org.apache.log4j.LogManager;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.apache.log4j.Priority;
/*     */ 
/*     */ public class PropertyPrinter
/*     */   implements PropertyGetter.PropertyCallback
/*     */ {
/*  38 */   protected int numAppenders = 0;
/*  39 */   protected Hashtable appenderNames = new Hashtable();
/*  40 */   protected Hashtable layoutNames = new Hashtable();
/*     */   protected PrintWriter out;
/*     */   protected boolean doCapitalize;
/*     */ 
/*     */   public PropertyPrinter(PrintWriter out)
/*     */   {
/*  46 */     this(out, false);
/*     */   }
/*     */ 
/*     */   public PropertyPrinter(PrintWriter out, boolean doCapitalize)
/*     */   {
/*  51 */     this.out = out;
/*  52 */     this.doCapitalize = doCapitalize;
/*     */ 
/*  54 */     print(out);
/*  55 */     out.flush();
/*     */   }
/*     */ 
/*     */   protected String genAppName()
/*     */   {
/*  60 */     return "A" + this.numAppenders++;
/*     */   }
/*     */ 
/*     */   protected boolean isGenAppName(String name)
/*     */   {
/*  69 */     if ((name.length() < 2) || (name.charAt(0) != 'A')) return false;
/*     */ 
/*  71 */     for (int i = 0; i < name.length(); i++) {
/*  72 */       if ((name.charAt(i) < '0') || (name.charAt(i) > '9')) return false;
/*     */     }
/*  74 */     return true;
/*     */   }
/*     */ 
/*     */   public void print(PrintWriter out)
/*     */   {
/*  85 */     printOptions(out, Logger.getRootLogger());
/*     */ 
/*  87 */     Enumeration cats = LogManager.getCurrentLoggers();
/*  88 */     while (cats.hasMoreElements())
/*  89 */       printOptions(out, (Logger)cats.nextElement());
/*     */   }
/*     */ 
/*     */   protected void printOptions(PrintWriter out, Category cat)
/*     */   {
/*  95 */     Enumeration appenders = cat.getAllAppenders();
/*  96 */     Level prio = cat.getLevel();
/*  97 */     String appenderString = prio == null ? "" : prio.toString();
/*     */ 
/*  99 */     while (appenders.hasMoreElements()) {
/* 100 */       Appender app = (Appender)appenders.nextElement();
/*     */       String name;
/* 103 */       if ((name = (String)this.appenderNames.get(app)) == null)
/*     */       {
/* 106 */         if (((name = app.getName()) == null) || (isGenAppName(name))) {
/* 107 */           name = genAppName();
/*     */         }
/* 109 */         this.appenderNames.put(app, name);
/*     */ 
/* 111 */         printOptions(out, app, "log4j.appender." + name);
/* 112 */         if (app.getLayout() != null) {
/* 113 */           printOptions(out, app.getLayout(), "log4j.appender." + name + ".layout");
/*     */         }
/*     */       }
/* 116 */       appenderString = appenderString + ", " + name;
/*     */     }
/* 118 */     String catKey = "log4j.logger." + cat.getName();
/*     */ 
/* 121 */     if (appenderString != "") {
/* 122 */       out.println(catKey + "=" + appenderString);
/*     */     }
/* 124 */     if ((!cat.getAdditivity()) && (cat != Logger.getRootLogger()))
/* 125 */       out.println("log4j.additivity." + cat.getName() + "=false");
/*     */   }
/*     */ 
/*     */   protected void printOptions(PrintWriter out, Logger cat)
/*     */   {
/* 130 */     printOptions(out, cat);
/*     */   }
/*     */ 
/*     */   protected void printOptions(PrintWriter out, Object obj, String fullname)
/*     */   {
/* 135 */     out.println(fullname + "=" + obj.getClass().getName());
/* 136 */     PropertyGetter.getProperties(obj, this, fullname + ".");
/*     */   }
/*     */ 
/*     */   public void foundProperty(Object obj, String prefix, String name, Object value)
/*     */   {
/* 141 */     if (((obj instanceof Appender)) && ("name".equals(name))) {
/* 142 */       return;
/*     */     }
/* 144 */     if (this.doCapitalize) {
/* 145 */       name = capitalize(name);
/*     */     }
/* 147 */     this.out.println(prefix + name + "=" + value.toString());
/*     */   }
/*     */ 
/*     */   public static String capitalize(String name) {
/* 151 */     if ((Character.isLowerCase(name.charAt(0))) && (
/* 152 */       (name.length() == 1) || (Character.isLowerCase(name.charAt(1))))) {
/* 153 */       StringBuffer newname = new StringBuffer(name);
/* 154 */       newname.setCharAt(0, Character.toUpperCase(name.charAt(0)));
/* 155 */       return newname.toString();
/*     */     }
/*     */ 
/* 158 */     return name;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 163 */     new PropertyPrinter(new PrintWriter(System.out));
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.config.PropertyPrinter
 * JD-Core Version:    0.6.0
 */
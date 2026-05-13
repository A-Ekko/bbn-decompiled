/*    */ package org.apache.log4j.spi;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.LineNumberReader;
/*    */ import java.io.PrintWriter;
/*    */ import java.io.Serializable;
/*    */ import java.io.StringReader;
/*    */ import java.io.StringWriter;
/*    */ import java.util.ArrayList;
/*    */ 
/*    */ public class ThrowableInformation
/*    */   implements Serializable
/*    */ {
/*    */   static final long serialVersionUID = -4748765566864322735L;
/*    */   private transient Throwable throwable;
/*    */   private String[] rep;
/*    */ 
/*    */   public ThrowableInformation(Throwable throwable)
/*    */   {
/* 49 */     this.throwable = throwable;
/*    */   }
/*    */ 
/*    */   public ThrowableInformation(String[] r)
/*    */   {
/* 58 */     if (r != null)
/* 59 */       this.rep = ((String[])(String[])r.clone());
/*    */   }
/*    */ 
/*    */   public Throwable getThrowable()
/*    */   {
/* 66 */     return this.throwable;
/*    */   }
/*    */ 
/*    */   public String[] getThrowableStrRep()
/*    */   {
/* 71 */     if (this.rep != null) {
/* 72 */       return (String[])(String[])this.rep.clone();
/*    */     }
/* 74 */     StringWriter sw = new StringWriter();
/* 75 */     PrintWriter pw = new PrintWriter(sw);
/* 76 */     this.throwable.printStackTrace(pw);
/* 77 */     pw.flush();
/* 78 */     LineNumberReader reader = new LineNumberReader(new StringReader(sw.toString()));
/*    */ 
/* 80 */     ArrayList lines = new ArrayList();
/*    */     try {
/* 82 */       String line = reader.readLine();
/* 83 */       while (line != null) {
/* 84 */         lines.add(line);
/* 85 */         line = reader.readLine();
/*    */       }
/*    */     } catch (IOException ex) {
/* 88 */       lines.add(ex.toString());
/*    */     }
/* 90 */     this.rep = new String[lines.size()];
/* 91 */     lines.toArray(this.rep);
/*    */ 
/* 93 */     return this.rep;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.spi.ThrowableInformation
 * JD-Core Version:    0.6.0
 */
/*    */ package org.apache.log4j.or;
/*    */ 
/*    */ import org.apache.log4j.Layout;
/*    */ 
/*    */ public class ThreadGroupRenderer
/*    */   implements ObjectRenderer
/*    */ {
/*    */   public String doRender(Object o)
/*    */   {
/* 41 */     if ((o instanceof ThreadGroup)) {
/* 42 */       StringBuffer sbuf = new StringBuffer();
/* 43 */       ThreadGroup tg = (ThreadGroup)o;
/* 44 */       sbuf.append("java.lang.ThreadGroup[name=");
/* 45 */       sbuf.append(tg.getName());
/* 46 */       sbuf.append(", maxpri=");
/* 47 */       sbuf.append(tg.getMaxPriority());
/* 48 */       sbuf.append("]");
/* 49 */       Thread[] t = new Thread[tg.activeCount()];
/* 50 */       tg.enumerate(t);
/* 51 */       for (int i = 0; i < t.length; i++) {
/* 52 */         sbuf.append(Layout.LINE_SEP);
/* 53 */         sbuf.append("   Thread=[");
/* 54 */         sbuf.append(t[i].getName());
/* 55 */         sbuf.append(",");
/* 56 */         sbuf.append(t[i].getPriority());
/* 57 */         sbuf.append(",");
/* 58 */         sbuf.append(t[i].isDaemon());
/* 59 */         sbuf.append("]");
/*    */       }
/* 61 */       return sbuf.toString();
/*    */     }
/*    */ 
/* 64 */     return o.toString();
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.or.ThreadGroupRenderer
 * JD-Core Version:    0.6.0
 */
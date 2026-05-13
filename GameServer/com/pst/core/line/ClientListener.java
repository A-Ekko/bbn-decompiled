/*    */ package com.pst.core.line;
/*    */ 
/*    */ import com.pst.core.line.command.Command;
/*    */ import com.pst.core.line.entity.Player;
/*    */ import com.pst.core.line.store.SystemStore;
/*    */ import com.pst.core.protocol.ProtocolF002;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import org.apache.mina.core.future.WriteFuture;
/*    */ import org.apache.mina.core.session.IoSession;
/*    */ 
/*    */ public class ClientListener
/*    */   implements Runnable
/*    */ {
/* 16 */   private long timeout = 60000L;
/*    */ 
/* 18 */   private ProtocolF002 protocol = new ProtocolF002();
/*    */   private byte[] heartBeat;
/* 20 */   private boolean flag = false;
/*    */ 
/*    */   public ClientListener() {
/* 23 */     this.heartBeat = this.protocol.packProtocolBytes(new byte[0], Command.heartBeat);
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/*    */     while (true)
/*    */       try {
/* 30 */         Thread.sleep(this.timeout);
/* 31 */         if (this.flag)
/* 32 */           checkSession();
/*    */         else {
/* 34 */           checkPlayer();
/*    */         }
/* 36 */         this.flag = (!this.flag); continue;
/*    */       } catch (Exception e) {
/* 38 */         e.printStackTrace();
/*    */       }
/*    */   }
/*    */ 
/*    */   private void checkPlayer()
/*    */   {
/* 44 */     Iterator iterator = SystemStore.players.keySet().iterator();
/* 45 */     List list = new ArrayList(SystemStore.players.size());
/* 46 */     while (iterator.hasNext()) {
/* 47 */       list.add((Integer)iterator.next());
/*    */     }
/*    */ 
/* 52 */     for (int i = 0; i < list.size(); i++) {
/* 53 */       Player player = (Player)SystemStore.players.get(list.get(i));
/* 54 */       if (player != null) {
/* 55 */         IoSession session = player.getClientSession();
/* 56 */         if (session != null) {
/* 57 */           print(session);
/*    */         }
/*    */       }
/*    */     }
/*    */ 
/* 62 */     list = null;
/*    */   }
/*    */ 
/*    */   private void checkSession() {
/* 66 */     Iterator iterator = SystemStore.sessionPlayers.keySet().iterator();
/*    */ 
/* 69 */     List list = new ArrayList(SystemStore.sessionPlayers.size());
/*    */ 
/* 71 */     while (iterator.hasNext()) {
/* 72 */       list.add((IoSession)iterator.next());
/*    */     }
/* 74 */     for (int i = 0; i < list.size(); i++) {
/* 75 */       IoSession session = (IoSession)list.get(i);
/* 76 */       if (session != null) {
/* 77 */         print(session);
/*    */       }
/*    */     }
/* 80 */     list = null;
/*    */   }
/*    */ 
/*    */   public boolean print(IoSession session) {
/* 84 */     WriteFuture writeFuture = null;
/*    */     try {
/* 86 */       for (int i = 0; i < 3; i++) {
/* 87 */         writeFuture = session.write(this.heartBeat);
/* 88 */         writeFuture.awaitUninterruptibly();
/* 89 */         boolean flag = writeFuture.isWritten();
/* 90 */         if (flag) {
/* 91 */           return true;
/*    */         }
/*    */       }
/* 94 */       return false;
/*    */     } catch (Exception e) {
/* 96 */       e.printStackTrace();
/*    */     }
/* 98 */     return true;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.core.line.ClientListener
 * JD-Core Version:    0.6.0
 */
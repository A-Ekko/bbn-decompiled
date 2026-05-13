/*    */ package com.pst.core.line.store;
/*    */ 
/*    */ import com.pst.core.line.entity.Line;
/*    */ import com.pst.core.line.entity.Player;
/*    */ import com.pst.core.line.entity.Team;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import org.apache.mina.core.session.IoSession;
/*    */ 
/*    */ public class SystemStore
/*    */ {
/* 14 */   public static final Map<IoSession, Player> sessionPlayers = new HashMap();
/*    */   public static String server;
/* 20 */   public static final Map<Integer, Player> players = new HashMap();
/*    */ 
/* 22 */   public static final Map<String, Player> nplayers = new HashMap();
/*    */   public static List<Line> lines;
/* 32 */   public static final Map<String, List<Integer>> linemaps = new HashMap();
/*    */ 
/* 38 */   public static final Map<String, Team> teams = new HashMap();
/*    */ 
/* 43 */   public static final Map<Integer, Team> pteams = new HashMap();
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.core.line.store.SystemStore
 * JD-Core Version:    0.6.0
 */
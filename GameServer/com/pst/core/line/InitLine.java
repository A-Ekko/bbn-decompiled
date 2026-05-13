/*    */ package com.pst.core.line;
/*    */ 
/*    */ import com.pst.core.config.SystemConfig;
/*    */ import com.pst.core.line.entity.Line;
/*    */ import com.pst.core.line.store.SystemStore;
/*    */ import com.pst.core.util.VirtualMachine;
/*    */ import com.pst.db.ConnectionPoolManager;
/*    */ import com.pst.db.dao.LineDao;
/*    */ import com.pst.db.dao.MapDao;
/*    */ import com.pst.db.dao.ServerDao;
/*    */ import com.pst.task.TaskAction;
/*    */ import java.io.File;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.Properties;
/*    */ import org.dom4j.Document;
/*    */ import org.dom4j.DocumentException;
/*    */ import org.dom4j.Element;
/*    */ import org.dom4j.io.SAXReader;
/*    */ 
/*    */ public class InitLine
/*    */ {
/*    */   public boolean init()
/*    */   {
/* 25 */     SystemConfig.writePId(String.valueOf(VirtualMachine.getPid()));
/*    */ 
/* 27 */     SAXReader reader = new SAXReader();
/* 28 */     Document document = null;
/*    */     try {
/* 30 */       document = reader.read(new File(System.getProperties().getProperty("user.dir") + "/resource/line-config.xml"));
/*    */     } catch (DocumentException e) {
/* 32 */       e.printStackTrace();
/*    */     }
/* 34 */     Element element = document.getRootElement().element("port");
/*    */ 
/* 36 */     SystemConfig.port = Integer.parseInt(element.getText());
/*    */ 
/* 38 */     SystemConfig.good = Integer.valueOf(document.getRootElement().element("onlineStatusGood").getText().trim()).intValue();
/* 39 */     SystemConfig.bad = Integer.valueOf(document.getRootElement().element("onlineStatusBad").getText().trim()).intValue();
/*    */ 
/* 42 */     ConnectionPoolManager.initConnectionPool();
/* 43 */     SystemStore.server = new ServerDao().getServerName();
/*    */ 
/* 45 */     SystemStore.lines = new LineDao().getLines();
/*    */ 
/* 47 */     TaskAction taskAction = new TaskAction();
/*    */ 
/* 49 */     taskAction.playerOnlineByMap();
/*    */ 
/* 51 */     taskAction.taskClearGiftAction();
/*    */ 
/* 53 */     taskAction.taskAction();
/*    */ 
/* 55 */     taskAction.wealthAction();
/*    */ 
/* 57 */     new Thread(new ClientListener()).start();
/*    */ 
/* 59 */     List list = new MapDao().getMaps();
/*    */ 
/* 61 */     for (int i = 0; i < SystemStore.lines.size(); i++) {
/* 62 */       for (int j = 0; j < list.size(); j++) {
/* 63 */         SystemStore.linemaps.put(((Line)SystemStore.lines.get(i)).getId() + "-" + list.get(j), new ArrayList());
/*    */       }
/*    */     }
/* 66 */     return true;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.core.line.InitLine
 * JD-Core Version:    0.6.0
 */
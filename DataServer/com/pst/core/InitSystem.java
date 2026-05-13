/*    */ package com.pst.core;
/*    */ 
/*    */ import com.pst.config.SystemConfig;
/*    */ import com.pst.core.comm.DataLitener;
/*    */ import com.pst.core.util.VirtualMachine;
/*    */ import com.pst.db.ConnectionPoolManager;
/*    */ import java.io.File;
/*    */ import java.io.PrintStream;
/*    */ import java.util.List;
/*    */ import java.util.Properties;
/*    */ import org.dom4j.Document;
/*    */ import org.dom4j.DocumentException;
/*    */ import org.dom4j.Element;
/*    */ import org.dom4j.io.SAXReader;
/*    */ 
/*    */ public class InitSystem
/*    */ {
/*    */   public boolean action()
/*    */   {
/* 18 */     SystemConfig.writePId(String.valueOf(VirtualMachine.getPid()));
/* 19 */     ConnectionPoolManager.initConnectionPool(System.getProperties().getProperty("user.dir") + "/resource/proxool.properties", "proxool.mysql");
/* 20 */     readXML();
/* 21 */     new DataLitener().litener();
/* 22 */     return true;
/*    */   }
/*    */ 
/*    */   private void readXML() {
/* 26 */     SAXReader reader = new SAXReader();
/* 27 */     Document document = null;
/*    */     try {
/* 29 */       document = reader.read(new File(System.getProperties().getProperty("user.dir") + "/resource/data-config.xml"));
/*    */     } catch (DocumentException e) {
/* 31 */       e.printStackTrace();
/*    */     }
/*    */ 
/* 34 */     SystemConfig.port = Integer.parseInt(document.getRootElement().element("port").getText().trim());
/* 35 */     SystemConfig.account = Integer.parseInt(document.getRootElement().element("account").getText().trim());
/* 36 */     SystemConfig.powertoken = Integer.parseInt(document.getRootElement().element("powertoken").getText().trim());
/* 37 */     SystemConfig.batchMax = Integer.parseInt(document.getRootElement().element("batchMax").getText().trim());
/* 38 */     SystemConfig.runRate = Long.parseLong(document.getRootElement().element("runRate").getText().trim());
/*    */ 
/* 40 */     Element element = document.getRootElement().element("concent");
/* 41 */     List list = element.elements("ip");
/* 42 */     for (int i = 0; i < list.size(); i++) {
/* 43 */       SystemConfig.concentipList.add(((Element)list.get(i)).getText().trim());
/*    */     }
/* 45 */     for (int i = 0; i < SystemConfig.concentipList.size(); i++)
/* 46 */       System.out.println((String)SystemConfig.concentipList.get(i));
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.pst.core.InitSystem
 * JD-Core Version:    0.6.0
 */
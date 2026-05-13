/*    */ package org.apache.log4j.varia;
/*    */ 
/*    */ import java.net.URL;
/*    */ import org.apache.log4j.PropertyConfigurator;
/*    */ import org.apache.log4j.spi.Configurator;
/*    */ import org.apache.log4j.spi.LoggerRepository;
/*    */ 
/*    */ public class ReloadingPropertyConfigurator
/*    */   implements Configurator
/*    */ {
/* 19 */   PropertyConfigurator delegate = new PropertyConfigurator();
/*    */ 
/*    */   public void doConfigure(URL url, LoggerRepository repository)
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.varia.ReloadingPropertyConfigurator
 * JD-Core Version:    0.6.0
 */
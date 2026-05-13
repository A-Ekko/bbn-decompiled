/*    */ package com.pst.core.protocol;
/*    */ 
/*    */ import org.apache.mina.core.session.IoSession;
/*    */ import org.apache.mina.filter.codec.ProtocolCodecFactory;
/*    */ import org.apache.mina.filter.codec.ProtocolDecoder;
/*    */ import org.apache.mina.filter.codec.ProtocolEncoder;
/*    */ 
/*    */ public class F002ProtocolCodecFactory
/*    */   implements ProtocolCodecFactory
/*    */ {
/* 10 */   private ProtocolEncoder PEncoder = null;
/* 11 */   private ProtocolDecoder PDecoder = null;
/*    */ 
/*    */   public F002ProtocolCodecFactory() {
/* 14 */     this.PEncoder = new F002ProtocolEncoder();
/* 15 */     this.PDecoder = new F002ProtocolDecoder();
/*    */   }
/*    */ 
/*    */   public ProtocolEncoder getEncoder(IoSession _session) {
/* 19 */     return this.PEncoder;
/*    */   }
/*    */ 
/*    */   public ProtocolDecoder getDecoder(IoSession _session) {
/* 23 */     return this.PDecoder;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.core.protocol.F002ProtocolCodecFactory
 * JD-Core Version:    0.6.0
 */
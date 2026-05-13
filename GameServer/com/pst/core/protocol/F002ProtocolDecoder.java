/*    */ package com.pst.core.protocol;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ import java.util.HashMap;
/*    */ import java.util.Vector;
/*    */ import org.apache.log4j.Logger;
/*    */ import org.apache.mina.core.buffer.IoBuffer;
/*    */ import org.apache.mina.core.session.IoSession;
/*    */ import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
/*    */ import org.apache.mina.filter.codec.ProtocolDecoderOutput;
/*    */ 
/*    */ public class F002ProtocolDecoder extends CumulativeProtocolDecoder
/*    */ {
/* 15 */   private Logger Loger = Logger.getLogger("F002ProtocolDecoder");
/* 16 */   private ProtocolF002 Protocol = new ProtocolF002();
/*    */ 
/* 19 */   private String flashSecurityString = "";
/*    */ 
/* 22 */   private byte[] flashSecurityHeader = { 60, 112, 111, 108, 105, 99, 121, 45, 102, 105, 108, 101, 45, 114, 101, 113, 117, 101, 115, 116, 47, 62 };
/*    */ 
/*    */   public boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput pdout)
/*    */   {
/* 34 */     int in_limit = in.limit();
/* 35 */     in.position(0);
/*    */ 
/* 37 */     byte[] prebytes = (byte[])session.getAttribute("LastBuffer");
/* 38 */     session.removeAttribute("LastBuffer");
/*    */ 
/* 40 */     byte[] readbyte = (byte[])null;
/*    */ 
/* 43 */     int writepos = 0;
/* 44 */     if (prebytes != null)
/*    */     {
/* 47 */       readbyte = new byte[in_limit + prebytes.length];
/* 48 */       System.arraycopy(prebytes, 0, readbyte, 0, prebytes.length);
/* 49 */       writepos += prebytes.length;
/*    */     }
/*    */     else {
/* 52 */       readbyte = new byte[in_limit];
/*    */     }
/*    */ 
/* 55 */     int i = 0;
/* 56 */     Vector parseCmds = null;
/* 57 */     HashMap singleCmd = null;
/*    */ 
/* 59 */     while (in.hasRemaining()) {
/* 60 */       byte[] inbytes = in.array();
/* 61 */       System.arraycopy(inbytes, 0, readbyte, writepos, in_limit);
/* 62 */       writepos += in_limit;
/*    */ 
/* 64 */       parseCmds = this.Protocol._parseBytes(readbyte);
/*    */ 
/* 67 */       for (i = 0; i < parseCmds.size(); i++) {
/* 68 */         singleCmd = (HashMap)parseCmds.get(i);
/* 69 */         if (singleCmd.get("DataComplete").toString().equals("1")) {
/* 70 */           pdout.write((byte[])singleCmd.get("CmdData"));
/*    */         } else {
/* 72 */           session.removeAttribute("LastBuffer");
/* 73 */           session.setAttribute("LastBuffer", (byte[])singleCmd.get("CmdData"));
/*    */         }
/*    */       }
/*    */ 
/* 77 */       if (parseCmds.size() == 0)
/*    */       {
/* 79 */         byte[] flashbyte = new byte[this.flashSecurityHeader.length];
/* 80 */         System.arraycopy(inbytes, 0, flashbyte, 0, flashbyte.length);
/* 81 */         if (Arrays.equals(flashbyte, this.flashSecurityHeader)) {
/* 82 */           session.write(this.flashSecurityString.getBytes());
/* 83 */           session.close(true);
/*    */         }
/*    */       }
/*    */ 
/* 87 */       parseCmds.clear();
/* 88 */       in.position(in_limit);
/*    */     }
/*    */ 
/* 91 */     in.position(in.position());
/* 92 */     in.limit(in.limit());
/* 93 */     return true;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.core.protocol.F002ProtocolDecoder
 * JD-Core Version:    0.6.0
 */
/*     */ package com.pst.core.line.command;
/*     */ 
/*     */ import com.pst.core.amf.AmfData;
/*     */ import com.pst.core.line.command.info.ServerInfo;
/*     */ import com.pst.core.protocol.Protocol;
/*     */ import com.pst.core.protocol.ProtocolF002;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ 
/*     */ public class CommandWrapper
/*     */ {
/*  15 */   private ProtocolF002 protocol = new ProtocolF002();
/*  16 */   private AmfData amfData = new AmfData();
/*     */ 
/*     */   public byte[] decreaseBugle(int playerId, int bugleArticleId) {
/*  19 */     byte[] bugleBytes = this.protocol.FillBytes(this.protocol.ToUnsignBLenByte(bugleArticleId), 4);
/*  20 */     return this.protocol.packToServerProtocolBytes(bugleBytes, Command.decreaseBugle, 1, playerId);
/*     */   }
/*     */ 
/*     */   public byte[] chat(int roleId, String name, int type, byte[] contentBytes, String dataStr)
/*     */   {
/*  33 */     byte[] roleBytes = this.protocol.FillBytes(this.protocol.ToUnsignBLenByte(roleId), 4);
/*  34 */     byte[] nameBytes = (byte[])null;
/*     */     try { nameBytes = name.getBytes(Protocol.PROTOCAL_ENCODEDEFAULT); } catch (UnsupportedEncodingException e) { e.printStackTrace(); }
/*  36 */     byte[] nameLenBytes = { (byte)nameBytes.length };
/*     */ 
/*  38 */     byte[] typeBytes = { (byte)type };
/*     */ 
/*  40 */     byte[] dataByte = (byte[])null;
/*     */     try { dataByte = dataStr.getBytes(Protocol.PROTOCAL_ENCODEDEFAULT); } catch (UnsupportedEncodingException e) { e.printStackTrace(); }
/*  42 */     byte[] dataLenByte = { (byte)dataByte.length };
/*     */ 
/*  44 */     byte[] cmdBytes = new byte[roleBytes.length + typeBytes.length + nameLenBytes.length + nameBytes.length + dataLenByte.length + dataByte.length + contentBytes.length];
/*  45 */     int destPos = 0;
/*     */ 
/*  47 */     System.arraycopy(roleBytes, 0, cmdBytes, destPos, roleBytes.length);
/*  48 */     destPos += roleBytes.length;
/*     */ 
/*  50 */     System.arraycopy(typeBytes, 0, cmdBytes, destPos, typeBytes.length);
/*  51 */     destPos += typeBytes.length;
/*     */ 
/*  53 */     System.arraycopy(nameLenBytes, 0, cmdBytes, destPos, nameLenBytes.length);
/*  54 */     destPos += nameLenBytes.length;
/*     */ 
/*  56 */     System.arraycopy(nameBytes, 0, cmdBytes, destPos, nameBytes.length);
/*  57 */     destPos += nameBytes.length;
/*     */ 
/*  59 */     System.arraycopy(dataLenByte, 0, cmdBytes, destPos, dataLenByte.length);
/*  60 */     destPos += dataLenByte.length;
/*     */ 
/*  62 */     System.arraycopy(dataByte, 0, cmdBytes, destPos, dataByte.length);
/*  63 */     destPos += dataByte.length;
/*     */ 
/*  65 */     System.arraycopy(contentBytes, 0, cmdBytes, destPos, contentBytes.length);
/*     */ 
/*  67 */     return this.protocol.packProtocolBytes(cmdBytes, Command.chat);
/*     */   }
/*     */ 
/*     */   public byte[] charError(String errorContent)
/*     */   {
/*     */     try
/*     */     {
/*  79 */       byte[] errorContentBytes = errorContent.getBytes("UTF-8");
/*  80 */       return this.protocol.packProtocolBytes(errorContentBytes, Command.chatError);
/*     */     } catch (UnsupportedEncodingException e) {
/*  82 */       e.printStackTrace();
/*     */     }
/*  84 */     return new byte[0];
/*     */   }
/*     */ 
/*     */   public byte[] informRole(int playerId, int type)
/*     */   {
/*  89 */     byte[] playerBytes = this.protocol.FillBytes(this.protocol.ToUnsignBLenByte(playerId), 4);
/*  90 */     byte[] typeBytes = { (byte)type };
/*     */ 
/*  92 */     byte[] cmdBytes = new byte[playerBytes.length + typeBytes.length];
/*  93 */     int destPos = 0;
/*     */ 
/*  95 */     System.arraycopy(playerBytes, 0, cmdBytes, destPos, playerBytes.length);
/*  96 */     destPos += playerBytes.length;
/*     */ 
/*  98 */     System.arraycopy(typeBytes, 0, cmdBytes, destPos, typeBytes.length);
/*     */ 
/* 100 */     return this.protocol.packProtocolBytes(cmdBytes, Command.informRole);
/*     */   }
/*     */ 
/*     */   public byte[] gameLineInfo(ServerInfo serverInfo)
/*     */   {
/* 109 */     return this.protocol.packProtocolBytes(this.amfData.getData(serverInfo), Command.gameLineInfo);
/*     */   }
/*     */ 
/*     */   public byte[] roleLineConnet(int roleId)
/*     */   {
/* 118 */     byte[] roleByte = this.protocol.FillBytes(this.protocol.ToUnsignBLenByte(roleId), 4);
/* 119 */     return this.protocol.packToServerProtocolBytes(roleByte, Command.roleLineConnet, 1, 1);
/*     */   }
/*     */ 
/*     */   public byte[] otherLineAccountConnect(int account)
/*     */   {
/* 128 */     byte[] roleByte = this.protocol.FillBytes(this.protocol.ToUnsignBLenByte(account), 4);
/* 129 */     return this.protocol.packToServerProtocolBytes(roleByte, Command.otherLineAccountConnect, 1, 1);
/*     */   }
/*     */ 
/*     */   public byte[] createGuild(byte[] data) {
/* 133 */     return this.protocol.packToServerProtocolBytes(data, Command.toGserver_createGuild, 1, 1);
/*     */   }
/*     */ 
/*     */   public byte[] addApplyJionGuild(byte[] data) {
/* 137 */     return this.protocol.packToServerProtocolBytes(data, Command.toGserver_addApplyJionGuild, 1, 1);
/*     */   }
/*     */ 
/*     */   public byte[] addGuildRole(byte[] data) {
/* 141 */     return this.protocol.packToServerProtocolBytes(data, Command.toGserver_addGuildRole, 1, 1);
/*     */   }
/*     */ 
/*     */   public byte[] addRoleGuild(byte[] data) {
/* 145 */     return this.protocol.packToServerProtocolBytes(data, Command.toGserver_addRoleGuild, 1, 1);
/*     */   }
/*     */ 
/*     */   public byte[] rejectApplyRole(byte[] data) {
/* 149 */     return this.protocol.packToServerProtocolBytes(data, Command.toGserver_rejectApplyRole, 1, 1);
/*     */   }
/*     */ 
/*     */   public byte[] rejectRole(byte[] data) {
/* 153 */     return this.protocol.packToServerProtocolBytes(data, Command.toGserver_rejectRole, 1, 1);
/*     */   }
/*     */ 
/*     */   public byte[] checkRoleInfo(byte[] data) {
/* 157 */     return this.protocol.packToServerProtocolBytes(data, Command.toGserver_checkRoleInfo, 1, 1);
/*     */   }
/*     */ 
/*     */   public byte[] radioMonster(byte[] data) {
/* 161 */     return this.protocol.packToServerProtocolBytes(data, Command.toGserver_radioKillMonster, 1, 1);
/*     */   }
/*     */ 
/*     */   public byte[] findRoleInfo(byte[] data)
/*     */   {
/* 170 */     return this.protocol.packToServerProtocolBytes(data, Command.toGserver_findRoleInfo, 1, 1);
/*     */   }
/*     */ 
/*     */   public byte[] dismissGuild(byte[] data)
/*     */   {
/* 179 */     return this.protocol.packToServerProtocolBytes(data, Command.toGserver_dismissGuild, 1, 1);
/*     */   }
/*     */ 
/*     */   public byte[] changeGuildInfo(byte[] data)
/*     */   {
/* 188 */     return this.protocol.packToServerProtocolBytes(data, Command.toGserver_changeGuildInfo, 1, 1);
/*     */   }
/*     */ 
/*     */   public byte[] careteTitle(byte[] data)
/*     */   {
/* 201 */     return this.protocol.packToServerProtocolBytes(data, Command.toGserver_careteTitle, 1, 1);
/*     */   }
/*     */ 
/*     */   public byte[] alertTitle(byte[] data)
/*     */   {
/* 210 */     return this.protocol.packToServerProtocolBytes(data, Command.toGserver_alertTitle, 1, 1);
/*     */   }
/*     */ 
/*     */   public byte[] delTitle(byte[] data)
/*     */   {
/* 219 */     return this.protocol.packToServerProtocolBytes(data, Command.toGserver_delTitle, 1, 1);
/*     */   }
/*     */ 
/*     */   public byte[] systemResult(String result)
/*     */   {
/* 228 */     byte[] cmdBytes = (byte[])null;
/*     */     try {
/* 230 */       cmdBytes = result.getBytes(Protocol.PROTOCAL_ENCODEDEFAULT);
/*     */     } catch (UnsupportedEncodingException e) {
/* 232 */       e.printStackTrace();
/*     */     }
/* 234 */     return this.protocol.packProtocolBytes(cmdBytes, Command.systemResult);
/*     */   }
/*     */ 
/*     */   public byte[] sysMessage(byte[] message)
/*     */   {
/* 243 */     byte[] totalByte = new byte[message.length];
/*     */ 
/* 245 */     int poset = 0;
/* 246 */     System.arraycopy(message, 0, totalByte, poset, message.length);
/*     */ 
/* 248 */     return this.protocol.packProtocolBytes(totalByte, Command.sysMessage);
/*     */   }
/*     */ 
/*     */   public byte[] warInfo(byte[] data)
/*     */   {
/* 257 */     return this.protocol.packToServerProtocolBytes(data, Command.toGserver_warInfo, 1, 1);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.core.line.command.CommandWrapper
 * JD-Core Version:    0.6.0
 */
/*    */ package com.pst.core.protocol;
/*    */ 
/*    */ public class Information
/*    */ {
/*    */   private int pt;
/*    */   private int roleId;
/*    */   private String type;
/*    */   private byte[] data;
/*    */ 
/*    */   public Information(int pt, int roleId, String type, byte[] data)
/*    */   {
/* 17 */     this.pt = pt;
/* 18 */     this.roleId = roleId;
/* 19 */     this.type = type;
/* 20 */     this.data = data;
/*    */   }
/*    */ 
/*    */   public int getRoleId()
/*    */   {
/* 28 */     return this.roleId;
/*    */   }
/*    */ 
/*    */   public void setRoleId(int roleId)
/*    */   {
/* 36 */     this.roleId = roleId;
/*    */   }
/*    */ 
/*    */   public int getPt()
/*    */   {
/* 44 */     return this.pt;
/*    */   }
/*    */ 
/*    */   public void setPt(int pt)
/*    */   {
/* 52 */     this.pt = pt;
/*    */   }
/*    */ 
/*    */   public String getType()
/*    */   {
/* 60 */     return this.type;
/*    */   }
/*    */ 
/*    */   public void setType(String type)
/*    */   {
/* 68 */     this.type = type;
/*    */   }
/*    */ 
/*    */   public byte[] getData()
/*    */   {
/* 76 */     return this.data;
/*    */   }
/*    */ 
/*    */   public void setData(byte[] data)
/*    */   {
/* 84 */     this.data = data;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.pst.core.protocol.Information
 * JD-Core Version:    0.6.0
 */
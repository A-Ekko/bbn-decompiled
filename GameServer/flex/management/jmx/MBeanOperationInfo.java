/*     */ package flex.management.jmx;
/*     */ 
/*     */ public class MBeanOperationInfo
/*     */ {
/*     */   public String name;
/*     */   public String description;
/*     */   public MBeanParameterInfo[] signature;
/*     */   public String returnType;
/*     */   public int impact;
/*     */ 
/*     */   public MBeanOperationInfo()
/*     */   {
/*     */   }
/*     */ 
/*     */   public MBeanOperationInfo(javax.management.MBeanOperationInfo mbeanOperationInfo)
/*     */   {
/*  67 */     this.name = mbeanOperationInfo.getName();
/*  68 */     this.description = mbeanOperationInfo.getDescription();
/*  69 */     this.signature = convertSignature(mbeanOperationInfo.getSignature());
/*  70 */     this.returnType = mbeanOperationInfo.getReturnType();
/*  71 */     this.impact = mbeanOperationInfo.getImpact();
/*     */   }
/*     */ 
/*     */   public javax.management.MBeanOperationInfo toMBeanOperationInfo()
/*     */   {
/*  82 */     return new javax.management.MBeanOperationInfo(this.name, this.description, convertSignature(this.signature), this.returnType, this.impact);
/*     */   }
/*     */ 
/*     */   private MBeanParameterInfo[] convertSignature(javax.management.MBeanParameterInfo[] source)
/*     */   {
/*  97 */     MBeanParameterInfo[] signature = new MBeanParameterInfo[source.length];
/*  98 */     for (int i = 0; i < source.length; i++)
/*     */     {
/* 100 */       signature[i] = new MBeanParameterInfo(source[i]);
/*     */     }
/* 102 */     return signature;
/*     */   }
/*     */ 
/*     */   private javax.management.MBeanParameterInfo[] convertSignature(MBeanParameterInfo[] source)
/*     */   {
/* 113 */     javax.management.MBeanParameterInfo[] signature = new javax.management.MBeanParameterInfo[source.length];
/* 114 */     for (int i = 0; i < source.length; i++)
/*     */     {
/* 116 */       signature[i] = source[i].toMBeanParameterInfo();
/*     */     }
/* 118 */     return signature;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.jmx.MBeanOperationInfo
 * JD-Core Version:    0.6.0
 */
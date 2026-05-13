/*     */ package flex.management.jmx;
/*     */ 
/*     */ public class MBeanConstructorInfo
/*     */ {
/*     */   public String name;
/*     */   public String description;
/*     */   public MBeanParameterInfo[] signature;
/*     */ 
/*     */   public MBeanConstructorInfo()
/*     */   {
/*     */   }
/*     */ 
/*     */   public MBeanConstructorInfo(javax.management.MBeanConstructorInfo mbeanConstructorInfo)
/*     */   {
/*  58 */     this.name = mbeanConstructorInfo.getName();
/*  59 */     this.description = mbeanConstructorInfo.getDescription();
/*  60 */     this.signature = convertSignature(mbeanConstructorInfo.getSignature());
/*     */   }
/*     */ 
/*     */   public javax.management.MBeanConstructorInfo toMBeanConstructorInfo()
/*     */   {
/*  71 */     return new javax.management.MBeanConstructorInfo(this.name, this.description, convertSignature(this.signature));
/*     */   }
/*     */ 
/*     */   private MBeanParameterInfo[] convertSignature(javax.management.MBeanParameterInfo[] source)
/*     */   {
/*  84 */     MBeanParameterInfo[] signature = new MBeanParameterInfo[source.length];
/*  85 */     for (int i = 0; i < source.length; i++)
/*     */     {
/*  87 */       signature[i] = new MBeanParameterInfo(source[i]);
/*     */     }
/*  89 */     return signature;
/*     */   }
/*     */ 
/*     */   private javax.management.MBeanParameterInfo[] convertSignature(MBeanParameterInfo[] source)
/*     */   {
/* 100 */     javax.management.MBeanParameterInfo[] signature = new javax.management.MBeanParameterInfo[source.length];
/* 101 */     for (int i = 0; i < source.length; i++)
/*     */     {
/* 103 */       signature[i] = source[i].toMBeanParameterInfo();
/*     */     }
/* 105 */     return signature;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.jmx.MBeanConstructorInfo
 * JD-Core Version:    0.6.0
 */
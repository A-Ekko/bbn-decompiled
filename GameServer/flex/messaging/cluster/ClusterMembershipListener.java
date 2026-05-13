/*     */ package flex.messaging.cluster;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.jgroups.Address;
/*     */ import org.jgroups.MembershipListener;
/*     */ import org.jgroups.View;
/*     */ 
/*     */ class ClusterMembershipListener
/*     */   implements MembershipListener
/*     */ {
/*     */   private JGroupsCluster cluster;
/*     */   private List memberList;
/*     */   private List zombieList;
/*     */ 
/*     */   public ClusterMembershipListener(Cluster cluster)
/*     */   {
/*  44 */     this.cluster = ((JGroupsCluster)cluster);
/*  45 */     this.memberList = new ArrayList();
/*  46 */     this.zombieList = new ArrayList();
/*     */   }
/*     */ 
/*     */   public void viewAccepted(View membershipView)
/*     */   {
/*  55 */     synchronized (this)
/*     */     {
/*  57 */       List currentMemberList = membershipView.getMembers();
/*     */ 
/*  60 */       for (Iterator iter = currentMemberList.iterator(); iter.hasNext(); )
/*     */       {
/*  62 */         Address member = (Address)iter.next();
/*  63 */         if ((!this.cluster.getLocalAddress().equals(member)) && (!this.memberList.contains(member)))
/*     */         {
/*  65 */           this.cluster.addClusterNode(member);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*  70 */       for (Iterator iter = this.memberList.iterator(); iter.hasNext(); )
/*     */       {
/*  72 */         Address member = (Address)iter.next();
/*  73 */         if (!membershipView.containsMember(member))
/*     */         {
/*  75 */           this.cluster.removeClusterNode(member);
/*  76 */           this.zombieList.remove(member);
/*     */         }
/*     */       }
/*  79 */       this.memberList = currentMemberList;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void suspect(Address zombieAddress)
/*     */   {
/*  92 */     synchronized (this)
/*     */     {
/*  94 */       this.zombieList.add(zombieAddress);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void block()
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean isZombie(Address address)
/*     */   {
/* 115 */     return this.zombieList.contains(address);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.cluster.ClusterMembershipListener
 * JD-Core Version:    0.6.0
 */
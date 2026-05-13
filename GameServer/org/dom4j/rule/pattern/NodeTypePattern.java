/*    */ package org.dom4j.rule.pattern;
/*    */ 
/*    */ import org.dom4j.Node;
/*    */ import org.dom4j.rule.Pattern;
/*    */ 
/*    */ public class NodeTypePattern
/*    */   implements Pattern
/*    */ {
/* 24 */   public static final NodeTypePattern ANY_ATTRIBUTE = new NodeTypePattern(2);
/*    */ 
/* 28 */   public static final NodeTypePattern ANY_COMMENT = new NodeTypePattern(8);
/*    */ 
/* 32 */   public static final NodeTypePattern ANY_DOCUMENT = new NodeTypePattern(9);
/*    */ 
/* 36 */   public static final NodeTypePattern ANY_ELEMENT = new NodeTypePattern(1);
/*    */ 
/* 40 */   public static final NodeTypePattern ANY_PROCESSING_INSTRUCTION = new NodeTypePattern(7);
/*    */ 
/* 44 */   public static final NodeTypePattern ANY_TEXT = new NodeTypePattern(3);
/*    */   private short nodeType;
/*    */ 
/*    */   public NodeTypePattern(short nodeType)
/*    */   {
/* 50 */     this.nodeType = nodeType;
/*    */   }
/*    */ 
/*    */   public boolean matches(Node node) {
/* 54 */     return node.getNodeType() == this.nodeType;
/*    */   }
/*    */ 
/*    */   public double getPriority() {
/* 58 */     return 0.5D;
/*    */   }
/*    */ 
/*    */   public Pattern[] getUnionPatterns() {
/* 62 */     return null;
/*    */   }
/*    */ 
/*    */   public short getMatchType() {
/* 66 */     return this.nodeType;
/*    */   }
/*    */ 
/*    */   public String getMatchesNodeName() {
/* 70 */     return null;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.dom4j.rule.pattern.NodeTypePattern
 * JD-Core Version:    0.6.0
 */
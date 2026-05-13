/*     */ package org.apache.mina.util.byteaccess;
/*     */ 
/*     */ import java.util.NoSuchElementException;
/*     */ 
/*     */ class ByteArrayList
/*     */ {
/*     */   private final Node header;
/*     */   private int firstByte;
/*     */   private int lastByte;
/*     */ 
/*     */   protected ByteArrayList()
/*     */   {
/*  59 */     this.header = new Node(null);
/*     */   }
/*     */ 
/*     */   public int lastByte()
/*     */   {
/*  71 */     return this.lastByte;
/*     */   }
/*     */ 
/*     */   public int firstByte()
/*     */   {
/*  83 */     return this.firstByte;
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/*  95 */     return this.header.next == this.header;
/*     */   }
/*     */ 
/*     */   public Node getFirst()
/*     */   {
/* 106 */     return this.header.getNextNode();
/*     */   }
/*     */ 
/*     */   public Node getLast()
/*     */   {
/* 117 */     return this.header.getPreviousNode();
/*     */   }
/*     */ 
/*     */   public void addFirst(ByteArray ba)
/*     */   {
/* 129 */     addNode(new Node(ba, null), this.header.next);
/* 130 */     this.firstByte -= ba.last();
/*     */   }
/*     */ 
/*     */   public void addLast(ByteArray ba)
/*     */   {
/* 142 */     addNode(new Node(ba, null), this.header);
/* 143 */     this.lastByte += ba.last();
/*     */   }
/*     */ 
/*     */   public Node removeFirst()
/*     */   {
/* 154 */     Node node = this.header.getNextNode();
/* 155 */     this.firstByte += node.ba.last();
/* 156 */     return removeNode(node);
/*     */   }
/*     */ 
/*     */   public Node removeLast()
/*     */   {
/* 167 */     Node node = this.header.getPreviousNode();
/* 168 */     this.lastByte -= node.ba.last();
/* 169 */     return removeNode(node);
/*     */   }
/*     */ 
/*     */   protected void addNode(Node nodeToInsert, Node insertBeforeNode)
/*     */   {
/* 185 */     Node.access$102(nodeToInsert, insertBeforeNode);
/* 186 */     Node.access$402(nodeToInsert, insertBeforeNode.previous);
/* 187 */     Node.access$102(insertBeforeNode.previous, nodeToInsert);
/* 188 */     Node.access$402(insertBeforeNode, nodeToInsert);
/*     */   }
/*     */ 
/*     */   protected Node removeNode(Node node)
/*     */   {
/* 201 */     Node.access$102(node.previous, node.next);
/* 202 */     Node.access$402(node.next, node.previous);
/* 203 */     Node.access$502(node, true);
/* 204 */     return node;
/*     */   }
/*     */ 
/*     */   public class Node
/*     */   {
/*     */     private Node previous;
/*     */     private Node next;
/*     */     private ByteArray ba;
/*     */     private boolean removed;
/*     */ 
/*     */     private Node()
/*     */     {
/* 235 */       this.previous = this;
/* 236 */       this.next = this;
/*     */     }
/*     */ 
/*     */     private Node(ByteArray ba)
/*     */     {
/* 247 */       if (ba == null)
/*     */       {
/* 249 */         throw new NullPointerException("ByteArray must not be null.");
/*     */       }
/* 251 */       this.ba = ba;
/*     */     }
/*     */ 
/*     */     public Node getPreviousNode()
/*     */     {
/* 262 */       if (!hasPreviousNode())
/*     */       {
/* 264 */         throw new NoSuchElementException();
/*     */       }
/* 266 */       return this.previous;
/*     */     }
/*     */ 
/*     */     public Node getNextNode()
/*     */     {
/* 277 */       if (!hasNextNode())
/*     */       {
/* 279 */         throw new NoSuchElementException();
/*     */       }
/* 281 */       return this.next;
/*     */     }
/*     */ 
/*     */     public boolean hasPreviousNode()
/*     */     {
/* 287 */       return this.previous != ByteArrayList.this.header;
/*     */     }
/*     */ 
/*     */     public boolean hasNextNode()
/*     */     {
/* 293 */       return this.next != ByteArrayList.this.header;
/*     */     }
/*     */ 
/*     */     public ByteArray getByteArray()
/*     */     {
/* 299 */       return this.ba;
/*     */     }
/*     */ 
/*     */     public boolean isRemoved()
/*     */     {
/* 305 */       return this.removed;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.util.byteaccess.ByteArrayList
 * JD-Core Version:    0.6.0
 */
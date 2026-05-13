/*     */ package org.apache.mina.handler.chain;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ 
/*     */ public class IoHandlerChain
/*     */   implements IoHandlerCommand
/*     */ {
/*  37 */   private static volatile int nextId = 0;
/*     */ 
/*  39 */   private final int id = nextId++;
/*     */ 
/*  41 */   private final String NEXT_COMMAND = IoHandlerChain.class.getName() + '.' + this.id + ".nextCommand";
/*     */ 
/*  44 */   private final Map<String, Entry> name2entry = new HashMap();
/*     */   private final Entry head;
/*     */   private final Entry tail;
/*     */ 
/*     */   public IoHandlerChain()
/*     */   {
/*  54 */     this.head = new Entry(null, null, "head", createHeadCommand(), null);
/*  55 */     this.tail = new Entry(this.head, null, "tail", createTailCommand(), null);
/*  56 */     Entry.access$102(this.head, this.tail);
/*     */   }
/*     */ 
/*     */   private IoHandlerCommand createHeadCommand() {
/*  60 */     return new IoHandlerCommand()
/*     */     {
/*     */       public void execute(IoHandlerCommand.NextCommand next, IoSession session, Object message) throws Exception {
/*  63 */         next.execute(session, message);
/*     */       } } ;
/*     */   }
/*     */ 
/*     */   private IoHandlerCommand createTailCommand() {
/*  69 */     return new IoHandlerCommand()
/*     */     {
/*     */       public void execute(IoHandlerCommand.NextCommand next, IoSession session, Object message) throws Exception {
/*  72 */         next = (IoHandlerCommand.NextCommand)session.getAttribute(IoHandlerChain.this.NEXT_COMMAND);
/*  73 */         if (next != null)
/*  74 */           next.execute(session, message);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public Entry getEntry(String name) {
/*  81 */     Entry e = (Entry)this.name2entry.get(name);
/*  82 */     if (e == null) {
/*  83 */       return null;
/*     */     }
/*  85 */     return e;
/*     */   }
/*     */ 
/*     */   public IoHandlerCommand get(String name) {
/*  89 */     Entry e = getEntry(name);
/*  90 */     if (e == null) {
/*  91 */       return null;
/*     */     }
/*     */ 
/*  94 */     return e.getCommand();
/*     */   }
/*     */ 
/*     */   public IoHandlerCommand.NextCommand getNextCommand(String name) {
/*  98 */     Entry e = getEntry(name);
/*  99 */     if (e == null) {
/* 100 */       return null;
/*     */     }
/*     */ 
/* 103 */     return e.getNextCommand();
/*     */   }
/*     */ 
/*     */   public synchronized void addFirst(String name, IoHandlerCommand command) {
/* 107 */     checkAddable(name);
/* 108 */     register(this.head, name, command);
/*     */   }
/*     */ 
/*     */   public synchronized void addLast(String name, IoHandlerCommand command) {
/* 112 */     checkAddable(name);
/* 113 */     register(this.tail.prevEntry, name, command);
/*     */   }
/*     */ 
/*     */   public synchronized void addBefore(String baseName, String name, IoHandlerCommand command)
/*     */   {
/* 118 */     Entry baseEntry = checkOldName(baseName);
/* 119 */     checkAddable(name);
/* 120 */     register(baseEntry.prevEntry, name, command);
/*     */   }
/*     */ 
/*     */   public synchronized void addAfter(String baseName, String name, IoHandlerCommand command)
/*     */   {
/* 125 */     Entry baseEntry = checkOldName(baseName);
/* 126 */     checkAddable(name);
/* 127 */     register(baseEntry, name, command);
/*     */   }
/*     */ 
/*     */   public synchronized IoHandlerCommand remove(String name) {
/* 131 */     Entry entry = checkOldName(name);
/* 132 */     deregister(entry);
/* 133 */     return entry.getCommand();
/*     */   }
/*     */ 
/*     */   public synchronized void clear() throws Exception {
/* 137 */     Iterator it = new ArrayList(this.name2entry.keySet()).iterator();
/*     */ 
/* 139 */     while (it.hasNext())
/* 140 */       remove((String)it.next());
/*     */   }
/*     */ 
/*     */   private void register(Entry prevEntry, String name, IoHandlerCommand command)
/*     */   {
/* 145 */     Entry newEntry = new Entry(prevEntry, prevEntry.nextEntry, name, command, null);
/*     */ 
/* 147 */     Entry.access$302(prevEntry.nextEntry, newEntry);
/* 148 */     Entry.access$102(prevEntry, newEntry);
/*     */ 
/* 150 */     this.name2entry.put(name, newEntry);
/*     */   }
/*     */ 
/*     */   private void deregister(Entry entry) {
/* 154 */     Entry prevEntry = entry.prevEntry;
/* 155 */     Entry nextEntry = entry.nextEntry;
/* 156 */     Entry.access$102(prevEntry, nextEntry);
/* 157 */     Entry.access$302(nextEntry, prevEntry);
/*     */ 
/* 159 */     this.name2entry.remove(entry.name);
/*     */   }
/*     */ 
/*     */   private Entry checkOldName(String baseName)
/*     */   {
/* 168 */     Entry e = (Entry)this.name2entry.get(baseName);
/* 169 */     if (e == null) {
/* 170 */       throw new IllegalArgumentException("Unknown filter name:" + baseName);
/*     */     }
/*     */ 
/* 173 */     return e;
/*     */   }
/*     */ 
/*     */   private void checkAddable(String name)
/*     */   {
/* 180 */     if (this.name2entry.containsKey(name))
/* 181 */       throw new IllegalArgumentException("Other filter is using the same name '" + name + "'");
/*     */   }
/*     */ 
/*     */   public void execute(IoHandlerCommand.NextCommand next, IoSession session, Object message)
/*     */     throws Exception
/*     */   {
/* 188 */     if (next != null) {
/* 189 */       session.setAttribute(this.NEXT_COMMAND, next);
/*     */     }
/*     */     try
/*     */     {
/* 193 */       callNextCommand(this.head, session, message);
/*     */     } finally {
/* 195 */       session.removeAttribute(this.NEXT_COMMAND);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void callNextCommand(Entry entry, IoSession session, Object message) throws Exception
/*     */   {
/* 201 */     entry.getCommand().execute(entry.getNextCommand(), session, message);
/*     */   }
/*     */ 
/*     */   public List<Entry> getAll() {
/* 205 */     List list = new ArrayList();
/* 206 */     Entry e = this.head.nextEntry;
/* 207 */     while (e != this.tail) {
/* 208 */       list.add(e);
/* 209 */       e = e.nextEntry;
/*     */     }
/*     */ 
/* 212 */     return list;
/*     */   }
/*     */ 
/*     */   public List<Entry> getAllReversed() {
/* 216 */     List list = new ArrayList();
/* 217 */     Entry e = this.tail.prevEntry;
/* 218 */     while (e != this.head) {
/* 219 */       list.add(e);
/* 220 */       e = e.prevEntry;
/*     */     }
/* 222 */     return list;
/*     */   }
/*     */ 
/*     */   public boolean contains(String name) {
/* 226 */     return getEntry(name) != null;
/*     */   }
/*     */ 
/*     */   public boolean contains(IoHandlerCommand command) {
/* 230 */     Entry e = this.head.nextEntry;
/* 231 */     while (e != this.tail) {
/* 232 */       if (e.getCommand() == command) {
/* 233 */         return true;
/*     */       }
/* 235 */       e = e.nextEntry;
/*     */     }
/* 237 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean contains(Class<? extends IoHandlerCommand> commandType) {
/* 241 */     Entry e = this.head.nextEntry;
/* 242 */     while (e != this.tail) {
/* 243 */       if (commandType.isAssignableFrom(e.getCommand().getClass())) {
/* 244 */         return true;
/*     */       }
/* 246 */       e = e.nextEntry;
/*     */     }
/* 248 */     return false;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 253 */     StringBuilder buf = new StringBuilder();
/* 254 */     buf.append("{ ");
/*     */ 
/* 256 */     boolean empty = true;
/*     */ 
/* 258 */     Entry e = this.head.nextEntry;
/* 259 */     while (e != this.tail) {
/* 260 */       if (!empty)
/* 261 */         buf.append(", ");
/*     */       else {
/* 263 */         empty = false;
/*     */       }
/*     */ 
/* 266 */       buf.append('(');
/* 267 */       buf.append(e.getName());
/* 268 */       buf.append(':');
/* 269 */       buf.append(e.getCommand());
/* 270 */       buf.append(')');
/*     */ 
/* 272 */       e = e.nextEntry;
/*     */     }
/*     */ 
/* 275 */     if (empty) {
/* 276 */       buf.append("empty");
/*     */     }
/*     */ 
/* 279 */     buf.append(" }");
/*     */ 
/* 281 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   public class Entry
/*     */   {
/*     */     private Entry prevEntry;
/*     */     private Entry nextEntry;
/*     */     private final String name;
/*     */     private final IoHandlerCommand command;
/*     */     private final IoHandlerCommand.NextCommand nextCommand;
/*     */ 
/*     */     private Entry(Entry prevEntry, Entry nextEntry, String name, IoHandlerCommand command)
/*     */     {
/* 303 */       if (command == null) {
/* 304 */         throw new NullPointerException("command");
/*     */       }
/* 306 */       if (name == null) {
/* 307 */         throw new NullPointerException("name");
/*     */       }
/*     */ 
/* 310 */       this.prevEntry = prevEntry;
/* 311 */       this.nextEntry = nextEntry;
/* 312 */       this.name = name;
/* 313 */       this.command = command;
/* 314 */       this.nextCommand = new IoHandlerCommand.NextCommand(IoHandlerChain.this)
/*     */       {
/*     */         public void execute(IoSession session, Object message) throws Exception {
/* 317 */           IoHandlerChain.Entry nextEntry = IoHandlerChain.Entry.this.nextEntry;
/* 318 */           IoHandlerChain.this.callNextCommand(nextEntry, session, message);
/*     */         }
/*     */       };
/*     */     }
/*     */ 
/*     */     public String getName()
/*     */     {
/* 327 */       return this.name;
/*     */     }
/*     */ 
/*     */     public IoHandlerCommand getCommand()
/*     */     {
/* 334 */       return this.command;
/*     */     }
/*     */ 
/*     */     public IoHandlerCommand.NextCommand getNextCommand()
/*     */     {
/* 341 */       return this.nextCommand;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.handler.chain.IoHandlerChain
 * JD-Core Version:    0.6.0
 */
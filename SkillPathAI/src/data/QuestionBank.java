package data;

import model.InterviewQuestion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The interview question bank: questions grouped by skill and difficulty.
 *
 * Each question has a list of keywords. InterviewEngine checks how many of
 * those keywords appear in the student's answer to give feedback and a score.
 *
 * HOW TO ADD A NEW SKILL:
 *   1. Add its name to SKILLS below.
 *   2. Create a method like addMySkill() and call it from getAllQuestions().
 *   3. Inside it, add questions with:  q("Skill", BASIC, "Question?", "keyword1,keyword2");
 * Nothing else needs to change -- the menus pick the new skill up automatically.
 */
public class QuestionBank {

    private static final String BASIC = InterviewQuestion.BASIC;
    private static final String INTER = InterviewQuestion.INTERMEDIATE;
    private static final String ADV = InterviewQuestion.ADVANCED;

    /** Skills shown in the "Practice by Skill" menu, in display order. */
    public static final List<String> SKILLS = Collections.unmodifiableList(Arrays.asList(
            "C", "C++", "Java", "Python", "HTML", "CSS", "JavaScript", "SQL",
            "Data Structures", "DBMS", "Operating Systems", "Computer Networks",
            "Machine Learning", "Artificial Intelligence"));

    private final List<InterviewQuestion> questions = new ArrayList<>();

    private QuestionBank() { }

    /** Builds and returns every question in the bank. */
    public static List<InterviewQuestion> getAllQuestions() {
        QuestionBank b = new QuestionBank();
        b.addC();
        b.addCpp();
        b.addJava();
        b.addPython();
        b.addHtml();
        b.addCss();
        b.addJavaScript();
        b.addSql();
        b.addDataStructures();
        b.addDbms();
        b.addOperatingSystems();
        b.addComputerNetworks();
        b.addMachineLearning();
        b.addArtificialIntelligence();
        return b.questions;
    }

    /** Helper: registers one question. */
    private void q(String skill, String difficulty, String question, String keywords) {
        questions.add(new InterviewQuestion(skill, question, keywords, difficulty));
    }

    // ---------------------------------------------------------------- C
    private void addC() {
        String s = "C";
        q(s, BASIC, "What is C and what are its key features?",
                "procedural,low-level,fast,portable,compiled,structured");
        q(s, BASIC, "What are variables and basic data types in C?",
                "int,float,char,double,memory,declare");
        q(s, BASIC, "What is an array in C?",
                "same type,contiguous,index,fixed size,memory");
        q(s, BASIC, "How are strings represented in C?",
                "char array,null,\\0,terminator,string.h");
        q(s, BASIC, "What is the difference between call by value and call by reference?",
                "copy,address,pointer,original,value");
        q(s, INTER, "What is a pointer and how do you declare one?",
                "address,variable,*,&,dereference");
        q(s, INTER, "What is the difference between a structure and a union?",
                "struct,union,memory,shared,members,largest");
        q(s, INTER, "What is recursion? Give an example.",
                "function calls itself,base case,factorial,stack,termination");
        q(s, INTER, "Explain malloc, calloc, realloc and free.",
                "heap,dynamic,allocate,initialize,zero,free,resize");
        q(s, INTER, "How do you read from and write to a file in C?",
                "fopen,fclose,fprintf,fscanf,fgets,mode,file pointer");
        q(s, ADV, "What is a dangling pointer and what is a memory leak?",
                "freed,invalid,leak,not freed,null,heap");
        q(s, ADV, "What are function pointers and where are they used?",
                "address of function,callback,pointer,array of functions,call");
        q(s, ADV, "Explain the relationship between pointers and arrays, and pointer arithmetic.",
                "base address,increment,element,size,name of array,pointer");
        q(s, ADV, "What are storage classes in C (auto, static, extern, register)?",
                "scope,lifetime,static,extern,register,auto");
        q(s, ADV, "What is the difference between stack and heap memory, and what causes stack overflow?",
                "stack,heap,automatic,manual,recursion,overflow,local");
    }

    // ---------------------------------------------------------------- C++
    private void addCpp() {
        String s = "C++";
        q(s, BASIC, "What is the difference between C and C++?",
                "object oriented,class,procedural,stl,namespace,reference");
        q(s, BASIC, "What are classes and objects in C++?",
                "blueprint,instance,members,methods,data");
        q(s, BASIC, "What are constructors and destructors?",
                "initialize,object creation,cleanup,destroyed,same name,~");
        q(s, BASIC, "What is the difference between a pointer and a reference?",
                "address,alias,null,reassign,initialized,*,&");
        q(s, BASIC, "What is encapsulation?",
                "data hiding,private,public,wrap,class,getter");
        q(s, INTER, "What are the types of inheritance in C++?",
                "single,multiple,multilevel,hierarchical,hybrid,base,derived");
        q(s, INTER, "What is polymorphism? Explain compile-time and run-time polymorphism.",
                "overloading,overriding,virtual,compile,run,many forms");
        q(s, INTER, "What is the STL and what are its main components?",
                "containers,algorithms,iterators,vector,map,template");
        q(s, INTER, "How does vector differ from an array, and how does map work?",
                "dynamic,resize,push_back,key,value,sorted,fixed");
        q(s, INTER, "What are templates in C++?",
                "generic,type,function template,class template,reuse,compile");
        q(s, ADV, "What is a virtual function and how does the vtable work?",
                "virtual,vtable,vptr,base pointer,run-time,override");
        q(s, ADV, "What are smart pointers (unique_ptr, shared_ptr, weak_ptr)?",
                "automatic,memory,ownership,reference count,raii,leak");
        q(s, ADV, "Explain move semantics and rvalue references.",
                "move,rvalue,&&,copy,resource,transfer,std::move");
        q(s, ADV, "What is the Rule of Three / Five?",
                "destructor,copy constructor,assignment,move,resource");
        q(s, ADV, "What is RAII and why is it important?",
                "resource,acquisition,initialization,destructor,exception safe,scope");
    }

    // ---------------------------------------------------------------- Java
    private void addJava() {
        String s = "Java";
        q(s, BASIC, "What is the difference between JDK, JRE and JVM?",
                "jdk,jre,jvm,development,runtime,bytecode,virtual machine");
        q(s, BASIC, "What are classes and objects in Java?",
                "blueprint,instance,state,behavior,new,fields,methods");
        q(s, BASIC, "What is a constructor?",
                "initialize,object,same name,no return,default,parameterized");
        q(s, BASIC, "What are the access modifiers in Java?",
                "public,private,protected,default,package,visibility");
        q(s, BASIC, "Why are Strings immutable in Java?",
                "immutable,string pool,security,thread safe,cannot change,hash");
        q(s, INTER, "What is inheritance in Java?",
                "extends,parent,child,reuse,super,is-a");
        q(s, INTER, "What is polymorphism? Explain overloading vs overriding.",
                "overloading,overriding,compile,run,same name,signature");
        q(s, INTER, "What is abstraction and how do abstract classes and interfaces provide it?",
                "hide,abstract,interface,implementation,contract,implements");
        q(s, INTER, "What is exception handling? Explain checked vs unchecked exceptions.",
                "try,catch,finally,throw,checked,unchecked,runtime");
        q(s, INTER, "What is the Java Collections Framework? Compare ArrayList and LinkedList.",
                "list,set,map,arraylist,linkedlist,dynamic array,node,index");
        q(s, ADV, "How does multithreading work in Java? Explain Thread vs Runnable.",
                "thread,runnable,run,start,concurrent,executor");
        q(s, ADV, "What is synchronization and what is a deadlock?",
                "synchronized,lock,monitor,race condition,deadlock,thread safe");
        q(s, ADV, "How does HashMap work internally?",
                "hash,bucket,hashcode,equals,collision,linked list,tree");
        q(s, ADV, "Explain the Java memory model: heap, stack and metaspace.",
                "heap,stack,objects,local,method area,metaspace,garbage");
        q(s, ADV, "What are lambda expressions and the Stream API?",
                "functional,lambda,stream,filter,map,collect,java 8");
    }

    // ---------------------------------------------------------------- Python
    private void addPython() {
        String s = "Python";
        q(s, BASIC, "What is Python and what are its key features?",
                "interpreted,high-level,dynamic,readable,object oriented,simple");
        q(s, BASIC, "What are the built-in data types in Python?",
                "int,float,str,list,tuple,dict,set,bool");
        q(s, BASIC, "What is the difference between a list and a tuple?",
                "mutable,immutable,list,tuple,brackets,parentheses");
        q(s, BASIC, "What are dictionaries and sets in Python?",
                "key,value,unique,unordered,hash,duplicate");
        q(s, BASIC, "What is the difference between == and is?",
                "value,identity,same object,equal,memory");
        q(s, INTER, "How do functions work in Python (arguments, *args, **kwargs, return)?",
                "def,return,args,kwargs,default,parameter");
        q(s, INTER, "What is list comprehension?",
                "concise,loop,expression,new list,for,condition");
        q(s, INTER, "How does exception handling work in Python?",
                "try,except,finally,raise,else,error");
        q(s, INTER, "Explain OOP in Python, including classes and inheritance.",
                "class,object,__init__,self,inherit,super,parent");
        q(s, INTER, "What are lambda functions?",
                "anonymous,lambda,single expression,inline,map,filter");
        q(s, INTER, "How do you handle files in Python?",
                "open,read,write,with,close,mode,context manager");
        q(s, ADV, "What are decorators in Python?",
                "function,wrapper,modify,@,behavior,higher-order");
        q(s, ADV, "What are iterators and generators?",
                "__iter__,__next__,yield,lazy,memory,iterable");
        q(s, ADV, "Explain Python memory management and garbage collection.",
                "reference counting,garbage collector,heap,private,cycle,automatic");
        q(s, ADV, "What is the Global Interpreter Lock (GIL)?",
                "lock,one thread,cpython,multithreading,multiprocessing,bytecode");
    }

    // ---------------------------------------------------------------- HTML
    private void addHtml() {
        String s = "HTML";
        q(s, BASIC, "What is HTML?",
                "hypertext,markup,structure,web page,browser,tags");
        q(s, BASIC, "What are tags, elements and attributes?",
                "tag,element,attribute,opening,closing,href,src");
        q(s, BASIC, "How do you create links and insert images in HTML?",
                "<a>,href,<img>,src,alt,anchor");
        q(s, BASIC, "How do you create lists and tables in HTML?",
                "ul,ol,li,table,tr,td,th");
        q(s, BASIC, "What is the basic structure of an HTML document?",
                "doctype,html,head,body,title,meta");
        q(s, INTER, "How do HTML forms work? Name common input types.",
                "form,input,action,method,text,password,submit,get,post");
        q(s, INTER, "What are semantic elements in HTML5?",
                "header,footer,nav,article,section,meaning,accessibility");
        q(s, INTER, "What is the difference between block and inline elements?",
                "block,inline,new line,width,div,span");
        q(s, INTER, "What is the difference between id and class attributes?",
                "unique,multiple,id,class,css,selector");
        q(s, INTER, "What is the purpose of the meta tag and the viewport?",
                "charset,viewport,responsive,description,metadata,seo");
        q(s, ADV, "What new features did HTML5 introduce?",
                "audio,video,canvas,semantic,localstorage,form,geolocation");
        q(s, ADV, "What is accessibility in HTML and what is ARIA?",
                "screen reader,aria,alt,role,semantic,keyboard");
        q(s, ADV, "What is the difference between localStorage, sessionStorage and cookies?",
                "persist,tab,session,size,server,expire,client");
        q(s, ADV, "What do the async and defer attributes on script tags do?",
                "async,defer,parsing,blocking,execute,download,order");
        q(s, ADV, "How does a browser turn HTML into a page (DOM, rendering)?",
                "parse,dom,tree,render,css,cssom,layout,paint");
    }

    // ---------------------------------------------------------------- CSS
    private void addCss() {
        String s = "CSS";
        q(s, BASIC, "What is CSS and how can it be added to a web page?",
                "style,inline,internal,external,link,presentation");
        q(s, BASIC, "What are CSS selectors?",
                "element,class,id,universal,group,descendant,target");
        q(s, BASIC, "Explain the CSS box model.",
                "content,padding,border,margin,width,height");
        q(s, BASIC, "What are common CSS units (px, em, rem, %, vh)?",
                "px,em,rem,percent,vh,vw,relative,absolute");
        q(s, BASIC, "What does the display property do?",
                "block,inline,none,flex,grid,inline-block");
        q(s, INTER, "What are the position values in CSS?",
                "static,relative,absolute,fixed,sticky,top,left");
        q(s, INTER, "What is Flexbox and when do you use it?",
                "flex,container,justify-content,align-items,direction,one-dimensional");
        q(s, INTER, "What is CSS specificity?",
                "specificity,inline,id,class,element,important,priority");
        q(s, INTER, "What is the difference between pseudo-classes and pseudo-elements?",
                "hover,::before,::after,state,:,part");
        q(s, INTER, "What are media queries and responsive design?",
                "@media,breakpoint,screen,responsive,width,mobile");
        q(s, ADV, "What is CSS Grid and how does it differ from Flexbox?",
                "grid,rows,columns,two-dimensional,template,flex,one-dimensional");
        q(s, ADV, "What is a stacking context and how does z-index work?",
                "z-index,stacking,context,position,layer,overlap");
        q(s, ADV, "What are CSS variables (custom properties)?",
                "--,var(),reuse,:root,theme,custom");
        q(s, ADV, "How do CSS transitions and animations differ?",
                "transition,animation,@keyframes,duration,state,timing");
        q(s, ADV, "Explain the cascade and inheritance in CSS.",
                "cascade,inherit,order,specificity,parent,origin");
    }

    // ---------------------------------------------------------------- JavaScript
    private void addJavaScript() {
        String s = "JavaScript";
        q(s, BASIC, "What is the difference between var, let and const?",
                "scope,block,function,reassign,hoist,const,let,var");
        q(s, BASIC, "What are the data types in JavaScript?",
                "string,number,boolean,null,undefined,object,symbol");
        q(s, BASIC, "What are functions and arrow functions?",
                "function,=>,arrow,this,syntax,return,short");
        q(s, BASIC, "What are common array methods (map, filter, reduce)?",
                "map,filter,reduce,new array,callback,foreach");
        q(s, BASIC, "How do you create and use objects in JavaScript?",
                "key,value,property,method,literal,dot,{}");
        q(s, INTER, "What is the DOM and how do you manipulate it?",
                "document object model,tree,getelementbyid,queryselector,innerhtml,element");
        q(s, INTER, "What are events and event bubbling?",
                "addeventlistener,click,bubble,propagation,handler,capture");
        q(s, INTER, "What are the key ES6 features?",
                "let,const,arrow,template,destructuring,spread,class,module");
        q(s, INTER, "What is the difference between == and ===?",
                "type,coercion,strict,value,equality,loose");
        q(s, INTER, "What is a closure?",
                "inner function,outer,scope,remember,variable,lexical");
        q(s, ADV, "What are Promises in JavaScript?",
                "pending,resolved,rejected,then,catch,asynchronous,future");
        q(s, ADV, "What are async and await?",
                "async,await,promise,synchronous,try,catch,readable");
        q(s, ADV, "Explain the JavaScript event loop.",
                "call stack,queue,single-threaded,callback,microtask,web api,non-blocking");
        q(s, ADV, "What is prototypal inheritance?",
                "prototype,chain,object,inherit,__proto__,constructor");
        q(s, ADV, "How does the this keyword work, and what do call, apply and bind do?",
                "this,context,call,apply,bind,object,arrow");
    }

    // ---------------------------------------------------------------- SQL
    private void addSql() {
        String s = "SQL";
        q(s, BASIC, "What is the purpose of the SELECT statement and the WHERE clause?",
                "retrieve,columns,rows,filter,condition,table");
        q(s, BASIC, "How does ORDER BY work?",
                "sort,asc,desc,ascending,descending,column");
        q(s, BASIC, "What are aggregate functions in SQL?",
                "count,sum,avg,min,max,group");
        q(s, BASIC, "What is the difference between a primary key and a foreign key?",
                "unique,identifier,reference,another table,relationship,null");
        q(s, BASIC, "What are SQL constraints?",
                "not null,unique,primary key,foreign key,check,default");
        q(s, INTER, "What does GROUP BY do, and how is HAVING different from WHERE?",
                "group,aggregate,having,where,before,after,filter");
        q(s, INTER, "Explain the different types of JOINs.",
                "inner,left,right,full,match,cross,self");
        q(s, INTER, "What is a subquery? When would you use one instead of a join?",
                "nested,inner query,outer,in,exists,result");
        q(s, INTER, "What is the difference between DELETE, TRUNCATE and DROP?",
                "delete,truncate,drop,rows,table,rollback,structure");
        q(s, INTER, "What is normalization? Explain 1NF, 2NF and 3NF.",
                "redundancy,1nf,2nf,3nf,atomic,dependency,anomaly");
        q(s, ADV, "What is a transaction and what are the ACID properties?",
                "atomicity,consistency,isolation,durability,commit,rollback");
        q(s, ADV, "What is an index and how does it affect performance?",
                "faster,lookup,b-tree,write,slower,search,column");
        q(s, ADV, "What are transaction isolation levels?",
                "read committed,repeatable read,serializable,dirty read,phantom,isolation");
        q(s, ADV, "What are window functions in SQL?",
                "over,partition,row_number,rank,window,running,without collapsing");
        q(s, ADV, "How would you optimize a slow SQL query?",
                "index,explain,execution plan,select *,join,avoid,limit");
    }

    // ---------------------------------------------------------------- Data Structures
    private void addDataStructures() {
        String s = "Data Structures";
        q(s, BASIC, "What is the difference between an array and a linked list?",
                "contiguous,node,pointer,insertion,random access,dynamic,size");
        q(s, BASIC, "What is a stack? Name its operations and uses.",
                "lifo,push,pop,peek,undo,recursion");
        q(s, BASIC, "What is a queue and what are its types?",
                "fifo,enqueue,dequeue,circular,priority,deque");
        q(s, BASIC, "What is time complexity and Big-O notation?",
                "growth,input size,worst case,o(n),efficiency,notation");
        q(s, BASIC, "Compare linear search and binary search.",
                "sorted,o(log n),o(n),middle,divide,sequential");
        q(s, INTER, "What is a Binary Search Tree?",
                "left,right,smaller,greater,search,insert,node");
        q(s, INTER, "Compare bubble sort, merge sort and quick sort.",
                "o(n^2),o(n log n),divide,pivot,merge,swap,stable");
        q(s, INTER, "What is hashing and how are collisions handled?",
                "hash function,key,index,collision,chaining,probing,table");
        q(s, INTER, "Explain tree traversals (inorder, preorder, postorder).",
                "inorder,preorder,postorder,left,root,right,recursion");
        q(s, INTER, "What is a doubly linked list and a circular linked list?",
                "prev,next,two pointers,circular,last,head,traverse");
        q(s, ADV, "Explain BFS and DFS graph traversal.",
                "queue,stack,level,depth,visited,breadth,recursion");
        q(s, ADV, "How does Dijkstra's algorithm find the shortest path?",
                "shortest path,priority queue,weights,distance,relax,greedy,non-negative");
        q(s, ADV, "What are balanced trees such as AVL trees?",
                "balance factor,rotation,height,o(log n),self-balancing,avl");
        q(s, ADV, "What is a heap and how is it used as a priority queue?",
                "min heap,max heap,complete binary tree,root,heapify,priority");
        q(s, ADV, "What is dynamic programming?",
                "overlapping subproblems,optimal substructure,memoization,tabulation,recursion,store");
    }

    // ---------------------------------------------------------------- DBMS
    private void addDbms() {
        String s = "DBMS";
        q(s, BASIC, "What is a DBMS and how is it better than a file system?",
                "database,management system,redundancy,consistency,security,access");
        q(s, BASIC, "What is the difference between DBMS and RDBMS?",
                "tables,relationships,relational,keys,rows,columns");
        q(s, BASIC, "What are the different types of keys in a database?",
                "primary,candidate,foreign,super,composite,unique");
        q(s, BASIC, "What is an ER model?",
                "entity,relationship,attribute,diagram,cardinality,design");
        q(s, BASIC, "What are DDL, DML and DCL commands?",
                "create,alter,drop,insert,update,delete,grant,revoke");
        q(s, INTER, "What is normalization and why is it needed?",
                "redundancy,anomaly,normal form,dependency,split,tables");
        q(s, INTER, "What are the ACID properties?",
                "atomicity,consistency,isolation,durability,transaction");
        q(s, INTER, "What is indexing and why is it used?",
                "fast,search,b+ tree,pointer,lookup,column,overhead");
        q(s, INTER, "What are views in a database?",
                "virtual table,query,security,simplify,stored,select");
        q(s, INTER, "What are the different types of joins?",
                "inner,outer,left,right,full,cross,combine");
        q(s, ADV, "What is concurrency control and why is it needed?",
                "concurrent,lock,conflict,lost update,isolation,transactions,consistency");
        q(s, ADV, "What is serializability and two-phase locking?",
                "serial,schedule,growing,shrinking,lock,equivalent,conflict");
        q(s, ADV, "What is a deadlock in a DBMS and how is it handled?",
                "wait,circular,prevention,detection,victim,rollback,timeout");
        q(s, ADV, "How does a B+ tree index work?",
                "leaf,balanced,linked,range,order,pointers,height");
        q(s, ADV, "How does a DBMS recover from failures (logs, checkpoints)?",
                "log,checkpoint,undo,redo,write-ahead,recovery,crash");
    }

    // ---------------------------------------------------------------- Operating Systems
    private void addOperatingSystems() {
        String s = "Operating Systems";
        q(s, BASIC, "What is an operating system and what are its functions?",
                "resource,manage,process,memory,file,device,interface");
        q(s, BASIC, "What is the difference between a process and a thread?",
                "process,thread,memory,lightweight,share,independent,address space");
        q(s, BASIC, "What is a kernel?",
                "core,hardware,system calls,privileged,manage,monolithic,micro");
        q(s, BASIC, "What is a system call?",
                "interface,user,kernel,request,service,mode,api");
        q(s, BASIC, "What is multiprogramming vs multitasking?",
                "multiple,cpu,time sharing,switch,utilization,jobs");
        q(s, INTER, "Explain CPU scheduling algorithms (FCFS, SJF, Round Robin).",
                "fcfs,sjf,round robin,time quantum,waiting time,priority,preemptive");
        q(s, INTER, "What is a deadlock and what are the necessary conditions?",
                "mutual exclusion,hold and wait,no preemption,circular wait,resources");
        q(s, INTER, "What is paging vs segmentation?",
                "pages,frames,fixed,segments,logical,variable,page table");
        q(s, INTER, "What is virtual memory?",
                "disk,swap,page fault,larger,demand paging,ram,illusion");
        q(s, INTER, "What are semaphores and mutexes?",
                "wait,signal,binary,counting,lock,critical section,synchronization");
        q(s, ADV, "What are page replacement algorithms (FIFO, LRU, Optimal)?",
                "fifo,lru,optimal,page fault,replace,frame,belady");
        q(s, ADV, "Explain the Banker's algorithm for deadlock avoidance.",
                "safe state,available,max,allocation,need,request,avoidance");
        q(s, ADV, "What is a race condition and how do you prevent it?",
                "shared,concurrent,critical section,lock,mutex,synchronization,order");
        q(s, ADV, "What happens during a context switch?",
                "save,restore,pcb,state,registers,overhead,scheduler");
        q(s, ADV, "What is thrashing and how can it be avoided?",
                "page faults,swapping,working set,degree of multiprogramming,cpu utilization,low");
    }

    // ---------------------------------------------------------------- Computer Networks
    private void addComputerNetworks() {
        String s = "Computer Networks";
        q(s, BASIC, "What are the layers of the OSI model?",
                "physical,data link,network,transport,session,presentation,application");
        q(s, BASIC, "What is the TCP/IP model?",
                "application,transport,internet,network access,four layers,protocol");
        q(s, BASIC, "What is an IP address? Explain IPv4 vs IPv6.",
                "32-bit,128-bit,address,unique,identify,dotted,hexadecimal");
        q(s, BASIC, "What is the difference between LAN, MAN and WAN?",
                "local,metropolitan,wide,area,range,size,internet");
        q(s, BASIC, "What is DNS?",
                "domain name,ip address,resolve,translate,server,lookup");
        q(s, INTER, "What is the difference between TCP and UDP?",
                "reliable,connection,connectionless,fast,handshake,order,streaming");
        q(s, INTER, "What is the difference between HTTP and HTTPS?",
                "secure,encrypted,ssl,tls,port 443,port 80,certificate");
        q(s, INTER, "What is subnetting?",
                "subnet mask,network,host,divide,cidr,address,efficient");
        q(s, INTER, "What is the role of routers, switches and hubs?",
                "router,switch,hub,mac,ip,forward,broadcast");
        q(s, INTER, "What are ARP and DHCP?",
                "arp,mac address,dhcp,assign,automatically,ip address,dynamic");
        q(s, ADV, "Explain the TCP three-way handshake.",
                "syn,syn-ack,ack,connection,establish,sequence,three");
        q(s, ADV, "What is NAT and why is it used?",
                "private,public,translate,address,router,ipv4,shortage");
        q(s, ADV, "What are routing algorithms (distance vector, link state)?",
                "distance vector,link state,rip,ospf,bellman-ford,dijkstra,shortest");
        q(s, ADV, "How does SSL/TLS secure a connection?",
                "encryption,certificate,handshake,public key,symmetric,authentication,session key");
        q(s, ADV, "What are flow control and congestion control in TCP?",
                "sliding window,receiver,network,congestion,slow start,window size,ack");
    }

    // ---------------------------------------------------------------- Machine Learning
    private void addMachineLearning() {
        String s = "Machine Learning";
        q(s, BASIC, "What is machine learning?",
                "learn,data,patterns,without explicit,predict,algorithm,experience");
        q(s, BASIC, "What is the difference between supervised and unsupervised learning?",
                "labeled,unlabeled,supervised,unsupervised,classification,clustering");
        q(s, BASIC, "What are features and labels?",
                "input,output,features,labels,target,variables");
        q(s, BASIC, "Why do we split data into training and testing sets?",
                "train,test,unseen,evaluate,generalize,overfitting");
        q(s, BASIC, "What is overfitting and underfitting?",
                "overfitting,underfitting,noise,training,generalize,complex,simple");
        q(s, INTER, "How does linear regression work?",
                "line,continuous,least squares,slope,intercept,predict,error");
        q(s, INTER, "What are precision, recall and accuracy?",
                "precision,recall,accuracy,true positive,false positive,f1,confusion");
        q(s, INTER, "How does a decision tree work?",
                "split,node,leaf,gini,entropy,feature,rules");
        q(s, INTER, "What are KNN and K-Means clustering?",
                "neighbors,distance,k,cluster,centroid,unsupervised,classify");
        q(s, INTER, "What is cross-validation?",
                "folds,k-fold,train,validate,average,performance,split");
        q(s, ADV, "Explain gradient descent.",
                "cost function,learning rate,minimize,gradient,iteration,weights,derivative");
        q(s, ADV, "What is the bias-variance tradeoff?",
                "bias,variance,tradeoff,underfit,overfit,complexity,error");
        q(s, ADV, "What is regularization (L1 and L2)?",
                "penalty,overfitting,lasso,ridge,weights,coefficients,l1");
        q(s, ADV, "How do neural networks learn through backpropagation?",
                "layers,weights,backpropagation,error,gradient,activation,forward");
        q(s, ADV, "What are ensemble methods such as Random Forest and Boosting?",
                "multiple models,bagging,boosting,random forest,combine,voting,variance");
    }

    // ---------------------------------------------------------------- Artificial Intelligence
    private void addArtificialIntelligence() {
        String s = "Artificial Intelligence";
        q(s, BASIC, "What is Artificial Intelligence?",
                "machines,human intelligence,learn,reason,problem solving,simulate");
        q(s, BASIC, "What is the difference between narrow AI and general AI?",
                "narrow,general,specific task,human-level,weak,strong");
        q(s, BASIC, "What is an intelligent agent?",
                "perceive,environment,sensors,actuators,act,goal,agent");
        q(s, BASIC, "What is the Turing Test?",
                "alan turing,human,machine,conversation,indistinguishable,imitation");
        q(s, BASIC, "How are AI, Machine Learning and Deep Learning related?",
                "subset,ai,machine learning,deep learning,neural networks,broader");
        q(s, INTER, "What is the difference between uninformed and informed search?",
                "bfs,dfs,heuristic,a*,uninformed,informed,goal");
        q(s, INTER, "What is knowledge representation?",
                "facts,rules,logic,semantic network,frames,reasoning,represent");
        q(s, INTER, "What is Natural Language Processing (NLP)?",
                "language,text,tokenization,sentiment,translation,understand,speech");
        q(s, INTER, "What is an expert system?",
                "knowledge base,inference engine,rules,domain,expert,decision");
        q(s, INTER, "What are the main types of machine learning in AI?",
                "supervised,unsupervised,reinforcement,labeled,reward,patterns");
        q(s, ADV, "How does the A* search algorithm work?",
                "f(n),g(n),h(n),heuristic,cost,optimal,admissible");
        q(s, ADV, "Explain the minimax algorithm and alpha-beta pruning.",
                "minimax,game,min,max,alpha,beta,prune,tree");
        q(s, ADV, "What is reinforcement learning?",
                "agent,reward,environment,policy,action,state,trial");
        q(s, ADV, "What are CNNs and Transformers used for?",
                "convolution,image,attention,sequence,language,layers,transformer");
        q(s, ADV, "What are the ethical issues in AI (bias, privacy, explainability)?",
                "bias,privacy,fairness,transparency,explainable,accountability,jobs");
    }
}

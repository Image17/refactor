# Precondition checks for rename field-variable refactorings

## Preconditions:
There exist three precondition checks for the rename field-variable refactoring. The first is that the variable being renamed must exist. The second is that the renamed variable must not be a duplicate. Finally, the renamed field variable must not result in variable shadowing.

The duplicate variable precondition check ensures that the
renamed variable doesn’t already exist within the current scope
of the program. Duplicate variables can be defined as multiple
accessible variables that have the same name.


## General Terms

### Access Modifiers
Access modifiers modify the visibility of a class or a variable. Field variable access modifiers must be considered when determining if it can be accessed outside of its given class or package. If a variable doesn’t explicitly declare an access modifier then the default access modifier is applied. 

The default access modifier allows the variable to be accessed within all classes in the same package only. The explicit access modifiers include public, private and protected.

The public access modifier allows variables to be accessed in all classes and all packages. The private access modifer allows variables to be accessed within the declared class only. The protected access modifier allows variables to be accessed within all classes within the same package and subclasses outside of the package. 

### Field Variables
Field variables, also known as instance variables are variables which are declared in a class but outside of constructors, methods or other blocks.
These variables are created when the class is instantiated and are accessible anywhere in the class. Access modifiers applied
to instance variables don’t effect their accessibility within the
class itself.

### Local variables
Local variables are variables that are defined within block scope. These variables are declared and initialized when the block scope is executed and removed when the block scope has completed. This renaming implementation doesn't aim to rename these variables. 


### Scope
The scope of a variable refers to where in the program a given variable can be accessed. This is determined by the variables access modifier, position within its class, overall position within the programs inheritance hierarchy and package.

### Block Scope
Block scope refers to a code within a pair of curly braces. Variables declared within block scope are only accessible within that block. Examples of block scope include classes, constructors, methods, loops and conditional statements. 

In order for a variable to be accessed after the termination of block scope it must be declared before the body of the block. In general, a variable can be accessed within the same set of brackets that it was declared. This includes nested blocks within the outer block in which the variable was declared.


```java
class A {
	int x = 1;

	for (int i = 0; i < 5; i++) {
		int j = i;
		// Code here CAN access both the local and field variables.
		System.out.println(x);
		System.out.println(i);
		System.out.println(j);
	}

	// Code here CAN access field variables.
	// Code here CANNOT access the above local variables.
	System.out.println(x); // Outputs 1
	System.out.println(i); // ERROR
	System.out.println(j); // ERROR
}

```

### Variable Shadowing
Subclass variables are able to reduce the visibility of its super class. This is known as variable shadowing and occurs when a variable declared within a block scope has the same name as a variable in its outer scope. If an field variable and a local variable have the same name then the local variable will be accessed. This process effectively shadows the field variable.

The same concept is applied to class hierarchies. A field variable declared within a super class will be shadowed by any variable with the same name in a sub class. An example of this is illustrated in Fig 2. The
two variables in are completely distinct and have completely
separate visibilities in different classes.


```java
class A {
	public int x = 5;
}

class B extends A {
	public int x = 10;
}

public class Test {
	public static void main(String [] args) {
		A a = new A();
		B b = new B();
		System.out.println(a.x); // Outputs 5
		System.out.println(b.x); // Outputs 10
	}
}
```
## Interfaces?
field must not be equal to

if field is global and within same class it CANNOT be used in methods or inner block scope in the same class, a sub class, implemented interface or extended abstract class. This also must be enforced for each class that implements an interface

## Class hierarchy considerations
Parent and child classes both need to be considered when determining if a field variable rename passes the duplicate precondition. The following condierations must be taken into account when changing the name of a field variable.

#### Parent:
* All super type(s) global fields must be explored.
* All interfaces and extended super type(s) global fields must be explored.
* DO NOT need to explore child block scope fields.

#### Child:
* All child classes global and block scope fields must be explored. 
* All implemented interfaces global and method fields must be explored.


# Scenerios


## Duplicate Variables Same Class
Variable shadowing is a direct result of duplicate variables within a class hierarchy. Duplicate variables can also exist within the same class. Two field variables declared with the same name within the class results in a compile time error. Both scenerios must be considered.

The following example results in a compile time error after renaming the field variable from i to j.
```java
class A {
    int i = 2; // rename to j
    int j = 2;
}
```

## Super Class Preconditions

#### Public field visible (Semantic)
```java
package demo;

public class A {
	public int i = 1;
}

class B extends A {
	public int j = 2; // rename to i
}

class Test {
	public static void main(String[] args) {
		B b = new B();
		System.out.println(b.i);
	}
}

```
#### Public field visibility reduction (Synatx)
```java
class A {
    public int i = 1;
}

class B extends A {
    public int i = 2; 
}

public class Test {
    public static void main(String []args){
        B b = new B();
        System.out.println(b.i);
     }
}
```

#### Package private field visible (Semantic)
```java
package demo;

public class A {
	int i = 1; // Package protected field variable: Default access Modifier.
}

class B extends A {
	public int j = 2; // rename to i
}

class Test {
	public static void main(String[] args) {
		B b = new B();
		System.out.println(b.i); // Outputs 2
	}
}
```

#### Package private field NOT visible (Semantic)

```java
package one;

public class A {
	int i = 1; // Package protected field variable: Default access Modifier.
}

package two;
import one.A;

class B extends A {
	// Super class A field variable i is not visible after rename.
	public int j = 2; // rename to i
}

class Test {
	public static void main(String[] args) {
		B b = new B();
		System.out.println(b.i); // Outputs 2 with no variable shadowing.
	}
}
```

#### Protected field visible (Semantic)
```java
public class A {
	protected int i = 1;
}

class B extends A {
	public int j = 2; // rename to i
}

class Test {
	public static void main(String[] args) {
		B b = new B();
		System.out.println(b.i); // Outputs 2
	}
}
```

#### Protected field NOT visible (Semantic)
```java
package one;

public class A {
	protected int i = 1;
}

package two;

class B {
	protected int j = 2; // rename to i
}

class Test {
	public static void main(String[] args) {
		B b = new B();
		System.out.println(b.i); // Outputs 2 with no variable shadowing
	}
}
```


### Super Class - Abstract

```java
public abstract class A {
	public int i = 1;

}

class T extends A {
	public int j = 2; // rename to i
	
	public void t() {	
		System.out.println(i);
		System.out.println(j);
	}
}
```
### Interface

```java
public interface A {
	public int i = 1; 

}

class T implements A {
	int j = 2; // rename to i
	
	public void t() {
		System.out.println(i);
	}
}
```

## Child Class Preconditions

We must also consider each child class and class that implements the interface the change is in

### Sub class

```java
public class A {
	public int i = 1; // rename to j

}

class T extends A {
	int j = 2;
	
	public void t() {	
		System.out.println(i);
		System.out.println(j);
	}
}
```

include abstract

### Implemented Interface

```java
public interface A {
	public int i = 1; // rename to j

}

class T implements A {
	int j = 2;
	
	public void t() {
		System.out.println(i);
		System.out.println(j);
	}
}
```



We must also compare against all interfaces inherited by a class

### Multiple Implemented Interface in sub class

```java
public interface A {
	public int i = 1;
	
}

interface B {
	public int k = 3; // rename to i
}

class T implements A, B {
	public int j = 2;
	
	public void t() {	
		System.out.println(i);
		System.out.println(k);
		System.out.println(j);
	}
}
```
We must also consider all subclasses of a class that implements an interface

### Interface subclass extensions

```java
public interface A {
	public int i = 1; // rename to k
}

abstract class B  {
	public int k = 3;
}

class T extends B implements A {
	public int j = 2;
	
	public void t() {	
		System.out.println(i);
		System.out.println(k);
		System.out.println(j);
	}
}
```


## Block Scope Variables
Variables defined in a block must be considered when renaming field variables in the current class, super class or interface.  



### Same class
The following scenario outlines a field variable being shadowed within a methods scope after being renamed. 

```java
public class A {
	public int i = 2; // rename to j
	
	public void t() {
		int j = 1; 
		int k = i;
		System.out.println(k);
	}
	
	public static void main(String args[]) {
	    A a = new A();
	    a.t(); // Outputs 2
	}
}
```

The same is true with other block scopes such as loops, conditionals or constructors. The below example demonstrates how the field variables would be shadowed within block scope after being renamed.
```java
public class A {
	int a = 2; // rename to i 
	int b = 2; // rename to j
	int c = 2; // rename to k
	
	A () {
	    int k = 0;
	    System.out.println(k); // Outputs 0
	}
	
	void t() {
		if (j == 2) { // True
		    int j = 1;
		    System.out.println(j); // Outputs 1
		}
		
		for (int i = 0; i < 5; i++) {
		    System.out.println(i); // Outputs 01234
		}
		
		System.out.println(i); // Outputs 2
		System.out.println(j); // Outputs 2
		System.out.println(k); // Outputs 2
	}
	
	public static void main(String args[]) {
	    A a = new A();
	    a.t();
	}
}
```
### Inheritance

Local varaibles of child classes must be considered when renaming a parents field variable. 

The following example demonstrates how renaming a parents field variable would cause the variable to be shadowed within the childs block scope.

```java
class A {
    int i = 2; // rename to j
}

class B extends A {
    B() {
        int j = 0;
        System.out.println(j);
    }
}

public class Test {
    public static void main(String args[]) {
        B b = new B(); // Outputs 0
    }
}
```

The same is true when implementing interfaces

```java
interface A {
    int j = 2; // rename to j
}

class B implements A {
    B() {
        int j = 0;
        System.out.println(j);
    }
}

public class Test {
    public static void main(String args[]) {
        B b = new B(); // Outputs 0
    }
}
```

The implemented interfaces of super classes must also be considered.

```java
interface A {
    int a = 2; // rename to j
}

abstract class T implements A {
    int t = 2; // rename to k
}

class B extends T {
    B() {
        System.out.println(j); // Outputs 2
        System.out.println(k); // Outputs 2
        int j = 0;
        int k = 0;
        System.out.println(j); // Outputs 0
        System.out.println(k); // Outputs 0
    }
}

public class Test {
    public static void main(String args[]) {
        B b = new B(); 
    }
}
```

Block scope of super classes shouldn't be considered when renaming child class field variables. These variables are not visible to the parent.

```java
class A {
    A() {
        int i = 2; // Field variable i within class B is not visible.
        System.out.println(i);
    }
}

class B extends A {
    int j = 0; // rename to i
}

public class Test {
    public static void main(String args[]) {
        B b = new B(); // Outputs 2
    }
}
```


The same is true for abstract classes

```java
abstract class A {
    void t() {
        int i = 2; // Field variable i within class B is not visible.
        System.out.println(i);
    }
}

class B extends A {
    int j = 0; // rename to i
}

public class Test {
    public static void main(String args[]) {
        B b = new B(); 
        b.t(); // Outputs 2
    }
}
```


### Static Methods and Fields
Static methods require an instance of the super class in order to access the parents field variables. In addition, the parents field variable must be declared as static. The same is true for variables within the same class.
For this resason, variables within static methods won't be shadowed.

```java
class A {
    static int i = 2;
}

class B extends A {
    static void t() {
        int j = 0; // rename to i
        System.out.println(A.i); // Outputs 2
        System.out.println(i); // Outputs 0
    }
}

public class Test {
    public static void main(String args[]) {
        B b = new B(); 
        b.t();
    }
}
```

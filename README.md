# Precondition checks for rename field-variable refactorings

## Preconditions:
There exist two precondition checks for the rename field-variable refactoring. The first is that the variable being renamed must exist. The second is that the renamed variable must not be a duplicate.

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
public class A {
	public int i = 1;
}

class B extends A {
	private int j = 2; // rename to i
}

class Test {
	public static void main(String[] args) {
		B b = new B();
		System.out.println(b.i); // Outputs 2
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
		System.out.println(j);
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


### Methods
Due to method scope a variable defined in a method must only consider fields in its current class or super types and cannot be aware of children fields.  This means that if a field within a method were to be renamed it must not conflict with any visible fields of the current class or super classes/ interfaces


We must also consider all variables visible in methods within the same class

## Same class - elaborate or put first

```java
public class A {
	public int i = 1; // rename to j
	
	public void t() {
		int j = 1;
		
		System.out.println(i);
		System.out.println(j);
	}
}
```
```java
```
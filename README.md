field must not be equal to

if field is global and within same class it CANNOT be used in methods in the same class, a sub class, implemented interface, extended abstract class.  This also needs to be implemented to each class that implements the interface

Parent considerations
* Explore all super types global fields
* Explore all interfaces and their super types global fields

Child considerations
* Explore all child classes global fields and methods
* Explore all interface implementations global fields and methods

Another name will shadow access to the renamed element.	


## Parents - We must consider all visible parent classes and interfaces when changing the value of a variable


###  Super class Preconditions

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
		System.out.println(b.i);
	}
}
```

##### Package private field visible (Semantic)
```java
package demo;

public class A {
	int i = 1;
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
		System.out.println(b.i);
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

We must also consider each child class and class that implements the interface the change is in

## Children

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

```java
```
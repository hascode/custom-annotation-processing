package com.hascode.tutorial.app;

public class Main {
	public static void main(final String[] args) {
		System.out.println("foooo");
		SomeClass some = new SomeClass();
		System.out.println(some.foo());
		BookDao dao = new BookDao();
	}
}

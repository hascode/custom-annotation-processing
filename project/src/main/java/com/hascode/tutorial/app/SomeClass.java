package com.hascode.tutorial.app;

import com.hascode.tutorial.annotation.Dao;

@Dao(entity = Book.class)
public class SomeClass {
	public String foo() {
		return "foo";
	}
}

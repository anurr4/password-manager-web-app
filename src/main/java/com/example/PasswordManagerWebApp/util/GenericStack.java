/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.PasswordManagerWebApp.util;
import java.util.Stack;
/**
 *
 * @author Anurra
 */
public class GenericStack<T> {
    private Stack<T> stack = new Stack<>();

    public void push(T item) {
        stack.push(item);
    }

    public T pop() {
        return stack.isEmpty() ? null : stack.pop();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }
}

package net.ncguy.argent.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Guy on 09/07/2016.
 */
public class CollectionUtils {

    public static <T> Stack<T> listToStack(List<T> list) {
        Stack<T> stack = new Stack<>();
        for (int i = list.size()-1; i >= 0; i--)
            stack.push(list.get(i));
        return stack;
    }

    public static <T> List<T> stackToList(Stack<T> stack) {
        List<T> list = new ArrayList<>();
        while(!stack.isEmpty())
            list.add(stack.pop());
        return list;
    }

    public static <T> void flipStack(Stack<T> stack) {
        ArrayList<T> tmp = new ArrayList<>(stack);
        stack.clear();
        for (int i = tmp.size()-1; i >= 0; i--)
            stack.push(tmp.get(i));
    }

}

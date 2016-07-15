package net.ncguy.argent.data.tree.sample;

import net.ncguy.argent.data.tree.Visitable;
import net.ncguy.argent.data.tree.Visitor;

/**
 * Created by Guy on 15/07/2016.
 */
public class PrintIndentedVisitor<T> implements Visitor<T> {

    private final int indent;

    public PrintIndentedVisitor(int indent) {
        this.indent = indent;
    }

    @Override
    public Visitor<T> visit(Visitable<T> visitable) {
        return new PrintIndentedVisitor<>(this.indent+2);
    }

    @Override
    public void visitData(Visitable<T> visitable, T data) {
        for(int i = 0; i < this.indent; i++) System.out.print(" ");
        System.out.println(data.toString());
    }
}

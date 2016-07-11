package net.ncguy.argent.utils.data.tree;

/**
 * Created by Guy on 10/07/2016.
 */
public class PrintIndentedVisitor<T> implements Visitor<T> {

    final int indent;

    public PrintIndentedVisitor(int indent) {
        this.indent = indent;
    }

    @Override
    public Visitor<T> visitVisitable(Visitable<T> visitable) {
        return new PrintIndentedVisitor<>(this.indent+2);
    }

    @Override
    public void visitData(Visitable<T> visitable, T data) {
        for(int i = 0; i < indent; i++)
            System.out.print(" ");
        System.out.println(data.toString());
    }
}

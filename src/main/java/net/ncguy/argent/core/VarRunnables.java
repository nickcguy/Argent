package net.ncguy.argent.core;

/**
 * Created by Guy on 12/06/2016.
 */
public class VarRunnables {
    public interface VarRunnable<T> {
        void run(T arg);
    }
    public interface Var2Runnable<T> {
        void run(T arg1, T arg2);
    }

    public interface ReturnRunnable<T> {
        T run();
    }
}

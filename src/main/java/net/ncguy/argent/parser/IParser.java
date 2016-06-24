package net.ncguy.argent.parser;

import java.util.Set;

/**
 * Created by Guy on 24/06/2016.
 */
public interface IParser<T> {
     Set<T> parse(String log);
}

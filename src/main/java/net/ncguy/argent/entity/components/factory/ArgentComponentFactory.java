package net.ncguy.argent.entity.components.factory;

import com.badlogic.ashley.core.Component;
import net.ncguy.argent.data.Meta;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by Guy on 22/07/2016.
 */
public abstract class ArgentComponentFactory<T extends Component> {

    public abstract Class<T> componentClass();

    public abstract ArrayList<String> errors(WorldEntity entity);
    public abstract String meta();
    public boolean canBeApplied(WorldEntity entity) {
        return errors(entity).size() == 0;
    }
    public boolean canBeRemoved(WorldEntity entity) { return true; }

    public abstract T build(WorldEntity entity);

    /**
     * Reports the error if the criteria {@link Supplier} returns true
     * @param errors
     * @param text
     * @param criteria
     */
    public void error(List<String> errors, String text, Supplier<Boolean> criteria) {
        if(criteria.get()) errors.add(text);
    }


    public String name() {
        if(getClass().isAnnotationPresent(Meta.class)) {
            return getClass().getAnnotation(Meta.class).displayName();
        }
        return StringUtils.splitCamelCase(getClass().getSimpleName());
    }

    public String category() {
        if(getClass().isAnnotationPresent(Meta.class)) {
            return getClass().getAnnotation(Meta.class).category();
        }
        return "Component";
    }

}

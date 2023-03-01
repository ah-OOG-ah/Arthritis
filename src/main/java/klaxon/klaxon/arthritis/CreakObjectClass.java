package klaxon.klaxon.arthritis;

import klaxon.klaxon.arthritis.api.CreakObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * A DashObject which is an object with adds Dash support to a target object. <br>
 * This class is very lazy as reflection is really slow
 *
 * @param <R> Raw
 * @param <D> Dashable
 */
public final class CreakObjectClass<R, D extends CreakObject> {

    private final Class<D> dashClass;
    @Nullable
    private Class<R> targetClass;
    int dashObjectId;

    public CreakObjectClass(Class<?> dashClass) {
        //noinspection unchecked
        this.dashClass = (Class<D>) dashClass;
    }

    public Class<D> getDashClass() {
        return this.dashClass;
    }

    // lazy
    @Nonnull
    public Class<R> getTargetClass() {
        if (this.targetClass == null) {
            Type[] genericInterfaces = this.dashClass.getGenericInterfaces();
            if (genericInterfaces.length == 0) {
                throw new RuntimeException(this.dashClass + " does not implement DashObject.");
            }

            boolean foundDashObject = false;
            for (Type genericInterface : genericInterfaces) {
                if (ScanUtil.getClassFrom(genericInterface) == CreakObject.class) {
                    foundDashObject = true;
                    if (genericInterface instanceof ParameterizedType targetClass) {
                        Type[] actualTypeArguments = targetClass.getActualTypeArguments();
                        Class<?> classFrom = ScanUtil.getClassFrom(actualTypeArguments[0]);
                        if (classFrom == null) {
                            throw new RuntimeException(this.dashClass + " has a non resolvable DashObject parameter");
                        }
                        this.targetClass = (Class<R>) classFrom;
                    } else {
                        throw new RuntimeException(this.dashClass + " implements raw DashObject");
                    }
                }
            }

            if (!foundDashObject) {
                throw new RuntimeException(this.dashClass + " must implement DashObject");
            }
        }
        return this.targetClass;
    }


    public int getDashObjectId() {
        return dashObjectId;
    }

    @Override
    public String toString() {
        return "CreakObjectClass{" +
            "dashClass=" + dashClass +
            ", targetClass=" + targetClass +
            ", dashObjectId=" + dashObjectId +
            '}';
    }
}

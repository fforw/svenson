package org.svenson.info;

import java.lang.reflect.Method;

public interface AccessFactory {
    Getter createGetter(Method getterMethod);

    Setter createSetter(Method setterMethod);

    Adder createAdder(Method adderMethod);

    PostConstructInvoker createPostConstructInvoker(Method postConstructMethod);
}

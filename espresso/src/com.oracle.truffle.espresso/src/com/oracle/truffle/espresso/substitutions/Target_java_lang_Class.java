/*
 * Copyright (c) 2018, 2020, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.oracle.truffle.espresso.substitutions;

import com.oracle.truffle.espresso.impl.Klass;
import com.oracle.truffle.espresso.meta.Meta;
import com.oracle.truffle.espresso.runtime.StaticObject;
import com.oracle.truffle.espresso.vm.InterpreterToVM;
import com.oracle.truffle.espresso.vm.VM;

/**
 * These substitutions are provided for performance concerns.
 * <p>
 * Native methods in {@link Class} that are annotated with
 * {@code jdk.internal.vm.annotation.IntrinsicCandidate} get a substitution here.
 */
@EspressoSubstitutions
public final class Target_java_lang_Class {
    // region perf substitutions

    /**
     * Determines if the specified {@code Object} is assignment-compatible with the object
     * represented by this {@code Class}. This method is the dynamic equivalent of the Java language
     * {@code instanceof} operator. The method returns {@code true} if the specified {@code Object}
     * argument is non-null and can be cast to the reference type represented by this {@code Class}
     * object without raising a {@code ClassCastException.} It returns {@code false} otherwise.
     *
     * <p>
     * Specifically, if this {@code Class} object represents a declared class, this method returns
     * {@code true} if the specified {@code Object} argument is an instance of the represented class
     * (or of any of its subclasses); it returns {@code false} otherwise. If this {@code Class}
     * object represents an array class, this method returns {@code true} if the specified
     * {@code Object} argument can be converted to an object of the array class by an identity
     * conversion or by a widening reference conversion; it returns {@code false} otherwise. If this
     * {@code Class} object represents an interface, this method returns {@code true} if the class
     * or any superclass of the specified {@code Object} argument implements this interface; it
     * returns {@code false} otherwise. If this {@code Class} object represents a primitive type,
     * this method returns {@code false}.
     *
     * @param obj the object to check
     * @return true if {@code obj} is an instance of this class
     *
     * @since JDK1.1
     */
    @Substitution(hasReceiver = true)
    public static boolean isInstance(@JavaType(Class.class) StaticObject self, @JavaType(Object.class) StaticObject obj) {
        return InterpreterToVM.instanceOf(obj, self.getMirrorKlass());
    }

    @Substitution(hasReceiver = true)
    public static boolean isAssignableFrom(@JavaType(Class.class) StaticObject self, @JavaType(Class.class) StaticObject cls,
                    @Inject Meta meta) {
        if (StaticObject.isNull(cls)) {
            meta.throwNullPointerException();
        }
        return self.getMirrorKlass().isAssignableFrom(cls.getMirrorKlass());
    }

    @Substitution(hasReceiver = true)
    public static boolean isInterface(@JavaType(Class.class) StaticObject self) {
        return VM.JVM_IsInterface(self);
    }

    @Substitution(hasReceiver = true)
    public static boolean isPrimitive(@JavaType(Class.class) StaticObject self) {
        return VM.JVM_IsPrimitiveClass(self);
    }

    @Substitution(hasReceiver = true)
    public static boolean isArray(@JavaType(Class.class) StaticObject self) {
        return VM.JVM_IsArrayClass(self);
    }

    @Substitution(hasReceiver = true)
    public static @JavaType(Class.class) StaticObject getSuperclass(@JavaType(Class.class) StaticObject self) {
        if (self.getMirrorKlass().isInterface()) {
            return StaticObject.NULL;
        }
        Klass superclass = self.getMirrorKlass().getSuperKlass();
        if (superclass == null) {
            return StaticObject.NULL;
        }
        return superclass.mirror();
    }

    @Substitution(hasReceiver = true)
    public static int getModifiers(@JavaType(Class.class) StaticObject self) {
        return self.getMirrorKlass().getClassModifiers();
    }

    @Substitution(hasReceiver = true)
    public static boolean isHidden(@JavaType(Class.class) StaticObject self) {
        return VM.JVM_IsHiddenClass(self);
    }

    // endregion perf substitutions
}

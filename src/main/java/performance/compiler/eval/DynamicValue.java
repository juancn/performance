package performance.compiler.eval;

import performance.parser.ParseException;
import performance.runtime.ExpectationData;
import performance.util.MutableArray;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DynamicValue extends Op {
    private final MutableArray<CharSequence> expression;
    private Object value;
    private boolean isBoolean;

    public DynamicValue(MutableArray<CharSequence> expression) {
        this.expression = expression;
    }

    @Override
    public double doubleVal() {
        if(isBoolean) {
            return super.doubleVal();
        }
        return ((Number) value).doubleValue();
    }

    @Override
    public boolean booleanVal() {
        if(!isBoolean) {
            return super.booleanVal();
        }
        return ((Boolean)value);
    }

    public void resolve(final Class<?> ctxClass, final Object instance, final Object[] argumentValues, final ExpectationData data) throws ParseException {
        final Object rootValue;
        final String rootVar = String.valueOf(expression.get(0));

        final int index = data.localVarIndexOf(rootVar);

        final boolean isStaticVar = "static".equals(rootVar);

        if(isStaticVar) {
            rootValue = null;
        } else if("this".equals(rootVar)){
            rootValue = instance;
        } else if(index >= 0) {
            //If the method is an instance method, local variables ar shifted by one
            int off = instance == null? 0 : 1;
            rootValue = argumentValues[index - off];
        }  else {
            try {
                rootValue = argumentValues[Integer.parseInt(rootVar)];
            } catch (ArrayIndexOutOfBoundsException e) {
                throw error("Index out of range: " + rootVar);
            } catch (NumberFormatException e) {
                throw error("Cannot resolve root value", 1);
            }
        }


        final Object v;
        if(isStaticVar) {
            v = resolveMember(null, ctxClass, 1);
        } else {
            if(rootValue == null) {
                throw error("Cannot resolve root value", 0);
            }
            v = resolveMember(rootValue, rootValue.getClass(), 1);
        }

        isBoolean = v instanceof Boolean;
        if(!isBoolean && !(v instanceof Number)){
            throw error("Expression yields a non-numeric value", expression.size());
        }
        value = v;
    }

    private ParseException error(String message) {
        return new ParseException("ERROR: " + message);
    }

    private ParseException error(String message, int upTo) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < expression.size() && i <= upTo; i++) {
            if(i != 0) {
                sb.append('.');
            }
            sb.append(expression.get(i));
        }
        return new ParseException("ERROR: " + message + ", ");
    }

    private Object resolveMember(final Object rootValue, final Class<?> rootValueClass, final int i) throws ParseException {
        if(i >= expression.size()) {
            return rootValue;
        }

        final String member = String.valueOf(expression.get(i));

        Method mtd = findMethod(rootValueClass, member);
        if(mtd == null) {
             mtd = findMethod(rootValueClass, "get" + capitalize(member));
        }
        if(mtd == null) {
             mtd = findMethod(rootValueClass, "is" + capitalize(member));
        }
        if(mtd == null) {
             mtd = findMethod(rootValueClass, "has" + capitalize(member));
        }

        final Object result;
        if(mtd == null) {
            try {
                Field declaredField = rootValueClass.getDeclaredField(member);
                declaredField.setAccessible(true);
                result = declaredField.get(rootValue);
            } catch (NoSuchFieldException e) {
                throw error(e.getMessage(), i);
            } catch (IllegalAccessException e) {
                throw error(e.getMessage(), i);
            }

        } else {
            try {
                mtd.setAccessible(true);
                result = mtd.invoke(rootValue);
            } catch (IllegalAccessException e) {
                throw error(e.getMessage(), i);
            } catch (InvocationTargetException e) {
                throw error(e.getMessage(), i);
            }
        }

        if(result == null) {
            throw error("Sub-expression yields a null value", i);
        }


        return resolveMember(result, result.getClass(), i+1);
    }

    private String capitalize(String member) {
        //Ignore multi-char codepoints for now at least
        return Character.toUpperCase(member.charAt(0)) + member.substring(1);
    }

    private Method findMethod(Class<?> rootValueClass, String member) {
        Method mtd;
        try {
            mtd = rootValueClass.getDeclaredMethod(member);
        } catch (NoSuchMethodException e) {
            mtd = null;
        }
        return mtd;
    }

    @Override
    public String toString() {
        return expression.join(".") + "=" + value;
    }
}

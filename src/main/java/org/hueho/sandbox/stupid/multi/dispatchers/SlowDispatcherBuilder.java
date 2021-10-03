package org.hueho.sandbox.stupid.multi.dispatchers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SlowDispatcherBuilder {
    private final List<DispatchTuple> candidates = new ArrayList<>();
    private final Method toDispatch;
    private final Method defaultDispatch;

    public SlowDispatcherBuilder(Method toDispatch) {
        this(toDispatch, null);
    }

    public SlowDispatcherBuilder(Method toDispatch, Method defaultDispatch) {
        this.toDispatch = toDispatch;
        this.defaultDispatch = defaultDispatch;
    }

    public SlowDispatcherBuilder add(Method candidate) {
        candidates.add(new DispatchTuple(candidate, DispatchTuple.ParameterOrder.DEFAULT, candidate.getParameterTypes()));
        return this;
    }

    public SlowDispatcherBuilder addMirrored(Method candidate) {
        candidates.add(new DispatchTuple(candidate, DispatchTuple.ParameterOrder.MIRRORED, candidate.getParameterTypes()));
        return this;
    }

    public SlowDispatcher build() {
        return new SlowDispatcher(toDispatch, defaultDispatch, candidates);
    }
}

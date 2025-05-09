package org.smojol.common.transpiler;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.smojol.common.ast.SemanticCategory;

import java.util.Collection;

@Getter
public class SetTranspilerNode extends TranspilerNode {
    private final TranspilerNode source;
    private final TranspilerNode destination;

    public SetTranspilerNode(TranspilerNode source, TranspilerNode destination) {
        super(ImmutableList.of(SemanticCategory.DATA_FLOW));
        this.source = source;
        this.destination = destination;
    }

    @Override
    public String description() {
        return String.format("set(%s, %s)", destination.description(), source.description());
    }

    @Override
    public Collection<TranspilerNode> internalElements() {
        return ImmutableList.of(source, destination);
    }
}

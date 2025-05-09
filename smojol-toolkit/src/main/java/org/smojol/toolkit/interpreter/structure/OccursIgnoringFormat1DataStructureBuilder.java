package org.smojol.toolkit.interpreter.structure;

import org.eclipse.lsp.cobol.core.CobolParser;
import org.smojol.common.structure.SourceSection;
import org.smojol.common.vm.strategy.UnresolvedReferenceStrategy;
import org.smojol.common.vm.structure.Format1DataStructure;
import org.smojol.common.vm.structure.Format1DataStructureBuildStrategy;

public class OccursIgnoringFormat1DataStructureBuilder implements Format1DataStructureBuildStrategy {
    @Override
    public Format1DataStructure build(CobolParser.DataDescriptionEntryFormat1Context format1Structure, UnresolvedReferenceStrategy strategy, SourceSection sourceSection) {
        return new Format1DataStructure(format1Structure, strategy, sourceSection);
    }
}

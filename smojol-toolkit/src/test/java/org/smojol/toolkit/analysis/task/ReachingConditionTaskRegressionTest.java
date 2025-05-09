package org.smojol.toolkit.analysis.task;

import com.google.common.collect.ImmutableList;
import org.jgrapht.graph.DefaultEdge;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.smojol.common.dialect.LanguageDialect;
import org.smojol.common.flowchart.FlowchartOutputFormat;
import org.smojol.common.graph.GraphSlice;
import org.smojol.common.graph.GraphSliceTask;
import org.smojol.common.graph.ReachingConditionDefinitionTask;
import org.smojol.common.id.UUIDProvider;
import org.smojol.common.resource.LocalFilesystemOperations;
import org.smojol.common.transpiler.PrintTranspilerNode;
import org.smojol.common.transpiler.TranspilerFlowgraph;
import org.smojol.common.transpiler.TranspilerInstruction;
import org.smojol.common.transpiler.TranspilerNode;
import org.smojol.toolkit.analysis.pipeline.BaseAnalysisModel;
import org.smojol.toolkit.analysis.pipeline.ProgramSearch;
import org.smojol.toolkit.analysis.task.analysis.CodeTaskRunner;
import org.smojol.toolkit.analysis.task.transpiler.BuildTranspilerFlowgraphTask;
import org.smojol.toolkit.interpreter.FullProgram;
import org.smojol.toolkit.interpreter.structure.OccursIgnoringFormat1DataStructureBuilder;
import org.smojol.toolkit.task.AnalysisTaskResult;
import org.smojol.toolkit.task.AnalysisTaskResultOK;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.smojol.toolkit.task.CommandLineAnalysisTask.BUILD_BASE_ANALYSIS;

public class ReachingConditionTaskRegressionTest {
    @Test
    @Disabled
    public void canFindReachingConditionForSimpleAcyclicGraph() throws IOException {
        UUIDProvider idProvider = new UUIDProvider();
        Map<String, List<AnalysisTaskResult>> result = new CodeTaskRunner("/Users/asgupta/code/smojol/smojol-test-code",
                "/Users/asgupta/code/smojol/out/report",
                ImmutableList.of(new File("/Users/asgupta/code/smojol/smojol-test-code")),
                "/Users/asgupta/code/smojol/che-che4z-lsp-for-cobol-integration/server/dialect-idms/target/dialect-idms.jar",
                LanguageDialect.IDMS, new FullProgram(FlowchartOutputFormat.PNG, idProvider), idProvider, new OccursIgnoringFormat1DataStructureBuilder(), new ProgramSearch(), new LocalFilesystemOperations())
                .runForPrograms(ImmutableList.of(BUILD_BASE_ANALYSIS), ImmutableList.of("simple-if.cbl"));

        AnalysisTaskResult value = result.values().stream().toList().getFirst().getFirst();
        BaseAnalysisModel baseResult = ((AnalysisTaskResultOK) value).getDetail();
        BuildTranspilerFlowgraphTask buildTranspilerFlowgraphTask = new BuildTranspilerFlowgraphTask(baseResult.rawAST(), baseResult.dataStructures(), baseResult.symbolTable(), ImmutableList.of());
        TranspilerFlowgraph transpilerFlowgraph = buildTranspilerFlowgraphTask.run();
        List<TranspilerInstruction> instructions = transpilerFlowgraph.instructions();
        TranspilerInstruction start = instructions.getFirst();
        TranspilerInstruction printInstruction = instructions.stream().filter(instr -> instr.ref() instanceof PrintTranspilerNode).findFirst().get();
        GraphSlice<TranspilerInstruction, DefaultEdge> slice = new GraphSliceTask<>(transpilerFlowgraph.instructionFlowgraph(), DefaultEdge.class).run(start, printInstruction);
        Map<TranspilerInstruction, TranspilerNode> reachingConditions = new ReachingConditionDefinitionTask<>(slice).run();
        System.out.println("DONE");
    }
}

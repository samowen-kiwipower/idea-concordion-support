package org.concordion.plugin.idea.completion;

import com.google.common.collect.ImmutableList;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.codeInsight.lookup.impl.LookupImpl;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import org.concordion.plugin.idea.ConcordionCodeInsightFixtureTestCase;
import com.intellij.openapi.vfs.VirtualFile;
import org.concordion.plugin.idea.ConcordionCommands;
import org.jetbrains.annotations.NotNull;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.concordion.plugin.idea.ConcordionCommands.DEFAULT_COMMANDS_WITH_C_PREFIX;

public class ConcordionCommandsCompletionContributorTest extends ConcordionCodeInsightFixtureTestCase {

    private static final Iterable<String> EXTENSION_COMMANDS_WITH_EXT_PREFIX = ImmutableList.of(
            "ext:screenshot",
            "ext:embed",
            "ext:executeOnlyIf"
    );

    @Override
    protected String getTestDataPath() {
        return "testData/completion";
    }

    public void testCompleteConcordionCommandsInHtmlTags() {

        copyTestFixtureToConcordionProject("Commands.java");
        VirtualFile htmlSpec = copySpecToConcordionProject("Commands.html");

        myFixture.configureFromExistingVirtualFile(htmlSpec);
        myFixture.completeBasic();

        assertThat(myFixture.getLookupElementStrings())
                .containsAll(DEFAULT_COMMANDS_WITH_C_PREFIX)
                .doesNotContainAnyElementsOf(EXTENSION_COMMANDS_WITH_EXT_PREFIX);

        completeWithConcordionCommand("c:execute");

        myFixture.checkResultByFile("CommandsCompleted.html");
    }

    public void testCompleteExtensionsCommandsInHtmlTags() {

        copyTestFixtureToConcordionProject("ExtensionsCommands.java");
        VirtualFile htmlSpec = copySpecToConcordionProject("ExtensionsCommands.html");

        myFixture.configureFromExistingVirtualFile(htmlSpec);
        myFixture.completeBasic();

        assertThat(myFixture.getLookupElementStrings())
                .containsAll(DEFAULT_COMMANDS_WITH_C_PREFIX)
                .containsAll(EXTENSION_COMMANDS_WITH_EXT_PREFIX);
    }

    public void testDoesNotDuplicateConcordionPrefixForStartedHalfTypedCommand() {

        copyTestFixtureToConcordionProject("CommandStarted.java");
        VirtualFile htmlSpec = copySpecToConcordionProject("CommandStarted.html");

        myFixture.configureFromExistingVirtualFile(htmlSpec);
        myFixture.completeBasic();

        completeWithConcordionCommand("c:execute");

        myFixture.checkResultByFile("CommandStartedCompleted.html");
    }

    public void testInsertConcordionSchemaIfAbsentWhileCompletion() {

        copyTestFixtureToConcordionProject("CommandsNoSchema.java");
        VirtualFile htmlSpec = copySpecToConcordionProject("CommandsNoSchema.html");

        myFixture.configureFromExistingVirtualFile(htmlSpec);
        myFixture.completeBasic();

        completeWithConcordionCommand("c:execute");

        myFixture.checkResultByFile("CommandsNoSchemaCompleted.html");
    }

    public void testInsertConcordionExtensionSchemaIfAbsentWhileCompletion() {

        copyTestFixtureToConcordionProject("ExtensionsCommandsNoSchema.java");
        VirtualFile htmlSpec = copySpecToConcordionProject("ExtensionsCommandsNoSchema.html");

        myFixture.configureFromExistingVirtualFile(htmlSpec);
        myFixture.completeBasic();

        completeWithConcordionCommand("ext:executeOnlyIf");

        myFixture.checkResultByFile("ExtensionsCommandsNoSchemaCompleted.html");
    }

    public void testCompleteConcordionCommandsInMarkdownLinks() {

        copyTestFixtureToConcordionProject("Commands.java");
        VirtualFile htmlSpec = copySpecToConcordionProject("Commands.md");

        myFixture.configureFromExistingVirtualFile(htmlSpec);
        myFixture.completeBasic();

        assertThat(myFixture.getLookupElementStrings())
                .containsAll(ConcordionCommands.MD_COMMANDS.stream().map(c -> c + '=').collect(toList()));
    }

    private void completeWithConcordionCommand(String command) {
        LookupImpl lookup = (LookupImpl) LookupManager.getActiveLookup(myFixture.getEditor());
        for (LookupElement element : lookup.getItems()) {
            if (element.getLookupString().equals(command)) {
                lookup.setCurrentItem(element);
                lookup.finishLookup('\t');
                return;
            }
        }
        fail(command + " is not found in completion");
    }

    @Override
    protected void runTest() throws Throwable {
        new WriteCommandAction(getProject()) {
            @Override
            protected void run(@NotNull Result result) throws Throwable {
                ConcordionCommandsCompletionContributorTest.super.runTest();
            }
        }.execute();
    }
}

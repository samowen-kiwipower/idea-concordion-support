package org.concordion.plugin.idea.inspection;

import org.concordion.plugin.idea.ConcordionCodeInsightFixtureTestCase;
import com.intellij.openapi.vfs.VirtualFile;

import static org.concordion.plugin.idea.HighlightingAssert.*;
import static com.intellij.lang.annotation.HighlightSeverity.*;

public class UsingUndeclaredVariableTest extends ConcordionCodeInsightFixtureTestCase {

    @Override
    protected String getTestDataPath() {
        return "testData/inspection";
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        myFixture.enableInspections(UsingUndeclaredVariable.class);

        copyTestFixtureToConcordionProject("ResolvingVariables.java");
        VirtualFile htmlSpec = copySpecToConcordionProject("ResolvingVariables.html");

        myFixture.configureFromExistingVirtualFile(htmlSpec);
    }

    public void testWarnUndeclaredVariableUsage() {

        assertThat(myFixture.doHighlighting())
                .has(usingUndeclaredVariable().withText("#undefined"));
    }

    public void testWarnUsingBeforeDeclaring() {

        assertThat(myFixture.doHighlighting())
                .has(usingUndeclaredVariable().withText("#definedToLate"));
    }

    public void testDoNotWarnUsageOfDeclaredVariable() {

        assertThat(myFixture.doHighlighting())
                .hasNo(usingUndeclaredVariable().withText("#definedBeforehand"));
    }

    public void testDoNotWarnUsageOfNestedDeclaration() {

        assertThat(myFixture.doHighlighting())
                .hasNo(usingUndeclaredVariable().withText("#definedInside"));
    }

    public void testDoNotWarnUsageOfReservedVariableWithoutDeclaration() {

        assertThat(myFixture.doHighlighting())
                .hasNo(usingUndeclaredVariable());
    }

    private Info usingUndeclaredVariable() {
        return anInfo().withSeverity(WARNING).withText("#TEXT").withDescription("Using undeclared variable");
    }

    //TODO @improvement warn overwriting reserved variables
}
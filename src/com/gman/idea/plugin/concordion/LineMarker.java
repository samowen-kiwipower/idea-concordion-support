package com.gman.idea.plugin.concordion;

import com.gman.idea.plugin.concordion.lang.ConcordionIcons;
import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class LineMarker {

    public static LineMarkerInfo<PsiElement> infoFor(@NotNull PsiElement element, @NotNull GutterIconNavigationHandler<PsiElement> navigationHandler) {
        return new LineMarkerInfo<>(
                element,
                element.getTextRange(),
                ConcordionIcons.ICON,
                Pass.UPDATE_ALL,
                LineMarker::generateTooltipForConcordion,
                navigationHandler,
                GutterIconRenderer.Alignment.CENTER
        );
    }

    @NotNull
    public static String generateTooltipForConcordion(@NotNull PsiElement element) {
        return "Concordion";
    }

    @NotNull
    public static GutterIconNavigationHandler<PsiElement> withNavigationTo(@NotNull NavigatablePsiElement element) {
        return (e, elt) -> element.navigate(true);
    }
}
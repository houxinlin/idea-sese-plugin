package dev.coolrequest.sese;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.wm.CustomStatusBarWidget;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.StatusBarWidgetFactory;
import com.intellij.openapi.wm.impl.status.TextPanel;
import com.intellij.ui.ClickListener;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.MouseEvent;

public class StatusBarNextWidgetFactory implements StatusBarWidgetFactory {
    @Override
    public @NotNull @NonNls String getId() {
        return "dev.coolrequest.sese.StatusBarNextWidgetFactory";
    }

    @Override
    public @NotNull @NlsContexts.ConfigurableName String getDisplayName() {
        return "Next";
    }

    @Override
    public @NotNull StatusBarWidget createWidget(@NotNull Project project) {
        return new NextStatusBarWidget();
    }

    static class NextStatusBarWidget implements CustomStatusBarWidget {

        @Override
        public JComponent getComponent() {
            TextPanel textPanel = new TextPanel(() -> "Next");
            textPanel.setText("Next");
            new ClickListener() {
                @Override
                public boolean onClick(@NotNull MouseEvent mouseEvent, int i) {
                    ApplicationManager.getApplication().executeOnPooledThread(new NextImageRunnable());
                    return false;
                }
            }.installOn(textPanel);
            return textPanel;
        }

        @NotNull
        @Override
        public String ID() {
            return "sese@NextStatusBarWidget";
        }
    }
}

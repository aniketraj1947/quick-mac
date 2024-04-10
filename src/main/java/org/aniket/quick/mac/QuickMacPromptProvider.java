package org.aniket.quick.mac;

import org.jline.utils.AttributedString;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.jline.PromptProvider;

@Configuration
public class QuickMacPromptProvider implements PromptProvider {

    private boolean isStart = true;

    @Override
    public AttributedString getPrompt() {
        if (isStart) {
            isStart = false;
            return new AttributedString("\nWelcome to Quick Mac - An interactive app-centric network & system analytics tool!\n\nType help to see list of available commands.\n\nquick-mac > ");
        }
        return new AttributedString("quick-mac > ");
    }
}

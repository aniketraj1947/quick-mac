package org.aniket.quick.mac.commands;

import static org.aniket.quick.mac.helper.Constants.COPY_DESC;
import static org.aniket.quick.mac.helper.Constants.COPY_PROMPT;
import static org.aniket.quick.mac.helper.Constants.GO_BACK_DESC;
import static org.aniket.quick.mac.helper.Constants.GO_BACK_PROMPT;
import static org.aniket.quick.mac.helper.Constants.OPTIONS;
import static org.aniket.quick.mac.helper.Constants.RUN_AGAIN_DESC;
import static org.aniket.quick.mac.helper.Constants.RUN_AGAIN_PROMPT;

import java.util.Arrays;
import java.util.List;
import org.aniket.quick.mac.helper.ClipboardHelper;
import org.aniket.quick.mac.helper.TimeHelper;
import org.jline.terminal.Terminal;
import org.springframework.core.io.ResourceLoader;
import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.style.TemplateExecutor;

public interface Command {

    void run() throws Exception;

    String getTable();

    Terminal getAbstractTerminal();

    ResourceLoader getAbstractResourceLoader();

    TemplateExecutor getAbstractTemplateExecutor();

    default SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> getDefaultContext() {
        final SelectorItem<String> runAgainItem = SelectorItem.of(RUN_AGAIN_PROMPT, RUN_AGAIN_DESC);
        final SelectorItem<String> copyClipItem = SelectorItem.of(COPY_PROMPT, COPY_DESC);
        final SelectorItem<String> goBackItem = SelectorItem.of(GO_BACK_PROMPT, GO_BACK_DESC);
        final List<SelectorItem<String>> items = Arrays.asList(runAgainItem, goBackItem, copyClipItem);
        final SingleItemSelector<String, SelectorItem<String>> component = new SingleItemSelector<>(getAbstractTerminal(),
                items, OPTIONS, null);
        component.setResourceLoader(getAbstractResourceLoader());
        component.setTemplateExecutor(getAbstractTemplateExecutor());
        return component.run(SingleItemSelector.SingleItemSelectorContext.empty());
    }

    default void postRun(final String result) throws Exception {
        switch (result) {
            case RUN_AGAIN_DESC:
                run();
                break;
            case COPY_DESC:
                final ClipboardHelper.Response clipResponse = ClipboardHelper.copyToClipboard(getTable());
                System.out.println(ClipboardHelper.getFooter(clipResponse));
            case GO_BACK_DESC:
                break;
        }
    }

    default void print() {
        System.out.println(TimeHelper.getTimeHeader());
        System.out.println(getTable());
    }
}

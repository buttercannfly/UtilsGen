import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.components.impl.stores.DirectoryStorageUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.WorkingDirectoryProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogPanel;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.psi.PsiFile;
import com.intellij.ui.components.JBList;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.SystemIndependent;
import ui.MainDialog;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

public class NewUtilAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
//        System.out.println(e.getData(CommonDataKeys.PSI_FILE));
        String path = e.getData(CommonDataKeys.VIRTUAL_FILE).getPath();
        Messages.InputDialog dialog = new Messages.InputDialog(null, "What's the function of your Util", null, "android write sdcard", null);
        dialog.show();
        if(dialog.isOK()){
            System.out.println(dialog.getTextField().getText());
            MainDialog mainDialog = new MainDialog(dialog.getTextField().getText(),e.getProject());
            mainDialog.show();
            if (mainDialog.isOK()){
                System.out.println("OK");
                try {
                    mainDialog.handleOK(path, e.getProject(), dialog.getTextField().getText());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

    }
}

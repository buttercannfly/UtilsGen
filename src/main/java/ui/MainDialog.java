package ui;
import com.alibaba.fastjson.JSONObject;
import com.intellij.codeInsight.documentation.DocumentationScrollPane;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.largeFilesEditor.editor.GlobalScrollBar;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.EditorSettingsProvider;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.colorpicker.ButtonPanel;
import com.intellij.ui.components.JBScrollBar;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.panels.OpaquePanel;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;
import entity.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.notebooks.editor.outputs.NotebookOutputDefaultScrollPane;
import org.jetbrains.plugins.notebooks.editor.outputs.NotebookOutputNonStickyScrollPane;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.AncestorListener;
import javax.swing.plaf.ButtonUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MainDialog extends DialogWrapper {
    private final int size = 3;
    private ArrayList<Item> list;
    private int wholeIndex;
    private String keyword;
    private int curPosition;
    private Project thisProject;
    public MainDialog(String text, @Nullable Project project){
        super(true);
        setTitle("Util Class Results ----> Choose One as Your Pleased Util");
        setOKButtonText("Choose This Util");
        list = new ArrayList<>();
        wholeIndex = 0;
        keyword = text;
        this.thisProject = project;
        curPosition = 0;
        setSize(1000,800);
        init();
    }


    @Override
    protected @Nullable JComponent createCenterPanel() {
        list = ESFunctions.getFromES(keyword,curPosition,10);
        curPosition = 10;
        JPanel dialogPanel = new JPanel(new BorderLayout());
        Dimension dimension = new Dimension(1000,800);
        dialogPanel.setPreferredSize(dimension);

        EditorTextField textField = new EditorTextField(new DocumentImpl(""), thisProject, JavaFileType.INSTANCE,true, false){
            @Override
            protected @NotNull EditorEx createEditor() {
                EditorEx editor = super.createEditor();
                editor.setHorizontalScrollbarVisible(true);
                editor.setVerticalScrollbarVisible(true);
                JScrollBar scrollBar = editor.getScrollPane().getVerticalScrollBar();
                int min = scrollBar.getMinimum();
                scrollBar.setValue(min);
                return editor;
            }
        };


//        JTextArea textField = new JTextArea();
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableModel.addColumn("Utils");
        for(Item item:list){
            tableModel.addRow(new String[]{item.getTitle()});
        }
        JBTable table = new JBTable(tableModel);
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoscrolls(true);


        table.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = table.getSelectedRow();
                Document document = new DocumentImpl(list.get(index).getCodes());
                textField.setDocument(document);
//                textField.setText(list.get(index).getCodes());
                wholeIndex = index;
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        JScrollPane pane = new JBScrollPane(table);
        JPanel leftPanel = new JPanel(new BorderLayout());
        JButton jButton = new JButton("More Utils...");
        jButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ArrayList<Item> tmpList = ESFunctions.getFromES(keyword,curPosition,size);
                curPosition+=size;
                for (Item item:tmpList){
                    list.add(item);
                    tableModel.addRow(new String[]{item.getTitle()});
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        leftPanel.add(pane, BorderLayout.CENTER);
        leftPanel.add(jButton, BorderLayout.SOUTH);
        dialogPanel.add(leftPanel, BorderLayout.WEST);
        dialogPanel.add(textField, BorderLayout.CENTER);
        table.setRowHeight(40);
        table.getColumnModel().getColumn(0).setPreferredWidth(200);
        return dialogPanel;
    }

    public void handleOK(String path, @Nullable Project project, String text) throws IOException {
        String filename = list.get(wholeIndex).getTitle();
        File dir = new File(path);
        File file = FileUtil.createTempFile(dir,filename,"");
        String codes = changePackage(list.get(wholeIndex).getCodes(), path);
        Files.write( Paths.get(path+File.separator+filename), codes.getBytes(StandardCharsets.UTF_16));
        LocalFileSystem.getInstance().refreshAndFindFileByPath(path+File.separator+filename);
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file);
        FileEditorManager.getInstance(project).openFile(virtualFile,true);
        ESFunctions.updateES(list.get(wholeIndex).getId(),text);
    }

    private String changePackage(String codes, String path) {
        System.out.println("path:"+path);
        int beginIndex = codes.indexOf("package ");
        int endIndex = -1;
        for(int j=beginIndex;j<codes.length();j++){
            if(codes.charAt(j)=='\n'){
                endIndex = j;
                break;
            }
        }
        if(endIndex==-1){
            endIndex = codes.length();
        }
        System.out.println("beginIndex:"+beginIndex+" endIndex:"+endIndex);
        if(path.endsWith("src")){
            String preCodes = codes.substring(0,beginIndex);
            String laterCodes = codes.substring(endIndex,codes.length());
            return preCodes+laterCodes;
        }
        int indexSrc = path.indexOf("src");
        path = path.substring(indexSrc+4);
        String nowPackName = path.replaceAll("/",".");
        String preCodes = codes.substring(0,beginIndex);
        String laterCodes = codes.substring(endIndex,codes.length());
        String resCodes = preCodes+"package "+ nowPackName+ ";" + laterCodes;
        return resCodes;
    }
}

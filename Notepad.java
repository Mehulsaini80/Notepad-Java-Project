import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.datatransfer.*;

public class Notepad implements ActionListener {

    Frame f;
    MenuBar mb;
    Menu m1, m2, m3, m4;
    MenuItem nw, opn, sve, sav, ext, ct, cpy, pst, fr, rep;
    CheckboxMenuItem bld, ite;

    TextArea a = new TextArea();
    private boolean isModified = false;
    private String currentFilePath = null;
    private String originalContent = "";

    public Notepad() {
        f = new Frame("Notepad");
        f.setSize(500, 500);
        f.setLayout(new BorderLayout());
        f.addWindowListener(new Windoww());

        bld = new CheckboxMenuItem("Bold");
        ite = new CheckboxMenuItem("Italic");
        ite.setState(false);

        mb = new MenuBar();
        m1 = new Menu("File");
        m2 = new Menu("Edit");
        m3 = new Menu("Others");
        m4 = new Menu("View");

        nw = new MenuItem("New");
        opn = new MenuItem("Open");
        sve = new MenuItem("Save");
        sav = new MenuItem("Save As");
        ext = new MenuItem("Exit");
        fr = new MenuItem("Find");
        rep = new MenuItem("Find & Replace");
        ct = new MenuItem("Cut");
        cpy = new MenuItem("Copy");
        pst = new MenuItem("Paste");

        nw.addActionListener(this);
        opn.addActionListener(this);
        sve.addActionListener(this);
        sav.addActionListener(this);
        ext.addActionListener(this);
        fr.addActionListener(this);
        rep.addActionListener(this);
        ct.addActionListener(this);
        cpy.addActionListener(this);
        pst.addActionListener(this);

        m1.add(nw);
        m1.add(opn);
        m1.add(sve);
        m1.add(sav);
        m1.addSeparator();
        m1.add(ext);

        m3.add(ct);
        m3.add(cpy);
        m3.add(pst);

        m2.add(fr);
        m2.add(rep);
        m2.addSeparator();
        m2.add(m3);

        m4.add(bld);
        m4.add(ite);

        mb.add(m1);
        mb.add(m2);
        mb.add(m4);

        f.setMenuBar(mb);
        f.add(a, BorderLayout.CENTER);

        a.addTextListener(new TextListener() {
            public void textValueChanged(TextEvent e) {
                isModified = !a.getText().equals(originalContent);
            }
        });

        f.setVisible(true);
    }

    

    public void actionPerformed(ActionEvent e) {
        String str = e.getActionCommand();

        switch (str) {
            case "New":
                newfile();
                break;

            case "Open":
                openFile();
                break;
            case "Save":
                saveFile();
                break;
            case "Save As":
                saveAsFile();
                break;
            case "Exit":
                if (!isModified) {
                    System.exit(0); 
                } else if (a.getText().trim().isEmpty()) {
                    System.exit(0);
                } else {
                    ConfirmDialogHandler exitHandler = new ConfirmDialogHandler();
                    exitHandler.showConfirmDialog(f, "Do you want to save the file before exit?");
                    int exitResponse = exitHandler.getResponse();
                    if (exitResponse == 0) { 
                        saveAsFile();
                        System.exit(0);
                    } else if (exitResponse == 1) { 
                        System.exit(0);
                    } else {
                        System.out.println("Exit canceled by the user.");
                    }
                }
                break;

            case "Cut":
                cut();
                break;
            case "Copy":
                copy();
                break;
            case "Paste":
                paste();
                break;
            case "Find":
                findDialog();
                break;
            case "Find & Replace":
                showFindReplaceDialog();
                break;

        }
    }

    public void showFindReplaceDialog() {
        Dialog findReplaceDialog = new Dialog(f, "Find & Replace", false);
        findReplaceDialog.setLayout(new GridBagLayout());
        GridBagConstraints gb = new GridBagConstraints();
        gb.insets = new Insets(5, 5, 5, 5);

        Label findLabel = new Label("Find:");
        TextField findField = new TextField(20);
        Label replaceLabel = new Label("Replace with:");
        TextField replaceField = new TextField(20);
        Button findButton = new Button("Find");
        Button replaceButton = new Button("Replace");
        Button replaceAllButton = new Button("Replace All");
        Button closeButton = new Button("Close");

        gb.gridx = 0;
        gb.gridy = 0;
        gb.anchor = GridBagConstraints.WEST;
        findReplaceDialog.add(findLabel, gb);

        gb.gridx = 1;
        gb.gridy = 0;
        gb.anchor = GridBagConstraints.EAST;
        findReplaceDialog.add(findField, gb);

        gb.gridx = 0;
        gb.gridy = 1;
        gb.anchor = GridBagConstraints.WEST;
        findReplaceDialog.add(replaceLabel, gb);

        gb.gridx = 1;
        gb.gridy = 1;
        gb.anchor = GridBagConstraints.EAST;
        findReplaceDialog.add(replaceField, gb);

        gb.gridx = 0;
        gb.gridy = 2;
        gb.gridwidth = 1;
        gb.anchor = GridBagConstraints.CENTER;
        findReplaceDialog.add(findButton, gb);

        gb.gridx = 1;
        gb.gridy = 2;
        gb.anchor = GridBagConstraints.CENTER;
        findReplaceDialog.add(replaceButton, gb);

        gb.gridx = 0;
        gb.gridy = 3;
        findReplaceDialog.add(replaceAllButton, gb);

        gb.gridx = 1;
        gb.gridy = 3;
        findReplaceDialog.add(closeButton, gb);

        findButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String searchText = findField.getText();
                if (!searchText.isEmpty()) {
                    find(searchText);
                    a.requestFocus();
                } else {
                    System.out.println("Find field is empty!");
                }
            }
        });

        replaceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String searchText = findField.getText();
                String replaceText = replaceField.getText();
                replaceFirst(searchText, replaceText);
            }
        });

        replaceAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String searchText = findField.getText();
                String replaceText = replaceField.getText();
                replaceAll(searchText, replaceText);
            }
        });

        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                findReplaceDialog.dispose();
            }
        });

        findReplaceDialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                findReplaceDialog.dispose();
            }
        });

        findReplaceDialog.setSize(300, 200);
        findReplaceDialog.setVisible(true);
    }

    private int currentIndex = 0;

    public void find(String searchText) {
        String content = a.getText();

        if (searchText == null || searchText.isEmpty()) {
            System.out.println("Search text is empty!");
            return;
        }

        int index = content.indexOf(searchText, currentIndex);

        if (index != -1) {
            a.select(index, index + searchText.length());
            currentIndex = index + searchText.length();

        } else {
            System.out.println("End of file reached !");

            currentIndex = 0;
            a.select(0, 0);
        }
    }

    public void findDialog() {
        Dialog d = new Dialog(f, "Find", false);
        d.setLayout(new FlowLayout());

        Label label = new Label("Find:");
        TextField tf = new TextField(20);
        Button find = new Button("Find");

        find.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String text = tf.getText();
                find(text);
                a.requestFocus();
            }
        });

        d.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                d.dispose();
            }
        });

        d.add(label);
        d.add(tf);
        d.add(find);
        d.setSize(300, 100);
        d.setVisible(true);
    }

    public void replaceFirst(String searchText, String replaceText) {
        String content = a.getText();
        String regex = Pattern.quote(searchText);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            content = matcher.replaceFirst(Matcher.quoteReplacement(replaceText));
            a.setText(content);
            a.select(matcher.start(), matcher.start() + replaceText.length());
        } else {
            System.out.println("Text not found!");
        }
    }

    public void replaceAll(String searchText, String replaceText) {
        String content = a.getText();
        String regex = Pattern.quote(searchText);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            content = matcher.replaceAll(Matcher.quoteReplacement(replaceText));
            a.setText(content);
        } else {
            System.out.println("Text not found!");
        }
    }

    public class ConfirmDialogHandler {
        private int response;

        public ConfirmDialogHandler() {
            this.response = -1;
        }

        public int getResponse() {
            return response;
        }

        public void showConfirmDialog(Frame f, String messageText) {
            Dialog confirmDialog = new Dialog(f, "Confirm", true);
            confirmDialog.setLayout(new BorderLayout());
            confirmDialog.setSize(300, 170);

            Label message = new Label(messageText, Label.CENTER);

            Panel buttonPanel = new Panel(new GridLayout(1, 3, 10, 0));
            Button yes = new Button("Yes");
            Button no = new Button("No");
            Button cancel = new Button("Cancel");

            yes.addActionListener(new ConfirmButtonHandler(confirmDialog, 0, this));
            no.addActionListener(new ConfirmButtonHandler(confirmDialog, 1, this));
            cancel.addActionListener(new ConfirmButtonHandler(confirmDialog, -1, this));

            buttonPanel.add(yes);
            buttonPanel.add(no);
            buttonPanel.add(cancel);

            Panel bottomWrapper = new Panel(new BorderLayout());
            bottomWrapper.add(buttonPanel, BorderLayout.NORTH);
            bottomWrapper.add(new Label(" "), BorderLayout.SOUTH);

            confirmDialog.add(message, BorderLayout.CENTER);
            confirmDialog.add(bottomWrapper, BorderLayout.SOUTH);

            confirmDialog.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    setResponse(-1);
                    confirmDialog.dispose();
                }
            });

            confirmDialog.setVisible(true);
        }

        public void setResponse(int response) {
            this.response = response;
        }
    }

    class ConfirmButtonHandler implements ActionListener {
        private Dialog dialog;
        private int responseValue;
        private ConfirmDialogHandler dialogHandler;

        public ConfirmButtonHandler(Dialog dialog, int responseValue, ConfirmDialogHandler dialogHandler) {
            this.dialog = dialog;
            this.responseValue = responseValue;
            this.dialogHandler = dialogHandler;
        }

        public void actionPerformed(ActionEvent e) {
            dialogHandler.setResponse(responseValue);
            dialog.setVisible(false);
        }
    }

    public void newfile() {
        if (isModified) {
            ConfirmDialogHandler confirmHandler = new ConfirmDialogHandler();
            confirmHandler.showConfirmDialog(f, "Do you want to save this file?");
            int response = confirmHandler.getResponse();

            if (response == 0) {
                saveAsFile();
            } else if (response == 1) {
                createNewFile();
            } else {
                System.out.println("New file creation canceled.");
            }
        } else {
            createNewFile();
        }
    }

    private void createNewFile() {
        a.setText("");
        currentFilePath = null;
        originalContent = "";
        isModified = false;
        System.out.println("New file created.");
    }

    public void openFile() {
        if (isModified) {
            ConfirmDialogHandler confirmHandler = new ConfirmDialogHandler();
            confirmHandler.showConfirmDialog(f, "Do you want to save this file?");

            int response = confirmHandler.getResponse();

            if (response == 0) {
                saveFile();
            } else if (response == 1) {
                System.out.println("Changes discarded.");
            } else if (response == -1) {
                System.out.println("Open file operation canceled.");
                return;
            }
        }

        FileDialog openDialog = new FileDialog(f, "Open File", FileDialog.LOAD);
        openDialog.setVisible(true);

        String directory = openDialog.getDirectory();
        String fileName = openDialog.getFile();

        if (directory != null && fileName != null) {
            currentFilePath = directory + fileName;
            try (BufferedReader br = new BufferedReader(new FileReader(currentFilePath))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                a.setText(sb.toString());
                originalContent = sb.toString();
                isModified = false;
                System.out.println("File opened successfully.");
            } catch (IOException ex) {
                System.out.println("Error opening file: " + ex.getMessage());
            }
        } else {
            System.out.println("Open operation canceled.");
        }
    }

    public void saveFile() {
        if (currentFilePath == null || currentFilePath.equals("null") || currentFilePath.isEmpty()) {
            FileDialog saveDialog = new FileDialog(f, "Save As", FileDialog.SAVE);
            saveDialog.setVisible(true);

            String directory = saveDialog.getDirectory();
            String fileName = saveDialog.getFile();

            if (directory != null && fileName != null) {
                currentFilePath = directory + fileName;
            } else {
                System.out.println("Save operation canceled.");
                return;
            }
        }

        try (FileWriter fw = new FileWriter(currentFilePath)) {
            fw.write(a.getText());
            originalContent = a.getText();
            isModified = false;
            System.out.println("File saved successfully!");
        } catch (IOException ex) {
            System.out.println("Error saving file: " + ex.getMessage());
        }
    }

    public void saveAsFile() {
        FileDialog saveDialog = new FileDialog(f, "Save As", FileDialog.SAVE);
        saveDialog.setVisible(true);

        String directory = saveDialog.getDirectory();
        String filename = saveDialog.getFile();

        if (directory != null && filename != null) {
            try (FileWriter writer = new FileWriter(directory + filename)) {
                writer.write(a.getText());
                currentFilePath = directory + filename;
                originalContent = a.getText();
                isModified = false;
                System.out.println("File saved successfully: " + currentFilePath);
            } catch (IOException e) {
                System.out.println("Error saving file: " + e.getMessage());
            }
        } else {
            System.out.println("Save operation canceled.");
        }
    }

    public void cut() {
        String selectedText = a.getSelectedText();
        if (selectedText != null) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(selectedText), null);
            a.replaceRange("", a.getSelectionStart(), a.getSelectionEnd());
        }
    }

    public void copy() {
        String selectedText = a.getSelectedText();
        if (selectedText != null) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(selectedText), null);
        }
    }

    public void paste() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                String pasteText = (String) clipboard.getData(DataFlavor.stringFlavor);
                a.replaceRange(pasteText, a.getSelectionStart(), a.getSelectionEnd());
            }
        } catch (Exception ex) {
            System.out.println("Error pasting text: " + ex);
        }
    }

    public static void main(String[] args) {
        new Notepad();
    }
}

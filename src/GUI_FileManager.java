import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.*;
import java.util.List;

public class GUI_FileManager extends JFrame {

    /*
    Для реализации используем Swing
     */
    private JPanel catalogPanel = new JPanel();
    private JList filesList = new JList();
    private JScrollPane scrollPane = new JScrollPane(filesList);
    private JPanel buttonsPane = new JPanel();
    private JButton createButton = new JButton("Create");
    private JButton deleteButton = new JButton("Delete");
    private JButton backButton = new JButton("Back");
    private JButton renameButton = new JButton("Rename");
    private ArrayList<String> dirCache = new ArrayList<>();

    public GUI_FileManager() {
        super("FileManager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        catalogPanel.setLayout(new BorderLayout(5, 5));
        catalogPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        buttonsPane.setLayout(new GridLayout(1, 4, 5, 5));
        JDialog createNewDirDialog = new JDialog(GUI_FileManager.this, "Create directory", true);
        JPanel createNeDirPanel = new JPanel();
        createNewDirDialog.add(createNeDirPanel);
        File[] discs = File.listRoots();// возвращает список всех корневых элементов (дисков С, Д, и т.п.)

        scrollPane.setPreferredSize(new Dimension(400, 500));
        filesList.setListData(discs);// добавляем диски по умолчанию
        filesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); // можно выбирать несколько файлов

        filesList.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { //проверка на двойное нажатие
                    DefaultListModel model = new DefaultListModel();// модель из МВС, которая отвечает за работу с данными, входящими
                    // в JList. Используем данный объект для динамической работы с файлами.
                    String selectedObject = filesList.getSelectedValue().toString(); // добавляем сюда адрес выделенного элемента
                    String fullPath = toFullPath(dirCache); // возвращаем полный путь места, в котором сейчас находимся (хлебные крошки)
                    File selectedFile;
                /*
                Проверяем, где мы находимся.
                Если мы не на самом верху, то мы создаем объект выбранного файла и передаем ему путь из хлебных крошен
                и выбранного файла.
                Если в самом начале пути, то просто переходим в выбранный диск

                 */
                    if (dirCache.size() > 1) {
                        selectedFile = new File(fullPath, selectedObject);
                    } else {
                        selectedFile = new File(fullPath + selectedObject);
                    }
                /*
                Далее проверям, перешли ли мы в файл или в директорию
                 */
                    if (selectedFile.isDirectory()) {
                        String[] rootStr = selectedFile.list();
                        for (String str : rootStr) {
                            File checkObject = new File(selectedFile.getPath(), str);
                            if (!checkObject.isHidden()) {
                                model.addElement(str);
                            } else {
                                model.addElement("file-" + str);
                            }
                        }
                    }
                /*
                Добавляем в кэш объект
                и добавляем в модель выбранный элемент
                 */
                    dirCache.add(selectedObject);
                    filesList.setModel(model);
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
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (dirCache.size() > 1) {
                    dirCache.remove(dirCache.size() - 1);
                    String backDir = toFullPath(dirCache);
                    String[] objects = new File(backDir).list();
                    DefaultListModel backRootModel = new DefaultListModel();
                    for (String s : objects) {
                        File checkFile = new File(backDir, s);
                        if (checkFile.isHidden()) {
                            if (checkFile.isDirectory()) {
                                backRootModel.addElement(s);
                            } else {
                                backRootModel.addElement("file-" + s);
                            }
                        }
                    }
                    filesList.setModel(backRootModel);
                } else {
                    dirCache.removeAll(dirCache);
                    filesList.setListData(discs);
                }


            }
        });
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!dirCache.isEmpty()) {
                    String currentPath;
                    File newFolder;
                    //диалоговое окно для создания нового каталога
                    CreateNewFolderJDialog newFolderJDialog = new CreateNewFolderJDialog(GUI_FileManager.this);

                    if (newFolderJDialog.getReady()) { // проверяем готовность создания нового каталога
                        currentPath = toFullPath(dirCache);
                        newFolder = new File(currentPath, newFolderJDialog.getNewName());
                        if (!newFolder.exists()) {
                            newFolder.mkdir();
                        }

                        File updateDir = new File(currentPath); // Обновление объекта файл после того, как мы создали новый каталог
                        String updateMas[] = updateDir.list();
                        DefaultListModel updateModel = new DefaultListModel();
                        for (String str : updateMas) {
                            File check = new File(updateDir.getPath(), str);
                            if (!check.isHidden()) {
                                if (check.isDirectory()) {
                                    updateModel.addElement(str);
                                } else {
                                    updateModel.addElement(str);
                                }
                            }
                        }
                        filesList.setModel(updateModel);
                    }
                }
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedObject = filesList.getSelectedValue().toString();
                String currentPath = toFullPath(dirCache);
                if (!selectedObject.isEmpty()) {

                    deleteDir(new File(currentPath, selectedObject)); // Небезопасное удаление каталога. Т.е. удаляет все, что там внутри.
                    // не удаляет их в козину!!!!!!

                    File updateDir = new File(currentPath);
                    String updateMas[] = updateDir.list();
                    DefaultListModel updateModel = new DefaultListModel();

                    for (String str : updateMas) {
                        File check = new File(updateDir.getPath(), str);
                        if (!check.isHidden()) {
                            if (check.isDirectory()) {
                                updateModel.addElement(str);
                            } else {
                                updateModel.addElement(str);
                            }
                        }
                    }
                    filesList.setModel(updateModel);
                }
            }
        });
        renameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!dirCache.isEmpty() & filesList.getSelectedValue() != null){

                    String currentPath = toFullPath(dirCache);
                    String selectedObject = filesList.getSelectedValue().toString();
                    RenameJDialog renamer = new RenameJDialog(GUI_FileManager.this);
                    if(renamer.getReady()){
                        File renameFile = new File(currentPath, selectedObject);
                        renameFile.renameTo(new File(currentPath, renamer.getNewName()));

                        File updateDir = new File(currentPath);
                        String updateMas[] = updateDir.list();
                        DefaultListModel updateModel = new DefaultListModel();
                        for (String str : updateMas) {
                            File check = new File (updateDir.getPath(), str);
                            if(!check.isHidden()){
                                if(check.isDirectory()){
                                    updateModel.addElement(str);
                                }else{
                                    updateModel.addElement("файл-" + str);
                                }
                            }
                        }
                        filesList.setModel(updateModel);
                    }
                }
            }
        });


        buttonsPane.add(backButton);
        buttonsPane.add(createButton);
        buttonsPane.add(renameButton);
        buttonsPane.add(deleteButton);

        catalogPanel.add(scrollPane, BorderLayout.CENTER);
        catalogPanel.add(buttonsPane, BorderLayout.SOUTH);


        getContentPane().add(catalogPanel);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private String toFullPath(List<String> list) {
        String listPart = "";
        for (String s : list) {
            listPart = listPart + s;
        }
        return listPart;
    }

    private void deleteDir(File file) {
        File[] odjects = file.listFiles();
        if (odjects != null) {
            for (File f : odjects) {
                deleteDir(f);
            }
        }
        file.delete();
    }

}

package com.practice.Gui;

import javax.swing.*;
import javax.swing.border.LineBorder;

import com.google.gson.Gson;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class Main extends JFrame {

    private Scene leftPanel;
    private Scene rightPanel;
    private JLabel statusLabel;
    private final String[] alphabet = new String[]{ "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };
    private int counter, s_count;
    private JButton addVertex_btn , addRib_btn , delete_btn, clear_btn, back_btn, runStop_btn, forward_btn;

    enum Option {
        NONE,
        CREATE,
        CONNECT,
        DELETE,
        CLEAR,
        RUN,
        STOP,
        NEXT,
        PREV
    }

    Option currentOption = Option.NONE;

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        InitUI();
        this.pack();
        this.setVisible(true);


        Gson gson = new Gson();
        
    }


    private void InitUI() {

        // получить размер экрана
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int W = (int)screenSize.getWidth(); // получить ширину экрана
        int H = (int)screenSize.getHeight(); // получить высоту экрана



        setMinimumSize(new Dimension(W/2, H/2));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout( mainPanel, BoxLayout.X_AXIS));
        mainPanel.add(createLeftPanel());
        mainPanel.add(createRightPanel());

        add( mainPanel );
        add(createToolBar(), BorderLayout.WEST);
        add(createStatusPanel(), BorderLayout.SOUTH);
        setJMenuBar(createMenuBar());
    }



    public void updateStatus(String msg) {
        String offset = "        ";
        statusLabel.setText(offset+msg);
    }

    public String getName() {
        if(counter > 25) {
            counter = 0;
            s_count++;
        }
        if( s_count > 0 ) {
            return alphabet[counter++]+s_count;
        }
        return  alphabet[counter++];
    }


    private Scene createLeftPanel() {
        leftPanel = new Scene();
        leftPanel.addMouseListener( new MouseAdapter() {
            @Override
            public void mouseClicked( MouseEvent mouseEvent ) {
                if(currentOption == Option.CREATE) {
                    // add after full
                    String name = getName();
                    Vertex vertex = new Vertex(name, mouseEvent.getX(), mouseEvent.getY(), Color.lightGray );
                    Vertex clone =  new Vertex(name, mouseEvent.getX(), mouseEvent.getY(), Color.lightGray );

                    vertex.addMouseListener( new MouseAdapter() {

                        @Override
                        public void mouseClicked( MouseEvent e ) {
                            super.mouseClicked( e );

                            if( currentOption == Main.Option.DELETE ) {
					
                                ArrayList<Rib> ribs = leftPanel.getRibList();
                                for(int i = ribs.size()-1; i >= 0; i--) {
                                    if( ribs.get(i).isConnect( vertex ) ) {
                                        ribs.remove(i);
                                    }	
                                }
                                leftPanel.remove(vertex);
                                rightPanel.remove(clone);
                                leftPanel.revalidate();
                                leftPanel.repaint();
                                rightPanel.revalidate();
                                rightPanel.repaint();
                            }
                        }

                        @Override
                        public void mousePressed( MouseEvent mouseEvent ) {
                            super.mousePressed( mouseEvent );
                                vertex.setColour( Color.magenta );
                        }
                        @Override
                        public void mouseReleased( MouseEvent mouseEvent ) {
                            super.mouseReleased( mouseEvent );
                            vertex.setColour( Color.lightGray);
                        }
                    } );

                    clone.addMouseMotionListener(new MouseAdapter() {
	
                        @Override
                        public void mouseDragged( MouseEvent mouseEvent ) {
                            vertex.setLocation(clone.getLocation());
                            revalidate();
                            repaint();
                        }
                    });
                    vertex.addMouseMotionListener(new MouseAdapter() {
                        @Override
                        public void mouseDragged( MouseEvent mouseEvent ) {
                            clone.setLocation(vertex.getLocation());
                            revalidate();
                            repaint();
                        }
                    });                    

                    leftPanel.addVertex( vertex );
                    rightPanel.addVertex( clone );
                }
            }
        } );
        return leftPanel;
    }

    private Scene createRightPanel() {
        rightPanel = new Scene();
        rightPanel.setBackground(Color.white);
        rightPanel.setBorder(new LineBorder(new Color(232, 232, 232)));
        return rightPanel;
    }

    private JPanel createStatusPanel() {

        JPanel statusPanel = new JPanel();
        statusPanel.setPreferredSize(new Dimension(getWidth(), 42));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        String offset = "        ";
        statusLabel = new JLabel(offset+"some comment");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(statusLabel);

        return statusPanel;
    }


    // создать меню файла
    private JMenu createFileMenu()
    {
        // Создание выпадающего меню
        JMenu fileMenu = new JMenu("Файл");

        // Пункт меню "Читать из файла"
        JMenuItem read = new JMenuItem("Читать из файла");

        // добавить действие Обработка
        read.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int option = fileChooser.showOpenDialog(getContentPane());
            if(option == JFileChooser.APPROVE_OPTION){
                File file1 = fileChooser.getSelectedFile();
                statusLabel.setText("Folder Selected: " + file1.getAbsolutePath() );
            }else{
                statusLabel.setText("Open command canceled");
            }
        });

        // Пункт меню "Сохранить"
        JMenuItem save = new JMenuItem("Сохранить");

        // добавить обработчик действия для кнопки "Сохранить"
        save.addActionListener( actionEvent -> {
            // ...
        });

        // Пункт меню "Сохранить как"
        JMenuItem save_as = new JMenuItem("Сохранить как");

        // добавить обработчик действия для кнопки "Сохранить как"
        save_as.addActionListener( actionEvent -> {

            String filename = JOptionPane.showInputDialog("Name this file");
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(filename));
            fileChooser.showSaveDialog(fileChooser);
            BufferedWriter writer;
            int option = fileChooser.showSaveDialog(null);
            if(option == JFileChooser.APPROVE_OPTION){
                try {
                    writer = new BufferedWriter(new FileWriter(fileChooser.getSelectedFile()));
                    writer.close();
                    JOptionPane.showMessageDialog(null, "File has been saved","File Saved",JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                statusLabel.setText("Open command canceled");
            }
        });


        // Пункт меню из команды с выходом из программы
        JMenuItem exit = new JMenuItem("Выход");

        // добавить обработчик действия для кнопки "Выход"
        exit.addActionListener( actionEvent -> System.exit(0) );

        fileMenu.add(read);             // Добавление в меню пункт "Читать из файла"
        fileMenu.add(save);             // Добавление в меню пункт "Сохранить"
        fileMenu.add(save_as);          // Добавление в меню пункт "Сохранить как"
        fileMenu.addSeparator();        // Добавление разделителя
        fileMenu.add(exit);             // Добавление в меню пункт "Выход"

        return fileMenu;
    }

    // создать строку меню
    private JMenuBar createMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createInfoMenu());

        return menuBar;
    }

    // создать информационное меню
    private JMenu createInfoMenu()
    {
        JMenu infoMenu = new JMenu("Информация");
        JMenuItem doc = new JMenuItem("Документация");
        JMenuItem aboutUs = new JMenuItem("О нас!");
        infoMenu.add(doc);

        doc.addActionListener(actionEvent -> new Documentation() );

        infoMenu.add(aboutUs);

        aboutUs.addActionListener(actionEvent -> new AboutUs() );
        return infoMenu;
    }

    public void ChangeCurrentOption( Option option ) {

        switch( option ) {
            case CONNECT:

                addRib_btn.setBackground( new Color( 250, 100, 100 ));
                addVertex_btn.setBackground(Color.WHITE);    
                delete_btn.setBackground(Color.WHITE);       
                clear_btn.setBackground(Color.WHITE);        
                back_btn.setBackground(Color.WHITE);         
                runStop_btn.setBackground(Color.WHITE);      
                forward_btn.setBackground(Color.WHITE); 
                
                break;
            case CLEAR:
                addRib_btn.setBackground( Color.WHITE);
                addVertex_btn.setBackground(Color.WHITE);    
                delete_btn.setBackground(Color.WHITE);       
                clear_btn.setBackground(new Color( 250, 100, 100 ));      
                back_btn.setBackground(Color.WHITE);         
                runStop_btn.setBackground(Color.WHITE);      
                forward_btn.setBackground(Color.WHITE); 
            break;
            case CREATE:
                addRib_btn.setBackground( Color.WHITE);
                addVertex_btn.setBackground(new Color( 250, 100, 100 ));    
                delete_btn.setBackground(Color.WHITE);       
                clear_btn.setBackground(Color.WHITE);        
                back_btn.setBackground(Color.WHITE);         
                runStop_btn.setBackground(Color.WHITE);      
                forward_btn.setBackground(Color.WHITE); 
                break;
            case RUN:
                addRib_btn.setBackground( Color.WHITE );
                runStop_btn.setBackground( new Color( 250, 100, 100 ));
                addVertex_btn.setBackground(Color.WHITE);    
                delete_btn.setBackground(Color.WHITE);       
                clear_btn.setBackground(Color.WHITE);        
                back_btn.setBackground(Color.WHITE);         
                forward_btn.setBackground(Color.WHITE); 
                break;
            case NEXT:
                addRib_btn.setBackground( Color.WHITE );
                addVertex_btn.setBackground(Color.WHITE);    
                delete_btn.setBackground(Color.WHITE);       
                clear_btn.setBackground(Color.WHITE);        
                back_btn.setBackground(Color.WHITE);         
                runStop_btn.setBackground(Color.WHITE);      
                forward_btn.setBackground(new Color( 250, 100, 100 )); 
                break;
            case PREV:

                addRib_btn.setBackground( Color.WHITE );
                addVertex_btn.setBackground(Color.WHITE);    
                delete_btn.setBackground(Color.WHITE);       
                clear_btn.setBackground(Color.WHITE);        
                runStop_btn.setBackground(Color.WHITE);      
                forward_btn.setBackground(Color.WHITE); 
                back_btn.setBackground(new Color( 250, 100, 100 ));         

                break;
            case STOP:
                addRib_btn.setBackground( Color.WHITE );
                addVertex_btn.setBackground(Color.WHITE);    
                delete_btn.setBackground(Color.WHITE);       
                clear_btn.setBackground(Color.WHITE);        
                back_btn.setBackground(Color.WHITE);         
                runStop_btn.setBackground(Color.WHITE);      
                forward_btn.setBackground(Color.WHITE); 
                runStop_btn.setBackground(new Color( 250, 100, 100 ));      

                break;
            case DELETE:
                addRib_btn.setBackground( Color.WHITE );
                addVertex_btn.setBackground(Color.WHITE);    
                clear_btn.setBackground(Color.WHITE);        
                back_btn.setBackground(Color.WHITE);         
                runStop_btn.setBackground(Color.WHITE);      
                forward_btn.setBackground(Color.WHITE); 
                delete_btn.setBackground(new Color( 250, 100, 100 ));       
                            
                break;
            default:
            addRib_btn.setBackground( Color.WHITE );
            addVertex_btn.setBackground(Color.WHITE);    
            clear_btn.setBackground(Color.WHITE);        
            back_btn.setBackground(Color.WHITE);         
            runStop_btn.setBackground(Color.WHITE);      
            forward_btn.setBackground(Color.WHITE); 
            delete_btn.setBackground(Color.WHITE );    
        }

        currentOption = option;
    }

    // создать панель инструментов
    private JToolBar createToolBar()
    {
        JToolBar toolBar = new JToolBar(1);

        addVertex_btn = new JButton(new ImageIcon("resources/vertex.png"));
        addRib_btn = new JButton(new ImageIcon("resources/rib.png"));
        delete_btn = new JButton(new ImageIcon("resources/delete.png"));
        clear_btn = new JButton(new ImageIcon("resources/trash.png"));
        back_btn = new JButton(new ImageIcon("resources/back.png"));
        runStop_btn = new JButton(new ImageIcon("resources/run.png"));
        forward_btn = new JButton(new ImageIcon("resources/forward.png"));

        addVertex_btn.addActionListener( actionEvent -> {
			
            if(currentOption == Option.CREATE){
                ChangeCurrentOption(Option.NONE);
                addVertex_btn.setBackground(Color.WHITE);
            } else {
                updateStatus( "Vertices Adding...");
                ChangeCurrentOption(Option.CREATE);
            }
            leftPanel.setCurrentOption( currentOption );
        });
        addRib_btn.addActionListener( actionEvent -> {
            if(currentOption == Option.CONNECT){
                ChangeCurrentOption(Option.NONE);
            
            } 
            else {
                updateStatus( "Ribs Adding...");
                ChangeCurrentOption(Option.CONNECT);
            }
            leftPanel.setCurrentOption( currentOption );
        });
        delete_btn.addActionListener( actionEvent -> {
            if(currentOption == Option.DELETE){
                ChangeCurrentOption(Option.NONE);
                
            } else {

                updateStatus( "Deleting...");
                ChangeCurrentOption(Option.DELETE);
            }
            leftPanel.setCurrentOption( currentOption );
        } );

        clear_btn.addActionListener( actionEvent -> {
                ChangeCurrentOption(Option.NONE);
                
                leftPanel.clear();
                rightPanel.clear();
                updateStatus( "Clear...");
                revalidate();
                repaint();
          });

        back_btn.addActionListener( actionEvent -> {
            if( currentOption == Option.PREV ){

                ChangeCurrentOption(Option.NONE);
            }else {
                
                updateStatus( "Prev...");
                ChangeCurrentOption(Option.PREV);
            }
        });
        forward_btn.addActionListener( actionEvent -> {
            if( currentOption == Option.NEXT ){

                ChangeCurrentOption(Option.NONE);
            }else {
                
                rightPanel.addRib( leftPanel.getRibList().get(0));
                updateStatus( "Next...");
                ChangeCurrentOption(Option.NEXT);
            }
        });

        runStop_btn.addActionListener( actionEvent -> {
            if( currentOption == Option.RUN || currentOption == Option.STOP  ){

                ChangeCurrentOption(Option.NONE);
            }else {
                updateStatus( "Run...");
                ChangeCurrentOption( Option.RUN );
            }
        });
        toolBar.add(addVertex_btn);
        toolBar.add(addRib_btn);
        toolBar.add(delete_btn);
        toolBar.add(clear_btn);
        toolBar.add(back_btn);
        toolBar.add(runStop_btn);
        toolBar.add(forward_btn);

        return toolBar;
    }
}
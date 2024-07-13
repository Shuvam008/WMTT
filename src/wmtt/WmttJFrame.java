/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmtt;
import com.fazecast.jSerialComm.SerialPort;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.Arrays;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
/**
 *
 * @author shuvam
 */
public class WmttJFrame extends javax.swing.JFrame {

    /**
     * Creates new form WmttJFrame
     */
    
    
    public Printer printerObj;
    public OutputStream outputStream;
    public InputStream inputStreamObj;
    public PrintWriter writer;
    private int numberOfPassenger;
    private String stationName;
    private final int stationFare=15;
    private String currentSelectedStation;
    private String qrTicketStartNo = "A00000000";
    private Properties properties;
    private String configPrinterPort;
    private JToggleButton selectedStationButton;
            
    private String getConfigProperty(String key) {
        return properties.getProperty(key);
    }
    
    private static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
     
    private void ReadConfigReader() {
        properties = new Properties();
        String currentDirectory = System.getProperty("user.dir");
        String configFilePath = currentDirectory + File.separator +"resources"+File.separator+ "config.properties";
        try (InputStream input = new FileInputStream(configFilePath)) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            // Load the properties file
            properties.load(input);
        } catch (Exception ex) {
            System.out.println("Sorry, unable to find config.properties"+ ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public WmttJFrame() {
        initComponents();
        configPrinterPort="";
        currentSelectedStation="";
        onePassengerToggleButton.setSelected(true);
        numberOfPassenger = 1;
        selectedStationButton=null;
        ReadConfigReader();
        System.out.println("WmttJFrame App Name: " + getConfigProperty("app.name"));
        System.out.println("WmttJFrame App Version: " + getConfigProperty("app.version"));
        System.out.println("WmttJFrame App Author: " + getConfigProperty("app.author"));
        System.out.println("WmttJFrame App Author: " + getConfigProperty("app.comport"));
        if( !isNullOrEmpty(getConfigProperty("app.comport") ) ){
            configPrinterPort = getConfigProperty("app.comport");
            System.out.println("WmttJFrame configPrinterPort: " + configPrinterPort);
        }else{
            configPrinterPort="";
        }
        stationName = "";
        this.setResizable(false);
        printerObj = new Printer();
        outputStream = null;
        inputStreamObj = null;
        writer=null;
        // Get current directory
        String currentDirectory = System.getProperty("user.dir");
        // Construct path to image file
        String railIconImagePath = currentDirectory + File.separator +"assets"+File.separator+ "ir.png";
        String qrcodeIconImagePath = currentDirectory+File.separator +"assets"+File.separator + "QRscanCode.png";
        ImageIcon imageIcon = new ImageIcon(railIconImagePath);
        ImageIcon imageIcon2 = new ImageIcon(qrcodeIconImagePath);
        Image originalImage = imageIcon.getImage();
        Image resizedImage = originalImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(resizedImage);
        Image originalImage1 = imageIcon2.getImage();
        Image resizedImage1 = originalImage1.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon1 = new ImageIcon(resizedImage1);
        LogojLabel.setIcon(resizedIcon);
        QRjLabel.setIcon(resizedIcon1);
        // Create a ButtonGroup of passenger
        ButtonGroup passengerButtonGroup = new ButtonGroup();
        passengerButtonGroup.add(onePassengerToggleButton);
        passengerButtonGroup.add(twoPassengerToggleButton);
        passengerButtonGroup.add(threePassengerToggleButton);
        passengerButtonGroup.add(fourPassengerToggleButton);
        // Add Common ActionListener to each button of passenger group
        ActionListener passengerButtonActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JToggleButton selectedButton = (JToggleButton) e.getSource();
                if (selectedButton.isSelected()) {
                    // Enable other buttons
                    System.out.println("No of Passenger selected >>>"+selectedButton.getText());
                    numberOfPassenger = Integer.parseInt(selectedButton.getText());
                    int currentFare = Integer.parseInt(selectedButton.getText()) * stationFare;
                    farejTextField.setText(String.valueOf(currentFare));
                    for (Enumeration<AbstractButton> buttons = passengerButtonGroup.getElements(); buttons.hasMoreElements();) {
                        AbstractButton button = buttons.nextElement();
                        if (button != selectedButton) {
                            button.setEnabled(true);
                        }
                    }
                } else {
                    // Disable other buttons
                    for (Enumeration<AbstractButton> buttons = passengerButtonGroup.getElements(); buttons.hasMoreElements();) {
                        AbstractButton button = buttons.nextElement();
                        if (button != selectedButton) {
                            button.setEnabled(false);
                        }
                    }
                }
            }
        };
        // Assign ActionListener to each button
        onePassengerToggleButton.addActionListener(passengerButtonActionListener);
        twoPassengerToggleButton.addActionListener(passengerButtonActionListener);
        threePassengerToggleButton.addActionListener(passengerButtonActionListener);
        fourPassengerToggleButton.addActionListener(passengerButtonActionListener);
        // Create a ButtonGroup of passenger
        ButtonGroup stationButtonGroup = new ButtonGroup();
        stationButtonGroup.add(SaltLakeSectorVjToggleButton);
        stationButtonGroup.add(KarunamoyeejToggleButton);
        stationButtonGroup.add(CentralParkjToggleButton);
        stationButtonGroup.add(CityCenterjToggleButton);
        stationButtonGroup.add(BengalChemicaljToggleButton);
        
        stationButtonGroup.add(SaltLakeStadiumjToggleButton);
        stationButtonGroup.add(PhoolbaganjToggleButton);
        stationButtonGroup.add(SealdahToggleButton);
        stationButtonGroup.add(KalighatjToggleButton);
        stationButtonGroup.add(BengalChemicaljToggleButton1);
        
        stationButtonGroup.add(SaltLakeSectorVjToggleButton2);
        stationButtonGroup.add(KarunamoyeejToggleButton2);
        stationButtonGroup.add(CentralParkjToggleButton2);
        stationButtonGroup.add(CityCenterjToggleButton2);
        stationButtonGroup.add(BengalChemicaljToggleButton2);
        
        // Add Common ActionListener to each button of station group
        ActionListener stationButtonActionListener;
        stationButtonActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JToggleButton selectedButton = (JToggleButton) e.getSource();
                // Enable other buttons
                currentSelectedStation = selectedButton.getText();
                selectedStationButton = (JToggleButton) e.getSource();
                System.out.println("Current Station Name >>>"+currentSelectedStation );
                if (selectedButton.isSelected()) {
                    // Enable other buttons
                    for (Enumeration<AbstractButton> buttons = stationButtonGroup.getElements(); buttons.hasMoreElements();) {
                        AbstractButton button = buttons.nextElement();
                        if (button != selectedButton) {
                            button.setEnabled(true);
                        }
                    }
                } else {
                    // Disable other buttons
                    for (Enumeration<AbstractButton> buttons = stationButtonGroup.getElements(); buttons.hasMoreElements();) {
                        AbstractButton button = buttons.nextElement();
                        if (button != selectedButton) {
                            button.setEnabled(false);
                        }
                    }
                }
            }
        };
        // Assign ActionListener to each button
        SaltLakeSectorVjToggleButton.addActionListener(stationButtonActionListener);
        KarunamoyeejToggleButton.addActionListener(stationButtonActionListener);
        CentralParkjToggleButton.addActionListener(stationButtonActionListener);
        CityCenterjToggleButton.addActionListener(stationButtonActionListener);
        BengalChemicaljToggleButton.addActionListener(stationButtonActionListener);
        
        SaltLakeStadiumjToggleButton.addActionListener(stationButtonActionListener);
        PhoolbaganjToggleButton.addActionListener(stationButtonActionListener);
        SealdahToggleButton.addActionListener(stationButtonActionListener);
        KalighatjToggleButton.addActionListener(stationButtonActionListener);
        BengalChemicaljToggleButton1.addActionListener(stationButtonActionListener);
        
        
        SaltLakeSectorVjToggleButton2.addActionListener(stationButtonActionListener);
        KarunamoyeejToggleButton2.addActionListener(stationButtonActionListener);
        CentralParkjToggleButton2.addActionListener(stationButtonActionListener);
        CityCenterjToggleButton2.addActionListener(stationButtonActionListener);
        BengalChemicaljToggleButton2.addActionListener(stationButtonActionListener);
        startTimer();
    }
    
     /** Returns an ImageIcon, or null if the path was invalid. */      
      protected ImageIcon createImageIcon(String path) {
        InputStream stream = getClass().getResourceAsStream(path);
        if (stream != null) {
            try {
                ImageIcon icon = new ImageIcon(ImageIO.read(stream));
                stream.close();
                return icon;
            } catch (IOException e) {
                 System.err.println("Resource not found: " + e.getMessage() );
                e.printStackTrace();
            }
        } else {
            System.err.println("Resource not found: " + path);
        }
        return null;
    }
    
//    JSerialPort JSerialPortObj = new JSerialPort();

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        SaltLakeSectorVjToggleButton = new javax.swing.JToggleButton();
        KarunamoyeejToggleButton = new javax.swing.JToggleButton();
        CentralParkjToggleButton = new javax.swing.JToggleButton();
        CityCenterjToggleButton = new javax.swing.JToggleButton();
        BengalChemicaljToggleButton = new javax.swing.JToggleButton();
        jButton1 = new javax.swing.JButton();
        farejTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        SaltLakeStadiumjToggleButton = new javax.swing.JToggleButton();
        PhoolbaganjToggleButton = new javax.swing.JToggleButton();
        SealdahToggleButton = new javax.swing.JToggleButton();
        KalighatjToggleButton = new javax.swing.JToggleButton();
        BengalChemicaljToggleButton1 = new javax.swing.JToggleButton();
        SaltLakeSectorVjToggleButton2 = new javax.swing.JToggleButton();
        KarunamoyeejToggleButton2 = new javax.swing.JToggleButton();
        CentralParkjToggleButton2 = new javax.swing.JToggleButton();
        CityCenterjToggleButton2 = new javax.swing.JToggleButton();
        BengalChemicaljToggleButton2 = new javax.swing.JToggleButton();
        jLabel5 = new javax.swing.JLabel();
        timejLabel = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        logoLable = new javax.swing.JLabel();
        LogojLabel = new javax.swing.JLabel();
        twoPassengerToggleButton = new javax.swing.JToggleButton();
        onePassengerToggleButton = new javax.swing.JToggleButton();
        fourPassengerToggleButton = new javax.swing.JToggleButton();
        threePassengerToggleButton = new javax.swing.JToggleButton();
        QRjLabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(153, 204, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(1280, 1024));

        SaltLakeSectorVjToggleButton.setText("Salt Lake Sector V");
        SaltLakeSectorVjToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaltLakeSectorVjToggleButtonActionPerformed(evt);
            }
        });

        KarunamoyeejToggleButton.setText("Karunamoyee");
        KarunamoyeejToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                KarunamoyeejToggleButtonActionPerformed(evt);
            }
        });

        CentralParkjToggleButton.setText("Central Park");
        CentralParkjToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CentralParkjToggleButtonActionPerformed(evt);
            }
        });

        CityCenterjToggleButton.setText("City Center");
        CityCenterjToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CityCenterjToggleButtonActionPerformed(evt);
            }
        });

        BengalChemicaljToggleButton.setText("Bengal Chemical");
        BengalChemicaljToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BengalChemicaljToggleButtonActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jButton1.setForeground(new java.awt.Color(102, 102, 255));
        jButton1.setText("Scan & Pay");
        jButton1.setVerifyInputWhenFocusTarget(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        farejTextField.setFont(new java.awt.Font("Tahoma", 1, 28)); // NOI18N
        farejTextField.setForeground(new java.awt.Color(0, 255, 102));
        farejTextField.setText(" FARE");
        farejTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                farejTextFieldActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(102, 102, 255));
        jLabel1.setText("DESTINATION STATION");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(102, 102, 255));
        jLabel2.setText("Amount [Rs.]");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 48)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 51, 0));
        jLabel3.setText("Metro Railway Kolkata");

        SaltLakeStadiumjToggleButton.setText("Salt Lake Stadium");
        SaltLakeStadiumjToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaltLakeStadiumjToggleButtonActionPerformed(evt);
            }
        });

        PhoolbaganjToggleButton.setText("Phoolbagan");
        PhoolbaganjToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PhoolbaganjToggleButtonActionPerformed(evt);
            }
        });

        SealdahToggleButton.setText("Sealdah");
        SealdahToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SealdahToggleButtonActionPerformed(evt);
            }
        });

        KalighatjToggleButton.setText("Kalighat");
        KalighatjToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                KalighatjToggleButtonActionPerformed(evt);
            }
        });

        BengalChemicaljToggleButton1.setText("Netaji Bhavan");
        BengalChemicaljToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BengalChemicaljToggleButton1ActionPerformed(evt);
            }
        });

        SaltLakeSectorVjToggleButton2.setText("Maidan");
        SaltLakeSectorVjToggleButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaltLakeSectorVjToggleButton2ActionPerformed(evt);
            }
        });

        KarunamoyeejToggleButton2.setText("Park Street");
        KarunamoyeejToggleButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                KarunamoyeejToggleButton2ActionPerformed(evt);
            }
        });

        CentralParkjToggleButton2.setText("Chandni Chowk");
        CentralParkjToggleButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CentralParkjToggleButton2ActionPerformed(evt);
            }
        });

        CityCenterjToggleButton2.setText("Central");
        CityCenterjToggleButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CityCenterjToggleButton2ActionPerformed(evt);
            }
        });

        BengalChemicaljToggleButton2.setText("Bengal Chemical");
        BengalChemicaljToggleButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BengalChemicaljToggleButton2ActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 51, 51));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Esplanade");

        timejLabel.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        timejLabel.setForeground(new java.awt.Color(255, 255, 255));
        timejLabel.setText("time");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(102, 102, 255));
        jLabel6.setText("No of pessangers: ");

        LogojLabel.setBackground(new java.awt.Color(51, 51, 51));
        LogojLabel.setMaximumSize(new java.awt.Dimension(150, 150));
        LogojLabel.setMinimumSize(new java.awt.Dimension(55, 55));
        LogojLabel.setPreferredSize(new java.awt.Dimension(55, 55));

        twoPassengerToggleButton.setText("2");
        twoPassengerToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                twoPassengerToggleButtonActionPerformed(evt);
            }
        });

        onePassengerToggleButton.setText("1");
        onePassengerToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onePassengerToggleButtonActionPerformed(evt);
            }
        });

        fourPassengerToggleButton.setText("4");
        fourPassengerToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fourPassengerToggleButtonActionPerformed(evt);
            }
        });

        threePassengerToggleButton.setText("3");
        threePassengerToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                threePassengerToggleButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(SaltLakeSectorVjToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(SaltLakeStadiumjToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(LogojLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(logoLable)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 67, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(KarunamoyeejToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(62, 62, 62)
                                        .addComponent(CentralParkjToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(PhoolbaganjToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(SealdahToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(KalighatjToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGap(58, 58, 58)
                                                .addComponent(timejLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGap(47, 47, 47)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(BengalChemicaljToggleButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(BengalChemicaljToggleButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                        .addGap(14, 14, 14))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(CityCenterjToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(49, 49, 49)
                                        .addComponent(BengalChemicaljToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(23, 23, 23))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(KarunamoyeejToggleButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(62, 62, 62)
                                .addComponent(CentralParkjToggleButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(CityCenterjToggleButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(269, 269, 269))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(onePassengerToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(twoPassengerToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(threePassengerToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(fourPassengerToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(farejTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(52, 52, 52)
                        .addComponent(QRjLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(318, 318, 318))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(SaltLakeSectorVjToggleButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 498, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(357, 357, 357)
                        .addComponent(jLabel3))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(486, 486, 486)
                        .addComponent(jLabel5)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addComponent(timejLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(47, 47, 47)
                                .addComponent(logoLable))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addComponent(jLabel3)))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(LogojLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(7, 7, 7)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(KarunamoyeejToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CentralParkjToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(SaltLakeSectorVjToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CityCenterjToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(BengalChemicaljToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(PhoolbaganjToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(SaltLakeStadiumjToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(KalighatjToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(BengalChemicaljToggleButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(SealdahToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(KarunamoyeejToggleButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CentralParkjToggleButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(SaltLakeSectorVjToggleButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CityCenterjToggleButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(BengalChemicaljToggleButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(102, 102, 102)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(twoPassengerToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(onePassengerToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fourPassengerToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(threePassengerToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(79, 79, 79)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(QRjLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(58, 58, 58)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(farejTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap(130, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(172, 172, 172))))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 1281, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, 1007, 1007, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jTabbedPane1.addTab("WMTT", jPanel1);

        jButton2.setText("Printer");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Status");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(466, 466, 466)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(64, 64, 64)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(503, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(172, 172, 172)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(784, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab2", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

//                int connect = printerObj.ConnectDevice(1, 9600);
//                System.out.println("Port connect successfully."+connect);
                // List all available ports
                // Replace with the actual port your printer is connected to
                String printerPortName = "COM1"; // Example port name, change as needed
                SerialPort printerPort = SerialPort.getCommPort(printerPortName);
                // Configure the port
                printerPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
                printerPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);
                inputStreamObj = printerPort.getInputStream();
                // Open the port
                if (printerPort.openPort()) {
                    System.out.println("Port opened successfully.");

                    try {
                        // Get the output stream
                        outputStream = printerPort.getOutputStream();
                        // Print text
                        printText(outputStream, "  Metro Railway Kolkata",4);
                        printText(outputStream, "         QR Ticket Number  : A003675619",1);
                        printText(outputStream, "",1);  
                        byte[] recv_byte = new byte[10];
//                        printerStatus(outputStream);
                        printQRCode(outputStream,"please scan and pay for qr ticket");                        
                        printText(outputStream, "",1);
                        printText(outputStream, "Fare : Rs. 10.00",1);
                        printText(outputStream, "Source : Esplanade",1);
                        printText(outputStream, "Destination : Howrah Metro",1);
                        printText(outputStream, "Valid For : One Passengers & One Ride",1);
                        printText(outputStream, "Entry Valid Upto, 16/07/2024 15:29:16",1);
                        printText(outputStream, "",1);
                        printText(outputStream, "ESPM14            16/07/2024 15:29:16",1);
                        printText(outputStream, "",1);
                        printText(outputStream, "Please preserve this ticket till exit.",1);
                        printText(outputStream, "",1);
                        printText(outputStream, "",1);
                        printText(outputStream, "",1);
                        printText(outputStream, "",1);
                        printText(outputStream, "",1);                        
                        printPartialCut(outputStream);
                        boolean rs = Read_Reply(recv_byte, 6);                        
//                        System.out.println("Text sent to printer.");
                        System.out.println("Text Satus >>>>>"+Arrays.toString(recv_byte));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        // Close the port
                        printerPort.closePort();
                        System.out.println("Port closed.");
                    }
                } else {
                    System.out.println("Failed to open port.");
                }

    }//GEN-LAST:event_jButton2ActionPerformed
    
    private static String incrementTicketNumber(String ticketNumber) {
        // Assuming the ticket number format is letter(s) followed by digits
        String prefix = ticketNumber.replaceAll("[0-9]", "");
        String numericPart = ticketNumber.replaceAll("[^0-9]", "");
        // Convert numeric part to integer
        int number = Integer.parseInt(numericPart);
        // Increment the number
        number++;
        // Format the number back into the original format
        String formattedNumber = String.format("%0" + numericPart.length() + "d", number);
        // Concatenate prefix with formatted number
        String incrementedTicketNo = prefix + formattedNumber;
        return incrementedTicketNo;
    }
    
    public void printerTicket() throws Exception {
                
                String printerPortName; // Example port name, change as needed
                if( true == configPrinterPort.isEmpty() ){
                    printerPortName = "COM1";
                }else{
                    printerPortName = configPrinterPort;
                }
                SerialPort printerPort = SerialPort.getCommPort(printerPortName);
                // Configure the port
                printerPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
                printerPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);
                inputStreamObj = printerPort.getInputStream();
                this.qrTicketStartNo = incrementTicketNumber(this.qrTicketStartNo);
                // Get current date and time
                LocalDateTime currentDateTime = LocalDateTime.now();
                // Define the desired format
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a");
                // Format the current date and time
                String todayDateTime = currentDateTime.format(formatter);
                // Add 3 hours to the current date and time
                LocalDateTime futureDateTime = currentDateTime.plusHours(4);
                // Format the future date and time
                String formattedFutureDateTime = futureDateTime.format(formatter);
                
                // Open the port
                if (printerPort.openPort()) {
                    System.out.println("printerTicket Port opened successfully.");
                    try {
                        // Get the output stream
                        outputStream = printerPort.getOutputStream();

                        // Print text
                        printText(outputStream, "  Metro Railway Kolkata",4);
                        printText(outputStream, "         QR Ticket Number  : "+this.qrTicketStartNo,1);
                        printText(outputStream, "",1);  
                        byte[] recv_byte = new byte[10];
//                        printerStatus(outputStream);
                        printQRCode(outputStream,"Esplanade to "+this.currentSelectedStation+"Fare : Rs. "+this.stationFare+" Entry Valid Upto, "+formattedFutureDateTime+" & Please preserve this ticket till exit. ");
                        
                        printText(outputStream, "",1);
                        printText(outputStream, "Fare : Rs. "+this.stationFare,1);
                        printText(outputStream, "Source : Esplanade",1);
                        printText(outputStream, "Destination : "+this.currentSelectedStation,1);
                        printText(outputStream, "Valid For : One Passengers & One Ride",1);
                        printText(outputStream, "Entry Valid Upto, "+formattedFutureDateTime,1);
                        printText(outputStream, "",1);
                        printText(outputStream, "ESPM14           "+todayDateTime,1);
                        printText(outputStream, "",1);
                        printText(outputStream, "Please preserve this ticket till exit.",1);
                        printText(outputStream, "",1);
                        printText(outputStream, "",1);
                        printText(outputStream, "",1);
                        printText(outputStream, "",1);
                        printText(outputStream, "",1);
                        
                        printPartialCut(outputStream);
                        boolean rs = Read_Reply(recv_byte, 6);
                        
//                        System.out.println("Text sent to printer.");
                        System.out.println("printerTicket Text Satus >>>>>"+Arrays.toString(recv_byte));
                    } catch (Exception e) {
                        System.out.println("printerTicket exception >>>>>"+e.getMessage() );
                        e.printStackTrace();
                    } finally {
                        // Close the port
                        printerPort.closePort();
                        System.out.println("printerTicket Port closed.");
                    }
                } else {
                    System.out.println("printerTicket Failed to open port.");
                }
    }
    
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

    }//GEN-LAST:event_jButton3ActionPerformed

    private void BengalChemicaljToggleButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BengalChemicaljToggleButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BengalChemicaljToggleButton2ActionPerformed

    private void CityCenterjToggleButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CityCenterjToggleButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CityCenterjToggleButton2ActionPerformed

    private void CentralParkjToggleButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CentralParkjToggleButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CentralParkjToggleButton2ActionPerformed

    private void KarunamoyeejToggleButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_KarunamoyeejToggleButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_KarunamoyeejToggleButton2ActionPerformed

    private void SaltLakeSectorVjToggleButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaltLakeSectorVjToggleButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SaltLakeSectorVjToggleButton2ActionPerformed

    private void BengalChemicaljToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BengalChemicaljToggleButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BengalChemicaljToggleButton1ActionPerformed

    private void KalighatjToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_KalighatjToggleButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_KalighatjToggleButtonActionPerformed

    private void SealdahToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SealdahToggleButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SealdahToggleButtonActionPerformed

    private void PhoolbaganjToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PhoolbaganjToggleButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PhoolbaganjToggleButtonActionPerformed

    private void SaltLakeStadiumjToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaltLakeStadiumjToggleButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SaltLakeStadiumjToggleButtonActionPerformed

    private void farejTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_farejTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_farejTextFieldActionPerformed

    private void BengalChemicaljToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BengalChemicaljToggleButtonActionPerformed
        // farejTextField.setText("50");
    }//GEN-LAST:event_BengalChemicaljToggleButtonActionPerformed

    private void CityCenterjToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CityCenterjToggleButtonActionPerformed
        // farejTextField.setText("40");
    }//GEN-LAST:event_CityCenterjToggleButtonActionPerformed

    private void CentralParkjToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CentralParkjToggleButtonActionPerformed
        // farejTextField.setText("30");
    }//GEN-LAST:event_CentralParkjToggleButtonActionPerformed

    private void KarunamoyeejToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_KarunamoyeejToggleButtonActionPerformed
        // farejTextField.setText("20");
    }//GEN-LAST:event_KarunamoyeejToggleButtonActionPerformed

    private void SaltLakeSectorVjToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaltLakeSectorVjToggleButtonActionPerformed
        // farejTextField.setText("10");
    }//GEN-LAST:event_SaltLakeSectorVjToggleButtonActionPerformed

    private void twoPassengerToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_twoPassengerToggleButtonActionPerformed
//       int value = Integer.parseInt(farejTextField.getText()) * 2;
//       farejTextField.setText(""+value);
    }//GEN-LAST:event_twoPassengerToggleButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
          
          System.out.println("Scan&Pay numberOfPassenger >>> "+this.numberOfPassenger);
          if( numberOfPassenger <= 0 || true == currentSelectedStation.isEmpty()){
              JOptionPane.showMessageDialog(null, "Please select any station and number of passenger before proceed", "Metro WMTT", JOptionPane.INFORMATION_MESSAGE);
              return;
          }
          for(int counter=1;counter<=numberOfPassenger;counter++){
               try {
                    printerTicket();
               } catch (Exception ex) {
                    Logger.getLogger(WmttJFrame.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Scan&Pay exception >>>>>"+ex.getMessage() );
               }
          }
          selectedStationButton.setSelected(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void threePassengerToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_threePassengerToggleButtonActionPerformed
//        int value = Integer.parseInt(farejTextField.getText()) * 3;
//        farejTextField.setText(""+value);
    }//GEN-LAST:event_threePassengerToggleButtonActionPerformed

    private void fourPassengerToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fourPassengerToggleButtonActionPerformed
//        int value = Integer.parseInt(farejTextField.getText()) * 4;
//        farejTextField.setText(""+value);
    }//GEN-LAST:event_fourPassengerToggleButtonActionPerformed

    private void onePassengerToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onePassengerToggleButtonActionPerformed
//        int value = Integer.parseInt(farejTextField.getText()) * 1;
//        farejTextField.setText(""+value);
    }//GEN-LAST:event_onePassengerToggleButtonActionPerformed
    public static void printText(OutputStream outputStream, String text, int fontSize) throws Exception {
        // Initialize printer (ESC @)
        
        outputStream.write(new byte[]{0x1b, 0x40});

        
        // Set font size
        //outputStream.write(new byte[] {0x1b, 0x21, (byte) ((fontSize - 1) | (bold ? 0x08 : 0))});
        if (4==fontSize) {
            outputStream.write(new byte[] {0x1b, 0x21, 0x20});
            outputStream.write(new byte[] {0x1b, 0x45, 0x01});
        }
    
        
        // Convert the text to bytes using the printer's character encoding
        byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);

        // Send the text to the printer
        outputStream.write(textBytes);
        outputStream.flush();

        // Print and feed line (ESC d n - feed n lines)
        byte[] feedCommand = {27, 100, 1}; // Feed one line
        outputStream.write(feedCommand);
        outputStream.flush();
    }
    
    public static void printerStatus(OutputStream outputStream) throws Exception {
        // Initialize printer (ESC @)
        byte[] initCommand = {29, 114, 2};
        outputStream.write(initCommand);
        outputStream.flush();
    }
    
    public synchronized boolean Read_Reply(byte[] recv_byte, int totalbytetorecv) {
		//System.out.println("[Read_Reply()]  Entry  >>>");
		Instant before = Instant.now();
		int receive = 0, counter = 0;
	
		while (true) {
			try {
				// Check if there is data available before attempting to read
				if (inputStreamObj.available() > 0) {
					receive = inputStreamObj.read();
					if (receive >= 0 && receive <= 255) {
						recv_byte[counter] = (byte) receive;
						counter++;
						if (counter == totalbytetorecv) {
							return true;
						}
					}
				}
			} catch (IOException ex) {
				
                                System.out.println(""+ ex.getMessage());
				return false;
			}
	
			// Check for timeout
			Instant after = Instant.now();
			long durationInterval = Duration.between(before, after).toMillis();
			if (durationInterval >= 500) {
				//System.out.println("[Read_Reply()] Operation timeout occurred");
				return false;
			}
	
			// Optional: add a small sleep to avoid busy-waiting
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return false;
			}
		}
    }
    
    private synchronized int writeQrPort(OutputStream outputStream,String QrData, int alingnmnt){
		//Scanner sc = new Scanner(System.in);
            synchronized(this){
                
                OutputStream out;
				InputStream stream =null;
				out=outputStream;
                
				String s = "!";
				String pr = QrData;
				pr = pr+"\n"+s;
				try
				{
					stream = new ByteArrayInputStream(pr.getBytes("UTF-8"));		
				}// try end
				catch(UnsupportedEncodingException uex)
				{
//					printlog(ERROR, "[ writePort() other error UnsupportedEncodingException caught ]");
					return 31;
				}// catch(UnsupportedEncodingException uex) end
				byte[] recv_byte= new byte[100];
				int totalbytetorecv=0;
				byte[] initCommand = {29, 112, 2, 29, 107, 10};
				try {
					out.write(27);
					out.write(64);
					out.write(29);
					out.write(40);
					out.write(107);
//					out.write(10);
				} catch (IOException ex) {
//					printlog(ERROR, "[writePort()] "+ex.getCause().toString());
                                        return 31;
				}
				
				try
				{
					int c=0;						
					while((c=stream.read())!=33)
					{									
						out.write(c);
						// //System.out.println(c);
						//printlog(c);
					}//while end

				}//try end
				catch(IOException ioexc)
				{
//					printlog(ERROR, "[writePort()] "+ioexc.getCause().toString());
					return 31;
				}//catch end
				
				try {
					out.write(00);
					
				} catch (IOException ex) {
//					printlog(ERROR, "[writePort()] "+ex.getCause().toString());
                                        return 31;
				}

				out=null;
				stream=null;
//                               printlog(INFO, "[Printer] [writePort()] Print msg Successfull.");
                
				return 0;
            }
	}
        
    public void printQRCode(OutputStream outputStream,String data) {
            
            
            try {
                // Initialize the printer
                outputStream.write(new byte[]{0x1b, 0x40});

                // Model
                outputStream.write(new byte[]{0x1d, 0x28, 0x6b, 0x03, 0x00, 0x31, 0x43, 0x03});

                // Error correction level
                outputStream.write(new byte[]{0x1d, 0x28, 0x6b, 0x03, 0x00, 0x31, 0x45, 0x30});
                
                //Size
                outputStream.write(new byte[]{0x1d, 0x28, 0x6b, 0x03, 0x00, 0x31, 0x43, 0x0A});
                 

                // Store data in the symbol storage area
                int dataLength = data.length() + 3;
                byte pL = (byte) (dataLength & 0xFF);
                byte pH = (byte) ((dataLength >> 8) & 0xFF);
                outputStream.write(new byte[]{0x1d, 0x28, 0x6b, pL, pH, 0x31, 0x50, 0x30});
                byte[] textBytes = data.getBytes(StandardCharsets.UTF_8);
                outputStream.write(textBytes);

                // Align center
                outputStream.write(new byte[]{0x1b, 0x61, 0x01});

                // Print the symbol data in the symbol storage area
                outputStream.write(new byte[]{0x1d, 0x28, 0x6b, 0x03, 0x00, 0x31, 0x52, 0x30});

                // Print the QR code
                outputStream.write(new byte[]{0x1d, 0x28, 0x6b, 0x03, 0x00, 0x31, 0x51, 0x30});

                outputStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
    
    public void printPartialCut(OutputStream outputStream) {
        try {
            outputStream.write(new byte[]{0x1B, 0x40});
//            outputStream.write(new byte[]{0x30, 0x30, 0x30, 0x0D, 0x0A});
            outputStream.write(new byte[]{0x1B, 0x6D});
            outputStream.flush();
        } catch (Exception e) {
             e.printStackTrace();
        }
    }
    
    private void startTimer() {
        Thread timerThread = new Thread(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            while (true) {
                try {
                    // Get the current time
                    String currentTime = sdf.format(new Date());

                    // Update the label on the Event Dispatch Thread
                    SwingUtilities.invokeLater(() -> timejLabel.setText("Time: " + currentTime));

                    // Sleep for 1 second
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // Start the timer thread
        timerThread.start();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(WmttJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(WmttJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(WmttJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(WmttJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new WmttJFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton BengalChemicaljToggleButton;
    private javax.swing.JToggleButton BengalChemicaljToggleButton1;
    private javax.swing.JToggleButton BengalChemicaljToggleButton2;
    private javax.swing.JToggleButton CentralParkjToggleButton;
    private javax.swing.JToggleButton CentralParkjToggleButton2;
    private javax.swing.JToggleButton CityCenterjToggleButton;
    private javax.swing.JToggleButton CityCenterjToggleButton2;
    private javax.swing.JToggleButton KalighatjToggleButton;
    private javax.swing.JToggleButton KarunamoyeejToggleButton;
    private javax.swing.JToggleButton KarunamoyeejToggleButton2;
    private javax.swing.JLabel LogojLabel;
    private javax.swing.JToggleButton PhoolbaganjToggleButton;
    private javax.swing.JLabel QRjLabel;
    private javax.swing.JToggleButton SaltLakeSectorVjToggleButton;
    private javax.swing.JToggleButton SaltLakeSectorVjToggleButton2;
    private javax.swing.JToggleButton SaltLakeStadiumjToggleButton;
    private javax.swing.JToggleButton SealdahToggleButton;
    private javax.swing.JTextField farejTextField;
    private javax.swing.JToggleButton fourPassengerToggleButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel logoLable;
    private javax.swing.JToggleButton onePassengerToggleButton;
    private javax.swing.JToggleButton threePassengerToggleButton;
    private javax.swing.JLabel timejLabel;
    private javax.swing.JToggleButton twoPassengerToggleButton;
    // End of variables declaration//GEN-END:variables
}

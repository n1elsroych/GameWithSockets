package view;

import formEvents.ClientConnectionEvent;
import formEvents.DataReceivedEventEvent;
import formEvents.ClientDisconnectionEvent;
import formEvents.FormEventsListener;
import formEvents.ValidationErrorEvent;
import java.awt.Color;
import static java.awt.Component.CENTER_ALIGNMENT;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;

import javax.swing.JLabel;
import javax.swing.JPanel;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import threads.Client;
import tools.Protocol;

public class GameWindow extends javax.swing.JFrame implements FormEventsListener{

    private final String SERVER_ADDRESS = "localhost";
    private final int PORT = 8888;
    
    private Client client;
    private Login login;
    private Register register;
    private String shipsCoord;
    
    private boolean isShip1Selected;
    private boolean isShip2Selected;
    private int shipsCount;
    
    private ImageIcon ship1Image;
    private ImageIcon ship2Image;
    private ImageIcon ship1SelectedImage;
    private ImageIcon ship2SelectedImage;
    
    private JPanel myMap;
    private JPanel enemyMap;
    private JPanel sideBarBeforeGame;
    private JPanel sideBarForGame;
    
    private int screenWidth;
    private int screenHeight;
    
    private JLabel lbTitle;
    
    private boolean isAttacking;
    
    public GameWindow() {
        initComponents();
        
        setExtendedState(MAXIMIZED_BOTH);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);
        isShip1Selected = false;
        isShip2Selected = false;
        shipsCount = 0;
        isAttacking = false;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = (int)screenSize.getWidth();
        screenHeight = (int)screenSize.getHeight();
        try {
            client = new Client(SERVER_ADDRESS, PORT);
            client.connect();
            client.addFormEventsListener(this);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        createSideBarBeforeGame();
        createSideBarForGame();
        createGameTitle();
        createMyMap();
        createEnemyMap();
        setImageBackgroundMap();
    }
    
    private void setImageBackgroundMap(){
        ImageIcon seaImageIcon = new ImageIcon(new ImageIcon(getClass().getResource("/img/water.gif")).getImage()
                                .getScaledInstance((screenWidth / 4) * 3, (screenHeight / 10) * 9, 0));
        JLabel lbSeaImage = new JLabel(seaImageIcon);
        getContentPane().add(lbSeaImage, new AbsoluteConstraints(10, 50, (screenWidth / 4) * 3, (screenHeight / 10) * 9));
    }
    
    private void createGameTitle(){        
        //lbTitle = new JLabel("Prepara tu estrategia antes de la batalla \u2694 \u2693", (int) CENTER_ALIGNMENT);
        lbTitle = new JLabel("No tienes conexión", (int) CENTER_ALIGNMENT);
        lbTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
        lbTitle.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        getContentPane().add(lbTitle, new AbsoluteConstraints(10, 5, (screenWidth / 4) * 3, 40));
    }
    
    private void loadImages(){
        ship1Image = new ImageIcon(new ImageIcon(getClass().getResource("/img/barco1.png")).getImage()
                                .getScaledInstance(lbShip1.getWidth(), lbShip1.getHeight(), 0));
        
        ship2Image = new ImageIcon(new ImageIcon(getClass().getResource("/img/barco2.png")).getImage()
                                .getScaledInstance(lbShip2.getWidth(), lbShip2.getHeight(), 0));
        
        ship1SelectedImage = new ImageIcon(new ImageIcon(getClass().getResource("/img/barco1Selected.png")).getImage()
                                .getScaledInstance(lbShip1.getWidth(), lbShip1.getHeight(), 0));
        
        ship2SelectedImage = new ImageIcon(new ImageIcon(getClass().getResource("/img/barco2Selected.png")).getImage()
                                .getScaledInstance(lbShip2.getWidth(), lbShip2.getHeight(), 0));
    }
    
    private void createSideBarBeforeGame(){
        loadImages();
        
        sideBarBeforeGame = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        sideBarBeforeGame.setBackground(Color.WHITE);
        //sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));
        sideBarBeforeGame.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        getContentPane().add(sideBarBeforeGame, new AbsoluteConstraints(
                10 + ((screenWidth / 4) * 3) + 5, 5, (screenWidth / 4)-10-5-5, ((screenHeight / 10) * 9) + 45));
        
        sideBarBeforeGame.add(lbUsername);
        
        sideBarBeforeGame.add(txaGameRulesContainer);
        lbShip1.setIcon(ship1Image);
        sideBarBeforeGame.add(lbShip1);
        lbShip2.setIcon(ship2Image);
        sideBarBeforeGame.add(lbShip2);
        
        lbGameText.setVisible(false);
        sideBarBeforeGame.add(lbGameText);
        
        sideBarBeforeGame.add(containerBtnPlay);
        
    }
    
    private void createSideBarForGame(){        
        sideBarForGame = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        sideBarForGame.setVisible(false);
        sideBarForGame.setBackground(Color.WHITE);
        //sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));
        sideBarForGame.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        sideBarForGame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                isAttacking = false;
            }
        });
        getContentPane().add(sideBarForGame, new AbsoluteConstraints(
                10 + ((screenWidth / 4) * 3) + 5, 5, (screenWidth / 4)-10-5-5, ((screenHeight / 10) * 9) + 45));
        
        sideBarForGame.add(lbUsernameInGame);
        sideBarForGame.add(containerBtnAttack);
        sideBarForGame.add(containerBtnViewMyMap);
        sideBarForGame.add(scpGameActions);
    }
    
    private void createMyMap(){
        myMap = new JPanel(new GridLayout(10, 10));
        myMap.setOpaque(false);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                final int row = i;
                final int col = j;
                JLabel label = new JLabel();
                label.setBorder(BorderFactory.createLineBorder(new Color(67, 140, 149)));
                label.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        if (isShip1Selected && shipsCount < 4){
                            if (shipsCount < 1){
                                shipsCoord = Protocol.Coords(client.getSessionId());
                            }
                            shipsCoord += Protocol.parseCoordData(row, col);
                            shipsCount++;
                            ImageIcon ship1 = new ImageIcon(new ImageIcon(getClass().getResource("/img/barco1.png")).getImage()
                                .getScaledInstance(label.getWidth(), label.getHeight(), 0));
                            label.setIcon(ship1);
                            isShip1Selected = false;
                            lbShip1.setIcon(ship1Image);
                        }
                            System.out.println(row + "," + col);
                        if (isShip2Selected && shipsCount < 4){
                            if (shipsCount < 1){
                                shipsCoord = Protocol.Coords(client.getSessionId());
                            }
                            shipsCoord += Protocol.parseCoordData(row, col);
                            shipsCount++;
                            ImageIcon ship2 = new ImageIcon(new ImageIcon(getClass().getResource("/img/barco2.png")).getImage()
                                .getScaledInstance(label.getWidth(), label.getHeight(), 0));
                            label.setIcon(ship2);
                            isShip2Selected = false;
                            lbShip2.setIcon(ship2Image);
                        }
                            
                        if (shipsCount == 4){
                            btnPlay.setEnabled(true);
                        }
                    }
                    
                    public void mouseEntered(MouseEvent e) {
                        if (isShip1Selected | isShip2Selected) {
                            label.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
                        }
                    }
                    public void mouseExited(MouseEvent e) {
                        //label.setBorder(null);
                        label.setBorder(BorderFactory.createLineBorder(new Color(67, 140, 149)));
                    }
                });
                myMap.add(label);
            }
        }
        getContentPane().add(myMap, new AbsoluteConstraints(10, 50, (screenWidth / 4) * 3, (screenHeight / 10) * 9));
    }
    
    private void createEnemyMap(){
        enemyMap = new JPanel(new GridLayout(10, 10));
        enemyMap.setVisible(false);
        enemyMap.setOpaque(false);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                final int row = i;
                final int col = j;
                JLabel label = new JLabel();
                label.setBorder(BorderFactory.createLineBorder(new Color(67, 140, 149)));
                label.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        if (isAttacking){
                            System.out.println("Atacar "+row + "," + col);
                            getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                            try {
                                client.sendData(Protocol.parseAttack(client.getSessionId(), row, col));
                            } catch (IOException ex){
                                ex.printStackTrace();
                            }
                            isAttacking = false;
                            myMap.setVisible(true);
                            enemyMap.setVisible(false);
                            lbTitle.setText("Espera tu turno para atacar");
                            btnAttack.setEnabled(false);
                        }
                    }
                    
                    public void mouseEntered(MouseEvent e) {
                        if (isAttacking) {
                            label.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
                        }
                    }
                    public void mouseExited(MouseEvent e) {
                        //label.setBorder(null);
                        label.setBorder(BorderFactory.createLineBorder(new Color(67, 140, 149)));
                    }
                });
                enemyMap.add(label);
            }
        }    
        getContentPane().add(enemyMap, new AbsoluteConstraints(10, 50, (screenWidth / 4) * 3, (screenHeight / 10) * 9));

    }   
    
    public Client getClientReference(){
        return client;
    }
    
    public void setLoginReference(Login login){
        this.login = login;
    }
    
    void setRegisterReference(Register register) {
        this.register = register;
    }
    
    public void sessionStarted(String data){
        lbTitle.setText("Prepara tu estrategia antes de la batalla \u2694 \u2693");
        String username = Protocol.getUsername(data);
        lbUsername.setText(lbUsername.getText()+" "+username);
        lbUsernameInGame.setText(lbUsernameInGame.getText()+" "+username);
        this.setVisible(true);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbGameText = new javax.swing.JLabel();
        lbShip1 = new javax.swing.JLabel();
        lbShip2 = new javax.swing.JLabel();
        txaGameRulesContainer = new javax.swing.JPanel();
        txaGameRules = new javax.swing.JTextArea();
        containerBtnPlay = new javax.swing.JPanel();
        btnPlay = new javax.swing.JButton();
        lbUsername = new javax.swing.JLabel();
        lbUsernameInGame = new javax.swing.JLabel();
        containerBtnAttack = new javax.swing.JPanel();
        btnAttack = new javax.swing.JButton();
        containerBtnViewMyMap = new javax.swing.JPanel();
        btnViewMyMap = new javax.swing.JButton();
        scpGameActions = new javax.swing.JScrollPane();
        txaGameActions = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbGameText.setText("Esperando Jugadores...");
        getContentPane().add(lbGameText, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 360, -1, -1));

        lbShip1.setFont(new java.awt.Font("Rockwell", 0, 18)); // NOI18N
        lbShip1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/barco1.png"))); // NOI18N
        lbShip1.setText("Selecciona tu barco");
        lbShip1.setToolTipText("");
        lbShip1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        lbShip1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lbShip1.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        lbShip1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbShip1MouseClicked(evt);
            }
        });
        getContentPane().add(lbShip1, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 70, 160, 180));

        lbShip2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/barco2.png"))); // NOI18N
        lbShip2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbShip2MouseClicked(evt);
            }
        });
        getContentPane().add(lbShip2, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 70, 160, 170));

        txaGameRulesContainer.setBackground(new java.awt.Color(255, 255, 153));
        txaGameRulesContainer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));

        txaGameRules.setEditable(false);
        txaGameRules.setBackground(new java.awt.Color(255, 255, 153));
        txaGameRules.setColumns(20);
        txaGameRules.setFont(new java.awt.Font("MS UI Gothic", 0, 17)); // NOI18N
        txaGameRules.setForeground(new java.awt.Color(0, 0, 0));
        txaGameRules.setRows(5);
        txaGameRules.setText("         Reglas del juego:\n - El ganador es el que queda \n    con algún barco en pie.\n - Necesitas 4 barcos para \n    crear una partida y jugar,\n    ni uno más ni uno menos.\n - Espera tu turno para atacar  ");
        txaGameRules.setAutoscrolls(false);
        txaGameRules.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8));
        txaGameRulesContainer.add(txaGameRules);

        getContentPane().add(txaGameRulesContainer, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 60, 260, 180));

        btnPlay.setBackground(new java.awt.Color(51, 153, 255));
        btnPlay.setFont(new java.awt.Font("Rockwell", 1, 18)); // NOI18N
        btnPlay.setForeground(new java.awt.Color(255, 255, 255));
        btnPlay.setText("Crear Partida");
        btnPlay.setEnabled(false);
        btnPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout containerBtnPlayLayout = new javax.swing.GroupLayout(containerBtnPlay);
        containerBtnPlay.setLayout(containerBtnPlayLayout);
        containerBtnPlayLayout.setHorizontalGroup(
            containerBtnPlayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnPlay, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
        );
        containerBtnPlayLayout.setVerticalGroup(
            containerBtnPlayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnPlay, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        getContentPane().add(containerBtnPlay, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 320, 240, 60));

        lbUsername.setFont(new java.awt.Font("Lucida Sans", 0, 18)); // NOI18N
        lbUsername.setText("Usuario:");
        getContentPane().add(lbUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 240, -1, -1));

        lbUsernameInGame.setFont(new java.awt.Font("Lucida Sans", 0, 18)); // NOI18N
        lbUsernameInGame.setText("Usuario:");
        getContentPane().add(lbUsernameInGame, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 170, -1, -1));

        btnAttack.setBackground(new java.awt.Color(51, 153, 255));
        btnAttack.setFont(new java.awt.Font("Rockwell", 1, 18)); // NOI18N
        btnAttack.setForeground(new java.awt.Color(255, 255, 255));
        btnAttack.setText("Atacar");
        btnAttack.setEnabled(false);
        btnAttack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAttackActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout containerBtnAttackLayout = new javax.swing.GroupLayout(containerBtnAttack);
        containerBtnAttack.setLayout(containerBtnAttackLayout);
        containerBtnAttackLayout.setHorizontalGroup(
            containerBtnAttackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 240, Short.MAX_VALUE)
            .addGroup(containerBtnAttackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(containerBtnAttackLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(btnAttack, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        containerBtnAttackLayout.setVerticalGroup(
            containerBtnAttackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 60, Short.MAX_VALUE)
            .addGroup(containerBtnAttackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(containerBtnAttackLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(btnAttack, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        getContentPane().add(containerBtnAttack, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 440, 240, 60));

        btnViewMyMap.setBackground(new java.awt.Color(51, 153, 255));
        btnViewMyMap.setFont(new java.awt.Font("Rockwell", 1, 18)); // NOI18N
        btnViewMyMap.setForeground(new java.awt.Color(255, 255, 255));
        btnViewMyMap.setText("Ver mi mapa");
        btnViewMyMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewMyMapActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout containerBtnViewMyMapLayout = new javax.swing.GroupLayout(containerBtnViewMyMap);
        containerBtnViewMyMap.setLayout(containerBtnViewMyMapLayout);
        containerBtnViewMyMapLayout.setHorizontalGroup(
            containerBtnViewMyMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnViewMyMap, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
        );
        containerBtnViewMyMapLayout.setVerticalGroup(
            containerBtnViewMyMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnViewMyMap, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
        );

        getContentPane().add(containerBtnViewMyMap, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 530, 240, 60));

        txaGameActions.setEditable(false);
        txaGameActions.setColumns(20);
        txaGameActions.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        txaGameActions.setRows(5);
        scpGameActions.setViewportView(txaGameActions);

        getContentPane().add(scpGameActions, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 350, 210, 260));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lbShip1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbShip1MouseClicked
        if (shipsCount == 4){
            return;
        }
        isShip1Selected = true;
        lbShip1.setIcon(ship1SelectedImage);

        isShip2Selected = false;
        lbShip2.setIcon(ship2Image);
    }//GEN-LAST:event_lbShip1MouseClicked

    private void lbShip2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbShip2MouseClicked
        if (shipsCount == 4){
            return;
        }
        isShip2Selected = true;
        lbShip2.setIcon(ship2SelectedImage);

        isShip1Selected = false;
        lbShip1.setIcon(ship1Image);
    }//GEN-LAST:event_lbShip2MouseClicked

    private void btnPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayActionPerformed
        if (btnPlay.getText().contains("Crear Partida")){
            try {
                client.sendData(Protocol.BeginGameControlRequest(client.getSessionId()));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        if (btnPlay.getText().contains("Jugar")){
            try {
                btnPlay.setEnabled(false);
                client.sendData(shipsCoord);
                client.sendData(Protocol.readyToPlay(client.getSessionId()));
            } catch (IOException ex){
                ex.printStackTrace();
            }
        }
        
        if (btnPlay.getText().contains("Comenzar Partida")){
            try {
                client.sendData(shipsCoord);
                client.sendData(Protocol.BeginGameRequest(client.getSessionId()));
            } catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_btnPlayActionPerformed

    private void btnAttackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAttackActionPerformed
        isAttacking = true;
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        myMap.setVisible(false);
        enemyMap.setVisible(true);
        
        int f = 2; //(0 - 9)
        int c = 5; //(0 - 9)
        int index = f * 10 + c;

    }//GEN-LAST:event_btnAttackActionPerformed

    private void btnViewMyMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewMyMapActionPerformed
        isAttacking = false;
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        myMap.setVisible(true);
        enemyMap.setVisible(false);
    }//GEN-LAST:event_btnViewMyMapActionPerformed

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
            java.util.logging.Logger.getLogger(GameWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GameWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GameWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GameWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GameWindow().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAttack;
    private javax.swing.JButton btnPlay;
    private javax.swing.JButton btnViewMyMap;
    private javax.swing.JPanel containerBtnAttack;
    private javax.swing.JPanel containerBtnPlay;
    private javax.swing.JPanel containerBtnViewMyMap;
    private javax.swing.JLabel lbGameText;
    private javax.swing.JLabel lbShip1;
    private javax.swing.JLabel lbShip2;
    private javax.swing.JLabel lbUsername;
    private javax.swing.JLabel lbUsernameInGame;
    private javax.swing.JScrollPane scpGameActions;
    private javax.swing.JTextArea txaGameActions;
    private javax.swing.JTextArea txaGameRules;
    private javax.swing.JPanel txaGameRulesContainer;
    // End of variables declaration//GEN-END:variables

    @Override
    public void onConnected(ClientConnectionEvent evt) {
        this.setVisible(false);
        login = new Login(this);
        setLoginReference(login);
        login.setVisible(true);
    }

    @Override
    public void onValidationError(ValidationErrorEvent evt) {
        login.showValidationError(evt.getValidationError());
    }

    private void setButtonPlay(){
        if (btnPlay.getText().contains("Comenzar Partida")){
            return;
        }
        btnPlay.setText("Jugar");
    }
    
    private void showBeginGameError(String data){
        String error = Protocol.getBeginGameError(data);
        lbGameText.setText(error);
        lbGameText.setForeground(Color.red);
        lbGameText.setVisible(true);
    }
    
    private void initGame(){
        if (btnPlay.getText().contains("Comenzar Partida")){
            btnPlay.setEnabled(false);
            lbGameText.setVisible(false);
        }
        lbTitle.setText("Espera tu turno para atacar");
        
        sideBarBeforeGame.setVisible(false);
        sideBarForGame.setVisible(true);
    }
    
    @Override
    public void onDataReceived(DataReceivedEventEvent evt) {
        String data = evt.getData();
            System.out.println(data);
        if (Protocol.isLoginSuccess(data)){
            sessionStarted(data);
            login.dispose();
            return;
        }
        if (Protocol.isLoginError(data)){
            login.showValidationError(Protocol.getLoginError(data));
        }
        if (Protocol.isRegisterSuccess(data)){
            sessionStarted(data);
            register.dispose();
            return;
        }
        if (Protocol.isRegisterError(data)){
            register.showValidationError(Protocol.getRegisterError(data));
        }
        if (Protocol.isSessionId(data)){
            int sessionId = Protocol.getSessionId(data);
            try {
                client.setCommunication(sessionId);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return;
        }
        if (Protocol.isGameControl(data)){
            btnPlay.setText("Comenzar Partida");
            return;
        }
        if (Protocol.isGameReady(data)){
            initGame();
            return;
        }
        if (Protocol.isBeginGameControlTaken(data)){
            setButtonPlay();
            return;
        }
        if (Protocol.isBeginGameError(data)){
            showBeginGameError(data);
            return;
        }
        if (Protocol.isGameInProgressError(data)){
            showGameInProgressError(data);
            return;
        }
        if (Protocol.isLoginErrorGameInProgress(data)){
            login.showValidationError(Protocol.getLoginErrorGameInProgress(data));
        }
        if (Protocol.isRegisterErrorGameInProgress(data)){
            register.showValidationError(Protocol.getRegisterErrorGameInProgress(data));
        }
        if (Protocol.isTurn(data)){
            activateAttack();
        }
        if (Protocol.isDamageReceived(data)){
            receiveAttack(data);
        }
        if (Protocol.isPlayerDefeated(data)){
            gameOver();
        }
        if (Protocol.isPlayerVictory(data)){
            Victory();
        }
        if (Protocol.isTakedownsMessage(data)){
            txaGameActions.setText(txaGameActions.getText()+"\n"+Protocol.getTakedownsMessage(data));
        }
        if (Protocol.isGameFinished(data)){
            resetGame();
        }
    }
    
    private void resetGame(){
        btnPlay.setText("Crear Partida");
        btnPlay.setEnabled(true);
        txaGameActions.setText("");
        sideBarForGame.setVisible(false);
        sideBarBeforeGame.setVisible(true);
        
        /**/
        //createMyMap();
        //myMap.setVisible(true);
        //getContentPane().add(myMap, new AbsoluteConstraints(10, 50, (screenWidth / 4) * 3, (screenHeight / 10) * 9));
    }
    
    private void Victory(){
        lbTitle.setForeground(Color.BLUE);
        lbTitle.setText("Victory");
        btnAttack.setEnabled(false);
    }
    
    private void gameOver(){
        lbTitle.setForeground(Color.red);
        lbTitle.setText("Game Over");
        //lbTitle.setForeground(Color.red);
        btnAttack.setEnabled(false);
    }
    
    private void activateAttack(){
        lbTitle.setText("Es tu turno de atacar \u2694");
        btnAttack.setEnabled(true);
        myMap.setVisible(false);
        myMap.setEnabled(false);
        
        enemyMap.setVisible(true);
        enemyMap.setEnabled(true);
    }
    
    private void receiveAttack(String data){
        myMap.setVisible(true);
        enemyMap.setVisible(false);
        
        int row = Protocol.getRow(data); //(0 - 9)
        int col = Protocol.getCol(data); //(0 - 9)
        int index = row * 10 + col;
        
        JLabel ship = (JLabel)myMap.getComponent(index);
        ship.setOpaque(true);
        ship.setBackground(Color.red);
        ship.setIcon(null);
        
        
        txaGameActions.setText(txaGameActions.getText()+"\nEl barco en la coordenada: "+row+", "+col+" ha sido eliminado");
    }
    
    private void showGameInProgressError(String data){
        String error = Protocol.getGameInProgressError(data);
        btnPlay.setEnabled(true);
        lbGameText.setText(error);
        lbGameText.setForeground(Color.red);
        lbGameText.setVisible(true);
    }
    
    @Override
    public void onDisconnection(ClientDisconnectionEvent evt) {
        lbTitle.setText("No tienes conexion");
    }
}

/*
 * The MIT License
 *
 * Copyright 2016 Luke Dawkes.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package livebeansserver.gui;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import livebeanscommon.IServerWatcher;
import livebeanscommon.ISysOutWatcher;
import livebeansserver.LiveBeansServer;
import livebeansserver.util.ServerConstants.LogType;
import livebeansserver.util.ServerConstants.OutputView;
import livebeansserver.util.SystemOutputStream;

/**
 *
 * @author Luke Dawkes
 */
public class ServerGUI extends javax.swing.JFrame implements ISysOutWatcher, IServerWatcher
{

    private final PrintStream _systemOutStream;
    private final SystemOutputStream _systemOutFilteredStream;
    private final LiveBeansServer _serverInstance;
    private final Style _infoStyle, _warningStyle, _errorStyle;

    private final ArrayList<String> _informationList, _warningList, _errorList, _everythingList;

    private OutputView _currentOutputView;

    /**
     * Creates new form ServerGUI
     */
    public ServerGUI()
    {
        initComponents();

        _everythingList = new ArrayList<>();
        _informationList = new ArrayList<>();
        _warningList = new ArrayList<>();
        _errorList = new ArrayList<>();

        _infoStyle = txtServerConsole.addStyle("Info", null);
        _warningStyle = txtServerConsole.addStyle("Warning", null);
        _errorStyle = txtServerConsole.addStyle("Error", null);

        StyleConstants.setForeground(_infoStyle, new Color(0, 0, 140));
        StyleConstants.setForeground(_warningStyle, Color.orange);
        StyleConstants.setForeground(_errorStyle, Color.red);

        _currentOutputView = OutputView.EVERYTHING;

        _systemOutFilteredStream = new SystemOutputStream(new ByteArrayOutputStream());
        _systemOutStream = new PrintStream(_systemOutFilteredStream);

        // Pass through System.out calls to a custom class
        System.setOut(_systemOutStream);
        System.setErr(_systemOutStream);

        _serverInstance = LiveBeansServer.getInstance();
    }

    @Override
    public void onServerStatusChange()
    {
        switch (_serverInstance.getCurrentStatus())
        {
            case ONLINE:
                btnStartServer.setText("Stop Server");
                txtServerPort.setEnabled(false);

                lblServerStatus.setForeground(Color.green);
                lblServerStatus.setText("Server is ONLINE");
                break;
            case OFFLINE:
                btnStartServer.setText("Start Server");
                txtServerPort.setEnabled(true);

                lblServerStatus.setForeground(Color.black);
                lblServerStatus.setText("Server is OFFLINE");
                break;
            case ERROR:
                btnStartServer.setText("Start Server");
                txtServerPort.setEnabled(true);

                lblServerStatus.setForeground(Color.red);
                lblServerStatus.setText("Server encountered an error, see console for more info");
                break;
        }
    }

    public void postConstructor()
    {
        _systemOutFilteredStream.addWatcher(this);
        _serverInstance.addWatcher(this);
    }

    @Override
    public void onPrintLine(String newString)
    {
        try
        {
            StyledDocument paneDocument = txtServerConsole.getStyledDocument();
            Integer documentLength = paneDocument.getLength();

            Style newLineStyle = _infoStyle;
            boolean shouldAppend;

            if (newString.contains("[SERVER-WARNING]"))
            {
                _warningList.add(newString);
                newLineStyle = _warningStyle;

                shouldAppend = (_currentOutputView == OutputView.EVERYTHING
                                || _currentOutputView == OutputView.WARNINGS);
            }
            else if (newString.contains("[SERVER-ERROR]")
                     || newString.contains("Exception")
                     || newString.contains(".java")
                     || newString.contains("at java"))
            {
                _errorList.add(newString);
                newLineStyle = _errorStyle;
                shouldAppend = (_currentOutputView == OutputView.EVERYTHING
                                || _currentOutputView == OutputView.ERRORS);
            }
            else
            {
                _informationList.add(newString);

                shouldAppend = (_currentOutputView == OutputView.EVERYTHING
                                || _currentOutputView == OutputView.INFORMATION);
            }

            _everythingList.add(newString);

            if (shouldAppend)
            {
                paneDocument.insertString(documentLength, newString, newLineStyle);
            }
        }
        catch (BadLocationException ex)
        {
            Logger.getLogger(ServerGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        lblConsoleTitle = new javax.swing.JLabel();
        tabPane = new javax.swing.JTabbedPane();
        pnlConsole = new javax.swing.JPanel();
        btnClearConsole = new javax.swing.JButton();
        cboConsoleView = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtServerConsole = new javax.swing.JTextPane();
        lblView = new javax.swing.JLabel();
        pnlClientList = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        btnViewClientInfo = new javax.swing.JButton();
        btnKickClient = new javax.swing.JButton();
        pnlVersions = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btnStartServer = new javax.swing.JButton();
        txtServerPort = new javax.swing.JTextField();
        lblServerPort = new javax.swing.JLabel();
        lblServerStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        lblConsoleTitle.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblConsoleTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblConsoleTitle.setText("LiveBeans Server Console");

        tabPane.setToolTipText("");

        btnClearConsole.setText("Clear Console");
        btnClearConsole.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnClearConsoleActionPerformed(evt);
            }
        });

        cboConsoleView.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Everything", "Information Only", "Warnings Only", "Errors Only" }));
        cboConsoleView.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cboConsoleViewActionPerformed(evt);
            }
        });

        txtServerConsole.setEditable(false);
        jScrollPane1.setViewportView(txtServerConsole);

        lblView.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblView.setText("View:");

        javax.swing.GroupLayout pnlConsoleLayout = new javax.swing.GroupLayout(pnlConsole);
        pnlConsole.setLayout(pnlConsoleLayout);
        pnlConsoleLayout.setHorizontalGroup(
            pnlConsoleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlConsoleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlConsoleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(pnlConsoleLayout.createSequentialGroup()
                        .addComponent(btnClearConsole)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 279, Short.MAX_VALUE)
                        .addComponent(lblView)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboConsoleView, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnlConsoleLayout.setVerticalGroup(
            pnlConsoleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlConsoleLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlConsoleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClearConsole)
                    .addComponent(cboConsoleView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblView))
                .addContainerGap())
        );

        tabPane.addTab("Server Console", pnlConsole);

        jList1.setModel(new javax.swing.AbstractListModel<String>()
        {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(jList1);

        btnViewClientInfo.setText("Information");
        btnViewClientInfo.setToolTipText("");

        btnKickClient.setText("Kick");

        javax.swing.GroupLayout pnlClientListLayout = new javax.swing.GroupLayout(pnlClientList);
        pnlClientList.setLayout(pnlClientListLayout);
        pnlClientListLayout.setHorizontalGroup(
            pnlClientListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlClientListLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 473, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlClientListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnViewClientInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnKickClient, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlClientListLayout.setVerticalGroup(
            pnlClientListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlClientListLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlClientListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlClientListLayout.createSequentialGroup()
                        .addComponent(btnViewClientInfo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnKickClient)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE))
                .addContainerGap())
        );

        tabPane.addTab("Client List", pnlClientList);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("IN DEVELOPMENT");

        javax.swing.GroupLayout pnlVersionsLayout = new javax.swing.GroupLayout(pnlVersions);
        pnlVersions.setLayout(pnlVersionsLayout);
        pnlVersionsLayout.setHorizontalGroup(
            pnlVersionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlVersionsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlVersionsLayout.setVerticalGroup(
            pnlVersionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlVersionsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(63, Short.MAX_VALUE))
        );

        tabPane.addTab("Code Versions", pnlVersions);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnStartServer.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnStartServer.setText("Start Server");
        btnStartServer.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnStartServerActionPerformed(evt);
            }
        });

        txtServerPort.setText("1099");

        lblServerPort.setLabelFor(txtServerPort);
        lblServerPort.setText("Server Port:");

        lblServerStatus.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        lblServerStatus.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblServerStatus.setText("No Server Active");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblServerStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lblServerPort)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtServerPort, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnStartServer, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnStartServer)
                    .addComponent(txtServerPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblServerPort))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblServerStatus)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tabPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(lblConsoleTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(163, 163, 163))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblConsoleTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tabPane, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnStartServerActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnStartServerActionPerformed
    {//GEN-HEADEREND:event_btnStartServerActionPerformed
        String portString = txtServerPort.getText();

        if (portString.equals(""))
        {
            displayDialog("Missing Information", "You must enter a port number", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer portInteger;

        try
        {
            portInteger = Integer.parseInt(portString);

            if (portInteger < 1024 || portInteger > 65535)
            {
                displayDialog("Invalid Port", "The port must be between 1024 and 65535", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        catch (NumberFormatException ex)
        {
            displayDialog("Given port is not a number", "The port must be a number", JOptionPane.WARNING_MESSAGE);
            return;
        }

        _serverInstance.serverInit(portInteger);
    }//GEN-LAST:event_btnStartServerActionPerformed

    private void btnClearConsoleActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnClearConsoleActionPerformed
    {//GEN-HEADEREND:event_btnClearConsoleActionPerformed
        txtServerConsole.setText("");

        _everythingList.clear();
        _informationList.clear();
        _warningList.clear();
        _errorList.clear();
    }//GEN-LAST:event_btnClearConsoleActionPerformed

    private void cboConsoleViewActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cboConsoleViewActionPerformed
    {//GEN-HEADEREND:event_cboConsoleViewActionPerformed
        String selectedItem = cboConsoleView.getSelectedItem().toString();

        switch (selectedItem)
        {
            case "Everything":
                _currentOutputView = OutputView.EVERYTHING;
                break;
            case "Information Only":
                _currentOutputView = OutputView.INFORMATION;
                break;
            case "Warnings Only":
                _currentOutputView = OutputView.WARNINGS;
                break;
            case "Errors Only":
                _currentOutputView = OutputView.ERRORS;
                break;
        }

        refreshConsole();
    }//GEN-LAST:event_cboConsoleViewActionPerformed

    public void displayDialog(String title, String message, int messageType)
    {
        JOptionPane.showMessageDialog(new JFrame(), message, title, messageType);
    }

    private void refreshConsole()
    {
        txtServerConsole.setText("");

        Style styleToSet = _infoStyle;

        if (_currentOutputView == OutputView.EVERYTHING)
        {
            for (String consoleLine : _everythingList)
            {
                switch (getLineLogType(consoleLine))
                {
                    case WARNING:
                        styleToSet = _warningStyle;
                        break;
                    case ERROR:
                        styleToSet = _errorStyle;
                        break;
                }

                appendToConsole(consoleLine, styleToSet);
            }
        }
        else
        {
            ArrayList<String> arrayToIterate = new ArrayList<>();

            switch (_currentOutputView)
            {
                case INFORMATION:
                    arrayToIterate.clear();
                    arrayToIterate.addAll(_informationList);
                    break;
                case WARNINGS:
                    arrayToIterate.clear();
                    arrayToIterate.addAll(_warningList);
                    styleToSet = _warningStyle;
                    break;
                case ERRORS:
                    arrayToIterate.clear();
                    arrayToIterate.addAll(_errorList);
                    styleToSet = _errorStyle;
                    break;
            }

            for (String consoleLine : arrayToIterate)
            {
                appendToConsole(consoleLine, styleToSet);
            }
        }
    }

    private LogType getLineLogType(String consoleLine)
    {
        LogType logType = LogType.INFORMATION;

        if (consoleLine.contains("[SERVER-WARNING]"))
        {
            logType = LogType.WARNING;
        }
        else if (consoleLine.contains("[SERVER-ERROR]")
                 || consoleLine.contains("Exception")
                 || consoleLine.contains(".java")
                 || consoleLine.contains("at java"))
        {
            logType = LogType.ERROR;
        }

        return logType;
    }

    private void appendToConsole(String message, Style style)
    {
        StyledDocument paneDocument = txtServerConsole.getStyledDocument();
        Integer documentLength = paneDocument.getLength();

        try
        {
            paneDocument.insertString(documentLength, message, style);
        }
        catch (BadLocationException ex)
        {
            // The system will attempt to update the log
            // if it fails to update the log, I sense an
            // infinite loop somewhere...
            System.out.println("[SERVER-WARNING] Failed to update log.\r\n\tError: " + ex.getMessage());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try
        {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
            {
                if ("Windows".equals(info.getName()))
                {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex)
        {
            java.util.logging.Logger.getLogger(ServerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(()
                ->
                {
                    ServerGUI serverGUI = new ServerGUI();
                    serverGUI.postConstructor();
                    serverGUI.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClearConsole;
    private javax.swing.JButton btnKickClient;
    private javax.swing.JButton btnStartServer;
    private javax.swing.JButton btnViewClientInfo;
    private javax.swing.JComboBox<String> cboConsoleView;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList<String> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblConsoleTitle;
    private javax.swing.JLabel lblServerPort;
    private javax.swing.JLabel lblServerStatus;
    private javax.swing.JLabel lblView;
    private javax.swing.JPanel pnlClientList;
    private javax.swing.JPanel pnlConsole;
    private javax.swing.JPanel pnlVersions;
    private javax.swing.JTabbedPane tabPane;
    private javax.swing.JTextPane txtServerConsole;
    private javax.swing.JTextField txtServerPort;
    // End of variables declaration//GEN-END:variables
}

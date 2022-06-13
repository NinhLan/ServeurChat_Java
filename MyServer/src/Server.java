
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import javax.swing.DefaultListModel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ninht
 */
public class Server extends javax.swing.JFrame {
    ServerSocket ss;
    HashMap clientColl = new HashMap();
    FileOutputStream fos;
    DataOutputStream dos;
    

    /**
     * Creates new form Server
     */
    public Server() {
        initComponents();
        try {
            FileOutputStream fos = new FileOutputStream("C:\\Users\\ninht\\OneDrive\\Documents\\NetBeansProjects\\stock.txt", true);
            DataOutputStream dos = new DataOutputStream(fos);
            ss=new ServerSocket(2089);
            this.sStatus.setText("Server Started");
            new ClientAccept().start();
            dos.writeBytes("************************************** \n"+java.util.Calendar.getInstance().getTime()+"\n Server Started! \n**************************************\n\n");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }
    class ClientAccept extends Thread {
        public void run() {
            
            while (true) {
                try {
                    FileOutputStream fos = new FileOutputStream("C:\\Users\\ninht\\OneDrive\\Documents\\NetBeansProjects\\stock.txt", true);
                    DataOutputStream dos = new DataOutputStream(fos);
                    Socket s = ss.accept();
                    String i = new DataInputStream(s.getInputStream()).readUTF();
                    File file = null;
                    boolean isCreat = false;
                    
                    if (clientColl.containsKey(i)) {
                        DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                        dout.writeUTF("you are already registered!");
                        
                    } else {
                        
                        clientColl.put(i, s);
                        try {
                            file = new File("C:\\Users\\ninht\\OneDrive\\Documents\\NetBeansProjects\\History\\" + i + ".txt");
                            //Ở đây mình tạo file trong ổ C
                            isCreat = file.createNewFile();
                            if (isCreat) {
                                System.out.print("Da tao file id");
                            } else {
                                System.out.print("id da ton tai");
                            }
                            //file.delete();
                        } catch (Exception e) {
                            System.out.print(e);
                        }
                        msgBox.append(i + " joined! \n");
                        DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                        dout.writeUTF("");
                        dos.writeBytes(java.util.Calendar.getInstance().getTime()+"\n "+i+" joined! \n---------------------------------------- \n\n");
                        new MsgRead(s,i).start();
                        new PrepareClientList().start();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    class MsgRead extends Thread {
        Socket s;
        String ID;
        
        MsgRead(Socket s, String ID){
            this.s =s;
            this.ID =ID;
        }
        public void run() {
            while (!clientColl.isEmpty()) {
                try {
                    String i = new DataInputStream(s.getInputStream()).readUTF();
                    FileOutputStream fos = new FileOutputStream("C:\\Users\\ninht\\OneDrive\\Documents\\NetBeansProjects\\stock.txt", true);
                    DataOutputStream dos = new DataOutputStream(fos);
                    FileOutputStream fos1 = new FileOutputStream("C:\\Users\\ninht\\OneDrive\\Documents\\NetBeansProjects\\History\\"+ID+".txt", true);
                    DataOutputStream dos1 = new DataOutputStream(fos1);
                    File dir = new File("C:\\Users\\ninht\\OneDrive\\Documents\\NetBeansProjects\\History\\");
                    File[] txtFiles = dir.listFiles(new TxtFileFilter());
                    
                    if (i.equals("stop")) {
                        clientColl.remove(ID);
                        msgBox.append(ID + ": removed! \n");
                        new PrepareClientList().start();
                        Set k = clientColl.keySet();
                        Iterator itr = k.iterator();
                        
                        while (itr.hasNext()) {
                            String key = (String) itr.next();
                            if (!key.equalsIgnoreCase(ID)) {
                                try {
                                    new DataOutputStream(((Socket) clientColl.get(key)).getOutputStream()).writeUTF("< " + ID + " : disconnected! >");
                                    dos.writeBytes(java.util.Calendar.getInstance().getTime()+": \n< " + ID + " : disconnected! > \n \n");
                                    dos1.writeBytes(java.util.Calendar.getInstance().getTime()+": \n< disconnected! > \n \n");
                                    for (File txtFile : txtFiles) {
                                        String a = txtFile.getAbsolutePath();
                                        FileOutputStream fos3 = new FileOutputStream(a, true);
                                        DataOutputStream dos3 = new DataOutputStream(fos3);
                                        dos3.writeBytes(java.util.Calendar.getInstance().getTime()+": \n< " + ID + " : disconnected! > \n \n");
                                        fos3.close();
                                        dos3.close();
                                        }
                                } catch (Exception ex) {
                                    clientColl.remove(key);
                                    msgBox.append(key + ": removed! \n");
                                    new PrepareClientList().start();

                                }
                            }
                        }

                    } else if (i.contains("#4344554@@@@@67667@@")) {
                        i = i.substring(20);
                        StringTokenizer st = new StringTokenizer(i, ":");
                        String id = st.nextToken();
                        i = st.nextToken();
                        
                        FileOutputStream fos2 = new FileOutputStream("C:\\Users\\ninht\\OneDrive\\Documents\\NetBeansProjects\\History\\"+id+".txt", true);
                        DataOutputStream dos2 = new DataOutputStream(fos2);
                        try {
                            new DataOutputStream(((Socket) clientColl.get(id)).getOutputStream()).writeUTF("< "+ID+" to "+id+" > "+i);
                            dos.writeBytes(java.util.Calendar.getInstance().getTime()+": \n< "+ID+" to "+id+" > "+i+"\n \n");
                            dos1.writeBytes(java.util.Calendar.getInstance().getTime()+": \n< You to "+id+" > "+i+"\n \n");
                            dos2.writeBytes(java.util.Calendar.getInstance().getTime()+": \n< "+ID+" to you > "+i+"\n \n");
                            fos2.close();
                            dos2.close();
                        } catch (Exception ex) {
                            clientColl.remove(id);
                            msgBox.append(id + ": removed\n");
                            new PrepareClientList().start();

                        }
                    }
                    else{
                        Set k = clientColl.keySet();
                        Iterator itr = k.iterator();
                        dos.writeBytes(java.util.Calendar.getInstance().getTime()+": \n< "+ID+" to All > "+i+"\n \n");
                        dos1.writeBytes(java.util.Calendar.getInstance().getTime()+": \n< You to All > "+i+"\n \n");
                        while (itr.hasNext()) {
                            String key = (String) itr.next();
                            if (!key.equalsIgnoreCase(ID)) {
                                try {
                                    new DataOutputStream(((Socket) clientColl.get(key)).getOutputStream()).writeUTF("< "+ID+" to All > "+i);
                                    for (File txtFile : txtFiles) {
                                        String a = txtFile.getAbsolutePath();
                                        FileOutputStream fos3 = new FileOutputStream(a, true);
                                        DataOutputStream dos3 = new DataOutputStream(fos3);
                                        dos3.writeBytes(java.util.Calendar.getInstance().getTime()+": \n\"< "+ID+" to All > "+i+"\n \n");
                                        fos3.close();
                                        dos3.close();
                                        }
                                    
                                    
                                } catch (Exception ex) {
                                    clientColl.remove(key);
                                    msgBox.append(key + ": removed!");
                                    new PrepareClientList().start();

                                }
                            }
                        } 
                        
                        
                    }
                    fos.close();
                    dos.close();
                    fos1.close();
                    dos1.close();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    class TxtFileFilter implements FileFilter {
	@Override
	public boolean accept(File pathname) {

		if (!pathname.isFile()) {
			return false;
		}

		if (pathname.getAbsolutePath().endsWith(".txt")) {
			return true;
		}

		return false;
	}

}
    class PrepareClientList extends Thread {
        public void run(){
            try {
                String ids = "";
                Set k = clientColl.keySet();
                Iterator itr = k.iterator();
                while (itr.hasNext()) {
                    String key = (String) itr.next();
                    ids += key + ",";
                }
                if (ids.length() != 0) {
                    ids = ids.substring(0, ids.length() - 1);

                }
                itr = k.iterator();
                while (itr.hasNext()) {
                    String key = (String) itr.next();
                    try {
                        new DataOutputStream(((Socket) clientColl.get(key)).getOutputStream()).writeUTF(":;.,/" + ids);
                    } catch (Exception ex) {
                        clientColl.remove(key);
                        msgBox.append(key + ": removed");
                    }
                }     
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        sStatus = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        msgBox = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MyServer");

        jPanel1.setBackground(new java.awt.Color(222, 172, 172));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Server Status: ");

        sStatus.setText("......................");

        msgBox.setColumns(20);
        msgBox.setRows(5);
        jScrollPane1.setViewportView(msgBox);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 326, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(sStatus))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(48, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Server().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea msgBox;
    private javax.swing.JLabel sStatus;
    // End of variables declaration//GEN-END:variables
   
}



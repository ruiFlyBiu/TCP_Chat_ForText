package messenger;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Client extends JFrame{
	   private Socket socket;
	    private DataInputStream datainputstream;
	    private DataOutputStream dataoutputstream;
	    private boolean ClientStatus;
	    private boolean ServerStatus;
	    private javax.swing.JTextArea Chat;
	    private javax.swing.JButton ConnectButton;
	    private javax.swing.JTextField ConnectionStatus;
	    private javax.swing.JTextField IP_Port;
	    private javax.swing.JLabel Label;
	    private javax.swing.JButton SendButton;
	    private javax.swing.JTextArea conversation;
	    private javax.swing.JScrollPane jScrollPane1;
	    private javax.swing.JScrollPane jScrollPane2;
	    
	    public Client() {
	        initComponents();
	        this.setBounds(700,100,520,420);
	        ClientStatus = false;	//标志Client是否上次发送
	        ServerStatus = false;	//标志Server是否上次发送
	    }

	    private void Initialize()
	    {
	        try
	        {
	            datainputstream = new DataInputStream(socket.getInputStream());
	            dataoutputstream = new DataOutputStream(socket.getOutputStream());
	        }
	        catch(Exception ex)
	        {
	            ConnectionStatus.setText("                         Not Connected");
	        }
	    }

	    private String GetTime()	//获得发送时间
	    {
	        Calendar cal = Calendar.getInstance();
	        cal.getTime();
	        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");	//格式为时：分：秒
	        return sdf.format(cal.getTime());
	    }    

	
	    @SuppressWarnings("unchecked")
	    private void initComponents() {
	        Label = new javax.swing.JLabel();
	        IP_Port = new javax.swing.JTextField();	//请求连接的端口号
	        ConnectButton = new javax.swing.JButton();	//连接按钮
	        jScrollPane2 = new javax.swing.JScrollPane();	
	        conversation = new javax.swing.JTextArea();		//聊天记录框
	        jScrollPane1 = new javax.swing.JScrollPane();
	        Chat = new javax.swing.JTextArea();	//输入文本框
	        SendButton = new javax.swing.JButton();	//发送按钮
	        ConnectionStatus = new javax.swing.JTextField();	//请求连接的Socket

	        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
	        setTitle("Client");
	        setResizable(false);
	        getContentPane().setLayout(null);
	        
	        //添加Socket连接区
	        Label.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); 
	        Label.setText("IP & Port:");
	        getContentPane().add(Label);
	        Label.setBounds(40, 40, 130, 30);

	        IP_Port.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
	        IP_Port.setText("localhost:1234");
	        getContentPane().add(IP_Port);
	        IP_Port.setBounds(130, 40, 180, 30);

	        ConnectButton.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
	        ConnectButton.setText("Connect");
	        ConnectButton.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                ConnectButtonActionPerformed(evt);
	            }
	        });
	        getContentPane().add(ConnectButton);
	        ConnectButton.setBounds(340, 40, 130, 30);
	        
	        //添加聊天框
	        conversation.setEditable(false);	//设置聊天记录框只读不写
	        conversation.setColumns(20);
	        conversation.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
	        conversation.setRows(5);
	        conversation.setEnabled(false);
	        jScrollPane2.setViewportView(conversation);	//将聊天框置于滚动面板中

	        getContentPane().add(jScrollPane2);
	        jScrollPane2.setBounds(10, 80, 490, 250);
	        
	        //添加文本发送区
	        Chat.setColumns(20);
	        Chat.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
	        Chat.setRows(1);
	        jScrollPane1.setViewportView(Chat);

	        getContentPane().add(jScrollPane1);
	        jScrollPane1.setBounds(10, 340, 400, 40);

	        SendButton.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
	        SendButton.setText("Send");
	        SendButton.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                SendButtonActionPerformed(evt);
	            }
	        });
	        getContentPane().add(SendButton);
	        SendButton.setBounds(430, 340, 60, 40);

	        ConnectionStatus.setEditable(false);
	        ConnectionStatus.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
	        ConnectionStatus.setText("                    Not Connected");	//使用JTextfield,方便切换为“Connected"
	        ConnectionStatus.setEnabled(false);
//	        ConnectionStatus.addActionListener(new java.awt.event.ActionListener() {
//	            public void actionPerformed(java.awt.event.ActionEvent evt) {
//	                ConnectionStatusActionPerformed(evt);
//	            }
//	        });
	        getContentPane().add(ConnectionStatus);
	        ConnectionStatus.setBounds(110, 10, 240, 20);

	        pack();	//
	    }
	    /*
	     * 监听ConnectButton，尝试与Server取得通信
	     */
	    private void ConnectButtonActionPerformed(java.awt.event.ActionEvent evt) {
	        new Thread(new Runnable()
	        {
	            @Override
	            public void run()
	            {
	                String[] temp = IP_Port.getText().split(":");
	                try
	                {
	                    socket = new Socket(temp[0],Integer.parseInt(temp[1]));
	                    Initialize();	//建立socket的输入输出流
	                    ConnectionStatus.setText("                      Connected");
	                    StartRecieving();	//随时准备接收Server端的消息
	                }
	                catch(Exception ex)
	                {
	                    ConnectionStatus.setText("                    Not Connected");	//捕捉异常，连接失败
	                }
	            }
	        }).start();
	    }
	    /*
	     * 监听SendButton按钮，发送文本输入dataoutputstream，打印发送消息
	     */
	    private void SendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SendButtonActionPerformed
	        new Thread(new Runnable()
	        {
	            @Override
	            public void run()
	            {
	                try
	                {
	                    dataoutputstream.writeUTF(Chat.getText());
	                    dataoutputstream.flush();
	                    if(ClientStatus == false)
	                    {
	                        conversation.append("\n Client: \n [" + GetTime() +"]  " + Chat.getText());
	                        ClientStatus = true;
	                    }
	                    else
	                    {
	                        conversation.append("\n [" + GetTime() + "]  " + Chat.getText());
	                    }
	                    ServerStatus = false;
	                    Chat.setText("");
	                }
	                catch(IOException ex)
	                {
	                }
	            }
	        }).start();
	    }

 	    /*
	     * 接收Server发送的文本数据，打印接送消息
	     */
	    private void StartRecieving()
	    {
	        new Thread(new Runnable()
	        {
	            @Override
	            public void run()
	            {
	                String text;
	                while(true)
	                {
	                    try
	                    {
	                        text = datainputstream.readUTF();
	                        ClientStatus = false;
	                        if(ServerStatus == false)
	                        {
	                           conversation.append("\n Server: \n [" + GetTime() + "]  " + text);
	                           ServerStatus = true;
	                        }
	                        else
	                        {
	                           conversation.append("\n [" + GetTime() + "]  " + text);
	                        }
	                    }
	                    catch (IOException ex)
	                    {
	                    }
	                }
	            }
	        }).start();
	    }
}

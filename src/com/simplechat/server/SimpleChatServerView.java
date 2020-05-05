package com.simplechat.server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;


public class SimpleChatServerView extends JFrame implements ActionListener, SimpleChatService.OnSocketAcceptListener {
    private JButton btnOpen;
    private JButton btnStop;
    private JTextArea tfLogInfo;
    private JScrollPane scrollPane;

    JPanel jp1;

    private JLabel label;
    private SimpleChatService service = null;

    public SimpleChatServerView(SimpleChatService service) {
        this.service = service;
        this.service.setOnAcceptListener(this);
        initView();
    }

    private void initView() {
        jp1 = new JPanel();
        btnOpen = new JButton("打开服务器");
        btnStop = new JButton("关闭服务器");
        btnStop.setEnabled(false);
        btnOpen.addActionListener(this);
        btnStop.addActionListener(this);
        jp1.add(btnOpen);
        jp1.add(btnStop);
        jp1.setLayout(new FlowLayout(FlowLayout.CENTER));

        tfLogInfo = new JTextArea(20, 20);
        tfLogInfo.setEditable(false);
        scrollPane = new JScrollPane(tfLogInfo);

        label = new JLabel("服务器停止工作");

        add(jp1, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(label, BorderLayout.SOUTH);
//        add(btnOpen);
//        add(btnStop);
        setTitle("服务器");
//        setLayout(new GridLayout(3, 1, 0, 10));

        setSize(300, 300);
        setLocation(450, 150);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnOpen) {
            open();
        } else {
            stop();
        }
    }

    private void open() {
//		service = new SimpleChatService();
        service.startup();
        label.setText("服务器正在运行");
        btnOpen.setEnabled(false);
        btnStop.setEnabled(true);
    }

    private void stop() {
        label.setText("服务器已关闭");
        btnOpen.setEnabled(true);
        btnStop.setEnabled(false);
        service.shutdown();
    }

    @Override
    public void onSocketAccept(Socket socket) {
        tfLogInfo.append("客户端" + socket.getInetAddress() + "已连接");
        tfLogInfo.append("\r\n");

//		JOptionPane.showMessageDialog(
//				this,
//				"客户端连接端口",
//				"Tip",
//				JOptionPane.INFORMATION_MESSAGE);
    }
}


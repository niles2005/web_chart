package com.xtwsoft.webchart;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class TestImage extends JFrame {
    private static boolean packFrame = false;
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        TestImage frame = new TestImage();
        //Validate frames that have preset sizes
        //Pack frames that have useful preferred size info, e.g. from their layout
        if (packFrame) {
            frame.pack();
        }
        else {
            frame.validate();
        }
        //Center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        frame.setVisible(true);
    }
	
    private JPanel contentPane;
    private BorderLayout borderLayout1 = new BorderLayout();
    //Construct the frame
    public TestImage() {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try {
            jbInit();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    //Component initialization
    private void jbInit() throws Exception  {
        //setIconImage(Toolkit.getDefaultToolkit().createImage(Frame2.class.getResource("[Your Icon]")));
        contentPane = (JPanel) this.getContentPane();
        contentPane.setLayout(borderLayout1);
        this.setBackground(Color.white);
        this.setSize(new Dimension(500, 400));
        this.setTitle("Visitors Tester");
        final JComboBox comboBox = new JComboBox();
        comboBox.addItemListener(new ItemListener() {
        	public void itemStateChanged(ItemEvent e) {
        		if ((e.getStateChange() == ItemEvent.SELECTED)) {
        			UrlItem selectUrlItem = (UrlItem)comboBox.getSelectedItem();
        			m_panel.selectUrlItem(selectUrlItem);
        		}
        	}
        });
        contentPane.add(comboBox,BorderLayout.SOUTH);

        JButton btn = new JButton("refresh");
        contentPane.add(btn,BorderLayout.NORTH);
        btn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent event) {
        		m_panel.doRefresh();
        	}
        });
        contentPane.add(m_panel,BorderLayout.CENTER);
        
        ArrayList urlItemList = new ArrayList();
        urlItemList.add(new UrlItem("http://127.0.0.1/%E4%BA%A4%E9%80%9A%E7%8A%B6%E5%86%B5%E9%A2%84%E6%B5%8B%E9%A2%84%E6%8A%A5/%E8%B7%AF%E6%AE%B5%E9%A2%84%E6%B5%8B%E5%88%86%E6%9E%90.page/trafficIncidentStatistics.action.jsonx"));
        urlItemList.add(new UrlItem("http://127.0.0.1/%E4%BA%A4%E9%80%9A%E7%8A%B6%E6%80%81%E7%BB%BC%E5%90%88%E7%9B%91%E6%B5%8B/%E9%87%8D%E7%82%B9%E8%B7%AF%E6%AE%B5%E7%BB%BC%E5%90%88%E6%80%81%E5%8A%BF.page/speRoadStatus.jsonx"));
        urlItemList.add(new UrlItem("http://127.0.0.1/%E4%BA%A4%E9%80%9A%E7%8A%B6%E5%86%B5%E9%A2%84%E6%B5%8B%E9%A2%84%E6%8A%A5/%E8%B7%AF%E6%AE%B5%E9%A2%84%E6%B5%8B%E5%88%86%E6%9E%90.page/hourRadar.jsonx"));
        urlItemList.add(new UrlItem("http://localhost/%E4%BA%A4%E9%80%9A%E7%8A%B6%E6%80%81%E7%BB%BC%E5%90%88%E7%9B%91%E6%B5%8B/%E8%B7%AF%E7%BD%91%E4%BA%A4%E9%80%9A%E7%BB%BC%E5%90%88%E6%80%81%E5%8A%BF.page/loop.jsonx"));
        urlItemList.add(new UrlItem("http://127.0.0.1/%E9%A6%96%E9%A1%B5.page/weekCircleChart.jsonx"));
        urlItemList.add(new UrlItem("http://127.0.0.1/%E4%BA%A4%E9%80%9A%E7%8A%B6%E6%80%81%E7%BB%BC%E5%90%88%E7%9B%91%E6%B5%8B/%E8%B7%AF%E7%BD%91%E4%BA%A4%E9%80%9A%E7%BB%BC%E5%90%88%E6%80%81%E5%8A%BF.page/pie-1.jsonx"));
        urlItemList.add(new UrlItem("http://127.0.0.1/%E4%BA%A4%E9%80%9A%E7%8A%B6%E6%80%81%E7%BB%BC%E5%90%88%E7%9B%91%E6%B5%8B/%E8%B7%AF%E7%BD%91%E4%BA%A4%E9%80%9A%E7%BB%BC%E5%90%88%E6%80%81%E5%8A%BF.page/roadnetBottleneckChart.jsonx"));
        
        urlItemList.add(new UrlItem("http://127.0.0.1/%E4%BA%A4%E9%80%9A%E7%8A%B6%E6%80%81%E7%BB%BC%E5%90%88%E7%9B%91%E6%B5%8B/%E9%87%8D%E7%82%B9%E8%B7%AF%E6%AE%B5%E7%BB%BC%E5%90%88%E6%80%81%E5%8A%BF.page/speRoadnetService.jsonx"));
        for(int i=0;i<urlItemList.size();i++) {
        	comboBox.addItem(urlItemList.get(i));
        }
    }
    
    private TestPanel m_panel = new TestPanel();
    //Overridden so we can exit when window is closed
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            System.exit(0);
        } else if(e.getID() == WindowEvent.WINDOW_ACTIVATED) {
        	this.repaint();
        }
    }
    
}

class UrlItem {
	String name = null;
	String url = null;
	public UrlItem(String name,String url) {
		this.name = name;
		this.url = url;
	}
	
	public UrlItem(String url) {
		this.url = url;
		int pos = url.lastIndexOf("/");
		if(pos != -1) {
			name = url.substring(pos + 1);
		} else {
			name = url;
		}
	}
	
	public String getUrl() {
		return url;
	}
	
	public String toString() {
		return name;
	}
}
//运行时需启动wuproject
class TestPanel extends JPanel {
	private JSONObject chartData = null;
	public TestPanel() {
		doRefresh();
	}
	
	private UrlItem m_item = null;
	public void selectUrlItem(UrlItem item) {
		m_item = item;
		doRefresh();
	}
	
	public void doRefresh() {
		if(m_item == null) {
			return;
		}
		String jsonConent = this.getJSONContent();
		System.err.println(jsonConent);
		if(jsonConent != null) {
			jsonConent = jsonConent.trim();
			if(jsonConent.startsWith("[")) {
				System.err.println("error json for array!");
			} else {
				chartData = JSON.parseObject(jsonConent);
			}
		}
		this.repaint();
	}
	
	private String getJSONContent() {
		try {
			if(m_item == null) {
				return null;
			}
			URL url = new URL(m_item.getUrl());
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(),"UTF-8"));
			StringBuffer strBuff = new StringBuffer();
			String line = reader.readLine();
			while(line != null) {
				strBuff.append(line);
				strBuff.append("\r\n");
				line = reader.readLine();
			}
			reader.close();
			return strBuff.toString();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public void paint(Graphics g) {
		if(chartData != null) {
			Graphics2D g2 = (Graphics2D)g;
			
			g2.setColor(Color.LIGHT_GRAY);
			Dimension size = this.getSize();
			g2.fillRect(0, 0, size.width, size.height);
			g2.translate(10, 10);
			ImageChartManager.getInstance().drawChart(g2,chartData,size.width - 20, size.height - 20);
		}
	}
}

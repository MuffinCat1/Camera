package com.muffincat.webcam;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import javazoom.jl.player.Player;


public class App extends JFrame implements Callable<Boolean>{
	
	private static final long serialVersionUID = -75997589526020136L;
	static JFrame frame = null;
	static Webcam webcam = null;
	
	public static void mirrorImage(String pictureName, File home) throws Exception {
	    File file = new File(String.format("Pictures/%s.jpg", pictureName));
	    BufferedImage value = ImageIO.read(file);

	    int height = value.getHeight();
	    int width = value.getWidth();

	    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	    for(int j = 0; j < height; j++){
	       
	    	for(int i = 0, w = width - 1; i < width; i++, w--){
	         
	    		int p = value.getRGB(i, j);

	    		img.setRGB(w, j, p);
	       }
	    }
	    
	    ImageIO.write(img, "JPG", new File(String.format("%s\\Pictures\\%s.jpg", home, pictureName)));
	}
	
	public static void frameWindowAdapterData() throws Exception {
		int answer = 0;
		boolean close = true;
		
		File warningSound = new File(String.format("%s\\Media\\Windows Ding.wav", System.getenv("systemroot")));
		
		Clip c = AudioSystem.getClip();
		c.open(AudioSystem.getAudioInputStream(warningSound));
		
		FloatControl volume = (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);
		volume.setValue(-1 * 20);
		
		c.start();
		answer = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit? ", "Exit", JOptionPane.YES_NO_OPTION);
		
		if (answer == JOptionPane.NO_OPTION) {
			 close = false;
		} else if(answer == JOptionPane.CANCEL_OPTION) {
			close = false;
		} else if(answer == JOptionPane.DEFAULT_OPTION){
			close = false;
		} else {
			close = true;
		}
		
		if(close) {
			System.exit(0);
		}
	}
	
	public static void buttonMouseAdapterData(Player p, String pictureName, File home, JFrame frame) throws Exception {
		if(webcam.getImage() == null) {
			JOptionPane.showMessageDialog(null, String.format("there are problems with camera \"%s\"", webcam, JOptionPane.ERROR_MESSAGE));
			System.exit(0);
		}
		p.play();
		
		File pictureFile = new File(String.format("Pictures/%s.jpg", pictureName));
		ImageIO.write(webcam.getImage(), "JPG", pictureFile);
			
		mirrorImage(pictureName, home);
	    
		pictureFile.delete();
		frame.dispose();
	    JOptionPane.showMessageDialog(null, String.format("your picture has been saved as %s.jpg", pictureName));
	    System.exit(0);
	}
	
	public static void resolutionSetup(String pictureMode) {
		if(pictureMode == null) {
		    System.exit(0);
		} else if((pictureMode != null && ("".equals(pictureMode)))) {
			webcam.setViewSize(WebcamResolution.HD.getSize());
		} else {
			switch(pictureMode) {
				case "hd": case "HD": webcam.setViewSize(WebcamResolution.HD.getSize()); break;
				case "hdp": case "HDP": webcam.setViewSize(WebcamResolution.HDP.getSize()); break;
				case "qhd": case "QHD": webcam.setViewSize(WebcamResolution.QHD.getSize()); break;
				case "fhd": case "FHD": webcam.setViewSize(WebcamResolution.FHD.getSize()); break;
				case "fhdp": case "FHDP": webcam.setViewSize(WebcamResolution.FHDP.getSize()); break;
				default: {
					JOptionPane.showMessageDialog(null, String.format("the resolution mode \"%s\" is not valid please write a valid mode", pictureMode)); 
					System.exit(0);
				}
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		System.console();
		
		UIManager.setLookAndFeel("com.jtattoo.plaf.noire.NoireLookAndFeel");

		final File home = FileSystemView.getFileSystemView().getHomeDirectory();
		
		Dimension[] nonStandardResolutions = new Dimension[] {
				WebcamResolution.HD.getSize(),
				WebcamResolution.HDP.getSize(),
				WebcamResolution.QHD.getSize(),
				WebcamResolution.FHD.getSize(),
				WebcamResolution.FHDP.getSize()
		};

		if(Webcam.getDefault() == null) {
			JOptionPane.showMessageDialog(null, "Please Connect a working webcamera"); 
			System.exit(0);
		}
		
		File file = new File("Pictures");
		
		if (!file.exists()) {
			if(file.mkdir()) {
				JOptionPane.showMessageDialog(null, "The Pictures Folder was created succsefully"); 
			} else {
				JOptionPane.showMessageDialog(null, "The Pictures Folder wasn't created succsefully, please create the folder manually"); 
				System.exit(0);
			}
		}
		
		File file3 = new File(String.format("%s\\Pictures", home));
		
		if (!file3.exists()) {
			if(file3.mkdir()) {
				JOptionPane.showMessageDialog(null, "The Pictures Folder in Desktop was created succsefully"); 
			} else {
				JOptionPane.showMessageDialog(null, "The Pictures Folder in Desktop wasn't created succsefully, please create the folder manually"); 
				System.exit(0);
			}
		}
		 
		
		final String pictureName = JOptionPane.showInputDialog("what do you want to call your picture? ");
		
		if(pictureName == null) {
		    System.exit(0);
		} else if((pictureName != null && ("".equals(pictureName)))) {
			JOptionPane.showMessageDialog(null, "not a valid name");
			System.exit(0);
		}
		
		webcam = Webcam.getDefault();
		webcam.setCustomViewSizes(nonStandardResolutions);
		
		String pictureMode = JOptionPane.showInputDialog("what resulotion your picture is? (HD, HDP, QHD, FHD, FHDP) ");
		
		resolutionSetup(pictureMode);
		
		
		UIManager.put("Button.font", new Font("Arial", Font.ITALIC, 25));
		
		final Player p = new Player(AudioSystem.getAudioInputStream(new URL("https://www.soundjay.com/mechanical/sounds/camera-shutter-click-01.mp3")));
		
		JButton btn = new JButton("take a picture");
		WebcamPanel panel = new WebcamPanel(webcam);
		
		frame = new JFrame("WebCamera");

		btn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					buttonMouseAdapterData(p, pictureName, home, frame);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		UIManager.put("Button.font", new Font("Arial", Font.PLAIN, 15));
		
		panel.setFPSLimit(60f);
		panel.setFPSDisplayed(true);
		panel.setDisplayDebugInfo(true);
		panel.setImageSizeDisplayed(true);
		panel.setMirrored(true);
		panel.add(btn);
		
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setSize(webcam.getViewSize());
		frame.setLocationRelativeTo(null);
		frame.setVisible(false);
		frame.add(panel);
		
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					frameWindowAdapterData();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		ExecutorService pool = Executors.newFixedThreadPool(1);
		pool.submit(new App()).get();
	}
	
	@Override
	public Boolean call() throws Exception {
		JPanel loadPanel = new JPanel();
		JFrame loadframe = new JFrame("Loading...");
		JLabel loadText = new JLabel("Test", SwingConstants.CENTER);
		
		loadText.setText("Loading Dont Exit");
		
		loadPanel.add(loadText);
		
		loadframe.setResizable(true);
		loadframe.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		loadframe.setSize(webcam.getViewSize());
		loadframe.setSize(400, 70);
		loadframe.setLocationRelativeTo(null);
		loadframe.setVisible(true);
		loadframe.add(loadPanel);
    	
		Thread.sleep(1650);
		loadframe.dispose();
    	frame.setVisible(true);
        return true;
	}
}
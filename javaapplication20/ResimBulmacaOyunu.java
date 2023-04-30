
package javaapplication20;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

class Buton extends JButton{
	private static final long serialVersionUID = 1L;
	boolean ButonSon;
	public Buton(boolean ButonSon) {
		this.ButonSon = ButonSon;
		setBorder(null);
		init();
	}
	
	public Buton(Image IkonResmi, boolean ButonSon) {
		setIcon(new ImageIcon(IkonResmi));
		this.ButonSon = ButonSon;
		setBorder(null);
		init();
	}
	
	private void init(){
		if(!ButonSon) {
			BorderFactory.createLineBorder(Color.GRAY);
			
			addMouseListener(new MouseAdapter() {
                                @Override
				public void mouseEntered(MouseEvent e) {
					setBorder(BorderFactory.createLineBorder(Color.YELLOW));
				}
                                @Override
				public void mouseExited(MouseEvent e) {
					setBorder(BorderFactory.createLineBorder(Color.GRAY));
				}
			
			});
		}
		else {
			BorderFactory.createLineBorder(Color.CYAN);
			addMouseListener(new MouseAdapter() {
                                @Override
				public void mouseEntered(MouseEvent e) {
					setBorder(BorderFactory.createLineBorder(Color.GREEN));
				}
				
				public void mouseExited(MouseEvent e) {
					setBorder(BorderFactory.createLineBorder(Color.CYAN));
				}
			});
		}
	}
}

public class ResimBulmacaOyunu extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	JPanel UstCerceve, Cerceve, AltCerceve;
	JLabel UstBaslık;
	JButton AnaMenu, Cozum, ZamanButonu, TıklamaButonu;
	BufferedImage Anaresim, Resim;
	Image OyunResmi;
	int Genislik, Yukseklik;
	long beforeTime;
	String timeTaken;
	boolean timeFlag;
	int Clickler;
        int picture;

	Scanner sc = new Scanner(System.in);
	Image IkonResmi = Toolkit.getDefaultToolkit().getImage("src\\resources\\puzzle-icon.png").getScaledInstance(80, 80, Image.SCALE_SMOOTH);
	List<Buton> Butonlar;
	List<Point> solution;
	final int butonSayisi = 12;
	final int istenilenGenislik = 300;
	
	public ResimBulmacaOyunu() {
		System.out.println("""
                                   Hangi resimle oynamak istediginizi secin. 
                                   1. Sebnem Ferah
                                   2. Sebnem Ferah 2 
                                   3. Sebnem Ferah 3""");
		int SecilenResim = sc.nextInt();
		
		switch(SecilenResim) {
		case 1:
			picture = 1;
			break;
		case 2:
			picture = 2;
			break;
		case 3:
			picture = 3;
			break;
		default:
			System.out.println("Geçersiz seçim. Otomatik olarak 1.seçenek seçildi.");
			picture = 1;
		}

		solution = new ArrayList<>();			
		solution.add(new Point(0, 0));
		solution.add(new Point(0, 1));
		solution.add(new Point(0, 2));
		solution.add(new Point(1, 0));
		solution.add(new Point(1, 1));
		solution.add(new Point(1, 2));
		solution.add(new Point(2, 0));
		solution.add(new Point(2, 1));
		solution.add(new Point(2, 2));
		solution.add(new Point(3, 0));
		solution.add(new Point(3, 1));
		solution.add(new Point(3, 2));
		
		Butonlar = new ArrayList<>();
		
		if(Cerceve != null) {				
			Cerceve.removeAll();
		}
		Cerceve = new JPanel();
		Cerceve.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		Cerceve.setLayout(new GridLayout(4, 3, 0, 0));
		
		try {
			Anaresim = ResimYukleyici();
			int h = YeniYukseklikEldeEtme(Anaresim.getWidth(), Anaresim.getHeight());			
			Resim = resizeResim(Anaresim, istenilenGenislik, h, BufferedImage.TYPE_INT_ARGB);			
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		Genislik = Resim.getWidth(null);			
		Yukseklik = Resim.getHeight(null);
		Buton lastButton = null;
		
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 3; j++) {
				OyunResmi = createImage(new FilteredImageSource(Resim.getSource(),
						new CropImageFilter(j * Genislik  / 3, i * Yukseklik / 4, Genislik / 3, Yukseklik / 4)));
				
				if(i == 3 && j == 2) {			
					lastButton = new Buton(OyunResmi, true);
					lastButton.putClientProperty("pozisyon", new Point(i, j));
				}
				else {
					Buton button = new Buton(OyunResmi, false);
					button.putClientProperty("pozisyon", new Point(i, j));
					Butonlar.add(button);
				}
			}
		}
		
		Collections.shuffle(Butonlar);		
		Butonlar.add(lastButton);			
		for(int i = 0; i < butonSayisi; i++) {
			JButton button = Butonlar.get(i);
			Cerceve.add(button);
			button.addActionListener(new ClickAction());
		}
		add(Cerceve, BorderLayout.CENTER);
		 
		AnaMenu = new JButton("Menü");
		AnaMenu.addActionListener(this);
		Cozum = new JButton("Çözüm");
		Cozum.addActionListener(this);
		ZamanButonu = new JButton("00 : 00");
		TıklamaButonu = new JButton("0");
		TıklamaButonu.setToolTipText("Toplam Tıklama");
                UstBaslık = new JLabel("Sağ en alttaki dikdörtgenle hareket edebilirsiniz.");
		UstCerceve = new JPanel();
		UstCerceve.add(UstBaslık);
		AltCerceve = new JPanel();
		AltCerceve.add(AnaMenu);  AltCerceve.add(Cozum);  AltCerceve.add(ZamanButonu);  AltCerceve.add(TıklamaButonu);
		
		add(UstCerceve, BorderLayout.NORTH);
		add(Cerceve, BorderLayout.CENTER);
		add(AltCerceve, BorderLayout.SOUTH);
		
		pack();
		setTitle("Resim Bulmaca Oyunu");
		setLayout(new BorderLayout());
		setIconImage(IkonResmi);
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				new ResimBulmacaOyunu();
			}
		});
	}

	private BufferedImage ResimYukleyici() throws IOException{
		BufferedImage Anaresim = null;
		if(picture == 1)
			Anaresim = ImageIO.read(new File("src\\resources\\sebo.jpg"));
		else if(picture == 2)
			Anaresim = ImageIO.read(new File("src\\resources\\sebo1.jpg"));
		else if(picture == 3)
			Anaresim = ImageIO.read(new File("src\\resources\\sebo2.jpg"));
		
		return Anaresim;
	}
	
	private int YeniYukseklikEldeEtme(int Genislik, int Yukseklik) {
		double ratio = istenilenGenislik / (double) Genislik;
		
		int YeniYukseklik = (int) (Yukseklik * ratio);
		return YeniYukseklik;
	}

	private BufferedImage resizeResim(BufferedImage Anaresim, int Genislik, int Yukseklik, int type) {
		BufferedImage Resim = new BufferedImage(Genislik, Yukseklik, type);
		Graphics g = Resim.createGraphics();
		g.drawImage(Anaresim, 0, 0, Genislik, Yukseklik, null);
		g.dispose();
		
		return Resim;
	}
	
	class ClickAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
                
		@Override
		public void actionPerformed(ActionEvent e) {
			if(!timeFlag) {
				beforeTime = System.currentTimeMillis();
				timeFlag = true;
				countdownTime();
			}
			Clickler++;
			TıklamaButonu.setText("" + Clickler);
			checkButton(e);
			CozumDogrulama();
		}

		private void checkButton(ActionEvent e) {
			int lid = 0;
			for(Buton button: Butonlar) {
				if(button.ButonSon) {
					lid = Butonlar.indexOf(button);
				}
			}
			JButton button = (JButton) e.getSource();
			int bid = Butonlar.indexOf(button);
			if((bid - 1 == lid) || (bid + 1 == lid) || (bid -3 == lid) || (bid + 3 == lid)) {
				Collections.swap(Butonlar, bid, lid);
				ButonlariGuncelle();
			}
		}

		private void ButonlariGuncelle() {
			Cerceve.removeAll();
			for(Buton button: Butonlar) {
				Cerceve.add(button);
			}
			Cerceve.validate();
		}

		private void CozumDogrulama() {
			List<Point> current = new ArrayList<>();
			for(Buton button: Butonlar) {
				current.add((Point) button.getClientProperty("pozisyon"));
			}
			if(compareList(solution, current)) {
				timeFlag = false;
				if(picture == 1) {
					JOptionPane.showMessageDialog(Cerceve, "Congratulations, You Won!\nJust like Şebnem Ferah ", 
							"You Won!!!   Time Taken: "+ timeTaken + "   Clicks: " + Clickler, JOptionPane.INFORMATION_MESSAGE);
				}
				else if(picture == 2) {
					JOptionPane.showMessageDialog(Cerceve, "Congratulations, You Won!\nJust like Şebnem Ferah 2 ", 
							"You Won!!!   Time Taken: "+ timeTaken + "   Clicks: " + Clickler, JOptionPane.INFORMATION_MESSAGE);
				}
				else if(picture == 3) {
					JOptionPane.showMessageDialog(Cerceve, "Congratulations, You Won!\nJust like Şebnem Ferah 3 ", 
							"You Won!!!   Time Taken: "+ timeTaken + "   Clicks: " + Clickler, JOptionPane.INFORMATION_MESSAGE);
				}	
				Clickler = 0;
				timeTaken = null;
			}
		}

		public boolean compareList(List<Point> list1, List<Point> list2) {
			return list1.toString().contentEquals(list2.toString());
		}
	}
        
	public void countdownTime() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				while(timeFlag) {
					long currentTime = System.currentTimeMillis();
					long runninTime = currentTime - beforeTime;
					Duration duration = Duration.ofMillis(runninTime);
					
					long minutes = duration.toMinutes();
					duration = duration.minusMinutes(minutes);
					long seconds = (duration.toMillis())/1000; 
					
					DecimalFormat formatter = new DecimalFormat("00");
					timeTaken = formatter.format(minutes) + " : " + formatter.format(seconds);
					ZamanButonu.setText(timeTaken);
				}
			}
		});
		t.start();
	}
        
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == AnaMenu) {
			int reply = JOptionPane.showConfirmDialog(this, "Eğer menüye dönerseniz oyunu kaybedeceksiniz kabul ediyor musunuz?", "Confirmation", JOptionPane.YES_NO_OPTION);
			if(reply == JOptionPane.YES_OPTION) {
				timeFlag = false;
				Clickler = 0;
				dispose();
				new ResimBulmacaOyunu();
			}
		}
		if(e.getSource() == Cozum) {
			JDialog dialog = new JDialog();
			dialog.setTitle("Çözüm");
			Image dialogIcon = Toolkit.getDefaultToolkit().getImage("src\\resources\\correct-mark-icon.png");
			dialog.setIconImage(dialogIcon);
			Image Cozum = null;
			try {
				Cozum = ResimYukleyici().getScaledInstance(200, 200, Image.SCALE_SMOOTH);	
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			JLabel label = new JLabel(new ImageIcon(Cozum));
			dialog.add(label);
			dialog.pack();
			dialog.setVisible(true);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		}
	}
}


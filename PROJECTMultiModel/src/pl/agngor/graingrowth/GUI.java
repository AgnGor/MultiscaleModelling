package pl.agngor.graingrowth;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.io.*;
import javax.imageio.ImageIO;

public class GUI extends JFrame {

	private Functions panelView = new Functions();
	private JSpinner spinner_1 = new JSpinner();
	private JSpinner spinner_2 = new JSpinner();
    private JSpinner spinner_3 = new JSpinner();
    private JSpinner spinner_4 = new JSpinner();
	private JRadioButton btnSquare= new JRadioButton("Square");
	private JRadioButton btnCircle= new JRadioButton("Circle");

	private JRadioButton btnPer= new JRadioButton("Periodic");
	private JRadioButton btnAbs= new JRadioButton("Absorbing");

	private final JComboBox chooseBox = new JComboBox();

	private GUI() {
		setBackground(Color.WHITE);
		setTitle("Grain Growth");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1000, 700);

		btnCircle.setSelected(true);
		btnPer.setSelected(true);

		JPanel contentPane;
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(20, 50, 20, 50));
		contentPane.setLayout(new BorderLayout(30,20));
		setContentPane(contentPane);
		JPanel conds;
		conds = new JPanel();
		contentPane.add(conds, BorderLayout.NORTH);
		JPanel buttons;
		buttons = new JPanel();
		contentPane.add(buttons, BorderLayout.CENTER);
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
		JPanel incls;
		incls = new JPanel();
		contentPane.add(incls, BorderLayout.SOUTH);

		panelView.setBackground(SystemColor.text);
		contentPane.add(panelView, BorderLayout.WEST);

		new Thread(panelView).start();

		JButton btnGrStart;
		btnGrStart = new JButton("       START GROWTH       ");
		btnGrStart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				panelView.setSize(Functions.width,Functions.height);
				if (!Functions.menuSaveMap) {
					if (Functions.beforeIncl) {
						GUI.this.panelView.loadMap();
						GUI.this.panelView.saveMap();
						Functions.beforeIncl =false;
					} else {
						GUI.this.panelView.random();
						GUI.this.panelView.saveMap();
					}
				}
				GUI.this.panelView.repaint();
				Functions.startGrowth = true;

			}
		});

		JLabel lblSize;
		lblSize = new JLabel("SIZE:");
		conds.add(lblSize);

		JLabel lblH;
		lblH = new JLabel("height:");
		conds.add(lblH);

        spinner_3.addChangeListener(arg0 -> {
			Functions.height=((Integer) spinner_3.getValue());
			Functions.sizeChange=true;
		});
        spinner_3.setModel(new SpinnerNumberModel(300, 100, 500, 50));
        conds.add(spinner_3);


		JLabel lblW;
		lblW = new JLabel("width:");
		conds.add(lblW);

        spinner_4.addChangeListener(arg0 -> {
			Functions.width=((Integer) spinner_4.getValue());
			Functions.sizeChange=true;
		});
        spinner_4.setModel(new SpinnerNumberModel(300, 100, 500, 50));
        conds.add(spinner_4);

		JLabel lblNeigh;
		lblNeigh = new JLabel("Neighbourhood:");
		conds.add(lblNeigh);

		chooseBox.addItemListener(argP -> Functions.neighbourhood = chooseBox.getSelectedIndex());


		chooseBox.setBackground(new Color(255, 255, 255));
		String[] nei;
		nei = new String[]{"Moore", "von Neumann"};
		chooseBox.setModel(new DefaultComboBoxModel(nei));
		conds.add(chooseBox);

		JLabel incType;
		incType = new JLabel("TYPE:");
		conds.add(incType);

		btnPer.addActionListener(e -> {

			btnAbs.setSelected(false);
			Functions.period = 0;
		});
		conds.add(btnPer);

		btnAbs.addActionListener(e -> {

			btnPer.setSelected(false);
			Functions.period = 1;
		});
		conds.add(btnAbs);

		JLabel lblNumGrain;
		lblNumGrain = new JLabel("Grains:");
		conds.add(lblNumGrain);


		spinner_2.addChangeListener(arg0 -> Functions.points =  (Integer)spinner_2.getValue());
		spinner_2.setModel(new SpinnerNumberModel(10, 1, 1000, 1));
		conds.add(spinner_2);


		JLabel lblTime;
		lblTime = new JLabel("Time:");
		conds.add(lblTime);



		spinner_1.addChangeListener(arg0 -> Functions.delay = (Integer) spinner_1.getValue());
		spinner_1.setModel(new SpinnerNumberModel(10, 1, 9999, 10));
		conds.add(spinner_1);

		JLabel lblMs;
		lblMs = new JLabel("ms");
		conds.add(lblMs);
		buttons.add(Box.createVerticalStrut(90));
		buttons.add(btnGrStart);
		buttons.add(Box.createVerticalStrut(10));
		JButton btnReset;
		btnReset = new JButton("                 RESET                ");
		btnReset.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				for(int i=0; i<5; i++){
				GUI.this.panelView.clearMap();
				GUI.this.panelView.repaint();
				}
			}
		});
		buttons.add(btnReset);
		buttons.add(Box.createVerticalStrut(30));

		JButton btnExpBmp;
		btnExpBmp = new JButton("       EXPORT to .BMP       ");
		// VERSION ONE: LETS YOU PICK DESTINATION FOLDER
		btnExpBmp.addActionListener(e -> {
			BufferedImage image;
			image = new BufferedImage(GUI.this.panelView.getWidth(), GUI.this.panelView.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics2D g;
			g = image.createGraphics();
			GUI.this.panelView.printAll(g);
			g.dispose();
			try {

				JFileChooser fileChooser;
				fileChooser = new JFileChooser();
				if (fileChooser.showSaveDialog(GUI.this) != JFileChooser.APPROVE_OPTION) {
					return;
				}
				File file;
				file = fileChooser.getSelectedFile();
				ImageIO.write(image, "bmp", file);

			} catch (IOException exp) {
				exp.printStackTrace();
			}
		});


// VERSION TWO: DOES NOT LET YOU PICK FOLDER, BUT SAVES IN DESIRED FORMAT


//			@Override
//            public void actionPerformed(ActionEvent evt) {
//                BufferedImage imagebuf=null;
//                try {
//                    imagebuf = new Robot().createScreenCapture(new Rectangle(panelView.getSize()));
//                } catch (AWTException e1) {
//                    e1.printStackTrace();
//                }
//                Graphics2D graphics2D = imagebuf.createGraphics();
//                File file = new File("save1.bmp");
//				panelView.paint(graphics2D);
//                try {
//                    ImageIO.write(imagebuf,"bmp", file);
//                    Desktop.getDesktop().open(file);
//                } catch (Exception e) {
//                    System.out.println("error");
//                }
//            }
//        });
		buttons.add(btnExpBmp);

		buttons.add(Box.createVerticalStrut(10));
		JButton btnExpTxt;
		btnExpTxt = new JButton("       EXPORT to .TXT        ");
		btnExpTxt.addActionListener(e -> GUI.this.panelView.expTxt());
		buttons.add(btnExpTxt);
		buttons.add(Box.createVerticalStrut(10));
		JButton btnImportTxt;
		btnImportTxt = new JButton("      IMPORT from .TXT     ");
		btnImportTxt.addActionListener(e -> {
			GUI.this.panelView.impTxt();
			GUI.this.panelView.loadMap();
			GUI.this.panelView.repaint();
			Functions.startGrowth = true;
		});
		buttons.add(btnImportTxt);

		buttons.add(Box.createVerticalStrut(10));
		JButton btnImpBmp;
		btnImpBmp = new JButton("      IMPORT from .BMP    ");
		btnImpBmp.addActionListener(e -> {
			{
				try {
					JFileChooser jfc = new JFileChooser();
					jfc.showDialog(null,"Select the File");

					jfc.setVisible(true);
					File toRead;
					toRead = jfc.getSelectedFile();

					BufferedImage myPicture;
					myPicture = ImageIO.read(toRead);
					JLabel picLabel = new JLabel(new ImageIcon(myPicture.getScaledInstance(Functions.width, Functions.height, Image.SCALE_FAST)));
					picLabel.setBounds(panelView.getX(), panelView.getY(), panelView.getWidth(), panelView.getHeight());
					panelView.add(picLabel);

				} catch (Exception f) {
					System.out.println("Not picture found");
				}

				GUI.this.panelView.repaint();
			}
		});
		buttons.add(btnImpBmp);

		buttons.add(Box.createVerticalStrut(30));

		JButton btnGrain;
		btnGrain = new JButton("          Show 1 GRAIN         ");
		btnGrain.addActionListener(e -> {
			GUI.this.panelView.oneGrain();

			GUI.this.panelView.loadMap();
			GUI.this.panelView.repaint();
		});
		buttons.add(btnGrain);
		buttons.add(Box.createVerticalStrut(10));
		JButton btnBoundAll;
		btnBoundAll = new JButton("Show BOUNDARY of ALL");
		btnBoundAll.addActionListener(e -> {
			GUI.this.panelView.onlyBounds();
			GUI.this.panelView.loadMap();
			GUI.this.panelView.repaint();
		});
		buttons.add(btnBoundAll);


		JButton btnInclAft;
		btnInclAft = new JButton("Add INCLUSIONS after");
		btnInclAft.addActionListener(e -> {
			GUI.this.panelView.addInclAft();

			GUI.this.panelView.loadMap();
			GUI.this.panelView.repaint();
		});
		incls.add(btnInclAft);

		JButton btnInclBef;
		btnInclBef = new JButton("Add INCLUSIONS before");
		btnInclBef.addActionListener(e -> {

			GUI.this.panelView.addInclBef();
			GUI.this.panelView.saveMap();
			GUI.this.panelView.loadMap();
			GUI.this.panelView.repaint();
		});
		incls.add(btnInclBef);

		JLabel incShape;
		incShape = new JLabel("SHAPE:");
		incls.add(incShape);

		btnCircle.addActionListener(e -> {

			btnSquare.setSelected(false);
			Functions.incShapeCircle =true;
		});
		incls.add(btnCircle);

		btnSquare.addActionListener(e -> {

			btnCircle.setSelected(false);
			Functions.incShapeCircle =false;
		});
		incls.add(btnSquare);

	}

	public static void main(String[] args) {
		try {
			GUI GUIframe;
			GUIframe = new GUI();
			GUIframe.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

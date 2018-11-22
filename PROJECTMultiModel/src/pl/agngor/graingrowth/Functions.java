package pl.agngor.graingrowth;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import javax.swing.*;

public class Functions extends JPanel implements Runnable {

	private int pixelSize;
	static int width ;
	static int height ;
	private int tabSizeX ;
	private int tabSizeY ;
	private pointState[][] pointState;
	private Map<Integer, int[]> pattern; //moore or von neumann
	private List<pointState> colours; //list of points colours
	{
		colours = new ArrayList<>();
	}
	static boolean startGrowth = false; //true when "start" pressed
	static int period = 0;
	static int delay=1;
	static int neighbourhood = 0;
	static int points = 8;
	private int inclNum = 8;
	static boolean incShapeCircle = true;
	static boolean sizeChange = false;
	static boolean menuSaveMap = false;
	static boolean beforeIncl = false;
	private Set<Map.Entry<coordinates, pointState>> savedSubs;
	static Map<coordinates, pointState> pkts; //map of all points
	static {
		pkts = new HashMap<>();
	}

	Functions() {
		width=300;
		height=300;
		pixelSize = 2;
		tabSizeX = width / this.pixelSize;
		tabSizeY = height / this.pixelSize;
		pointState = new pointState[this.tabSizeX][this.tabSizeY];
		pattern = new HashMap<>();
		pattern.put(0, new int[] { 1, 1, 1, 1, 1, 1, 1, 1 }); // Moore
		pattern.put(1, new int[] { 0, 1, 0, 1, 0, 1, 0, 1 }); // von Neumann

		for (int i = 0; i < this.tabSizeX; i++) {
			for (int j = 0; j < this.tabSizeY; j++) {
				this.pointState[i][j] = new pointState();
			}
		}

		setPreferredSize(new Dimension(500, 500));

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int x = arg0.getX();
				int y = arg0.getY();
				Functions.this.clickPixel(x, y);
				Functions.this.repaint();
			}
		});

	}


	public void run() {
		try {
			while (true) {
				this.resizeP(); // map's size
				if (Functions.startGrowth) {
					this.reverse(); // pointState: prev = act

					this.naiveIte();

					this.repaint();
				}
				try {
					Thread.sleep(delay); //if start not pressed do this
				} catch (Exception ignored) {
				}
			}
		}catch (Exception ignored){};
	}

	private void naiveIte() {
		if (Functions.startGrowth) {

			pointState tmp;

			for (int i = 0; i <= this.tabSizeX - 1; i++) {
				for (int j = 0; j <= this.tabSizeY - 1; j++) {

					if (this.pointState[i][j].act == 1) //is it a seed (actgrow) or grain(quit)
					{
						continue;
					}

					tmp = this.actGrow(i, j, "act"); // neighbour whose colour I take
					if (tmp.act < 0 && tmp.prev < 0) {
						continue;
					}
					this.pointState[i][j].act = tmp.act;
					this.pointState[i][j].recolor(tmp);

				}
			}
		}
	}

	void clearMap() { //map clearMap

		for (int i = 0; i < this.tabSizeX; i++) {
			for (int j = 0; j < this.tabSizeY; j++) {
				this.pointState[i][j].set(0);
			}
		}
	}


	void random() { //seeds arrangement
		Random r = new Random();
		int x, y;
		int xMiddle = this.tabSizeX / 2;
		int yMiddle = this.tabSizeY / 2;

				int PRx,PRy;
				for (int i = 0; i < 360; i = i + (360 / Functions.points)) {
					PRx = r.nextInt(this.tabSizeX / 2);
					PRy = r.nextInt(this.tabSizeY / 2);

					x = (int) (PRx * Math.cos(i * Math.PI / 180));
					x = x + xMiddle;
					y = (int) (PRy * Math.sin(i * Math.PI / 180));
					y = y + yMiddle;

					while (x < 0) {
						x++;
					}
					while (y < 0) {
						y++;
					}
					while (x >= this.tabSizeX) {
						x--;
					}
					while (y >= this.tabSizeY) {
						y--;
					}

					do {
						this.pointState[x][y].set_random(1);
					} while (this.colorExists(this.pointState[x][y],x,y));

					colours.add(new pointState(this.pointState[x][y]));
				}
	}

	private boolean colorExists(pointState pointState, int x, int y) { //is the color already on the map
		int i = 0;
		while (i < this.tabSizeX) {
			int j = 0;
			while (j < this.tabSizeY) {
				if (x==i && y==j && x>=0 && y>=0) {
					i++;
					j++;
					continue;
				}
				if (this.pointState[i][j].colorMatch(pointState)) {
					return true;
				}
				j++;
			}
			i++;
		}
		return false;
	}

	@Override
	public void paintComponent(Graphics g) { //creates color
		try {
			int x = 0;
			while (x < this.tabSizeX) {
				for (int y = 0; y < this.tabSizeY; y++) {
						g.setColor(new Color(this.pointState[x][y].R, this.pointState[x][y].G,
								this.pointState[x][y].B));

					g.fillRect(x * this.pixelSize, // position X
							y * this.pixelSize, // position Y
							this.pixelSize, // width
							this.pixelSize // height
					);
				}
				x++;
			}
		}catch (ArrayIndexOutOfBoundsException ignored){}
	}


	void loadMap() { //loading saved map
		for (Entry<coordinates, pointState> o : pkts.entrySet()) {
			this.pointState[o.getKey().x][o.getKey().y] = new pointState(o.getValue());
		}
	}

	void saveMap() { //remember the map
		pkts.clear();
		for (int i = 0; i < tabSizeX; i++) {
			for (int j = 0; j < tabSizeY; j++) {
				if (this.pointState[i][j].act == 1) {
					pkts.put(new coordinates(i, j), new pointState(this.pointState[i][j]));
				}
			}
		}
	}

	private void resizeP() { //if map resized

		int w = width;
		int h = height;

		this.tabSizeX = width / this.pixelSize;
		this.tabSizeY = height / this.pixelSize;
		try {
		if (sizeChange) {
			pointState[][] pointState_new = new pointState[w / this.pixelSize][h / this.pixelSize];
			for (int i = 0; i < w / this.pixelSize; i++) {
				for (int j = 0; j < h / this.pixelSize; j++) {
					pointState_new[i][j] = new pointState();
				}
			}
			// set new size
			width = w;
			height = h;
			this.tabSizeX = width / this.pixelSize;
			this.tabSizeY = height / this.pixelSize;
			this.pointState = pointState_new;
			sizeChange=false;
		}
		}catch (ArrayIndexOutOfBoundsException ignored){}
	}

	private pointState actGrow(int x, int y, String t) { //find a point's neighbour
		int[] s = {0, 0, 0, 0, 0, 0, 0, 0};
		//upper-left, upper-center, upper-right, center-right, bottom-right, bottom-center, bottom-left, center-left
		int[] s_pattern = { 0, 0, 0, 0, 0, 0, 0, 0 };

		int xLeft = x - 1;
		int xRight = x + 1;
		int yTop = y - 1;
		int yBottom = y + 1;

		if (Functions.period == 0) { //periodic type
			if (xLeft < 0) {
				xLeft = this.tabSizeX - 1;
			}
			if (xRight > this.tabSizeX - 1) {
				xRight = 0;
			}

			if (yTop < 0) {
				yTop = this.tabSizeY - 1;
			} else if (yBottom > this.tabSizeY - 1) {
				yBottom = 0;
			}
		}

		if (t == "act") { //neighbour's array
			if (xLeft >= 0 && yTop >= 0) {
				s[0] = this.pointState[xLeft][yTop].prev;
			}
			if (yTop >= 0) {
				s[1] = this.pointState[x][yTop].prev;
			}
			if (yTop >= 0 && xRight <= this.tabSizeX - 1) {
				s[2] = this.pointState[xRight][yTop].prev;
			}
			if (xRight <= this.tabSizeX - 1) {
				s[3] = this.pointState[xRight][y].prev;
			}
			if (yBottom <= this.tabSizeY - 1 && xRight <= this.tabSizeX - 1) {
				s[4] = this.pointState[xRight][yBottom].prev;
			}
			if (yBottom <= this.tabSizeY - 1) {
				s[5] = this.pointState[x][yBottom].prev;
			}
			if (xLeft >= 0 && yBottom <= this.tabSizeY - 1) {
				s[6] = this.pointState[xLeft][yBottom].prev;
			}
			if (xLeft >= 0) {
				s[7] = this.pointState[xLeft][y].prev;
			}
		}

		pointState newPointState;
		newPointState = new pointState();
		newPointState.set(-1);

		Random r;
		r = new Random();
		{
			s_pattern = pattern.get(Functions.neighbourhood);

		}

		if (s[0] == 1 && s_pattern[4] == 1) {
			return this.pointState[xLeft][yTop];
		} else if (s[1] == 1 && s_pattern[5] == 1) {
			return this.pointState[x][yTop];
		} else if (s[2] == 1 && s_pattern[6] == 1) {
			return this.pointState[xRight][yTop];
		} else if (s[3] == 1 && s_pattern[7] == 1) {
			return this.pointState[xRight][y];
		} else if (s[4] == 1 && s_pattern[0] == 1) {
			return this.pointState[xRight][yBottom];
		} else if (s[5] == 1 && s_pattern[1] == 1) {
			return this.pointState[x][yBottom];
		} else if (s[6] == 1 && s_pattern[2] == 1) {
			return this.pointState[xLeft][yBottom];
		} else if (s[7] == 1 && s_pattern[3] == 1) {
			return this.pointState[xLeft][y];
		}
		return newPointState;
	}

	private void reverse() { //actual is saved as previous
		int i = 0;
		while (i < this.tabSizeX) {
			int j = 0;
			while (j < this.tabSizeY) {
				this.pointState[i][j].prev = this.pointState[i][j].act;
				j++;
			}
			i++;
		}
	}

	private void clickPixel(int x, int y) { //new seed is generated with a mouse-click
		int pixel_x;
		pixel_x = x / this.pixelSize;
		int pixel_y;
		pixel_y = y / this.pixelSize;

		while (pixel_x < 1) {
			pixel_x++;
		}
		while (pixel_x >= this.tabSizeX - 1) {
			pixel_x--;
		}
		while (pixel_y < 1) {
			pixel_y++;
		}
		while (pixel_y >= this.tabSizeY - 1) {
			pixel_y--;
		}

		if (pixel_x >= this.tabSizeX || pixel_y >= this.tabSizeY) {
			return;
		}

		if (this.pointState[pixel_x][pixel_y].act == 0) {

			this.pointState[pixel_x][pixel_y] = new pointState();
			this.pointState[pixel_x][pixel_y].set_random(1);
		}

		this.reverse();
	}

		private boolean onBound(int x, int y) { //is a cell on a grain's boundary
		boolean[] s;
		s = new boolean[8];

		int xLeft = x - 1;
		int xRight = x + 1;
		int yTop = y - 1;
		int yBottom = y + 1;

		if (Functions.period == 0) {
			if (xLeft < 0) {
				xLeft = this.tabSizeX - 1;
			}
			if (xRight > this.tabSizeX - 1) {
				xRight = 0;
			}

			if (yTop < 0) {
				yTop = this.tabSizeY - 1;
			} else if (yBottom > this.tabSizeY - 1) {
				yBottom = 0;
			}
		}

		if (xLeft >= 0 && yTop >= 0) {
			s[0] = this.pointState[xLeft][yTop].colorMatch(this.pointState[x][y]);
		}
		if (yTop >= 0) {
			s[1] = this.pointState[x][yTop].colorMatch(this.pointState[x][y]);
		}
		if (yTop >= 0 && xRight <= this.tabSizeX - 1) {
			s[2] = this.pointState[xRight][yTop].colorMatch(this.pointState[x][y]);
		}
		if (xRight <= this.tabSizeX - 1) {
			s[3] = this.pointState[xRight][y].colorMatch(this.pointState[x][y]);
		}
		if (yBottom <= this.tabSizeY - 1 && xRight <= this.tabSizeX - 1) {
			s[4] = this.pointState[xRight][yBottom].colorMatch(this.pointState[x][y]);
		}
		if (yBottom <= this.tabSizeY - 1) {
			s[5] = this.pointState[x][yBottom].colorMatch(this.pointState[x][y]);
		}
		if (xLeft >= 0 && yBottom <= this.tabSizeY - 1) {
			s[6] = this.pointState[xLeft][yBottom].colorMatch(this.pointState[x][y]);
		}
		if (xLeft >= 0) {
			s[7] = this.pointState[xLeft][y].colorMatch(this.pointState[x][y]);
		}

		for(boolean ss : s) {
			if (ss) {
				continue;
			}
			return true;
		}
		return false;

	}


	void expTxt() //export to TXT
	{
		this.saveMap();

		try {

			JFileChooser jfc;
			jfc = new JFileChooser();
			jfc.showDialog(null,"Select the File");
			jfc.setVisible(true);
			File toExport;
			toExport = jfc.getSelectedFile();
			BufferedWriter out;
			out = new BufferedWriter(new FileWriter(toExport));

			out.write(String.valueOf(this.tabSizeX) + " " + String.valueOf(this.tabSizeY));
			out.newLine();

			for (Map.Entry<coordinates, pointState> entry : pkts.entrySet())
			{
				out.write(entry.getKey().x + " " + entry.getKey().y + " 0 " + entry.getValue().color2int() + " " +  entry.getValue().prev + " " +  entry.getValue().act );
				out.newLine();

			}
			out.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	void impTxt() //import from TXT
	{
		//String filePath = "ExportToTXT.txt";
		Map<coordinates, pointState> map;
		map = new HashMap<>();

		String line;
		BufferedReader reader;
		reader = null;
		try
		{
			JFileChooser jfc;
			jfc = new JFileChooser();
			jfc.showDialog(null,"Select the File");
			jfc.setVisible(true);
			File toRead;
			toRead = jfc.getSelectedFile();

			reader = new BufferedReader(new FileReader(toRead));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		try
		{
			if (reader == null) {
				throw new AssertionError();
			}
			while ((line = reader.readLine()) != null)
			{
				String[] parts = line.split(" ", 6);
				if (parts.length >= 6)
				{
					int xx, yy, integerRGBInt, actInt, prevInt;
					xx = Integer.parseInt(parts[0]);
					yy = Integer.parseInt(parts[1]);
					integerRGBInt = Integer.parseInt(parts[3]);
					actInt = Integer.parseInt(parts[4]);
					prevInt = Integer.parseInt(parts[5]);

					if (actInt == 1)
					{
							pkts.put(new coordinates(xx, yy), new pointState(actInt,prevInt,integerRGBInt));
					}

				} else {
					System.out.println("ignoring line: " + line);
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		for (coordinates key : map.keySet())
		{
			System.out.println(key + ":" + map.get(key));
		}
		try
		{
			reader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	void addInclAft() //inclusions AFTER
	{
		this.saveMap();
		int inclIte = 0;
		int[][] grBound; //boundary points array
		grBound = new int[pkts.size()*50][2];

		for (Map.Entry<coordinates, pointState> entry : pkts.entrySet())
		{
			boolean checkBound = onBound(entry.getKey().x, entry.getKey().y);

			if(checkBound)
			{

				grBound[inclIte][0] = entry.getKey().x;
				grBound[inclIte][1] = entry.getKey().y;

				inclIte++;
			}
		}

		Random rand;
		rand = new Random();

		int inclNumBegin;
		inclNumBegin = 0;

		while(inclNumBegin < inclNum)
		{
			int n = rand.nextInt(inclIte)+1;

			for (Map.Entry<coordinates, pointState> entry : pkts.entrySet())
			{
				int inclRange = 4;
				if (inclRange == 0) {
					if (grBound[n][0] == entry.getKey().x && grBound[n][1] == entry.getKey().y)
					{
						entry.getValue().R=0;
						entry.getValue().G=0;
						entry.getValue().B=0;
					}
				} else {
					if (incShapeCircle) {
						//cicle
						int x2 = grBound[n][0];
						int y2 = grBound[n][1];
						int r = inclRange;


						int dx = x2 - entry.getKey().x; // horizontal offset
						int dy = y2 - entry.getKey().y; // vertical offset
						if ( (dx*dx + dy*dy) <= (r*r) )
						{
							entry.getValue().R=0;
							entry.getValue().G=0;
							entry.getValue().B=0;
						}
					} else {
						for (int i = 0; i < inclRange; i++)
						{
							for (int j = 0; j< inclRange; j++)
							{
								if (grBound[n][0]+i == entry.getKey().x && grBound[n][1]+j == entry.getKey().y)
								{
									entry.getValue().R=0;
									entry.getValue().G=0;
									entry.getValue().B=0;
								}
							}
						}
					}
				}
			}
			inclNumBegin++;
		}
	}


	void addInclBef() //inclusions BEFORE
	{
		int i;
		for(i = 0; i< inclNum; i++){

			beforeIncl = true;
			Random r;
			r = new Random();
			int x;
			x = r.nextInt(width);
			int y;
			y = r.nextInt(height);

			pointState pointState;
			pointState = new pointState();


			int pixel_x = x / this.pixelSize;
			int pixel_y = y / this.pixelSize;


			while (pixel_x < 1) {
				pixel_x++;
			}
			while (pixel_x >= this.tabSizeX - 1) {
				pixel_x--;
			}
			while (pixel_y < 1) {
				pixel_y++;
			}
			while (pixel_y >= this.tabSizeY - 1) {
				pixel_y--;
			}
			if (this.pointState[pixel_x][pixel_y].act != 0) {
				continue;
			}

			pointState.set(0);
			pointState.B=0;
			pointState.G=0;
			pointState.R=0;
			this.pointState[pixel_x][pixel_y] = pointState;
			pkts.put(new coordinates(pixel_x, pixel_y), new pointState(pointState));

		}
	}


	void oneGrain() //show one grain from map
	{
		this.saveMap();

		Set<Map.Entry<coordinates, pointState>> entries = pkts.entrySet();

		Map.Entry<coordinates, pointState> next =  entries.iterator().next();

		savedSubs = new HashSet<>();

		int color2int = next.getValue().color2int();


		for (Map.Entry<coordinates, pointState> entry : entries)
		{
			int colors = entry.getValue().color2int();

			if (colors != color2int)
			{

				entry.getValue().R=255;
				entry.getValue().G=255;
				entry.getValue().B=255;
			}
			else
			{
				savedSubs.add(entry);
			}
		}
	}


	void onlyBounds() //show black boundaries
	{
		this.saveMap();

		double boundIte;
		boundIte = 0.0;

		for (Iterator<Entry<coordinates, pl.agngor.graingrowth.pointState>> iterator = pkts.entrySet().iterator(); iterator.hasNext(); ) {
			Entry<coordinates, pl.agngor.graingrowth.pointState> entry = iterator.next();
			boolean checkBound = onBound(entry.getKey().x, entry.getKey().y); //where is the boundary

			if (checkBound) {

				entry.getValue().R = 0;
				entry.getValue().G = 0;
				entry.getValue().B = 0;

				double v = boundIte++;
			}
		}
		//rest in white
		for (Iterator<Entry<coordinates, pl.agngor.graingrowth.pointState>> iterator = pkts.entrySet().iterator(); iterator.hasNext(); ) {
			Entry<coordinates, pl.agngor.graingrowth.pointState> entry = iterator.next();
			int colors = entry.getValue().color2int();

			if (colors != 0) {
				entry.getValue().R = 255;
				entry.getValue().G = 255;
				entry.getValue().B = 255;
			}
		}

	}



}

class coordinates {

	int x;
	int y;

	coordinates(int a, int b) {
		this.x = a;
		this.y = b;
	}


}

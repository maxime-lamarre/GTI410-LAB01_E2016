/*
   This file is part of j2dcg.
   j2dcg is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.
   j2dcg is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.
   You should have received a copy of the GNU General Public License
   along with j2dcg; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import model.Pixel;

/**
 * <p>Title: ColorDialog</p>
 * <p>Description: ... (JDialog)</p>
 * <p>Copyright: Copyright (c) 2003 Mohammed Elghaouat, Eric Paquette</p>
 * <p>Company: (ÉTS) - École de Technologie Supérieure</p>
 * @author unascribed
 * @version $Revision: 1.7 $
 */
public class ColorDialog extends JDialog {
	private JButton okButton;
	private RGBColorMediator rgbMediator;
	private CMYKColorMediator cmykMediator;
	private HSVColorMediator hsvmediator;
	private ActionListener okActionListener;
	private ColorDialogResult result;

	static public Pixel getColor(Frame owner, Pixel color, int imageWidths) {
		ColorDialogResult result = new ColorDialogResult(color);
		ColorDialog colorDialog = new ColorDialog(owner, result, imageWidths);
		colorDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		colorDialog.pack();
		colorDialog.setVisible(true);
		if (result.isAccepted()) {
			return result.getPixel();
		} else {
			return null;
		}
	}

	ColorDialog(Frame owner, ColorDialogResult result, int imageWidths) {
		super(owner, true);
		this.result = result;

		JTabbedPane tabbedPane = new JTabbedPane();
		JPanel rgbPanel = createRGBPanel(result, imageWidths);
		tabbedPane.addTab("RGB", rgbPanel);

		JPanel cmykPanel = createCMYKPanel(result, imageWidths);
		tabbedPane.addTab("CMYK", cmykPanel);

		JPanel hsvPanel = createHSVPanel(result, imageWidths);
		tabbedPane.addTab("HSV", hsvPanel);

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
		AbstractAction okAction = new AbstractAction("OK") {
			public void actionPerformed(ActionEvent e) {
				ColorDialog.this.result.setAccepted(true);
				dispose();
			}
		};
		okButton = new JButton(okAction);
		buttonsPanel.add(okButton);
		AbstractAction cancelAction = new AbstractAction("Cancel") {
			public void actionPerformed(ActionEvent e) {
				ColorDialog.this.dispose();
			}
		};
		buttonsPanel.add(new JButton(cancelAction));

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(tabbedPane);
		mainPanel.add(buttonsPanel);

		getContentPane().add(mainPanel, BorderLayout.CENTER);
	}

	private JPanel createRGBPanel(ColorDialogResult result, int imageWidths) {	
		rgbMediator = new RGBColorMediator(result, imageWidths, 30);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		ColorSlider csRed = new ColorSlider("R:", result.getPixel().getRed(), rgbMediator.getRedImage());
		ColorSlider csGreen = new ColorSlider("G:", result.getPixel().getGreen(), rgbMediator.getGreenImage());
		ColorSlider csBlue = new ColorSlider("B:", result.getPixel().getBlue(), rgbMediator.getBlueImage());

		rgbMediator.setRedCS(csRed);
		rgbMediator.setGreenCS(csGreen);
		rgbMediator.setBlueCS(csBlue);

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(csRed);
		panel.add(csGreen);
		panel.add(csBlue);

		return panel;
	}

	private JPanel createCMYKPanel(ColorDialogResult result, int imageWidths) {	
cmykMediator = new CMYKColorMediator(result, imageWidths, 30);
		
		Double redD, greenD, blueD, cyan, magenta, yellow, tempo;
		Double black = 0.0;
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		redD = (double) (result.getPixel().getRed() / 255.0);
		greenD = (double) (result.getPixel().getGreen() / 255.0);
		blueD = (double) (result.getPixel().getBlue() / 255.0);
		
		if ((redD <= greenD) && (redD <= blueD)) black = 0.0 + redD;
		if ((greenD <= redD) && (greenD <= blueD)) black = 0.0 + greenD;
		if ((blueD <= redD) && (blueD <= greenD)) black = 0.0 + blueD;
		
		cyan = (1.0 - redD - black) / (1.0 - black);
		magenta = (1.0 - greenD - black) / (1.0 - black);
		yellow = (1.0 - blueD - black) / (1.0 - black);
		
		if ((cyan <= magenta) && (cyan <= yellow)) black = 0.0 + cyan;
		if ((magenta <= cyan) && (magenta <= yellow)) black = 0.0 + magenta;
		if ((yellow <= cyan) && (yellow <= magenta)) black = 0.0 + yellow;
		
		tempo = cyan * 255;
		ColorSlider csCyan = new ColorSlider("C:", tempo.intValue(), cmykMediator.getCyanImage());
		tempo = magenta * 255;
		ColorSlider csMagenta = new ColorSlider("M:", tempo.intValue(), cmykMediator.getMagentaImage());
		tempo = yellow * 255;
		ColorSlider csYellow = new ColorSlider("Y:", tempo.intValue(), cmykMediator.getYellowImage());
		tempo = black * 255;
		ColorSlider csBlack = new ColorSlider("B:", tempo.intValue(), cmykMediator.getBlackImage());
		
		cmykMediator.setCyanCS(csCyan);
		cmykMediator.setCyan(cyan);
		cmykMediator.computeCyanImage(cyan, magenta, yellow,black);
		cmykMediator.setMagentaCS(csMagenta);
		cmykMediator.setMangenta(magenta);
		cmykMediator.computeMagentaImage(cyan, magenta, yellow,black);
		cmykMediator.setYellowCS(csYellow);
		cmykMediator.setYellow(yellow);
		cmykMediator.computeYellowImage(cyan, magenta, yellow,black);
		cmykMediator.setBlackCS(csBlack);
		cmykMediator.setBlack(black);
		cmykMediator.computeBlackImage(cyan, magenta, yellow, black);
		
		//System.out.println("C: "+cyan+" M: "+magenta+" Y: "+yellow+" B: "+black);
				
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(csCyan);
		panel.add(csMagenta);
		panel.add(csYellow);
		panel.add(csBlack);
		
		return panel;
	}

	private JPanel createHSVPanel(ColorDialogResult result, int imageWidths) {	
		hsvmediator = new HSVColorMediator(result, imageWidths, 30);

		JPanel panel = new JPanel();
		
		int var_r = result.getPixel().getRed();
		int var_g = result.getPixel().getGreen();
		int var_b = result.getPixel().getBlue();
		
		double var_h = hsvmediator.getHueFromRGB(var_r, var_g, var_b);
		double var_s = hsvmediator.getSaturationFromRGB(var_r, var_g, var_b);
		double var_v = hsvmediator.getValueFromRGB(var_r, var_g, var_b);
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		ColorSlider csH = new ColorSlider("H:", (int)(var_h*imageWidths), hsvmediator.getHueImage());
		ColorSlider csS = new ColorSlider("S:", (int)(var_s*imageWidths), hsvmediator.getSaturationImage());
		ColorSlider csV = new ColorSlider("V:", (int)(var_v*imageWidths), hsvmediator.getValueImage());

		hsvmediator.setHCS(csH);
		hsvmediator.setSCS(csS);
		hsvmediator.setVCS(csV);

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(csH);
		panel.add(csS);
		panel.add(csV);

		return panel;
	}
}


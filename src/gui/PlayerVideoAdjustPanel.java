package gui;
/**
* The code for this panel was originally taken from:
* https://github.com/caprica/vlcj/blob/master/src/test/java/uk/co/caprica/vlcj/test/basic/PlayerVideoAdjustPanel.java
* and modified to meet the needs of this project.
* It contains some sliders that can be used to adjust the brightness, contrast, hue, and saturation of the current media.
*/
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import state.LanguageSelector;
import state.State;
import uk.co.caprica.vlcj.binding.LibVlcConst;
import uk.co.caprica.vlcj.player.MediaPlayer;

public class PlayerVideoAdjustPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final MediaPlayer mediaPlayer;
    private JCheckBox resetAdjustCheckBox;
    private JLabel contrastLabel;
    private JSlider contrastSlider;
    private JLabel brightnessLabel;
    private JSlider brightnessSlider;
    private JLabel hueLabel;
    private JSlider hueSlider;
    private int defaultHue;
    private int defaultContrast;
    private int defaultBrightness;
    public PlayerVideoAdjustPanel(MediaPlayer mediaPlayer, JCheckBox checkbox) {
        this.mediaPlayer = mediaPlayer;
        this.resetAdjustCheckBox = checkbox;
        defaultHue = mediaPlayer.getHue();
        defaultContrast = Math.round(mediaPlayer.getContrast() * 100.0f);
        defaultBrightness = Math.round(mediaPlayer.getBrightness() * 100.0f);
        createUI();
    }
    private void createUI() {
        createControls();
        layoutControls();
        registerListeners();
    }
    private void createControls() {
        contrastLabel = new JLabel(getString("contrast"));
        contrastSlider = new JSlider();
        contrastSlider.setOrientation(JSlider.HORIZONTAL);
        contrastSlider.setMinimum(Math.round(LibVlcConst.MIN_CONTRAST * 100.0f));
        contrastSlider.setMaximum(Math.round(LibVlcConst.MAX_CONTRAST * 100.0f));
        contrastSlider.setPreferredSize(new Dimension(100, 40));
        contrastSlider.setToolTipText(getString("contrastToolTip"));
        contrastSlider.setPaintLabels(true);
        contrastSlider.setPaintTicks(true);
        brightnessLabel = new JLabel(getString("brightness"));
        brightnessSlider = new JSlider();
        brightnessSlider.setOrientation(JSlider.HORIZONTAL);
        brightnessSlider.setMinimum(Math.round(LibVlcConst.MIN_BRIGHTNESS * 100.0f));
        brightnessSlider.setMaximum(Math.round(LibVlcConst.MAX_BRIGHTNESS * 100.0f));
        brightnessSlider.setPreferredSize(new Dimension(100, 40));
        brightnessSlider.setToolTipText(getString("brightnessToolTip"));
        hueLabel = new JLabel(getString("hue"));
        hueSlider = new JSlider();
        hueSlider.setOrientation(JSlider.HORIZONTAL);
        hueSlider.setMinimum(LibVlcConst.MIN_HUE);
        hueSlider.setMaximum(LibVlcConst.MAX_HUE);
        hueSlider.setPreferredSize(new Dimension(100, 40));
        hueSlider.setToolTipText(getString("hueToolTip"));
        contrastSlider.setValue(defaultContrast);
        brightnessSlider.setValue(defaultBrightness);
        hueSlider.setValue(defaultHue);

        hueSlider.setEnabled(true);
    }
    private void layoutControls() {
        setBorder(new EmptyBorder(4, 4, 4, 4));
        setLayout(new BorderLayout());
        JPanel slidersPanel = new JPanel();
        slidersPanel.setLayout(new BoxLayout(slidersPanel, BoxLayout.Y_AXIS));
        slidersPanel.add(contrastLabel);
        slidersPanel.add(contrastSlider);
        slidersPanel.add(brightnessLabel);
        slidersPanel.add(brightnessSlider);
        slidersPanel.add(hueLabel);
        slidersPanel.add(hueSlider);
        add(slidersPanel, BorderLayout.CENTER);
        State.getState().addColourListeners(slidersPanel, contrastLabel, contrastSlider, brightnessLabel, brightnessSlider,
        		hueLabel, hueSlider, this);
    }
    private void registerListeners() {
        resetAdjustCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if (resetAdjustCheckBox.isSelected()){
	                contrastSlider.setValue(defaultContrast);
	                brightnessSlider.setValue(defaultBrightness);
	                hueSlider.setValue(defaultHue);
	                resetAdjustCheckBox.setSelected(true);
            	}
            }
        });
        contrastSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                mediaPlayer.setAdjustVideo(true);
                JSlider source = (JSlider)e.getSource();
                mediaPlayer.setContrast(source.getValue() / 100.0f);
                resetAdjustCheckBox.setSelected(false);
            }
        });
        brightnessSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                mediaPlayer.setAdjustVideo(true);
                JSlider source = (JSlider)e.getSource();
                mediaPlayer.setBrightness(source.getValue() / 100.0f);
                resetAdjustCheckBox.setSelected(false);
            }
        });
        hueSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                mediaPlayer.setAdjustVideo(true);
                JSlider source = (JSlider)e.getSource();
                mediaPlayer.setHue(source.getValue());
                resetAdjustCheckBox.setSelected(false);
            }
        });
    }
    
	private String getString(String label){
		return LanguageSelector.getString(label);
	}
}
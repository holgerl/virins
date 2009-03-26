package eit.headtracking.camera.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;

import javax.media.Processor;

public class ProcessorGUI extends Frame {
	
	private static final long serialVersionUID = -8508358864137930724L;

	@SuppressWarnings("unused")
	private ProcessorGUI() {
		// This hides the no-argument constructor
	}
	
	public ProcessorGUI(Processor p) {
		setLayout(new BorderLayout());
		
		Component vc = p.getVisualComponent();
		add("Center", vc);

		Component cc = p.getControlPanelComponent();
		add("South", cc);
	}
	
	public void addNotify() {
		super.addNotify();
		pack();
	}
	


}

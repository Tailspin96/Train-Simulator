package com.trains.ui.gui;

import com.trains.utils.lang.I18N;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GUILanguage extends GUI {
	private static final int MAX_LIST_HEIGHT = 300;

	public GUILanguage(JFrame frame, GUI previous) {
		super(frame, previous);
	}

	@Override
	protected String getTitle() {
		return "menu.title.language";
	}

	@Override
	protected void drawMenuItems() {
		JPanel toggleHolder = new JPanel();
		toggleHolder.setLayout(new BoxLayout(toggleHolder, BoxLayout.Y_AXIS));
		toggleHolder.setOpaque(false);

		ButtonGroup toggleGroup = new ButtonGroup();
		List<Locale> locales = new ArrayList<>(I18N.availableLocales);

		for (Locale locale : locales) {
			if (toggleHolder.getComponentCount() > 0) {
				toggleHolder.add(Box.createRigidArea(new Dimension(0, 15)));
			}

			JToggleButton button = new JToggleButton(locale.getDisplayName(locale));
			toggleGroup.add(button);
			button.setSelected(locale.equals(I18N.getLocale()));
			button.setAlignmentX(Component.CENTER_ALIGNMENT);
			lockButtonSize(button, MAX_BUTTON_SIZE);
			button.addActionListener(event -> I18N.setLocale(locale));
			toggleHolder.add(button);
		}

		JScrollPane scrollPane = new JScrollPane(toggleHolder);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		getListSize(scrollPane, toggleHolder);
		addMenuItems(scrollPane);
	}

	private static void getListSize(JScrollPane scrollPane, JPanel toggleHolder) {
		JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
		int scrollBarWidth = scrollBar.getPreferredSize().width;

		Runnable updatePadding = () -> {
			scrollPane.setBorder(BorderFactory.createEmptyBorder(0, scrollBarWidth, 0, scrollBar.isVisible() ? 0 : scrollBarWidth));
		};
		updatePadding.run();

		scrollBar.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				updatePadding.run();
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				updatePadding.run();
			}
		});

		Dimension listSize = new Dimension(MAX_BUTTON_SIZE + scrollBarWidth * 2, Math.min(toggleHolder.getPreferredSize().height, MAX_LIST_HEIGHT));
		scrollPane.setPreferredSize(listSize);
		scrollPane.setMaximumSize(listSize);
		scrollPane.setMinimumSize(listSize);
	}
}

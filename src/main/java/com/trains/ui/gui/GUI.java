package com.trains.ui.gui;

import com.trains.utils.lang.I18N;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class GUI extends JPanel {
	protected static final int MAX_BUTTON_SIZE = 300;
	private static final float DEFAULT_TITLE_SIZE = 24.0f;
	protected final JFrame frame;
	protected final JPanel buttonContainer;
	private final JLabel menuTitle;
	private final List<Runnable> i18nRefreshers = new ArrayList<>();
	private final Runnable localeRefresh = this::refreshI18n;
	protected float titleSize = DEFAULT_TITLE_SIZE;
	protected GUI previous;
	private int menuItemCount = 0;

	public GUI(JFrame frame, GUI previous) {
		super(new BorderLayout());
		this.frame = frame;
		this.previous = previous;
		this.buttonContainer = new JPanel();
		buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.Y_AXIS));
		buttonContainer.setOpaque(false);
		buttonContainer.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

		menuTitle = new JLabel("", SwingConstants.CENTER);
		applyTitleFont();
		bind(menuTitle, getTitle());

		JPanel titleContainer = new JPanel(new BorderLayout());
		titleContainer.setOpaque(false);
		titleContainer.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));
		titleContainer.add(menuTitle, BorderLayout.CENTER);

		buttonContainer.add(Box.createVerticalGlue());
		drawMenuItems();
		buttonContainer.add(Box.createVerticalGlue());

		JPanel bottomContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
		bottomContainer.setOpaque(false);
		bottomContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0));
		if (previous != null) {
			bottomContainer.add(createBackButton());
		}

		int edgeHeight = Math.max(titleContainer.getPreferredSize().height, bottomContainer.getPreferredSize().height);
		titleContainer.setPreferredSize(new Dimension(0, edgeHeight));
		bottomContainer.setPreferredSize(new Dimension(0, edgeHeight));

		add(titleContainer, BorderLayout.NORTH);
		add(buttonContainer, BorderLayout.CENTER);
		add(bottomContainer, BorderLayout.SOUTH);

		I18N.addListener(localeRefresh);
	}

	protected static void lockButtonSize(AbstractButton button, int width) {
		int height = button.getPreferredSize().height;
		Dimension size = new Dimension(width, height);
		button.setPreferredSize(size);
		button.setMinimumSize(size);
		button.setMaximumSize(size);
	}

	protected static String stringToHTML(String text, String align) {
		return "<html><div style='text-align:" + align + "'>" + text.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\n", "<br/>") + "</div></html>";
	}

	protected abstract String getTitle();

	protected abstract void drawMenuItems();

	protected void setTitleSize(float size) {
		this.titleSize = size;
		applyTitleFont();
	}

	protected JButton createMenuButton(String label, Runnable action) {
		JButton button = new JButton();
		bind(button, label);
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		lockButtonSize(button, MAX_BUTTON_SIZE);
		button.addActionListener(event -> action.run());
		return button;
	}

	protected JPanel createButtonRow(JButton... buttons) {
		int spacing = 16;
		JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, spacing, 0));
		buttonRow.setLayout(new BoxLayout(buttonRow, BoxLayout.X_AXIS));
		buttonRow.setOpaque(false);
		buttonRow.setAlignmentX(Component.CENTER_ALIGNMENT);

		int available = MAX_BUTTON_SIZE - spacing * (buttons.length - 1);
		int baseWidth = available / buttons.length;
		int remainder = available % buttons.length;

		for (int i = 0; i < buttons.length; i++) {
			if (i > 0) {
				buttonRow.add(Box.createHorizontalStrut(spacing));
			}

			int width = baseWidth + (i < remainder ? 1 : 0);
			lockButtonSize(buttons[i], width);
			buttonRow.add(buttons[i]);
		}

		buttonRow.setMaximumSize(new Dimension(MAX_BUTTON_SIZE, buttonRow.getPreferredSize().height));
		return buttonRow;
	}

	protected JButton createBackButton() {
		return createMenuButton("menu.button.back", () -> openMenu(previous));
	}

	protected void addMenuItems(JComponent... items) {
		for (JComponent item : items) {
			if (menuItemCount > 0) {
				buttonContainer.add(Box.createRigidArea(new Dimension(0, 15)));
			}
			item.setAlignmentX(Component.CENTER_ALIGNMENT);
			buttonContainer.add(item);
			menuItemCount++;
		}
	}

	protected void bind(JLabel label, String key) {
		Runnable refresher = () -> label.setText(I18N.getString(key));
		i18nRefreshers.add(refresher);
		refresher.run();
	}

	protected void bind(AbstractButton button, String key) {
		Runnable refresher = () -> button.setText(I18N.getString(key));
		i18nRefreshers.add(refresher);
		refresher.run();
	}

	protected void bind(Runnable refresher) {
		i18nRefreshers.add(refresher);
		refresher.run();
	}

	protected void openMenu(GUI menu) {
		I18N.removeListener(localeRefresh);
		menu.attachLocale();
		frame.setContentPane(menu);
		frame.revalidate();
		frame.repaint();
	}

	private void attachLocale() {
		I18N.removeListener(localeRefresh);
		I18N.addListener(localeRefresh);
		refreshI18n();
	}

	private void refreshI18n() {
		for (Runnable refresher : i18nRefreshers) {
			refresher.run();
		}
	}

	private void applyTitleFont() {
		menuTitle.setFont(menuTitle.getFont()
				.deriveFont(Font.BOLD, titleSize));
	}
}

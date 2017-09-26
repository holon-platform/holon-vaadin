/*
 * Copyright 2016-2017 Axioma srl.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.vaadin.internal.components;

import java.util.LinkedList;
import java.util.List;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.Dialog;
import com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Base {@link Dialog} implementation.
 * 
 * @since 5.0.0
 */
public abstract class AbstractDialog extends Window implements Dialog, CloseListener {

	private static final long serialVersionUID = 684174338177053986L;

	/**
	 * Close listeners
	 */
	private final List<com.holonplatform.vaadin.components.Dialog.CloseListener> closeListeners = new LinkedList<>();

	/**
	 * Flag for close listeners handling
	 */
	private boolean fireListenersOnCloseEvent = true;

	/**
	 * Root component
	 */
	private final VerticalLayout root;

	/**
	 * Dialog content
	 */
	private final Panel content;

	/**
	 * Dialog actions
	 */
	private final HorizontalLayout actions;

	/**
	 * Whether the HTML is allowed for dialog messages
	 */
	private boolean htmlContentAllowed = false;

	/**
	 * Default dialog message
	 */
	private Localizable message;
	/**
	 * Default dialog message description
	 */
	private Localizable messageDescription;

	/**
	 * Dialog message style name
	 */
	private String messageStyleName;

	/**
	 * Dialog message description style name
	 */
	private String messageDescriptionStyleName;

	/**
	 * Custom dialog content
	 */
	private Component dialogContent;

	/**
	 * Constructor
	 */
	public AbstractDialog() {
		super();

		// defaults
		setModal(true);
		setResizable(false);
		setDraggable(false);
		setClosable(false);

		// style name
		addStyleName("h-dialog");

		// build
		content = new Panel();
		content.setWidth("100%");
		content.addStyleName(ValoTheme.PANEL_BORDERLESS);
		content.addStyleName("h-dialog-content");

		actions = new HorizontalLayout();
		actions.setWidth("100%");
		actions.setSpacing(true);
		actions.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
		actions.addStyleName("h-dialog-actions");

		root = new VerticalLayout();
		root.addComponent(content);
		root.addComponent(actions);

		setContent(root);
	}

	/**
	 * Build the dialog actions
	 * @param actionsContainer Actions container
	 */
	protected abstract void buildActions(HorizontalLayout actionsContainer);

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractComponent#attach()
	 */
	@Override
	public void attach() {
		super.attach();

		// check content
		if (getDialogContent() != null) {
			content.setContent(getDialogContent());
		} else {
			// default content
			final VerticalLayout vl = new VerticalLayout();
			vl.setMargin(true);
			vl.setSpacing(true);

			if (getMessage() != null) {
				Label lbl = new Label();
				lbl.setWidth("100%");
				lbl.addStyleName("h-dialog-message");
				if (getMessageStyleName() != null) {
					lbl.addStyleName(getMessageStyleName());
				}
				if (isHtmlContentAllowed()) {
					lbl.setContentMode(ContentMode.HTML);
				}
				lbl.setValue(LocalizationContext.translate(getMessage(), true));
				vl.addComponent(lbl);
				vl.setComponentAlignment(lbl, Alignment.MIDDLE_CENTER);
			}

			if (getMessageDescription() != null) {
				Label lbl = new Label();
				lbl.setWidth("100%");
				lbl.addStyleName("h-dialog-message-description");
				if (getMessageDescriptionStyleName() != null) {
					lbl.addStyleName(getMessageDescriptionStyleName());
				}
				if (isHtmlContentAllowed()) {
					lbl.setContentMode(ContentMode.HTML);
				}
				lbl.setValue(LocalizationContext.translate(getMessageDescription(), true));
				vl.addComponent(lbl);
				vl.setComponentAlignment(lbl, Alignment.MIDDLE_CENTER);
			}

			content.setContent(vl);
		}

		// actions
		buildActions(actions);
	}

	/**
	 * Get the custom dialog content
	 * @return the dialog content
	 */
	public Component getDialogContent() {
		return dialogContent;
	}

	/**
	 * Set a custom dialog content
	 * @param dialogContent the dialog content to set
	 */
	public void setDialogContent(Component dialogContent) {
		this.dialogContent = dialogContent;
	}

	/**
	 * Get whether the HTML is allowed for dialog messages
	 * @return the htmlContentAllowed <code>true</code> if HTML is allowed for dialog messages
	 */
	public boolean isHtmlContentAllowed() {
		return htmlContentAllowed;
	}

	/**
	 * Set whether the HTML is allowed for dialog messages
	 * @param htmlContentAllowed <code>true</code> to allow HTML for dialog messages
	 */
	public void setHtmlContentAllowed(boolean htmlContentAllowed) {
		this.htmlContentAllowed = htmlContentAllowed;
	}

	/**
	 * Get the dialog message
	 * @return the dialog message
	 */
	public Localizable getMessage() {
		return message;
	}

	/**
	 * Set the dialog message
	 * @param message the message to set
	 */
	public void setMessage(Localizable message) {
		this.message = message;
	}

	/**
	 * Get the dialog message description
	 * @return the message description
	 */
	public Localizable getMessageDescription() {
		return messageDescription;
	}

	/**
	 * Set the dialog message description
	 * @param messageDescription the message description to set
	 */
	public void setMessageDescription(Localizable messageDescription) {
		this.messageDescription = messageDescription;
	}

	/**
	 * Get the dialog message style name
	 * @return the dialog message style name
	 */
	public String getMessageStyleName() {
		return messageStyleName;
	}

	/**
	 * Set the dialog message style name
	 * @param messageStyleName the dialog message style name to set
	 */
	public void setMessageStyleName(String messageStyleName) {
		this.messageStyleName = messageStyleName;
	}

	/**
	 * Get the dialog message description style name
	 * @return the dialog message description style name
	 */
	public String getMessageDescriptionStyleName() {
		return messageDescriptionStyleName;
	}

	/**
	 * Set the dialog message description style name
	 * @param messageDescriptionStyleName the dialog message description style name to set
	 */
	public void setMessageDescriptionStyleName(String messageDescriptionStyleName) {
		this.messageDescriptionStyleName = messageDescriptionStyleName;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Dialog#open(com.vaadin.ui.UI)
	 */
	@Override
	public void open(UI ui) {
		openDialogWindow((ui != null) ? ui : UI.getCurrent());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Dialog#addCloseListener(com.holonplatform.vaadin.components.Dialog.
	 * CloseListener)
	 */
	@Override
	public Registration addCloseListener(com.holonplatform.vaadin.components.Dialog.CloseListener listener) {
		ObjectUtils.argumentNotNull(listener, "CloseListener must be not null");
		closeListeners.add(listener);
		return () -> closeListeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.Window.CloseListener#windowClose(com.vaadin.ui.Window.CloseEvent)
	 */
	@Override
	public void windowClose(CloseEvent e) {
		if (fireListenersOnCloseEvent) {
			fireCloseListeners(null);
		}
	}

	/**
	 * Closes the dialog
	 * @param actionId Action id to provide to dialog {@link com.holonplatform.vaadin.components.Dialog.CloseListener}s.
	 */
	protected void close(Object actionId) {
		try {
			fireListenersOnCloseEvent = false;
			close();
			fireCloseListeners(actionId);
		} finally {
			fireListenersOnCloseEvent = true;
		}
	}

	/**
	 * Fire registered close listeners
	 * @param actionId Optional action id
	 */
	protected void fireCloseListeners(Object actionId) {
		for (com.holonplatform.vaadin.components.Dialog.CloseListener listener : closeListeners) {
			listener.dialogClosed(this, actionId);
		}
	}

	/**
	 * Open the dialog Window using given <code>ui</code>. Any Window which <code>equals</code> to <code>window</code>
	 * is removed from UI before adding the new Window.
	 * @param ui UI to which to attach the dialog window
	 */
	protected void openDialogWindow(UI ui) {
		if (ui == null) {
			throw new IllegalStateException("No UI available to open Dialog Window");
		}

		List<Window> toRemove = new LinkedList<>();
		for (Window wnd : ui.getWindows()) {
			if (this.equals(wnd)) {
				toRemove.add(wnd);
			}
		}
		for (Window wnd : toRemove) {
			ui.removeWindow(wnd);
		}
		ui.addWindow(this);
	}

	// Builder

	/**
	 * Base {@link Dialog} builder.
	 * @param <D> Concrete dialog type to build
	 * @param <B> Concrete builder type
	 */
	static abstract class AbstractBuilder<D extends AbstractDialog, B extends Builder<B>>
			extends AbstractComponentBuilder<Dialog, D, B> implements Builder<B> {

		/**
		 * Cosntructor
		 * @param instance Instance to build
		 */
		public AbstractBuilder(D instance) {
			super(instance);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.Dialog.Builder#message(com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public B message(Localizable message) {
			getInstance().setMessage(message);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#description(com.
		 * holonframework.core.i18n.Localizable)
		 */
		@Override
		public B description(Localizable description) {
			getInstance().setMessageDescription(description);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.Dialog.Builder#messageStyleName(java.lang.String)
		 */
		@Override
		public B messageStyleName(String messageStyleName) {
			getInstance().setMessageStyleName(messageStyleName);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.Dialog.Builder#messageDescriptionStyleName(java.lang.String)
		 */
		@Override
		public B messageDescriptionStyleName(String messageDescriptionStyleName) {
			getInstance().setMessageDescriptionStyleName(messageDescriptionStyleName);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.Dialog.Builder#modal(boolean)
		 */
		@Override
		public B modal(boolean modal) {
			getInstance().setModal(modal);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.Dialog.Builder#closable(boolean)
		 */
		@Override
		public B closable(boolean closable) {
			getInstance().setClosable(closable);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.Dialog.Builder#resizable(boolean)
		 */
		@Override
		public B resizable(boolean resizable) {
			getInstance().setResizable(resizable);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.Dialog.Builder#draggable(boolean)
		 */
		@Override
		public B draggable(boolean draggable) {
			getInstance().setDraggable(draggable);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.Dialog.Builder#htmlContentAllowed(boolean)
		 */
		@Override
		public B htmlContentAllowed(boolean htmlContentAllowed) {
			getInstance().setHtmlContentAllowed(htmlContentAllowed);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.Dialog.Builder#withCloseListener(com.holonplatform.vaadin.components.
		 * Dialog.CloseListener)
		 */
		@Override
		public B withCloseListener(com.holonplatform.vaadin.components.Dialog.CloseListener closeListener) {
			getInstance().addCloseListener(closeListener);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.Dialog.Builder#content(com.vaadin.ui.Component)
		 */
		@Override
		public B content(Component content) {
			getInstance().setDialogContent(content);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.Dialog.Builder#position(int, int)
		 */
		@Override
		public B position(int x, int y) {
			getInstance().setPosition(x, y);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.Dialog.Builder#withCloseShortcut(int, int[])
		 */
		@Override
		public B withCloseShortcut(int keyCode, int... modifiers) {
			getInstance().addCloseShortcut(keyCode, modifiers);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.Dialog.Builder#open(com.vaadin.ui.UI)
		 */
		@Override
		public void open(UI ui) {
			build().open(ui);
		}

	}

}

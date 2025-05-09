/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.mcreator.ui.modgui;

import net.mcreator.element.ModElementType;
import net.mcreator.element.types.Function;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.ide.RSyntaxTextAreaStyler;
import net.mcreator.ui.ide.mcfunction.MinecraftCommandsTokenMaker;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.RegistryNameValidator;
import net.mcreator.ui.validation.validators.UniqueNameValidator;
import net.mcreator.workspace.elements.ModElement;
import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Objects;

public class FunctionGUI extends ModElementGUI<Function> {

	private final JComboBox<String> namespace = new JComboBox<>(new String[] { "mod", "minecraft" });
	private final VTextField name = new VTextField();

	private final RSyntaxTextArea te = new RSyntaxTextArea();

	public FunctionGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		JPanel pane3 = new JPanel(new BorderLayout());
		pane3.setOpaque(false);

		//@formatter:off
		name.setValidator(new UniqueNameValidator(
			L10N.t("modelement.function"),
			() -> namespace.getSelectedItem() + ":" + name.getText(),
			() -> mcreator.getWorkspace().getModElements().stream()
				.filter(me -> me.getType() == ModElementType.FUNCTION)
				.map(ModElement::getGeneratableElement)
				.filter(Objects::nonNull)
				.map(ge -> ((Function) ge).namespace + ":" + ((Function) ge).name),
			new RegistryNameValidator(name, L10N.t("modelement.function")).setValidChars(Arrays.asList('_', '/'))
		).setIsPresentOnList(this::isEditingMode));
		//@formatter:on
		name.enableRealtimeValidation();

		if (isEditingMode()) {
			name.setEnabled(false);
			namespace.setEnabled(false);
		} else {
			name.setText(RegistryNameFixer.fromCamelCase(modElement.getName()));

			te.setText("# Enter the function code here");
		}

		JPanel northPanel = new JPanel(new GridLayout(2, 2, 15, 10));
		northPanel.setOpaque(false);

		northPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("function/registry_name"),
				L10N.label("elementgui.function.registry_name")));
		northPanel.add(name);

		northPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("function/namespace"),
				L10N.label("elementgui.function.namespace")));
		northPanel.add(namespace);

		RTextScrollPane sp = new RTextScrollPane(te, true);

		RSyntaxTextAreaStyler.style(te, sp, 14);
		LanguageSupportFactory.get().register(te);

		te.requestFocusInWindow();
		te.setMarkOccurrences(true);
		te.setCodeFoldingEnabled(false);
		te.setClearWhitespaceLinesEnabled(true);
		te.setAutoIndentEnabled(false);
		te.setTabSize(4);
		te.setTabsEmulated(false);

		sp.setFoldIndicatorEnabled(true);
		sp.getGutter().setFoldBackground(Theme.current().getBackgroundColor());
		sp.getGutter().setBorderColor(Theme.current().getBackgroundColor());
		sp.getGutter().setBackground(Theme.current().getBackgroundColor());
		sp.getGutter().setBookmarkingEnabled(true);
		sp.setIconRowHeaderEnabled(false);
		sp.setBackground(Theme.current().getBackgroundColor());
		sp.setBorder(null);

		AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
		atmf.putMapping("text/mcfunction", MinecraftCommandsTokenMaker.class.getName());
		te.setSyntaxEditingStyle("text/mcfunction");

		pane3.add(PanelUtils.northAndCenterElement(PanelUtils.join(FlowLayout.LEFT, northPanel),
				PanelUtils.northAndCenterElement(L10N.label("elementgui.function.indications"), sp, 10, 10), 15, 15));

		addPage(pane3).validate(name);
	}

	@Override public void openInEditingMode(Function function) {
		namespace.setSelectedItem(function.namespace);
		name.setText(function.name);

		te.setText(function.code);
	}

	@Override public Function getElementFromGUI() {
		Function function = new Function(modElement);

		function.namespace = (String) namespace.getSelectedItem();
		function.name = name.getText();

		function.code = te.getText();

		return function;
	}

	@Override protected boolean allowCodePreview() {
		return false;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-function");
	}

}

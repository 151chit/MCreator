/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.element.converter.v2023_4;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.types.LivingEntity;
import net.mcreator.workspace.Workspace;

import java.util.ArrayList;

public class EntityGlowLayerConverter implements IConverter {

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		LivingEntity entity = (LivingEntity) input;

		JsonObject jsonObject = jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject();
		if (jsonObject.get("mobModelGlowTexture") != null) {
			String glowTexture = jsonObject.get("mobModelGlowTexture").getAsString();
			if (!glowTexture.isEmpty()) {
				LivingEntity.ModelLayerEntry glowLayer = new LivingEntity.ModelLayerEntry();
				glowLayer.setWorkspace(workspace);
				glowLayer.model = "Default";
				glowLayer.texture = glowTexture;
				glowLayer.glow = true;
				glowLayer.condition = null;

				entity.modelLayers = new ArrayList<>();
				entity.modelLayers.add(glowLayer);
			}
		}

		return entity;
	}

	@Override public int getVersionConvertingTo() {
		return 57;
	}

}

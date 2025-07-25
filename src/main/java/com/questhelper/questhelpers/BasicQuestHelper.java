/*
 * Copyright (c) 2019, Trevor <https://github.com/Trevor159>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.questhelpers;

import com.questhelper.QuestHelperConfig;
import com.questhelper.panel.PanelDetails;
import com.questhelper.steps.QuestStep;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BasicQuestHelper extends QuestHelper
{
	protected Map<Integer, QuestStep> steps;
	protected int var;

	public Map<Integer, QuestStep> getStepList() {
		return this.steps;
	}

	/**
	 * Attempt to load steps from the quest if steps have not yet been loaded
	 */
	private void tryLoadSteps()
	{
		if (steps == null)
		{
			steps = loadSteps();
		}
	}

	@Override
	public void init()
	{
		this.tryLoadSteps();
	}

	@Override
	public void startUp(QuestHelperConfig config)
	{
		// this.tryLoadSteps();
		// TODO: This should use `tryLoadSteps` but when it is being more careful to be initialized, it doesn't handle
		// highlighting in sidebars properly
		steps = loadSteps();
		this.config = config;
		instantiateSteps(steps.values());
		var = getVar();
		sidebarOrder = questHelperPlugin.loadSidebarOrder(this);
		startUpStep(steps.get(var));
	}

	@Override
	public void shutDown()
	{
		super.shutDown();
		shutDownStep();
	}

	@Override
	public boolean updateQuest()
	{
		if (var != getVar())
		{
			var = getVar();
			shutDownStep();
			startUpStep(steps.get(var));
			return true;
		}
		return false;
	}

	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> panelSteps = new ArrayList<>();
		steps.forEach((id, step) -> panelSteps.add(new PanelDetails("", step)));
		return panelSteps;
	}

	public abstract Map<Integer, QuestStep> loadSteps();
}

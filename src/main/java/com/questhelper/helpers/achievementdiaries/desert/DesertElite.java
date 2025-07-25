/*
 * Copyright (c) 2021, Obasill <https://github.com/Obasill>
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
package com.questhelper.helpers.achievementdiaries.desert;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.TeleportItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.Spellbook;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarPlayerID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DesertElite extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement combatGear, rawPie, waterRune, bloodRune, deathRune, dragonDartTip, feather, mahoganyPlank,
		goldLeaves, coins, saw, hammer, kqHead;

	// Items recommended
	ItemRequirement food, pharaohSceptre, desertRobe, waterskin, desertBoots, desertShirt;

	// Quests required
	Requirement desertTreasure, icthlarinsLittleHelper, touristTrap, priestInPeril;

	Requirement notWildPie, notIceBarrage, notDragonDarts, notTalkKQHead, notGrandGoldChest, notRestorePrayer, ancientBook;

	QuestStep claimReward, wildPie, dragonDarts, talkKQHead, grandGoldChest, restorePrayer,
		moveToPyramidPlunder, startPyramidPlunder, traversePyramid, moveToBed;

	NpcStep iceBarrage;

	Zone bed, pyramidPlunderLobby, lastRoom, pyramidRooms;

	ZoneRequirement inBed, inPyramidPlunderLobby, inPyramidRooms, inLastRoom;

	ConditionalStep wildPieTask, iceBarrageTask, dragonDartsTask, talkKQHeadTask, grandGoldChestTask, restorePrayerTask;

	@Override
	public QuestStep loadStep()
	{
		initializeRequirements();
		setupSteps();

		ConditionalStep doElite = new ConditionalStep(this, claimReward);

		wildPieTask = new ConditionalStep(this, wildPie);
		doElite.addStep(notWildPie, wildPieTask);

		iceBarrageTask = new ConditionalStep(this, iceBarrage);
		doElite.addStep(notIceBarrage, iceBarrageTask);

		grandGoldChestTask = new ConditionalStep(this, moveToPyramidPlunder);
		grandGoldChestTask.addStep(inPyramidPlunderLobby, startPyramidPlunder);
		grandGoldChestTask.addStep(inPyramidRooms, traversePyramid);
		grandGoldChestTask.addStep(inLastRoom, grandGoldChest);
		doElite.addStep(notGrandGoldChest, grandGoldChestTask);

		restorePrayerTask = new ConditionalStep(this, restorePrayer);
		doElite.addStep(notRestorePrayer, restorePrayerTask);

		dragonDartsTask = new ConditionalStep(this, moveToBed);
		dragonDartsTask.addStep(inBed, dragonDarts);
		doElite.addStep(notDragonDarts, dragonDartsTask);

		talkKQHeadTask = new ConditionalStep(this, talkKQHead);
		doElite.addStep(notTalkKQHead, talkKQHeadTask);

		return doElite;
	}

	@Override
	protected void setupRequirements()
	{
		notWildPie = new VarplayerRequirement(VarPlayerID.DESERT_ACHIEVEMENT_DIARY2, false, 2);
		notIceBarrage = new VarplayerRequirement(VarPlayerID.DESERT_ACHIEVEMENT_DIARY2, false, 4);
		notDragonDarts = new VarplayerRequirement(VarPlayerID.DESERT_ACHIEVEMENT_DIARY2, false, 5);
		notTalkKQHead = new VarplayerRequirement(VarPlayerID.DESERT_ACHIEVEMENT_DIARY2, false, 6);
		notGrandGoldChest = new VarplayerRequirement(VarPlayerID.DESERT_ACHIEVEMENT_DIARY2, false, 7);
		notRestorePrayer = new VarplayerRequirement(VarPlayerID.DESERT_ACHIEVEMENT_DIARY2, false, 8);

		ancientBook = new SpellbookRequirement(Spellbook.ANCIENT);

		coins = new ItemRequirement("Coins", ItemCollections.COINS).showConditioned(notTalkKQHead);
		rawPie = new ItemRequirement("Raw wild pie", ItemID.UNCOOKED_WILD_PIE).showConditioned(notWildPie);
		waterRune = new ItemRequirement("Water rune", ItemID.WATERRUNE).showConditioned(notIceBarrage);
		bloodRune = new ItemRequirement("Blood rune", ItemID.BLOODRUNE).showConditioned(notIceBarrage);
		deathRune = new ItemRequirement("Death rune", ItemID.DEATHRUNE).showConditioned(notIceBarrage);
		dragonDartTip = new ItemRequirement("Dragon dart tip", ItemID.DRAGON_DART_TIP).showConditioned(notDragonDarts);
		feather = new ItemRequirement("Feather", ItemID.FEATHER).showConditioned(notDragonDarts);
		mahoganyPlank = new ItemRequirement("Mahogany plank", ItemID.PLANK_MAHOGANY).showConditioned(notTalkKQHead);
		goldLeaves = new ItemRequirement("Gold leaf", ItemID.ICS_GOLDLEAF).showConditioned(notTalkKQHead);
		saw = new ItemRequirement("Saw", ItemID.POH_SAW).showConditioned(notTalkKQHead).isNotConsumed();
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER).showConditioned(notTalkKQHead).isNotConsumed();
		kqHead = new ItemRequirement("Stuffed KQ head", ItemCollections.STUFFED_KQ_HEAD).showConditioned(notTalkKQHead);

		pharaohSceptre = new TeleportItemRequirement("Pharaoh's sceptre", ItemCollections.PHAROAH_SCEPTRE).isNotConsumed();
		desertBoots = new ItemRequirement("Desert boots", ItemID.DESERT_BOOTS).isNotConsumed();
		desertRobe = new ItemRequirement("Desert robe", ItemID.DESERT_ROBE).isNotConsumed();
		desertShirt = new ItemRequirement("Desert shirt", ItemID.DESERT_SHIRT).isNotConsumed();
		waterskin = new ItemRequirement("Waterskin", ItemCollections.WATERSKIN).isNotConsumed();

		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);

		inBed = new ZoneRequirement(bed);
		inLastRoom = new ZoneRequirement(lastRoom);
		inPyramidPlunderLobby = new ZoneRequirement(pyramidPlunderLobby);
		inPyramidRooms = new ZoneRequirement(pyramidRooms);

		desertTreasure = new QuestRequirement(QuestHelperQuest.DESERT_TREASURE, QuestState.FINISHED);
		icthlarinsLittleHelper = new QuestRequirement(QuestHelperQuest.ICTHLARINS_LITTLE_HELPER, QuestState.IN_PROGRESS);
		touristTrap = new QuestRequirement(QuestHelperQuest.THE_TOURIST_TRAP, QuestState.FINISHED);
		priestInPeril = new QuestRequirement(QuestHelperQuest.PRIEST_IN_PERIL, QuestState.FINISHED);
	}

	@Override
	protected void setupZones()
	{
		bed = new Zone(new WorldPoint(3163, 3049, 0), new WorldPoint(3181, 3024, 0));
		pyramidPlunderLobby = new Zone(new WorldPoint(1926, 4465, 2), new WorldPoint(1976, 4419, 3));
		lastRoom = new Zone(new WorldPoint(1966, 4437, 0), new WorldPoint(1980, 4419, 0));
		pyramidRooms = new Zone(new WorldPoint(1922, 4417, 0), new WorldPoint(1984, 4480, 0));
	}

	public void setupSteps()
	{
		moveToPyramidPlunder = new ObjectStep(this, 26622, new WorldPoint(3289, 2800, 0),
			"Enter the Pyramid plunder minigame. If you don't see a Guardian mummy exit and try a different " +
				"entrance.", true);
		((ObjectStep) moveToPyramidPlunder).addAlternateObjects(ObjectID.NTK_PYRAMID_DOOR_EAST_MULTI, ObjectID.NTK_PYRAMID_DOOR_SOUTH_MULTI,
			ObjectID.NTK_PYRAMID_DOOR_WEST_MULTI);
		startPyramidPlunder = new NpcStep(this, NpcID.NTK_MUMMY_GUARDIAN, new WorldPoint(1934, 4427, 3),
			"Talk to the guardian mummy to start the minigame.");
		startPyramidPlunder.addDialogStep("I know what I'm doing - let's get on with it.");

		traversePyramid = new ObjectStep(this, 26618, new WorldPoint(1951, 4452, 0),
			"Go through each of the pyramid rooms until the final room.", true);
		((ObjectStep) traversePyramid).setMaxObjectDistance(40);
		((ObjectStep) traversePyramid).setMaxRenderDistance(10);
		for (int i = 26619; i < 26651; i++)
		{
			if (i == 26626)
			{
				continue;
			}
			((ObjectStep) traversePyramid).addAlternateObjects(i);
		}

		grandGoldChest = new ObjectStep(this, ObjectID.NTK_GOLDEN_CHEST_CLOSED, new WorldPoint(1973, 4431, 0),
			"Loot the grand gold chest.", true);// Can't test the coords but should be close / correct

		restorePrayer = new ObjectStep(this, ObjectID.CONTACT_HIGH_PRIEST_TEMPLE_ALTAR, new WorldPoint(3281, 2774, 0),
			"Pray at the altar in Sophanem restoring at least 85 prayer points.");

		iceBarrage = new NpcStep(this, NpcID.RAG_VULTURE, new WorldPoint(3334, 2865, 0),
			"Cast Ice barrage against any foe in the Desert (away from any city). You must not splash.", true,
			waterRune.quantity(6), bloodRune.quantity(2), deathRune.quantity(4));
		iceBarrage.addAlternateNpcs(NpcID.RAG_VULTURE_FLYING);

		wildPie = new ObjectStep(this, ObjectID.ELID_CLAY_OVEN, new WorldPoint(3434, 2886, 0),
			"Cook a wild pie on the clay oven in Nardah.", rawPie);

		moveToBed = new TileStep(this, new WorldPoint(3175, 3041, 0),
			"Go to Bedabin Camp south-west of Al Kharid.");
		dragonDarts = new ItemStep(this, "Fletch a dragon dart.", dragonDartTip.highlighted(), feather.highlighted());

		talkKQHead = new DetailedQuestStep(this, "Mount and then talk to a Kalphite Queen head in your POH. The 50k " +
			"is necessary to stuff the KQ head at the taxidermist in Canifis.",
			kqHead, mahoganyPlank.quantity(2), goldLeaves.quantity(2), saw, hammer, coins.quantity(50000));

		claimReward = new NpcStep(this, NpcID.JARR_DESERT_DIARY, new WorldPoint(3303, 3124, 0),
			"Talk to Jarr at the Shantay pass to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(rawPie, waterRune.quantity(6), bloodRune.quantity(2), deathRune.quantity(4),
			dragonDartTip, feather, kqHead, mahoganyPlank.quantity(2), goldLeaves.quantity(2), coins.quantity(50000), saw,
			hammer, kqHead);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(food, pharaohSceptre, desertRobe, desertBoots, desertShirt, waterskin);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Desert amulet 4", ItemID.DESERT_AMULET_ELITE),
			new ItemReward("50,000 Exp. Lamp (Any skill over 70)", ItemID.THOSF_REWARD_LAMP)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("Unlimited teleports to Nardah and the Kalphite Cave on the " +
				"desert amulet"),
			new UnlockReward("The Nardah teleport on the desert amulet now takes players " +
				"directly inside the Elidinis" +
				" shrine"),
			new UnlockReward("100% protection against desert heat when the desert amulet is worn"),
			new UnlockReward("Pharaoh's sceptre can hold up to 100 charges"),
			new UnlockReward("Free pass-through of the Shantay Pass"),
			new UnlockReward("Access to a crevice shortcut, requiring 86 Agility, " +
				"in the Kalphite Lair from the entrance to the antechamber before the " +
				"Kalphite Queen boss room.")
		);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new SkillRequirement(Skill.CONSTRUCTION, 78, true));
		reqs.add(new SkillRequirement(Skill.COOKING, 85, true));
		reqs.add(new SkillRequirement(Skill.FLETCHING, 95, true));
		reqs.add(new SkillRequirement(Skill.MAGIC, 94, true));
		reqs.add(new SkillRequirement(Skill.PRAYER, 85, false));
		reqs.add(new SkillRequirement(Skill.THIEVING, 91, false));


		reqs.add(desertTreasure);
		reqs.add(icthlarinsLittleHelper);
		reqs.add(priestInPeril);
		reqs.add(touristTrap);

		return reqs;
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails wildPieSteps = new PanelDetails("Bake Wild Pie", Collections.singletonList(wildPie),
			new SkillRequirement(Skill.COOKING, 85, true), rawPie);
		wildPieSteps.setDisplayCondition(notWildPie);
		wildPieSteps.setLockingStep(wildPieTask);
		allSteps.add(wildPieSteps);

		PanelDetails iceBarrageSteps = new PanelDetails("Ice Barrage", Collections.singletonList(iceBarrage),
			new SkillRequirement(Skill.MAGIC, 94, true), desertTreasure, ancientBook, waterRune.quantity(6),
			bloodRune.quantity(2), deathRune.quantity(4));
		iceBarrageSteps.setDisplayCondition(notIceBarrage);
		iceBarrageSteps.setLockingStep(iceBarrageTask);
		allSteps.add(iceBarrageSteps);

		PanelDetails grandGoldChestSteps = new PanelDetails("Grand Gold Chest", Arrays.asList(moveToPyramidPlunder,
			startPyramidPlunder, grandGoldChest), new SkillRequirement(Skill.THIEVING, 91, false), icthlarinsLittleHelper);
		grandGoldChestSteps.setDisplayCondition(notGrandGoldChest);
		grandGoldChestSteps.setLockingStep(grandGoldChestTask);
		allSteps.add(grandGoldChestSteps);

		PanelDetails restorePrayerSteps = new PanelDetails("Restore 85 Prayer",
			Collections.singletonList(restorePrayer), new SkillRequirement(Skill.PRAYER, 85, false), icthlarinsLittleHelper);
		restorePrayerSteps.setDisplayCondition(notRestorePrayer);
		restorePrayerSteps.setLockingStep(restorePrayerTask);
		allSteps.add(restorePrayerSteps);

		PanelDetails dragonDartsSteps = new PanelDetails("Dragon Darts", Arrays.asList(moveToBed, dragonDarts),
			new SkillRequirement(Skill.FLETCHING, 95, true), touristTrap, dragonDartTip, feather);
		dragonDartsSteps.setDisplayCondition(notDragonDarts);
		dragonDartsSteps.setLockingStep(dragonDartsTask);
		allSteps.add(dragonDartsSteps);

		PanelDetails kqHeadSteps = new PanelDetails("Kalphite Queen Head", Collections.singletonList(talkKQHead),
			new SkillRequirement(Skill.CONSTRUCTION, 78, true), priestInPeril, kqHead, coins.quantity(50000),
			mahoganyPlank.quantity(2), goldLeaves.quantity(2), saw, hammer);
		kqHeadSteps.setDisplayCondition(notTalkKQHead);
		kqHeadSteps.setLockingStep(talkKQHeadTask);
		allSteps.add(kqHeadSteps);

		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}

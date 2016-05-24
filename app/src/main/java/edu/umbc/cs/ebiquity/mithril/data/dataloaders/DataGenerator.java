package edu.umbc.cs.ebiquity.mithril.data.dataloaders;

import java.util.ArrayList;
import java.util.List;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.PolicyRule;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.actions.Action;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.actions.RuleAction;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.UserContext;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.DeviceTime;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.Identity;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.InferredActivity;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.InferredLocation;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.PresenceInfo;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.protectedresources.Resource;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.requesters.Requester;
import edu.umbc.cs.ebiquity.mithril.data.helpers.MithrilDBHelper;

public class DataGenerator {
	/**
	 * This data generator is used in the loadDefaultDataIntoDB method in {@link MithrilDBHelper} to generate default policies
	 * @param aRequester
	 * @param aResource
	 * @param aUserContext
	 * @param aRuleAction
	 * @return
	 */
	public static PolicyRule generateSocialMediaCameraAccessRule(Requester aRequester, Resource aResource, UserContext aUserContext, RuleAction aRuleAction) {
		PolicyRule policyRule = new PolicyRule(MithrilApplication.getConstPolRulNameSocialMediaCameraAccessRule(), aRequester, aResource, aUserContext, aRuleAction);
		return policyRule;
	}
	
	public static UserContext generateContextForSocialMediaCameraAccessRule() {
		List<String> tempIdentityString = new ArrayList<String>();
		for(String tempString : MithrilApplication.getConstArrayPresenceInfoIdentity()) {
			tempIdentityString.add(tempString);
		}
		
		List<String> tempLocationString = new ArrayList<String>();
		for(String tempString : MithrilApplication.getConstArrayLocation()) {
			tempLocationString.add(tempString);
		}

		List<String> tempActivityString = new ArrayList<String>();
		for(String tempString : MithrilApplication.getConstArrayActivity()) {
			tempActivityString.add(tempString);
		}

		List<String> tempTimeString = new ArrayList<String>();
		for(String tempString : MithrilApplication.getConstArrayTime()) {
			tempTimeString.add(tempString);
		}

		List<Identity> presenceInfo = new ArrayList<Identity>();
		presenceInfo.add(new Identity(tempIdentityString.get(2))); // Index 2 is Department Head

		UserContext userContext = new UserContext(
				new PresenceInfo(presenceInfo),
				new InferredActivity(tempActivityString.get(2)), //Index 2 is university talk
				new Identity(MithrilApplication.getConstContextDefaultIdentity()),
				new InferredLocation(tempLocationString.get(2)), // Index 2 is university state
				new DeviceTime(tempTimeString.get(2))); // Index 2 is Week Day
		return userContext;
	}
	
	/**
	 * Actions generated
	 */
	public static RuleAction generateAllowAction() {
		return new RuleAction(Action.ALLOW);
	}
	public static RuleAction generateAllowWithCaveatAction() {
		return new RuleAction(Action.ALLOW_WITH_CAVEAT);
	}
	public static RuleAction generateDenyAction() {
		return new RuleAction(Action.DENY);
	}

	/**
	 * Resources generated
	 */ 
	public static List<Resource> generateResources() {
		List<Resource> tempList = new ArrayList<Resource>();
		for(String aResourceString : MithrilApplication.getConstArrayResourceCategory())
			tempList.add(new Resource(aResourceString));
		return tempList;
	}

	/**
	 * Requesters generated
	 */ 
	public static List<Requester> generateRequesters() {
		List<Requester> tempList = new ArrayList<Requester>();
		for(String aRequesterString : MithrilApplication.getConstArrayRequesterCategory())
			tempList.add(new Requester(aRequesterString));
		return tempList;
	}
}